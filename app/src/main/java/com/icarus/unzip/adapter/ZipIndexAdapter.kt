package com.icarus.unzip.adapter

import a.icarus.impl.RecyclerAdapter
import android.view.View
import android.widget.TextView
import com.icarus.unzip.R
import com.icarus.unzip.data.ZipFileNode

class ZipIndexAdapter(list: List<ZipFileNode>) :
    RecyclerAdapter<ZipFileNode, ZipIndexAdapter.ViewHolder>(list, R.layout.item_file_index) {
    var itemClickListener: ((item: ZipFileNode, position: Int) -> Unit)? = null
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

    override fun onBindViewHolder(holder: ViewHolder, item: ZipFileNode, position: Int) {
        holder.name.text = item.name
        holder.name.setTextColor( 0xFFFFC107.toInt())
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

    fun add(node: ZipFileNode) {
        list.add(node)
        backViewTint()
    }

    private fun backViewTint() {
        val color = if (list.size > 1)
            0xFFFFC107.toInt()
        else 0xFF999999.toInt()
        backView?.run {
            setTextColor(color)
            compoundDrawables.forEach {
                it?.setTint(color)
            }
        }
    }
}