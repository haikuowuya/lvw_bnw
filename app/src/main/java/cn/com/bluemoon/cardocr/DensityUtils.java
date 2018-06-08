package cn.com.bluemoon.cardocr;

import android.content.Context;

/***
 * dp、sp和px之间的转化
 */
public class DensityUtils
{

    /***
     * 将像素转换为对应设备的density
     *
     * @param context context
     * @param pixels  pixels
     * @return int
     */
    public static int pixelsToDp(Context context, int pixels)
    {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((pixels - 0.5f) / scale);
    }

    /**
     * 获取手机设备的density
     *
     * @param context context
     * @return float
     */
    public static float getDensity(Context context)
    {
        return context.getResources().getDisplayMetrics().density;
    }

    /****
     * 将dp值转换为对应的像素值*
     *
     * @param context context
     * @param dp      dp
     * @return int
     */
    public static int dpToPx(Context context, float dp)
    {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /***
     * 将sp值转换为对应的像素值，主要用于TextView的字体中
     *
     * @param context context
     * @param sp      sp
     * @return int
     */
    public static int spToPx(Context context, float sp)
    {
        return (int) (sp * context.getResources().getDisplayMetrics().scaledDensity);
    }

    /***
     * 将像素值值转换为对应的sp值，主要用于TextView的字体中
     *
     * @param context context
     * @param pixels  pixels
     * @return int
     */
    public static int pxToSp(Context context, int pixels)
    {
        return (int) (pixels / context.getResources().getDisplayMetrics().scaledDensity);
    }

    /***
     * 获取手机屏幕的宽度
     *
     * @param context   context
     * @return int
     */
    public static int getScreenWidthInPx(Context context)
    {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /****
     * 获取手机屏幕的高度
     *
     * @param context  context
     * @return int
     */
    public static int getScreenHeightInPx(Context context)
    {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}
