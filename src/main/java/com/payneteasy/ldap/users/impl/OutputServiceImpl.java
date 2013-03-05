package com.payneteasy.ldap.users.impl;

import com.payneteasy.ldap.users.IOutputService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class OutputServiceImpl implements IOutputService {

    private static final Logger LOG = LoggerFactory.getLogger("ldap-users");

    public static final int MAX_IN_COLUMN = 30;

    public OutputServiceImpl(PrintWriter aOut) {
        theOut = aOut;
    }

    @Override
    public String format(List<Map<String, Object>> aResult, String[] aHeaders) {
        Set<String> keys =  listKeys(aResult);
        keys.addAll(Arrays.asList(aHeaders));

        Map<String, Integer> maxMap = new HashMap<String, Integer>();

        findMaxLength(maxMap, keys);
        findMaxLength(maxMap, keys, aResult);

        StringBuilder sb = new StringBuilder();
        for (String header : aHeaders) {
            sb.append(' ');
            formatText(sb, maxMap, header, header);
            sb.append(' ');
        }

        sb.append("\n");

        for (Map<String, Object> map : aResult) {
            for (String header : aHeaders) {
                sb.append(' ');
                formatText(sb, maxMap, header, format(map.get(header), MAX_IN_COLUMN));
                sb.append(' ');
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public String format(Map<String, Object> aResult, String[] aHeaders) {
        int max = findMaxLength(aResult.keySet());
        StringBuilder sb = new StringBuilder();
        for (String header : aHeaders) {
            printText(sb, header, max);
            sb.append(" :  ");
            sb.append(format(aResult.get(header), 200));
            sb.append("\n");

        }
        return sb.toString();
    }

    @Override
    public void println(String aText) {
        theOut.println(aText);
        theOut.flush();
    }

    @Override
    public void info(String aText) {
        theOut.println(aText);
        theOut.flush();
        LOG.info(aText);
    }

    @Override
    public void log(String aText) {
        LOG.info(aText);
    }

    @Override
    public void error(String aMessage, Exception e) {
        theOut.println("ERROR: "+aMessage);
        theOut.flush();
        LOG.error(aMessage, e);
    }

    @Override
    public void error(String aMessage) {
        theOut.println("ERROR: "+aMessage);
        theOut.flush();
        LOG.error(aMessage);
    }

    private void printText(StringBuilder sb, String text, int max) {
        sb.append(text);
        for(int i=text.length(); i<max; i++) {
            sb.append(' ');
        }
    }

    private int findMaxLength(Set<String> aValues) {
        int max = 0;
        for (String value : aValues) {
            if(value.length() > max) {
                max = value.length();
            }
        }
        return max;
    }

    private void formatText(StringBuilder aSb, Map<String, Integer> aMaxMap, String aKey, String aValue) {
        Integer length = aMaxMap.get(aKey);
        if(length==null) length=0;

        if(aValue==null) {
            aValue="-";
        }
        aSb.append(aValue);
        for(int i=0; i < length - aValue.length(); i++) {
            aSb.append(' ');
        }

    }

    private void findMaxLength(Map<String, Integer> aMapMap, Set<String> aKeys, List<Map<String, Object>> aResult) {
        for (Map<String, Object> map : aResult) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                maxOf(aMapMap, entry.getKey(), format(entry.getValue(), MAX_IN_COLUMN));
            }
        }
    }

    private void findMaxLength(Map<String, Integer> aMapMap, Set<String> aKeys) {
        for (String key : aKeys) {
            maxOf(aMapMap, key, key);
        }
    }

    private void maxOf(Map<String, Integer> aMapMap, String aKey, String aValue) {
        if(aValue==null) return;

        Integer max = aMapMap.get(aKey);
        if(max==null) {
            aMapMap.put(aKey, aValue.length());
        } else {
            if(max < aValue.length()) {
                aMapMap.put(aKey, aValue.length());
            }
        }
    }

    private Set<String> listKeys(List<Map<String, Object>> aMap) {
        Set<String> set = new TreeSet<String>();
        for (Map<String, Object> map : aMap) {
            set.addAll(map.keySet());
        }
        return set;
    }

    private String format(Object aValue, int aMax) {
        if(aValue==null) return "-";

        String ret;
        if(aValue instanceof String) {
            String text = (String) aValue;
            if(isDate(text)) {
                ret = parseAndFormatDate(text);
            } else {
                ret = text;
            }

        } else if(aValue instanceof byte[]) {
            byte[] value = (byte[]) aValue;
            ret = "b64["+value.length+"]="+DatatypeConverter.printBase64Binary(value);

        } else if(aValue instanceof List) {
            StringBuilder sb = new StringBuilder();
            for (Object obj : (List) aValue) {
                if(sb.length()!=0) sb.append(",");
                sb.append(format(obj, aMax));
            }
            ret = sb.toString();
        } else {
            throw new IllegalStateException("Can't format class "+aValue.getClass());
        }

        if(ret!=null && ret.length()>aMax) {
            ret = ret.substring(0, aMax)+"...";
        }
        return ret;
    }

    private String parseAndFormatDate(String aText) {
        // 2012 11 21 08 09 13 Z
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyyMMddHHmmss zzz");
        SimpleDateFormat outFormat = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");

        try {
            Date date = inFormat.parse(aText.substring(0, 14)+" GMT");
            return outFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return aText;
        }
    }

    /**
     * example 20121121080913Z;
     * @param aText
     * @return
     */
    private boolean isDate(String aText) {
        final int LENGTH = 15;
        if(aText.length()==15) {
            if(!aText.endsWith("Z")) {
                return false;
            }
            for(int i=0; i<LENGTH-1; i++) {
                Character ch = aText.charAt(i);
                if(!Character.isDigit(ch)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private final PrintWriter theOut;
}