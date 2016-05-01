package hw.happyjacket.com.familycontactlist.MyDirPicker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Stack;

import hw.happyjacket.com.familycontactlist.R;

public class DirPicker extends AppCompatActivity {
    public static final int TO_PICK_A_DIR = 233;
    public static final String PATH_KEY = "path";
    public static final String ACTION = "MY_DIR_PICKER";
    public static final String OriDir = "/storage/emulated/0";
    private File currentFile;
    private ListView dir_list;
    private DirAdapter dirAdapter;
    private ArrayList<DirDescriptor> data;
    private Stack<File> thePath;
    private Button back, select;
    private AlertDialog.Builder dlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dir_picker);

        thePath = new Stack<>();
        data = new ArrayList<>();
        dirAdapter = new DirAdapter(DirPicker.this, R.layout.dir_list_item, data);

        InitView();

        currentFile =  new File(OriDir);
        // /storage/emulated/0/
        refreshView(true);
    }

    private void InitView() {
        dir_list = (ListView) findViewById(R.id.dir_list);
        dir_list.setAdapter(dirAdapter);
        dir_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentFile = data.get(position).getDir();
                refreshView(true);
            }
        });

        dlg = new AlertDialog.Builder(DirPicker.this);
        dlg.setTitle("选中目录");
        dlg.setNegativeButton("取消", null);
        dlg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ReadyToQuit();
            }
        });

        back = (Button) findViewById(R.id.back_to_last_dir);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToLastDir();
            }
        });

        select = (Button) findViewById(R.id.pick_this_dir);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.setMessage("您当前选择的目录是：" + currentFile.getAbsolutePath());
                dlg.show();
            }
        });
    }

    private void refreshView(boolean addToStack) {
        data.clear();
        File[] files = currentFile.listFiles();
        if (files != null) {
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return lhs.getName().toString().compareToIgnoreCase(rhs.getName().toString());
                }
            });

            for (File f : files) {
                if (f.isDirectory() && f.getName().charAt(0) != '.')
                    data.add(new DirDescriptor(f));
            }
        }

        if (addToStack) {
            thePath.push(currentFile);
            updateBackBtnText();
        }

        dirAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backToLastDir();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void backToLastDir() {
        if (thePath.size() > 1) {
            thePath.pop();
            currentFile = thePath.peek();
            refreshView(false);
            updateBackBtnText();
        } else if (thePath.size() == 1) {
            ReadyToQuit();
        }
    }

    private void ReadyToQuit() {
        Intent from = getIntent();
        Intent intent = new Intent(from.getAction());
        intent.putExtra(PATH_KEY, currentFile.getAbsolutePath());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void updateBackBtnText() {
        if (thePath.size() > 1)
            back.setText("上一级");
        else
            back.setText("取消选择");
    }
}