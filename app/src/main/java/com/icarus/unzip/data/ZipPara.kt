package com.icarus.unzip.data

import java.io.Serializable

data class ZipPara(
    var filePath: String,
    var fileName: String,
    var zipLevel: Int = 0,
    var lockType: Int = 0,
    var passWord: String = "",
    var delete: Boolean = false
) : Serializable
