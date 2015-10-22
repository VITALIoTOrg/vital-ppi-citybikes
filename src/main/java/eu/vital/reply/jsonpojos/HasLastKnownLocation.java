
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
    "geo:long"
})
public class HasLastKnownLocation {

    @JsonProperty("type")
    private String type;
    @JsonProperty("geo:lat")
    private String geoLat;
    @JsonProperty("geo:long")
    private String geoLong;
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

    public HasLastKnownLocation withType(String type) {
        this.type = type;
        return this;
    }

    /**
     * 
     * @return
     *     The geoLat
     */
    @JsonProperty("geo:lat")
    public String getGeoLat() {
        return geoLat;
    }

    /**
     * 
     * @param geoLat
     *     The geo:lat
     */
    @JsonProperty("geo:lat")
    public void setGeoLat(String geoLat) {
        this.geoLat = geoLat;
    }

    public HasLastKnownLocation withGeoLat(String geoLat) {
        this.geoLat = geoLat;
        return this;
    }

    /**
     * 
     * @return
     *     The geoLong
     */
    @JsonProperty("geo:long")
    public String getGeoLong() {
        return geoLong;
    }

    /**
     * 
     * @param geoLong
     *     The geo:long
     */
    @JsonProperty("geo:long")
    public void setGeoLong(String geoLong) {
        this.geoLong = geoLong;
    }

    public HasLastKnownLocation withGeoLong(String geoLong) {
        this.geoLong = geoLong;
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

    public HasLastKnownLocation withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
