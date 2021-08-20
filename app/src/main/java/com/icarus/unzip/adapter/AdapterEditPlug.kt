package com.icarus.unzip.adapter

import com.icarus.unzip.coustomView.FileEditMenu
import com.icarus.unzip.enums.FileType
import com.icarus.unzip.util.getType
import com.icarus.unzip.util.visible
import java.io.File

class AdapterEditPlug {
    private val editFiles = ArrayList<File>()
    private var editMenu: FileEditMenu? = null
    var editStateChangedListener: (() -> Unit)? = null
    var editMode = false
        set(value) {
            if (field != value) {
                editFiles.clear()
                editMenu?.run {
                    visible(value)
                    editable = false
                    unzipMode = false
                }
                editStateChangedListener?.let { it() }
                field = value
            }
        }

    fun toggleFile(file: File) {
        if (editFiles.contains(file)) {
            editFiles.remove(file)
        } else {
            editFiles.add(file)
        }
        editMenu?.run {
            editable = editFiles.isNotEmpty()
            unzipMode =
                editFiles.size == 1 && editFiles[0].getType() == FileType.ZIP
        }
    }


    fun bindEditMenu(editMenu: FileEditMenu?) {
        this.editMenu = editMenu
    }

    fun isFileSelected(file: File): Boolean = editFiles.contains(file)

    fun getList(): ArrayList<File> {
        val arrayList = ArrayList<File>()
        arrayList.addAll(editFiles)
        return arrayList
    }
}