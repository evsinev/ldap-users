package com.payneteasy.ldap.users.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class LdapDateUtil {

    public static String format(Date aDate) {
        SimpleDateFormat format = createDateFormat();
        return format.format(aDate);
    }

    private static SimpleDateFormat createDateFormat() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format;
    }

    public static Date parse(String aText) throws ParseException {
         return createDateFormat().parse(aText);

    }
}
