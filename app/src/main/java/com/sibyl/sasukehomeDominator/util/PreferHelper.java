package com.sibyl.sasukehomeDominator.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.*;
import java.util.List;


/**
 * @author samy
 * @description：PreferHelper
 * @date 2014年9月17日 下午6:30:24
 * <p>
 * 注意：
 * 使用前请一定一定要把 init(Application app) 方法在你的Application里初始化一下。
 */
public class PreferHelper {
    public static String NAME = "";//BuildConfig.APPLICATION_ID;
    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;
    private volatile static PreferHelper mInstance;

    @SuppressLint("CommitPrefEdits")
    private PreferHelper(Application app) {
        sp = app.getSharedPreferences(NAME, 0);
        editor = sp.edit();
    }

    public static void init(Application app) {
        if (null == mInstance) {
            synchronized (PreferHelper.class) {
                if (null == mInstance) {
                    mInstance = new PreferHelper(app);
                }
            }
        }
    }

    public static PreferHelper getInstance() {
        synchronized (PreferHelper.class) {
            return mInstance;
        }
    }

    /**
     * 储存值
     */
    public void setString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public void setInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * 用于切换环境之类的这种需要同步执行的操作
     * @param key
     * @param value
     */
    public boolean setIntCommit(String key, int value) {
        editor.putInt(key, value);
        return editor.commit();
    }

    public boolean setBooleanCommit(String key, boolean value) {
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public boolean setStringCommit(String key, String value) {
        editor.putString(key, value);
        return editor.commit();
    }

    public void setLong(String key, long value) {
        editor.putLong(key, value);
        editor.apply();
    }

    public void setBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * 获取值
     */
    public String getString(String key) {
        return sp.getString(key, "");
    }

    public String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public int getInt(String key) {
        return sp.getInt(key, -1);
    }

    public int getInt(String key, int defaultInt) {
        return sp.getInt(key, defaultInt);
    }

    public long getLong(String key) {
        return sp.getLong(key, 1);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    /**
     * @description： 移除特定的
     * @date 2014年11月5日 下午4:30:08
     */
    public void remove(String name) {
        editor.remove(name);
        editor.apply();
    }

    /**
     * 移除所有数据
     */
    public void clearAll() {
        editor.clear();
        editor.apply();
    }

    /**
     * 保存集合
     *
     * @param key  ...
     * @param list ...
     */
    public void setList(String key, List<Double> list) {
        try {
            String liststr = ListToString(list);
            editor.putString(key, liststr);
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取集合
     *
     * @param key ...
     * @return ...
     */
    public List<Double> getList(String key) {
        String listStr = sp.getString(key, "");
        try {
            return StringToList(listStr);
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String ListToString(List<Double> list) throws IOException {
        //创建ByteArrayOutputStream对象，用来存放字节文件。
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //得到的字符放到到ObjectOutputStream
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        // writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
        objectOutputStream.writeObject(list);
        //Base64.encode将字节文件转换成Base64编码存在String中
        String string = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
        // 关闭Stream
        objectOutputStream.close();
        byteArrayOutputStream.close();
        return string;
    }

    @SuppressWarnings("unchecked")
    private static List<Double> StringToList(String string)
            throws StreamCorruptedException, IOException, ClassNotFoundException {
        byte[] b = Base64.decode(string.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(b);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        List<Double> list = (List<Double>) objectInputStream.readObject();
        // 关闭Stream
        objectInputStream.close();
        byteArrayInputStream.close();
        return list;
    }
}
