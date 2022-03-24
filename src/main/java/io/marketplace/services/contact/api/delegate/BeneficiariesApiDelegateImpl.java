package io.marketplace.services.contact.api.delegate;

import io.marketplace.services.contact.api.BeneficiariesApiDelegate;
import io.marketplace.services.contact.model.BeneficiaryAccount;
import io.marketplace.services.contact.model.BeneficiaryAccountResponse;
import io.marketplace.services.contact.model.WalletDto;
import io.marketplace.services.contact.model.WalletResponse;
import io.marketplace.services.contact.service.ContactService;
import io.marketplace.services.contact.utils.Constants;
import io.marketplace.services.pxchange.client.annotation.PXLogEventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static io.marketplace.services.contact.utils.Constants.SEARCH_REQUEST_BUSINESS_DATA;

@Service
public class BeneficiariesApiDelegateImpl implements BeneficiariesApiDelegate {
    private static final String TEST_ACCOUNT_NUMBER = "BPI00000001";
    private static final String DEFAUT_ACCOUNT_ID = "4f95373a-6326-4b6c-85e4-d693f4befddd";
    private static final String DEFAUT_ACCOUNT_NAME = "John Doe";
    private static final String SECOND_ACCOUNT_ID = "b4ed8583-db7e-3313-afdd-de5dcb288d5c";

    @Autowired
    private ContactService contactService;

    @Override
    public ResponseEntity<BeneficiaryAccountResponse> lookupBeneficiaryAccount(String accountNumber) {
        String accountId = DEFAUT_ACCOUNT_ID;
        if (TEST_ACCOUNT_NUMBER.equals(accountNumber)) {
            accountId = SECOND_ACCOUNT_ID;
        }

        return ResponseEntity.ok(BeneficiaryAccountResponse
                .builder()
                .data(BeneficiaryAccount
                        .builder()
                        .accountId(UUID.fromString(accountId))
                        .accountNumber(TEST_ACCOUNT_NUMBER)
                        .displayName(DEFAUT_ACCOUNT_NAME)
                        .build())
                .build());
    }

    @Override
    public ResponseEntity<BeneficiaryAccountResponse> lookupBeneficiaryAccountWithMobile(String mobileNumber) {
        String accountId = DEFAUT_ACCOUNT_ID;
        if ("84972966263".equals(mobileNumber)) {
            accountId = SECOND_ACCOUNT_ID;
        }
        return ResponseEntity.ok(BeneficiaryAccountResponse
                .builder()
                .data(BeneficiaryAccount
                        .builder()
                        .accountId(UUID.fromString(accountId))
                        .accountNumber(TEST_ACCOUNT_NUMBER)
                        .displayName(DEFAUT_ACCOUNT_NAME)
                        .build())
                .build());
    }

    @PXLogEventMessage(
            activityName = Constants.RECEIVING_THE_REQUEST_TO_GET_BENEFICIARY,
            eventCode = Constants.RECV_GET_REQUEST,
            eventTitle = Constants.RECEIVING_THE_REQUEST_TO_GET,
            businessIdName = {SEARCH_REQUEST_BUSINESS_DATA},
            businessIdIndex = {0, 1}
    )
    public ResponseEntity<WalletResponse> lookupBeneficiary(String mobileNumber,
                                                            String accountNumber) {

        WalletDto walletDto = contactService.getBeneficiaryInformation(mobileNumber, accountNumber);

        return ResponseEntity.ok(WalletResponse
                .builder()
                .data(walletDto)
                .build());
    }
}
