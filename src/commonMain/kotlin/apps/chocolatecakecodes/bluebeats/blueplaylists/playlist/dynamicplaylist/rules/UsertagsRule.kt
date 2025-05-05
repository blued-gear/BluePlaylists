package apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.dynamicplaylist.rules

import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media.MediaFile
import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media.MediaLibrary
import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.items.MediaFileItem
import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.items.PlaylistItem
import apps.chocolatecakecodes.bluebeats.blueplaylists.utils.takeOrAll

class UsertagsRule(
    override var share: Rule.Share,
    /** if true returned files will match all tags; if false returned files will match any of the tags */
    var combineWithAnd: Boolean = true,
    override val isOriginal: Boolean,
    override val id: Long
) : Rule<UsertagsRule> {

    private val tags = HashSet<String>()

    fun addTag(tag: String) {
        tags.add(tag)
    }

    fun removeTag(tag: String) {
        tags.remove(tag)
    }

    fun getTags(): Set<String> {
        return tags
    }

    override fun generateItems(amount: Int, exclude: Set<PlaylistItem>): List<PlaylistItem> {
        val excludedFiles = exclude.mapNotNull {
            if(it is MediaFileItem)
                it.file
            else
                null
        }.toSet()

        // and-combine results (all tags must match)
        val tags = getTags()
        return MediaLibrary.Slot.INSTANCE.findFilesWithUsertags(tags.toList())
            .minus(excludedFiles)
            .let {
                if (combineWithAnd)
                    combineAnd(it)
                else
                    combineOr(it)
            }
            .map { MediaFileItem(it) }
            .toList()
            .shuffled()
            .takeOrAll(amount)
    }

    override fun copy(): UsertagsRule {
        return UsertagsRule(share.copy(), combineWithAnd, false, id).apply {
            tags.addAll(this@UsertagsRule.tags)
        }
    }

    override fun applyFrom(other: UsertagsRule) {
        tags.clear()
        tags.addAll(other.tags)
        combineWithAnd = other.combineWithAnd
        share = other.share.copy()
    }

    override fun equals(other: Any?): Boolean {
        if(other !is UsertagsRule)
            return false

        return this.getTags() == other.getTags()
                && this.combineWithAnd == other.combineWithAnd
                && this.share == other.share
    }

    override fun hashCode(): Int {
        return arrayOf<Any>(this::class.qualifiedName!!, getTags(), combineWithAnd, share).contentDeepHashCode()
    }

    private fun combineAnd(results: Map<MediaFile, List<String>>): Set<MediaFile> {
        return results.filterValues {
            it.containsAll(getTags())
        }.keys
    }

    private fun combineOr(results: Map<MediaFile, List<String>>): Set<MediaFile> {
        // already combined by getFilesForTags()
        return results.keys
    }
}
