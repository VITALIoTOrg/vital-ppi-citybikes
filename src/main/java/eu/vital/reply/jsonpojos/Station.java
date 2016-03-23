
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
    "empty_slots",
    "extra",
    "free_bikes",
    "id",
    "latitude",
    "longitude",
    "name",
    "timestamp"
})
public class Station {

    @JsonProperty("empty_slots")
    private Integer emptySlots;
    @JsonProperty("extra")
    @Valid
    private Extra extra;
    @JsonProperty("free_bikes")
    private Integer freeBikes;
    @JsonProperty("id")
    private String id;
    @JsonProperty("latitude")
    private Double latitude;
    @JsonProperty("longitude")
    private Double longitude;
    @JsonProperty("name")
    private String name;
    @JsonProperty("timestamp")
    private String timestamp;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The emptySlots
     */
    @JsonProperty("empty_slots")
    public Integer getEmptySlots() {
        return emptySlots;
    }

    /**
     * 
     * @param emptySlots
     *     The empty_slots
     */
    @JsonProperty("empty_slots")
    public void setEmptySlots(Integer emptySlots) {
        this.emptySlots = emptySlots;
    }

    public Station withEmptySlots(Integer emptySlots) {
        this.emptySlots = emptySlots;
        return this;
    }

    /**
     * 
     * @return
     *     The extra
     */
    @JsonProperty("extra")
    public Extra getExtra() {
        return extra;
    }

    /**
     * 
     * @param extra
     *     The extra
     */
    @JsonProperty("extra")
    public void setExtra(Extra extra) {
        this.extra = extra;
    }

    public Station withExtra(Extra extra) {
        this.extra = extra;
        return this;
    }

    /**
     * 
     * @return
     *     The freeBikes
     */
    @JsonProperty("free_bikes")
    public Integer getFreeBikes() {
        return freeBikes;
    }

    /**
     * 
     * @param freeBikes
     *     The free_bikes
     */
    @JsonProperty("free_bikes")
    public void setFreeBikes(Integer freeBikes) {
        this.freeBikes = freeBikes;
    }

    public Station withFreeBikes(Integer freeBikes) {
        this.freeBikes = freeBikes;
        return this;
    }

    /**
     * 
     * @return
     *     The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    public Station withId(String id) {
        this.id = id;
        return this;
    }

    /**
     * 
     * @return
     *     The latitude
     */
    @JsonProperty("latitude")
    public Double getLatitude() {
        return latitude;
    }

    /**
     * 
     * @param latitude
     *     The latitude
     */
    @JsonProperty("latitude")
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Station withLatitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    /**
     * 
     * @return
     *     The longitude
     */
    @JsonProperty("longitude")
    public Double getLongitude() {
        return longitude;
    }

    /**
     * 
     * @param longitude
     *     The longitude
     */
    @JsonProperty("longitude")
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Station withLongitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    /**
     * 
     * @return
     *     The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public Station withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 
     * @return
     *     The timestamp
     */
    @JsonProperty("timestamp")
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * 
     * @param timestamp
     *     The timestamp
     */
    @JsonProperty("timestamp")
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Station withTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

    public Station withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(emptySlots).append(extra).append(freeBikes).append(id).append(latitude).append(longitude).append(name).append(timestamp).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Station) == false) {
            return false;
        }
        Station rhs = ((Station) other);
        return new EqualsBuilder().append(emptySlots, rhs.emptySlots).append(extra, rhs.extra).append(freeBikes, rhs.freeBikes).append(id, rhs.id).append(latitude, rhs.latitude).append(longitude, rhs.longitude).append(name, rhs.name).append(timestamp, rhs.timestamp).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
