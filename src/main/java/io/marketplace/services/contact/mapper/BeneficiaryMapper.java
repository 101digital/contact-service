package io.marketplace.services.contact.mapper;

import io.marketplace.services.contact.entity.BeneficiaryEntity;
import org.openapitools.model.BeneficiaryRecord;
import org.springframework.stereotype.Component;
import lombok.Builder;

@Component
public class BeneficiaryMapper {

    public BeneficiaryEntity toBeneficiaryEntity(BeneficiaryRecord beneficiaryRecord){
        return BeneficiaryEntity.builder()
                .accountNumber(beneficiaryRecord.getAccountNumber())
                .address(beneficiaryRecord.getAddress())
                .bankCode(beneficiaryRecord.getBankCode())
                .branchCode(beneficiaryRecord.getBranchCode())
                .city(beneficiaryRecord.getCity())
                .build();
    }
}
