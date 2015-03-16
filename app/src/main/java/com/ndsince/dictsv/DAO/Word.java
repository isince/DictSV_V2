package com.ndsince.dictsv.DAO;

/**
 * Word Object
 */
public class Word {

    //private variable
    private long mId;
    private String mWord;
    private String mTrans;
    private String mTermino;
    private Category mCategory;

    public Word() {
        super();
    }

    public Word(String word, String transliterated, String terminology, Category category) {
        this.mWord = word;
        this.mTrans = transliterated;
        this.mTermino = terminology;
        this.mCategory = category;
    }   // Constructor

    public Word(long id, String word, String transliterated, String terminology, Category category) {
        this.mId = id;
        this.mWord = word;
        this.mTrans = transliterated;
        this.mTermino = terminology;
        this.mCategory = category;
    }   // Constructor

    public long getmId() {
        return mId;
    }

    public void setmId(long id) {
        this.mId = id;
    }

    public String getmWord() {
        return mWord;
    }

    public void setmWord(String word) {
        this.mWord = word;
    }

    public String getmTrans() {
        return mTrans;
    }

    public void setmTrans(String transliterated) {
        this.mTrans = transliterated;
    }

    public String getmTermino() {
        return mTermino;
    }

    public void setmTermino(String terminology) {
        this.mTermino = terminology;
    }

    public Category getmCategory() {
        return mCategory;
    }

    public void setmCategory(Category category) {
        this.mCategory = category;
    }
}

