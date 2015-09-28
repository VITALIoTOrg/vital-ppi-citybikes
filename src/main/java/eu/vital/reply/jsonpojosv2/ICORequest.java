
package eu.vital.reply.jsonpojosv2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    "@context",
    "icos"
})
public class ICORequest {

    @JsonProperty("@context")
    private String Context;
    @JsonProperty("icos")
    private List<String> icos = new ArrayList<String>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The Context
     */
    @JsonProperty("@context")
    public String getContext() {
        return Context;
    }

    /**
     * 
     * @param Context
     *     The @context
     */
    @JsonProperty("@context")
    public void setContext(String Context) {
        this.Context = Context;
    }

    public ICORequest withContext(String Context) {
        this.Context = Context;
        return this;
    }

    /**
     * 
     * @return
     *     The icos
     */
    @JsonProperty("icos")
    public List<String> getIcos() {
        return icos;
    }

    /**
     * 
     * @param icos
     *     The icos
     */
    @JsonProperty("icos")
    public void setIcos(List<String> icos) {
        this.icos = icos;
    }

    public ICORequest withIcos(List<String> icos) {
        this.icos = icos;
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

    public ICORequest withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
