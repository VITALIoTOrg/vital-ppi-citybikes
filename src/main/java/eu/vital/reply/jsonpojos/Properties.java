
package eu.vital.reply.jsonpojos;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import javax.validation.Valid;
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
    "sensor",
    "property",
    "from",
    "to"
})
public class Properties {

    @JsonProperty("sensor")
    @Valid
    private Sensor sensor;
    @JsonProperty("property")
    @Valid
    private Property property;
    @JsonProperty("from")
    @Valid
    private From from;
    @JsonProperty("to")
    @Valid
    private To to;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The sensor
     */
    @JsonProperty("sensor")
    public Sensor getSensor() {
        return sensor;
    }

    /**
     * 
     * @param sensor
     *     The sensor
     */
    @JsonProperty("sensor")
    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public Properties withSensor(Sensor sensor) {
        this.sensor = sensor;
        return this;
    }

    /**
     * 
     * @return
     *     The property
     */
    @JsonProperty("property")
    public Property getProperty() {
        return property;
    }

    /**
     * 
     * @param property
     *     The property
     */
    @JsonProperty("property")
    public void setProperty(Property property) {
        this.property = property;
    }

    public Properties withProperty(Property property) {
        this.property = property;
        return this;
    }

    /**
     * 
     * @return
     *     The from
     */
    @JsonProperty("from")
    public From getFrom() {
        return from;
    }

    /**
     * 
     * @param from
     *     The from
     */
    @JsonProperty("from")
    public void setFrom(From from) {
        this.from = from;
    }

    public Properties withFrom(From from) {
        this.from = from;
        return this;
    }

    /**
     * 
     * @return
     *     The to
     */
    @JsonProperty("to")
    public To getTo() {
        return to;
    }

    /**
     * 
     * @param to
     *     The to
     */
    @JsonProperty("to")
    public void setTo(To to) {
        this.to = to;
    }

    public Properties withTo(To to) {
        this.to = to;
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

    public Properties withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(sensor).append(property).append(from).append(to).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Properties) == false) {
            return false;
        }
        Properties rhs = ((Properties) other);
        return new EqualsBuilder().append(sensor, rhs.sensor).append(property, rhs.property).append(from, rhs.from).append(to, rhs.to).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
