package com.payneteasy.ldap.users.util;

import java.security.SecureRandom;

/**
 *
 */
public class PasswordGenerator {

    public static final char[] LOWER_CASE = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    public static final char[] UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static final char[] DIGITS = "0123456789".toCharArray();

    public static final char[] PUNCTUATIONS = ".`,:;'!?\"".toCharArray();

    public static final char[] SYMBOLS = "$%^&*()-_+=|\\[]{}#@/~".toCharArray();


    public static String createPassword() {

        StringBuilder sb = new StringBuilder();
        sb.append(randomChars(UPPER_CASE  , random(5, 6) ));
        sb.append(randomChars(LOWER_CASE  , random(5, 6) ));
        sb.append(randomChars(DIGITS      , random(2, 3) ));
        sb.append(randomChars(PUNCTUATIONS, random(1, 2) ));
        sb.append(randomChars(SYMBOLS     , random(1, 2) ));

        return mix(sb);

    }

    private static int random(int aMin, int aMax) {
        double random = Math.random();
        double minus = aMax - aMin;
        double minusRandom = Math.round(random*minus);
        double ret = aMin+minusRandom;
        return (int) ret;
    }

    private static String mix(StringBuilder in) {
        int length = in.length();
        StringBuilder out = new StringBuilder();
        for(int i=0; i<length; i++) {
            int index = (int) (Math.random() * in.length());
            out.append(in.charAt(index));
            in.deleteCharAt(index);
        }
        return out.toString();
    }

    private static char[] randomChars(char[] aChars, int aCount) {
        int length = aChars.length;
        char[] ret = new char[aCount];
        for(int i=0; i<aCount; i++) {
            int index = (int) (Math.random() * length);
            ret[i] = aChars[index];
        }
        return ret;
    }


}
