package cn.com.bluemoon.cardocr;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;

/**
 * 图片选择工具类
 */
public class PhotoUtils {
    /**
     * 最大上传图片的宽度和高度
     */
    public static final int W_H = 200;
    /***
     * 从相册文件中选取图片时的请求码
     */
    public static final int REQUEST_FROM_PHOTO = 1111;
    /***
     * 从拍照中选取图片时的请求码
     */
    public static final int REQUEST_FROM_CAMERA = 2222;

    /***
     * 将上一步拍照/文件进行剪取
     */
    public static final int REQUEST_FROM_CROP = 3333;

    /***
     * 上传头像
     */

    private static String sFinalCameraImagePath = null;

    /***
     * 剪切后的图片路径
     */
    private static String sFinalCroppedImagePath = null;

    /***
     * 将  sFinalCameraImagePath、sFinalCroppedImagePath置为null
     */
    private static void reset() {
        sFinalCameraImagePath = null;
        sFinalCroppedImagePath = null;
    }


    /**
     * 从相册中选择图片
     *
     * @param activity activity
     */
    public static void selectPicFromSD(Activity activity) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        Intent localIntent2 = Intent.createChooser(intent, "选择图片");
        activity.startActivityForResult(localIntent2, PhotoUtils.REQUEST_FROM_PHOTO);
    }

    private static void cropSelectedPic(Activity activity, String cropImagePath) {
        Intent intent = new Intent(activity, CropActivity.class);
        intent.putExtra(CropActivity.EXTRA_CROP_PATH, cropImagePath);
        activity.startActivityForResult(intent, REQUEST_FROM_CROP);
    }

    /***
     * 拍照选择图片
     *
     * @param activity activity
     * @return 保存的拍照图片路径
     */
    public static String selectPicFromCamera(Activity activity) {
        String imageFilePath = null;
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(activity, "SD卡不可用", Toast.LENGTH_SHORT).show();
        }
        try {
//            System.out.println("activity.getExternalCacheDir() = " + activity.getExternalCacheDir());
            File imageFile = new File(StorageUtils.getCacheDir(activity), System.currentTimeMillis() + ".png");  //通过路径创建保存文件
            if (imageFile.exists()) {
                imageFile.delete();
            }
            imageFilePath = imageFile.getPath();
            Uri photoUri = Uri.fromFile(imageFile);                                    //获取文件的Uri
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);            //跳转到相机Activity
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);    //告诉相机拍摄完毕输出图片到指定的Uri
            activity.startActivityForResult(intent, PhotoUtils.REQUEST_FROM_CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sFinalCameraImagePath = imageFilePath;
        return imageFilePath;
    }

    /***
     * 获取拍照选择的图片路径
     *
     * @return String
     */
    private static String getFinalCameraImagePath() {
        return sFinalCameraImagePath;
    }

    /***
     * 获取剪切后的图片路径
     *
     * @return String
     */
    public static String getFinalCroppedImagePath() {
        return sFinalCroppedImagePath;
    }

    /***
     * 获取从相册选择的图片path
     *
     * @param activity activity
     * @param uri      uri
     * @return String
     */
    private static String getFinalPhotoImagePath(Activity activity, Uri uri) {
        if (null != activity && null != uri) {
            return StorageUtils.getFilePathFromUri(activity, uri);
        }
        return null;
    }

    public static String getFinalCropImagePath(Activity activity, Uri uri) {
        if (null != activity && null != uri) {
            return StorageUtils.getFilePathFromUri(activity, uri);
        }
        return null;
    }

    public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String imagePath = null;
            if (PhotoUtils.REQUEST_FROM_CROP == requestCode) {
                sFinalCroppedImagePath = PhotoUtils.getFinalCropImagePath(activity, data.getData());
                System.out.println("sFinalCroppedImagePath = " + sFinalCroppedImagePath + " size = " + FileUtils.getFileSize(sFinalCroppedImagePath));
            } else {
                if (PhotoUtils.REQUEST_FROM_PHOTO == requestCode) {
                    if (null != data && null != data.getData()) {
                        imagePath = PhotoUtils.getFinalPhotoImagePath(activity, data.getData());
                    }
                } else if (PhotoUtils.REQUEST_FROM_CAMERA == requestCode) {
                    imagePath = PhotoUtils.getFinalCameraImagePath();
                }
                if (!TextUtils.isEmpty(imagePath)) {
                    try {
                        System.out.println("选取的图片 = " + imagePath + " size = " + FileUtils.getFileSize(imagePath));
                        String compressPath = BitmapUtils.getCompressBitmapFilePath(activity, imagePath);
                        System.out.println("第一次压缩后的图片路径 = " + compressPath + " size = " + FileUtils.getFileSize(compressPath));
                        PhotoUtils.cropSelectedPic(activity, compressPath);
                    } catch (Exception e) {
                    }

                }
            }
        }
    }

}
