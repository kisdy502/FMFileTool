package cn.fengmang.file.utils;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.fengmang.baselib.ELog;

/**
 * Created by Administrator on 2018/7/23.
 */

public class SpannableUtils {

    public static SpannableStringBuilder matcherSearchTitle(@ColorInt int color, @NonNull String text, @NonNull String keyword) {
        SpannableStringBuilder s = new SpannableStringBuilder(text);
        Pattern p = Pattern.compile(keyword);
        Matcher m = p.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            s.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return s;
    }
}
