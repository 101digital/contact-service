package io.marketplace.services.contact.mapper;

import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;
import io.marketplace.commons.utils.MembershipUtils;
import io.marketplace.services.contact.entity.BeneficiaryEntity;
import io.marketplace.services.contact.model.BeneficiaryRecord;
import io.marketplace.services.contact.utils.Constants;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BeneficiaryMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeneficiaryMapper.class);

    public BeneficiaryEntity toBeneficiaryEntity(BeneficiaryRecord beneficiaryRecord){

        boolean isAdmin = MembershipUtils.hasRole(Constants.SUPER_ROLE);
        String userId;

        if(!isAdmin)  {
            userId = MembershipUtils.getUserId();
            LOGGER.info("Creating Beneficiary request for user id : " + userId);
        }else{
            userId = beneficiaryRecord.getUserId();
            LOGGER.info("Creating Beneficiary request for admin : " + userId);
        }

        return BeneficiaryEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
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
                .verificationStatus(beneficiaryRecord.getVerificationStatus() != null ? beneficiaryRecord.getVerificationStatus() : "")
                .address(beneficiaryRecord.getAddress())
                .build();
    }
}
