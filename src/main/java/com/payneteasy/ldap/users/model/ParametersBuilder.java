package com.payneteasy.ldap.users.model;

import java.util.HashMap;
import java.util.Map;

public class ParametersBuilder {

    public ParametersBuilder add(String aName, Object aValue) {
        theMap.put(aName, aValue);
        return this;
    }

    public Map<String, Object> build() {
        return theMap;
    }

    private Map<String, Object> theMap = new HashMap<String, Object>();
}
