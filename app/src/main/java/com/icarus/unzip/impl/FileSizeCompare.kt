package com.icarus.unzip.impl

import java.io.File

class FileSizeCompare : Comparator<File> {
    override fun compare(f1: File?, f2: File?): Int {
        f1 as File
        f2 as File
        return when {
            f1.isDirectory && f2.isFile -> -1
            f1.isFile && f2.isDirectory -> 1
            f1.isDirectory && f2.isDirectory -> f1.name.compareTo(f2.name,true)
            f1.isFile && f2.isFile -> f2.length().compareTo(f1.length())
            else -> 0
        }
    }
}