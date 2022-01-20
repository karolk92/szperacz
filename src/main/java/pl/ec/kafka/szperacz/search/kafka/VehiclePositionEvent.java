package pl.ec.kafka.szperacz.search.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Zdarzenie z OBU
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressFBWarnings(value = {"JLM_JSR166_UTILCONCURRENT_MONITORENTER", "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"})
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderMethodName = "aVehiclePositionEvent")
@Setter
@Getter
public class VehiclePositionEvent {

    private long fixTimeEpoch;
    private Double gpsHeading;
    private Double gpsSpeed;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String lac;
    private String mcc;
    private String mnc;
    private String mobileCellId;
    private short satellitesForFix;
    private short satellitesInView;
    private String serialNumber;
    private String dataId;
    private String businessId;
    private int deviceId;
    private double accuracy;
    private double altitude;
    private boolean corrected;
    private boolean retryEvent;
    private String licensePlateNumber;
    private String trailerLicensePlateNumber;
    private String trainTollCategory;
    private String vehicleEmissionClass;
    private String systemBusinessId;
    private Integer decimalClass;
    private String registrationCountryCode;
    private String trailerRegistrationCountryCode;
    private Long billingAccountId;
    private Long vehicleId;
    private boolean blackListFlag;

    public boolean getCorrected() {
        return corrected;
    }

    public void setCorrected(boolean corrected) {
        this.corrected = corrected;
    }

    public boolean getRetryEvent() {
        return retryEvent;
    }

    public void setRetryEvent(boolean retryEvent) {
        this.retryEvent = retryEvent;
    }
}
