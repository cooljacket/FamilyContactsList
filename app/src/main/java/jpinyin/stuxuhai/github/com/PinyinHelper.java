package jpinyin.stuxuhai.github.com;

import android.content.Context;
import android.util.Log;

import java.util.LinkedHashSet;
import java.util.Map;

/**
 * 汉字转拼音类
 *
 * @author stuxuhai (dczxxuhai@gmail.com)
 */
public final class PinyinHelper {
    private static Map<String, String> PINYIN_TABLE;
    private static final String PINYIN_SEPARATOR = ","; // 拼音分隔符
    private static final char CHINESE_LING = '〇';
    private static final String ALL_UNMARKED_VOWEL = "aeiouv";
    private static final String ALL_MARKED_VOWEL = "āáǎàēéěèīíǐìōóǒòūúǔùǖǘǚǜ"; // 所有带声调的拼音字母
    private static PinyinHelper helper;

    public static PinyinHelper getInstance(Context context) {
        if (helper == null)
            helper = new PinyinHelper(context);
        return helper;
    }


    private PinyinHelper(Context context) {
        PINYIN_TABLE = PinyinResource.getPinyinResource(context);
        ChineseHelper.getInstance(context);
    }


    /**
     * 将带声调格式的拼音转换为不带声调格式的拼音
     * 
     * @param pinyinArrayString 带声调格式的拼音
     * @return 不带声调的拼音
     */
    private static String[] convertWithoutTone(String pinyinArrayString) {
        String[] pinyinArray;
        for (int i = ALL_MARKED_VOWEL.length() - 1; i >= 0; i--) {
            char originalChar = ALL_MARKED_VOWEL.charAt(i);
            char replaceChar = ALL_UNMARKED_VOWEL.charAt(((i - i % 4)) / 4);
            pinyinArrayString = pinyinArrayString.replace(String.valueOf(originalChar), String.valueOf(replaceChar));
        }
        // 将拼音中的ü替换为v
        pinyinArray = pinyinArrayString.replace("ü", "v").split(PINYIN_SEPARATOR);

        // 去掉声调后的拼音可能存在重复，做去重处理
        LinkedHashSet<String> pinyinSet = new LinkedHashSet<String>();
        for (String pinyin : pinyinArray) {
            pinyinSet.add(pinyin);
        }

        return pinyinSet.toArray(new String[pinyinSet.size()]);
    }


    /**
     * 将单个汉字转换为相应格式的拼音
     * 
     * @param c 需要转换成拼音的汉字
     * @return 汉字的拼音
     */
    public static String[] convertToPinyinArray(char c) {
        String pinyin = PINYIN_TABLE.get(String.valueOf(c));
        if ((pinyin != null) && (!pinyin.equals("null"))) {
            return convertWithoutTone(pinyin);
        }
        return null;
    }


    /**
     * 将字符串转换成相应格式的拼音
     * 
     * @param str 需要转换的字符串
     * @param separator 拼音分隔符
     * @return 字符串的拼音
     */
    public static String convertToPinyinString(String str, String separator) {
//        str = ChineseHelper.convertToSimplifiedChinese(str);
        StringBuilder sb = new StringBuilder();
        for (int i = 0, len = str.length(); i < len; i++) {
            char c = str.charAt(i);
            // 判断是否为汉字
            if (ChineseHelper.isChinese(c)) {
                String[] pinyinArray = convertToPinyinArray(str.charAt(i));
                if (pinyinArray != null) {
                    sb.append(pinyinArray[0].charAt(0));
                } else {
                    sb.append(str.charAt(i));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    /**
     * 获取字符串对应拼音的首字母
     * 
     * @param str 需要转换的字符串
     * @return 对应拼音的首字母
     */
    public static String getShortPinyin(String str) {
        String separator = "#"; // 使用#作为拼音分隔符
        StringBuilder sb = new StringBuilder();

        char[] charArray = new char[str.length()];
        for (int i = 0, len = str.length(); i < len; i++) {
            char c = str.charAt(i);

            // 首先判断是否为汉字或者〇，不是的话直接将该字符返回
            if (!ChineseHelper.isChinese(c) && c != CHINESE_LING) {
                charArray[i] = c;
            } else {
                int j = i + 1;
                sb.append(c);

                // 搜索连续的汉字字符串
                while (j < len && (ChineseHelper.isChinese(str.charAt(j)) || str.charAt(j) == CHINESE_LING)) {
                    sb.append(str.charAt(j));
                    j++;
                }
                String hanziPinyin = convertToPinyinString(sb.toString(), separator);
                String[] pinyinArray = hanziPinyin.split(separator);
                for (String string : pinyinArray) {
                    charArray[i] = string.charAt(0);
                    i++;
                }
                i--;
                sb.setLength(0);
            }
        }
        return String.valueOf(charArray);
    }
}