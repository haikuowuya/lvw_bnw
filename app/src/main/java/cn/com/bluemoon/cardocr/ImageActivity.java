package cn.com.bluemoon.cardocr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.bluemoon.cardocr.lib.bean.TextOcrItem;
import cn.com.bluemoon.cardocr.lib.utils.GsonUtils;
import uk.co.senab.photoview.PhotoView;

public class ImageActivity extends BaseTitleActivity {

    private  static final  String EXTRA_PATH= "extra_path";


    public  static  void actionImage(Activity activity,String imageFilePath )
    {
        Intent intent = new Intent(activity, ImageActivity.class);
        intent.putExtra(EXTRA_PATH, imageFilePath);
        activity.startActivity(intent);
    }

    @BindView(R.id.pv_photo_view)
    PhotoView mPhotoView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String imageFilePath = getIntent().getStringExtra(EXTRA_PATH);
        if(!TextUtils.isEmpty(imageFilePath)) {
            mPhotoView.setImageBitmap(BitmapFactory.decodeFile(imageFilePath));
        }
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_image;
    }

    @Override
    public String pageTitle() {
        return "图片展示";
    }
}
