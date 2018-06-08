package cn.com.bluemoon.cardocr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.bluemoon.cardocr.lib.bean.OcrItem;
import cn.com.bluemoon.cardocr.lib.bean.TextOcrItem;
import cn.com.bluemoon.cardocr.lib.utils.GsonUtils;

public class OcrResultActivity extends BaseTitleActivity {
    private static final String URL = "http://47.93.248.253/bainiuwang/bainiuwang?name=%s&mobile=%s&address=%s&orderno=%s&platform=%s";

    private static final String SAME_LINE_FLAG = ")";
    private static final String EXTRA_PATH = "extra_path";
    private static final String EXTRA_JSON = "extra_json";

    public static void actionOcrResult(Activity activity, String imageFilePath, String ocrResultJson) {
        Intent intent = new Intent(activity, OcrResultActivity.class);
        intent.putExtra(EXTRA_PATH, imageFilePath);
        intent.putExtra(EXTRA_JSON, ocrResultJson);
        activity.startActivity(intent);
    }

    @BindView(R.id.et_address)
    EditText mEtAddress;
    @BindView(R.id.et_name)
    EditText mEtName;
    @BindView(R.id.et_phone)
    EditText mEtPhone;
    @BindView(R.id.et_orderno)
    EditText mEtOrderNo;
    @BindView(R.id.radio_group)
    RadioGroup mRadioGroup;
    TextOcrItem textOcrItem = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String json = getIntent().getStringExtra(EXTRA_JSON);
        textOcrItem = GsonUtils.jsonToClass(json, TextOcrItem.class);
        if (null != textOcrItem) {
            handleTextOcr();
            if (textOcrItem.items != null && textOcrItem.items.size() > 2) {
                mEtAddress.setText(textOcrItem.items.get(0).itemstring);
                mEtAddress.setSelection(mEtAddress.getText().length());
                mEtName.setText(textOcrItem.items.get(1).itemstring);
                mEtName.setSelection(mEtName.getText().length());
                mEtPhone.setText(textOcrItem.items.get(2).itemstring);
                mEtPhone.setSelection(mEtPhone.getText().length());
            }
        }
    }

    @OnClick(R.id.tv_image)
    public void onImageClick() {
        ImageActivity.actionImage(this, getIntent().getStringExtra(EXTRA_PATH));
    }

    @OnClick(R.id.tv_save)
    public void onSaveClick() {
        new Thread() {
            @Override
            public void run() {
                doSaveInfo();
            }
        }.start();
    }

    private void doSaveInfo() {
        try {
            String name = URLEncoder.encode(mEtName.getText().toString(), "UTF-8");
            String mobile = URLEncoder.encode(mEtPhone.getText().toString(), "UTF-8");
            String address = URLEncoder.encode(mEtAddress.getText().toString(), "UTF-8");
            String orderNo = URLEncoder.encode(mEtOrderNo.getText().toString(), "UTF-8");
            if(TextUtils.isEmpty(orderNo))
            {
                orderNo = UUID.randomUUID().toString().replace("-", "");
                orderNo.substring(0, 10);
            }
            String platform = mRadioGroup.getCheckedRadioButtonId()==R.id.rb_mt?"1":"2";
            String formatUrl = String.format(URL, name, mobile, address, orderNo, platform);
            URL url = new URL(formatUrl);
            System.out.println("请求的URL = " + formatUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            final int responseCode = connection.getResponseCode();
            System.out.println("responseCode = " + responseCode);
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                // 读取响应
                InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String lines;
                StringBuffer responseBuffer = new StringBuffer("");
                while ((lines = reader.readLine()) != null) {
                    lines = new String(lines.getBytes(), "utf-8");
                    responseBuffer.append(lines);
                }
                reader.close();
                System.out.println(responseBuffer + "\n");
                JSONObject jsonObject = new JSONObject(responseBuffer.toString());
                if(null != jsonObject)
                {
                    String msg = jsonObject.optString("msg");
                    Looper.prepare();
                    Toast.makeText(mActivity,msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_ocr_result;
    }

    @Override
    public String pageTitle() {
        return "识别结果";
    }

    private void handleTextOcr() {
        if (null != textOcrItem) {
            if (null != textOcrItem.items && !textOcrItem.items.isEmpty()) {
                List<OcrItem> tmpOcrItems = new LinkedList<>();
                OcrItem tmpOcrItem = textOcrItem.items.get(0);
                for (int i = 0; i < textOcrItem.items.size(); i++) {
                    OcrItem ocrItem = textOcrItem.items.get(i);
                    if (ocrItem.itemstring.contains(SAME_LINE_FLAG)) {
                        if (i == 1) {
                            tmpOcrItem.itemstring = tmpOcrItem.itemstring + ocrItem.itemstring;
                            tmpOcrItems.set(0, tmpOcrItem);
                        } else {
                            tmpOcrItems.add(ocrItem);
                        }
                    } else {
                        tmpOcrItems.add(ocrItem);
                    }
                }
                if (tmpOcrItems.size() > 2) {
                    String text = tmpOcrItems.get(1).itemstring;
                    tmpOcrItems.get(1).itemstring = tmpOcrItems.get(2).itemstring;
                    tmpOcrItems.get(2).itemstring = text;
                }
                textOcrItem.items.clear();
                textOcrItem.items.addAll(tmpOcrItems);
            }

        }
    }
}
