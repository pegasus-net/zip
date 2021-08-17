package com.icarus.unzip.ad;

import android.widget.TextView;


import com.icarus.unzip.R;

import a.icarus.component.BaseActivity;
import a.icarus.utils.Strings;
import a.icarus.utils.SystemServiceHelper;
import a.icarus.utils.WindowUtil;

public class AboutUsActivity extends BaseActivity {
    private TextView title;
    private TextView version;

    @Override
    protected int setLayout() {
        return R.layout.activity_set_about;
    }

    @Override
    protected void initView() {
        WindowUtil.setBlackStatusText(this);
        title = findViewById(R.id.title);
        version = findViewById(R.id.tv_app_version);
    }

    @Override
    protected void initData() {
        title.setText("关于我们");
        version.setText(Strings.format("版本:%s", SystemServiceHelper.getVersionName(this)));
    }

    @Override
    protected void initListener() {
        setBackView(R.id.back);
    }
}
