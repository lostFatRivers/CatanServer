package com.jokerbee.util;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串工具;
 *
 * @author: Joker
 * @date: Created in 2021/1/28 10:17
 * @version: 1.0
 */
public class StringUtil {

    /**
     * 将字符串中的内容通过匹配规则提取出;<br/>
     * 例如:<br/>
     * 输入 ("我的名字叫Joker", "我的名字叫{0}")<br/>
     * 输出 ["Joker"]
     *
     * @param line 需要解析的字符串
     * @param matchStr 匹配规则, 目前不能有重复间隔
     * @return 匹配的结果集合
     */
    public static List<String> replaceHolderExtract(String line, String matchStr) {
        List<String> list = new ArrayList<>();

        boolean firstMatch = false;
        List<String> splitsList = new ArrayList<>();
        int lastHolderIndex = 0;
        for (int i = 0; i < 1000; i++) {
            String holder = "{" + i + "}";
            if (!matchStr.contains(holder)) {
                break;
            }
            int index = matchStr.indexOf(holder);
            if (index == 0) {
                firstMatch = true;
            }
            if (index <= lastHolderIndex) {
                lastHolderIndex = index + holder.length();
                continue;
            }
            String substring = matchStr.substring(lastHolderIndex, index);
            if (StringUtils.isEmpty(substring)) {
                continue;
            }
            splitsList.add(escapeChars(substring));
            lastHolderIndex = index + holder.length();
        }
        if (lastHolderIndex < matchStr.length() - 1) {
            String substring = matchStr.substring(lastHolderIndex);
            if (StringUtils.isNotEmpty(substring)) {
                splitsList.add(escapeChars(substring));
            }
        }

        for (int i = 0; i < splitsList.size(); i++) {
            String eachS = splitsList.get(i);
            String[] split = line.split(eachS);
            if (firstMatch && i == 0) {
                list.add(split[0]);
            }
            if (split.length <= 1) {
                continue;
            }
            String content = split[1];
            if (i + 1 < splitsList.size()) {
                String eachE = splitsList.get(i + 1);
                list.add(content.split(eachE)[0]);
            } else {
                list.add(content);
            }
        }
        return list;
    }

    /**
     * 转换特殊字符
     *
     * @param s 需要转义的字符串
     * @return 返回转义后的字符串
     */
    public static String escapeChars(String s) {
        if (StringUtils.isBlank(s)) {
            return s;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')'
                    || c == ':' || c == '^'	|| c == '[' || c == ']' || c == '\"'
                    || c == '{' || c == '}' || c == '~' || c == '*' || c == '?'
                    || c == '|' || c == '&' || c == ';' || c == '/' || c == '.'
                    || c == '$' || Character.isWhitespace(c)) {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
