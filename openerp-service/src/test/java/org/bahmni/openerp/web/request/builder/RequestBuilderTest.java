package org.bahmni.openerp.web.request.builder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:applicationContext-openerp-service.xml")
public class RequestBuilderTest {

    @Autowired
    RequestBuilder requestBuilder;

    @Test
    public void shouldCreateNewCustomerRequestWithPatientDataPopulated() throws Exception {

        String patientName="Ramu";
        String patientId="13466";
        int id = 1;
        String database="openerp";
        String password="password";
        String resource="res.partner";
        String operation="create";
        String requestXml = requestBuilder.buildNewCustomerRequest(patientName, patientId, id, database, password, resource, operation);
        //String requestXmlForComparison = requestXml.replace("\n", " ");

        assertEquals("<?xml version='1.0'?>\n" +
                "<methodCall>\n" +
                "    <methodName>execute</methodName>\n" +
                "    <params>\n" +
                "        <param>\n" +
                "        <value><string>"+database+"</string></value>\n" +
                "        </param>\n" +
                "        <param>\n" +
                "        <value><int>"+id+"</int></value>\n" +
                "        </param>\n" +
                "        <param>\n" +
                "        <value><string>"+password+"</string></value>\n" +
                "        </param>\n" +
                "        <param>\n" +
                "        <value><string>"+resource+"</string></value>\n" +
                "        </param>\n" +
                "        <param>\n" +
                "        <value><string>"+operation+"</string></value>\n" +
                "        </param>\n" +
                "        <param>\n" +
                "        <value><struct>\n" +
                "            <member>\n" +
                "                <name>name</name>\n" +
                "                <value><string>"+patientName+"</string></value>\n" +
                "            </member>\n" +
                "            <member>\n" +
                "                <name>ref</name>\n" +
                "                <value><string>"+patientId+"</string></value>\n" +
                "            </member>\n" +
                "        </struct></value>\n" +
                "        </param>\n" +
                "    </params>\n" +
                "</methodCall>\n",requestXml);
    }
}
