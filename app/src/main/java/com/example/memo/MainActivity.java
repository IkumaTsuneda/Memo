package com.example.memo;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnSave;
    Button btnDelete;
    ListView lvMemoList = null;
    int memoId = -1; //id
    int save_select = 0; //0:新規追加  1:編集

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ボタンオブジェクト
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.buttonDelete);
        lvMemoList = findViewById(R.id.lvMemoList);

        //リスト表示
        memoListDisplay();

        lvMemoList.setOnItemClickListener(new ListItemClickListener());
    }

    //追加ボタン
    public void onAddButtonClick(View view){

        //タイトル
        EditText etTitle = findViewById(R.id.etTitle);
        etTitle.setText("新しいメモ");
        //内容
        EditText etNote = findViewById(R.id.etNote);
        etNote.setText("");
        //保存ボタン活性化
        btnSave.setEnabled(true);

        save_select = 0;//0:新規追加
    }

    //保存ボタン
    public void onSaveButtonClick(View view){

        //タイトル取得
        EditText etTitle = findViewById(R.id.etTitle);
        String title = etTitle.getText().toString();

        //内容取得
        EditText etNote = findViewById(R.id.etNote);
        String note = etNote.getText().toString();

        //DB更新用オブジェクト
        DatabaseHelper helper = new DatabaseHelper(MainActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            // 編集からの保存時
            if(save_select == 1){
                //UPDATE文作成
                String sqlUpdate = "UPDATE notememo SET name = ?, note = ? WHERE _id =?";
                SQLiteStatement stmt = db.compileStatement(sqlUpdate);
                stmt.bindString(1,title);
                stmt.bindString(2,note);
                stmt.bindLong(3,memoId);

                //SQL実行
                stmt.executeUpdateDelete();
            }else{
                //新規作成の場合
                //INSERT文作成
                String sqlInsert = "INSERT INTO notememo (name, note) VALUES(?, ?)";
                SQLiteStatement stmt = db.compileStatement(sqlInsert);
                stmt.bindString(1, title);
                stmt.bindString(2, note);

                //SQL実行
                stmt.executeInsert();

            }
        }catch (SQLException e){
            System.out.println("DBエラー");
        }finally {
            db.close();
        }

        //テキスト初期化
        etTitle.setText("");
        etNote.setText("");
        //ボタン非活性化
        btnSave.setEnabled(false);
        btnDelete.setEnabled(false);

        //リスト再表示
        memoListDisplay();
    }

    //メモリスト表示
    private void  memoListDisplay(){

        //DB参照用オブジェクト
        DatabaseHelper helper = new DatabaseHelper(MainActivity.this);
        SQLiteDatabase db = helper.getReadableDatabase();

        try {
            //SELECT文
            String sql = "SELECT _id,name FROM notememo";
            //実行結果格納
            Cursor cursor = db.rawQuery(sql, null);
            //リスト表示項目
            String[] from = {"name"};
            int[] to = {android.R.id.text1};
            //表示項目設定
            SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this,android.R.layout.simple_list_item_1,cursor,from,to,0);
            lvMemoList.setAdapter(simpleCursorAdapter);

        }catch (SQLException e){
            System.out.println("DBエラー");
        }finally {
            db.close();
        }

    }

    //Listクリック時のリスナークラス
    private class ListItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            memoId = (int)id; //id
            save_select = 1; //1:編集

            //ボタン活性化
            btnSave.setEnabled(true);
            btnDelete.setEnabled(true);

            //DB参照用オブジェクト
            DatabaseHelper helper = new DatabaseHelper(MainActivity.this);
            SQLiteDatabase db = helper.getReadableDatabase();

            try {
                //SELECT文
                String sql = "SELECT name, note FROM notememo WHERE _id = "+memoId;
                //実行結果格納
                Cursor cursor = db.rawQuery(sql, null);
                String note = "";
                String title = "";

                while(cursor.moveToNext()){
                    //idに紐づく内容を取得
                    int idxTitle = cursor.getColumnIndex("name");
                    title = cursor.getString(idxTitle);

                    int idxNote = cursor.getColumnIndex("note");
                    note = cursor.getString(idxNote);
                }

                //タイトル取得
                EditText etTitle = findViewById(R.id.etTitle);
                etTitle.setText(title);

                //内容取得
                EditText etNote = findViewById(R.id.etNote);
                etNote.setText(note);

            }catch (SQLException e){
                System.out.println("DBエラー");
            }finally {
                db.close();
            }
        }
    }

    // 削除ボタン
    public void onDeleteButtonClick(View view){
        //DB更新用オブジェクト
        DatabaseHelper helper = new DatabaseHelper(MainActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            //DELETE文作成
            String sqlDelete = "DELETE FROM notememo WHERE _id = ?";
            SQLiteStatement stmt = db.compileStatement(sqlDelete);
            stmt.bindLong(1,memoId);

            //SQL実行
            stmt.executeUpdateDelete();

        }catch (SQLException e) {
            System.out.println("DBエラー");
        }finally {
                db.close();
        }

            //タイトル初期化
            EditText etTile = findViewById(R.id.etTitle);
            etTile.setText("");
            //内容初期化
            EditText etNote = findViewById(R.id.etNote);
            etNote.setText("");

            //ボタン非活性化
            btnDelete.setEnabled(false);
            btnSave.setEnabled(false);

            //リスト再表示
            memoListDisplay();

        }


    }