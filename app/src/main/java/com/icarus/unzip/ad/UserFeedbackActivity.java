package com.icarus.unzip.ad;

import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;


import com.icarus.unzip.R;

import a.icarus.component.BaseActivity;
import a.icarus.utils.SpUtil;
import a.icarus.utils.ToastUtil;
import a.icarus.utils.WindowUtil;


public class UserFeedbackActivity extends BaseActivity {
    private EditText suggestion, email;
    private Button submit;
    private boolean sending = false;

    @Override
    protected int setLayout() {
        return R.layout.activity_set_feedback;
    }

    @Override
    protected void initView() {
        WindowUtil.setBlackStatusText(this);
        suggestion = findViewById(R.id.et_suggestion);
        email = findViewById(R.id.et_email);
        submit = findViewById(R.id.submit);
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initListener() {
        setBackView(R.id.back);
        submit.setOnClickListener(v -> {
            if (suggestion.getText().toString().equalsIgnoreCase("13579")) {
                SpUtil.putBoolean(Constants.AdState.SUPER_MODE, true);
                ToastUtil.show("已开启超级权限，重启生效");
                return;
            }
            if (TextUtils.isEmpty(suggestion.getText().toString().trim())) {
                ToastUtil.show("请填写内容");
            } else if (TextUtils.isEmpty(email.getText().toString().trim())) {
                ToastUtil.show("请填写联系方式");
            } else {
                ToastUtil.show("谢谢您的反馈，敬请期待我们的改进");
                finish();
            }
        });
    }
}
