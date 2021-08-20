package com.icarus.unzip.util

import a.icarus.utils.SpUtil

class Config {
    companion object {
        var isShowHideFile: Boolean
            set(value) {
                SpUtil.putBoolean("show_hide_file", value)
            }
            get() {
                return SpUtil.getBoolean("show_hide_file")
            }
    }
}