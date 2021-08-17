package com.icarus.unzip.coustomView

import a.icarus.component.BaseDialog
import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import com.icarus.unzip.R
import com.icarus.unzip.data.ZipPara
import com.icarus.unzip.util.*
import java.io.File

class ZipParaDialog(context: Context) :
    BaseDialog(context, R.layout.dialog_zip_para) {

    var fileName: String = ""
        set(value) {
            nameInput.text = value
            field = value
        }
    var path = ""
        set(value) {
            field = value
            pathView.text = value.getTextPath()
        }

    private var nameInput = findTextView(R.id.file_name)
    private var pathView = findTextView(R.id.file_path)

    private var zipType: Spinner = findViewById(R.id.zip_type)
    private var lockType: Spinner = findViewById(R.id.lock_type)

    private var passwordInput = findTextView(R.id.password)
    private var passwordGroup: Group = findViewById(R.id.group_password)
    private var deleteBox: CheckBox = findViewById(R.id.delete_source)

    var zipStartListener: ((para: ZipPara) -> Unit)? = null
    var pathSelectListener: (() -> Unit)? = null

    init {
        setCanceledOnTouchOutside(false)
        setViewOnClickListener(R.id.confirm) {
            fileName = nameInput.text.toString().trim()
            if (fileName.isEmpty()) {
                fileNameEmpty()
                return@setViewOnClickListener
            }
            if (fileName.contains(File.separatorChar) || fileName.isEmpty()) {
                fileNameInvalid()
                return@setViewOnClickListener
            }
            val realName =
                if (fileName.endsWith(".zip")) fileName else "${fileName}.zip"
            val file = File(path, realName)
            if (file.exists()) {
                fileExists()
                return@setViewOnClickListener
            }
            zipStartListener?.run {
                val zipPara = ZipPara(
                    path,
                    realName,
                    zipType.selectedItemPosition,
                    lockType.selectedItemPosition,
                    passwordInput.text.toString().trim(),
                    deleteBox.isChecked
                )
                this(zipPara)
            }
            dismiss()
        }
        setViewOnClickListener(R.id.cancel) {
            dismiss()
        }
        setViewOnClickListener(R.id.file_path) {
            pathSelectListener?.run {
                this()
            }
        }
        setViewOnClickListener(R.id.more) {
            pathSelectListener?.run {
                this()
            }
        }

        zipType.dropDownVerticalOffset = 36.toPx()
        zipType.setSelection(2)
        lockType.dropDownVerticalOffset = 36.toPx()

        zipType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (view is TextView) {
                    view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13F)
                    view.setTextColor(0xFF333333.toInt())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
        lockType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (view is TextView) {
                    view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13F)
                    view.setTextColor(0xFF333333.toInt())
                }
                passwordGroup.visible(position != 0)
                if (position == 0) {
                    passwordInput.text = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }
}