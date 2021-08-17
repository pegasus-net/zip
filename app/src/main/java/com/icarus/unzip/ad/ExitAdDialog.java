package com.icarus.unzip.ad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;


import com.icarus.unzip.R;

import a.icarus.utils.Strings;
import a.icarus.utils.SystemServiceHelper;

public class ExitAdDialog extends Dialog {

    Activity mActivity;

    final int padding = 60;

    public ExitAdDialog(@NonNull Context context) {
        this(context, 0);
    }

    public ExitAdDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mActivity = (Activity) context;
        init();
    }


    private void init() {
        @SuppressLint("InflateParams")
        View contentView = getLayoutInflater().inflate(R.layout.dialog_exit, null);
        setContentView(contentView);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));

        int windowWidth = (int) (getContext().getResources().getDisplayMetrics().widthPixels - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding, getContext().getResources().getDisplayMetrics()));
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = windowWidth;
        window.setAttributes(params);

        CardView realContentViewLayout = contentView.findViewById(R.id.realContentViewLayout);
        FrameLayout.LayoutParams ps = (FrameLayout.LayoutParams) realContentViewLayout.getLayoutParams();
        ps.width = windowWidth;
        realContentViewLayout.setLayoutParams(ps);


        ((TextView) findViewById(R.id.appVersionCode)).setText(Strings.format("版本:%s", SystemServiceHelper.getVersionName(getContext())));
        findViewById(R.id.sureBtn).setOnClickListener(v -> {
            dismiss();
            if (exitListener != null) {
                exitListener.onExit();
            }
        });
        findViewById(R.id.cancelBtn).setOnClickListener(v -> dismiss());
    }

    private ExitListener exitListener;

    public ExitAdDialog setExitListener(ExitListener exitListener) {
        this.exitListener = exitListener;
        return this;
    }


    public interface ExitListener {
        void onExit();
    }
}
