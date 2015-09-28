
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
    "@context",
    "ico",
    "property",
    "from",
    "to"
})
public class ObservationRequest {

    @JsonProperty("@context")
    private String Context;
    @JsonProperty("ico")
    private String ico;
    @JsonProperty("property")
    private String property;
    @JsonProperty("from")
    private String from;
    @JsonProperty("to")
    private String to;
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

    public ObservationRequest withContext(String Context) {
        this.Context = Context;
        return this;
    }

    /**
     * 
     * @return
     *     The ico
     */
    @JsonProperty("ico")
    public String getIco() {
        return ico;
    }

    /**
     * 
     * @param ico
     *     The ico
     */
    @JsonProperty("ico")
    public void setIco(String ico) {
        this.ico = ico;
    }

    public ObservationRequest withIco(String ico) {
        this.ico = ico;
        return this;
    }

    /**
     * 
     * @return
     *     The property
     */
    @JsonProperty("property")
    public String getProperty() {
        return property;
    }

    /**
     * 
     * @param property
     *     The property
     */
    @JsonProperty("property")
    public void setProperty(String property) {
        this.property = property;
    }

    public ObservationRequest withProperty(String property) {
        this.property = property;
        return this;
    }

    /**
     * 
     * @return
     *     The from
     */
    @JsonProperty("from")
    public String getFrom() {
        return from;
    }

    /**
     * 
     * @param from
     *     The from
     */
    @JsonProperty("from")
    public void setFrom(String from) {
        this.from = from;
    }

    public ObservationRequest withFrom(String from) {
        this.from = from;
        return this;
    }

    /**
     * 
     * @return
     *     The to
     */
    @JsonProperty("to")
    public String getTo() {
        return to;
    }

    /**
     * 
     * @param to
     *     The to
     */
    @JsonProperty("to")
    public void setTo(String to) {
        this.to = to;
    }

    public ObservationRequest withTo(String to) {
        this.to = to;
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

    public ObservationRequest withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
