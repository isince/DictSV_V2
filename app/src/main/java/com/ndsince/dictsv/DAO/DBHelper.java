package com.ndsince.dictsv.DAO;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * ใช้ในการกำหนดสร้างฐานข้อมูลเริ่มต้นให้กับตัวแอพพลิเคชั่น
 *  // Table Category
 *                Table Name TABLE_cate
 *  +-----------------+--------------+-----------+
 *  |  FIELD          |     TYPE     |    KEY    |
 *  +-----------------+--------------+-----------+
 *  |   C0_cate_id    |     INT      |    PK     |
 *  |   C1_cate_name  |     TEXT     |           |
 ***
 *
 *  // Table WordTB
 *                Table Name TABLE_WordTB
 *  +-----------------+--------------+-----------+
 *  |  FIELD          |     TYPE     |    KEY    |
 *  +-----------------+--------------+-----------+
 *  |     C0_id       |     INT      |    PK     |
 *  |     C1_Words    |     TEXT     |           |
 *  |     C2_trans    |     TEXT     |           |
 *  |     C3_ter      |     TEXT     |           |
 *  |     C4_cate     |     INT      |    FK     |
 *  ***
 *
 *  // Table favorite
 *               Table Name favorite
 *  +-----------------+--------------+-----------+
 *  |  FIELD          |     TYPE     |    KEY    |
 *  +-----------------+--------------+-----------+
 *  |     C0_id       |     INT      |    PK     |
 *  |     C1_name     |     TEXT     |           |
 *  ***
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String TAG = "DBHelper";

    // All Static variables
    private Context mContext;

    // Database Version
    private static final int DATABASE_VERSION = 1;

    //Database Name
    public static final String DATABASE_NAME = "wordsDB";

    //Table Words
    public static final String TABLE_WORDS = "Words";
    public static final String COLUMN_WORD_ID = "_id";
    public static final String COLUMN_WORD_WORD = "Word";
    public static final String COLUMN_WORD_TERMINOLOGY = "ter";
    public static final String COLUMN_WORD_TRANSLITERATED = "trans";
    public static final String COLUMN_WORD_CATEGORY_ID = "cate_id";

    //Table Catagory
    public static final String TABLE_CATEGORY = "Category";
    public static final String COLUMN_CATEGORY_ID = "_id";
    public static final String COLUMN_CATEGORY_NAME = "cat_name";

    //Table Favorite
    public static final String TABLE_FAVORITE = "Favorite";
    public static final String COLUMN_FAVORITE_ID = "_id";
    public static final String COLUMN_FAVORITE_WORD_ID = "word_id";

    //SQL Statement
    /*CREATE TABLE WORDS (_id INTEGER PRIMARY KEY AUTOINCREMENT,
    Word TEXT, Trans TEXT, Ter TEXT, cate_id INTEGER);*/
    private static final String TABLE_CREATE_WORDS = "CREATE TABLE  "
            + TABLE_WORDS + "("
            + COLUMN_WORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_WORD_WORD + " TEXT , "
            + COLUMN_WORD_TERMINOLOGY + " TEXT , "
            + COLUMN_WORD_TRANSLITERATED + " TEXT , "
            + COLUMN_WORD_CATEGORY_ID + " INTEGER"
            +");";

    //CREATE TABLE CATEGORY (_id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT);
    private static final String TABLE_CREATE_CATEGORY = "CREATE TABLE "
            + TABLE_CATEGORY + "("
            + COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CATEGORY_NAME + " TEXT"
            +");";

    //CREATE TABLE FAVORITE (_id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT);
    private static final String TABLE_CREATE_FAVORITE = "CREATE TABLE "
            + TABLE_FAVORITE + "("
            + COLUMN_FAVORITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_FAVORITE_WORD_ID + " LONG"
            +");";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }   // Constructor

    /**
     * onCreate() นิยมแทรกคำสั่งสำหรับการ Create Database และ Create Table
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            //Create table
            db.execSQL(TABLE_CREATE_WORDS);
            db.execSQL(TABLE_CREATE_CATEGORY);
            db.execSQL(TABLE_CREATE_FAVORITE);

            Log.i("LOG", TAG + " : " + "CREATE TABLE");
        } catch (SQLException e) {
//            Message.longToast(mContext, TAG, e.getMessage());
            Log.e("LOG", TAG + " : " + "Create Database Error " + e.getMessage().toString());
        }
    }   // onCreate

    /**
     * onUpgrade() นิยมใช้สำหรับการเปลี่ยนแปลง Version หรือโครงสร้างของ Database และ table
     * ทำงานเมื่อพบว่าฐานข้อมูลเดิมเป็น version เก่า จะอัพเกรดฐานข้อมูล โดยจะให้ลบของเก่าทิ้งแล้วสร้างขึ้นมาใหม่
     * แต่ สามารถใช้คำสั่ง ALTER แทรกคอลัมได้
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Clear all data
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE);
        Log.i("LOG", TAG + " : " + "Table Upgrade from" + oldVersion + "to" + newVersion);

        //Recreate table
        onCreate(db);

    }   // onUpgrade
}   // Main Class
