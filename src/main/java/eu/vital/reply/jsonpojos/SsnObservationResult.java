
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
public class SsnObservationResult {

    @JsonProperty("type")
    private String type;
    @JsonProperty("ssn:hasValue")
    private SsnHasValue ssnHasValue;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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

    public SsnObservationResult withType(String type) {
        this.type = type;
        return this;
    }

    /**
     * 
     * @return
     *     The ssnHasValue
     */
    @JsonProperty("ssn:hasValue")
    public SsnHasValue getSsnHasValue() {
        return ssnHasValue;
    }

    /**
     * 
     * @param ssnHasValue
     *     The ssn:hasValue
     */
    @JsonProperty("ssn:hasValue")
    public void setSsnHasValue(SsnHasValue ssnHasValue) {
        this.ssnHasValue = ssnHasValue;
    }

    public SsnObservationResult withSsnHasValue(SsnHasValue ssnHasValue) {
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

    public SsnObservationResult withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
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
        if ((other instanceof SsnObservationResult) == false) {
            return false;
        }
        SsnObservationResult rhs = ((SsnObservationResult) other);
        return new EqualsBuilder().append(type, rhs.type).append(ssnHasValue, rhs.ssnHasValue).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
