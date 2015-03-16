package com.ndsince.dictsv.DAO;

public class Category{

    //private variable
    private int mId;
    private String mName;

    public Category() {
        super();
    }

    public Category(int id, String name) {
        this.mId = id;
        this.mName = name;
    }   // Constructor

    public Category(String name) {
        this.mName = name;
    }   // Constructor

    public int getmId() {
        return mId;
    }

    public void setmId(int id) {
        this.mId = id;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String name) {
        this.mName = name;
    }
}
