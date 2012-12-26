package com.payneteasy.ldap.users.command;

import com.payneteasy.ldap.users.IDirectoryService;
import com.payneteasy.ldap.users.IFormatService;
import com.payneteasy.ldap.users.model.LdapQuery;
import com.payneteasy.ldap.users.model.LdapQueryHolder;
import com.payneteasy.ldap.users.model.ParametersBuilder;
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
public class UserResetCommand implements ICommand {


    public UserResetCommand(LdapQueryHolder aHolder, String aUsersBase, String aPpoliciesDn) {
        holder = aHolder;
        theUsersBase = aUsersBase;
        thePpoliciesDn = aPpoliciesDn;
    }

    @Override
    public String getModule() {
        return "user";
    }

    @Override
    public String getCommand() {
        return "reset";
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

        String password = PasswordGenerator.createPassword();

        aDirectoryService.addOrModify(thePpoliciesDn, new ParametersBuilder()
                .add("pwdMinAge", "1")
                .build()
        );

        aDirectoryService.addOrModify(name, new ParametersBuilder()
                .add("userPassword", password)
                .add("pwdReset", "TRUE")
                .build()
        );

        aDirectoryService.addOrModify(thePpoliciesDn, new ParametersBuilder()
                .add("pwdMinAge", "86400")
                .build()
        );

        aOut.println("Username / password is "+userParameter+" / "+password);

        aOut.println("User must change password on next login.");
    }


    private final LdapQueryHolder holder;
    private final String theUsersBase;
    private OptionSpec<String> usernameSpec;
    private final String thePpoliciesDn;

}
