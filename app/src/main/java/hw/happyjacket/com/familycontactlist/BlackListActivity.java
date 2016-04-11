package hw.happyjacket.com.familycontactlist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class BlackListActivity extends AppCompatActivity {
    private final BlackListMaster handler = new BlackListMaster(BlackListActivity.this);
    private List<String> blackList;
    private TextView title;
    private ListView blackListView;
    ArrayAdapter<String> adapter;
    FloatingActionButton fab;
    private final int MENU_UPDATE_ID = 0, MENU_DELETE_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);

        blackList = handler.getTheBlackList();
        InitView();
    }

    private void SetTitle() {
        title.setText(String.format("目前有%d个黑名单项，长按列表可编辑", blackList.size()));
    }

    private void InitView() {
        title = (TextView) findViewById(R.id.BL_title);
        SetTitle();
        adapter = new ArrayAdapter<String>(BlackListActivity.this, android.R.layout.simple_list_item_1, blackList);
        blackListView = (ListView) findViewById(R.id.black_list_view);
        blackListView.setAdapter(adapter);
        registerForContextMenu(blackListView);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(BlackListActivity.this);
                dialog.setTitle("请输入想要拉黑的号码");
                dialog.setIcon(android.R.drawable.ic_lock_lock);
                final EditText phoneNumberText = new EditText(BlackListActivity.this);
                phoneNumberText.setInputType(InputType.TYPE_CLASS_PHONE);
                dialog.setView(phoneNumberText);
                dialog.setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String phoneNumber = phoneNumberText.getText().toString();
                        boolean result = !phoneNumber.isEmpty() && handler.add(phoneNumber);
                        if (result) {
                            Toast.makeText(BlackListActivity.this, String.format("电话号码%s已加入黑名单", phoneNumber), Toast.LENGTH_SHORT).show();
                            blackList.add(0, phoneNumber);
                            adapter.notifyDataSetChanged();
                            SetTitle();
                        } else
                            Toast.makeText(BlackListActivity.this, String.format("电话号码%s已在黑名单中，无需重复加入", phoneNumber), Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.setNegativeButton("取消", null);
                dialog.show();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, MENU_UPDATE_ID, 0, "更新号码");
        menu.add(0, MENU_DELETE_ID, 1, "移出黑名单");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        final int position = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;
        AlertDialog.Builder dialog = new AlertDialog.Builder(BlackListActivity.this);
        final String thePhoneNumber = blackList.get(position);

        switch (id) {
            case MENU_UPDATE_ID:
                final EditText phoneNumberText = new EditText(BlackListActivity.this);
                phoneNumberText.setInputType(InputType.TYPE_CLASS_PHONE);
                phoneNumberText.setText(thePhoneNumber);
                dialog = new AlertDialog.Builder(BlackListActivity.this);
                dialog.setView(phoneNumberText);
                dialog.setTitle("编辑黑名单号码");
                dialog.setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.delete(thePhoneNumber);
                        String newPhoneNumber = phoneNumberText.getText().toString();
                        handler.add(newPhoneNumber);
                        blackList.set(position, newPhoneNumber);
                        adapter.notifyDataSetChanged();
                    }
                });
                dialog.setNegativeButton("取消", null);
                dialog.show();
                break;
            case MENU_DELETE_ID:
                dialog.setTitle("移出黑名单");
                dialog.setMessage(String.format("您是否确定要将号码%s移出黑名单？", thePhoneNumber));
                dialog.setPositiveButton("移出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.delete(thePhoneNumber);
                        blackList.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(BlackListActivity.this, String.format("已将号码%s移出黑名单", thePhoneNumber), Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.setNegativeButton("取消", null);
                dialog.show();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
}