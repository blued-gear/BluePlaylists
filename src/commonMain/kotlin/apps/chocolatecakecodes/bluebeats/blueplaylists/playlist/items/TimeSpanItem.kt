package apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.items

import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.BasicPlayer
import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media.MediaFile
import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media.MediaNode
import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media.TimeSpanItemPlayerController

open class TimeSpanItem(
    override val file: MediaFile,
    val startMs: Long,
    val endMs: Long
) : PlaylistItem {

    override fun play(player: BasicPlayer) {
        val controller = TimeSpanItemPlayerController.Factory.Slot.INSTANCE.create()
        controller.init(this, player)
        controller.register {
            player.playMedia(file, true)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TimeSpanItem) return false

        if (!file.shallowEquals(other.file)) return false
        if (startMs != other.startMs) return false
        if (endMs != other.endMs) return false

        return true
    }

    override fun hashCode(): Int {
        return arrayOf(this::class.qualifiedName, file, startMs, endMs).contentHashCode()
    }

    override fun toString(): String {
        return "TimeSpanItem(file=$file, startMs=$startMs, endMs=$endMs)"
    }

    data object INVALID : TimeSpanItem(MediaNode.INVALID_FILE, 0, 0), PlaylistItem.INVALID {
        override fun play(player: BasicPlayer) {
            throw IllegalStateException("INVALID item may not be played")
        }
    }
}
