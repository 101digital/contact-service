package io.marketplace.services.contact.api.delegate;

import io.marketplace.services.contact.api.ContactsApiDelegate;
import io.marketplace.services.contact.model.BeneficiaryCreateResponse;
import io.marketplace.services.contact.model.BeneficiaryData;
import io.marketplace.services.contact.model.BeneficiaryRecord;
import io.marketplace.services.contact.model.BeneficiaryResponse;
import io.marketplace.services.contact.service.ContactService;

import io.marketplace.services.contact.utils.Constants;
import io.marketplace.services.pxchange.client.annotation.PXLogEventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.marketplace.services.contact.utils.Constants.SAVE_REQUEST_BUSINESS_DATA;
import static io.marketplace.services.contact.utils.Constants.SEARCH_REQUEST_BUSINESS_DATA;

@Service
public class ContactApiDelegateImpl implements ContactsApiDelegate {

    @Autowired
    private ContactService contactService;

    @PXLogEventMessage(
            activityName = Constants.RECEIVING_THE_REQUEST_TO_GET_ACTIVITY,
            eventCode = Constants.RECV_GET_REQUEST,
            eventTitle = Constants.RECEIVING_THE_REQUEST_TO_GET,
            businessIdName = {SEARCH_REQUEST_BUSINESS_DATA},
            businessIdIndex = {0, 1}
    )
    @Override
    public ResponseEntity<BeneficiaryResponse> getContactList(String userId,
                                                              String searchText) {

        List<BeneficiaryData> contactList = contactService.getContactList(userId, searchText);

        return ResponseEntity.ok(BeneficiaryResponse.builder().data(contactList).build());
    }

    @PXLogEventMessage(
            activityName = Constants.RECEIVING_THE_REQUEST_TO_SAVE_ACTIVITY,
            eventCode = Constants.RECV_SAVE_REQUEST,
            eventTitle = Constants.RECEIVING_THE_REQUEST_TO_SAVE_CONTACT,
            businessIdName = {SAVE_REQUEST_BUSINESS_DATA},
            businessIdIndex = {0}
    )
    @Override
    public ResponseEntity<BeneficiaryCreateResponse> createContact(BeneficiaryRecord beneficiaryRecord) {

        return ResponseEntity.ok(BeneficiaryCreateResponse
                .builder()
                .data(contactService.createContact(beneficiaryRecord))
                .build());
    }
}
