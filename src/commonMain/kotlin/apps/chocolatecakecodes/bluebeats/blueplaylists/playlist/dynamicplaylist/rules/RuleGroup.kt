package apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.dynamicplaylist.rules

import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.log.Logger
import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.storage.RuleStorage
import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.items.PlaylistItem
import apps.chocolatecakecodes.bluebeats.blueplaylists.utils.*

class RuleGroup(
    override val id: Long,
    override val isOriginal: Boolean,
    override var share: Share,
    override var name: String = "",
    var combineWithAnd: Boolean = false,
    rules: List<Pair<GenericRule, Boolean>> = emptyList()
) : Rule<RuleGroup> {

    private val rules: MutableList<Pair<GenericRule, Boolean>> = ArrayList(rules)

    val graveyard: MutableList<GenericRule> = mutableListOf()

    override fun generateItems(amount: Int, exclude: Set<PlaylistItem>): List<PlaylistItem> {
        val (negativeRules, positiveRules) = getRules().partition { it.second }.let {
            Pair(
                it.first.map { it.first },
                it.second.map { it.first }
            )
        }
        val (relativeRules, evenRules, absoluteRules) = positiveRules.partition {
            it.share.isRelative
        }.let { (relativeRules, absoluteRules) ->
            relativeRules.partition {
                it.share.modeRelative()
            }.let {
                Triple(it.first, it.second, absoluteRules)
            }
        }

        val excludeAcc = exclude + negativeRules.flatMap { it.generateItems(-1, emptySet()) }

        val absoluteItems = absoluteRules.map {
            val localAmount = if(amount >= 0 && !combineWithAnd) it.share.value.toInt() else -1
            it.generateItems(localAmount, excludeAcc)
        }

        val relativeAmount = let {
            if(amount >= 0 && !combineWithAnd) {
                (amount - absoluteItems.sumOf { it.size }).coerceAtLeast(0)
            } else {
                -1
            }
        }
        if(relativeAmount == 0 && relativeRules.isNotEmpty())
            Logger.Slot.INSTANCE.warn("RuleGroup::generateItems",
                "calculated amount for relative rules resulted in 0; try increasing your IterationSize")
        val relativeItems = relativeRules.map {
            val localAmount = if(amount >= 0 && !combineWithAnd) (relativeAmount * it.share.value).toInt() else -1
            it.generateItems(localAmount, excludeAcc)
        }

        val evenAmount = if(amount >= 0 && !combineWithAnd)
            (((1.0 - relativeRules.sumOf { it.share.value.toDouble() }) / evenRules.size) * relativeAmount).toInt()
        else
            -1
        if(evenAmount == 0 && evenRules.isNotEmpty())
            Logger.Slot.INSTANCE.warn("RuleGroup::generateItems",
                "calculated amount for even rules resulted in 0; try increasing your IterationSize")
        val evenItems = evenRules.map {
            it.generateItems(evenAmount, excludeAcc)
        }

        return let {
            if(combineWithAnd) {
                (absoluteItems + relativeItems + evenItems).map {
                    it.toSet()
                }.reduceOrNull { acc, cur ->
                    acc.intersect(cur)
                } ?: emptySet()
            } else {
                val absoluteCombined = LinkedHashSet<PlaylistItem>()
                absoluteItems.forEach { absoluteCombined.addAll(it) }

                val relativeCombined = LinkedHashSet<PlaylistItem>()
                relativeItems.forEach { relativeCombined.addAll(it) }
                evenItems.forEach { relativeCombined.addAll(it) }

                absoluteCombined + relativeCombined.shuffled()
            }
        }.takeOrAll(amount)
    }

    /**
     * @return List<Pair<rule, negate>>
     */
    fun getRules(): List<Pair<GenericRule, Boolean>> {
        return rules
    }

    fun addRule(rule: GenericRule, negate: Boolean = false) {
        rules.add(Pair(rule, negate))
    }

    fun getRuleNegated(rule: GenericRule): Boolean? {
        return getRules().find {
            it.first == rule
        }?.second
    }

    fun setRuleNegated(rule: GenericRule, negated: Boolean) {
        val idx = rules.indexOfFirst { it.first == rule }
        if(idx == -1)
            throw NoSuchElementException("rule not found")
        rules[idx] = Pair(rule, negated)
    }

    fun removeRule(rule: GenericRule) {
        rules.removeIfSingle { it.first == rule }
            ?.also { graveyard.add(it.first) }
            ?: throw IllegalArgumentException("given rule was not in this group")
    }

    fun removeRuleAt(idx: Int) {
        rules.removeAt(idx).also { graveyard.add(it.first) }
    }

    override fun copy(): RuleGroup {
        return RuleGroup(id, false, share.copy(), name, combineWithAnd).apply {
            this@RuleGroup.rules.map {
                Pair(it.first.copy() as GenericRule, it.second)
            }.let {
                rules.addAll(it)
            }
        }
    }

    /**
     * this will make a deep-copy of all subrules (it will create new instances if this and other are both originals)
     * @see Rule.applyFrom
     */
    override fun applyFrom(other: RuleGroup) {
        this.combineWithAnd = other.combineWithAnd
        this.share = other.share.copy()
        this.name = other.name

        data class RuleId(val type: Int, val instance: Long)

        fun ruleId(rule: GenericRule) = RuleId(rule::class.hashCode(), rule.id)

        val thisRules = this.rules.associateBy { ruleId(it.first) }
        val otherRules = other.rules.associateBy { ruleId(it.first) }
        CollectionUtils.diffChanges(thisRules.keys, otherRules.keys).let { (added, deleted, same) ->
            added.map {
                otherRules[it]!!
            }.forEach { (rule, negate) ->
                if(other.isOriginal)
                    assert(rule.isOriginal) { "original RuleGroups can only contain original subrules" }

                if(this.isOriginal) {
                    if(rule.isOriginal && !other.isOriginal) {
                        // take over rule
                        other.releaseRule(rule)
                        this.addRule(rule, negate)
                    } else {
                        // make new instance
                        when(rule) {
                            is RuleGroup -> RuleStorage.Slot.INSTANCE.newRuleGroup(rule.share)
                            is IncludeRule -> RuleStorage.Slot.INSTANCE.newIncludeRule(rule.share)
                            is UsertagsRule -> RuleStorage.Slot.INSTANCE.newUsertagsRule(rule.share)
                            is ID3TagsRule -> RuleStorage.Slot.INSTANCE.newID3TagsRule(rule.share)
                            is RegexRule -> RuleStorage.Slot.INSTANCE.newRegexRule(rule.share)
                            is TimeSpanRule -> RuleStorage.Slot.INSTANCE.newTimeSpanRule(rule.share)
                        }.castTo<Rule<in GenericRule>>().apply {
                            this.applyFrom(rule)
                        }.let {
                            this.addRule(it, negate)
                        }
                    }
                } else {
                    // make (another) copy
                    this.addRule(rule.copy() as GenericRule, negate)
                }
            }

            deleted.map {
                thisRules[it]!!
            }.forEach { (rule, _) ->
                if(this.isOriginal)
                    assert(rule.isOriginal) { "original RuleGroups can only contain original subrules" }

                this.removeRule(rule)
            }

            same.map { id ->
                otherRules[id]!!.let {
                    Triple(it.first, it.second, thisRules[id]!!.first)
                }
            }.forEach { (otherRule, negate, thisRule) ->
                if(this.isOriginal)
                    assert(thisRule.isOriginal) { "original RuleGroups can only contain original subrules" }
                if(other.isOriginal)
                    assert(otherRule.isOriginal) { "original RuleGroups can only contain original subrules" }

                thisRule.castTo<Rule<in GenericRule>>().applyFrom(otherRule)

                this.setRuleNegated(thisRule, negate)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if(other !is RuleGroup)
            return false

        return this.getRules() == other.getRules()
                && this.combineWithAnd == other.combineWithAnd
                && this.share == other.share
                && this.name == other.name
    }

    override fun hashCode(): Int {
        return arrayOf<Any>(this::class.qualifiedName!!, getRules(), combineWithAnd, share, name).contentDeepHashCode()
    }

    /**
     * replace the rule of this group with a copy (this instance must not be an original)
     */
    private fun releaseRule(rule: GenericRule) {
        if(isOriginal)
            throw IllegalStateException("can not takeover rule from original")

        this.rules.removeIfSingle { it.first == rule }?.let {
            addRule(it.first.copy() as GenericRule, it.second)
        } ?: throw IllegalArgumentException("rule was not in this group")
    }
}
