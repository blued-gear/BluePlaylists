package apps.chocolatecakecodes.bluebeats.blueplaylists.playlist

import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.items.PlaylistItem

/**
 * provides a generic interface for the most important playlist-attributes
 */
interface Playlist {

    /**
     * some per-type unique ID the target implementation uses for storage
     */
    val id: Long

    /**
     * the type of the playlist
     */
    val type: PlaylistType

    /**
     * the name of the playlist
     */
    var name: String

    /**
     * returns all items in this playlist
     * (in case of dynamic playlists this may only be a representative selection)
     */
    fun items(): List<PlaylistItem>

    /**
     * generates a PlaylistIterator to play this playlist with the given parameters
     * @param repeat if the playlist should play endlessly
     * @param shuffle if the playlist should be shuffled
     */
    fun getIterator(repeat: PlaylistIterator.RepeatMode, shuffle: Boolean): PlaylistIterator
}

enum class PlaylistType {

    /**
     * type of playlist where all media was added by the user
     */
    STATIC,

    /**
     * type of playlist where the list of next media is generated based on rules at every play-through
     */
    DYNAMIC
}
