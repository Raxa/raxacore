package org.bahmni.openerp.web;

public class OpenERPPropertiesStub implements OpenERPProperties{
    @Override
    public String getHost() {
        return "172.18.2.12";
    }

    @Override
    public int getPort() {
        return 8069;
    }

    @Override
    public String getDatabase() {
        return "openerp";
    }

    @Override
    public String getUser() {
        return "admin";
    }

    @Override
    public String getPassword() {
        return "password";
    }

    @Override
    public int getConnectionTimeoutInMilliseconds() {
        return 0;
    }

    @Override
    public int getReplyTimeoutInMilliseconds() {
        return 0;
    }
}
