
package eu.vital.reply.jsonpojos;

import java.util.HashMap;
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
    "name",
    "value"
})
public class Parameter_ {

    @JsonProperty("name")
    private String name;
    @JsonProperty("value")
    private String value;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    protected final static Object NOT_FOUND_VALUE = new Object();

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

    public Parameter_ withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 
     * @return
     *     The value
     */
    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    /**
     * 
     * @param value
     *     The value
     */
    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }

    public Parameter_ withValue(String value) {
        this.value = value;
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

    public Parameter_ withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @SuppressWarnings({
        "unchecked"
    })
    protected boolean declaredProperty(String name, Object value) {
        switch (name) {
            case "name":
                if (value instanceof String) {
                    setName(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"name\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
                }
                return true;
            case "value":
                if (value instanceof String) {
                    setValue(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"value\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
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
            case "name":
                return getName();
            case "value":
                return getValue();
            default:
                return notFoundValue;
        }
    }

    @SuppressWarnings({
        "unchecked"
    })
    public<T >T get(String name) {
        Object value = declaredPropertyOrNotFound(name, Parameter_.NOT_FOUND_VALUE);
        if (Parameter_.NOT_FOUND_VALUE!= value) {
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
    public Parameter_ with(String name, Object value) {
        if (!declaredProperty(name, value)) {
            getAdditionalProperties().put(name, ((Object) value));
        }
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(value).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Parameter_) == false) {
            return false;
        }
        Parameter_ rhs = ((Parameter_) other);
        return new EqualsBuilder().append(name, rhs.name).append(value, rhs.value).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
