package com.icarus.unzip.adapter

import a.icarus.impl.RecyclerAdapter
import a.icarus.utils.DateUtil
import a.icarus.utils.FileUtil
import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.icarus.unzip.R
import com.icarus.unzip.data.ZipFileNode
import com.icarus.unzip.enums.FileType
import com.icarus.unzip.util.*
import java.io.File
import java.util.*

class ZipFileAdapter(list: List<ZipFileNode>) :
    RecyclerAdapter<ZipFileNode, ZipFileAdapter.ViewHolder>(list, R.layout.item_file) {
    var itemClickListener: ((item: ZipFileNode, position: Int) -> Unit)? = null


    class ViewHolder(view: View) : RecyclerAdapter.ViewHolder(view) {
        val name: TextView = findViewById(R.id.name)
        val icon: ImageView = findViewById(R.id.icon)
        val size: TextView = findViewById(R.id.size)
    }

    override fun onCreateViewHolder(convertView: View): ViewHolder {
        return ViewHolder(convertView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, item: ZipFileNode, position: Int) {

        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        if (position == list.size - 1) {
            layoutParams.bottomMargin = 75.toPx()
        } else {
            layoutParams.bottomMargin = 0
        }
        holder.name.text = item.name
        holder.size.text = item.size.toSize()
        if (item.isDirectory) {
            holder.icon.setImageResource(R.drawable.file_item_1)
            holder.size.text = "${item.size}项"
        } else {
            holder.icon.setImageResource(
                when (item.name.getType()) {
                    FileType.ZIP -> R.drawable.file_item_3
                    FileType.TEXT -> R.drawable.file_item_4
                    FileType.IMAGE -> R.drawable.file_item_7
                    FileType.MUSIC -> R.drawable.file_item_2
                    FileType.VIDEO -> R.drawable.file_item_5
                    FileType.APP -> R.drawable.file_item_6
                    else -> R.drawable.file_item_0
                }
            )
        }
        holder.itemView.setOnClickListener {
            itemClickListener?.let {
                it(item, position)
            }
        }

    }

}