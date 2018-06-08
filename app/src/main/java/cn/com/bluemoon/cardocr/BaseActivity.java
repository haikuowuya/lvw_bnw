package cn.com.bluemoon.cardocr;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import butterknife.ButterKnife;

/**
 * 文件名：BaseActivity
 * 描述：activity的基类
 * 创建者：leo
 * 邮箱： leo@shutadata.com
 * 时间：17-6-26 下午8:39
 * 版本：V1.0
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected BaseActivity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mActivity = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        afterOnCreate(savedInstanceState);
    }



    /*****
     * Activity的onCreate方法执行后可执行的方法，用于做一些初始化的操作
     * @param savedInstanceState
     */
    protected abstract void afterOnCreate(@Nullable Bundle savedInstanceState);

    /****
     * 隐藏软键盘
     */
    public void hideKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        boolean active = inputMethodManager.isActive();
        View currentFocusView = getCurrentFocus();
        if (null != currentFocusView) {
            if (active) {
                inputMethodManager.hideSoftInputFromWindow(currentFocusView.getWindowToken(), 0);
            }
        }
    }
}
