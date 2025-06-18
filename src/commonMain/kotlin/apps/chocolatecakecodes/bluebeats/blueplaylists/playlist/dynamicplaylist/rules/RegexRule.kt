package apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.dynamicplaylist.rules

import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media.MediaFile
import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media.MediaLibrary
import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.items.MediaFileItem
import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.items.PlaylistItem
import apps.chocolatecakecodes.bluebeats.blueplaylists.utils.castTo
import apps.chocolatecakecodes.bluebeats.blueplaylists.utils.takeOrAll

class RegexRule(
    override val id: Long,
    override val isOriginal: Boolean,
    var attribute: Attribute,
    regex: String,
    initialShare: Share,
    override var name: String = "",
) : Rule<RegexRule> {

    enum class Attribute {
        FILENAME,
        FILEPATH,
        TITLE,
        ARTIST,
        USERTAGS
    }

    override var share = initialShare
    var regex: String
        get() = matcher.pattern
        set(value) { matcher = Regex(value) }

    private var matcher = Regex(regex)

    //region public functions
    override fun generateItems(amount: Int, exclude: Set<PlaylistItem>): List<PlaylistItem> {
        val excludedFiles = exclude.mapNotNull {
            if(it is MediaFileItem)
                it.file
            else
                null
        }.toSet()

        return collectFiles(excludedFiles)
            .shuffled()
            .takeOrAll(amount)
            .map { MediaFileItem(it) }
            .toList()
    }

    override fun copy(): RegexRule {
        return RegexRule(
            id,
            false,
            attribute,
            regex,
            share.copy(),
            name
        )
    }

    override fun equals(other: Any?): Boolean {
        if(other !is RegexRule)
            return false

        return this.attribute == other.attribute
                && this.regex == other.regex
                && this.share == other.share
                && this.name == other.name
    }

    override fun hashCode(): Int {
        return arrayOf<Any>(this::class.qualifiedName!!, attribute, regex, name).contentDeepHashCode()
    }

    override fun applyFrom(other: RegexRule) {
        attribute = other.attribute
        matcher = Regex(other.regex)
        share = other.share.copy()
        name = other.name
    }
    //endregion

    //region private functions
    private fun collectFiles(exclude: Set<MediaFile>): Sequence<MediaFile> {
        return MediaLibrary.Slot.INSTANCE.getAllFiles().asSequence().filterNot {
            exclude.contains(it)
        }.map {
            val attr: List<String> = when (attribute) {
                Attribute.FILENAME -> listOf(it.name)
                Attribute.FILEPATH -> listOf(it.path)
                Attribute.TITLE -> listOf(it.mediaTags.title)
                Attribute.ARTIST -> listOf(it.mediaTags.artist)
                Attribute.USERTAGS -> it.userTags
            }.filterNot {
                it.isNullOrEmpty()
            }.castTo<List<String>>()

            Pair(it, attr)
        }.filterNot {
            it.second.isEmpty()
        }.filter {
            it.second.any {
                matcher.matches(it)
            }
        }.map {
            it.first
        }
    }
    //endregion
}
