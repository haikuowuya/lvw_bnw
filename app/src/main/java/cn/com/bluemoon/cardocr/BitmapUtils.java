package cn.com.bluemoon.cardocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Bitmap 工具类
 */
public class BitmapUtils
{
    /***
     * 将一个图片文件压缩到指定的大小(不大于指定的宽度和高度)并保存，
     * 宽度为屏幕的宽度、高度为屏幕的高度
     *
     * @param context      上下文
     * @param originalPath 原始图片路径
     * @return 压缩后的图片路径
     */
    public static String getCompressBitmapFilePath(Context context, String originalPath)
    {
        String compressBitmapFielPath = null;
        Bitmap bitmap = null;
        if (null != originalPath)
        {
            try
            {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                InputStream input = new FileInputStream(originalPath);
                BitmapFactory.decodeStream(input, null, options);
                int sourceWidth = options.outWidth;
                int sourceHeight = options.outHeight;
                //   System.out.println("sourceWidth =" + sourceWidth + " sourceHeight = " + sourceHeight);
                input.close();
                float rate = Math.max(sourceWidth / (float) DensityUtils.getScreenWidthInPx(context), sourceHeight / (float) DensityUtils.getScreenHeightInPx(context));
                options.inJustDecodeBounds = false;
                options.inSampleSize = (int) rate;
                input = new FileInputStream(originalPath);
                bitmap = BitmapFactory.decodeStream(input, null, options);
                compressBitmapFielPath = genCompressBitmapFilePath(context, bitmap, originalPath);
            } catch (Exception e)
            {

            }
        }
        return compressBitmapFielPath;
    }

    /***
     * 获取压缩后的bitmap保存的文件路径
     *
     * @param context      上下文
     * @param bitmap       bitmap
     * @param originalPath 原始图片路径
     * @return String
     */
    private static String genCompressBitmapFilePath(Context context, Bitmap bitmap, String originalPath)
    {
        String compressBitmapFielPath = null;
        if (null != bitmap)
        {
            try
            {
                File file = new File(StorageUtils.getCacheDir(context), MD5Utils.getMD5(originalPath));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(file));
                compressBitmapFielPath = file.getAbsolutePath();
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        //System.out.println("新的处理之后的图片路径 = " + compressBitmapFielPath);
        return compressBitmapFielPath;
    }

    /****
     * 将bitmap保存到文件中
     *
     * @param bitmap  bitmap
     * @param file    file
     * @param quality quality
     * @throws IOException IOException
     */
    public static void writeBitmapToFile(Bitmap bitmap, File file, int quality) throws IOException
    {

        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
        fos.flush();
        fos.close();
    }

    /**
     * 按原比例缩放图片
     *
     * @param path      图片的URI地址
     * @param maxWidth  缩放后的宽度
     * @param maxHeight 缩放后的高度
     * @return Bitmap
     */
    public static Bitmap scaleBitmap(String path, int maxWidth, int maxHeight)
    {
        Bitmap resizedBitmap = null;
        try
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream input = new FileInputStream(path);
            BitmapFactory.decodeStream(input, null, options);
            int sourceWidth = options.outWidth;
            int sourceHeight = options.outHeight;
            input.close();
            float rate = Math.max(sourceWidth / (float) maxWidth, sourceHeight / (float) maxHeight);
            options.inJustDecodeBounds = false;
            options.inSampleSize = (int) rate;
            input = new FileInputStream(path);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
            int w0 = bitmap.getWidth();
            int h0 = bitmap.getHeight();
            float scaleWidth = maxWidth / (float) w0;
            float scaleHeight = maxHeight / (float) h0;
            float maxScale = Math.min(scaleWidth, scaleHeight);
            Matrix matrix = new Matrix();
            matrix.reset();
            if (maxScale < 1)
            {
                matrix.postScale(maxScale, maxScale);
            }
            resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, w0, h0, matrix, true);
            input.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return resizedBitmap;
    }

    /***
     * 缩放bitmap
     *
     * @param path 图片路径
     * @return Bitmap
     */
    public static Bitmap scaleBitmap(String path)
    {
        Bitmap resizedBitmap = null;
        try
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream input = new FileInputStream(path);
            BitmapFactory.decodeStream(input, null, options);
            int sourceWidth = options.outWidth;
            int sourceHeight = options.outHeight;
            input.close();
            float rate = Math.max(sourceWidth / (float) PhotoUtils.W_H, sourceHeight / (float) PhotoUtils.W_H);
            options.inJustDecodeBounds = false;
            options.inSampleSize = (int) rate;
            input = new FileInputStream(path);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
            int w0 = bitmap.getWidth();
            int h0 = bitmap.getHeight();
            float scaleWidth = PhotoUtils.W_H / (float) w0;
            float scaleHeight = PhotoUtils.W_H / (float) h0;
            float maxScale = Math.min(scaleWidth, scaleHeight);
            Matrix matrix = new Matrix();
            matrix.reset();
            if (maxScale < 1)
            {
                matrix.postScale(maxScale, maxScale);
            }
            resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, w0, h0, matrix, true);
            input.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return resizedBitmap;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    private static int readPictureDegree(String path)
    {
        int degree = 0;
        try
        {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation)
            {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return degree;
    }

    /****
     * 将图片旋转后返回bitmap ,用于选择图片时图片旋转90度的问题
     *
     * @param imagePath imagePath
     * @param bitmap    bitmap
     * @return bitmap
     */
    public static Bitmap rotatePictureBitmap(String imagePath, Bitmap bitmap)
    {
//        //旋转图片 动作
//        Matrix matrix = new Matrix();
//        matrix.postRotate(readPictureDegree(imagePath));
//        // 创建新的图片
//        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//        return resizedBitmap;
        int angle = readPictureDegree(imagePath);
        return rotateBitmap(bitmap, angle);
    }

    /***
     * 将图片旋转angle
     *
     * @param bitmap bitmap
     * @param angle  angle
     * @return bitmap
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


}
