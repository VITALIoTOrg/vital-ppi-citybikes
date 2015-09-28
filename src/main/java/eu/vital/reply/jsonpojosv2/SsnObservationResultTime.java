
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
    "inXSDDateTime"
})
public class SsnObservationResultTime {

    @JsonProperty("inXSDDateTime")
    private String inXSDDateTime;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The inXSDDateTime
     */
    @JsonProperty("inXSDDateTime")
    public String getInXSDDateTime() {
        return inXSDDateTime;
    }

    /**
     * 
     * @param inXSDDateTime
     *     The inXSDDateTime
     */
    @JsonProperty("inXSDDateTime")
    public void setInXSDDateTime(String inXSDDateTime) {
        this.inXSDDateTime = inXSDDateTime;
    }

    public SsnObservationResultTime withInXSDDateTime(String inXSDDateTime) {
        this.inXSDDateTime = inXSDDateTime;
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
