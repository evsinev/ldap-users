package com.payneteasy.ldap.users.command;

import com.payneteasy.ldap.users.IDirectoryService;
import com.payneteasy.ldap.users.IOutputService;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 *
 */
public interface ICommand {

    /**
     * user
     */
    String getModule();

    /**
     * list
     */
    String getCommand();

    /**
     *
     */
    OptionParser getOptionParser();

    void execute(OptionSet aOptionSet, IDirectoryService aDirectoryService, IOutputService aFormatService) throws Exception;
}
