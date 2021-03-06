
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
public class SsnObservationResultTime_ {

    @JsonProperty("time:inXSDDateTime")
    private String timeInXSDDateTime;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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

    public SsnObservationResultTime_ withTimeInXSDDateTime(String timeInXSDDateTime) {
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

    public SsnObservationResultTime_ withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
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
        if ((other instanceof SsnObservationResultTime_) == false) {
            return false;
        }
        SsnObservationResultTime_ rhs = ((SsnObservationResultTime_) other);
        return new EqualsBuilder().append(timeInXSDDateTime, rhs.timeInXSDDateTime).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
