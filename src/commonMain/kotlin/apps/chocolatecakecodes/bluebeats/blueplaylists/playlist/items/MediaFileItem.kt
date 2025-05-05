package apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.items

import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.BasicPlayer
import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media.MediaFile
import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media.MediaNode

open class MediaFileItem(
    override val file: MediaFile
) : PlaylistItem {

    override fun play(player: BasicPlayer) {
        player.playMedia(file, true)
    }

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is MediaFileItem) return false

        if(!file.shallowEquals(other.file)) return false

        return true
    }

    override fun hashCode(): Int {
        return arrayOf(this::class.qualifiedName, file).contentHashCode()
    }

    override fun toString(): String {
        return "MediaFileItem(file=$file)"
    }

    data object INVALID : MediaFileItem(MediaNode.INVALID_FILE), PlaylistItem.INVALID {
        override fun play(player: BasicPlayer) {
            throw IllegalStateException("INVALID item may not be played")
        }
    }
}
