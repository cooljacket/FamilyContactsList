package hw.happyjacket.com.familycontactlist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class FamilyMoney extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_money);

        List<FamilyRecord> data = new ArrayList<>();
        data.add(new FamilyRecord("Jacket", 23));
        data.add(new FamilyRecord("Jacket", FamilyRecord.MINUTE*3));
        data.add(new FamilyRecord("Tom", FamilyRecord.HOUR*5));
        data.add(new FamilyRecord("Tim", FamilyRecord.DAY*32));

        FamilyRecordAdapter adapter = new FamilyRecordAdapter(FamilyMoney.this, R.layout.family_record_item, data);
        ListView fmList = (ListView) findViewById(R.id.families);
        fmList.setAdapter(adapter);
    }
}