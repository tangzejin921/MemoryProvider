package com.tzj.providers;


import android.database.AbstractCursor;

public class MemoryCursor extends AbstractCursor {
    private String[] mColumns;
    private Object[] mDatas;

    public MemoryCursor(String[] columns, Object[] datas) {
        mColumns = columns;
        mDatas = datas;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public String[] getColumnNames() {
        return mColumns;
    }

    @Override
    public String getString(int i) {
        return (String) mDatas[i];
    }

    @Override
    public short getShort(int i) {
        return (short) mDatas[i];
    }

    @Override
    public int getInt(int i) {
        return (int) mDatas[i];
    }

    @Override
    public long getLong(int i) {
        return (long) mDatas[i];
    }

    @Override
    public float getFloat(int i) {
        return (float) mDatas[i];
    }

    @Override
    public double getDouble(int i) {
        return (double) mDatas[i];
    }

    @Override
    public boolean isNull(int i) {
        return mDatas[i] == null;
    }
}
