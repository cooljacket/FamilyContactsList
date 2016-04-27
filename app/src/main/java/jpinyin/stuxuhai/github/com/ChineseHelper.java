package jpinyin.stuxuhai.github.com;

import android.content.Context;

import java.util.Map;

/**
 * 汉字简繁体转换类
 *
 * @author stuxuhai (dczxxuhai@gmail.com)
 */
public final class ChineseHelper {

    private static final String CHINESE_REGEX = "[\\u4e00-\\u9fa5]";
    private static Map<String, String> CHINESE_MAP;
    private static ChineseHelper helper;

    public static ChineseHelper getInstance(Context context) {
        if (helper == null)
            helper = new ChineseHelper(context);
        return helper;
    }

    private ChineseHelper(Context context) {
        CHINESE_MAP = PinyinResource.getChineseResource(context);
    }

    /**
     * 判断某个字符是否为汉字
     * 
     * @param c 需要判断的字符
     * @return 是汉字返回true，否则返回false
     */
    public static boolean isChinese(char c) {
        return '〇' == c || String.valueOf(c).matches(CHINESE_REGEX);
    }
}
