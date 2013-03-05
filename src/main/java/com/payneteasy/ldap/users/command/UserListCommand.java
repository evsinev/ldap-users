package com.payneteasy.ldap.users.command;

import com.payneteasy.ldap.users.IDirectoryService;
import com.payneteasy.ldap.users.IOutputService;
import com.payneteasy.ldap.users.model.LdapQuery;
import com.payneteasy.ldap.users.model.LdapQueryHolder;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class UserListCommand implements ICommand {


    public UserListCommand(LdapQueryHolder aHolder) {
        holder = aHolder;
    }

    @Override
    public String getModule() {
        return "user";
    }

    @Override
    public String getCommand() {
        return "list";
    }

    @Override
    public OptionParser getOptionParser() {
        return new OptionParser();
    }

    @Override
    public void execute(OptionSet aOptionSet, IDirectoryService aDirectoryService, IOutputService aFormatService) throws Exception {
        LdapQuery query = holder.find("users");

        List<Map<String, Object>> result = aDirectoryService.search(query.name, query.match, query.attributes);

        aFormatService.println(aFormatService.format(result, query.attributes));

    }

    private final LdapQueryHolder holder;

}
