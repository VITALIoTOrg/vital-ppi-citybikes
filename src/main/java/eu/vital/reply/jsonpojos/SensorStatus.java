
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
    "@context",
    "id",
    "type",
    "ssn:observedBy",
    "ssn:observationProperty",
    "ssn:observationResultTime",
    "dul:hasLocation",
    "ssn:observationQuality",
    "ssn:observationResult"
})
public class SensorStatus {

    @JsonProperty("@context")
    private String Context;
    @JsonProperty("id")
    private String id;
    @JsonProperty("type")
    private String type;
    @JsonProperty("ssn:observedBy")
    private String ssnObservedBy;
    @JsonProperty("ssn:observationProperty")
    private SsnObservationProperty__ ssnObservationProperty;
    @JsonProperty("ssn:observationResultTime")
    private SsnObservationResultTime__ ssnObservationResultTime;
    @JsonProperty("dul:hasLocation")
    private DulHasLocation_ dulHasLocation;
    @JsonProperty("ssn:observationQuality")
    private SsnObservationQuality__ ssnObservationQuality;
    @JsonProperty("ssn:observationResult")
    private SsnObservationResult__ ssnObservationResult;
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

    public SensorStatus withContext(String Context) {
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

    public SensorStatus withId(String id) {
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

    public SensorStatus withType(String type) {
        this.type = type;
        return this;
    }

    /**
     * 
     * @return
     *     The ssnObservedBy
     */
    @JsonProperty("ssn:observedBy")
    public String getSsnObservedBy() {
        return ssnObservedBy;
    }

    /**
     * 
     * @param ssnObservedBy
     *     The ssn:observedBy
     */
    @JsonProperty("ssn:observedBy")
    public void setSsnObservedBy(String ssnObservedBy) {
        this.ssnObservedBy = ssnObservedBy;
    }

    public SensorStatus withSsnObservedBy(String ssnObservedBy) {
        this.ssnObservedBy = ssnObservedBy;
        return this;
    }

    /**
     * 
     * @return
     *     The ssnObservationProperty
     */
    @JsonProperty("ssn:observationProperty")
    public SsnObservationProperty__ getSsnObservationProperty() {
        return ssnObservationProperty;
    }

    /**
     * 
     * @param ssnObservationProperty
     *     The ssn:observationProperty
     */
    @JsonProperty("ssn:observationProperty")
    public void setSsnObservationProperty(SsnObservationProperty__ ssnObservationProperty) {
        this.ssnObservationProperty = ssnObservationProperty;
    }

    public SensorStatus withSsnObservationProperty(SsnObservationProperty__ ssnObservationProperty) {
        this.ssnObservationProperty = ssnObservationProperty;
        return this;
    }

    /**
     * 
     * @return
     *     The ssnObservationResultTime
     */
    @JsonProperty("ssn:observationResultTime")
    public SsnObservationResultTime__ getSsnObservationResultTime() {
        return ssnObservationResultTime;
    }

    /**
     * 
     * @param ssnObservationResultTime
     *     The ssn:observationResultTime
     */
    @JsonProperty("ssn:observationResultTime")
    public void setSsnObservationResultTime(SsnObservationResultTime__ ssnObservationResultTime) {
        this.ssnObservationResultTime = ssnObservationResultTime;
    }

    public SensorStatus withSsnObservationResultTime(SsnObservationResultTime__ ssnObservationResultTime) {
        this.ssnObservationResultTime = ssnObservationResultTime;
        return this;
    }

    /**
     * 
     * @return
     *     The dulHasLocation
     */
    @JsonProperty("dul:hasLocation")
    public DulHasLocation_ getDulHasLocation() {
        return dulHasLocation;
    }

    /**
     * 
     * @param dulHasLocation
     *     The dul:hasLocation
     */
    @JsonProperty("dul:hasLocation")
    public void setDulHasLocation(DulHasLocation_ dulHasLocation) {
        this.dulHasLocation = dulHasLocation;
    }

    public SensorStatus withDulHasLocation(DulHasLocation_ dulHasLocation) {
        this.dulHasLocation = dulHasLocation;
        return this;
    }

    /**
     * 
     * @return
     *     The ssnObservationQuality
     */
    @JsonProperty("ssn:observationQuality")
    public SsnObservationQuality__ getSsnObservationQuality() {
        return ssnObservationQuality;
    }

    /**
     * 
     * @param ssnObservationQuality
     *     The ssn:observationQuality
     */
    @JsonProperty("ssn:observationQuality")
    public void setSsnObservationQuality(SsnObservationQuality__ ssnObservationQuality) {
        this.ssnObservationQuality = ssnObservationQuality;
    }

    public SensorStatus withSsnObservationQuality(SsnObservationQuality__ ssnObservationQuality) {
        this.ssnObservationQuality = ssnObservationQuality;
        return this;
    }

    /**
     * 
     * @return
     *     The ssnObservationResult
     */
    @JsonProperty("ssn:observationResult")
    public SsnObservationResult__ getSsnObservationResult() {
        return ssnObservationResult;
    }

    /**
     * 
     * @param ssnObservationResult
     *     The ssn:observationResult
     */
    @JsonProperty("ssn:observationResult")
    public void setSsnObservationResult(SsnObservationResult__ ssnObservationResult) {
        this.ssnObservationResult = ssnObservationResult;
    }

    public SensorStatus withSsnObservationResult(SsnObservationResult__ ssnObservationResult) {
        this.ssnObservationResult = ssnObservationResult;
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

    public SensorStatus withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(Context).append(id).append(type).append(ssnObservedBy).append(ssnObservationProperty).append(ssnObservationResultTime).append(dulHasLocation).append(ssnObservationQuality).append(ssnObservationResult).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SensorStatus) == false) {
            return false;
        }
        SensorStatus rhs = ((SensorStatus) other);
        return new EqualsBuilder().append(Context, rhs.Context).append(id, rhs.id).append(type, rhs.type).append(ssnObservedBy, rhs.ssnObservedBy).append(ssnObservationProperty, rhs.ssnObservationProperty).append(ssnObservationResultTime, rhs.ssnObservationResultTime).append(dulHasLocation, rhs.dulHasLocation).append(ssnObservationQuality, rhs.ssnObservationQuality).append(ssnObservationResult, rhs.ssnObservationResult).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
