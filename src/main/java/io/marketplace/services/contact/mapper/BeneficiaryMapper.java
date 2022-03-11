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
                .userId(beneficiaryRecord.getUserId())
                .serviceCode(beneficiaryRecord.getServiceCode())
                .subServiceCode(beneficiaryRecord.getSubServiceCode())
                .displayName(beneficiaryRecord.getDisplayName())
                .paymentReference(beneficiaryRecord.getPaymentReference())
                .mobileNumber(beneficiaryRecord.getMobileNumber())
                .accountNumber(beneficiaryRecord.getAccountNumber())
                .branchCode(beneficiaryRecord.getBranchCode())
                .bankCode(beneficiaryRecord.getBankCode())
                .city(beneficiaryRecord.getCity())
                .state(beneficiaryRecord.getState())
                .postCode(beneficiaryRecord.getPostCode())
                .address(beneficiaryRecord.getAddress())
                .verificationStatus(beneficiaryRecord.getVerificationStatus())
                .address(beneficiaryRecord.getAddress())
                .build();
    }
}
