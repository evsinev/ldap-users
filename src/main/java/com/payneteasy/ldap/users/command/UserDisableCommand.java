package com.payneteasy.ldap.users.command;

import com.payneteasy.ldap.users.IDirectoryService;
import com.payneteasy.ldap.users.IOutputService;
import com.payneteasy.ldap.users.model.LdapQueryHolder;
import com.payneteasy.ldap.users.model.ParametersBuilder;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 */
public class UserDisableCommand implements ICommand {


    public UserDisableCommand(LdapQueryHolder aHolder, String aUsersBase) {
        theUsersBase = aUsersBase;
    }

    @Override
    public String getModule() {
        return "user";
    }

    @Override
    public String getCommand() {
        return "disable";
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

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -100);

        aDirectoryService.addOrModify(name, new ParametersBuilder()
                .add("pwdAccountLockedTime", format.format(calendar.getTime()))
                .build()
        );

        aFormatService.info("Username " + userParameter + " is locked");
    }

    private final String theUsersBase;
    private OptionSpec<String> usernameSpec;

}
