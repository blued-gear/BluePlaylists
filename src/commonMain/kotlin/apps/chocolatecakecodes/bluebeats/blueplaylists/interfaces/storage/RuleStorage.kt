package apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.storage

import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.dynamicplaylist.rules.*

interface RuleStorage {

    /** consuming projects should put their implementation here */
    object Slot {
        lateinit var INSTANCE: RuleStorage
    }

    fun newRuleGroup(share: Share): RuleGroup
    fun newIncludeRule(share: Share): IncludeRule
    fun newUsertagsRule(share: Share): UsertagsRule
    fun newID3TagsRule(share: Share): ID3TagsRule
    fun newRegexRule(share: Share): RegexRule
    fun newTimeSpanRule(share: Share): TimeSpanRule

}
