package com.tzj.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.Log;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

/**
 * 以 k-v 方式存放与内存中,重启后将恢复默认值
 */
final public class MemoryProvider extends ContentProvider {
    private static final String TAG = App.TAG;
    private Boolean DEBUG = App.DEBUG;
    //可以存储的最大数据个数
    private static final int MAX_COUNT = 100;
    //存储的数据
    private static final ArrayMap<String, Object> mMap = new ArrayMap<String, Object>();

    private static final UriMatcher mMatcher;

    public static final Uri URI_KV = Uri.parse("content://memory_provider/kv");

    static {
        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mMatcher.addURI("memory_provider", "kv", 1);
    }

    @Override
    public boolean onCreate() {
        String string = getContext().getString(R.string.memory_provider_default);
        init(string);
        Object debug = get("debug");
        if (debug instanceof Boolean) {
            DEBUG = (Boolean) debug;
        }
        if (DEBUG) {
            Log.d(TAG, "onCreate: ");
        }
        ServiceManager.addService("memory_provider", new MemoryService(this));

        try {
            String defaultStr = Settings.System.getString(getContext().getContentResolver(), "memory_provider_default");
            Log.d(TAG, "default from setting: " + defaultStr);
            init(defaultStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return "vnd.android.cursor.item/text/plain";
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (projection == null) {
            projection = new String[0];
        }
        Object[] objs = new Object[projection.length];
        if (mMatcher.match(uri) == 1) {
            for (int i = 0; i < projection.length; i++) {
                objs[i] = get(projection[i]);
            }
        }
        return new MemoryCursor(projection, objs);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (mMatcher.match(uri) == 1) {
            if (mMap.size() > MAX_COUNT){
                Log.e(TAG,"insert 存储超过数量限制了");
                return uri;
            }
            Iterator<String> iterator = values.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                put(getContext(), key, values.get(key));
            }
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (mMatcher.match(uri) == 1) {
            return delete(getContext(), selection) == null ? 0 : 1;
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Iterator<String> iterator = values.keySet().iterator();
        int count = 0;
        if (mMatcher.match(uri) == 1) {
            if (mMap.size() > MAX_COUNT){
                Log.e(TAG,"update 存储超过数量限制了");
                return count;
            }
            while (iterator.hasNext()) {
                String key = iterator.next();
                put(getContext(), key, values.get(key));
                count++;
            }
        }
        return count;
    }

    /**
     * 添加默认值
     */
    private static void init(String jsonStr) {
        if (jsonStr == null) {
            return;
        }
        try {
            JSONObject js = new JSONObject(jsonStr);
            Iterator<String> keys = js.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                mMap.put(key, js.get(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 权限问题
     */
    private void enforceWritePermission(String permission) {
        if (getContext().checkCallingOrSelfPermission(permission)
                != PackageManager.PERMISSION_GRANTED) {
            throw new SecurityException("Permission denial: writing to settings requires:"
                    + permission);
        }
    }

    public static Object get(String key) {
        return mMap.get(key);
    }

    public static Object put(Context ctx, String key, Object value) {
        mMap.put(key, value);
        ctx.getContentResolver().notifyChange(Uri.withAppendedPath(URI_KV, key), null);
        return key;
    }

    public static Object delete(Context ctx, String key) {
        mMap.remove(key);
        ctx.getContentResolver().notifyChange(Uri.withAppendedPath(URI_KV, key), null);
        return key;
    }

    public static Collection<Object> list() {
        return mMap.values();
    }

    public static void dumpAll(PrintWriter pw) {
        mMap.forEach((k, v) -> {
            pw.println(k + " = " + v);
        });
    }
}
