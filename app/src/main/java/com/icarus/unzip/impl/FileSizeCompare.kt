package com.icarus.unzip.impl

import com.icarus.unzip.data.FileData

class FileSizeCompare : Comparator<FileData> {
    override fun compare(f1: FileData?, f2: FileData?): Int {
        f1 as FileData
        f2 as FileData
        return when {
            f1.file.isDirectory && f2.file.isFile -> -1
            f1.file.isFile && f2.file.isDirectory -> 1
            f1.file.isDirectory && f2.file.isDirectory -> f1.name.compareTo(f2.name,true)
            f1.file.isFile && f2.file.isFile -> f2.length.compareTo(f1.length)
            else -> 0
        }
    }
}