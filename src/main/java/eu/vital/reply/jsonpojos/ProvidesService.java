
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
    "type",
    "msm:hasOperation"
})
public class ProvidesService {

    @JsonProperty("type")
    private String type;
    @JsonProperty("msm:hasOperation")
    private MsmHasOperation msmHasOperation;
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

    public ProvidesService withType(String type) {
        this.type = type;
        return this;
    }

    /**
     * 
     * @return
     *     The msmHasOperation
     */
    @JsonProperty("msm:hasOperation")
    public MsmHasOperation getMsmHasOperation() {
        return msmHasOperation;
    }

    /**
     * 
     * @param msmHasOperation
     *     The msm:hasOperation
     */
    @JsonProperty("msm:hasOperation")
    public void setMsmHasOperation(MsmHasOperation msmHasOperation) {
        this.msmHasOperation = msmHasOperation;
    }

    public ProvidesService withMsmHasOperation(MsmHasOperation msmHasOperation) {
        this.msmHasOperation = msmHasOperation;
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

    public ProvidesService withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
