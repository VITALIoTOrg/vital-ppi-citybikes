
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
    "value",
    "qudt:unit"
})
public class SsnHasValue {

    @JsonProperty("type")
    private String type;
    @JsonProperty("value")
    private Integer value;
    @JsonProperty("qudt:unit")
    private String qudtUnit;
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

    public SsnHasValue withType(String type) {
        this.type = type;
        return this;
    }

    /**
     * 
     * @return
     *     The value
     */
    @JsonProperty("value")
    public Integer getValue() {
        return value;
    }

    /**
     * 
     * @param value
     *     The value
     */
    @JsonProperty("value")
    public void setValue(Integer value) {
        this.value = value;
    }

    public SsnHasValue withValue(Integer value) {
        this.value = value;
        return this;
    }

    /**
     * 
     * @return
     *     The qudtUnit
     */
    @JsonProperty("qudt:unit")
    public String getQudtUnit() {
        return qudtUnit;
    }

    /**
     * 
     * @param qudtUnit
     *     The qudt:unit
     */
    @JsonProperty("qudt:unit")
    public void setQudtUnit(String qudtUnit) {
        this.qudtUnit = qudtUnit;
    }

    public SsnHasValue withQudtUnit(String qudtUnit) {
        this.qudtUnit = qudtUnit;
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

    public SsnHasValue withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(type).append(value).append(qudtUnit).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SsnHasValue) == false) {
            return false;
        }
        SsnHasValue rhs = ((SsnHasValue) other);
        return new EqualsBuilder().append(type, rhs.type).append(value, rhs.value).append(qudtUnit, rhs.qudtUnit).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
