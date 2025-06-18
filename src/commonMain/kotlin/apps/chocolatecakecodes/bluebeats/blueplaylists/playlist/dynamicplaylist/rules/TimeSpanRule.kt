package apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.dynamicplaylist.rules

import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media.MediaFile
import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media.MediaNode
import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.items.PlaylistItem
import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.items.TimeSpanItem

class TimeSpanRule(
    override val id: Long,
    override val isOriginal: Boolean,
    var file: MediaFile,
    var startMs: Long,
    var endMs: Long,
    var description: String,
    initialShare: Share,
    override var name: String = "",
): Rule<TimeSpanRule> {

    override var share = initialShare

    override fun generateItems(amount: Int, exclude: Set<PlaylistItem>): List<PlaylistItem> {
        if(amount == 0)
            return emptyList()

        if(file === MediaNode.INVALID_FILE)
            return emptyList()

        val item = TimeSpanItem(file, startMs, endMs)

        if(exclude.contains(item))
            return emptyList()

        return listOf(item)
    }

    override fun copy(): TimeSpanRule {
        return TimeSpanRule(
            id,
            false,
            file,
            startMs,
            endMs,
            description,
            share.copy(),
            name
        )
    }

    override fun applyFrom(other: TimeSpanRule) {
        file = other.file
        startMs = other.startMs
        endMs = other.endMs
        description = other.description
        share = other.share.copy()
        name = other.name
    }

    override fun equals(other: Any?): Boolean {
        if(other !is TimeSpanRule)
            return false

        return this.file.shallowEquals(other.file)
                && this.startMs == other.startMs
                && this.endMs == other.endMs
                && this.share == other.share
                && this.name == other.name
    }

    override fun hashCode(): Int {
        return arrayOf<Any>(this::class.qualifiedName!!, file, startMs, endMs, share, name).contentDeepHashCode()
    }
}
