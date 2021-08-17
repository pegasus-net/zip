package com.icarus.unzip.adapter

import a.icarus.impl.RecyclerAdapter
import android.view.View
import android.widget.TextView
import com.icarus.unzip.R
import com.icarus.unzip.util.*
import java.io.File

class FileIndexAdapter(list: List<File>) :
    RecyclerAdapter<File, FileIndexAdapter.ViewHolder>(list, R.layout.item_file_index) {
    var itemClickListener: ((item: File, position: Int) -> Unit)? = null
    var backView: TextView? = null
        set(value) {
            field = value
            backViewTint()
            value?.setOnClickListener {
                if (list.size > 1) {
                    remove(list.size - 2)
                }
            }
        }

    class ViewHolder(view: View) : RecyclerAdapter.ViewHolder(view) {
        val name: TextView = findViewById(R.id.name)
    }

    override fun onCreateViewHolder(convertView: View): ViewHolder {
        return ViewHolder(convertView)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: File, position: Int) {
        holder.name.text = if (position == 0) "内部储存" else item.name
        holder.itemView.setOnClickListener {
            remove(position)
        }
    }

    private fun remove(position: Int) {
        if (position < list.size - 1) {
            itemClickListener?.let {
                it(list[position], position)
            }
            while (list.size > position + 1) {
                list.removeAt(position + 1)
            }
            backViewTint()
            notifyDataSetChanged()
        }
    }

    fun add(file: File) {
        list.add(file)
        backViewTint()
    }

    private fun backViewTint() {
        val color = if (list.size > 1)
            getAppResources().getColor(R.color.main_color, null)
        else 0xFF999999.toInt()
        backView?.run {
            setTextColor(color)
            compoundDrawables.forEach {
                it?.setTint(color)
            }
        }
    }
}