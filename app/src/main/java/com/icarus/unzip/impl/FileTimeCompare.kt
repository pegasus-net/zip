package com.icarus.unzip.impl

import com.icarus.unzip.data.FileData

class FileTimeCompare : Comparator<FileData> {
    override fun compare(f1: FileData?, f2: FileData?): Int {
        f1 as FileData
        f2 as FileData
        return when {
            f1.file.isDirectory && f2.file.isFile -> -1
            f1.file.isFile && f2.file.isDirectory -> 1
            else -> f2.lastTime.compareTo(f1.lastTime)
        }
    }
}