package com.example.memo;


import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    //DB名
    private static final String DATABASE_NAME = "notememo.db";
    //DB version
    private static final int DATABASE_VERSION = 1;

    //コンストラクタ
    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //CREATE分作成
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE notememo (");
        sb.append("_id INTEGER PRIMARY KEY,");
        sb.append("name TEXT,");
        sb.append("note TEXT");
        sb.append(");");
        String sql = sb.toString();

        try {
            //SQL実行
            db.execSQL(sql);
            System.out.println("DBOK");
        } catch (SQLException e) {
            System.out.println("DBエラー");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }


}
