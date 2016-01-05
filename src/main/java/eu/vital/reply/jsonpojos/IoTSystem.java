
package eu.vital.reply.jsonpojos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "@context",
    "id",
    "type",
    "name",
    "description",
    "operator",
    "serviceArea",
    "sensors",
    "services",
    "status"
})
public class IoTSystem {

    @JsonProperty("@context")
    private String Context;
    @JsonProperty("id")
    private String id;
    @JsonProperty("type")
    private String type;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("operator")
    private String operator;
    @JsonProperty("serviceArea")
    private String serviceArea;
    @JsonProperty("sensors")
    private List<String> sensors = new ArrayList<String>();
    @JsonProperty("services")
    private List<String> services = new ArrayList<String>();
    @JsonProperty("status")
    private String status;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    protected final static Object NOT_FOUND_VALUE = new Object();

    /**
     * 
     * @return
     *     The Context
     */
    @JsonProperty("@context")
    public String getContext() {
        return Context;
    }

    /**
     * 
     * @param Context
     *     The @context
     */
    @JsonProperty("@context")
    public void setContext(String Context) {
        this.Context = Context;
    }

    public IoTSystem withContext(String Context) {
        this.Context = Context;
        return this;
    }

    /**
     * 
     * @return
     *     The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    public IoTSystem withId(String id) {
        this.id = id;
        return this;
    }

    /**
     * 
     * @return
     *     The type
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    public IoTSystem withType(String type) {
        this.type = type;
        return this;
    }

    /**
     * 
     * @return
     *     The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public IoTSystem withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 
     * @return
     *     The description
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     * 
     * @param description
     *     The description
     */
    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    public IoTSystem withDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * 
     * @return
     *     The operator
     */
    @JsonProperty("operator")
    public String getOperator() {
        return operator;
    }

    /**
     * 
     * @param operator
     *     The operator
     */
    @JsonProperty("operator")
    public void setOperator(String operator) {
        this.operator = operator;
    }

    public IoTSystem withOperator(String operator) {
        this.operator = operator;
        return this;
    }

    /**
     * 
     * @return
     *     The serviceArea
     */
    @JsonProperty("serviceArea")
    public String getServiceArea() {
        return serviceArea;
    }

    /**
     * 
     * @param serviceArea
     *     The serviceArea
     */
    @JsonProperty("serviceArea")
    public void setServiceArea(String serviceArea) {
        this.serviceArea = serviceArea;
    }

    public IoTSystem withServiceArea(String serviceArea) {
        this.serviceArea = serviceArea;
        return this;
    }

    /**
     * 
     * @return
     *     The sensors
     */
    @JsonProperty("sensors")
    public List<String> getSensors() {
        return sensors;
    }

    /**
     * 
     * @param sensors
     *     The sensors
     */
    @JsonProperty("sensors")
    public void setSensors(List<String> sensors) {
        this.sensors = sensors;
    }

    public IoTSystem withSensors(List<String> sensors) {
        this.sensors = sensors;
        return this;
    }

    /**
     * 
     * @return
     *     The services
     */
    @JsonProperty("services")
    public List<String> getServices() {
        return services;
    }

    /**
     * 
     * @param services
     *     The services
     */
    @JsonProperty("services")
    public void setServices(List<String> services) {
        this.services = services;
    }

    public IoTSystem withServices(List<String> services) {
        this.services = services;
        return this;
    }

    /**
     * 
     * @return
     *     The status
     */
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */
    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    public IoTSystem withStatus(String status) {
        this.status = status;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public IoTSystem withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @SuppressWarnings({
        "unchecked"
    })
    protected boolean declaredProperty(String name, Object value) {
        switch (name) {
            case "@context":
                if (value instanceof String) {
                    setContext(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"@context\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
                }
                return true;
            case "id":
                if (value instanceof String) {
                    setId(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"id\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
                }
                return true;
            case "type":
                if (value instanceof String) {
                    setType(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"type\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
                }
                return true;
            case "name":
                if (value instanceof String) {
                    setName(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"name\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
                }
                return true;
            case "description":
                if (value instanceof String) {
                    setDescription(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"description\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
                }
                return true;
            case "operator":
                if (value instanceof String) {
                    setOperator(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"operator\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
                }
                return true;
            case "serviceArea":
                if (value instanceof String) {
                    setServiceArea(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"serviceArea\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
                }
                return true;
            case "sensors":
                if (value instanceof List) {
                    setSensors(((List<String> ) value));
                } else {
                    throw new IllegalArgumentException(("property \"sensors\" is of type \"java.util.List<java.lang.String>\", but got "+ value.getClass().toString()));
                }
                return true;
            case "services":
                if (value instanceof List) {
                    setServices(((List<String> ) value));
                } else {
                    throw new IllegalArgumentException(("property \"services\" is of type \"java.util.List<java.lang.String>\", but got "+ value.getClass().toString()));
                }
                return true;
            case "status":
                if (value instanceof String) {
                    setStatus(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"status\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
                }
                return true;
            default:
                return false;
        }
    }

    @SuppressWarnings({
        "unchecked"
    })
    protected Object declaredPropertyOrNotFound(String name, Object notFoundValue) {
        switch (name) {
            case "@context":
                return getContext();
            case "id":
                return getId();
            case "type":
                return getType();
            case "name":
                return getName();
            case "description":
                return getDescription();
            case "operator":
                return getOperator();
            case "serviceArea":
                return getServiceArea();
            case "sensors":
                return getSensors();
            case "services":
                return getServices();
            case "status":
                return getStatus();
            default:
                return notFoundValue;
        }
    }

    @SuppressWarnings({
        "unchecked"
    })
    public<T >T get(String name) {
        Object value = declaredPropertyOrNotFound(name, IoTSystem.NOT_FOUND_VALUE);
        if (IoTSystem.NOT_FOUND_VALUE!= value) {
            return ((T) value);
        } else {
            return ((T) getAdditionalProperties().get(name));
        }
    }

    @SuppressWarnings({
        "unchecked"
    })
    public void set(String name, Object value) {
        if (!declaredProperty(name, value)) {
            getAdditionalProperties().put(name, ((Object) value));
        }
    }

    @SuppressWarnings({
        "unchecked"
    })
    public IoTSystem with(String name, Object value) {
        if (!declaredProperty(name, value)) {
            getAdditionalProperties().put(name, ((Object) value));
        }
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(Context).append(id).append(type).append(name).append(description).append(operator).append(serviceArea).append(sensors).append(services).append(status).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof IoTSystem) == false) {
            return false;
        }
        IoTSystem rhs = ((IoTSystem) other);
        return new EqualsBuilder().append(Context, rhs.Context).append(id, rhs.id).append(type, rhs.type).append(name, rhs.name).append(description, rhs.description).append(operator, rhs.operator).append(serviceArea, rhs.serviceArea).append(sensors, rhs.sensors).append(services, rhs.services).append(status, rhs.status).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
