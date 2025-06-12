package apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media

import apps.chocolatecakecodes.bluebeats.blueplaylists.model.tag.Chapter
import apps.chocolatecakecodes.bluebeats.blueplaylists.model.tag.TagFields
import apps.chocolatecakecodes.bluebeats.blueplaylists.utils.StringUtils

sealed class MediaNode : Comparable<MediaNode> {

    companion object {
        const val UNALLOCATED_NODE_ID: Long = 0
        const val NULL_PARENT_ID: Long = -1
        const val UNSPECIFIED_NODE_ID: Long = -2
    }

    object UNSPECIFIED_DIR: MediaDir() {
        override val id: Long = UNSPECIFIED_NODE_ID
        override val name: String = "~UNSPECIFIED~"
        override val path: String = name
        override val parent: MediaNode? = null
        override fun getDirs(): List<MediaDir> = emptyList()
        override fun getFiles(): List<MediaFile> = emptyList()
        override fun findChild(name: String): MediaNode? = null
        override fun createCopy(): MediaDir = this
        override fun deepEquals(that: MediaDir?): Boolean = this === that
        override fun equals(that: Any?): Boolean = this === that
        override fun hashCode(): Int = this::class.hashCode()
        override fun toString(): String = "MediaDir: $name"
    }
    object INVALID_FILE : MediaFile() {
        override val id: Long = UNSPECIFIED_NODE_ID
        override val type: Type = Type.OTHER
        override val name: String = "~Missing File~"
        override val path: String = name
        override val parent: MediaNode? = null
        override var mediaTags: TagFields = dummyMediaTags
        override var chapters: List<Chapter>? = emptyList()
        override var userTags: List<String> = emptyList()
        override fun shallowEquals(that: MediaFile?): Boolean = this === that
        override fun equals(that: Any?): Boolean = this === that
        override fun hashCode(): Int = this::class.hashCode()
        override fun toString(): String = "MediaFile: $name"
    }

    /**
     * some per-type unique ID the target implementation uses for storage
     */
    abstract val id: Long

    abstract val name: String
    abstract val path: String
    abstract val parent: MediaNode?// only for runtime-caching

    abstract override fun equals(that: Any?): Boolean

    abstract override fun hashCode(): Int

    abstract override fun toString(): String

    override fun compareTo(that: MediaNode): Int {
        // dirs before files
        if(this is MediaDir && that !is MediaDir)
            return -1
        if(this !is MediaDir && that is MediaDir)
            return 1

        return StringUtils.compareStringNaturally(this.name, that.name)
    }
}

private val dummyMediaTags = TagFields(null, null, null, 0)
