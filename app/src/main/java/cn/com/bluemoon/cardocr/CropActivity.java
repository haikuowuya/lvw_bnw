package cn.com.bluemoon.cardocr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.io.File;

public class CropActivity extends Activity {
    public static final String EXTRA_CROP_PATH = "crop_path";
    private CropImageView mCropView;
    private ImageView mIvCrop;
    private ImageView mIvMenu;
    private String mCropImagePath;

    private int mCurrentMode = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_crop);
        mCropView = (CropImageView) findViewById(R.id.crop_imageview);
        mCropView.setCropMode(CropImageView.CropMode.RATIO_FREE);
        mIvCrop = (ImageView) findViewById(R.id.iv_crop);
        mIvMenu = (ImageView) findViewById(R.id.iv_menu);
        mCropImagePath = getIntent().getStringExtra(EXTRA_CROP_PATH);
        if (!TextUtils.isEmpty(mCropImagePath)) {
            mCropView.setImageBitmap(BitmapFactory.decodeFile(mCropImagePath));
        }
        mIvCrop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = null;
                try {
                    Bitmap bitmap = mCropView.getCroppedBitmap();
                    File file = new File(StorageUtils.getCacheDir(CropActivity.this), MD5Utils.getMD5(mCropImagePath));
                    BitmapUtils.writeBitmapToFile(bitmap, file, 50);
                    intent = new Intent();
                    intent.setData(Uri.fromFile(file));
                } catch (Exception e) {

                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        mIvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListPopupWindow();
            }
        });
    }

    private void showListPopupWindow() {
        final PopupWindow popupWindow = new PopupWindow(this);
        // 设置SelectPicPopupWindow弹出窗体的宽
        int width = DensityUtils.dpToPx(this, 128.f);
//        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(width);
        // 设置SelectPicPopupWindow弹出窗体的高
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        popupWindow.setFocusable(true);
        //设置点击窗口外边窗口消失
        popupWindow.setOutsideTouchable(false);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        //  popupWindow.setAnimationStyle(R.style.AnimBottom);
        // 设置SelectPicPopupWindow弹出窗体的背景
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
        final ListView listView = new ListView(this);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(genListAdapter());
        popupWindow.setContentView(listView);
        popupWindow.showAsDropDown(mIvMenu);
        listView.setItemChecked(mCurrentMode, true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mCropView.setCropMode(CropImageView.CropMode.RATIO_FIT_IMAGE);
                        break;
                    case 1:
                        mCropView.setCropMode(CropImageView.CropMode.RATIO_1_1);
                        break;
                    case 2:
                        mCropView.setCropMode(CropImageView.CropMode.RATIO_3_4);
                        break;
                    case 3:
                        mCropView.setCropMode(CropImageView.CropMode.RATIO_4_3);
                        break;
                    case 4:
                        mCropView.setCropMode(CropImageView.CropMode.RATIO_9_16);
                        break;
                    case 5:
                        mCropView.setCropMode(CropImageView.CropMode.RATIO_16_9);
                        break;
                    case 6:
                        mCropView.setCropMode(CropImageView.CropMode.RATIO_FREE);
                        break;
                    case 7:
                        mCropView.setCropMode(CropImageView.CropMode.CIRCLE);
                        break;
                    case 8:
                        mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                        break;
                }
                mCurrentMode = position;
                listView.setItemChecked(mCurrentMode, true);
                popupWindow.dismiss();
            }
        });
    }

    private ListAdapter genListAdapter() {
        String[] texts = {"FIT_IMAGE", "RATIO_1_1", "RATIO_3_4", "RATIO_4_3", "RATIO_9_16", "RATIO_16_9", "RATIO_FREE", "CIRCLE", "Rotate"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_crop_menu_item, texts);
        return arrayAdapter;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_CROP_PATH, mCropImagePath);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCropImagePath = savedInstanceState.getString(EXTRA_CROP_PATH);
    }

}
