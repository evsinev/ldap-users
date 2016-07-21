package com.payneteasy.ldap.users.impl;

import com.payneteasy.ldap.AttributesHolder;
import com.payneteasy.ldap.users.IDirectoryService;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.*;

/**
 *
 */
public class DirectoryServiceImpl implements IDirectoryService {

    public DirectoryServiceImpl() {
    }

    public DirectoryServiceImpl(LdapContext aConnection) {
        this.theConnection = aConnection;
    }

    @Override
    public void connect(String aUrl, String aUsername, String aPassword) throws NamingException {
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, aUrl);
        env.put(Context.SECURITY_PRINCIPAL, aUsername);
        env.put(Context.SECURITY_CREDENTIALS, aPassword);
//        env.put("java.naming.ldap.version", "3");
//        env.put("java.naming.referral", "throw");

        theConnection = new InitialLdapContext(env, null);
    }

    public void addEntry(String aName, AttributesHolder aAttributes) throws NamingException {
        theConnection.createSubcontext(aName, aAttributes.getAttributes());
    }

    @Override
    public List<Map<String, Object>> search(String aName, String aMatch, String ...aAttributes) throws NamingException {

        if(aAttributes==null || aAttributes.length==0) {
            throw new NamingException("No Attributes passed to search");
        }
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setReturningAttributes(aAttributes);
        searchControls.setReturningObjFlag(true);
//        searchControls.setSearchScope();

        NamingEnumeration<SearchResult> results = theConnection.search(aName, aMatch, searchControls);

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        while (results.hasMore()) {

            SearchResult result = results.next();

            Map<String, Object> map = createMap(result.getAttributes());
            if(map.size()>0) {
                list.add(map);
            }

        }

        return list;
    }

    private Map<String, Object> createMap(Attributes aAttributes) throws NamingException {
        Map<String, Object> map = new HashMap<String, Object>();
        NamingEnumeration<? extends Attribute> en = aAttributes.getAll();
        while (en.hasMoreElements()) {
            Attribute attribute = en.nextElement();

            String id = attribute.getID();
            if(attribute.size()>1) {
                List list = new ArrayList();
                Enumeration enAttribute = attribute.getAll();
                while (enAttribute.hasMoreElements()) {
                    Object o =  enAttribute.nextElement();
                    list.add(o);
                }
                map.put(id, list);
            } else {
                Object value = attribute.get();
                map.put(id, value);
            }

        }
        return map;
    }

    @Override
    public void remove(String aName, Map<String, Object> aParameters) throws NamingException {
        ModificationItem[] modifications = new ModificationItem[aParameters.size()];
        int i = 0;
        for (Map.Entry<String, Object> entry : aParameters.entrySet()) {
            modifications[i] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(entry.getKey(), entry.getValue()));
        }
        theConnection.modifyAttributes(aName, modifications);
    }

    @Override
    public void add(String aName, Map<String, Object> aParameters) throws NamingException {
        ModificationItem[] modifications = new ModificationItem[aParameters.size()];
        int i = 0;
        for (Map.Entry<String, Object> entry : aParameters.entrySet()) {
            modifications[i] = new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute(entry.getKey(), entry.getValue()));
        }
        theConnection.modifyAttributes(aName, modifications);
    }

    @Override
    public void remove(String aName, String... aAttributes) throws NamingException {
        ModificationItem[] modifications = new ModificationItem[aAttributes.length];
        for(int i=0; i<aAttributes.length; i++) {
            ModificationItem item = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(aAttributes[i]));
            modifications[i] = item;
        }
        theConnection.modifyAttributes(aName, modifications);

    }

    public void addOrModify(String aName, Map<String, Object> aParameters) throws NamingException {

        String[] attributes = getAttributes(aParameters);
        String[] searchAttributes = new String[attributes.length+1];
        System.arraycopy(attributes, 0, searchAttributes, 1, attributes.length);
        searchAttributes[0]="cn";
        Map<String, Object> current = get(aName, searchAttributes);

        ModificationItem[] modifications = new ModificationItem[aParameters.size()];

        int i = 0;
        for (Map.Entry<String, Object> entry : aParameters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            int modificationOption = current.containsKey(key) ? DirContext.REPLACE_ATTRIBUTE : DirContext.ADD_ATTRIBUTE ;
            Attribute attribute = new BasicAttribute(key, value);
            modifications[i] = new ModificationItem(modificationOption, attribute);

            i++;
        }
        theConnection.modifyAttributes(aName, modifications);
    }

    private String[] getAttributes(Map<String, Object> aParameters) {
        Set<String> keys = aParameters.keySet();
        String[] ret = new String[keys.size()];
        keys.toArray(ret);
        return ret;
    }


    public Map<String, Object> get(String aName, String ... aAttributes) throws NamingException {
        List<Map<String, Object>> list = search(aName, "(objectClass=*)", aAttributes);

        if(list==null || list.isEmpty()) throw new NamingException("Can't find "+aName);
        if(list.size()>1) throw new NamingException("Search returns more than 1 result ("+list.size()+")");

        return list.get(0);
    }

    LdapContext theConnection;
}
