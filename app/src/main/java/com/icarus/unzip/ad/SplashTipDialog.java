package com.icarus.unzip.ad;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.icarus.unzip.R;

import a.icarus.component.BaseDialog;
import a.icarus.utils.ToastUtil;


public class SplashTipDialog extends BaseDialog {

    public SplashTipDialog(@NonNull Context context) {
        super(context, R.layout.dialog_splash_tip);
        init();
    }

    public enum BtnClickIndex {
        INDEX_YHXY,
        INDEX_YSZC,
        INDEX_AGREE,
        INDEX_DISAGREE
    }

    protected void init() {
        initViews();
        initListeners();
    }

    private void initListeners() {
        findViewById(R.id.btn_yhxy).setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.OnClick(v, BtnClickIndex.INDEX_YHXY);
            }
        });

        findViewById(R.id.btn_yszc).setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.OnClick(v, BtnClickIndex.INDEX_YSZC);
            }
        });

        findViewById(R.id.agreeBtn).setOnClickListener(v -> {
            dismiss();
            if (clickListener != null) {
                clickListener.OnClick(v, BtnClickIndex.INDEX_AGREE);
            }
        });

        findViewById(R.id.unAgreeBtn).setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.OnClick(v, BtnClickIndex.INDEX_DISAGREE);
            }
            ToastUtil.show("您需要同意《用户协议》及《隐私政策》才能使用本应用。");
        });

    }

    private void initViews() {
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_splash_tip, null);
        setContentView(inflate);
        setCancelable(false);
        TextView contentTxt = findViewById(R.id.tipContentTxt);
        contentTxt.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    DialogBtnClickListener clickListener;

    public SplashTipDialog setDialogBtnClickListener(DialogBtnClickListener clickListener) {
        this.clickListener = clickListener;
        return this;
    }

    public interface DialogBtnClickListener {
        void OnClick(View view, BtnClickIndex clickIndex);
    }

}
