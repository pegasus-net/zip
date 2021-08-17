package com.icarus.unzip.coustomView

import a.icarus.component.BaseDialog
import android.content.Context
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.icarus.unzip.R
import com.icarus.unzip.data.Event
import com.icarus.unzip.util.show
import dagger.hilt.android.qualifiers.ActivityContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class ProgressDialog @Inject constructor(@ActivityContext context: Context) :
    BaseDialog(context, R.layout.dialog_progress), LifecycleObserver {

    private var progressView: Progress = findViewById(R.id.progress)
    var progressText: TextView = findViewById(R.id.progress_text)
    var name: TextView = findViewById(R.id.woke_name)
    var cancelButton: Button = findViewById(R.id.cancel)

    init {
        setCancelable(false)
        cancelButton.setOnClickListener {
            EventBus.getDefault().post(Event.FileTaskCancel)
            dismiss()
        }
    }

    private var progress: Int = 0
        set(value) {
            field = value
            progressView.progress = progress
            val s = "${value / 10F}%"
            progressText.text = s
        }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        EventBus.getDefault().register(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(event: Event) {
        when (event) {
            is Event.FileTaskProgress -> {
                progress = event.progress
            }
            Event.FileCopyStart -> {
                show("复制")
            }
            Event.FileZipStart -> {
                show("压缩")
            }
            Event.FileUnzipStart -> {
                show("解压")
            }
            Event.FileTaskFinish -> {
                dismiss()
            }
            Event.FileTaskFailed -> {
                "任务失败".show()
            }
            else -> Unit
        }
    }

    private fun show(name: String) {
        val s = "${name}中..."
        this.name.text = s
        progress = 0
        super.show()
    }
}