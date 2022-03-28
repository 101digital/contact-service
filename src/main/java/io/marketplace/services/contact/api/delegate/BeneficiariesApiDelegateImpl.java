package io.marketplace.services.contact.api.delegate;

import io.marketplace.services.contact.api.BeneficiariesApiDelegate;
import io.marketplace.services.contact.model.BeneficiaryData;
import io.marketplace.services.contact.model.WalletResponse;
import io.marketplace.services.contact.service.ContactService;
import io.marketplace.services.contact.utils.Constants;
import io.marketplace.services.pxchange.client.annotation.PXLogEventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.marketplace.services.contact.utils.Constants.SEARCH_REQUEST_BUSINESS_DATA_BENEFICIARY;

@Service
public class BeneficiariesApiDelegateImpl implements BeneficiariesApiDelegate {


    @Autowired
    private ContactService contactService;

    @PXLogEventMessage(
            activityName = Constants.RECEIVING_THE_REQUEST_TO_GET_BENEFICIARY_ACTIVITY,
            eventCode = Constants.RECV_GET_BEN_REQUEST,
            eventTitle = Constants.RECEIVING_THE_REQUEST_TO_GET_BENEFICIARY,
            businessIdName = {SEARCH_REQUEST_BUSINESS_DATA_BENEFICIARY},
            businessIdIndex = {0, 1}
    )
    @Override
    public ResponseEntity<WalletResponse> lookupBeneficiary(String mobileNumber,
                                                                 String accountNumber) {

        List<BeneficiaryData> beneficiaryDtoList = contactService.getBeneficiaryInformation(mobileNumber, accountNumber);

        return ResponseEntity.ok(WalletResponse
                .builder()
                .data(beneficiaryDtoList)
                .build());
    }
}
