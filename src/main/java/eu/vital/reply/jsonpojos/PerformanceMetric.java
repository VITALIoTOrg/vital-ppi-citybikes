
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
    "@context",
    "id",
    "type",
    "ssn:observedBy",
    "ssn:observationProperty",
    "ssn:observationResultTime",
    "ssn:observationQuality",
    "ssn:featureOfInterest",
    "ssn:observationResult"
})
public class PerformanceMetric {

    @JsonProperty("@context")
    private String Context;
    @JsonProperty("id")
    private String id;
    @JsonProperty("type")
    private String type;
    @JsonProperty("ssn:observedBy")
    private String ssnObservedBy;
    @JsonProperty("ssn:observationProperty")
    private SsnObservationProperty_ ssnObservationProperty;
    @JsonProperty("ssn:observationResultTime")
    private SsnObservationResultTime_ ssnObservationResultTime;
    @JsonProperty("ssn:observationQuality")
    private SsnObservationQuality_ ssnObservationQuality;
    @JsonProperty("ssn:featureOfInterest")
    private String ssnFeatureOfInterest;
    @JsonProperty("ssn:observationResult")
    private SsnObservationResult_ ssnObservationResult;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    protected final static Object NOT_FOUND_VALUE = new Object();

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

    public PerformanceMetric withContext(String Context) {
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

    public PerformanceMetric withId(String id) {
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

    public PerformanceMetric withType(String type) {
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

    public PerformanceMetric withSsnObservedBy(String ssnObservedBy) {
        this.ssnObservedBy = ssnObservedBy;
        return this;
    }

    /**
     * 
     * @return
     *     The ssnObservationProperty
     */
    @JsonProperty("ssn:observationProperty")
    public SsnObservationProperty_ getSsnObservationProperty() {
        return ssnObservationProperty;
    }

    /**
     * 
     * @param ssnObservationProperty
     *     The ssn:observationProperty
     */
    @JsonProperty("ssn:observationProperty")
    public void setSsnObservationProperty(SsnObservationProperty_ ssnObservationProperty) {
        this.ssnObservationProperty = ssnObservationProperty;
    }

    public PerformanceMetric withSsnObservationProperty(SsnObservationProperty_ ssnObservationProperty) {
        this.ssnObservationProperty = ssnObservationProperty;
        return this;
    }

    /**
     * 
     * @return
     *     The ssnObservationResultTime
     */
    @JsonProperty("ssn:observationResultTime")
    public SsnObservationResultTime_ getSsnObservationResultTime() {
        return ssnObservationResultTime;
    }

    /**
     * 
     * @param ssnObservationResultTime
     *     The ssn:observationResultTime
     */
    @JsonProperty("ssn:observationResultTime")
    public void setSsnObservationResultTime(SsnObservationResultTime_ ssnObservationResultTime) {
        this.ssnObservationResultTime = ssnObservationResultTime;
    }

    public PerformanceMetric withSsnObservationResultTime(SsnObservationResultTime_ ssnObservationResultTime) {
        this.ssnObservationResultTime = ssnObservationResultTime;
        return this;
    }

    /**
     * 
     * @return
     *     The ssnObservationQuality
     */
    @JsonProperty("ssn:observationQuality")
    public SsnObservationQuality_ getSsnObservationQuality() {
        return ssnObservationQuality;
    }

    /**
     * 
     * @param ssnObservationQuality
     *     The ssn:observationQuality
     */
    @JsonProperty("ssn:observationQuality")
    public void setSsnObservationQuality(SsnObservationQuality_ ssnObservationQuality) {
        this.ssnObservationQuality = ssnObservationQuality;
    }

    public PerformanceMetric withSsnObservationQuality(SsnObservationQuality_ ssnObservationQuality) {
        this.ssnObservationQuality = ssnObservationQuality;
        return this;
    }

    /**
     * 
     * @return
     *     The ssnFeatureOfInterest
     */
    @JsonProperty("ssn:featureOfInterest")
    public String getSsnFeatureOfInterest() {
        return ssnFeatureOfInterest;
    }

    /**
     * 
     * @param ssnFeatureOfInterest
     *     The ssn:featureOfInterest
     */
    @JsonProperty("ssn:featureOfInterest")
    public void setSsnFeatureOfInterest(String ssnFeatureOfInterest) {
        this.ssnFeatureOfInterest = ssnFeatureOfInterest;
    }

    public PerformanceMetric withSsnFeatureOfInterest(String ssnFeatureOfInterest) {
        this.ssnFeatureOfInterest = ssnFeatureOfInterest;
        return this;
    }

    /**
     * 
     * @return
     *     The ssnObservationResult
     */
    @JsonProperty("ssn:observationResult")
    public SsnObservationResult_ getSsnObservationResult() {
        return ssnObservationResult;
    }

    /**
     * 
     * @param ssnObservationResult
     *     The ssn:observationResult
     */
    @JsonProperty("ssn:observationResult")
    public void setSsnObservationResult(SsnObservationResult_ ssnObservationResult) {
        this.ssnObservationResult = ssnObservationResult;
    }

    public PerformanceMetric withSsnObservationResult(SsnObservationResult_ ssnObservationResult) {
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

    public PerformanceMetric withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @SuppressWarnings({
        "unchecked"
    })
    protected boolean declaredProperty(String name, Object value) {
        switch (name) {
            case "@context":
                if (value instanceof String) {
                    setContext(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"@context\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
                }
                return true;
            case "id":
                if (value instanceof String) {
                    setId(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"id\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
                }
                return true;
            case "type":
                if (value instanceof String) {
                    setType(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"type\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
                }
                return true;
            case "ssn:observedBy":
                if (value instanceof String) {
                    setSsnObservedBy(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"ssn:observedBy\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
                }
                return true;
            case "ssn:observationProperty":
                if (value instanceof SsnObservationProperty_) {
                    setSsnObservationProperty(((SsnObservationProperty_) value));
                } else {
                    throw new IllegalArgumentException(("property \"ssn:observationProperty\" is of type \"eu.vital.reply.jsonpojos.SsnObservationProperty_\", but got "+ value.getClass().toString()));
                }
                return true;
            case "ssn:observationResultTime":
                if (value instanceof SsnObservationResultTime_) {
                    setSsnObservationResultTime(((SsnObservationResultTime_) value));
                } else {
                    throw new IllegalArgumentException(("property \"ssn:observationResultTime\" is of type \"eu.vital.reply.jsonpojos.SsnObservationResultTime_\", but got "+ value.getClass().toString()));
                }
                return true;
            case "ssn:observationQuality":
                if (value instanceof SsnObservationQuality_) {
                    setSsnObservationQuality(((SsnObservationQuality_) value));
                } else {
                    throw new IllegalArgumentException(("property \"ssn:observationQuality\" is of type \"eu.vital.reply.jsonpojos.SsnObservationQuality_\", but got "+ value.getClass().toString()));
                }
                return true;
            case "ssn:featureOfInterest":
                if (value instanceof String) {
                    setSsnFeatureOfInterest(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"ssn:featureOfInterest\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
                }
                return true;
            case "ssn:observationResult":
                if (value instanceof SsnObservationResult_) {
                    setSsnObservationResult(((SsnObservationResult_) value));
                } else {
                    throw new IllegalArgumentException(("property \"ssn:observationResult\" is of type \"eu.vital.reply.jsonpojos.SsnObservationResult_\", but got "+ value.getClass().toString()));
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
            case "@context":
                return getContext();
            case "id":
                return getId();
            case "type":
                return getType();
            case "ssn:observedBy":
                return getSsnObservedBy();
            case "ssn:observationProperty":
                return getSsnObservationProperty();
            case "ssn:observationResultTime":
                return getSsnObservationResultTime();
            case "ssn:observationQuality":
                return getSsnObservationQuality();
            case "ssn:featureOfInterest":
                return getSsnFeatureOfInterest();
            case "ssn:observationResult":
                return getSsnObservationResult();
            default:
                return notFoundValue;
        }
    }

    @SuppressWarnings({
        "unchecked"
    })
    public<T >T get(String name) {
        Object value = declaredPropertyOrNotFound(name, PerformanceMetric.NOT_FOUND_VALUE);
        if (PerformanceMetric.NOT_FOUND_VALUE!= value) {
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
    public PerformanceMetric with(String name, Object value) {
        if (!declaredProperty(name, value)) {
            getAdditionalProperties().put(name, ((Object) value));
        }
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(Context).append(id).append(type).append(ssnObservedBy).append(ssnObservationProperty).append(ssnObservationResultTime).append(ssnObservationQuality).append(ssnFeatureOfInterest).append(ssnObservationResult).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof PerformanceMetric) == false) {
            return false;
        }
        PerformanceMetric rhs = ((PerformanceMetric) other);
        return new EqualsBuilder().append(Context, rhs.Context).append(id, rhs.id).append(type, rhs.type).append(ssnObservedBy, rhs.ssnObservedBy).append(ssnObservationProperty, rhs.ssnObservationProperty).append(ssnObservationResultTime, rhs.ssnObservationResultTime).append(ssnObservationQuality, rhs.ssnObservationQuality).append(ssnFeatureOfInterest, rhs.ssnFeatureOfInterest).append(ssnObservationResult, rhs.ssnObservationResult).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
