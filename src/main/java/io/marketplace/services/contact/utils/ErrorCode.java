package io.marketplace.services.contact.utils;

public final class ErrorCode {

    public static final String CONTACT_CREATION_DB_ERROR_CODE = "092.01.500.01";
    public static final String CONTACT_CREATION_DB_ERROR_MESSAGE = "Error occurred when creating new contact record";

    public static final String WALLET_NOT_FOUND_ERROR_CODE = "092.01.401.02";

    public static final String USER_GET_VIA_MOBILE_ERROR_CODE = "092.01.500.03";
    public static final String USER_GET_VIA_MOBILE_ERROR_CODE_MESSAGE = "Error occurred when getting user information using mobile number";

    public static final String WALLET_SEARCH_VIA_USER_ERROR_CODE = "092.01.500.04";
    public static final String WALLET_SEARCH_VIA_USER_ERROR_MESSAGE = "Error occurred when getting wallet information using userId";

    public static final String WALLET_SEARCH_VIA_ACCOUNT_ERROR_CODE = "092.01.404.05";
    public static final String WALLET_SEARCH_VIA_ACCOUNT_ERROR_MESSAGE = "Error occurred when getting wallet information using account number";

    public static final String KAFKA_CONSUMER_ERROR_MESSAGE = "Error in consuming kafka message for the topic :";
    public static final String KAFKA_CONSUMER_ERROR_CODE = "092.01.500.06";

    public static final String CONTACT_CREATION_DUP_MESSAGE = "Contact creation failed due to paymentReference/accountNumber already exist";
    public static final String CONTACT_CREATION_DUP_ERROR_CODE = "092.01.409.07";

    public static final String CONTACT_CREATION_ERROR = "Contact creation failed due to invalid request";
    public static final String CONTACT_CREATION_ERROR_CODE = "092.01.400.01";

    public static final String CONTACT_DELETE_MESSAGE = "Contact id not found in the system for the user";
    public static final String CONTACT_DELETE_ERROR_CODE = "092.01.409.08";
}
