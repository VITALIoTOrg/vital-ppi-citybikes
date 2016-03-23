
package eu.vital.reply.jsonpojos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    "company",
    "href",
    "id",
    "location",
    "name",
    "stations"
})
public class Network {

    @JsonProperty("company")
    private String company;
    @JsonProperty("href")
    private String href;
    @JsonProperty("id")
    private String id;
    @JsonProperty("location")
    @Valid
    private Location location;
    @JsonProperty("name")
    private String name;
    @JsonProperty("stations")
    @Valid
    private List<Station> stations = new ArrayList<Station>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The company
     */
    @JsonProperty("company")
    public String getCompany() {
        return company;
    }

    /**
     * 
     * @param company
     *     The company
     */
    @JsonProperty("company")
    public void setCompany(String company) {
        this.company = company;
    }

    public Network withCompany(String company) {
        this.company = company;
        return this;
    }

    /**
     * 
     * @return
     *     The href
     */
    @JsonProperty("href")
    public String getHref() {
        return href;
    }

    /**
     * 
     * @param href
     *     The href
     */
    @JsonProperty("href")
    public void setHref(String href) {
        this.href = href;
    }

    public Network withHref(String href) {
        this.href = href;
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

    public Network withId(String id) {
        this.id = id;
        return this;
    }

    /**
     * 
     * @return
     *     The location
     */
    @JsonProperty("location")
    public Location getLocation() {
        return location;
    }

    /**
     * 
     * @param location
     *     The location
     */
    @JsonProperty("location")
    public void setLocation(Location location) {
        this.location = location;
    }

    public Network withLocation(Location location) {
        this.location = location;
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

    public Network withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 
     * @return
     *     The stations
     */
    @JsonProperty("stations")
    public List<Station> getStations() {
        return stations;
    }

    /**
     * 
     * @param stations
     *     The stations
     */
    @JsonProperty("stations")
    public void setStations(List<Station> stations) {
        this.stations = stations;
    }

    public Network withStations(List<Station> stations) {
        this.stations = stations;
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

    public Network withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(company).append(href).append(id).append(location).append(name).append(stations).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Network) == false) {
            return false;
        }
        Network rhs = ((Network) other);
        return new EqualsBuilder().append(company, rhs.company).append(href, rhs.href).append(id, rhs.id).append(location, rhs.location).append(name, rhs.name).append(stations, rhs.stations).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
