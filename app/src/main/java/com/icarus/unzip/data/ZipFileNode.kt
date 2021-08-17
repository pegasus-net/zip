package com.icarus.unzip.data

data class ZipFileNode(
    val path: String,
    val parent: String,
    val name: String,
    val isDirectory: Boolean,
    var size: Long = 0
) : Comparable<ZipFileNode> {
    override fun compareTo(other: ZipFileNode): Int {
        return when {
            isDirectory && !other.isDirectory -> -1
            !isDirectory && other.isDirectory -> 1
            else -> name.compareTo(other.name, true)
        }
    }

}