
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
    "sensor",
    "property",
    "from",
    "to"
})
public class ObservationRequest {

    @JsonProperty("sensor")
    private List<String> sensor = new ArrayList<String>();
    @JsonProperty("property")
    private String property;
    @JsonProperty("from")
    private String from;
    @JsonProperty("to")
    private String to;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    protected final static Object NOT_FOUND_VALUE = new Object();

    /**
     * 
     * @return
     *     The sensor
     */
    @JsonProperty("sensor")
    public List<String> getSensor() {
        return sensor;
    }

    /**
     * 
     * @param sensor
     *     The sensor
     */
    @JsonProperty("sensor")
    public void setSensor(List<String> sensor) {
        this.sensor = sensor;
    }

    public ObservationRequest withSensor(List<String> sensor) {
        this.sensor = sensor;
        return this;
    }

    /**
     * 
     * @return
     *     The property
     */
    @JsonProperty("property")
    public String getProperty() {
        return property;
    }

    /**
     * 
     * @param property
     *     The property
     */
    @JsonProperty("property")
    public void setProperty(String property) {
        this.property = property;
    }

    public ObservationRequest withProperty(String property) {
        this.property = property;
        return this;
    }

    /**
     * 
     * @return
     *     The from
     */
    @JsonProperty("from")
    public String getFrom() {
        return from;
    }

    /**
     * 
     * @param from
     *     The from
     */
    @JsonProperty("from")
    public void setFrom(String from) {
        this.from = from;
    }

    public ObservationRequest withFrom(String from) {
        this.from = from;
        return this;
    }

    /**
     * 
     * @return
     *     The to
     */
    @JsonProperty("to")
    public String getTo() {
        return to;
    }

    /**
     * 
     * @param to
     *     The to
     */
    @JsonProperty("to")
    public void setTo(String to) {
        this.to = to;
    }

    public ObservationRequest withTo(String to) {
        this.to = to;
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

    public ObservationRequest withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @SuppressWarnings({
        "unchecked"
    })
    protected boolean declaredProperty(String name, Object value) {
        switch (name) {
            case "sensor":
                if (value instanceof List) {
                    setSensor(((List<String> ) value));
                } else {
                    throw new IllegalArgumentException(("property \"sensor\" is of type \"java.util.List<java.lang.String>\", but got "+ value.getClass().toString()));
                }
                return true;
            case "property":
                if (value instanceof String) {
                    setProperty(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"property\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
                }
                return true;
            case "from":
                if (value instanceof String) {
                    setFrom(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"from\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
                }
                return true;
            case "to":
                if (value instanceof String) {
                    setTo(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"to\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
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
            case "sensor":
                return getSensor();
            case "property":
                return getProperty();
            case "from":
                return getFrom();
            case "to":
                return getTo();
            default:
                return notFoundValue;
        }
    }

    @SuppressWarnings({
        "unchecked"
    })
    public<T >T get(String name) {
        Object value = declaredPropertyOrNotFound(name, ObservationRequest.NOT_FOUND_VALUE);
        if (ObservationRequest.NOT_FOUND_VALUE!= value) {
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
    public ObservationRequest with(String name, Object value) {
        if (!declaredProperty(name, value)) {
            getAdditionalProperties().put(name, ((Object) value));
        }
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(sensor).append(property).append(from).append(to).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ObservationRequest) == false) {
            return false;
        }
        ObservationRequest rhs = ((ObservationRequest) other);
        return new EqualsBuilder().append(sensor, rhs.sensor).append(property, rhs.property).append(from, rhs.from).append(to, rhs.to).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
