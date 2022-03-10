package io.marketplace.services.contact.api.delegate;

import io.marketplace.services.contact.service.ContactService;
import org.openapitools.api.ContactsApiDelegate;
import org.openapitools.model.BeneficiaryRecord;
import org.openapitools.model.BeneficiaryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactApiDelegateImpl implements ContactsApiDelegate {

    @Autowired
    private ContactService contactService;

    @Override
    public ResponseEntity<BeneficiaryResponse> getContactList(String userId,
                                                              String searchText) {

        List<BeneficiaryRecord> contactList = contactService.getContactList(userId, searchText);

        return ResponseEntity.ok(BeneficiaryResponse.builder().data(contactList).build());
    }

    @Override
    public ResponseEntity<BeneficiaryRecord> createContact(BeneficiaryRecord beneficiaryRecord) {

        return ResponseEntity.ok(contactService.createBeneficiary(beneficiaryRecord));
    }
}
