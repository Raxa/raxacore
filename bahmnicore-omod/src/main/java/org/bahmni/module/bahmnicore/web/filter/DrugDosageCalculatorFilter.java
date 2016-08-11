package org.bahmni.module.bahmnicore.web.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bahmni.module.bahmnicore.extensions.BahmniExtensions;
import org.openmrs.module.rulesengine.domain.DosageRequest;
import org.openmrs.module.rulesengine.rule.DosageRule;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import java.io.File;
import java.io.IOException;

public class DrugDosageCalculatorFilter implements Filter {

    protected final Log log = LogFactory.getLog(getClass());
    private final String rulesEngineExtensionPath = "rulesengine"+ File.separator+"rulesengineextension";

    @Override
    public void init(FilterConfig config) throws ServletException {
        log.debug("Initializing DrugDoseCalculatorFilter");
    }

    @Override
    public void destroy() {
        log.debug("Destroying DrugDoseCalculatorFilter");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        log.debug("Using rules extension path as: "+rulesEngineExtensionPath);
        if (rulesEngineExtensionPath != null) {

            String dosageRequestStr = request.getParameter("dosageRequest");
            DosageRequest dosageRequest = new DosageRequest(dosageRequestStr);

            String name = dosageRequest.getDosingRule() + ".groovy";
            BahmniExtensions extensions = new BahmniExtensions();
            DosageRule rule = (DosageRule) extensions.getExtension(rulesEngineExtensionPath, name);
            if (rule != null) {
                log.debug("Loaded rule extension object: "+name);
                ServletContext servletContext = request.getServletContext();
                WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
                AutowireCapableBeanFactory autowireCapableBeanFactory = webApplicationContext.getAutowireCapableBeanFactory();

                BeanDefinitionRegistry registry = (BeanDefinitionRegistry) autowireCapableBeanFactory;

                GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
                genericBeanDefinition.setBeanClass(rule.getClass());
                genericBeanDefinition.setAutowireCandidate(true);
                registry.registerBeanDefinition(dosageRequest.getDosingRule(), genericBeanDefinition);
                autowireCapableBeanFactory.autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
                log.debug("successfully wired bean: "+name);
            }
        }
        chain.doFilter(request, response);
    }
}
