package cn.fengmang.file.utils;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cn.fengmang.baselib.ELog;

/**
 * Created by Administrator on 2018/6/28.
 */

public class FileTagHelper {

    public static ArrayMap<String, String> getTagList(Context context) {
        ArrayMap<String, String> tagLIst = new ArrayMap<>();
        InputStream is = null;
        try {
            String temp = null;
            is = context.getAssets().open("filecontent.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while ((temp = reader.readLine()) != null) {

                if (temp.contains(":")) {
                    if (temp.endsWith(",")) {
                        temp = temp.substring(0, temp.length() - 1);
                    }
                    String[] tags = temp.split(":");
                    if (tags != null && tags.length == 2) {
                        tagLIst.put(tags[0].trim().replace("\"",""), tags[1].trim().replace("\"",""));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tagLIst;
    }
}
