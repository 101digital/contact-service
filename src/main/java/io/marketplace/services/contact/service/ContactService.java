package io.marketplace.services.contact.service;


import io.marketplace.commons.exception.GenericException;
import io.marketplace.services.contact.entity.BeneficiaryEntity;
import io.marketplace.services.contact.mapper.BeneficiaryMapper;
import io.marketplace.services.contact.repository.BeneficiaryRepository;
import org.openapitools.model.BeneficiaryRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContactService {

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    @Autowired
    private BeneficiaryMapper beneficiaryMapper;

    public List<BeneficiaryRecord> getContactList(String userId, String searchText){

        List<BeneficiaryEntity> beneficiaryEntities = beneficiaryRepository.findAllByUserIdAndDisplayName(userId, searchText);
        List<BeneficiaryRecord> beneficiaryRecords = new ArrayList<>();

        for (BeneficiaryEntity e:beneficiaryEntities) {
            beneficiaryRecords.add(BeneficiaryRecord.builder()
                    .accountNumber(e.getAccountNumber())
                    .build());
        }

        return beneficiaryRecords;
    }

    public BeneficiaryRecord createBeneficiary(BeneficiaryRecord beneficiaryRecord){

        try{
            beneficiaryRepository.save(beneficiaryMapper.toBeneficiaryEntity(beneficiaryRecord));
            return beneficiaryRecord;
        }catch (Exception e){
            throw new GenericException("", "", "");
        }

    }
}
