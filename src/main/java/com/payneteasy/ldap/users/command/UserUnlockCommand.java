package com.payneteasy.ldap.users.command;

import com.payneteasy.ldap.users.IDirectoryService;
import com.payneteasy.ldap.users.IFormatService;
import com.payneteasy.ldap.users.model.LdapQueryHolder;
import com.payneteasy.ldap.users.util.PasswordGenerator;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class UserUnlockCommand implements ICommand {


    public UserUnlockCommand(LdapQueryHolder aHolder, String aUsersBase) {
        theUsersBase = aUsersBase;
    }

    @Override
    public String getModule() {
        return "user";
    }

    @Override
    public String getCommand() {
        return "unlock";
    }

    @Override
    public OptionParser getOptionParser() {
        OptionParser parser = new OptionParser();

        usernameSpec      = parser.accepts("u", "username").withRequiredArg().required();

        return parser;
    }

    @Override
    public void execute(OptionSet aOptionSet, PrintWriter aOut, IDirectoryService aDirectoryService, IFormatService aFormatService) throws Exception {
        final String userParameter = aOptionSet.valueOf(usernameSpec);

        String name;
        if(userParameter.startsWith("cn")) {
            name = userParameter;
        } else {
            name = "cn="+userParameter+","+theUsersBase;
        }

        Map<String, Object> userInfo = aDirectoryService.get(name, "cn", "pwdAccountLockedTime");
        if(userInfo.containsKey("pwdAccountLockedTime")) {
            aDirectoryService.remove(name, "pwdAccountLockedTime");
        }

    }

    private final String theUsersBase;
    private OptionSpec<String> usernameSpec;

}
