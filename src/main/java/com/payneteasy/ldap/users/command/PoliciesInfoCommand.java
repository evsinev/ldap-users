package com.payneteasy.ldap.users.command;

import com.payneteasy.ldap.users.IDirectoryService;
import com.payneteasy.ldap.users.IFormatService;
import com.payneteasy.ldap.users.model.LdapQuery;
import com.payneteasy.ldap.users.model.LdapQueryHolder;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.PrintWriter;
import java.util.Map;

/**
 *
 */
public class PoliciesInfoCommand implements ICommand {


    public PoliciesInfoCommand(LdapQueryHolder aHolder, String aPpolicyDn) {
        holder = aHolder;
        thePpolicyDn = aPpolicyDn;
    }

    @Override
    public String getModule() {
        return "policies";
    }

    @Override
    public String getCommand() {
        return "info";
    }

    @Override
    public OptionParser getOptionParser() {
        return new OptionParser();
    }

    @Override
    public void execute(OptionSet aOptionSet, PrintWriter aOut, IDirectoryService aDirectoryService, IFormatService aFormatService) throws Exception {
        LdapQuery query = holder.find("policies-info");
        Map<String, Object> result = aDirectoryService.get(thePpolicyDn, query.attributes);

        aOut.print(aFormatService.format(result, query.attributes));

    }

    private final LdapQueryHolder holder;
    private final String thePpolicyDn;

}
