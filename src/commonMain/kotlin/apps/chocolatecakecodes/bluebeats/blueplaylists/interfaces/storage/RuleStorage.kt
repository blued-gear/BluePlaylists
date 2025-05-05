package apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.storage

import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.dynamicplaylist.rules.*

interface RuleStorage {

    /** consuming projects should put their implementation here */
    object Slot {
        lateinit var INSTANCE: RuleStorage
    }

    fun newRuleGroup(share: Rule.Share): RuleGroup
    fun newIncludeRule(share: Rule.Share): IncludeRule
    fun newUsertagsRule(share: Rule.Share): UsertagsRule
    fun newID3TagsRule(share: Rule.Share): ID3TagsRule
    fun newRegexRule(share: Rule.Share): RegexRule
    fun newTimeSpanRule(share: Rule.Share): TimeSpanRule

}
