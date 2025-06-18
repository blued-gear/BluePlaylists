package apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.dynamicplaylist.rules

/**
 * determines how much items a rule should add to the resulting collection <br/>
 * there are four modes: relative (isRelative = true, value >= 0), absolute (isRelative = false, value >= 0),
 *  even (isRelative = true, value = -1; all with even will get the same relative value),
 *  unlimited (isRelative = false, value = -1)
 */
data class Share(
    val value: Float,
    /** if true the value is a percentage between 0 and 1.0;
     *     else it is the absolute amount of items which should be generated (should be cast to int) */
    val isRelative: Boolean
) {

    companion object {

        fun relative(relativeValue: Float = 1.0f) = Share(relativeValue, true)
        fun absolute(absoluteValue: Int = 1) = Share(absoluteValue.toFloat(), false)
        fun even() = Share(-1.0f, true)
        fun unlimited() = Share(-1.0f, false)
    }

    fun modeRelative() = isRelative && value >= 0
    fun modeAbsolute() = !isRelative && value >= 0
    fun modeEven() = isRelative && value == -1f
    fun modeUnlimited() = !isRelative && value == -1f
}
