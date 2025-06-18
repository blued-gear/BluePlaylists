package apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.dynamicplaylist.rules

import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media.MediaDir
import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media.MediaFile
import apps.chocolatecakecodes.bluebeats.blueplaylists.interfaces.media.MediaLibrary
import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.items.MediaFileItem
import apps.chocolatecakecodes.bluebeats.blueplaylists.playlist.items.PlaylistItem
import apps.chocolatecakecodes.bluebeats.blueplaylists.utils.takeOrAll

/** <dir, deep (include all subdirs)> */
internal typealias DirPathInclude = Pair<MediaDir, Boolean>

class IncludeRule(
    override val id: Long,
    override val isOriginal: Boolean,
    initialShare: Share,
    dirs: Set<DirPathInclude> = emptySet(),
    files: Set<MediaFile> = emptySet(),
    override var name: String = "",
) : Rule<IncludeRule> {

    private val dirs = HashMap<MediaDir, Boolean>(dirs.associate { it.first to it.second })
    private val files = HashSet(files)

    override var share = initialShare

    override fun generateItems(amount: Int, exclude: Set<PlaylistItem>): List<PlaylistItem> {
        val excludedFiles = exclude.mapNotNull {
            if(it is MediaFileItem)
                it.file
            else
                null
        }.toSet()

        return expandDirs()
            .union(getFiles())
            .minus(excludedFiles)
            .filter { MediaLibrary.Slot.INSTANCE.fileExists(it.path) }
            .map { MediaFileItem(it) }
            .shuffled()
            .toList()
            .takeOrAll(amount)
    }

    fun getDirs(): Set<DirPathInclude> {
        return dirs.map {
            DirPathInclude(it.key, it.value )
        }.toSet()
    }

    fun getFiles() = files

    /**
     * Adds a dir to the exclude list.
     * If was already added, its former value for deep will be overwritten
     * @param dir the dir to exclude
     * @param deep if true the dir and all its subdirs will be excluded
     */
    fun addDir(dir: MediaDir, deep: Boolean) {
        dirs.put(dir, deep)
    }

    fun removeDir(dir: MediaDir) {
        dirs.remove(dir)
    }

    /**
     * Sets the deep value for the given dir.
     * If the dir did not exist, it will be added.
     */
    fun setDirDeep(dir: MediaDir, deep: Boolean) {
        dirs.put(dir, deep)
    }

    fun addFile(file: MediaFile) {
        files.add(file)
    }

    fun removeFile(file: MediaFile) {
        files.remove(file)
    }

    override fun copy(): IncludeRule {
        return IncludeRule(
            id,
            false,
            share.copy(),
            getDirs(),
            getFiles(),
            name
        )
    }

    override fun applyFrom(other: IncludeRule) {
        dirs.clear()
        dirs.putAll(other.dirs)
        files.clear()
        files.addAll(other.files)
        share = other.share.copy()
        name = other.name
    }

    override fun equals(other: Any?): Boolean {
        if(other !is IncludeRule)
            return false

        return this.getFiles() == other.getFiles()
                && this.getDirs() == other.getDirs()
                && this.share == other.share
                && this.name == other.name
    }

    override fun hashCode(): Int {
        return arrayOf<Any>(this::class.qualifiedName!!, getFiles(), getDirs(), share, name).contentDeepHashCode()
    }

    private fun expandDirs(): Set<MediaFile> {
        return getDirs().flatMap {
            if(it.second){
                expandDirRecursive(it.first)
            } else {
                it.first.getFiles()
            }
        }.toSet()
    }

    private fun expandDirRecursive(dir: MediaDir): Set<MediaFile> {
        return dir.getDirs()
            .flatMap(this::expandDirRecursive)
            .plus(dir.getFiles())
            .toSet()
    }
}
