package io.marketplace.services.contact.api.delegate;

import io.marketplace.services.contact.api.BeneficiariesApiDelegate;
import io.marketplace.services.contact.model.BeneficiaryAccount;
import io.marketplace.services.contact.model.BeneficiaryAccountResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BeneficiariesApiDelegateImpl implements BeneficiariesApiDelegate {
    private static final String TEST_ACCOUNT_NUMBER = "BPI00000001";
    private static final String DEFAUT_ACCOUNT_ID = "4f95373a-6326-4b6c-85e4-d693f4befddd";
    private static final String DEFAUT_ACCOUNT_NAME = "John Doe";
    private static final String SECOND_ACCOUNT_ID = "b4ed8583-db7e-3313-afdd-de5dcb288d5c";

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

    public ResponseEntity<BeneficiaryAccountResponse> lookupBeneficiary(String mobileNumber,
                                                                        String accountNumber) {
        String accountId = DEFAUT_ACCOUNT_ID;
        if ("84972966263".equals(mobileNumber) ||
                TEST_ACCOUNT_NUMBER.equals(accountNumber)) {
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
}
