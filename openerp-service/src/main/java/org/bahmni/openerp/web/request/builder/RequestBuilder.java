package org.bahmni.openerp.web.request.builder;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Service
public class RequestBuilder {
    private VelocityEngine velocityEngine;

    @Autowired
    public RequestBuilder(VelocityEngine velocityEngine){
        this.velocityEngine = velocityEngine;
    }

    public String buildNewCustomerRequest(String patientName,String patientId,Object id,String database,
                                          String password,String resource,String operation) {
        Template template = velocityEngine.getTemplate("/request/template/new_customer.vm");
        VelocityContext context = new VelocityContext();
        context.put("name", patientName);
        context.put("patientId", patientId);
        context.put("id", id);
        context.put("database", database);
        context.put("password", password);
        context.put("resource", resource);
        context.put("operation", operation);

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }
}
