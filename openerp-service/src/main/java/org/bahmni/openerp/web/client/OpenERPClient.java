package org.bahmni.openerp.web.client;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.bahmni.openerp.web.OpenERPException;
import org.bahmni.openerp.web.OpenERPProperties;
import org.bahmni.openerp.web.http.client.HttpClient;
import org.bahmni.openerp.web.request.builder.RequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

@Service
@Lazy
public class OpenERPClient {
    private String host;
    private int port;
    private String database;
    private String user;
    private String password;

    Object id;
    XmlRpcClient xmlRpcClient;
    RequestBuilder requestBuilder;
    HttpClient httpClient;

    @Autowired
    public OpenERPClient(RequestBuilder requestBuilder, HttpClient httpClient, OpenERPProperties openERPProperties) {
        this.requestBuilder = requestBuilder;
        this.httpClient = httpClient;
        host = openERPProperties.getHost();
        port = openERPProperties.getPort();
        database = openERPProperties.getDatabase();
        user = openERPProperties.getUser();
        password = openERPProperties.getPassword();
    }

    private Object login() {
        XmlRpcClient loginRpcClient = createRPCClient(host, port, "/xmlrpc/common");
        Vector params = new Vector();
        params.addElement(database);
        params.addElement(user);
        params.addElement(password);

        return executeRPC(loginRpcClient, params, "login");
    }

    private Object executeRPC(XmlRpcClient loginRpcClient, Vector params, String methodName) {
        try {
            return loginRpcClient.execute(methodName, params);
        } catch (XmlRpcException e) {
            throw new OpenERPException(e);
        }
    }

    public Object search(String resource, Vector params) {
        return execute(resource, "search", params);
    }

    public Object create(String resource, String name, String patientId) {
        if (id == null)
            id = login();
        String request = requestBuilder.buildNewCustomerRequest(name, patientId, id, database, password, resource, "create");
        return httpClient.post("http://" + host + ":" + port + "/xmlrpc/object", request);
    }

    public Object delete(String resource, Vector params) {
        return execute(resource, "unlink", params);
    }

    public Object execute(String resource, String operation, Vector params) {
        if (id == null)
            id = login();
        Object args[] = {database, (Integer) id, password, resource, operation, params};

        try {
            return xmlRpcClient().execute("execute", args);
        } catch (XmlRpcException e) {
            throw new OpenERPException(e);
        }
    }

    private XmlRpcClient xmlRpcClient() {
        if (this.xmlRpcClient == null)
            this.xmlRpcClient = createRPCClient(host, port, "/xmlrpc/object");
        return this.xmlRpcClient;
    }

    private XmlRpcClient createRPCClient(String host, int port, String endpoint) {
        try {
            XmlRpcClientConfigImpl rpc = new XmlRpcClientConfigImpl();
            rpc.setEnabledForExtensions(true);
            rpc.setEnabledForExceptions(true);
            rpc.setServerURL(new URL("http", host, port, endpoint));

            XmlRpcClient rpcClient = new XmlRpcClient();
            rpcClient.setConfig(rpc);

            return rpcClient;
        } catch (MalformedURLException e) {
            throw new OpenERPException(e);
        }
    }

    public Object updateCustomerReceivables(String resource, Vector params) {
        return execute(resource, "update_customer_receivables", params);
    }

}
