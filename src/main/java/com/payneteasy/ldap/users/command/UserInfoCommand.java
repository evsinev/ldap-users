package com.payneteasy.ldap.users.command;

import com.payneteasy.ldap.users.IDirectoryService;
import com.payneteasy.ldap.users.IOutputService;
import com.payneteasy.ldap.users.model.LdapQuery;
import com.payneteasy.ldap.users.model.LdapQueryHolder;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.util.Map;

/**
 *
 */
public class UserInfoCommand implements ICommand {


    public UserInfoCommand(LdapQueryHolder aHolder, String aUsersBase) {
        holder = aHolder;
        theUsersBase = aUsersBase;
    }

    @Override
    public String getModule() {
        return "user";
    }

    @Override
    public String getCommand() {
        return "info";
    }

    @Override
    public OptionParser getOptionParser() {
        OptionParser parser = new OptionParser();

        usernameSpec      = parser.accepts("u", "username").withRequiredArg().required();

        return parser;
    }

    @Override
    public void execute(OptionSet aOptionSet, IDirectoryService aDirectoryService, IOutputService aFormatService) throws Exception {
        LdapQuery query = holder.find("user-info");
        String userParameter = aOptionSet.valueOf(usernameSpec);
        if(!userParameter.startsWith("cn")) {
            userParameter = "cn="+userParameter+","+theUsersBase;
        }

        Map<String, Object> result = aDirectoryService.get(userParameter, query.attributes);

        aFormatService.println(aFormatService.format(result, query.attributes));

    }

    private final LdapQueryHolder holder;
    private final String theUsersBase;
    private OptionSpec<String> usernameSpec;

}
