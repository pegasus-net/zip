package com.icarus.unzip.data

import com.icarus.unzip.enums.FileType
import com.icarus.unzip.util.getType
import com.icarus.unzip.util.isNeedHide
import com.icarus.unzip.util.toDate
import com.icarus.unzip.util.toSize
import java.io.File

data class FileData(val file: File) {

    val name = file.name
    val path = file.absolutePath
    val lastTime = file.lastModified()

    val fileType = file.getType()
    val length = file.length()
    private val count: Int
        get() {
            return file.listFiles()?.filter { !it.isNeedHide() }?.size ?: 0
        }

    val time = lastTime.toDate()
    val size
        get() = if (fileType == FileType.DIRECTORY)
            "${count}项"
        else
            length.toSize()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (javaClass == other?.javaClass) {
            other as FileData
            if (file == other.file) return true
        }
        if (file.javaClass == other?.javaClass) {
            other as File
            if (file == other) return true
        }
        return false
    }

    override fun hashCode(): Int {
        return file.hashCode()
    }
}
