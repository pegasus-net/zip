package com.icarus.unzip.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.icarus.unzip.R
import com.icarus.unzip.data.ExpandableTitle
import com.icarus.unzip.util.*
import java.io.File

class MediaAdapter(
    private val helper: SecondaryListAdapterHelper,
    private val editPlug: AdapterEditPlug
) : RecyclerView.Adapter<ViewHolder>() {

    class ViewHolderTitle(view: View) : ViewHolder(view) {
        val title: CheckBox = view.findViewById(R.id.index_title)
    }

    class ViewHolderCover(view: View) : ViewHolder(view) {
        val cover: ImageView = view.findViewById(R.id.media_pic)
        val box: CheckBox = view.findViewById(R.id.box)
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
            TYPE_TITLE -> ViewHolderTitle(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_media_index,
                    parent,
                    false
                )
            )
            TYPE_INFO -> ViewHolderCover(
                LayoutInflater.from(parent.context).inflate(R.layout.item_media, parent, false)
            )
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
            is File -> {
                if (holder !is ViewHolderCover) return
                Glide.with(getAppContext()).load(item).error(R.drawable.error_image)
                    .into(holder.cover)

                holder.box.visible(editPlug.editMode)
                holder.box.isChecked = editPlug.isFileSelected(item)
                val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
                if (editPlug.editMode && position == itemCount - 1) {
                    layoutParams.bottomMargin = 75.toPx()
                } else {
                    layoutParams.bottomMargin = 0
                }
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
        if (any is File) return TYPE_INFO
        return 0
    }
}