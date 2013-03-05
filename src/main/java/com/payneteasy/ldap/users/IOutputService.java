package com.payneteasy.ldap.users;

import java.util.List;
import java.util.Map;

public interface IOutputService {

    String format(List<Map<String, Object>> aResult, String[] aHeaders);

    String format(Map<String, Object> aResult, String[] aHeaders);

    void println(String aText);

    void error(String aMessage, Exception e);

    void error(String aMessage);

    void info(String aText);

    void log(String aText);
}
