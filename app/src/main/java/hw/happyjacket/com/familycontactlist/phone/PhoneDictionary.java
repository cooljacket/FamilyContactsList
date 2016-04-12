package hw.happyjacket.com.familycontactlist.phone;
import android.provider.CallLog;

import hw.happyjacket.com.familycontactlist.phone.database.DataBaseDictionary;

/**
 * Created by root on 16-3-27.
 */

/*Store the basic value*/
public class PhoneDictionary {

    public static final String NUMBER = CallLog.Calls.NUMBER;
    public static final String NAME = CallLog.Calls.CACHED_NAME;
    public static final String TYPE = CallLog.Calls.TYPE;
    public static final String DURATION = CallLog.Calls.DURATION;
    public static final String DATE = CallLog.Calls.DATE;
    public static final String ID = CallLog.Calls._ID;
    public static final String READ = CallLog.Calls.IS_READ;
    public static final String DATE_SAVE = "date_save";
    public static final String LOCATION = "location";
    public static final String SHARE_PREFERANCE_NAME = "listen";
    public static final String SHARE_PREFERANCE_KEY = "catch";
    public static final String MAIN_OPTION = "main";
    public static final String CONTENT_OPTION = "content";
    public static final String CALLLOG_OPTION = "calllog";
    public static final String DEFAULT_DB = DataBaseDictionary.CALLLOG;
    public static final String DEFAULT_TABLE = DataBaseDictionary.CallLog;
    public static final int BLUETOOTH_FOUND = 1;
    public static final int BLUETOOTH_FINISHED = 2;
    public static final int INCOMMING = 1;
    public static final int OUTGOING = 2;
    public static final int MISSED = 3;
    public static final int MAINADAPTER = 1;
    public static final int CALLLOGADAPTER = 2;
    public static final int CONTENT1 = 1;
    public static final int CONTENT2 = 2;
    public static final int MAIN_OPTIONS = 0;
    public static final int CONTENT_OPTIONS1 = 1;
    public static final int CONTENT_OPTIONS2 = 2;
    public static final int CALLLOG_OPTIONS = 3;
    public static final String TYPE_LIST[] = {"nothing","来电","拨出","响铃"};
    public static final String PhoneAll[] = {ID,NUMBER,NAME,DATE,TYPE,DURATION};
    public static final String PhoneDetail[] = {ID,DATE,TYPE,DURATION};
    public static final String PhoneCallLog[] = {ID,NUMBER,DATE,TYPE,DURATION};
    public static final String TEST[] = {NUMBER,NAME,DATE,ID};
    public static final String MainItems[] = {"呼叫","删除","添加黑名单"};
    public static final String CallLogItems[] = {"呼叫","删除"};
    public static final String ContentItems1[] = {"呼叫"};
    public static final String ContentItems2[] = {"呼叫","删除"};
    public static final String PhoneCallNumber[] = {"1","2","3","4","5","6","7","8","9","*","0","#"};
    public static final String PhoneCallAlphabet[] = {"","abc","def","ghi","jkl","nmo","pqrs","tuv","wxyz",",","+",""};

}
