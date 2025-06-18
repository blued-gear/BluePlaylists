package apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.dynamicplaylist.rules

import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.log.Logger
import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media.MediaLibrary
import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.items.MediaFileItem
import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.items.PlaylistItem
import apps.chocolatecakecodes.bluebeats.blueplaylists.utils.takeOrAll

private const val LOG_TAG = "ID3TagsRule"

class ID3TagsRule(
    override var share: Share,
    override val isOriginal: Boolean,
    override val id: Long,
) : Rule<ID3TagsRule> {

    private val tagValues = HashSet<String>()

    var tagType: String = ""
        set(value) {
            field = value
            // reset values because they may not fit to the new type anymore
            tagValues.clear()
        }

    fun getTagValues(): Set<String> {
        return tagValues
    }

    fun addTagValue(value: String) {
        tagValues.add(value)
    }

    fun removeTagValue(value: String) {
        tagValues.remove(value)
    }

    override fun generateItems(amount: Int, exclude: Set<PlaylistItem>): List<PlaylistItem> {
        if(tagType.isEmpty()) {
            Logger.Slot.INSTANCE.warn(LOG_TAG, "no tag-type set; skipping")
            return emptyList()
        }

        val excludedFiles = exclude.mapNotNull {
            if(it is MediaFileItem)
                it.file
            else
                null
        }.toSet()

        return getTagValues()
            .flatMap { MediaLibrary.Slot.INSTANCE.findFilesWithId3Tag(tagType, it) }
            .toSet()
            .minus(excludedFiles)
            .map { MediaFileItem(it) }
            .shuffled().takeOrAll(amount)
    }

    override fun copy(): ID3TagsRule {
        return ID3TagsRule(share.copy(), false, id).apply {
            tagType = this@ID3TagsRule.tagType
            tagValues.addAll(this@ID3TagsRule.tagValues)
        }
    }

    override fun applyFrom(other: ID3TagsRule) {
        tagType = other.tagType
        tagValues.addAll(other.tagValues)
        share = other.share.copy()
    }

    override fun equals(other: Any?): Boolean {
        if(other !is ID3TagsRule)
            return false

        return this.tagType == other.tagType
                && this.getTagValues() == other.getTagValues()
                && this.share == other.share
    }

    override fun hashCode(): Int {
        return arrayOf<Any>(this::class.qualifiedName!!, tagType, getTagValues(), share).contentDeepHashCode()
    }
}
