
package eu.vital.reply.jsonpojosv2;

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
    "time:inXSDDateTime"
})
public class SsnObservationResultTime {

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

    public SsnObservationResultTime withTimeInXSDDateTime(String timeInXSDDateTime) {
        this.timeInXSDDateTime = timeInXSDDateTime;
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

    public SsnObservationResultTime withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
