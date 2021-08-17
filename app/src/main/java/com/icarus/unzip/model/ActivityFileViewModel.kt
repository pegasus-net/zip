package com.icarus.unzip.model

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt

class ActivityFileViewModel {
    var title = ObservableField<String>()
    var unzip = ObservableField<String>()
    var operationAllow = ObservableBoolean()
}