package com.example.diarys;

import android.content.ContentValues;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.SharedPreferences.Editor;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class Edit extends AppCompatActivity implements OnClickListener {
    Button ButtonDelete, ButtonSave, ButtonCancel;
    EditText EditTextContent, EditTextTitle, EditTextEditAuthor;
    int tran = 0;
    String Author = "";
    MyDataBaseHelper dbHelper = new MyDataBaseHelper(this, "Note.db", null, 1);

    private void InitNote() {       //进行数据填装
        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this, "Note.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Note", new String[]{"id", "title", "content"}, "id=?", new String[]{tran + ""}, null, null, null);
        if (cursor.moveToNext()) {       //根据mainactivity传来的id值选择数据库中对应的行，将值返回
            do {
                String Title = cursor.getString(cursor.getColumnIndex("title"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                EditTextContent.setText(content);
                EditTextTitle.setText(Title);
            } while (cursor.moveToNext());
        }

        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        String name = pref.getString("author", "");      //通过sharedpreferences传递作者信息
        //Log.d("MainActivity","name is " + name);
        EditTextEditAuthor.setText(name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        EditTextContent = (EditText) findViewById(R.id.EditTextEditContent);
        EditTextTitle = (EditText) findViewById(R.id.EditTextEditTitle);
        ButtonCancel = (Button) findViewById(R.id.ButtonCancel);
        ButtonSave = (Button) findViewById(R.id.ButtonSave);
        ButtonDelete = (Button) findViewById(R.id.ButtonDelete);
        EditTextEditAuthor = findViewById(R.id.EditTextEditAuthor);


        ButtonCancel.setOnClickListener(this);
        ButtonSave.setOnClickListener(this);
        ButtonDelete.setOnClickListener(this);

        Intent intent = getIntent();
        tran = intent.getIntExtra("tran", -1);       //取出mainactivity传来的id值

        InitNote();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ButtonDelete:     //将对应的id行删除

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete("Note", "id=?", new String[]{tran + ""});
                Edit.this.setResult(RESULT_OK, getIntent());
                Edit.this.finish();
                break;
            case R.id.ButtonSave:       //保存该界面的数据
                SQLiteDatabase db1 = dbHelper.getWritableDatabase();
                Date date = new Date();
                ContentValues values = new ContentValues();
                String Title = String.valueOf(EditTextTitle.getText());
                String Content = String.valueOf(EditTextContent.getText());
                if (Title.length() == 0) {
                    Toast.makeText(this, "请输入一个标题", Toast.LENGTH_LONG).show();
                } else {
                    values.put("title", Title);
                    values.put("content", Content);
                    db1.update("Note", values, "id=?", new String[]{tran + ""});        //对数据进行更新
                    Edit.this.setResult(RESULT_OK, getIntent());
                    Edit.this.finish();
                }


                Author = String.valueOf(EditTextEditAuthor.getText());
                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putString("author", Author);      //写入作者信息
                editor.apply();

                break;


            case R.id.ButtonCancel:
                Edit.this.setResult(RESULT_OK, getIntent());
                Edit.this.finish();
                break;

        }

    }
}
