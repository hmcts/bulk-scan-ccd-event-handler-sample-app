package uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Address {

    public final String addressLine1;
    public final String addressLine2;
    public final String addressLine3;
    public final String postCode;
    public final String postTown;
    public final String county;
    public final String country;

    public Address(
        @JsonProperty("addressLine1") String addressLine1,
        @JsonProperty("addressLine2") String addressLine2,
        @JsonProperty("addressLine3")String addressLine3,
        @JsonProperty("postCode") String postCode,
        @JsonProperty("postTown") String postTown,
        @JsonProperty("county") String county,
        @JsonProperty("country") String country
    ) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.addressLine3 = addressLine3;
        this.postCode = postCode;
        this.postTown = postTown;
        this.county = county;
        this.country = country;
    }
}
