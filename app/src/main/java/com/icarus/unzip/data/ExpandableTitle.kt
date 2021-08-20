package com.icarus.unzip.data

data class ExpandableTitle(
    val tag: Any,
    val title: String,
    val sort: String,
    var state: Boolean = false
) : Comparable<ExpandableTitle> {
    override fun equals(other: Any?): Boolean {
        if (other !is ExpandableTitle) return false
        return tag == other.tag
    }

    override fun hashCode(): Int {
        return tag.hashCode()
    }

    override fun compareTo(other: ExpandableTitle): Int {
        return sort.compareTo(other.sort)
    }
}
