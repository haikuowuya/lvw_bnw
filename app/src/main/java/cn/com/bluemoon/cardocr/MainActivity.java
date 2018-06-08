package cn.com.bluemoon.cardocr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;

import butterknife.OnClick;
import cn.com.bluemoon.cardocr.lib.common.YTServerAPI;

public class MainActivity extends BaseTitleActivity {

    @Override
    protected void afterOnCreate(@Nullable Bundle savedInstanceState) {
        super.afterOnCreate(savedInstanceState);
        hideTitle();

        PermissionUtils.OnApplyPermissionSuccessListener listener = new PermissionUtils.OnApplyPermissionSuccessListener() {
            @Override
            public void onSuccess(String[] permissions) {

            }
        };
        PermissionUtils.applyPermission(mActivity, PermissionUtils.PERMISSIONS_SD, listener);
        PermissionUtils.applyPermission(mActivity, PermissionUtils.PERMISSIONS_CAMERA, listener);
    }

    @OnClick(R.id.ll_photo)
    public void onPhotoClick() {
        PhotoUtils.selectPicFromSD(mActivity);
    }

    @OnClick(R.id.ll_camera)
    public void onCameraClick() {
        PhotoUtils.selectPicFromCamera(mActivity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("requestCode = " + requestCode + " resultCode = " + resultCode + " data = " + data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PhotoUtils.REQUEST_FROM_PHOTO || requestCode == PhotoUtils.REQUEST_FROM_CAMERA) {
                PhotoUtils.onActivityResult(this, requestCode, resultCode, data);
            } else if (requestCode == PhotoUtils.REQUEST_FROM_CROP) {
                String croppedImagePath = PhotoUtils.getFinalCropImagePath(this, data.getData());
                System.out.println("croppedImagePath = " + croppedImagePath);
                if (!TextUtils.isEmpty(croppedImagePath)) {
                    identification(croppedImagePath);
                }
            }
        }
    }

    private void identification(final String imageFilePath) {
        new Thread() {
            @Override
            public void run() {
                try {
                    byte[] bytes = FileUtils.FileToByte(imageFilePath);
                    String fileData = Base64.encodeToString(bytes, Base64.DEFAULT);
                    YTServerAPI serverAPI = new YTServerAPI(MainActivity.this);
                    serverAPI.setRequestListener(new YTServerAPI.OnRequestListener() {
                        @Override
                        public void onSuccess(int statusCode, final String responseBody) {
                            new Handler(getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    OcrResultActivity.actionOcrResult(MainActivity.this, imageFilePath, responseBody);
                                }
                            });
                        }

                        @Override
                        public void onFailure(int statusCode) {

                        }
                    });
                    serverAPI.textOcr(fileData);
                } catch (Exception e) {
                }
            }
        }.start();
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_main;
    }

    @Override
    public String pageTitle() {
        return "选择方式";
    }
}
