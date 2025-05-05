package apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media

import apps.chocolatecakecodes.bluebeats.blueplaylists.model.tag.Chapter
import apps.chocolatecakecodes.bluebeats.blueplaylists.model.tag.TagFields

abstract class MediaFile : MediaNode() {

    private val nameWithoutExt: String by lazy {
        val dotIdx = name.lastIndexOf('.')
        return@lazy if(dotIdx != -1) name.substring(0, dotIdx) else name
    }

    abstract val type: Type

    abstract var mediaTags: TagFields

    abstract var chapters: List<Chapter>?

    abstract var userTags: List<String>

    open val title: String
        get() {
            return mediaTags.title.takeUnless { it.isNullOrEmpty() } ?: nameWithoutExt
        }

    /**
     * like [equals], with the difference that just the type and path are compared
     */
    abstract fun shallowEquals(that: MediaFile?): Boolean

    enum class Type {
        AUDIO, VIDEO, OTHER
    }
}
