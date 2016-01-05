
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
    "type",
    "hasValue"
})
public class SsnHasMeasurementProperty__ {

    @JsonProperty("type")
    private String type;
    @JsonProperty("hasValue")
    private String hasValue;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    protected final static Object NOT_FOUND_VALUE = new Object();

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

    public SsnHasMeasurementProperty__ withType(String type) {
        this.type = type;
        return this;
    }

    /**
     * 
     * @return
     *     The hasValue
     */
    @JsonProperty("hasValue")
    public String getHasValue() {
        return hasValue;
    }

    /**
     * 
     * @param hasValue
     *     The hasValue
     */
    @JsonProperty("hasValue")
    public void setHasValue(String hasValue) {
        this.hasValue = hasValue;
    }

    public SsnHasMeasurementProperty__ withHasValue(String hasValue) {
        this.hasValue = hasValue;
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

    public SsnHasMeasurementProperty__ withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @SuppressWarnings({
        "unchecked"
    })
    protected boolean declaredProperty(String name, Object value) {
        switch (name) {
            case "type":
                if (value instanceof String) {
                    setType(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"type\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
                }
                return true;
            case "hasValue":
                if (value instanceof String) {
                    setHasValue(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"hasValue\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
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
            case "type":
                return getType();
            case "hasValue":
                return getHasValue();
            default:
                return notFoundValue;
        }
    }

    @SuppressWarnings({
        "unchecked"
    })
    public<T >T get(String name) {
        Object value = declaredPropertyOrNotFound(name, SsnHasMeasurementProperty__.NOT_FOUND_VALUE);
        if (SsnHasMeasurementProperty__.NOT_FOUND_VALUE!= value) {
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
    public SsnHasMeasurementProperty__ with(String name, Object value) {
        if (!declaredProperty(name, value)) {
            getAdditionalProperties().put(name, ((Object) value));
        }
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(type).append(hasValue).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SsnHasMeasurementProperty__) == false) {
            return false;
        }
        SsnHasMeasurementProperty__ rhs = ((SsnHasMeasurementProperty__) other);
        return new EqualsBuilder().append(type, rhs.type).append(hasValue, rhs.hasValue).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
