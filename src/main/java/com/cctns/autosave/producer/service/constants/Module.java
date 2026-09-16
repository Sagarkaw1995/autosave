package com.cctns.autosave.producer.service.constants;

import com.cctns.autosave.producer.service.utility.ArrestValidation;
import com.cctns.autosave.producer.service.utility.BailValidation;
import com.cctns.autosave.producer.service.utility.FirValidation;
import com.cctns.autosave.producer.service.utility.GenericValidation;
import com.cctns.autosave.producer.service.utility.MlcValidation;
import lombok.Getter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import static com.cctns.autosave.producer.service.constants.AutosaveGridField.ACCUSED_NAME;
import static com.cctns.autosave.producer.service.constants.AutosaveGridField.ACCUSED_SERIAL_NUMBER;
import static com.cctns.autosave.producer.service.constants.AutosaveGridField.ACCUSED_VID;
import static com.cctns.autosave.producer.service.constants.AutosaveGridField.ARR_SURR_SR_NO;
import static com.cctns.autosave.producer.service.constants.AutosaveGridField.COMPLAINANT_NAME;
import static com.cctns.autosave.producer.service.constants.AutosaveGridField.DRAFT_DATE_TIME;
import static com.cctns.autosave.producer.service.constants.AutosaveGridField.DRAFT_ID;
import static com.cctns.autosave.producer.service.constants.AutosaveGridField.DRAFT_NUM;
import static com.cctns.autosave.producer.service.constants.AutosaveGridField.DRAFT_SRNO;
import static com.cctns.autosave.producer.service.constants.AutosaveGridField.FIR_REG_NUM;
import static com.cctns.autosave.producer.service.constants.AutosaveGridField.LAST_UPDATED;
import static com.cctns.autosave.producer.service.constants.AutosaveGridField.MLC_SUB_TYPE;
import static com.cctns.autosave.producer.service.constants.AutosaveGridField.MLC_TYPE;
import static com.cctns.autosave.producer.service.constants.AutosaveGridField.REG_DATE;
import static com.cctns.autosave.producer.service.constants.AutosaveGridField.REG_NUMBER;

/**
 * This enum class maps a module to : A bean validation group
 */
@Getter
public enum Module {

    GENERIC(
            Set.of(),
            GenericValidation.class,
            EnumSet.of(DRAFT_NUM, DRAFT_SRNO, DRAFT_ID, DRAFT_DATE_TIME, COMPLAINANT_NAME, REG_NUMBER, REG_DATE),
            EnumSet.of(COMPLAINANT_NAME, LAST_UPDATED)
    ),
    FIR(
            Set.of("FIR"),
            FirValidation.class,
            EnumSet.of(DRAFT_NUM, DRAFT_SRNO, DRAFT_ID, DRAFT_DATE_TIME, FIR_REG_NUM, COMPLAINANT_NAME),
            EnumSet.of(COMPLAINANT_NAME, LAST_UPDATED)
    ),
    BAIL(
            Set.of("BAIL"),
            BailValidation.class,
            EnumSet.of(DRAFT_NUM, DRAFT_SRNO, DRAFT_ID, DRAFT_DATE_TIME, ARR_SURR_SR_NO, ACCUSED_NAME),
            EnumSet.of(ACCUSED_NAME, LAST_UPDATED)
    ),
    ARREST_WARRANT(
            Set.of("ARREST_WARRANT"),
            ArrestValidation.class,
            EnumSet.of(DRAFT_NUM, DRAFT_SRNO, DRAFT_ID, DRAFT_DATE_TIME, ACCUSED_VID, ACCUSED_NAME, ACCUSED_SERIAL_NUMBER),
            EnumSet.of(ACCUSED_NAME, LAST_UPDATED)
    ),
    MLC(
            Set.of("MLC"),
            MlcValidation.class,
            EnumSet.of(DRAFT_NUM, DRAFT_SRNO, DRAFT_ID, DRAFT_DATE_TIME, COMPLAINANT_NAME),
            EnumSet.of(MLC_TYPE, MLC_SUB_TYPE, COMPLAINANT_NAME, LAST_UPDATED)
    ),
    CRIME(
            Set.of("CRIME"),
            MlcValidation.class,
            EnumSet.of(DRAFT_NUM, DRAFT_SRNO, DRAFT_ID, DRAFT_DATE_TIME, FIR_REG_NUM, REG_DATE),
            EnumSet.of(LAST_UPDATED)
    );


    private final Set<String> moduleNames; //Module name of respective module

    @Getter
    private final Class<?> validationGroup; //Validation group of respective module

    @Getter
    private final Set<AutosaveGridField> allowedGridFields; //Allowed fields for respective modules

    @Getter
    private final Set<AutosaveGridField> updatableFields; //Updatable fields for respective modules

    Module(Set<String> moduleNames, Class<?> validationGroup, Set<AutosaveGridField> allowedGridFields,
           Set<AutosaveGridField> updatableFields) {
        this.moduleNames = moduleNames;
        this.validationGroup = validationGroup;
        this.allowedGridFields = allowedGridFields;
        this.updatableFields = updatableFields;
    }

    public static Module fromModuleName(String moduleName) {
        return Arrays.stream(values())
                .filter(m -> m != GENERIC)
                .filter(m -> m.moduleNames.contains(moduleName))
                .findFirst()
                .orElse(GENERIC);
    }
}
