package io.marketplace.services.contact.mapper;

import io.marketplace.services.contact.entity.BeneficiaryEntity;
import io.marketplace.services.contact.model.BeneficiaryRecord;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BeneficiaryMapper {

    public BeneficiaryEntity toBeneficiaryEntity(BeneficiaryRecord beneficiaryRecord){

        return BeneficiaryEntity.builder()
                .id(UUID.randomUUID())
                .userId(beneficiaryRecord.getUserId() != null ? beneficiaryRecord.getUserId() : "")
                .serviceCode(beneficiaryRecord.getServiceCode() != null ? beneficiaryRecord.getServiceCode() : "")
                .subServiceCode(beneficiaryRecord.getSubServiceCode() != null ? beneficiaryRecord.getSubServiceCode() : "")
                .displayName(beneficiaryRecord.getDisplayName() != null ? beneficiaryRecord.getDisplayName() : "")
                .paymentReference(beneficiaryRecord.getPaymentReference() != null ? beneficiaryRecord.getPaymentReference() : "")
                .mobileNumber(beneficiaryRecord.getMobileNumber() != null ? beneficiaryRecord.getMobileNumber() : "")
                .accountNumber(beneficiaryRecord.getAccountNumber() != null ? beneficiaryRecord.getAccountNumber() : "")
                .branchCode(beneficiaryRecord.getBranchCode() != null ? beneficiaryRecord.getBranchCode() : "")
                .bankCode(beneficiaryRecord.getBankCode() != null ? beneficiaryRecord.getBankCode() : "")
                .city(beneficiaryRecord.getCity() != null ? beneficiaryRecord.getCity() : "")
                .state(beneficiaryRecord.getState() != null ? beneficiaryRecord.getState() : "")
                .postCode(beneficiaryRecord.getPostCode() != null ? beneficiaryRecord.getPostCode() : "")
                .address(beneficiaryRecord.getAddress() != null ? beneficiaryRecord.getAddress() : "")
                .verificationStatus(beneficiaryRecord.getVerificationStatus() != null ? beneficiaryRecord.getVerificationStatus() : "")
                .address(beneficiaryRecord.getAddress() != null ? beneficiaryRecord.getAddress() : "")
                .build();
    }
}
