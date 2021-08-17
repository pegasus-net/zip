package com.icarus.unzip.util

import a.icarus.utils.DateUtil
import a.icarus.utils.FileUtil
import android.content.Intent
import android.net.Uri
import android.os.Environment
import com.icarus.unzip.enums.FileType
import com.icarus.unzip.ui.activity.ZipPreviewActivity
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


fun File.getType(): FileType {

    if (exists()) {
        if (isDirectory) {
            return FileType.DIRECTORY
        }
        if (isFile) {
            return name.getType()
        }
    }
    return FileType.UNKNOWN
}

fun String.getType(): FileType {
    val apkRegex = Regex("\\.(apk)$", RegexOption.IGNORE_CASE)
    val videoRegex = Regex("\\.(mp4|wmv|avi|mkv|m4v|flv|rmvb|ts|3gp)$", RegexOption.IGNORE_CASE)
    val musicRegex = Regex("\\.(mp3|wav|ogg|flac)$", RegexOption.IGNORE_CASE)
    val imageRegex = Regex("\\.(jpg|jpeg|png|bmp|gif)$", RegexOption.IGNORE_CASE)
    val textRegex =
        Regex("\\.(txt|h|htm|html|c|cpp|conf|java|log|xml|rc|sh)$", RegexOption.IGNORE_CASE)
    val zipRegex = Regex("\\.(rar|zip|)$", RegexOption.IGNORE_CASE)
    return when {
        contains(apkRegex) -> FileType.APP
        contains(videoRegex) -> FileType.VIDEO
        contains(musicRegex) -> FileType.MUSIC
        contains(imageRegex) -> FileType.IMAGE
        contains(textRegex) -> FileType.TEXT
        contains(zipRegex) -> FileType.ZIP
        else -> FileType.UNKNOWN
    }
}

fun File.getNameWithoutType(): String {
    val lastIndexOf = name.lastIndexOf('.')
    return if (lastIndexOf != -1 && isFile) {
        name.substring(0, lastIndexOf)
    } else {
        name
    }
}

fun File.getTextPath(): String {
    return absolutePath.replace(getRootFile().absolutePath, "内部储存")
}

fun String.getTextPath(): String {
    return replace(getRootFile().absolutePath, "内部储存")
}

fun getRootFile(): File {
    return Environment.getExternalStorageDirectory()
}

fun File.open() {
    val intent = Intent()
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    if (getType() == FileType.ZIP) {
        intent.setClass(getAppContext(), ZipPreviewActivity::class.java)
        intent.data = Uri.parse(absolutePath)
        getAppContext().startActivity(intent)
        return
    } else {
        intent.action = Intent.ACTION_VIEW
        getMIMEType(this)?.let {
            intent.setDataAndType(FileUtil.getUri(this), it)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                getAppContext().startActivity(intent)
            } catch (e: Exception) {
                "没有合适的程序打开".show()
            }
            return
        }
    }
    "未知的文件类型".show()
}

private fun getMIMEType(file: File): String? {
    var type: String? = null
    val fName = file.name
    val dotIndex = fName.lastIndexOf(".")
    if (dotIndex < 0) {
        return type
    }
    val end = fName.substring(dotIndex, fName.length).toLowerCase(Locale.ROOT)
    if (end === "") return type
    for (i in MIME_MapTable.indices) {
        if (end == MIME_MapTable[i][0]) type = MIME_MapTable[i][1]
    }
    return type
}

