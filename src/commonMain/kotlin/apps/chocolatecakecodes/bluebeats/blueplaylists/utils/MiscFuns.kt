package apps.chocolatecakecodes.bluebeats.blueplaylists.utils

inline fun assert(value: Boolean, lazyMessage: () -> Any) {
    if(!value) throw AssertionError(lazyMessage())
}
