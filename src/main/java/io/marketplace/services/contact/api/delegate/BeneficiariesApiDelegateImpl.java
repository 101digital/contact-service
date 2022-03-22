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

    @Override
    public ResponseEntity<BeneficiaryAccountResponse> lookupBeneficiaryAccount(String accountNumber) {
        String accountId = "4f95373a-6326-4b6c-85e4-d693f4befddd";
        if (TEST_ACCOUNT_NUMBER.equals(accountNumber)) {
            accountId = "b4ed8583-db7e-3313-afdd-de5dcb288d5c";
        }

        return ResponseEntity.ok(BeneficiaryAccountResponse
                .builder()
                .data(BeneficiaryAccount
                        .builder()
                        .accountId(UUID.fromString(accountId))
                        .accountNumber(TEST_ACCOUNT_NUMBER)
                        .displayName("John Does")
                        .build())
                .build());
    }

    @Override
    public ResponseEntity<BeneficiaryAccountResponse> lookupBeneficiaryAccountWithMobile(String mobileNumber) {
        String accountId = "4f95373a-6326-4b6c-85e4-d693f4befddd";
        if ("84972966263".equals(mobileNumber)) {
            accountId = "b4ed8583-db7e-3313-afdd-de5dcb288d5c";
        }
        return ResponseEntity.ok(BeneficiaryAccountResponse
                .builder()
                .data(BeneficiaryAccount
                        .builder()
                        .accountId(UUID.fromString(accountId))
                        .accountNumber(TEST_ACCOUNT_NUMBER)
                        .displayName("John Does")
                        .build())
                .build());
    }
}
