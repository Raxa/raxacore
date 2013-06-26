package org.bahmni.openerp.web.request.builder;


public class Parameter {

    private String name;
    private String value;
    private String type;

    public Parameter(String name, String value, String type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parameter parameter = (Parameter) o;

        if (name != null ? !name.equals(parameter.name) : parameter.name != null) return false;
        if (type != null ? !type.equals(parameter.type) : parameter.type != null) return false;
        if (value != null ? !value.equals(parameter.value) : parameter.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    public String getType() {
        return type;

    }

    @Override
    public String toString() {
        return "Parameter{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
