package cn.com.bluemoon.cardocr.lib.bean;

import java.io.Serializable;
import java.util.List;

public class TextOcrItem implements Serializable {
    public static final String OK = "OK";
    public int errorcode;
    public String errormsg;
    public List<OcrItem> items;

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (null != items && !items.isEmpty()) {
            int y = items.get(0).itemcoord.y;
            for (OcrItem ocrItem : items) {
                if (Math.abs(y - ocrItem.itemcoord.y) > 10) {
                    stringBuilder.append("\n");
                    stringBuilder.append(ocrItem.itemstring);
                    y = ocrItem.itemcoord.y;
                } else {
                    stringBuilder.append("\t");
                    stringBuilder.append(ocrItem.itemstring);
                }
            }
        }


        return stringBuilder.toString();

    }
}
