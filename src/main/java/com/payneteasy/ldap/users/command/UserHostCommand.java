package com.payneteasy.ldap.users.command;

import com.payneteasy.ldap.users.IDirectoryService;
import com.payneteasy.ldap.users.IOutputService;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class UserHostCommand implements ICommand {


    public UserHostCommand(String aUsersBase) {
        theUsersBase = aUsersBase;
    }

    @Override
    public String getModule() {
        return "user";
    }

    @Override
    public String getCommand() {
        return "host";
    }

    @Override
    public OptionParser getOptionParser() {
        OptionParser parser = new OptionParser();


        usernameSpec      = parser.accepts("u", "username").withRequiredArg().required();
        addSpec           = parser.accepts("add", "Add host");
        removeSpec           = parser.accepts("remove", "Remove host");
        hostSpec           = parser.accepts("h", "hostname").withRequiredArg().required();

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

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("Host", aOptionSet.valueOf(hostSpec));

        if(aOptionSet.has(addSpec)) {
            aDirectoryService.add(name, parameters);
        } else if(aOptionSet.has((removeSpec))) {
            aDirectoryService.remove(name, parameters);
        } else {
            throw new IllegalStateException("-add or -remove option must be specified");
        }

    }

    private final String theUsersBase;
    private OptionSpec<String> usernameSpec;
    private OptionSpec<String> hostSpec;
    private OptionSpec<Void> addSpec;
    private OptionSpec<Void> removeSpec;

}
