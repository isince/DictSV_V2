package com.ndsince.dictsv.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.ndsince.dictsv.LogCheck;

/**
 * Word Data acess object
 *
 * @see Word
 */
public class WordDAO {

    public static final String TAG = "WordDAO";

    //Datebase field
    private SQLiteDatabase mDb;
    private DBHelper mDBHelper;
    private String[] mAllColumns = {DBHelper.COLUMN_WORD_ID,
            DBHelper.COLUMN_WORD_WORD,
            DBHelper.COLUMN_WORD_TRANSLITERATED,
            DBHelper.COLUMN_WORD_TERMINOLOGY,
            DBHelper.COLUMN_WORD_CATEGORY_ID};

    private Context mContext;

    private Word word;
    private Category category;

    /**
     * Constructor
     *
     * @param context
     */
    public WordDAO(Context context) {
        this.mContext = context;
        mDBHelper = new DBHelper(context);

        //open database
        try {
            open();
        } catch (Exception e) {
            //Log.e(TAG, "SQLException on opening database" + e.getMessage());
        }
    }   // Constructor

    /**
     * Open database connection
     */
    public void open() {
        mDb = mDBHelper.getWritableDatabase();
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Add word by categoryID(int)
     *
     * @param word       Word
     * @param trans      Transliterated (คำทับศัพท์)
     * @param termino    Terminology (ศัพท์บัญญัติ)
     * @param categoryID Category ID
     */
    public void addWord(String word, String termino, String trans, int categoryID) {
        //Add Content value and insert row
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_WORD_WORD, word);
        values.put(DBHelper.COLUMN_WORD_TERMINOLOGY, termino);
        values.put(DBHelper.COLUMN_WORD_TRANSLITERATED, trans);
        values.put(DBHelper.COLUMN_WORD_CATEGORY_ID, categoryID);

        long insertID = mDb.insert(DBHelper.TABLE_WORDS, null, values);

        LogCheck.d(TAG, "addWord", String.valueOf(insertID) + " - " + word);
    }

    /**
     * Add word by object
     *
     * @param word Word object
     * @see Word
     */
    public void addWord(Word word) {
        //Add Content value and insert row
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_WORD_WORD, word.getmWord());
        values.put(DBHelper.COLUMN_WORD_TERMINOLOGY, word.getmTermino());
        values.put(DBHelper.COLUMN_WORD_TRANSLITERATED, word.getmTrans());
        values.put(DBHelper.COLUMN_WORD_CATEGORY_ID, word.getmCategory().getmId());

        long insertID = mDb.insert(DBHelper.TABLE_WORDS, null, values);

        if(insertID>0) Toast.makeText(mContext, "Add Word Successful", Toast.LENGTH_SHORT).show();
        LogCheck.d(TAG, "addWord", String.valueOf(insertID) + " - " + word.getmWord());
    }

    /**
     * deleteWordByCategoryID
     * @param word
     */
    public void deleteWordByCategoryID(Word word) {

        String[] whereArgs = new String[]{String.valueOf(word.getmCategory().getmId())};
        int deleteID = mDb.delete(DBHelper.TABLE_WORDS,
                DBHelper.COLUMN_WORD_CATEGORY_ID + "=?", whereArgs);

        if (deleteID > 0) LogCheck.d(TAG, "deleteWordByCategoryID" ,
                "ลบคำศัพท์ในหมวด " + word.getmCategory().getmId() + " แล้ว");
    }

    //----------------------------------------------------------------------------------------------

    public boolean chkRepetitionWord(String wordName, int categoryID) {
        boolean chkRepetitionWord = true;
        //SELECT * FROM Words WHERE category_id = 0 AND word = wordName
        String[] whereArgs = new String[]{String.valueOf(wordName), String.valueOf(categoryID)};
        Cursor cursor = mDb.query(DBHelper.TABLE_WORDS, mAllColumns,
                DBHelper.COLUMN_WORD_WORD + "=? AND " +
                DBHelper.COLUMN_WORD_CATEGORY_ID + "=?",
                whereArgs, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.isAfterLast()) chkRepetitionWord = false;
            else chkRepetitionWord = true;
        }

        if (cursor != null) cursor.close();
        return chkRepetitionWord;
    }

    public boolean chkWordByCategoryID(int categoryID) {
        boolean chkWord = true;
        //SELECT * FROM Words WHERE category_id = 0 AND word = wordName
        String[] whereArgs = new String[]{String.valueOf(categoryID)};
        Cursor cursor = mDb.query(DBHelper.TABLE_WORDS, mAllColumns,
                DBHelper.COLUMN_WORD_CATEGORY_ID + "=?",
                whereArgs, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.isAfterLast()) chkWord = false;
            else chkWord = true;
        }

        if (cursor != null) cursor.close();
        return chkWord;
    }
}
