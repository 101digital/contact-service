package io.marketplace.services.contact.adapters;

import com.google.gson.Gson;
import io.marketplace.commons.exception.InternalServerErrorException;
import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;
import io.marketplace.services.contact.adapters.dto.UserListResponse;
import io.marketplace.services.contact.utils.RestUtils;
import io.marketplace.services.pxchange.client.service.PXChangeServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
public class MembershipAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(MembershipAdapter.class);
    private final RestTemplate restTemplate;

    @Autowired
    private Gson gsonInstance;

    @Value("${membership.server.base-url}")
    private String membershipServiceUrl;

    @Autowired
    private PXChangeServiceClient pxClient;

    @Autowired
    private RestUtils restUtils;

    @Autowired
    public MembershipAdapter(@Qualifier("gson-rest-template") final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Retryable(value = {InternalServerErrorException.class,
            ResourceAccessException.class}, maxAttempts = 2, backoff = @Backoff(delay = 5000))
    public UserListResponse getUserInformation(
            String mobileNumber) {
        try {

            URI uri = URI.create(membershipServiceUrl + "/admin/users");

            if(mobileNumber != null && !mobileNumber.isEmpty()) {
                uri = restUtils.appendUri(uri, "mobileNumber=" + mobileNumber);
            }

            ResponseEntity<UserListResponse> response = restTemplate.exchange(uri, HttpMethod.GET, restUtils.getHttpEntity(), UserListResponse.class);

            LOG.info("User information GET response {}", gsonInstance.toJson(response));

            if(response.getBody() != null){

                // Generate event for response
//                pxClient.addEvent(EventMessage.builder()
//                        .activityName(Constants.EVENT_CODE_CREATE_KYC_REQUEST)
//                        .eventTitle(Constants.EVENT_TRACKING_APPLICANT_CREATION)
//                        .eventCode(Constants.EVENT_CODE_CREATE_KYC_REQUEST)
//                        .eventBusinessId("Applicant id : " + response.getBody().getData().getApplicantId())
//                        .build());

                return response.getBody();
            }

        } catch (Exception ex){
//            LOG.error(ErrorCodes.CANNOT_CREATE_APPLICANT_ERROR_MESSAGE + ex.getMessage(), Error.of(CANNOT_CREATE_APPLICANT_ERROR_CODE), ex);
//            throw new ApiException(ErrorCodes.CANNOT_CREATE_APPLICANT_ERROR_MESSAGE, ex);
        }
        return null;
    }

}
