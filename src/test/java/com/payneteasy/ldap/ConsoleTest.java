package com.payneteasy.ldap;

import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: es
 * Date: 12/20/12
 * Time: 11:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class ConsoleTest {

    @Test
    public void extractArguments() {
        String[] arguments = Console.extractArguments("user list --all --name=\"Test Argument'");
        for (String argument : arguments) {
            System.out.println("argument = " + argument);
        }
    }
}
