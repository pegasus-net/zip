package com.icarus.unzip.data

import java.io.Serializable

data class UnzipPara(
    var file: String,
    var targetPath: String,
    var passWord: String = "",
    var delete: Boolean = false
) : Serializable
