package cn.com.bluemoon.cardocr;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;

import java.util.Arrays;
import java.util.List;
//                       _oo0oo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                      0\  =  /0
//                    ___/`---'\___
//                  .' \\|     |// '.
//                 / \\|||  :  |||// \
//                / _||||| -:- |||||- \
//               |   | \\\  -  /// |   |
//               | \_|  ''\---/''  |_/ |
//               \  .-\__  '-'  ___/-. /
//             ___'. .'  /--.--\  `. .'___
//          ."" '<  `.___\_<|>_/___.' >' "".
//         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
//         \  \ `_.   \_ __\ /__ _/   .-` /  /
//     =====`-.____`.___ \_____/___.-`___.-'=====
//                       `=---='
//
//
//     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//


/**
 * 权限申请的工具
 */
public class PermissionUtils {
    public static final String FIRST_CAMERA_HINT = "开启相机权限，轻松编辑图片";//"你已经拒绝过APP使用相应权限，请同意授权，否则将无法正常使用！";
    public static final String FIRST_SD_HINT = "开启存储权限，轻松缓存资料。";//"你已经拒绝过APP使用相应权限，请同意授权，否则将无法正常使用！";
    public static final String DENY_HINT = "开启相机权限，轻松缓存资料。权限二次拒绝后，将需要前往系统设置手动开启";//"你已经拒绝过APP使用相应权限，请同意授权，否则将无法正常使用！";
    public static final String AGAIN_DENY_HINT = "您拒绝了我们必要的一些权限，这样会导致相关功能无法正常使用，请在设置中授权！";

    /****
     * 请求申请需要的权限
     */
    public static final int REQUEST_NEED_PERMISSION_CODE = 234;
    /****
     * 请求申请需要的权限最终失败后，跳转到app详情界面去手动开启权限
     */
    public static final int REQUEST_SETTINGS_OPT_PERMISSION_CODE = 235;

    public static final String[] PERMISSIONS_CAMERA = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static final String[] PERMISSIONS_SD = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private static Rationale mRationale;

    public static void applyPermission(final Activity activity, String[] permissions, OnApplyPermissionSuccessListener listener) {
        AndPermission.with(activity).permission(permissions).rationale((requestCode, rationale) -> {
            mRationale = rationale;
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
            dialogBuilder.setTitle("提醒");
            dialogBuilder.setCancelable(false);
            if (PERMISSIONS_SD.length == permissions.length) {
                dialogBuilder.setMessage(FIRST_SD_HINT);
            } else {
                dialogBuilder.setMessage(FIRST_CAMERA_HINT);
            }
            dialogBuilder.setPositiveButton("开启", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    rationale.resume();
                }
            });
            dialogBuilder.setPositiveButton("拒绝", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    rationale.resume();
                }
            });
            dialogBuilder.create().show();
        }).requestCode(REQUEST_NEED_PERMISSION_CODE).callback(new PermissionListener() {
            @Override
            public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                if (requestCode == REQUEST_NEED_PERMISSION_CODE || requestCode == REQUEST_SETTINGS_OPT_PERMISSION_CODE) {
                    if (listener != null) {
                        listener.onSuccess(permissions);
                    }
                }
            }

            @Override
            public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                System.out.println("权限申请失败　requestCode = " + requestCode + " 被拒绝的权限　= " + deniedPermissions.toString());
                if (requestCode == REQUEST_NEED_PERMISSION_CODE) {
                    showApplyPermissionFailedDialog(activity, mRationale);
                } else if (requestCode == REQUEST_SETTINGS_OPT_PERMISSION_CODE) {

                }
            }
        }).start();
    }


    /****
     * 显示权限被拒绝后的提示对话框
     */
    private static final void showApplyPermissionFailedDialog(Activity activity, final Rationale rationale) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setTitle("提醒");
        dialogBuilder.setMessage(FIRST_SD_HINT);
        dialogBuilder.setPositiveButton("开启", (dialog, which) -> {
            // 是否有不再提示并拒绝的权限。
            if (AndPermission.hasAlwaysDeniedPermission(activity, Arrays.asList(PermissionUtils.PERMISSIONS_CAMERA))) {
                showApplyPermissionAgainFailedDialog(activity);
            } else {
                if (null != rationale) {
                    rationale.resume();
                }
            }
        });
        dialogBuilder.setPositiveButton("拒绝", (dialog, which) -> {
        });
        dialogBuilder.create().show();
    }


    /****
     * 再次申请权限失败或，必须让用户手动开启权限
     */
    private static void showApplyPermissionAgainFailedDialog(Activity activity) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setTitle("提醒");
//        dialogBuilder.withDialogColor(0xFFFFFFFF);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setMessage(AGAIN_DENY_HINT);
        dialogBuilder.setPositiveButton("去设置", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            activity.startActivityForResult(intent, PermissionUtils.REQUEST_SETTINGS_OPT_PERMISSION_CODE);
        });
        dialogBuilder.create().show();
    }


    public interface OnApplyPermissionSuccessListener {
        void onSuccess(String[] permissions);

    }
}
