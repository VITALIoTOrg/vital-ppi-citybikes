
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
    "geo:lat",
    "geo:long"
})
public class HasLastKnownLocation {

    @JsonProperty("type")
    private String type;
    @JsonProperty("geo:lat")
    private Double geoLat;
    @JsonProperty("geo:long")
    private Double geoLong;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    protected final static Object NOT_FOUND_VALUE = new Object();

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

    public HasLastKnownLocation withGeoLat(Double geoLat) {
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

    public HasLastKnownLocation withGeoLong(Double geoLong) {
        this.geoLong = geoLong;
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

    public HasLastKnownLocation withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @SuppressWarnings({
        "unchecked"
    })
    protected boolean declaredProperty(String name, Object value) {
        switch (name) {
            case "type":
                if (value instanceof String) {
                    setType(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"type\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
                }
                return true;
            case "geo:lat":
                if (value instanceof Double) {
                    setGeoLat(((Double) value));
                } else {
                    throw new IllegalArgumentException(("property \"geo:lat\" is of type \"java.lang.Double\", but got "+ value.getClass().toString()));
                }
                return true;
            case "geo:long":
                if (value instanceof Double) {
                    setGeoLong(((Double) value));
                } else {
                    throw new IllegalArgumentException(("property \"geo:long\" is of type \"java.lang.Double\", but got "+ value.getClass().toString()));
                }
                return true;
            default:
                return false;
        }
    }

    @SuppressWarnings({
        "unchecked"
    })
    protected Object declaredPropertyOrNotFound(String name, Object notFoundValue) {
        switch (name) {
            case "type":
                return getType();
            case "geo:lat":
                return getGeoLat();
            case "geo:long":
                return getGeoLong();
            default:
                return notFoundValue;
        }
    }

    @SuppressWarnings({
        "unchecked"
    })
    public<T >T get(String name) {
        Object value = declaredPropertyOrNotFound(name, HasLastKnownLocation.NOT_FOUND_VALUE);
        if (HasLastKnownLocation.NOT_FOUND_VALUE!= value) {
            return ((T) value);
        } else {
            return ((T) getAdditionalProperties().get(name));
        }
    }

    @SuppressWarnings({
        "unchecked"
    })
    public void set(String name, Object value) {
        if (!declaredProperty(name, value)) {
            getAdditionalProperties().put(name, ((Object) value));
        }
    }

    @SuppressWarnings({
        "unchecked"
    })
    public HasLastKnownLocation with(String name, Object value) {
        if (!declaredProperty(name, value)) {
            getAdditionalProperties().put(name, ((Object) value));
        }
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(type).append(geoLat).append(geoLong).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof HasLastKnownLocation) == false) {
            return false;
        }
        HasLastKnownLocation rhs = ((HasLastKnownLocation) other);
        return new EqualsBuilder().append(type, rhs.type).append(geoLat, rhs.geoLat).append(geoLong, rhs.geoLong).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
