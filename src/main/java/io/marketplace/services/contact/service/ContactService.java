package io.marketplace.services.contact.service;

import io.marketplace.commons.exception.ApiResponseException;
import io.marketplace.commons.exception.BadRequestException;
import io.marketplace.commons.exception.ConflictErrorException;
import io.marketplace.commons.exception.GenericException;
import io.marketplace.commons.exception.InternalServerErrorException;
import io.marketplace.commons.exception.NotFoundException;
import io.marketplace.commons.logging.Error;
import io.marketplace.commons.logging.Logger;
import io.marketplace.commons.logging.LoggerFactory;
import io.marketplace.commons.model.ShortTermCache;
import io.marketplace.commons.model.event.EventMessage;
import io.marketplace.commons.utils.MembershipUtils;
import io.marketplace.services.contact.adapters.MembershipAdapter;
import io.marketplace.services.contact.adapters.WalletServiceAdapter;
import io.marketplace.services.contact.adapters.dto.UserListResponse;
import io.marketplace.services.contact.adapters.dto.WalletListResponse;
import io.marketplace.services.contact.entity.BeneficiaryEntity;
import io.marketplace.services.contact.mapper.BeneficiaryMapper;
import io.marketplace.services.contact.model.BeneficiaryData;
import io.marketplace.services.contact.model.BeneficiaryRecord;
import io.marketplace.services.contact.model.BeneficiaryResponse;
import io.marketplace.services.contact.model.PagingInformation;
import io.marketplace.services.contact.repository.BeneficiaryRepository;
import io.marketplace.services.contact.specifications.BeneficiarySpecification;
import io.marketplace.services.contact.utils.Constants;
import io.marketplace.services.contact.utils.ErrorCode;
import io.marketplace.services.pxchange.client.annotation.PXLogEventMessage;
import io.marketplace.services.pxchange.client.service.PXChangeServiceClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.marketplace.services.contact.utils.Constants.ACCOUNT_COLUMN;
import static io.marketplace.services.contact.utils.Constants.CREATED_AT_COLUMN;
import static io.marketplace.services.contact.utils.Constants.DELETE_ACTIVITY;
import static io.marketplace.services.contact.utils.Constants.DISPLAY_NAME;
import static io.marketplace.services.contact.utils.Constants.PAYMENT_REFERENCE;
import static io.marketplace.services.contact.utils.Constants.PERMISSION_GET_CONTACT_LIST;
import static io.marketplace.services.contact.utils.Constants.RECEIVING_THE_REQUEST_TO_DELETE_ACTIVITY;
import static io.marketplace.services.contact.utils.Constants.RECEIVING_THE_REQUEST_TO_SAVE_ACTIVITY;
import static io.marketplace.services.contact.utils.Constants.RECV_DELETE_REQUEST;
import static io.marketplace.services.contact.utils.Constants.RECV_SAVE_REQUEST;
import static io.marketplace.services.contact.utils.Constants.SEARCH_REQUEST_BUSINESS_DATA_BENEFICIARY;
import static io.marketplace.services.contact.utils.Constants.SUCCESS_REQUEST_TO_SAVE_CONTACT;
import static io.marketplace.services.contact.utils.ErrorCode.CONTACT_CREATION_DB_ERROR_CODE;
import static io.marketplace.services.contact.utils.ErrorCode.CONTACT_CREATION_DB_ERROR_MESSAGE;
import static io.marketplace.services.contact.utils.ErrorCode.CONTACT_CREATION_DUP_ERROR_CODE;
import static io.marketplace.services.contact.utils.ErrorCode.CONTACT_CREATION_DUP_MESSAGE;
import static io.marketplace.services.contact.utils.ErrorCode.CONTACT_CREATION_ERROR;
import static io.marketplace.services.contact.utils.ErrorCode.CONTACT_CREATION_ERROR_CODE;
import static io.marketplace.services.contact.utils.ErrorCode.CONTACT_DELETE_ERROR_CODE;
import static io.marketplace.services.contact.utils.ErrorCode.CONTACT_DELETE_MESSAGE;

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

    @Autowired
    private ShortTermCache shortermCached;

    @Value("${contact.lookup.max-attempts:3}")
    private Integer lookupContactAttempts;

    @Value("${contact.lookup.max-duration:60}")
    private Integer lookupContactAttemptsDuration;

    public BeneficiaryResponse getContactList(String userId, String searchText, Integer pageSizeValue,
            Integer pageNumber, List<String> listOrders) {

        Page<BeneficiaryEntity> beneficiaryEntities;

        boolean isAdmin = MembershipUtils.hasRole(Constants.SUPER_ROLE)
                || MembershipUtils.hasPermission(PERMISSION_GET_CONTACT_LIST);

        Integer pageNum = pageNumber != null && pageNumber > 0 ? pageNumber
                : Constants.DEFAULT_PAGE_NUMBER;
        Integer pageSize = pageSizeValue != null && pageSizeValue > 0 ? pageSizeValue
                : Constants.DEFAULT_PAGE_SIZE;

        // build order
        Sort.Direction direction = Sort.Direction.DESC;
        String fieldPassed = CREATED_AT_COLUMN;

        List<Sort.Order> sortOrders = new ArrayList<>();
        if (listOrders != null && listOrders.size() > 0) {
            for (String field : listOrders) {
                String[] fields = StringUtils.split(field, "-");
                if (fields.length > 1) {
                    if (DISPLAY_NAME.equalsIgnoreCase(fields[0])) {
                        fieldPassed = DISPLAY_NAME;
                        if (Constants.ORDER_ASCENT.equalsIgnoreCase(fields[1])) {
                            direction = Sort.Direction.ASC;
                        } else if (Constants.ORDER_DESCENT.equalsIgnoreCase(fields[1])) {
                            direction = Sort.Direction.DESC;
                        }
                    } else if (PAYMENT_REFERENCE.equalsIgnoreCase(fields[0])) {
                        fieldPassed = PAYMENT_REFERENCE;
                        if (Constants.ORDER_ASCENT.equalsIgnoreCase(fields[1])) {
                            direction = Sort.Direction.ASC;
                        } else if (Constants.ORDER_DESCENT.equalsIgnoreCase(fields[1])) {
                            direction = Sort.Direction.DESC;
                        }
                    } else if (ACCOUNT_COLUMN.equalsIgnoreCase(fields[0])) {
                        fieldPassed = ACCOUNT_COLUMN;
                        if (Constants.ORDER_ASCENT.equalsIgnoreCase(fields[1])) {
                            direction = Sort.Direction.ASC;
                        } else if (Constants.ORDER_DESCENT.equalsIgnoreCase(fields[1])) {
                            direction = Sort.Direction.DESC;
                        }
                    } else if (CREATED_AT_COLUMN.equalsIgnoreCase(fields[0])) {
                        fieldPassed = CREATED_AT_COLUMN;
                        if (Constants.ORDER_ASCENT.equalsIgnoreCase(fields[1])) {
                            direction = Sort.Direction.ASC;
                        } else if (Constants.ORDER_DESCENT.equalsIgnoreCase(fields[1])) {
                            direction = Sort.Direction.DESC;
                        }
                    }
                    sortOrders.add(new Sort.Order(direction, fieldPassed));
                }
            }
        }

        Pageable pageable = PageRequest.of((pageNum - 1), pageSize,
                Sort.by(sortOrders));

        if (isAdmin) {
            // for admin user return all the contacts
            Specification<BeneficiaryEntity> beneficiaryEntitySpecification = new BeneficiarySpecification(userId,
                    searchText);

            if (userId != null || searchText != null) {
                beneficiaryEntities = beneficiaryRepository.findAll(beneficiaryEntitySpecification, pageable);
            } else {
                beneficiaryEntities = beneficiaryRepository.findAll(pageable);
            }
        } else {
            // normal user can only get contacts under logging user id
            String loggedInUserId = MembershipUtils.getUserId();
            Specification<BeneficiaryEntity> beneficiaryEntitySpecification = new BeneficiarySpecification(
                    loggedInUserId, searchText);

            if (searchText != null) {
                beneficiaryEntities = beneficiaryRepository.findAll(beneficiaryEntitySpecification, pageable);
            } else {
                beneficiaryEntities = beneficiaryRepository.findAllByUserId(loggedInUserId, pageable);
            }
        }

        Integer totalCount = (int) beneficiaryEntities.getTotalElements();
        PagingInformation paging = PagingInformation.builder().totalRecords(totalCount).pageNumber(pageNum)
                .pageSize(pageSize).build();

        return BeneficiaryResponse.builder()
                .paging(paging)
                .data(loadRecords(beneficiaryEntities.getContent()))
                .build();
    }

    @PXLogEventMessage(activityName = Constants.RECEIVING_THE_REQUEST_TO_SAVE_ACTIVITY, eventTitle = "Create a new contact in the database", eventCode = Constants.RECV_SAVE_REQUEST)
    public BeneficiaryData createContact(BeneficiaryRecord beneficiaryRecord) {

        log.info("createContact request payload : {}", beneficiaryRecord.toString());

        try {

            List<BeneficiaryEntity> beneficiaryEntities = new ArrayList<>();
            boolean isAdmin = MembershipUtils.hasRole(Constants.SUPER_ROLE)
                    || MembershipUtils.hasPermission(PERMISSION_GET_CONTACT_LIST);

            String userId;
            if (!isAdmin) {
                userId = MembershipUtils.getUserId();
                log.info("Creating Beneficiary request for user id : " + userId);
            } else {
                userId = beneficiaryRecord.getUserId();
                log.info("Creating Beneficiary request for admin : " + userId);
            }

            if (userId == null) {
                throw new BadRequestException(CONTACT_CREATION_ERROR_CODE, CONTACT_CREATION_ERROR,
                        beneficiaryRecord.toString());
            }

            if (beneficiaryRecord.getAccountNumber().isEmpty() && beneficiaryRecord.getPaymentReference().isEmpty()) {
                // validation error, either account number or payment reference should provide
                throw new BadRequestException(CONTACT_CREATION_ERROR_CODE, CONTACT_CREATION_ERROR,
                        beneficiaryRecord.toString());
            }

            if (!beneficiaryRecord.getAccountNumber().isEmpty()) {
                beneficiaryEntities = beneficiaryRepository
                        .findAllByAccountNumberAndUserId(beneficiaryRecord.getAccountNumber(), userId);
            }

            if (beneficiaryEntities.isEmpty() && !beneficiaryRecord.getPaymentReference().isEmpty()) {
                beneficiaryEntities = beneficiaryRepository
                        .findAllByPaymentReferenceAndUserId(beneficiaryRecord.getPaymentReference(), userId);
            }

            if (!beneficiaryEntities.isEmpty()) {
                // throwing exception because logged in user is having contacts
                log.error(CONTACT_CREATION_DUP_MESSAGE, Error.of(CONTACT_CREATION_DUP_ERROR_CODE));
                throw new ConflictErrorException(CONTACT_CREATION_DUP_ERROR_CODE, CONTACT_CREATION_DUP_MESSAGE, userId);
            }

            // loggedIn user does not have contacts with same payment reference and account
            // numbers
            beneficiaryRepository.save(beneficiaryMapper.toBeneficiaryEntity(beneficiaryRecord, userId));

            // Generate event for adapter
            pxClient.addEvent(EventMessage.builder()
                    .activityName(RECEIVING_THE_REQUEST_TO_SAVE_ACTIVITY)
                    .eventTitle(SUCCESS_REQUEST_TO_SAVE_CONTACT)
                    .eventCode(RECV_SAVE_REQUEST)
                    .businessData(
                            beneficiaryRecord.getPaymentReference() != null ? beneficiaryRecord.getPaymentReference()
                                    : "N/A")
                    .build());

            return beneficiaryMapper.transformFromBeneficiaryRecordToBeneficiaryDto(beneficiaryRecord);

        } catch (ConflictErrorException ex) {
            throw new ConflictErrorException(CONTACT_CREATION_DUP_ERROR_CODE, CONTACT_CREATION_DUP_MESSAGE, "");
        } catch (BadRequestException ex) {
            throw new BadRequestException(CONTACT_CREATION_ERROR_CODE, CONTACT_CREATION_ERROR, "");
        } catch (Exception e) {
            log.error(CONTACT_CREATION_DB_ERROR_MESSAGE, Error.of(CONTACT_CREATION_DB_ERROR_CODE));
            throw new InternalServerErrorException(CONTACT_CREATION_DB_ERROR_CODE, e.getMessage(), null);
        }
    }

    public BeneficiaryData deleteContact(String contactId) {

        try {

            String loggedInUserId = MembershipUtils.getUserId();

            Optional<BeneficiaryEntity> beneficiaryRecord = beneficiaryRepository
                    .findByIdAndUserId(UUID.fromString(contactId), loggedInUserId);

            if (beneficiaryRecord.isPresent()) {
                beneficiaryRepository.deleteById(UUID.fromString(contactId));

                // Generate event for adapter
                pxClient.addEvent(EventMessage.builder()
                        .activityName(RECEIVING_THE_REQUEST_TO_DELETE_ACTIVITY)
                        .eventTitle(DELETE_ACTIVITY)
                        .eventCode(RECV_DELETE_REQUEST)
                        .businessData("user Id: " + loggedInUserId)
                        .build());

                return BeneficiaryData.builder()
                        .accountNumber(beneficiaryRecord.get().getAccountNumber())
                        .displayName(beneficiaryRecord.get().getDisplayName())
                        .paymentReference(beneficiaryRecord.get().getPaymentReference())
                        .build();

            } else {
                throw new ConflictErrorException(CONTACT_DELETE_ERROR_CODE, CONTACT_DELETE_MESSAGE, loggedInUserId);
            }

        } catch (ConflictErrorException e) {
            throw new ConflictErrorException(CONTACT_DELETE_ERROR_CODE, CONTACT_DELETE_MESSAGE, "");
        } catch (Exception e) {
            log.error(CONTACT_CREATION_DB_ERROR_MESSAGE, Error.of(CONTACT_CREATION_DB_ERROR_CODE));
            throw new GenericException(CONTACT_CREATION_DB_ERROR_CODE, e.getMessage(), "");
        }
    }

    public List<BeneficiaryData> getBeneficiaryInformationWithRateLimit(String mobileNumber, String accountNumber) {
        final String userId = MembershipUtils.getUserId();
        try {
            log.info(
                    "Start to lookup beneficiary by mobileNumber: {}, accountNumber: {}, limit: {}, duration: {}, userId: {}",
                    mobileNumber, accountNumber, lookupContactAttempts, lookupContactAttemptsDuration, userId);
            return shortermCached.runWithRateLimiter("lookup-beneficiary-" + userId, lookupContactAttempts,
                    Duration.ofMinutes(lookupContactAttemptsDuration),
                    () -> getBeneficiaryInformation(mobileNumber, accountNumber));
        } catch (ApiResponseException ex) {
            // Handle exception two many request
            if (HttpStatus.TOO_MANY_REQUESTS.value() == ex.getHttpStatus()) {
                // Add the event tracking
                pxClient.addEvent(EventMessage.builder()
                        .activityName(Constants.RECEIVING_THE_REQUEST_TO_GET_BENEFICIARY_ACTIVITY)
                        .eventTitle(ErrorCode.CONTACT_LOOKUP_LIMIT_ERROR_MESSAGE)
                        .eventCode(Constants.RECV_GET_BEN_REQUEST)
                        .businessData("user id " + userId)
                        .build());

                log.error(ErrorCode.CONTACT_LOOKUP_LIMIT_ERROR_MESSAGE,
                        Error.of(ErrorCode.CONTACT_LOOKUP_LIMIT_ERROR_CODE));

                throw ApiResponseException
                        .builder()
                        .httpStatus(HttpStatus.TOO_MANY_REQUESTS.value())
                        .code(ErrorCode.CONTACT_LOOKUP_LIMIT_ERROR_CODE)
                        .message(ErrorCode.CONTACT_LOOKUP_LIMIT_ERROR_MESSAGE)
                        .build();
            }
            throw new InternalServerErrorException(ErrorCode.CONTACT_LOOKUP_INTERNAL_ERROR_CODE,
                    ErrorCode.CONTACT_LOOKUP_INTERNAL_ERROR_MESSAGE, userId);
        }
    }

    public List<BeneficiaryData> getBeneficiaryInformation(String mobileNumber, String accountNumber) {

        log.info("getBeneficiaryInformation for mobileNumber : {} and accountNumber : {}", mobileNumber, accountNumber);
        String businessId = String.format(SEARCH_REQUEST_BUSINESS_DATA_BENEFICIARY, mobileNumber, accountNumber);

        WalletListResponse walletListResponse = null;

        if (mobileNumber != null && !mobileNumber.isEmpty()) {
            // get beneficiary details by mobile number
            UserListResponse userListResponse = membershipAdapter.getUserInformation(mobileNumber);

            if (!userListResponse.getData().isEmpty()) {
                // get the first userid and calling the wallet api
                walletListResponse = walletServiceAdapter
                        .getWalletInformation(userListResponse.getData().get(0).getUserId());
                log.info("wallet api response for userId : {} response: {}",
                        userListResponse.getData().get(0).getUserId(), walletListResponse);
            }
        } else if (accountNumber != null && !accountNumber.isEmpty()) {
            // get beneficiary details by account
            walletListResponse = walletServiceAdapter.getWalletInformationByAccountNumber(accountNumber);
            log.info("wallet api response for accountNumber : {} response: {}", accountNumber, walletListResponse);
        }

        if (walletListResponse != null && walletListResponse.getData() != null
                && !walletListResponse.getData().isEmpty()) {
            return beneficiaryMapper.transformFromWalletDtoToBeneficiaryType(walletListResponse.getData());
        } else {
            throw new NotFoundException(ErrorCode.WALLET_NOT_FOUND_ERROR_CODE,
                    "Wallet not found for the business id : " + businessId, businessId);
        }
    }

    List<BeneficiaryData> loadRecords(List<BeneficiaryEntity> beneficiaryEntities) {

        List<BeneficiaryData> beneficiaryDtoList = new ArrayList<>();

        for (BeneficiaryEntity beneficiaryEntity : beneficiaryEntities) {

            beneficiaryDtoList.add(
                    BeneficiaryData.builder()
                            .id(String.valueOf(beneficiaryEntity.getId()))
                            .accountNumber(beneficiaryEntity.getAccountNumber())
                            .displayName(beneficiaryEntity.getDisplayName())
                            .paymentReference(beneficiaryEntity.getPaymentReference())
                            .build());
        }

        return beneficiaryDtoList;
    }
}
