package com.payneteasy.ldap.users.command;

import com.payneteasy.ldap.users.IDirectoryService;
import com.payneteasy.ldap.users.IFormatService;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.PrintWriter;

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

    void execute(OptionSet aOptionSet, PrintWriter aOut, IDirectoryService aDirectoryService, IFormatService aFormatService) throws Exception;
}
