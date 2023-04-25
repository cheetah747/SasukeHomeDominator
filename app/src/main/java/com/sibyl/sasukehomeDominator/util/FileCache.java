package com.sibyl.sasukehomeDominator.util;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件缓存类
 *
 * @author Treasure
 */
public class FileCache {
    public static void deleteMIUIglobalTrash(){
        File globalTrashDir = FileData.INSTANCE.globalTrashFile();
        File [] fileList = globalTrashDir.listFiles();

        if (fileList != null && fileList.length != 0){
            for (File file: fileList){
                file.delete();
            }
        }
    }

    public static void saveBitmap(Bitmap bm, File desFile) {
        try {
            if (desFile.exists()) {
                desFile.delete();
            }
            FileOutputStream out = new FileOutputStream(desFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean copyFile(String oldPath, String newPath) {
        boolean isok = true;
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    // System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                fs.flush();
                fs.close();
                inStream.close();
            } else {
                isok = false;
            }
        } catch (Exception e) {
            isok = false;
        }
        return isok;
    }
}
