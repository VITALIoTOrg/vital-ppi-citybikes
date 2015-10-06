
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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "@context",
    "id",
    "type",
    "name",
    "description",
    "status",
    "hasLastKnownLocation",
    "ssn:observes"
})
public class Sensor {

    @JsonProperty("@context")
    private String Context;
    @JsonProperty("id")
    private String id;
    @JsonProperty("type")
    private String type;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("status")
    private String status;
    @JsonProperty("hasLastKnownLocation")
    private HasLastKnownLocation hasLastKnownLocation;
    @JsonProperty("ssn:observes")
    private List<SsnObserf> ssnObserves = new ArrayList<SsnObserf>();
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

    public Sensor withContext(String Context) {
        this.Context = Context;
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

    public Sensor withId(String id) {
        this.id = id;
        return this;
    }

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

    public Sensor withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 
     * @return
     *     The description
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     * 
     * @param description
     *     The description
     */
    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    public Sensor withDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * 
     * @return
     *     The status
     */
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */
    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    public Sensor withStatus(String status) {
        this.status = status;
        return this;
    }

    /**
     * 
     * @return
     *     The hasLastKnownLocation
     */
    @JsonProperty("hasLastKnownLocation")
    public HasLastKnownLocation getHasLastKnownLocation() {
        return hasLastKnownLocation;
    }

    /**
     * 
     * @param hasLastKnownLocation
     *     The hasLastKnownLocation
     */
    @JsonProperty("hasLastKnownLocation")
    public void setHasLastKnownLocation(HasLastKnownLocation hasLastKnownLocation) {
        this.hasLastKnownLocation = hasLastKnownLocation;
    }

    public Sensor withHasLastKnownLocation(HasLastKnownLocation hasLastKnownLocation) {
        this.hasLastKnownLocation = hasLastKnownLocation;
        return this;
    }

    /**
     * 
     * @return
     *     The ssnObserves
     */
    @JsonProperty("ssn:observes")
    public List<SsnObserf> getSsnObserves() {
        return ssnObserves;
    }

    /**
     * 
     * @param ssnObserves
     *     The ssn:observes
     */
    @JsonProperty("ssn:observes")
    public void setSsnObserves(List<SsnObserf> ssnObserves) {
        this.ssnObserves = ssnObserves;
    }

    public Sensor withSsnObserves(List<SsnObserf> ssnObserves) {
        this.ssnObserves = ssnObserves;
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
        return new HashCodeBuilder().append(Context).append(id).append(type).append(name).append(description).append(status).append(hasLastKnownLocation).append(ssnObserves).append(additionalProperties).toHashCode();
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
        return new EqualsBuilder().append(Context, rhs.Context).append(id, rhs.id).append(type, rhs.type).append(name, rhs.name).append(description, rhs.description).append(status, rhs.status).append(hasLastKnownLocation, rhs.hasLastKnownLocation).append(ssnObserves, rhs.ssnObserves).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
