package org.bahmni.openerp.web.request.builder;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.bahmni.openerp.web.OpenERPException;
import org.bahmni.openerp.web.request.OpenERPRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;

import java.io.StringWriter;
import java.util.List;

@Service
public class RequestBuilder {
    private VelocityConfigurer configurer;

    @Autowired
    public RequestBuilder(VelocityConfigurer configurer) {
        this.configurer = configurer;
    }


    public String buildNewRequest(OpenERPRequest openERPRequest, Object id, String database, String password) {
        try {
            VelocityEngine velocityEngine = configurer.getVelocityEngine();
            Template template = velocityEngine.getTemplate("/request/template/new_customer.vm");
            VelocityContext context = new VelocityContext();
            context.put("parametersList", openERPRequest.getParameters());
            context.put("id", id);
            context.put("database", database);
            context.put("password", password);
            context.put("resource", openERPRequest.getResource());
            context.put("operation", openERPRequest.getOperation());

            StringWriter writer = new StringWriter();
            template.merge(context, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new OpenERPException(e);
        }
    }
}