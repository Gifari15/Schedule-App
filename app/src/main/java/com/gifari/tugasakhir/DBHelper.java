package com.gifari.tugasakhir;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper{
    static final String DATABASE = "taskss.db";
    static final int VERSION = 1;
    static final String TABLE = "task";

    static final String C_ID = "_id";
    static final String C_ENAME = "ename";
    static final String C_DATE = "date";
    static final String C_TIME = "time";
    public DBHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TABLE + " ( "+ C_ID
                +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ C_ENAME + " text, "
                +C_DATE + " text, "+ C_TIME + " text )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table " + TABLE);

        // CREATE NEW VERSION TABLE
        onCreate(db);
    }
}