package apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.dynamicplaylist.rules

import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.items.PlaylistItem

typealias GenericRule = Rule<*>

/**
 * @param T type of the implementing class
 */
sealed interface Rule<T> {

    /**
     * some per-type unique ID the target implementation uses for storage
     */
    val id: Long

    /**
     * only one original may exist at any given time; multiple copies may exist; only the original can be stored to DB
     * @see Rule.copy
     */
    val isOriginal: Boolean

    var share: Share

    var name: String

    /**
     * returns a collection of media from this rule
     * @param amount the expected amount of media to return; if -1 then the amount is unlimited
     * @param exclude set of files which must not be contained in the resulting set
     */
    fun generateItems(amount: Int, exclude: Set<PlaylistItem>): List<PlaylistItem>

    /**
     * Returns a deep-copy of the rule and all its subrules.
     * The returned rule will have isOriginal set to false
     */
    fun copy(): T

    /**
     * Applies all properties of the given rule to this instance.
     * If this rule is a group then this method will be called on all subrules.
     */
    fun applyFrom(other: T)

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int
}
