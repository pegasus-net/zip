package com.icarus.unzip.adapter

import a.icarus.impl.RecyclerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.icarus.unzip.R
import com.icarus.unzip.util.*
import java.io.File

class DirectoryAdapter(list: List<File>) :
    RecyclerAdapter<File, DirectoryAdapter.ViewHolder>(list, R.layout.item_directory) {
    var itemClickListener: ((item: File, position: Int) -> Unit)? = null
    var ignore: List<File> = ArrayList()

    class ViewHolder(view: View) : RecyclerAdapter.ViewHolder(view) {
        val name: TextView = findViewById(R.id.name)
        val icon: ImageView = findViewById(R.id.icon)
    }

    override fun onCreateViewHolder(convertView: View): ViewHolder {
        return ViewHolder(convertView)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: File, position: Int) {

        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        if (position == list.size - 1) {
            layoutParams.bottomMargin = 75.toPx()
        } else {
            layoutParams.bottomMargin = 0
        }
        holder.name.text = item.name
        holder.name.setTextColor(
            if (ignore.contains(item)) 0xFFC0C0C0.toInt() else 0xFF333333.toInt()
        )
        holder.icon.setImageResource(
            if (ignore.contains(item)) R.drawable.folder_gray else R.drawable.file_item_1
        )
        holder.itemView.setOnClickListener {
            itemClickListener?.let {
                if (!ignore.contains(item)) {
                    it(item, position)
                }
            }
        }

    }

}