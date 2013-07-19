package org.bahmni.openerp.web;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public interface OpenERPProperties {
    public String getHost();
    public int getPort();
    public String getDatabase();
    public String getUser();
    public String getPassword();
    public int getConnectionTimeoutInMilliseconds();
    public int getReplyTimeoutInMilliseconds();
}
