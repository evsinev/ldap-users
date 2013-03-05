ldap-users
==========

Manage ldap users from console shell

Building project
----------------

```bash
mvn clean install assembly:single
```

Running ldap-users shell
---------------------

```bash
java \
  -Djavax.net.ssl.trustStore=trust-store.jks \
  -Djavax.net.ssl.trustPassword=STORE_PASSWORD \
  -jar target/users-1.0-SNAPSHOT-jar-with-dependencies.jar \
  -url=ldaps://localhost:636/ \
  -username="cn=admin,ou=users,dc=example,dc=com" \
  -password=LDAP_PASSWORD \
  -users-dn="ou=users,dc=example,dc=com" \
  -groups-dn="ou=groups,dc=example,dc=com"
```

Available commadns
------------------

* `user list`     Show all users list
* `user info`     Show detailed user info
* `user reset`    Reset user password 
* `user unlock`   Unlock user
* `user disable`  Lock user 
* `user host`     Add or remove host to user
* `group list`    Show all groups
* `policies info` Show ppolicy overlap settings

