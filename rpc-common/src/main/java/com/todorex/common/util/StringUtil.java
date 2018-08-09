package com.todorex.common.util;

import org.apache.commons.lang3.StringUtils;

/**
 * String工具类
 *
 * @Author rex
 * 2018/8/8
 */
public class StringUtil {
    /**
     * 分割固定格式的字符串
     * @param str
     * @param separator
     * @return
     */
    public static String[] split(String str, String separator) {
        return StringUtils.splitByWholeSeparator(str, separator);
    }
}
