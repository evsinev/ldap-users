package com.payneteasy.ldap.users;

import javax.naming.NamingException;
import java.util.List;
import java.util.Map;

/**
 *
 */
public interface IDirectoryService {

    void connect(String aUrl, String aUsername, String aPassword) throws NamingException;

    List<Map<String, Object>> search(String aName, String aMatch, String ...aAttributes) throws NamingException;

    void addOrModify(String aName, Map<String, Object> aParameters) throws NamingException;

    void remove(String aName, String ... aAttributes) throws NamingException;

    void remove(String aName, Map<String, Object> aParameters) throws NamingException;
    void add(String aName, Map<String, Object> aParameters) throws NamingException;

    Map<String, Object> get(String aName, String ... aAttributes) throws NamingException;

}
