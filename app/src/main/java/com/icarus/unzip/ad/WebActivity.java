package com.icarus.unzip.ad;

import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;


import com.icarus.unzip.R;

import a.icarus.component.BaseActivity;
import a.icarus.utils.WindowUtil;


public class WebActivity extends BaseActivity {

    public static final String TITLE = "title";
    public static final String URL = "url";
    private TextView titleView, subtitleView;
    private WebView webView;
    private ImageView back;


    @Override
    protected int setLayout() {
        return R.layout.activity_set_web;
    }

    @Override
    protected void initView() {
        WindowUtil.setBlackStatusText(this);
        webView = findViewById(R.id.web_view);
        titleView = findViewById(R.id.title);
        subtitleView = findViewById(R.id.subTitle);
        back = findViewById(R.id.back);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        String title = intent.getStringExtra(TITLE);
        String url = intent.getStringExtra(URL);
        titleView.setText(title);
        subtitleView.setText(String.format("%s(以下称'本应用')", getResources().getString(R.string.app_name)));
        webView.loadUrl(url);
    }

    @Override
    protected void initListener() {
        back.setOnClickListener(v -> onBackPressed());
    }


    public static void openUrl(Context context, String title, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(TITLE, title);
        intent.putExtra(URL, url);
        context.startActivity(intent);
    }
}
