package com.payneteasy.ldap.users.util;


import org.junit.Assert;
import org.junit.Test;

public class PasswordGeneratorTest {

    @Test
    public void test() {
        String password = PasswordGenerator.createPassword();
        System.out.println("password.length() = " + password.length());
        System.out.println("password = " + password);

        Assert.assertTrue("Password must be more than 8 characters", password.length() > 8);
    }
}
