package cn.com.bluemoon.cardocr;

import java.security.MessageDigest;

public class MD5Utils
{

    /***
     *     MD5加密，32位
     * @param str        str
     * @return       str
     */
    public static String EncoderByMd5(String str)
    {
        MessageDigest md5 = null;
        try
        {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }

        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
        {
            byteArray[i] = (byte) charArray[i];
        }

        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++)
        {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
            {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    private static final String MD5_CHARSET = "UTF-8";

    /***
     *
     * @param sStr     str
     * @return    string
     */
    public static String getMD5(String sStr)
    {
        String sReturnCode = "";
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sStr.getBytes(MD5_CHARSET));
            byte b[] = md.digest();
            int i;
            StringBuffer sb = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++)
            {
                i = b[offset];
                if (i < 0)
                {
                    i += 256;
                }
                if (i < 16)
                {
                    sb.append("0");
                }
                sb.append(Integer.toHexString(i));
            }
            sReturnCode = sb.toString();
        } catch (Exception ex)
        {

        }
        return sReturnCode;
    }
}