private val MIME_MapTable = arrayOf(
    arrayOf(".3gp", "video/3gpp"),
    arrayOf(".apk", "application/vnd.android.package-archive"),
    arrayOf(".asf", "video/x-ms-asf"),
    arrayOf(".avi", "video/x-msvideo"),
    arrayOf(".bin", "application/octet-stream"),
    arrayOf(".bmp", "image/bmp"),
    arrayOf(".c", "text/plain"),
    arrayOf(".class", "application/octet-stream"),
    arrayOf(".conf", "text/plain"),
    arrayOf(".cpp", "text/plain"),
    arrayOf(".doc", "application/msword"),
    arrayOf(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    arrayOf(".xls", "application/vnd.ms-excel"),
    arrayOf(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    arrayOf(".exe", "application/octet-stream"),
    arrayOf(".gif", "image/gif"),
    arrayOf(".h", "text/plain"),
    arrayOf(".htm", "text/html"),
    arrayOf(".html", "text/html"),
    arrayOf(".jar", "application/java-archive"),
    arrayOf(".java", "text/plain"),
    arrayOf(".jpeg", "image/jpeg"),
    arrayOf(".jpg", "image/jpeg"),
    arrayOf(".js", "application/x-javascript"),
    arrayOf(".log", "text/plain"),
    arrayOf(".m3u", "audio/x-mpegurl"),
    arrayOf(".m4a", "audio/mp4a-latm"),
    arrayOf(".m4b", "audio/mp4a-latm"),
    arrayOf(".m4p", "audio/mp4a-latm"),
    arrayOf(".m4u", "video/vnd.mpegurl"),
    arrayOf(".m4v", "video/x-m4v"),
    arrayOf(".mov", "video/quicktime"),
    arrayOf(".mp2", "audio/x-mpeg"),
    arrayOf(".mp3", "audio/x-mpeg"),
    arrayOf(".mp4", "video/mp4"),
    arrayOf(".mpc", "application/vnd.mpohun.certificate"),
    arrayOf(".mpe", "video/mpeg"),
    arrayOf(".mpeg", "video/mpeg"),
    arrayOf(".mpg", "video/mpeg"),
    arrayOf(".mpg4", "video/mp4"),
    arrayOf(".mpga", "audio/mpeg"),
    arrayOf(".msg", "application/vnd.ms-outlook"),
    arrayOf(".ogg", "audio/ogg"),
    arrayOf(".pdf", "application/pdf"),
    arrayOf(".png", "image/png"),
    arrayOf(".pps", "application/vnd.ms-powerpoint"),
    arrayOf(".ppt", "application/vnd.ms-powerpoint"),
    arrayOf(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
    arrayOf(".prop", "text/plain"),
    arrayOf(".rc", "text/plain"),
    arrayOf(".rmvb", "audio/x-pn-realaudio"),
    arrayOf(".rtf", "application/rtf"),
    arrayOf(".sh", "text/plain"),
    arrayOf(".txt", "text/plain"),
    arrayOf(".wav", "audio/x-wav"),
    arrayOf(".wma", "audio/x-ms-wma"),
    arrayOf(".wmv", "audio/x-ms-wmv"),
    arrayOf(".wps", "application/vnd.ms-works"),
    arrayOf(".xml", "text/plain"),
    arrayOf("", "*/*")
)

fun Long.toDate() = DateUtil.format(Date(this))!!
fun Long.toSize() = (if (this < 1024) "${this}B" else FileUtil.formatBytes(this))!!

fun File.deleteWithDir(): Boolean {
    if (exists()) {
        return if (isDirectory) {
            val children = listFiles()
            if (children != null) {
                for (child in children) {
                    child.deleteWithDir()
                }
            }
            delete()
        } else {
            delete()
        }
    }
    return true
}


fun File.flat(): ArrayList<File> {
    val list = ArrayList<File>()
    if (isDirectory && list()?.size != 0) {
        listFiles()?.forEach {
            if (it.isDirectory) {
                list.addAll(it.flat())
            } else {
                list.add(it)
            }
        }
    } else {
        list.add(this)
    }
    return list
}

fun fileNameInvalid() {
    "文件名含有非法字符".show()
}

fun fileNameEmpty() {
    "空的文件名".show()
}

fun fileExists() {
    "文件或文件夹已经存在".show()
}

