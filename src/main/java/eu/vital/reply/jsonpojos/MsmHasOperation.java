
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
    "hrest:hasAddress",
    "hrest:hasMethod"
})
public class MsmHasOperation {

    @JsonProperty("hrest:hasAddress")
    private String hrestHasAddress;
    @JsonProperty("hrest:hasMethod")
    private String hrestHasMethod;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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

    public MsmHasOperation withHrestHasAddress(String hrestHasAddress) {
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

    public MsmHasOperation withHrestHasMethod(String hrestHasMethod) {
        this.hrestHasMethod = hrestHasMethod;
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

    public MsmHasOperation withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
