package com.payneteasy.ldap;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

/**
 *
 */
public class AttributesHolder {

    public AttributesHolder(String ...aClasses) {
        BasicAttribute attribute = new BasicAttribute("objectClass");
        for (String name : aClasses) {
            attribute.add(name);
        }
        theAttributes.put(attribute);
    }

    public AttributesHolder add(String aName, String aValue) {
        BasicAttribute attribute = new BasicAttribute(aName);
        attribute.add(aValue);
        theAttributes.put(attribute);
        return this;
    }

    public Attributes getAttributes() {
        return theAttributes;
    }

    private final Attributes theAttributes = new BasicAttributes();
}
