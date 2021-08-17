package com.icarus.unzip.adapter

import a.icarus.impl.RecyclerAdapter
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Vibrator
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.icarus.unzip.R
import com.icarus.unzip.enums.FileType
import com.icarus.unzip.data.FileData
import com.icarus.unzip.util.*
import java.io.File

class FileAdapter(list: List<FileData>) :
    RecyclerAdapter<FileData, FileAdapter.ViewHolder>(list, R.layout.item_file) {
    var itemClickListener: ((item: FileData, position: Int) -> Unit)? = null
    var editModeChangeListener: ((allow: Boolean) -> Unit)? = null
        set(value) {
            field = value
            value?.run { this(editMode) }
        }


    class ViewHolder(view: View) : RecyclerAdapter.ViewHolder(view) {
        val name: TextView = findViewById(R.id.name)
        val size: TextView = findViewById(R.id.size)
        val time: TextView = findViewById(R.id.time)
        val icon: ImageView = findViewById(R.id.icon)
        val icon2: ImageView = findViewById(R.id.icon2)
        val box: CheckBox = findViewById(R.id.checkBox)
    }

    override fun onCreateViewHolder(convertView: View): ViewHolder {
        return ViewHolder(convertView)
    }

    var selectedItems = ArrayList<FileData>()
    var filesChangeListener: ((files: List<File>) -> Unit)? = null
        set(value) {
            field = value
            value?.run { this(editFiles) }
        }
    var editMode = false
        set(value) {
            if (field != value) {
                selectedItems.clear()
                field = value
                editModeChangeListener?.run { this(value) }
                filesChangeListener?.run { this(editFiles) }
                notifyDataSetChanged()
            }
        }
    val editFiles: List<File>
        get() {
            return selectedItems.map { it.file }
        }

    override fun onBindViewHolder(holder: ViewHolder, item: FileData, position: Int) {
        holder.name.text = item.name
        holder.size.text = item.size
        holder.time.text = item.time
        holder.icon.setImageResource(
            when (item.fileType) {
                FileType.DIRECTORY -> R.drawable.file_item_1
                FileType.ZIP -> R.drawable.file_item_3
                FileType.TEXT -> R.drawable.file_item_4
                FileType.IMAGE -> R.drawable.file_item_7
                FileType.MUSIC -> R.drawable.file_item_2
                FileType.VIDEO -> R.drawable.file_item_5
                FileType.APP -> R.drawable.file_item_6
                FileType.UNKNOWN -> R.drawable.file_item_0
            }
        )
        holder.icon2.visible(item.fileType == FileType.VIDEO)
        if (item.fileType == FileType.VIDEO) {
            Glide.with(getAppContext()).load(item.file)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.icon2.visible(false)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                }).error(R.drawable.file_item_5)
                .into(holder.icon)
        }
        if (item.fileType == FileType.IMAGE) {
            Glide.with(getAppContext()).load(item.file).error(R.drawable.file_item_7)
                .into(holder.icon)
        }
        holder.box.visibility = if (editMode) View.VISIBLE else View.GONE
        holder.box.isChecked = selectedItems.contains(item)
        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        if (editMode && position == list.size - 1) {
            layoutParams.bottomMargin = 75.toPx()
        } else {
            layoutParams.bottomMargin = 0
        }
        holder.itemView.setOnClickListener {
            if (editMode) {
                if (selectedItems.contains(item)) {
                    selectedItems.remove(item)
                } else {
                    selectedItems.add(item)
                }
                filesChangeListener?.run { this(editFiles) }
                notifyItemChanged(position)
            } else {
                itemClickListener?.let {
                    it(item, position)
                }
            }
        }
        holder.itemView.setOnLongClickListener {
            if (editMode) {
                false
            } else {
                editMode = true
                (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(30)
                selectedItems.add(item)
                filesChangeListener?.run { this(editFiles) }
                true
            }
        }
    }

    fun delete() {
        if (selectedItems.size == 0) {
            return
        }
        val copy = selectedItems.toTypedArray()
        editMode = false
        subThread {
            copy.forEach {
                if (it.file.deleteWithDir()) mainThread {
                    if (list.remove(it)) notifyDataSetChanged()
                }
            }
        }
    }
}