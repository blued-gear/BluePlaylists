package apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.dynamicplaylist

import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.PlaylistIterator
import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.PlaylistIterator.Companion.UNDETERMINED_COUNT
import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.dynamicplaylist.rules.RuleGroup
import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.items.MediaFileItem
import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.items.PlaylistItem
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.concurrent.Volatile

class DynamicPlaylistIterator(
    private val rootRuleGroup: RuleGroup,
    private val bufferSize: Int
) : PlaylistIterator {

    private val mediaBuffer = ArrayList<PlaylistItem>(bufferSize + 2)
    private val pregeneratedMediaBuffer = ArrayList<PlaylistItem>(bufferSize + 1)
    @Volatile
    private var pregeneratedMediaBufferValid: Boolean = false
    private val mediaBufferMutex = Mutex()

    override val totalItems: Int = UNDETERMINED_COUNT
    @Volatile
    override var currentPosition: Int = -1
        private set

    override var repeat: PlaylistIterator.RepeatMode = PlaylistIterator.RepeatMode.ALL
        set(value) {
            field = if(value != PlaylistIterator.RepeatMode.NONE) value else PlaylistIterator.RepeatMode.ALL// NONE is forbidden
        }
    @Suppress("SetterBackingFieldAssignment")
    override var shuffle: Boolean = true
        set(_) {
            // used to trigger regeneration
            val current = currentItem()
            withMediaBufferLock { generateItems(mediaBuffer, currentItem()) }
            seekToMedia(current)
        }

    init {
        generateItems(mediaBuffer, null)
        currentPosition = -1
    }

    override fun nextItem(): PlaylistItem {
        if(repeat != PlaylistIterator.RepeatMode.ONE)
            seek(1)

        return currentItem()
    }

    override fun currentItem(): PlaylistItem {
        return withMediaBufferLock {
            if(mediaBuffer.isNotEmpty())
                mediaBuffer[currentPosition.coerceAtLeast(0)]
            else
                MediaFileItem.INVALID
        }
    }

    override fun seek(amount: Int) {
        if(amount == 0) return

        val newPos = currentPosition + amount

        if(newPos == mediaBuffer.size) {
            withMediaBufferLock {
                if(pregeneratedMediaBufferValid) {
                    mediaBuffer.clear()
                    mediaBuffer.addAll(pregeneratedMediaBuffer)
                    pregeneratedMediaBufferValid = false
                } else {
                    generateItems(mediaBuffer, mediaBuffer.last())// retain last media and place at top
                }

                if(mediaBuffer.size > 1)
                    currentPosition = 1// current media is at idx 0 but we want the next one
                else
                    currentPosition = 0
            }
        } else if(newPos >= 0 && newPos < mediaBuffer.size) {
            currentPosition = newPos

            // pregenerate items if near end of current buffer
            if(currentPosition >= mediaBuffer.size - 2){
                CoroutineScope(Dispatchers.IO).launch {
                    pregeneratedMediaBufferValid = false
                    generateItems(pregeneratedMediaBuffer, mediaBuffer.last())// retain last media and place at top
                    pregeneratedMediaBufferValid = true
                }
            }
        } else {
            throw IllegalArgumentException("seeking by $amount would result in an out-of-bounds position")
        }
    }

    /**
     * Tries to find the media in the current buffer and selects it as the next media.
     * If the media could not be found in the buffer, it will be inserted after the current item.
     */
    fun seekToMedia(media: PlaylistItem) {
        withMediaBufferLock {
            val idx = mediaBuffer.indexOf(media)
            if(idx != -1) {
                currentPosition = idx - 1
            } else {
                mediaBuffer.add(currentPosition + 1, media)
            }
        }
    }

    override fun isAtEnd(): Boolean {
        return false
    }

    override fun getItems(): List<PlaylistItem> {
        return mediaBuffer
    }

    private fun generateItems(dest: MutableList<PlaylistItem>, prepend: PlaylistItem?) {
        val toExclude = if(prepend !== null) setOf(prepend) else emptySet()// exclude to prevent repetition

        dest.clear()
        if(prepend != null)
            dest.add(prepend)
        dest.addAll(rootRuleGroup.generateItems(bufferSize, toExclude).shuffled())
    }

    private inline fun <R> withMediaBufferLock(crossinline block: () -> R): R {
        return runBlocking {
            return@runBlocking mediaBufferMutex.withLock {
                return@withLock block()
            }
        }
    }
}
