package apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.log

interface Logger {

    /** consuming projects should put their implementation here */
    object Slot {
        lateinit var INSTANCE: Logger
    }

    fun info(tag: String, message: String)

    fun warn(tag: String, message: String)

    fun error(tag: String, message: String)

    fun error(tag: String, message: String, throwable: Throwable)
}
