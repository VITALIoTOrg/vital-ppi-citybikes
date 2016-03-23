
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
    "type",
    "minItems",
    "items"
})
public class Sensor {

    @JsonProperty("type")
    private String type;
    @JsonProperty("minItems")
    private Integer minItems;
    @JsonProperty("items")
    @Valid
    private Items items;
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

    public Sensor withType(String type) {
        this.type = type;
        return this;
    }

    /**
     * 
     * @return
     *     The minItems
     */
    @JsonProperty("minItems")
    public Integer getMinItems() {
        return minItems;
    }

    /**
     * 
     * @param minItems
     *     The minItems
     */
    @JsonProperty("minItems")
    public void setMinItems(Integer minItems) {
        this.minItems = minItems;
    }

    public Sensor withMinItems(Integer minItems) {
        this.minItems = minItems;
        return this;
    }

    /**
     * 
     * @return
     *     The items
     */
    @JsonProperty("items")
    public Items getItems() {
        return items;
    }

    /**
     * 
     * @param items
     *     The items
     */
    @JsonProperty("items")
    public void setItems(Items items) {
        this.items = items;
    }

    public Sensor withItems(Items items) {
        this.items = items;
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

    public Sensor withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(type).append(minItems).append(items).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Sensor) == false) {
            return false;
        }
        Sensor rhs = ((Sensor) other);
        return new EqualsBuilder().append(type, rhs.type).append(minItems, rhs.minItems).append(items, rhs.items).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
