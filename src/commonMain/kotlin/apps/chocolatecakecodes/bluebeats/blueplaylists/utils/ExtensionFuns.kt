package apps.chocolatecakecodes.bluebeats.blueplaylists.utils

/**
 * returns n elements of this Iterable or all if amount == -1
 */
fun <T, I : Iterable<T>> I.takeOrAll(amount: Int): List<T> {
    return if(amount == -1)
        this.toList()
    else
        this.take(amount)
}

/**
 * returns n elements of this Iterable or all if amount == -1
 */
fun <T, I : Sequence<T>> I.takeOrAll(amount: Int): Sequence<T> {
    return if(amount == -1)
        this
    else
        this.take(amount)
}

/**
 * removes the first element which satisfies the filter
 * @return the removed element or null if none matched the filter
 */
fun <T, I : MutableIterable<T>> I.removeIfSingle(filter: (T) -> Boolean): T? {
    val iter = this.iterator()
    while (iter.hasNext()) {
        val el = iter.next()
        if (filter(el)) {
            iter.remove()
            return el
        }
    }

    return null
}

inline fun <reified T> Any.castTo(): T {
    return this as T
}

inline fun <reified T> Any.castToOrNull(): T? {
    return this as? T
}
