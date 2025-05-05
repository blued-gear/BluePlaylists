package apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media

abstract class MediaDir : MediaNode() {

    abstract fun getDirs(): List<MediaDir>

    abstract fun getFiles(): List<MediaFile>

    abstract fun findChild(name: String): MediaNode?

    abstract fun createCopy(): MediaDir

    /**
     * like [equals], with the difference all properties and children are compared (might result in recursive execution)
     */
    abstract fun deepEquals(that: MediaDir?): Boolean
}
