package com.payneteasy.ldap;

import com.payneteasy.ldap.users.IDirectoryService;
import com.payneteasy.ldap.users.impl.DirectoryServiceImpl;
import com.payneteasy.ldap.users.model.ParametersBuilder;
import com.payneteasy.ldap.users.util.LdapDateUtil;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import javax.naming.NamingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.*;

public class CheckInactiveUsers {

    public CheckInactiveUsers() {
    }


    public void check(String aUrl, String aUsername, String aPassword, String aUsersDn) throws NamingException, ParseException {
        IDirectoryService directoryService = new DirectoryServiceImpl();
        directoryService.connect(aUrl, aUsername, aPassword);
        List<Map<String, Object>> users = directoryService.search(aUsersDn, "(objectClass=posixAccount)"
                , "cn", "authTimestamp", "createTimestamp", "pwdReset", "pwdChangedTime", "pwdAccountLockedTime");
        for (Map<String, Object> user : users) {
            if(isUserInactive(user)) {
                disableUser(directoryService, (String) user.get("cn"), aUsersDn);
            }
        }
    }

    private void disableUser(IDirectoryService aDirectoryService, String aUsername, String aUsersDn) throws NamingException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -100);

        aDirectoryService.addOrModify("cn="+aUsername+","+aUsersDn, new ParametersBuilder()
                .add("pwdAccountLockedTime", LdapDateUtil.format(calendar.getTime()))
                .build()
        );

        System.out.println("Disabled user "+aUsername);
    }

    private boolean isUserInactive(Map<String, Object> aUser) throws ParseException {
        if("TRUE".equals(aUser.get("pwdReset"))) return false;
        if(aUser.containsKey("pwdAccountLockedTime")) return false;

        Date lastAuth = getDate(aUser, "authTimestamp");
        if(lastAuth==null) {
            lastAuth = getDate(aUser, "pwdChangedTime");
        }
        if(lastAuth==null) {
             lastAuth = getDate(aUser, "createTimestamp");
        }
        if(lastAuth==null) throw new IllegalStateException("Can't find any date (authTimestamp or createTimestamp) for user "+aUser.get("cn"));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastAuth);
        calendar.add(Calendar.DAY_OF_YEAR, 90);

        return calendar.getTime().before(new Date());


    }

    private Date getDate(Map<String, Object> aUser, String aText) throws ParseException {
        String value = (String)aUser.get(aText);
        if(value==null || value.trim().length()==0) return null;
        return LdapDateUtil.parse(value);
    }

    public static void main(String[] args) throws ParseException, NamingException, IOException {
        OptionParser parser = new OptionParser();

        OptionSpec<String> urlSpec      = parser.accepts("url", "server url").withRequiredArg().required();
        OptionSpec<String> usernameSpec = parser.accepts("username", "Use Distinguished Name username to bind to the LDAP directory").withRequiredArg().required();
        OptionSpec<String> passwordSpec = parser.accepts("password-file", "User password file").withRequiredArg();
        OptionSpec<Void>   helpSpec     = parser.acceptsAll(Arrays.asList("h", "?"), "show help").forHelp();
        OptionSpec<String> usersDnSpec  = parser.accepts("users-dn", "Users DN").withRequiredArg().required();

        try {
            OptionSet options = parser.parse(args);

            if(options.has(helpSpec)) {
                parser.printHelpOn(System.out);
            } else {
                String password = readPassword(options.valueOf(passwordSpec));

                new CheckInactiveUsers().check(
                        options.valueOf(urlSpec)
                        , options.valueOf(usernameSpec)
                        , password
                        , options.valueOf(usersDnSpec)
                );
            }

        } catch (OptionException e) {
            System.out.println(e.getMessage());
            parser.printHelpOn(System.out);
        }

    }

    private static String readPassword(String aPasswordFile) throws IOException {
        File file = new File(aPasswordFile);
        FileInputStream in = new FileInputStream(file);
        try {
            Charset charset = Charset.forName("utf-8");

            byte[] buf = new byte[(int) file.length()];
            int count = in.read(buf);
            return new String(buf, 0, count, charset);

        } finally {
            in.close();
        }


    }
}
