package org.bahmni.openerp.web.client;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.bahmni.openerp.web.http.client.HttpClient;
import org.bahmni.openerp.web.request.builder.RequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

@Service
public class OpenERPClient {

    public @Value("${host}") String host;
    public @Value("${port}") int port;
    public @Value("${database}") String database;
    public @Value("${user}") String user;
    public @Value("${password}") String password;

    Object id ;
    XmlRpcClient xmlRpcClient ;
    RequestBuilder requestBuilder;
    HttpClient httpClient;

    @Autowired
    public OpenERPClient(RequestBuilder requestBuilder,HttpClient httpClient) throws Exception {
        this.requestBuilder = requestBuilder;
        this.httpClient = httpClient;
    }
    public OpenERPClient(String host, int port, String database, String user, String password) throws Exception {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    private Object login() throws Exception {
        XmlRpcClient loginRpcClient = createRPCClient(host, port, "/xmlrpc/common");
        Vector params = new Vector();
        params.addElement(database);
        params.addElement(user);
        params.addElement(password);

        return loginRpcClient.execute("login", params);
    }

    public Object search(String resource, Vector params) throws Exception {
        return execute(resource,"search",params) ;
    }

    public Object create(String resource, String name,String patientId) throws Exception {
        if(id == null)
            id = login();
        String request = requestBuilder.buildNewCustomerRequest(name, patientId, id, database, password, resource, "create");
        return httpClient.post("http://"+host+":"+ port+ "/xmlrpc/object",request);
    }

    public Object delete(String resource, Vector params) throws Exception {
        return execute(resource,"unlink",params) ;
    }

    public Object execute(String resource, String operation, Vector params) throws Exception {
        if(id == null)
            id = login();
        Object args[]={database,(Integer)id,password,resource,operation,params};

        return xmlRpcClient().execute("execute", args);
    }

    private XmlRpcClient xmlRpcClient() throws Exception {
        if(this.xmlRpcClient == null)
            this.xmlRpcClient = createRPCClient(host, port, "/xmlrpc/object");
        return  this.xmlRpcClient;
    }

    private XmlRpcClient createRPCClient(String host, int port,String endpoint) throws MalformedURLException {
        XmlRpcClientConfigImpl rpc = new XmlRpcClientConfigImpl();
        rpc.setEnabledForExtensions(true);
        rpc.setEnabledForExceptions(true);
        rpc.setServerURL(new URL("http", host, port, endpoint));

        XmlRpcClient rpcClient = new XmlRpcClient();
        rpcClient.setConfig(rpc);

        return rpcClient;
    }

}
