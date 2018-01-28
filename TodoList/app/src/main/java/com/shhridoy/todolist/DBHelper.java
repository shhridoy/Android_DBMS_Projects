package com.shhridoy.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dream Land on 1/24/2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "todoList.db";
    private static final int DB_VERSION = 1;

    private static final String TODO_TABLE = "todo_list";
    private static final String ID = "id";
    private static final String TODO = "todo";
    private static final String DATE = "date";
    private static final String TIME = "time";

    private static final String CREATE_TABLE = "CREATE TABLE " + TODO_TABLE +
            "( " +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TODO + " TEXT NOT NULL, " +
            DATE + " TEXT NOT NULL, " +
            TIME + " TEXT);";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TODO_TABLE;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void insertData(String todo, String date, String time) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TODO, todo);
        contentValues.put(DATE, date);
        contentValues.put(TIME, time);
        this.getWritableDatabase().insertOrThrow(TODO_TABLE, "", contentValues);
        this.getWritableDatabase().close();
    }

    public boolean deleteData(int id) {
        int result = this.getWritableDatabase().delete(TODO_TABLE, ID+" = "+id, null);
        this.getWritableDatabase().close();
        return result > 0;
    }

    public boolean updateData(int id, String todo, String date, String time){
        ContentValues cv = new ContentValues();
        cv.put(TODO, todo);
        cv.put(DATE, date);
        cv.put(TIME, time);
        int reslt = this.getWritableDatabase().update(TODO_TABLE, cv, ID+" =?", new String[]{String.valueOf(id)});
        this.getWritableDatabase().close();
        return reslt > 0;
    }

    public Cursor getData() {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM "+ TODO_TABLE,null);
        return c;
    }
}
