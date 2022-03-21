package io.marketplace.services.contact.api.delegate;

import org.openapitools.api.BeneficiariesApiDelegate;
import org.openapitools.model.BeneficiaryAccount;
import org.openapitools.model.BeneficiaryAccountResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BeneficiariesApiDelegateImpl implements BeneficiariesApiDelegate {


    @Override
    public ResponseEntity<BeneficiaryAccountResponse> lookupBeneficiaryAccount(String accountNumber) {
        return ResponseEntity.ok(BeneficiaryAccountResponse
                .builder()
                .data(BeneficiaryAccount
                        .builder()
                        .accountId(UUID.fromString("4f95373a-6326-4b6c-85e4-d693f4befddd"))
                        .accountNumber("120321456")
                        .displayName("John Does")
                        .build())
                .build());
    }

    @Override
    public ResponseEntity<BeneficiaryAccountResponse> lookupBeneficiaryAccountWithMobile(String mobileNumber) {
        return ResponseEntity.ok(BeneficiaryAccountResponse
                .builder()
                .data(BeneficiaryAccount
                        .builder()
                        .accountId(UUID.fromString("4f95373a-6326-4b6c-85e4-d693f4befddd"))
                        .accountNumber("120321456")
                        .displayName("John Does")
                        .build())
                .build());
    }
}
