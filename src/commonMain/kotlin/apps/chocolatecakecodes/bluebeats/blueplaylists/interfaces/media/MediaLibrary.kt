package apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media

interface MediaLibrary {

    /** consuming projects should put their implementation here */
    object Slot {
        lateinit var INSTANCE: MediaLibrary
    }

    fun getAllFiles(): Sequence<MediaFile>

    fun findFilesWithId3Tag(type: String, value: String): Sequence<MediaFile>

    /**
     * tags are OR combined
     * @return Map<file, List<tags>>
     */
    fun findFilesWithUsertags(tags: List<String>): Map<MediaFile, List<String>>

    fun fileExists(path: String): Boolean
}
