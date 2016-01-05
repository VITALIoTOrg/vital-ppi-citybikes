
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
    "time:inXSDDateTime"
})
public class SsnObservationResultTime__ {

    @JsonProperty("time:inXSDDateTime")
    private String timeInXSDDateTime;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    protected final static Object NOT_FOUND_VALUE = new Object();

    /**
     * 
     * @return
     *     The timeInXSDDateTime
     */
    @JsonProperty("time:inXSDDateTime")
    public String getTimeInXSDDateTime() {
        return timeInXSDDateTime;
    }

    /**
     * 
     * @param timeInXSDDateTime
     *     The time:inXSDDateTime
     */
    @JsonProperty("time:inXSDDateTime")
    public void setTimeInXSDDateTime(String timeInXSDDateTime) {
        this.timeInXSDDateTime = timeInXSDDateTime;
    }

    public SsnObservationResultTime__ withTimeInXSDDateTime(String timeInXSDDateTime) {
        this.timeInXSDDateTime = timeInXSDDateTime;
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

    public SsnObservationResultTime__ withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @SuppressWarnings({
        "unchecked"
    })
    protected boolean declaredProperty(String name, Object value) {
        switch (name) {
            case "time:inXSDDateTime":
                if (value instanceof String) {
                    setTimeInXSDDateTime(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"time:inXSDDateTime\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
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
            case "time:inXSDDateTime":
                return getTimeInXSDDateTime();
            default:
                return notFoundValue;
        }
    }

    @SuppressWarnings({
        "unchecked"
    })
    public<T >T get(String name) {
        Object value = declaredPropertyOrNotFound(name, SsnObservationResultTime__.NOT_FOUND_VALUE);
        if (SsnObservationResultTime__.NOT_FOUND_VALUE!= value) {
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
    public SsnObservationResultTime__ with(String name, Object value) {
        if (!declaredProperty(name, value)) {
            getAdditionalProperties().put(name, ((Object) value));
        }
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(timeInXSDDateTime).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SsnObservationResultTime__) == false) {
            return false;
        }
        SsnObservationResultTime__ rhs = ((SsnObservationResultTime__) other);
        return new EqualsBuilder().append(timeInXSDDateTime, rhs.timeInXSDDateTime).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
