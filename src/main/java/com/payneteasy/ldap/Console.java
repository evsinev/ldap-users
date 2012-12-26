package com.payneteasy.ldap;

import com.payneteasy.ldap.users.IDirectoryService;
import com.payneteasy.ldap.users.IFormatService;
import com.payneteasy.ldap.users.command.*;
import com.payneteasy.ldap.users.impl.DirectoryServiceImpl;
import com.payneteasy.ldap.users.impl.FormatServiceImpl;
import com.payneteasy.ldap.users.model.LdapQuery;
import com.payneteasy.ldap.users.model.LdapQueryHolder;
import jline.console.ConsoleReader;
import jline.console.completer.StringsCompleter;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import javax.naming.NamingException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;

public class Console {

    public Console(String aUrl, String aUsername, String aUsersDn, String aGroupDn, String aPpolicyDn) throws IOException {
        theUrl = aUrl;
        theUsername = aUsername;
        holder = new LdapQueryHolder(aUsersDn, aGroupDn);

        theFormatService = new FormatServiceImpl();
        theDirectoryService = new DirectoryServiceImpl();


        theCommands = new ArrayList<ICommand>();
        theCommands.add(new UserListCommand(holder));
        theCommands.add(new UserInfoCommand(holder, aUsersDn));
        theCommands.add(new UserResetCommand(holder, aUsersDn, aPpolicyDn));
        theCommands.add(new UserUnlockCommand(holder, aUsersDn));
        theCommands.add(new UserDisableCommand(holder, aUsersDn));
        theCommands.add(new UserHostCommand(aUsersDn));
        theCommands.add(new GroupListCommand(holder));
        theCommands.add(new PoliciesInfoCommand(holder, aPpolicyDn));
    }

    public void run(String aPassword) throws IOException {
        run(aPassword, null);
    }

    public void run(String aPassword, InputStream aInputStream) throws IOException {
        ConsoleReader reader = aInputStream!=null ? new ConsoleReader(aInputStream, System.out) : new ConsoleReader();

        Set<String> completes = new TreeSet<String>();
        completes.add("quit");
        completes.add("q");
        completes.add("exit");
        completes.add("help");
        for (ICommand command : theCommands) {
            completes.add(command.getModule() + " "+command.getCommand());
        }

        reader.addCompleter(new StringsCompleter(completes));


        if(aPassword==null || aPassword.trim().length() == 0 ) {
            aPassword = reader.readLine("Enter password: ", '*');
        }

        reader.setPrompt("ldap> ");
        try {
            System.out.println("Connecting to "+theUrl+" ...");
            theDirectoryService.connect(theUrl, theUsername, aPassword);
        } catch (NamingException e) {
            throw new IOException("Can't connect to server ", e);
        }

        PrintWriter out = new PrintWriter(System.out);

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if(line.startsWith("#")) continue;

            if("quit".equals(line) || "exit".equals(line) || "q".equals(line)) {
                break;

            } else if("help".equals(line)) {
                System.out.println("Available commands: ");
                for (ICommand command : theCommands) {
                    System.out.println("    "+command.getModule() + " "+command.getCommand());
                }


            } else {
                try {
                    ICommand command = findCommand(line);
                    try {
                        OptionSet options = command.getOptionParser().parse(extractArguments(line));
                        try {
                            command.execute(options, out, theDirectoryService, theFormatService);
                        } catch (Exception e) {
                            showException(e);
                        }

                    } catch (OptionException e) {
                        System.out.println(e.getMessage());
                        command.getOptionParser().printHelpOn(System.out);

                    }
                } catch (IllegalStateException e) {
                    System.out.println(e.getMessage());
                }
            }

            out.println();
            out.flush();
        }

    }

    public static String[] extractArguments(String aLine) {
        //String line = "foo,bar,c;qual=\"baz,blurb\",d;junk=\"quux,syzygy\"";
        String[] tokens = aLine.replace('\'', '"').split(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        if(tokens.length>2) {
            return Arrays.copyOfRange(tokens, 2, tokens.length);
        } else {
            return new String[]{};
        }
    }

    private ICommand findCommand(String line) throws IllegalStateException {
        StringTokenizer st = new StringTokenizer(line, ",-= \t");
        String moduleText = st.hasMoreTokens() ? st.nextToken() : "";
        String commandText = st.hasMoreTokens() ? st.nextToken() : "";
        boolean foundModule = false;
        for (ICommand command : theCommands) {
            if(command.getModule().equals(moduleText)) {
                foundModule = true;
                if(command.getCommand().equals(commandText)) {
                    return command;
                }
            }
        }
        if(!foundModule) {
            throw new IllegalStateException("Module "+moduleText+" not found");
        }

        throw new IllegalStateException("Can't find command "+commandText+" in module "+moduleText);
    }

    private void executeQuery(String aName) {
        LdapQuery query = holder.find(aName);

        try {
            List<Map<String, Object>> result = theDirectoryService.search(query.name, query.match, query.attributes);
            System.out.println("result = " + result);

            System.out.println(theFormatService.format(result, query.attributes));

        } catch (NamingException e) {
            showException(e);
        }
    }


    public static void main(String[] args) throws IOException {
        OptionParser parser = new OptionParser();

        OptionSpec<String> urlSpec      = parser.accepts("url", "server url").withRequiredArg().required();
        OptionSpec<String> usernameSpec = parser.accepts("username", "Use Distinguished Name username to bind to the LDAP directory").withRequiredArg().required();
        OptionSpec<String> passwordSpec = parser.accepts("password", "User password").withRequiredArg();
        OptionSpec<Void>   helpSpec     = parser.acceptsAll(Arrays.asList("h", "?"), "show help").forHelp();
        OptionSpec<String> sourceSpec   = parser.accepts("source", "File source").withRequiredArg();
        OptionSpec<String> usersDnSpec  = parser.accepts("users-dn", "Users DN").withRequiredArg().required();
        OptionSpec<String> groupsDnSpec   = parser.accepts("groups-dn", "Groups DN").withRequiredArg().required();
        OptionSpec<String> ppolicyDnSpec  = parser.accepts("ppolicy-dn", "Groups DN").withRequiredArg();

        try {
            OptionSet options = parser.parse(args);

            if (options.has(helpSpec)) {

                parser.printHelpOn(System.out);

            } else {

                Console console = new Console(
                        options.valueOf(urlSpec)
                        , options.valueOf(usernameSpec)
                        , options.valueOf(usersDnSpec)
                        , options.valueOf(groupsDnSpec)
                        , options.has(ppolicyDnSpec) ? options.valueOf(ppolicyDnSpec) : "cn=default,ou=policies,dc=yoyyo"
                );


                if(options.has(sourceSpec)) {
                    console.run(options.valueOf(passwordSpec), new FileInputStream(options.valueOf(sourceSpec)));
                } else {
                    console.run(options.valueOf(passwordSpec));
                }

            }

        } catch (OptionException e) {
            System.out.println(e.getMessage());
            parser.printHelpOn(System.out);

        } catch (Throwable e) {
            showException(e);
        }

    }

    private static void showException(Throwable e) {
        if(e.getMessage()==null) {
            e.printStackTrace();
        } else {

            System.out.println(e.getMessage());

            e = e.getCause();
            for(int i=1; i<20 && e!=null; i++, e=e.getCause()) {
                System.out.println(i+". " + e.getMessage());
            }
        }
    }

    private final String theUrl;
    private final IDirectoryService theDirectoryService;
    private final IFormatService theFormatService;
    private final String theUsername;
    private final LdapQueryHolder holder;
    private final List<ICommand> theCommands;

}
