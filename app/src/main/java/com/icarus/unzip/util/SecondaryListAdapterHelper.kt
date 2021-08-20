package com.icarus.unzip.util

import com.icarus.unzip.data.ExpandableTitle
import java.io.File
import java.io.PrintStream

class SecondaryListAdapterHelper(
    val index: List<ExpandableTitle>,
    val map: HashMap<ExpandableTitle, ArrayList<File>>,
    private val packUpCount: Int
) {
    fun getItem(position: Int): Any? {
        var p = 0
        for (i in index.indices) {
            if (position == p) return index[i]
            p++
            val arrayList = map[index[i]]!!
            val size =
                if (index[i].state) arrayList.size else arrayList.size.coerceAtMost(packUpCount)
            if (position < p + size) {
                return arrayList[position - p]
            }
            p += size
        }
        return null
    }
    
    fun getItemCount(): Int {
        var size = 0
        index.forEach {
            val arrayList = map[it]!!
            val listSize = if (it.state) arrayList.size else arrayList.size.coerceAtMost(packUpCount)
            size += listSize
        }
        return size + index.size
    }
}