package com.icarus.unzip.inter

import java.io.File

interface FileEditable {
    fun copy(copyFiles: List<File>)
    fun zip(zipFiles: List<File>)
    fun unzip(file: File)
    fun delete(deleteFiles: List<File>)
}