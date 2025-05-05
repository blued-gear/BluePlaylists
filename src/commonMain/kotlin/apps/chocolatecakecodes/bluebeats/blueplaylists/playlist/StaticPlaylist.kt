package apps.chocolatecakecodes.bluebeats.blueplaylists.playlist

import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media.MediaFile
import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.items.MediaFileItem
import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.items.PlaylistItem

/**
 * a normal playlist where the user puts media in manually
 */
class StaticPlaylist(
    override val id: Long,
    override var name: String,
    entries: List<PlaylistItem>
) : Playlist {

    override val type: PlaylistType = PlaylistType.STATIC

    private val media: ArrayList<PlaylistItem> = ArrayList(entries)

    override fun items(): List<PlaylistItem> {
        return media
    }

    override fun getIterator(repeat: PlaylistIterator.RepeatMode, shuffle: Boolean): PlaylistIterator {
        val validMedia = media.filter { it !is PlaylistItem.INVALID }
        return StaticPlaylistIterator(validMedia, repeat, shuffle)
    }

    fun addMedia(toAdd: MediaFile) {
        media.add(MediaFileItem(toAdd))
    }

    fun removeMedia(index: Int) {
        media.removeAt(index)
    }

    fun moveMedia(from: Int, newIndex: Int) {
        val item = media.removeAt(from)
        media.add(newIndex, item)
    }
}

private class StaticPlaylistIterator(
    media: List<PlaylistItem>,
    override var repeat: PlaylistIterator.RepeatMode,
    shuffle: Boolean
) : PlaylistIterator {

    private val items = ArrayList(media)

    override val totalItems: Int = items.size
    override var currentPosition: Int = -1
        private set

    override var shuffle: Boolean = shuffle
        set(value) {
            field = value

            if(value)
                shuffle()
        }

    init {
        if(shuffle)
            shuffle()
    }

    override fun nextItem(): PlaylistItem {
        if(isAtEnd())
            throw NoSuchElementException("end reached")

        if(repeat != PlaylistIterator.RepeatMode.ONE)
            seek(1)

        return items[currentPosition]
    }

    override fun currentItem(): PlaylistItem {
        return items[currentPosition.coerceAtLeast(0)]
    }

    /**
     * seek relative to the current position
     * (use negative amount for seeking backward)
     *
     * if repeat is true then seeking to one after the last item is allowed;
     *  this will result to reset the iterator to the first item
     *
     * @throws IllegalArgumentException if seeking results in out-of-bounds
     */
    override fun seek(amount: Int) {
        if(amount == 0) return

        val newPos = currentPosition + amount

        if(newPos == totalItems && repeat == PlaylistIterator.RepeatMode.ALL) {
            currentPosition = 0

            if(shuffle)
                shuffle()
        } else if(newPos >= 0 && newPos < totalItems) {
            currentPosition = newPos
        } else {
            throw IllegalArgumentException("seeking by $amount would result in an out-of-bounds position")
        }
    }

    override fun isAtEnd(): Boolean {
        return repeat == PlaylistIterator.RepeatMode.NONE && currentPosition == (totalItems - 1)
    }

    override fun getItems(): List<PlaylistItem> {
        return items
    }

    private fun shuffle() {
        // current media must stay at same index
        if(currentPosition == -1) {
            items.shuffle()
        } else {
            val media = items.removeAt(currentPosition)
            items.shuffle()
            items.add(currentPosition, media)
        }
    }
}
