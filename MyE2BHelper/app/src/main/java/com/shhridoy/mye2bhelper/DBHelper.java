package com.shhridoy.mye2bhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dream Land on 1/28/2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "e2bhelper.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "etob_table";
    private static final String ID = "id";
    private static final String ENGLISH = "english";
    private static final String BANGLA = "bangla";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
            "( " +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ENGLISH + " TEXT NOT NULL, " +
            BANGLA + " TEXT NOT NULL);";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

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

    public void insertData(String english, String bangla) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ENGLISH, english);
        contentValues.put(BANGLA, bangla);
        this.getWritableDatabase().insertOrThrow(TABLE_NAME, "", contentValues);
        this.getWritableDatabase().close();
    }

    public boolean deleteData(int id) {
        int result = this.getWritableDatabase().delete(TABLE_NAME, ID+" = "+id, null);
        this.getWritableDatabase().close();
        return result > 0;
    }

    public boolean updateData(int id, String english, String bangla){
        ContentValues cv = new ContentValues();
        cv.put(ENGLISH, english);
        cv.put(BANGLA, bangla);
        int reslt = this.getWritableDatabase().update(TABLE_NAME, cv, ID+" =?", new String[]{String.valueOf(id)});
        this.getWritableDatabase().close();
        return reslt > 0;
    }

    public Cursor getData() {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM "+ TABLE_NAME+" ORDER BY "+ENGLISH+" ASC",
                null);
        return c;
    }
}
