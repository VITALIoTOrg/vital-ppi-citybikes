
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
    "geo:lat",
    "geo:long",
    "geo:alt"
})
public class DulHasLocation {

    @JsonProperty("type")
    private String type;
    @JsonProperty("geo:lat")
    private Double geoLat;
    @JsonProperty("geo:long")
    private Double geoLong;
    @JsonProperty("geo:alt")
    private Double geoAlt;
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

    public DulHasLocation withType(String type) {
        this.type = type;
        return this;
    }

    /**
     * 
     * @return
     *     The geoLat
     */
    @JsonProperty("geo:lat")
    public Double getGeoLat() {
        return geoLat;
    }

    /**
     * 
     * @param geoLat
     *     The geo:lat
     */
    @JsonProperty("geo:lat")
    public void setGeoLat(Double geoLat) {
        this.geoLat = geoLat;
    }

    public DulHasLocation withGeoLat(Double geoLat) {
        this.geoLat = geoLat;
        return this;
    }

    /**
     * 
     * @return
     *     The geoLong
     */
    @JsonProperty("geo:long")
    public Double getGeoLong() {
        return geoLong;
    }

    /**
     * 
     * @param geoLong
     *     The geo:long
     */
    @JsonProperty("geo:long")
    public void setGeoLong(Double geoLong) {
        this.geoLong = geoLong;
    }

    public DulHasLocation withGeoLong(Double geoLong) {
        this.geoLong = geoLong;
        return this;
    }

    /**
     * 
     * @return
     *     The geoAlt
     */
    @JsonProperty("geo:alt")
    public Double getGeoAlt() {
        return geoAlt;
    }

    /**
     * 
     * @param geoAlt
     *     The geo:alt
     */
    @JsonProperty("geo:alt")
    public void setGeoAlt(Double geoAlt) {
        this.geoAlt = geoAlt;
    }

    public DulHasLocation withGeoAlt(Double geoAlt) {
        this.geoAlt = geoAlt;
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

    public DulHasLocation withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(type).append(geoLat).append(geoLong).append(geoAlt).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof DulHasLocation) == false) {
            return false;
        }
        DulHasLocation rhs = ((DulHasLocation) other);
        return new EqualsBuilder().append(type, rhs.type).append(geoLat, rhs.geoLat).append(geoLong, rhs.geoLong).append(geoAlt, rhs.geoAlt).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
