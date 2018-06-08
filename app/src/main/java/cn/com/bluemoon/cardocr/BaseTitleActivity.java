package cn.com.bluemoon.cardocr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 文件名：BaseTitleActivity
 * 描述：带有标题的基类activity
 * 创建者：leo
 * 邮箱： leo@shutadata.com
 * 时间：17-11-28 下午3:15
 * 版本：V0.0.1
 */

public abstract class BaseTitleActivity extends BaseActivity {
    /****
     * 继承的子类提供的页面布局文件
     * @return:布局文件的res id
     */

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.ll_title)
    ViewGroup mLlTitle;

    @Override
    protected void afterOnCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_base_title);
        mTvTitle.setText(pageTitle());
    }
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        FrameLayout mFrameLayout = findViewById(R.id.frame_container);
        LayoutInflater.from(mActivity).inflate(getContentViewResId(), mFrameLayout, true);
        ButterKnife.bind(mActivity);
    }
    @OnClick(R.id.iv_back)
    public  void onBackClick()
    {
        onBackPressed();
    }


    public  void hideTitle()
    {
        mLlTitle.setVisibility(View.GONE);
    }
    public abstract int getContentViewResId();
    public  abstract  String pageTitle();
}
