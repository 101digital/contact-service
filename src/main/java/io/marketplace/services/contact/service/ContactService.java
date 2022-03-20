package io.marketplace.services.contact.service;


import io.marketplace.commons.exception.GenericException;
import io.marketplace.commons.logging.Error;
import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;
import io.marketplace.commons.model.event.EventMessage;
import io.marketplace.services.contact.entity.BeneficiaryEntity;
import io.marketplace.services.contact.mapper.BeneficiaryMapper;
import io.marketplace.services.contact.model.BeneficiaryRecord;
import io.marketplace.services.contact.repository.BeneficiaryRepository;
import io.marketplace.services.contact.specifications.BeneficiarySpecification;
import io.marketplace.services.pxchange.client.service.PXChangeServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static io.marketplace.services.contact.utils.Constants.RECEIVING_THE_REQUEST_TO_SAVE_ACTIVITY;
import static io.marketplace.services.contact.utils.Constants.RECV_SAVE_REQUEST;
import static io.marketplace.services.contact.utils.Constants.SUCCESS_REQUEST_TO_SAVE_CONTACT;
import static io.marketplace.services.contact.utils.ErrorCode.CONTACT_CREATION_DB_ERROR_CODE;
import static io.marketplace.services.contact.utils.ErrorCode.CONTACT_CREATION_DB_ERROR_MESSAGE;

@Service
public class ContactService {
    private static final Logger log = LoggerFactory.getLogger(ContactService.class);

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    @Autowired
    private BeneficiaryMapper beneficiaryMapper;

    @Autowired
    private PXChangeServiceClient pxClient;

    public List<BeneficiaryRecord> getContactList(String userId, String searchText){

        List<BeneficiaryEntity> beneficiaryEntities;

        if(userId != null || searchText != null){
            Specification<BeneficiaryEntity> beneficiaryEntitySpecification = new BeneficiarySpecification(userId, searchText);
            beneficiaryEntities = beneficiaryRepository.findAll(beneficiaryEntitySpecification);
        }else{
            beneficiaryEntities = beneficiaryRepository.findAll();
        }

        List<BeneficiaryRecord> beneficiaryRecords = new ArrayList<>();

        for (BeneficiaryEntity e:beneficiaryEntities) {
            beneficiaryRecords.add(BeneficiaryRecord.builder()
                    .accountNumber(e.getAccountNumber() != null ? e.getAccountNumber() : "")
                    .address(e.getAddress() != null ? e.getAddress() : "")
                    .bankCode(e.getBankCode() != null ? e.getBankCode() : "")
                    .branchCode(e.getBranchCode() != null ? e.getBranchCode() : "")
                    .city(e.getCity() != null ? e.getCity() : "")
                    .serviceCode(e.getServiceCode() != null ? e.getServiceCode() : "")
                    .subServiceCode(e.getSubServiceCode() != null ? e.getSubServiceCode() : "")
                    .postCode(e.getPostCode() != null ? e.getPostCode() : "")
                    .userId(e.getUserId() != null ? e.getUserId() : "")
                    .displayName(e.getDisplayName() != null ? e.getDisplayName() : "")
                    .mobileNumber(e.getMobileNumber() != null ? e.getMobileNumber() : "")
                    .paymentReference(e.getPaymentReference() != null ? e.getPaymentReference() : "")
                    .verificationAt(e.getVerificationAt() != null ? e.getVerificationAt().toString() : "")
                    .verificationStatus(e.getVerificationStatus() != null ? e.getVerificationStatus() : "")
                    .build());
        }

        return beneficiaryRecords;
    }

    public BeneficiaryRecord createBeneficiary(BeneficiaryRecord beneficiaryRecord){

        try{
            beneficiaryRepository.save(beneficiaryMapper.toBeneficiaryEntity(beneficiaryRecord));

            // Generate event for adapter
            pxClient.addEvent(EventMessage.builder()
                    .activityName(RECEIVING_THE_REQUEST_TO_SAVE_ACTIVITY)
                    .eventTitle(SUCCESS_REQUEST_TO_SAVE_CONTACT)
                    .eventCode(RECV_SAVE_REQUEST)
                    .businessData(beneficiaryRecord.getPaymentReference() != null ?
                            beneficiaryRecord.getPaymentReference() : "N/A")
                    .build());

            return beneficiaryRecord;
        }catch (Exception e){
            log.error(CONTACT_CREATION_DB_ERROR_MESSAGE, Error.of(CONTACT_CREATION_DB_ERROR_CODE));
            throw new GenericException(CONTACT_CREATION_DB_ERROR_CODE, e.getMessage(), "");
        }

    }
}
