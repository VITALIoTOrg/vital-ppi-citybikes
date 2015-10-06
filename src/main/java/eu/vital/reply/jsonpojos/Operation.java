
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
    "hrest:hasAddress",
    "hrest:hasMethod"
})
public class Operation {

    @JsonProperty("type")
    private String type;
    @JsonProperty("hrest:hasAddress")
    private String hrestHasAddress;
    @JsonProperty("hrest:hasMethod")
    private String hrestHasMethod;
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

    public Operation withType(String type) {
        this.type = type;
        return this;
    }

    /**
     * 
     * @return
     *     The hrestHasAddress
     */
    @JsonProperty("hrest:hasAddress")
    public String getHrestHasAddress() {
        return hrestHasAddress;
    }

    /**
     * 
     * @param hrestHasAddress
     *     The hrest:hasAddress
     */
    @JsonProperty("hrest:hasAddress")
    public void setHrestHasAddress(String hrestHasAddress) {
        this.hrestHasAddress = hrestHasAddress;
    }

    public Operation withHrestHasAddress(String hrestHasAddress) {
        this.hrestHasAddress = hrestHasAddress;
        return this;
    }

    /**
     * 
     * @return
     *     The hrestHasMethod
     */
    @JsonProperty("hrest:hasMethod")
    public String getHrestHasMethod() {
        return hrestHasMethod;
    }

    /**
     * 
     * @param hrestHasMethod
     *     The hrest:hasMethod
     */
    @JsonProperty("hrest:hasMethod")
    public void setHrestHasMethod(String hrestHasMethod) {
        this.hrestHasMethod = hrestHasMethod;
    }

    public Operation withHrestHasMethod(String hrestHasMethod) {
        this.hrestHasMethod = hrestHasMethod;
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

    public Operation withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(type).append(hrestHasAddress).append(hrestHasMethod).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Operation) == false) {
            return false;
        }
        Operation rhs = ((Operation) other);
        return new EqualsBuilder().append(type, rhs.type).append(hrestHasAddress, rhs.hrestHasAddress).append(hrestHasMethod, rhs.hrestHasMethod).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
