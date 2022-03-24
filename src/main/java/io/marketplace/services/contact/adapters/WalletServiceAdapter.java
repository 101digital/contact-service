package io.marketplace.services.contact.adapters;

import com.google.gson.Gson;
import io.marketplace.commons.exception.InternalServerErrorException;
import io.marketplace.commons.exception.UnauthorizedException;
import io.marketplace.commons.logging.Error;
import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;
import io.marketplace.commons.model.event.EventMessage;
import io.marketplace.services.contact.adapters.dto.WalletListResponse;
import io.marketplace.services.contact.utils.Constants;
import io.marketplace.services.contact.utils.ErrorCode;
import io.marketplace.services.contact.utils.RestUtils;
import io.marketplace.services.pxchange.client.service.PXChangeServiceClient;
import org.apache.kafka.common.errors.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static io.marketplace.services.contact.utils.ErrorCode.WALLET_SEARCH_VIA_ACCOUNT_ERROR_CODE;
import static io.marketplace.services.contact.utils.ErrorCode.WALLET_SEARCH_VIA_ACCOUNT_ERROR_MESSAGE;
import static io.marketplace.services.contact.utils.ErrorCode.WALLET_SEARCH_VIA_USER_ERROR_CODE;

@Component
public class WalletServiceAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(WalletServiceAdapter.class);
    private final RestTemplate restTemplate;

    @Autowired
    private Gson gsonInstance;

    @Value("${wallet.server.base-url}")
    private String walletServiceUrl;

    @Autowired
    private PXChangeServiceClient pxClient;

    @Autowired
    private RestUtils restUtils;

    @Autowired
    public WalletServiceAdapter(@Qualifier("gson-rest-template") final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Retryable(value = {InternalServerErrorException.class,
            ResourceAccessException.class}, maxAttempts = 2, backoff = @Backoff(delay = 5000))
    public WalletListResponse getWalletInformation(
            String userId) {
        try {

            URI uri = URI.create(walletServiceUrl + "/wallets");

            if(userId != null && !userId.isEmpty()) {
                uri = restUtils.appendUri(uri, "userId=" + userId);
            }

            ResponseEntity<WalletListResponse> response = restTemplate.exchange(uri, HttpMethod.GET, restUtils.getHttpEntity(), WalletListResponse.class);

            LOG.info("Wallet information GET response {}", gsonInstance.toJson(response));

            if(response.getBody() != null){

                // Generate event for response
                pxClient.addEvent(EventMessage.builder()
                        .activityName(Constants.RECEIVING_THE_REQUEST_TO_GET_BENEFICIARY_ACTIVITY)
                        .eventTitle(Constants.RECEIVING_THE_REQUEST_TO_GET_WALLETS)
                        .eventCode(Constants.RECV_GET_BEN_REQUEST)
                        .eventBusinessId("User Id : " + userId)
                        .build());
                return response.getBody();
            }

        } catch (HttpClientErrorException ex){
            throw new UnauthorizedException(WALLET_SEARCH_VIA_USER_ERROR_CODE,
                    ErrorCode.WALLET_SEARCH_VIA_USER_ERROR_MESSAGE, userId);
        } catch (Exception e){
            LOG.error(ErrorCode.WALLET_SEARCH_VIA_USER_ERROR_MESSAGE + e.getMessage(), Error.of(WALLET_SEARCH_VIA_USER_ERROR_CODE), e);
            throw new ApiException(ErrorCode.WALLET_SEARCH_VIA_USER_ERROR_MESSAGE, e);
        }
        return null;
    }

    @Retryable(value = {InternalServerErrorException.class,
            ResourceAccessException.class}, maxAttempts = 2, backoff = @Backoff(delay = 5000))
    public WalletListResponse getWalletInformationByAccountNumber(
            String accountNumber) {
        try {

            URI uri = URI.create(walletServiceUrl + "/wallets");

            if(accountNumber != null && !accountNumber.isEmpty()) {
                uri = restUtils.appendUri(uri, "accountNumber=" + accountNumber);
            }

            ResponseEntity<WalletListResponse> response = restTemplate.exchange(uri, HttpMethod.GET, restUtils.getHttpEntity(), WalletListResponse.class);

            LOG.info("Wallet information by account GET response {}", gsonInstance.toJson(response));

            if(response.getBody() != null){

                // Generate event for response
                pxClient.addEvent(EventMessage.builder()
                        .activityName(Constants.RECEIVING_THE_REQUEST_TO_GET_BENEFICIARY_ACTIVITY)
                        .eventTitle(Constants.RECEIVING_THE_REQUEST_TO_GET_WALLETS)
                        .eventCode(Constants.RECV_GET_BEN_REQUEST)
                        .eventBusinessId("Account Number : " + accountNumber)
                        .build());

                return response.getBody();
            }
        } catch (HttpClientErrorException ex){
            throw new UnauthorizedException(WALLET_SEARCH_VIA_ACCOUNT_ERROR_CODE,
                WALLET_SEARCH_VIA_ACCOUNT_ERROR_MESSAGE, accountNumber);
        } catch (Exception ex){
            LOG.error(WALLET_SEARCH_VIA_ACCOUNT_ERROR_MESSAGE + ex.getMessage(), Error.of(WALLET_SEARCH_VIA_ACCOUNT_ERROR_CODE), ex);
            throw new ApiException(WALLET_SEARCH_VIA_ACCOUNT_ERROR_MESSAGE, ex);
        }
        return null;
    }

}
