
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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "ssn:hasMeasurementProperty"
})
public class SsnObservationQuality_ {

    @JsonProperty("ssn:hasMeasurementProperty")
    private SsnHasMeasurementProperty_ ssnHasMeasurementProperty;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The ssnHasMeasurementProperty
     */
    @JsonProperty("ssn:hasMeasurementProperty")
    public SsnHasMeasurementProperty_ getSsnHasMeasurementProperty() {
        return ssnHasMeasurementProperty;
    }

    /**
     * 
     * @param ssnHasMeasurementProperty
     *     The ssn:hasMeasurementProperty
     */
    @JsonProperty("ssn:hasMeasurementProperty")
    public void setSsnHasMeasurementProperty(SsnHasMeasurementProperty_ ssnHasMeasurementProperty) {
        this.ssnHasMeasurementProperty = ssnHasMeasurementProperty;
    }

    public SsnObservationQuality_ withSsnHasMeasurementProperty(SsnHasMeasurementProperty_ ssnHasMeasurementProperty) {
        this.ssnHasMeasurementProperty = ssnHasMeasurementProperty;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public SsnObservationQuality_ withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
