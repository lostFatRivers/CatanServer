package com.joker.tools.game;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TODO description
 *
 * @author: Joker
 * @date: Created in 2020/12/28 0:53
 * @version: 1.0
 */
public class DateTest {
    public static void main(String[] args) throws Exception {
        String strDate = "2020-12-26T10:52:27.174Z";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = dateFormat.parse(strDate);
        System.out.println(date.getTime());
    }
}
