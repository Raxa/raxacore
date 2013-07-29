package org.bahmni.openerp.web.client;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcSun15HttpTransportFactory;
import org.bahmni.openerp.web.OpenERPException;
import org.bahmni.openerp.web.OpenERPProperties;
import org.bahmni.openerp.web.http.client.HttpClient;
import org.bahmni.openerp.web.request.OpenERPRequest;
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
    public static final String XML_RPC_OBJECT_ENDPOINT = "/xmlrpc/object";
    public static final String XML_RPC_COMMON_ENDPOINT = "/xmlrpc/common";

    private final int connectionTimeoutInMilliseconds;
    private final int replyTimeoutInMilliseconds;
    private String host;
    private int port;
    private String database;
    private String user;
    private String password;

    private Object id;
    private RequestBuilder requestBuilder;

    private XmlRpcClient xmlRpcClient;
    private HttpClient httpClient;

    @Autowired
    public OpenERPClient(RequestBuilder requestBuilder, HttpClient httpClient, OpenERPProperties openERPProperties) {
        this.requestBuilder = requestBuilder;
        this.httpClient = httpClient;
        host = openERPProperties.getHost();
        port = openERPProperties.getPort();
        database = openERPProperties.getDatabase();
        user = openERPProperties.getUser();
        password = openERPProperties.getPassword();
        connectionTimeoutInMilliseconds = openERPProperties.getConnectionTimeoutInMilliseconds();
        replyTimeoutInMilliseconds = openERPProperties.getReplyTimeoutInMilliseconds();
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

    public String execute(OpenERPRequest openERPRequest) {
        login();
        String request = requestBuilder.buildNewRequest(openERPRequest, id, database, password);
        return httpClient().post("http://" + host + ":" + port + XML_RPC_OBJECT_ENDPOINT, request);
    }

    private void login() {
        if (id == null) {
            XmlRpcClient loginRpcClient = xmlRpcClient(XML_RPC_COMMON_ENDPOINT);

            Vector params = new Vector();
            params.addElement(database);
            params.addElement(user);
            params.addElement(password);

            Object loginId = executeRPC(loginRpcClient, params, "login");
            if(loginId == null || loginId.getClass() != Integer.class)
                throw new OpenERPException(String.format("Failed to login. The login id is : %s", loginId));
            id = loginId;
        }
    }

    public Object delete(String resource, Vector params) {
        return execute(resource, "unlink", params);
    }

    public Object execute(String resource, String operation, Vector params) {
        login();
        Object args[] = {database, (Integer) id, password, resource, operation, params};

        try {
            return xmlRpcClient(XML_RPC_OBJECT_ENDPOINT).execute("execute", args);
        } catch (XmlRpcException e) {
            throw new OpenERPException(e);
        }
    }

    public Object updateCustomerReceivables(String resource, Vector params) {
        return execute(resource, "update_customer_receivables", params);
    }

    private HttpClient httpClient() {
        httpClient.setTimeout(replyTimeoutInMilliseconds);
        return httpClient;
    }

    private XmlRpcClient xmlRpcClient(String endpoint) {
        if (xmlRpcClient == null) {
            xmlRpcClient = createRPCClient();
        }
        XmlRpcClientConfigImpl clientConfig = (XmlRpcClientConfigImpl) xmlRpcClient.getClientConfig();
        try {
            clientConfig.setServerURL(new URL("http", host, port, endpoint));
        } catch (MalformedURLException e) {
            throw new OpenERPException(e);
        }
        return xmlRpcClient;
    }

    private XmlRpcClient createRPCClient() {
        XmlRpcClientConfigImpl clientConfiguration = new XmlRpcClientConfigImpl();
        clientConfiguration.setEnabledForExtensions(true);
        clientConfiguration.setEnabledForExceptions(true);
        clientConfiguration.setConnectionTimeout(connectionTimeoutInMilliseconds);
        clientConfiguration.setReplyTimeout(replyTimeoutInMilliseconds);

        XmlRpcClient rpcClient = new XmlRpcClient();
        rpcClient.setTransportFactory(new XmlRpcSun15HttpTransportFactory(rpcClient));
        rpcClient.setConfig(clientConfiguration);
        return rpcClient;
    }

}
