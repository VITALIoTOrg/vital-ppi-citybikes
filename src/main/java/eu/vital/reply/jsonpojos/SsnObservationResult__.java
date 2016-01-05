
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
    "ssn:hasValue"
})
public class SsnObservationResult__ {

    @JsonProperty("type")
    private String type;
    @JsonProperty("ssn:hasValue")
    private SsnHasValue__ ssnHasValue;
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

    public SsnObservationResult__ withType(String type) {
        this.type = type;
        return this;
    }

    /**
     * 
     * @return
     *     The ssnHasValue
     */
    @JsonProperty("ssn:hasValue")
    public SsnHasValue__ getSsnHasValue() {
        return ssnHasValue;
    }

    /**
     * 
     * @param ssnHasValue
     *     The ssn:hasValue
     */
    @JsonProperty("ssn:hasValue")
    public void setSsnHasValue(SsnHasValue__ ssnHasValue) {
        this.ssnHasValue = ssnHasValue;
    }

    public SsnObservationResult__ withSsnHasValue(SsnHasValue__ ssnHasValue) {
        this.ssnHasValue = ssnHasValue;
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

    public SsnObservationResult__ withAdditionalProperty(String name, Object value) {
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
            case "ssn:hasValue":
                if (value instanceof SsnHasValue__) {
                    setSsnHasValue(((SsnHasValue__) value));
                } else {
                    throw new IllegalArgumentException(("property \"ssn:hasValue\" is of type \"eu.vital.reply.jsonpojos.SsnHasValue__\", but got "+ value.getClass().toString()));
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
            case "ssn:hasValue":
                return getSsnHasValue();
            default:
                return notFoundValue;
        }
    }

    @SuppressWarnings({
        "unchecked"
    })
    public<T >T get(String name) {
        Object value = declaredPropertyOrNotFound(name, SsnObservationResult__.NOT_FOUND_VALUE);
        if (SsnObservationResult__.NOT_FOUND_VALUE!= value) {
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
    public SsnObservationResult__ with(String name, Object value) {
        if (!declaredProperty(name, value)) {
            getAdditionalProperties().put(name, ((Object) value));
        }
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(type).append(ssnHasValue).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SsnObservationResult__) == false) {
            return false;
        }
        SsnObservationResult__ rhs = ((SsnObservationResult__) other);
        return new EqualsBuilder().append(type, rhs.type).append(ssnHasValue, rhs.ssnHasValue).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
