package com.payneteasy.ldap.users.command;

import com.payneteasy.ldap.AttributesHolder;
import com.payneteasy.ldap.users.IDirectoryService;
import com.payneteasy.ldap.users.IOutputService;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class UserAddCommand implements ICommand {

    public UserAddCommand(String aUsersBase, String aGroupsBase, String aPpoliciesDn) {
        theUsersBase = aUsersBase;
        theGroupsBase = aGroupsBase;

        theUserResetCommand = new UserResetCommand(aUsersBase, aPpoliciesDn);
    }

    @Override
    public String getModule() {
        return "user";
    }

    @Override
    public String getCommand() {
        return "add";
    }

    @Override
    public OptionParser getOptionParser() {
        OptionParser parser = new OptionParser();


        usernameSpec      = parser.accepts("u", "username").withRequiredArg().required();
        gecosSpec         = parser.accepts("gecos", "Fullname").withRequiredArg().required();
        idSpec            = parser.accepts("id", "User and group integer identifier (uid and gidNumber)").withRequiredArg().ofType(Integer.class).required();

        return parser;
    }

    @Override
    public void execute(OptionSet aOptionSet, IDirectoryService aDirectoryService, IOutputService aFormatService) throws Exception {
        final String userParameter = aOptionSet.valueOf(usernameSpec);

        String userDn =  "cn="+userParameter+","+theUsersBase;
        String groupDn = "cn="+userParameter+","+theGroupsBase;

        String id = String.valueOf(aOptionSet.valueOf(idSpec));
        String gecos = aOptionSet.valueOf(gecosSpec);

        AttributesHolder group = new AttributesHolder("top", "posixGroup");
        group.add("gidNumber", id);
        group.add("description", gecos);

        try {
            aDirectoryService.addEntry(groupDn, group);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        AttributesHolder user = new AttributesHolder("top", "person", "organizationalPerson", "inetOrgPerson", "posixAccount", "shadowAccount", "hostObject");
        user.add("gecos", gecos);
        user.add("sn", gecos);
        user.add("uid", userParameter);
        user.add("homeDirectory", "/home/"+userParameter);
        user.add("loginShell", "/bin/bash");
        user.add("gidNumber", id);
        user.add("uidNumber", id);

        aDirectoryService.addEntry(userDn, user);

        theUserResetCommand.createUser(aDirectoryService, userParameter, userDn, aFormatService);
    }

    private final String theUsersBase;
    private final String theGroupsBase;
    private OptionSpec<String> usernameSpec;
    private OptionSpec<String> gecosSpec;
    private OptionSpec<Integer> idSpec;
    private final UserResetCommand theUserResetCommand;

}
