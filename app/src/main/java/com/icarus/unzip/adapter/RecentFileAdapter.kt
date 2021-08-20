package com.icarus.unzip.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.icarus.unzip.R
import com.icarus.unzip.adapter.holder.ViewHolderFile
import com.icarus.unzip.data.ExpandableTitle
import com.icarus.unzip.util.*

class RecentFileAdapter(
    private val helper: SecondaryListAdapterHelper,
    private val editPlug: AdapterEditPlug
) :
    RecyclerView.Adapter<ViewHolder>() {

    class ViewHolderTitle(view: View) : ViewHolder(view) {
        val title: CheckBox = view.findViewById(R.id.index_title)
    }

    class ViewHolderEmpty(context: Context) : ViewHolder(TextView(context)) {
        init {
            itemView as TextView
            itemView.gravity = Gravity.CENTER
            itemView.text = "什么都没有"
        }
    }

    companion object {
        const val TYPE_TITLE = 1
        const val TYPE_INFO = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            TYPE_TITLE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_media_index, parent, false)
                ViewHolderTitle(view)
            }
            TYPE_INFO -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_file, parent, false)
                ViewHolderFile(view)
            }
            else -> ViewHolderEmpty(parent.context)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val item = helper.getItem(position)) {
            is ExpandableTitle -> {
                if (holder !is ViewHolderTitle) return
                holder.title.isChecked = item.state
                holder.title.text = item.title
                holder.itemView.setOnClickListener {
                    item.state = !item.state
                    notifyDataSetChanged()
                }
            }
            is java.io.File -> {
                if (holder !is ViewHolderFile) return
                holder.bindFile(item, position == itemCount - 1, editPlug)
                holder.itemView.setOnClickListener {
                    if (editPlug.editMode) {
                        editPlug.toggleFile(item)
                        notifyItemChanged(position)
                    } else {
                        item.open()
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
    }

    override fun getItemCount(): Int {
        return helper.getItemCount()
    }

    override fun getItemViewType(position: Int): Int {
        val any = helper.getItem(position)
        if (any is ExpandableTitle) return TYPE_TITLE
        if (any is java.io.File) return TYPE_INFO
        return 0
    }
}