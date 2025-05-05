package apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.dynamicplaylist

import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.Playlist
import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.PlaylistIterator
import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.PlaylistType
import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.dynamicplaylist.rules.RuleGroup
import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.items.PlaylistItem

private const val EXAMPLE_ITEM_COUNT = 50

class DynamicPlaylist(
    override val id: Long,
    override var name: String,
    val rootRuleGroup: RuleGroup
) : Playlist {

    override val type: PlaylistType = PlaylistType.DYNAMIC

    /**
     * the minimum size after which media is allowed to repeat
     * (this determines how large the media-buffer will be before the next items will be generated)
     */
    var iterationSize = EXAMPLE_ITEM_COUNT

    override fun items(): List<PlaylistItem> {
        return rootRuleGroup.generateItems(iterationSize.coerceAtLeast(EXAMPLE_ITEM_COUNT), emptySet())
    }

    override fun getIterator(repeat: PlaylistIterator.RepeatMode, shuffle: Boolean): PlaylistIterator {
        return DynamicPlaylistIterator(rootRuleGroup, iterationSize)
    }
}
