package com.payneteasy.ldap.users.command;

import com.payneteasy.ldap.users.IDirectoryService;
import com.payneteasy.ldap.users.IFormatService;
import com.payneteasy.ldap.users.model.LdapQuery;
import com.payneteasy.ldap.users.model.LdapQueryHolder;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class GroupListCommand implements ICommand {


    public GroupListCommand(LdapQueryHolder aHolder) {
        holder = aHolder;
    }

    @Override
    public String getModule() {
        return "group";
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
    public void execute(OptionSet aOptionSet, PrintWriter aOut, IDirectoryService aDirectoryService, IFormatService aFormatService) throws Exception {
        LdapQuery query = holder.find("groups");

        List<Map<String, Object>> result = aDirectoryService.search(query.name, query.match, query.attributes);

        aOut.print(aFormatService.format(result, query.attributes));

    }

    private final LdapQueryHolder holder;

}
