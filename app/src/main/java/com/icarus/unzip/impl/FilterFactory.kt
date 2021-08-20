package com.icarus.unzip.impl

import com.icarus.unzip.enums.FileType
import com.icarus.unzip.ui.activity.FileFilterActivity
import com.icarus.unzip.util.getRootFile
import com.icarus.unzip.util.getType
import com.icarus.unzip.util.scan
import java.io.File

class FilterFactory {

    companion object {

        val qq_1 =
            getRootFile().absolutePath + "/Android/data/com.tencent.mobileqq/Tencent/QQfile_recv"
        val qq_2 = getRootFile().absolutePath + "/tencent/QQfile_recv"
        val qq_3 = getRootFile().absolutePath + "/Android/data/com.tencent.tim/Tencent/TIMfile_recv"
        val qq_4 = getRootFile().absolutePath + "/tencent/TIMfile_recv"
        val vx_1 = getRootFile().absolutePath + "/Android/data/com.tencent.mm/MicroMsg/Download"
        val bd_1 = getRootFile().absolutePath + "/BaiduNetdisk"
        val down = getRootFile().absolutePath + "/Download"

        fun getFilterByName(name: String): (File) -> Boolean {
            return when (name) {
                "压缩包" -> { file: File ->
                    file.getType() == FileType.ZIP
                }
                "视频" -> { file: File -> file.getType() == FileType.VIDEO }
                "图片" -> { file: File -> file.getType() == FileType.IMAGE }
                "音乐" -> { file: File -> file.getType() == FileType.MUSIC }
                "文档" -> { file: File ->
                    val type = file.getType()
                    type == FileType.TEXT ||
                            type == FileType.WORD ||
                            type == FileType.EXCEL ||
                            type == FileType.PDF ||
                            type == FileType.PPT
                }
                "QQ" -> { file: File ->
                    file.parent?.contains(qq_1, true) ?: false ||
                            file.parent?.contains(qq_2, true) ?: false ||
                            file.parent?.contains(qq_3, true) ?: false ||
                            file.parent?.contains(qq_4, true) ?: false
                }
                "微信" -> { file: File -> file.parent?.contains(vx_1, true) ?: false }
                "百度网盘" -> { file: File -> file.parent?.contains(bd_1, true) ?: false }
                "下载" -> { file: File -> file.parent?.contains(down, true) ?: false }
                "Word" -> { file: File -> file.getType() == FileType.WORD }
                "Excel" -> { file: File -> file.getType() == FileType.EXCEL }
                "PPT" -> { file: File -> file.getType() == FileType.PPT }
                "PDF" -> { file: File -> file.getType() == FileType.PDF }
                "TXT" -> { file: File -> file.getType() == FileType.TEXT }
                "系统录音" -> { file: File -> false }
                else -> { _: File -> true }
            }
        }

        private fun getFilterByType(type: FileFilterActivity.Type): (File) -> Boolean {
            return when (type) {
                FileFilterActivity.Type.ZIP -> getFilterByName("压缩包")
                FileFilterActivity.Type.MUSIC -> getFilterByName("音乐")
                FileFilterActivity.Type.TEXT -> getFilterByName("文档")
                FileFilterActivity.Type.DOWNLOAD -> getFilterByName("下载")
                FileFilterActivity.Type.QQ -> getFilterByName("QQ")
                FileFilterActivity.Type.WE_CHAT -> getFilterByName("微信")
                FileFilterActivity.Type.BAI_DU -> getFilterByName("百度网盘")
            }
        }

        fun getScanResult(type: FileFilterActivity.Type): ArrayList<File> {
            val result = ArrayList<File>()
            val filter = getFilterByType(type)
            when (type) {
                FileFilterActivity.Type.ZIP,
                FileFilterActivity.Type.MUSIC,
                FileFilterActivity.Type.TEXT -> {
                    val arrayList = ArrayList<File>()
                    getRootFile().scan(arrayList)
                    result.addAll(arrayList.filter(filter))
                }
                FileFilterActivity.Type.DOWNLOAD -> File(down).scan(result, false)
                FileFilterActivity.Type.QQ -> {
                    File(qq_1).scan(result, false)
                    File(qq_2).scan(result, false)
                    File(qq_3).scan(result, false)
                    File(qq_4).scan(result, false)
                }
                FileFilterActivity.Type.WE_CHAT -> File(vx_1).scan(result, false)
                FileFilterActivity.Type.BAI_DU -> File(bd_1).scan(result, false)
            }
            result.sortedWith(FileNameCompare())
            return result
        }
    }
}