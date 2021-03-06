
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
    "metrics"
})
public class PerformanceMetricsMetadata {

    @JsonProperty("metrics")
    @Valid
    private List<Metric> metrics = new ArrayList<Metric>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The metrics
     */
    @JsonProperty("metrics")
    public List<Metric> getMetrics() {
        return metrics;
    }

    /**
     * 
     * @param metrics
     *     The metrics
     */
    @JsonProperty("metrics")
    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }

    public PerformanceMetricsMetadata withMetrics(List<Metric> metrics) {
        this.metrics = metrics;
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

    public PerformanceMetricsMetadata withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(metrics).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof PerformanceMetricsMetadata) == false) {
            return false;
        }
        PerformanceMetricsMetadata rhs = ((PerformanceMetricsMetadata) other);
        return new EqualsBuilder().append(metrics, rhs.metrics).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
