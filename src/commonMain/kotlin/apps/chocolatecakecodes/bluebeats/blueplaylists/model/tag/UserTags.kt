package apps.chocolatecakecodes.bluebeats.blueplaylists.model.tag

/**
 * holds the list of tags (in terms of BlueBeats, not ID3) the user attached to a file
 */
data class UserTags(
    val tags: List<String>,
) {

    object Parser {

        const val USERTEXT_KEY = "_BlueBeats::Usertags::v1"
        const val VALUE_SEPARATOR = " ; "

        fun parse(inp: String): UserTags {
            return UserTags(inp.split(VALUE_SEPARATOR))
        }
    }
}
