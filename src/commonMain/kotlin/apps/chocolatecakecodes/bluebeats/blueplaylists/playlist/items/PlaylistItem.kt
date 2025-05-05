package apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.items

import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.BasicPlayer
import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media.MediaFile

sealed interface PlaylistItem {

    /** the MediaFile which will be played, or <code>null</code> if this item does not use a file */
    val file: MediaFile?

    fun play(player: BasicPlayer)

    /** marker for items which could not be loaded (for example when the referenced file was removed) */
    sealed interface INVALID : PlaylistItem
}
