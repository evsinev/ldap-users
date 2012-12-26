package com.payneteasy.ldap.users.model;

/**
 *
 */
public class LdapQuery {

    public LdapQuery(String name, String match, String[] attributes) {
        this.name = name;
        this.match = match;
        this.attributes = attributes;
    }

    public final String name;
    public final String match;
    public final String[] attributes;

}
