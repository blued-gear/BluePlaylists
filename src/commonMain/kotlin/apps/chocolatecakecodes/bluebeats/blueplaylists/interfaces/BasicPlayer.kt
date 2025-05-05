package apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces

import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media.MediaFile

interface BasicPlayer {

    fun playMedia(media: MediaFile, keepPlaylist: Boolean)
}
