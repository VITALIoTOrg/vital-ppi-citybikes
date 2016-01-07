
package eu.vital.reply.jsonpojos;

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
public class ObservationRequest {

    @JsonProperty("sensor")
    private List<String> sensor = new ArrayList<String>();
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
     *     The sensor
     */
    @JsonProperty("sensor")
    public List<String> getSensor() {
        return sensor;
    }

    /**
     * 
     * @param sensor
     *     The sensor
     */
    @JsonProperty("sensor")
    public void setSensor(List<String> sensor) {
        this.sensor = sensor;
    }

    public ObservationRequest withSensor(List<String> sensor) {
        this.sensor = sensor;
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

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(sensor).append(property).append(from).append(to).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ObservationRequest) == false) {
            return false;
        }
        ObservationRequest rhs = ((ObservationRequest) other);
        return new EqualsBuilder().append(sensor, rhs.sensor).append(property, rhs.property).append(from, rhs.from).append(to, rhs.to).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
