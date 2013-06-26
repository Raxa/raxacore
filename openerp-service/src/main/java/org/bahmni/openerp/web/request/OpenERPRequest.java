package org.bahmni.openerp.web.request;

import org.bahmni.openerp.web.request.builder.Parameter;

import java.util.List;

public class OpenERPRequest {
    private final String resource;
    private final String operation;
    private final List<Parameter> parameters;

    public OpenERPRequest(String resource, String operation, List<Parameter> parameters) {
        this.resource = resource;
        this.operation = operation;
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "OpenERPRequest{" +
                "resource='" + resource + '\'' +
                ", operation='" + operation + '\'' +
                ", parameters=" + parameters +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OpenERPRequest request = (OpenERPRequest) o;

        if (!operation.equals(request.operation)) return false;
        if (!parameters.equals(request.parameters)) return false;
        if (!resource.equals(request.resource)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = resource.hashCode();
        result = 31 * result + operation.hashCode();
        result = 31 * result + parameters.hashCode();
        return result;
    }

    public String getResource() {
        return resource;
    }

    public String getOperation() {
        return operation;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }
}
