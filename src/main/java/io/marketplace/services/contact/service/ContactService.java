package io.marketplace.services.contact.service;


import io.marketplace.commons.exception.GenericException;
import io.marketplace.commons.exception.NotFoundException;
import io.marketplace.commons.logging.Error;
import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;
import io.marketplace.commons.model.event.EventMessage;
import io.marketplace.commons.utils.MembershipUtils;
import io.marketplace.services.contact.adapters.MembershipAdapter;
import io.marketplace.services.contact.adapters.WalletServiceAdapter;
import io.marketplace.services.contact.adapters.dto.UserListResponse;
import io.marketplace.services.contact.adapters.dto.WalletListResponse;
import io.marketplace.services.contact.entity.BeneficiaryEntity;
import io.marketplace.services.contact.mapper.BeneficiaryMapper;
import io.marketplace.services.contact.model.BeneficiaryRecord;
import io.marketplace.services.contact.model.WalletDto;
import io.marketplace.services.contact.repository.BeneficiaryRepository;
import io.marketplace.services.contact.specifications.BeneficiarySpecification;
import io.marketplace.services.contact.utils.Constants;
import io.marketplace.services.contact.utils.ErrorCode;
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

    @Autowired
    private MembershipAdapter membershipAdapter;

    @Autowired
    private WalletServiceAdapter walletServiceAdapter;

    public List<BeneficiaryRecord> getContactList(String userId, String searchText){

        List<BeneficiaryEntity> beneficiaryEntities;
        List<BeneficiaryRecord> beneficiaryRecords = new ArrayList<>();

        boolean isAdmin = MembershipUtils.hasRole(Constants.SUPER_ROLE);

        if(isAdmin)  {
            //for admin user return all the contacts
            Specification<BeneficiaryEntity> beneficiaryEntitySpecification = new BeneficiarySpecification(userId, searchText);

            if(userId != null || searchText != null){
                beneficiaryEntities = beneficiaryRepository.findAll(beneficiaryEntitySpecification);
            }else{
                beneficiaryEntities = beneficiaryRepository.findAll();
            }

            return loadRecords(beneficiaryEntities, beneficiaryRecords);

        }else{
            //normal user can only get contacts under logging user id
            String loggedInUserId = MembershipUtils.getUserId();
            Specification<BeneficiaryEntity> beneficiaryEntitySpecification = new BeneficiarySpecification(loggedInUserId, searchText);

            if(searchText != null){
                beneficiaryEntities = beneficiaryRepository.findAll(beneficiaryEntitySpecification);
            }else{
                beneficiaryEntities = beneficiaryRepository.findAllByUserId(loggedInUserId);
            }

            return loadRecords(beneficiaryEntities, beneficiaryRecords);
        }
    }

    public BeneficiaryRecord createContact(BeneficiaryRecord beneficiaryRecord){

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

    public WalletDto getBeneficiaryInformation(String mobileNumber, String accountNumber){

        log.info("getBeneficiaryInformation for mobileNumber : {} and accountNumber : {}", mobileNumber, accountNumber);

        WalletListResponse walletListResponse = null;

        if(mobileNumber != null && !mobileNumber.isEmpty()){
            //get beneficiary details by mobile number
            UserListResponse userListResponse = membershipAdapter.getUserInformation(mobileNumber);

            if(!userListResponse.getData().isEmpty()){
                //get the first userid and calling the wallet api
                walletListResponse = walletServiceAdapter.getWalletInformation(userListResponse.getData().get(0).getUserId());
                log.info("wallet api response for userId : {} response: {}", userListResponse.getData().get(0).getUserId(), walletListResponse);
            }
        }else if(accountNumber != null && !accountNumber.isEmpty()){
            //get beneficiary details by account
            walletListResponse = walletServiceAdapter.getWalletInformationByAccountNumber(accountNumber);
            log.info("wallet api response for accountNumber : {} response: {}", accountNumber, walletListResponse);
        }

        if(walletListResponse != null && walletListResponse.getData() != null && !walletListResponse.getData().isEmpty()){
            return walletListResponse.getData().get(0);
        }else{
            throw new NotFoundException(ErrorCode.WALLET_NOT_FOUND_ERROR_CODE,
                    "Wallet not found for the business id : ", mobileNumber != null ? mobileNumber : accountNumber);
        }
    }

    List<BeneficiaryRecord> loadRecords(List<BeneficiaryEntity> beneficiaryEntities, List<BeneficiaryRecord> beneficiaryRecords){
        for (BeneficiaryEntity beneficiaryEntity: beneficiaryEntities) {

            beneficiaryRecords.add(BeneficiaryRecord.builder()
                    .accountNumber(beneficiaryEntity.getAccountNumber())
                    .address(beneficiaryEntity.getAddress())
                    .bankCode(beneficiaryEntity.getBankCode())
                    .branchCode(beneficiaryEntity.getBranchCode())
                    .city(beneficiaryEntity.getCity())
                    .serviceCode(beneficiaryEntity.getServiceCode())
                    .subServiceCode(beneficiaryEntity.getSubServiceCode())
                    .postCode(beneficiaryEntity.getPostCode())
                    .userId(beneficiaryEntity.getUserId())
                    .displayName(beneficiaryEntity.getDisplayName())
                    .mobileNumber(beneficiaryEntity.getMobileNumber())
                    .paymentReference(beneficiaryEntity.getPaymentReference())
                    .verificationAt(beneficiaryEntity.getVerificationAt() != null ? beneficiaryEntity.getVerificationAt().toString() : "")
                    .verificationStatus(beneficiaryEntity.getVerificationStatus() != null ? beneficiaryEntity.getVerificationStatus() : "")
                    .build());
        }

        return beneficiaryRecords;
    }
}
