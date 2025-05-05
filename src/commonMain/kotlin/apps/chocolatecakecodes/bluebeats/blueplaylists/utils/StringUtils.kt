package apps.chocolatecakecodes.bluebeats.blueplaylists.utils

object StringUtils {

    /**
     * compares the strings as lowercase and if they are equal in their original case
     */
    fun compareStringNaturally(o1: String, o2: String): Int {
        if(o1 === o2)
            return 0

        val lcCmp = o1.lowercase().compareTo(o2.lowercase())
        if(lcCmp != 0)
            return lcCmp

        return o1.compareTo(o2)
    }

    /**
     * compares two strings and return true if they are equal, or both null or empty
     */
    fun cmpStringNEE(a: String?, b: String?): Boolean {
        if(a.isNullOrEmpty() && b.isNullOrEmpty())
            return true
        return a == b
    }
}
