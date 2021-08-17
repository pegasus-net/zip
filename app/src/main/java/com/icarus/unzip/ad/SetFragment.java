package com.icarus.unzip.ad;

import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.icarus.unzip.R;
import com.icarus.unzip.ad.AboutUsActivity;
import com.icarus.unzip.ad.UserFeedbackActivity;
import com.icarus.unzip.ad.WebActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import a.icarus.component.BaseFragment;
import a.icarus.utils.FileUtil;
import a.icarus.utils.ToastUtil;

public class SetFragment extends BaseFragment {
    private ConstraintLayout item_0, item_1, item_2, item_3, item_4, item_5;
    private TextView cache;
    private File cacheDir, externalCacheDir;

    @Override
    protected int setLayout() {
        return R.layout.fragment_set;
    }

    @Override
    protected void initView(@NotNull View view) {
        cache = findViewById(R.id.info_0);
        item_0 = findViewById(R.id.set_item_0);
        item_1 = findViewById(R.id.set_item_1);
        item_2 = findViewById(R.id.set_item_2);
        item_3 = findViewById(R.id.set_item_3);
        item_4 = findViewById(R.id.set_item_4);
        item_5 = findViewById(R.id.set_item_5);
    }

    @Override
    protected void initData() {
        item_0.setOnClickListener(v -> itemClick(0));
        item_1.setOnClickListener(v -> itemClick(1));
        item_2.setOnClickListener(v -> itemClick(2));
        item_3.setOnClickListener(v -> itemClick(3));
        item_4.setOnClickListener(v -> itemClick(4));
        item_5.setOnClickListener(v -> itemClick(5));
        // AdLoader.nativeAD(mActivity,findViewById(R.id.ad_container));
    }

    @Override
    protected void initListener() {

    }

    private void itemClick(int position) {
        Intent intent;
        switch (position) {
            case 0:
                FileUtil.delete(cacheDir);
                FileUtil.delete(externalCacheDir);
                cache.setText(FileUtil.formatBytes(0));
                ToastUtil.show("缓存已清理");
                break;
            case 1:
                ToastUtil.show("已是最新版本");
                break;
            case 2:
                intent = new Intent(mContext, AboutUsActivity.class);
                startActivity(intent);
                break;
            case 3:
                WebActivity.openUrl(mContext, "隐私政策", "file:///android_asset/www/yszc.html");
                break;
            case 4:
                WebActivity.openUrl(mContext, "用户协议", "file:///android_asset/www/yhxy.html");
                break;
            case 5:
                intent = new Intent(mContext, UserFeedbackActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cacheDir = mContext.getCacheDir();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            externalCacheDir = mContext.getExternalCacheDir();
        }
        long size = FileUtil.getSize(cacheDir) + FileUtil.getSize(externalCacheDir);
        cache.setText(FileUtil.formatBytes(size));
    }
}
