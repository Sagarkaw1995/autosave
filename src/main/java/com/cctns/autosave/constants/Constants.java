package com.cctns.autosave.constants;

import lombok.Data;

@Data
public class Constants {

	private Constants() {
		super();
	}

	public static final String SUCCESS = "SUCCESS";

	public static final String TIME_FORMAT = "HH:mm:ss";
	public static final String DATE_FORMAT = "dd/MM/yyyy";
	public static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm";
	public static final String success = "GDSaved0001";
	public static final String failed = "GDSaved0002";
	public static final String CIRCUIT_OPEN_FAILURE = "GDSaved0003";
	public static final String SQL_ERROR = "EXF0001";
	public static final String EX0001 = "CONSTRAINT SQL ERROR";
	public static final String EX0002 = "SQL Error";
	public static final String EX0003 = "UNIQUE KEY CONSTRAINT 1";
	public static final String NOT_FOUND = "EXF0002";
	public static final String CONSTRAINT_SQL_ERROR = "EXF0003";
	public static final String LIST_NOT_FOUND = "EXF0004";
	public static final String UNIQUE_KEY_CONSTRAINT_ERROR = "EXF0005";
	public static final String ENTRY_NOT_SUBMITTED="N";
	public static final String ENTRY_SUBMITTED="Y";
	public static final String COMPLETED_RECORD_STATUS="C";


	//Constants :
	public static final String COMPLAINANT = "COMPL";
	public static final String MLC = "MLC";
	public static final String MISSING_PERSON = "MISSING";
	public static final String FIR = "FIR";
	public static final String NCR = "NCR";
	public static final String UIFP = "UIFP";
}
