
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
    "$schema",
    "title",
    "type",
    "properties",
    "required"
})
public class ObservationRequest {

    @JsonProperty("$schema")
    private String $schema;
    @JsonProperty("title")
    private String title;
    @JsonProperty("type")
    private String type;
    @JsonProperty("properties")
    @Valid
    private Properties properties;
    @JsonProperty("required")
    @Valid
    private List<String> required = new ArrayList<String>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The $schema
     */
    @JsonProperty("$schema")
    public String get$schema() {
        return $schema;
    }

    /**
     * 
     * @param $schema
     *     The $schema
     */
    @JsonProperty("$schema")
    public void set$schema(String $schema) {
        this.$schema = $schema;
    }

    public ObservationRequest with$schema(String $schema) {
        this.$schema = $schema;
        return this;
    }

    /**
     * 
     * @return
     *     The title
     */
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    public ObservationRequest withTitle(String title) {
        this.title = title;
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

    public ObservationRequest withType(String type) {
        this.type = type;
        return this;
    }

    /**
     * 
     * @return
     *     The properties
     */
    @JsonProperty("properties")
    public Properties getProperties() {
        return properties;
    }

    /**
     * 
     * @param properties
     *     The properties
     */
    @JsonProperty("properties")
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public ObservationRequest withProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    /**
     * 
     * @return
     *     The required
     */
    @JsonProperty("required")
    public List<String> getRequired() {
        return required;
    }

    /**
     * 
     * @param required
     *     The required
     */
    @JsonProperty("required")
    public void setRequired(List<String> required) {
        this.required = required;
    }

    public ObservationRequest withRequired(List<String> required) {
        this.required = required;
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
        return new HashCodeBuilder().append($schema).append(title).append(type).append(properties).append(required).append(additionalProperties).toHashCode();
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
        return new EqualsBuilder().append($schema, rhs.$schema).append(title, rhs.title).append(type, rhs.type).append(properties, rhs.properties).append(required, rhs.required).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
