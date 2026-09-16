package com.cctns.autosave.producer.service.constants;

import lombok.Data;

@Data
public class Constants {
    private Constants() {
        super();
    }

    //Date and time constants :
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm";
    public static final String ZONE_ID = "Asia/Kolkata";

    //Validation Response Code :
    public static final String PARSE_MAPPING_ERRORS = "EXF0001";
    public static final String VALIDATION_ERRORS = "EXF0002";
    public static final String DATABASE_CONSTRAINTS_ERRORS = "EXF0003";
    public static final String ENTITY_NOT_FOUND_ERRORS = "EXF0004";
    public static final String LAZY_INIT_ERRORS = "EXF0005";
    public static final String ILLEGAL_ARGS_ERRORS = "EXF0006";
    public static final String CONCURRENT_UPDATE_CONFLICT = "EXF0007";
    public static final String TRANSACTION_FAILURE_ERROR = "EXF0008";
    public static final String FEIGN_ERRORS = "EXF0009";
    public static final String FALLBACK_ERRORS = "EXF0010";
    public static final String METHOD_ARGUMENT_NOT_VALID = "EXF0011";
    public static final String VALIDATION_FAILED_EXC = "VFEX";

    public static final String SQL_ERROR = "EXF0001";
    public static final String EX0001 = "CONSTRAINT SQL ERROR";
    public static final String EX0002 = "SQL Error";
    public static final String EX0003 = "UNIQUE KEY CONSTRAINT 1";
    public static final String NOT_FOUND = "EXF0002";
    public static final String CONSTRAINT_SQL_ERROR = "EXF0003";
    public static final String LIST_NOT_FOUND = "EXF0004";

    //Success Message :
    public static final String SUCCESS = "SUCCESS";
    public static final String DRAFT_CREATED_SUCCESSFULLY = "DCS0001";
    public static final String SAVE_DRAFT_SUCCESSFULLY = "SDS0001";
    public static final String FETCH_DRAFT_SUCCESSFULLY = "FDS0001";
    public static final String INVALID_HEADER_MISSING_COMMON_PARAMS_EXCEPTION = "FDS0001";
    public static final String INVALID_HEADER_FORMAT_EXCEPTION = "FDS0001";

    //Fail Message :
    public static final String UNIQUE_KEY_CONSTRAINT_ERROR = "EXF0005";
    public static final String DATA_NOT_FOUND = "Data Not Found";
    public static final String DATA_FOUND = " Data fetched successfully";

    //Autosave Supported Service Constants :
    public static final String COMPLAINANT = "COMPL";
    public static final String MLC = "MLC";
    public static final String MISSING_PERSON = "MISSING";
    public static final String FIR = "FIR";
    public static final String NCR = "NCR";
    public static final String UIFP = "UIFP";
    public static final String UIDB = "UIDB";
    public static final String ARREST_MEMO = "ARREST_MEMO";
    public static final String PREVENTIVE_ACTION = "PREVENTIVE_ACTION";
    public static final String STRANGER_ROLL = "STRANGER_ROLL";

    //Autosave Service Constants :
    public static final String DRAFT_NUMBER_DELIMITER = "_";
    public static final String SHARD_TAG_START ="{";
    public static final String SHARD_TAG_END ="}";
    public static final Integer SHARD_COUNT = 32;

    //Expression for validation :
    public static final String VALIDATION_EXPRESSION = "^[A-Z]+(?:[_/][A-Z]+)*_\\d+_\\{\\d+}$";

    //Constants For Validation :
    public static final String STAFF_ID_NOT_NULL_MSG = "STAFF ID REQUIRED";
    public static final String LANG_CD_NOT_NULL_MSG = "LANG CD REQUIRED";
    public static final String OFFICE_CD_NOT_NULL_MSG = "OFFICE CD REQUIRED";
    public static final String STATE_ID_NOT_NULL_MSG = "STATE ID REQUIRED";
    public static final String ROLES_NOT_EMPTY_MSG = "ROLES REQUIRED";

    //Encryption :
    public static final String SECRET_KEY_INIT_FAILED_EX = "SECKEYINITFAILEX";
    public static final String ENCRYPTION_FAILED_EX = "ENCFAILEX";
    public static final String DECRYPTION_FAILED_EX = "DECFAILEX";

    public static final String ALGORITHM = "AES/GCM/NoPadding";
    public static final String ENCRYPTION = "AES";
    public static final int GCM_TAG_LENGTH = 128; // bits
    public static final int GCM_IV_LENGTH = 12; // bytes (96 bits)
    public static final int AES_KEY_SIZE = 32;

    //Module Name Validation :
    public static final String FINAL_FORM_MODULE_NAME_VALIDATION = "FINALR";
    public static final String FIR_MODULE_NAME_VALIDATION = "FIR";
    public static final String BAIL_CANCEL_MODULE = "BAIL/CANCELLATION";
    public static final String ARREST_WARRANT_MODULE = "ARREST_WARRANT";
    public static final String MLC_MODULE_NAME_VALIDATION = "MLC";
    public static final String CRIME_MODULE = "CRIME";

    //Script Validation :
    public static final String SCRIPT_REGEX = "<[^>]*>";
}
