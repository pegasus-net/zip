package com.icarus.unzip.inter

import java.io.File

interface FileEditable {
    fun copy(copyFiles: List<File>)
    fun zip(zipFiles: List<File>, defaultDir: File, defaultName: String? = null)
    fun unzip(file: File)
}