package com.payneteasy.ldap.users.model;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class LdapQueryHolder {

    public LdapQueryHolder(String aUsersDn, String aGroupsDn) {
        theMap = new HashMap<String, LdapQuery>();
        theMap.put("users",  new LdapQuery(aUsersDn, "(objectClass=*)"
                , new String[] {"cn", "gecos", "uid", "uidNumber", "authTimestamp", "pwdFailedTime", "pwdChangedTime", "pwdReset", "pwdAccountLockedTime", "host"}));

        theMap.put("user-info",  new LdapQuery(aUsersDn, "(objectClass=*)"
                , new String[] {"cn", "gecos", "uid", "uidNumber", "authTimestamp", "pwdFailedTime", "pwdChangedTime", "pwdReset", "pwdFailureTime", "pwdAccountLockedTime","host" }));

        theMap.put("groups", new LdapQuery(aGroupsDn, "(objectClass=*)", new String[] {"cn", "gidNumber", "memberUid"}));

        theMap.put("policies-info",  new LdapQuery(aUsersDn, "(objectClass=*)"
                , new String[] {"cn", "pwdAttribute", "pwdAllowUserChange", "pwdCheckModule", "pwdCheckQuality", "pwdExpireWarning"
                , "pwdFailureCountInterval", "pwdGraceAuthNLimit", "pwdInHistory", "pwdLockout", "pwdLockoutDuration"
                , "pwdMaxAge", "pwdMaxFailure", "pwdMinAge", "pwdMinLength", "pwdMustChange", "pwdSafeModify"}));
    }

    public LdapQuery find(String aName) {
        return theMap.get(aName);
    }

    private final Map<String, LdapQuery> theMap ;
}
