package hw.happyjacket.com.familycontactlist.phone.database;

import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

/**
 * Created by root on 16-3-30.
 */
public class DataBaseDictionary {

    public static final String CALLLOG = "CallLog.db";
    public static final int databaseVersion = 1;
    public static final String CallLog = "CallLog";
    public static final String CallLog_Projection[] = {PhoneDictionary.ID,PhoneDictionary.NUMBER,PhoneDictionary.NAME,PhoneDictionary.DATE,PhoneDictionary.TYPE};
    public static final int DATABASE_INDEX_OF_ID = 0;
    public static final int DATABASE_INDEX_OF_NUMBER = 1;
    public static final int DATABASE_INDEX_OF_NAME = 2;
    public static final int DATABASE_INDEX_OF_DATE = 3;
    public static final int DATABASE_INDEX_OF_TYPE = 4;
    public static final String [] CREATE = new String[]{
                    "create table CallLog (_id integer, number text primary key, name text, date integer, type integer)"
    };


}
