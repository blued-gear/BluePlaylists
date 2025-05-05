package apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media

import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.BasicPlayer
import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.items.TimeSpanItem

interface TimeSpanItemPlayerController {

    fun interface Factory {

        /** consuming projects should put their implementation here */
        object Slot {
            lateinit var INSTANCE: TimeSpanItemPlayerController.Factory
        }

        fun create(): TimeSpanItemPlayerController
    }

    fun init(item: TimeSpanItem, player: BasicPlayer)

    /**
     * @param callback called after the controller was registered with the player
     */
    fun register(callback: () -> Unit)

    suspend fun unregister()
}
