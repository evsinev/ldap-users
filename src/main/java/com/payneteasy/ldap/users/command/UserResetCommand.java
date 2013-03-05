package com.payneteasy.ldap.users.command;

import com.payneteasy.ldap.users.IDirectoryService;
import com.payneteasy.ldap.users.IOutputService;
import com.payneteasy.ldap.users.model.ParametersBuilder;
import com.payneteasy.ldap.users.util.PasswordGenerator;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import javax.naming.NamingException;

/**
 *
 */
public class UserResetCommand implements ICommand {


    public UserResetCommand(String aUsersBase, String aPpoliciesDn) {
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
    public void execute(OptionSet aOptionSet, IDirectoryService aDirectoryService, IOutputService aFormatService) throws Exception {
        final String userParameter = aOptionSet.valueOf(usernameSpec);

        String name;
        if(userParameter.startsWith("cn")) {
            name = userParameter;
        } else {
            name = "cn="+userParameter+","+theUsersBase;
        }

        createUser(aDirectoryService, userParameter, name, aFormatService);
    }

    public void createUser(IDirectoryService aDirectoryService, String userParameter, String name,  IOutputService aFormatService) throws NamingException {
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

        aFormatService.println("Username / password is "+userParameter+" / "+password);

        aFormatService.info("User "+userParameter+" must change password on next login.");
    }


    private final String theUsersBase;
    private OptionSpec<String> usernameSpec;
    private final String thePpoliciesDn;

}
