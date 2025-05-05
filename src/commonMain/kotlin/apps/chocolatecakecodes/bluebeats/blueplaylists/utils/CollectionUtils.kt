package apps.chocolatecakecodes.bluebeats.blueplaylists.utils

object CollectionUtils {

    /**
     * @return (added, deleted, same)
     */
    fun <T> diffChanges(old: Set<T>, new: Set<T>): Triple<Set<T>, Set<T>, Set<T>> {
        return Triple(
            new.minus(old),
            old.minus(new),
            old.intersect(new)
        )
    }
}
