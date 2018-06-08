package cn.com.bluemoon.cardocr;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;

public class StorageUtils
{

    /***
     * 获取统一的缓存目录，如果有sd卡 :/mnt/sdcard/android/data/包名/cache/
     * 如果没有: /data/data/包名/cache/
     *
     * @param context :上下文对象
     * @return file
     */
    public static File getCacheDir(Context context)
    {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            return context.getExternalCacheDir();
        } else
        {
            return context.getCacheDir();
        }
    }

    /***
     * 获取崩溃日志文件目录
     *
     * @param context :上下文对象
     * @return file
     */
    public static File getCrashDir(Context context)
    {
        File crashDirFile = new File(getCacheDir(context).getAbsolutePath() + File.separator + "crash");
        if (!crashDirFile.exists())
        {
            crashDirFile.mkdirs();
        }
        return crashDirFile;
    }

    /**
     * 根据url的MD5来获取对应的bitmap(用于网站图标)
     *
     * @param context :上下文对象
     * @param url     url
     * @return bitmap
     */
    public static Bitmap getCacheBitmap(Context context, String url)
    {
        Bitmap bitmap = null;
        File cacheFile = new File(getCacheFilePath(context, url));
        if (cacheFile.exists())
        {
            bitmap = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
        }
        return bitmap;
    }

    /****
     * 获取缓存文件路径
     *
     * @param context :上下文对象
     * @param url     url
     * @return string
     */
    public static String getCacheFilePath(Context context, String url)
    {
        return null;//getCacheDir(context).getAbsolutePath() + MD5Utils.generate(url);
    }

    /***
     * @return 获取拍照目录
     */
    public static File getDcimDir()
    {
        File dcim = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM);
        if (!dcim.exists())
        {
            dcim.mkdirs();
        }
        return dcim;
    }

    /**
     * 从Uri中获取包含的文件路径， 需要判断是否4.4以上，主要用于onActivityResult中的处理
     * 文件路径：可以是来自Storage Access Framework Documents（4.4),也可以是基于ContentProviders 或者   MediaStore 中的_data
     *
     * @param context :上下文对象
     * @param uri     uri
     * @return string
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getFilePathFromUri(final Context context, final Uri uri)
    {
        boolean isKK = Build.VERSION.SDK_INT >= 19;
        if (isKK && DocumentsContract.isDocumentUri(context, uri))// 4.4以上才有的
        {
            if (isExternalStorageDocument(uri))
            {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type))
                {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri))
            {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri))
            {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type))
                {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type))
                {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type))
                {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme()))// MediaStore (and general)
        {
            return getDataColumn(context, uri, null, null);
        } else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme()))
        {// File
            return uri.getPath();
        }
        return uri.getPath();
    }

    /**
     * 从cursor中查询获取uri中包含的文件路径
     *
     * @param context       :上下文对象
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return string
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs)
    {
        Cursor cursor = null;
        final String column = MediaStore.MediaColumns.DATA;
        final String[] projection = {column};
        try
        {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst())
            {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 判断是否是SDcard存储文件
     *
     * @param uri : uri
     * @return boolean
     */
    public static boolean isExternalStorageDocument(Uri uri)
    {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * 判断Uri是否是下载文件
     *
     * @param uri :uri
     * @return boolean
     */
    public static boolean isDownloadsDocument(Uri uri)
    {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * 判断Uri是否是多媒体文件
     *
     * @param uri uri
     * @return boolean
     */
    public static boolean isMediaDocument(Uri uri)
    {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /***
     * 将图片保存到DCIM目录中
     *
     * @param context   ：context
     * @param bmp       :bmp
     * @param imageName :imagename
     * @return file
     */
    public static File saveImageToDCIM(Context context, Bitmap bmp, String imageName)
    {
        // 首先保存图片
        String fileName = imageName + ".png";
        File file = null;
        try
        {
            file = new File(getDcimDir(), fileName);
            if (file.exists())
            {
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            // 其次把文件插入到系统图库
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
            // 最后通知图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getPath())));
        } catch (Exception e)
        {
            e.printStackTrace();
            file = null;
        }
        return file;
    }
}


