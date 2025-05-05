package apps.chocolatecakecodes.bluebeats.blueplaylists.model.tag

import apps.chocolatecakecodes.bluebeats.blueplaylists.utils.StringUtils

/**
 * holds all supported tag-values (in terms of ID3)
 */
data class TagFields(
    val title: String? = null,
    val artist: String? = null,
    val genre: String? = null,
    val length: Long = 0,
    //TODO add more
) {

    /**
     * like [equals], just that empty strings and null-values are treated as equal
     * @param other other object to compare against
     * @return true if this and other are equal (with "" eq. null)
     */
    fun laxEquals(other: TagFields): Boolean {
        return StringUtils.cmpStringNEE(other.title, this.title)
                && StringUtils.cmpStringNEE(other.artist, this.artist)
                && StringUtils.cmpStringNEE(other.genre, this.genre)
                && other.length == this.length
    }
}
