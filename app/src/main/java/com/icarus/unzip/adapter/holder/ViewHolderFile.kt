package com.icarus.unzip.adapter.holder

import a.icarus.impl.RecyclerAdapter
import android.graphics.drawable.Drawable
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
import com.icarus.unzip.adapter.AdapterEditPlug
import com.icarus.unzip.data.FileData
import com.icarus.unzip.enums.FileType
import com.icarus.unzip.util.apkIcon
import com.icarus.unzip.util.getAppContext
import com.icarus.unzip.util.toPx
import com.icarus.unzip.util.visible
import java.io.File

class ViewHolderFile(view: View) : RecyclerAdapter.ViewHolder(view) {
    val name: TextView = findViewById(R.id.name)
    val size: TextView = findViewById(R.id.size)
    val time: TextView = findViewById(R.id.time)
    val icon: ImageView = findViewById(R.id.icon)
    val icon2: ImageView = findViewById(R.id.icon2)
    val box: CheckBox = findViewById(R.id.checkBox)

    fun bindFile(file: File, isLast: Boolean, editPlug: AdapterEditPlug) {
        val fileData = FileData(file)
        name.text = fileData.name
        size.text = fileData.size
        time.text = fileData.time
        icon.setImageResource(
            when (fileData.fileType) {
                FileType.DIRECTORY -> R.drawable.file_item_1
                FileType.ZIP -> R.drawable.file_item_3
                FileType.TEXT -> R.drawable.file_item_4
                FileType.IMAGE -> R.drawable.file_item_7
                FileType.MUSIC -> R.drawable.file_item_2
                FileType.VIDEO -> R.drawable.file_item_5
                FileType.APP -> R.drawable.file_item_6
                FileType.WORD -> R.drawable.file_item_4_1
                FileType.EXCEL -> R.drawable.file_item_4_2
                FileType.PPT -> R.drawable.file_item_4_3
                FileType.PDF -> R.drawable.file_item_4_4
                FileType.UNKNOWN -> R.drawable.file_item_0
            }
        )
        icon2.visible(fileData.fileType == FileType.VIDEO)
        if (fileData.fileType == FileType.VIDEO) {
            Glide.with(getAppContext()).load(fileData.file)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        icon2.visible(false)
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
                .into(icon)
        }
        if (fileData.fileType == FileType.IMAGE) {
            Glide.with(getAppContext()).load(file).error(R.drawable.file_item_7)
                .into(icon)
        }
        if (fileData.fileType == FileType.APP) {
            Glide.with(getAppContext()).load(file.apkIcon()).error(R.drawable.file_item_6)
                .into(icon)
        }
        box.visible(editPlug.editMode)
        box.isChecked = editPlug.isFileSelected(file)

        val layoutParams = itemView.layoutParams as ViewGroup.MarginLayoutParams
        if (editPlug.editMode && isLast) {
            layoutParams.bottomMargin = 75.toPx()
        } else {
            layoutParams.bottomMargin = 0
        }
    }
}