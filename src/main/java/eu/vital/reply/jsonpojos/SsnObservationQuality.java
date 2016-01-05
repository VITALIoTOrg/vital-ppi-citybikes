
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
    "ssn:hasMeasurementProperty"
})
public class SsnObservationQuality {

    @JsonProperty("ssn:hasMeasurementProperty")
    private SsnHasMeasurementProperty ssnHasMeasurementProperty;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    protected final static Object NOT_FOUND_VALUE = new Object();

    /**
     * 
     * @return
     *     The ssnHasMeasurementProperty
     */
    @JsonProperty("ssn:hasMeasurementProperty")
    public SsnHasMeasurementProperty getSsnHasMeasurementProperty() {
        return ssnHasMeasurementProperty;
    }

    /**
     * 
     * @param ssnHasMeasurementProperty
     *     The ssn:hasMeasurementProperty
     */
    @JsonProperty("ssn:hasMeasurementProperty")
    public void setSsnHasMeasurementProperty(SsnHasMeasurementProperty ssnHasMeasurementProperty) {
        this.ssnHasMeasurementProperty = ssnHasMeasurementProperty;
    }

    public SsnObservationQuality withSsnHasMeasurementProperty(SsnHasMeasurementProperty ssnHasMeasurementProperty) {
        this.ssnHasMeasurementProperty = ssnHasMeasurementProperty;
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

    public SsnObservationQuality withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @SuppressWarnings({
        "unchecked"
    })
    protected boolean declaredProperty(String name, Object value) {
        switch (name) {
            case "ssn:hasMeasurementProperty":
                if (value instanceof SsnHasMeasurementProperty) {
                    setSsnHasMeasurementProperty(((SsnHasMeasurementProperty) value));
                } else {
                    throw new IllegalArgumentException(("property \"ssn:hasMeasurementProperty\" is of type \"eu.vital.reply.jsonpojos.SsnHasMeasurementProperty\", but got "+ value.getClass().toString()));
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
            case "ssn:hasMeasurementProperty":
                return getSsnHasMeasurementProperty();
            default:
                return notFoundValue;
        }
    }

    @SuppressWarnings({
        "unchecked"
    })
    public<T >T get(String name) {
        Object value = declaredPropertyOrNotFound(name, SsnObservationQuality.NOT_FOUND_VALUE);
        if (SsnObservationQuality.NOT_FOUND_VALUE!= value) {
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
    public SsnObservationQuality with(String name, Object value) {
        if (!declaredProperty(name, value)) {
            getAdditionalProperties().put(name, ((Object) value));
        }
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(ssnHasMeasurementProperty).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SsnObservationQuality) == false) {
            return false;
        }
        SsnObservationQuality rhs = ((SsnObservationQuality) other);
        return new EqualsBuilder().append(ssnHasMeasurementProperty, rhs.ssnHasMeasurementProperty).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
