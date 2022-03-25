package io.marketplace.services.contact.utils;

public final class ErrorCode {

    public static final String CONTACT_CREATION_DB_ERROR_CODE = "092.01.500.01";
    public static final String CONTACT_CREATION_DB_ERROR_MESSAGE = "Error occurred when creating new contact record";

    public static final String WALLET_NOT_FOUND_ERROR_CODE = "092.01.401.02";

    public static final String USER_GET_VIA_MOBILE_ERROR_CODE = "092.01.500.03";
    public static final String USER_GET_VIA_MOBILE_ERROR_CODE_MESSAGE = "Error occurred when getting user information using mobile number";

    public static final String WALLET_SEARCH_VIA_USER_ERROR_CODE = "092.01.500.04";
    public static final String WALLET_SEARCH_VIA_USER_ERROR_MESSAGE = "Error occurred when getting wallet information using userId";

    public static final String WALLET_SEARCH_VIA_ACCOUNT_ERROR_CODE = "092.01.500.05";
    public static final String WALLET_SEARCH_VIA_ACCOUNT_ERROR_MESSAGE = "Error occurred when getting wallet information using account number";

}
