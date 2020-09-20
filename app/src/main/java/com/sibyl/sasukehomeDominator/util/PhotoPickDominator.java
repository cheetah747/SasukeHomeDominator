package com.sibyl.sasukehomeDominator.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sasuke on 2017/7/24.
 * 选取图片工具类（拍照+本地相册）
 */

public class PhotoPickDominator {
    final private static String DEFAULT_DIR = FileData.INSTANCE.getSdStorageDir();
    /**
     * 拍照
     */
    final private static int PHOTO_REQUEST_TAKEPHOTO = 7479;
    /**
     * 从相册选取
     */
    final public static int PHOTO_REQUEST_GALLERY = 7450;//一般选择
//    final public static int PHOTO_REQUEST_YOU_ICON = 7451;//选你的头像
//    final public static int PHOTO_REQUEST_ME_ICON = 7452;//选我的头像


    /**
     * 裁剪
     */
    final public static int CROP_REQUEST = 7460;

    //    private BottomDialog mDialog;
    private AppCompatActivity mActivity;
    /**
     * 具体地址
     */
    private String mPhotoPath = "";
    /**
     * 存图的目录（自定义的时候一定要像DEFAULT_DIR一样结尾带斜杠啊！）
     */
    private String mPhotoDir = "";

    //图片预览popupWindow
//    CustomPopupWindow photoPopup;

    /**
     * 照片池，映射到外部的集合对象，然后外部到时候可以直接取这个值来用了。
     */
    private List<String> pathPool = new ArrayList();

    public PhotoPickDominator(AppCompatActivity activity) {
        this(activity, DEFAULT_DIR);
    }

    public PhotoPickDominator(AppCompatActivity activity, List<String> pathPool) {
        this(activity, DEFAULT_DIR);
        if (pathPool != null && pathPool.size() == 0) {
            this.pathPool = pathPool;
        }
    }

    public PhotoPickDominator(AppCompatActivity activity, String photoDir) {
        this.mActivity = activity;
        this.mPhotoDir = photoDir;
    }


    /**
     * 单独从相册获取
     */
    public void selectByLocal(int selectType) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        mActivity.startActivityForResult(intent, selectType);
    }


    /**
     * 获取选取的图片本机地址（用在引用的Activity的onActivityResult()里面）
     *
     * @return
     */
    public String onPhotoPathResult(Context context, int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //拍照
            case PHOTO_REQUEST_TAKEPHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    return mPhotoPath;
                }
                break;
            //相册
//            case PHOTO_REQUEST_YOU_ICON:
//            case PHOTO_REQUEST_ME_ICON:
            case PHOTO_REQUEST_GALLERY:
                if (resultCode == Activity.RESULT_OK) {
                    String imgPath;
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        Cursor cursor = context.getContentResolver().query(data.getData(), null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            //安卓7.0以上，4才是路径，但低版本安卓，1是路径。
                            imgPath = cursor.getString(4);
                            //如果不是路径，就再找找。
                            if (!imgPath.contains("/storage/")) {
                                //找个6次应该就差不多了，7、8.。。。后面一般都是null
                                for (int i = 0; i < 7; i++) {
                                    if (cursor.getString(i).contains("/storage/")) {
                                        imgPath = cursor.getString(i);
                                        break;
                                    }
                                }
                            }
                            cursor.close();
                        } else {
                            imgPath = data.getDataString().replace("file://", "");
                        }
                    } else {//如果是安卓7.0或以上
                        imgPath = FuckGoogleAdaptUtil.getRealFilePath(context, data.getData());
                    }

                    String picName = "CACHE_FOR_CROP.JPG";
                    FileCache.copyFile(imgPath, mPhotoDir + File.separator + picName);
                    return mPhotoDir + File.separator + picName;
                }
                break;
            default:
        }
        return "";
    }


    /**
     * 把临时在SD卡上创建的图片临时文件删除
     */
//    public void deletePicCache() {
//        FileUtil.deleteAll(new File(mPhotoDir));
//    }
    private int[] getGoalSizes(Context context, String photoPath) {
        //先计算好应该显示的大图尺寸
        int scrWidth = context.getResources().getDisplayMetrics().widthPixels;
        int scrHeight = context.getResources().getDisplayMetrics().heightPixels;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);

        int goalWidth = 0;
        int goalHeight = 0;
        if (options.outWidth / (float) options.outHeight > scrWidth / (float) scrHeight) {
            goalWidth = scrWidth;//图的宽太大了，调成屏幕同宽
            goalHeight = (int) (scrWidth / (float) options.outWidth * options.outHeight);
        } else {
            goalHeight = scrHeight;//图的高太大了，调成屏幕同宽
            goalWidth = (int) (scrHeight / (float) options.outHeight * options.outWidth);
        }
        return new int[]{goalWidth, goalHeight};
    }

    /**
     * 调用系统的裁剪
     *
     * @param uri ...
     */
    public void cropPhoto(AppCompatActivity activity, Uri uri, int width, int height) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", width);
        intent.putExtra("aspectY", height);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("return-data", false);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        activity.startActivityForResult(intent, CROP_REQUEST);
    }
}
