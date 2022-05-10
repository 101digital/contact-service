package io.marketplace.services.contact.mapper;

import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;
import io.marketplace.services.contact.entity.BeneficiaryEntity;
import io.marketplace.services.contact.model.BeneficiaryData;
import io.marketplace.services.contact.model.BeneficiaryRecord;
import io.marketplace.services.contact.model.Wallet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class BeneficiaryMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeneficiaryMapper.class);

    public BeneficiaryEntity toBeneficiaryEntity(BeneficiaryRecord beneficiaryRecord, String userId){



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

    public List<BeneficiaryData> transformFromWalletDtoToBeneficiaryType(List<Wallet> walletDtos){

        List<BeneficiaryData> beneficiaryDtoList = new ArrayList<>();

        for(Wallet walletObject : walletDtos){

            beneficiaryDtoList.add(BeneficiaryData.builder()
                    .accountNumber(walletObject.getBankAccount().getAccountNumber())
                    .displayName(walletObject.getBankAccount().getAccountHolderName())
                    .paymentReference(walletObject.getBankAccount().getAccountId())
                    .build());
        }

        return beneficiaryDtoList;
    }

    public BeneficiaryData transformFromBeneficiaryRecordToBeneficiaryDto(BeneficiaryRecord beneficiaryRecord){

        return BeneficiaryData.builder()
                .accountNumber(beneficiaryRecord.getAccountNumber())
                .displayName(beneficiaryRecord.getDisplayName())
                .paymentReference(beneficiaryRecord.getPaymentReference())
                .build();

    }
}
