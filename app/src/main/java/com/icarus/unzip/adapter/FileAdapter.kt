package com.icarus.unzip.adapter

import a.icarus.impl.RecyclerAdapter
import android.view.View
import com.icarus.unzip.R
import com.icarus.unzip.adapter.holder.ViewHolderFile
import com.icarus.unzip.data.FileData
import com.icarus.unzip.util.open
import java.io.File

class FileAdapter(list: List<File>, var editPlug: AdapterEditPlug) :
    RecyclerAdapter<File, ViewHolderFile>(list, R.layout.item_file) {

    var itemClickListener: ((item: File, position: Int) -> Unit) = { item, _ -> item.open() }

    override fun onCreateViewHolder(convertView: View): ViewHolderFile {
        return ViewHolderFile(convertView)
    }

    override fun onBindViewHolder(holder: ViewHolderFile, item: File, position: Int) {
        holder.bindFile(item, position == itemCount - 1, editPlug)
        holder.itemView.setOnClickListener {
            if (editPlug.editMode) {
                editPlug.toggleFile(item)
                notifyItemChanged(position)
            } else {
                itemClickListener(item, position)
            }
        }
        holder.itemView.setOnLongClickListener {
            if (editPlug.editMode) {
                false
            } else {
                editPlug.editMode = true
                editPlug.toggleFile(item)
                notifyItemChanged(position)
                true
            }
        }
    }
}