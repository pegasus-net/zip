package com.icarus.unzip.impl

import com.icarus.unzip.data.FileData
import java.io.File

class FileTimeCompare : Comparator<File> {
    override fun compare(f1: File?, f2: File?): Int {
        f1 as File
        f2 as File
        return when {
            f1.isDirectory && f2.isFile -> -1
            f1.isFile && f2.isDirectory -> 1
            else -> f2.lastModified().compareTo(f1.lastModified())
        }
    }
}