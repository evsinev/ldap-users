package com.payneteasy.ldap.users;

import java.util.List;
import java.util.Map;

public interface IFormatService {

    String format(List<Map<String, Object>> aResult, String[] aHeaders);

    String format(Map<String, Object> aResult, String[] aHeaders);

}
