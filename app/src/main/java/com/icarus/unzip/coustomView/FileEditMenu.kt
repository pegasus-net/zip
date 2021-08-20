package com.icarus.unzip.coustomView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.icarus.unzip.R
import com.icarus.unzip.util.visible

class FileEditMenu(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    LinearLayout(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val color33 = 0xFF333333.toInt()
    private val color99 = 0xFF999999.toInt()
    private val zip: TextView
    private val copy: TextView
    private val delete: TextView
    private val cancel: TextView
    var menuItemClickListener: ((menuItem: MenuItem) -> Unit)? = null

    var unzipMode = false
        set(value) {
            zip.text = if (value) "解压" else "压缩"
            field = value
        }
    var editable = false
        set(value) {
            zip.setTextColor(if (value) color33 else color99)
            copy.setTextColor(if (value) color33 else color99)
            delete.setTextColor(if (value) color33 else color99)
            field = value
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.file_edit_menu, this)
        zip = findViewById(R.id.zip)
        copy = findViewById(R.id.copy)
        delete = findViewById(R.id.delete)
        cancel = findViewById(R.id.cancel)
        visible(false)
        editable = false
        unzipMode = false
        zip.setOnClickListener {
            if (editable) {
                menuItemClickListener?.let { it((if (unzipMode) MenuItem.UNZIP else MenuItem.ZIP)) }
            }
        }
        copy.setOnClickListener {
            if (editable) {
                menuItemClickListener?.let { it(MenuItem.COPY) }
            }
        }
        delete.setOnClickListener {
            if (editable) {
                menuItemClickListener?.let { it(MenuItem.DELETE) }
            }
        }
        cancel.setOnClickListener {
            menuItemClickListener?.let { it(MenuItem.CANCEL) }
        }
    }

    enum class MenuItem {
        COPY, ZIP, UNZIP, DELETE, CANCEL
    }
}