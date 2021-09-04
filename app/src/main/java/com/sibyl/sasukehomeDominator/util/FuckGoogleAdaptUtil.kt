package com.sibyl.sasukehomeDominator.util

import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.util.*


/**
 * @author Sasuke on 2019-04-12 0012.
 * 去你妈的谷歌适配！！！
 *
 * 以后新版安卓需要适配的新特性的东西就都封装在这里吧。。。
 *
 * 坑好多
 */
class FuckGoogleAdaptUtil {
    companion object {
//        @JvmStatic
//        fun grantPermissions(activity: Activity) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                XXPermissions.with(activity)
//                        .constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
//                        //.permission(Permission.SYSTEM_ALERT_WINDOW, Permission.REQUEST_INSTALL_PACKAGES) //支持请求6.0悬浮窗权限8.0请求安装权限
//                        //                    .permission(Permission.Group.STORAGE, Permission.Group.CALENDAR) //不指定权限则自动获取清单中的危险权限
//                        .permission(//存储
//                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                Manifest.permission.READ_EXTERNAL_STORAGE,
//                                //电话
//                                Manifest.permission.CALL_PHONE,
//                                Manifest.permission.READ_PHONE_STATE,
//                                //短信
//                                Manifest.permission.SEND_SMS,
//                                //通讯录
//                                Manifest.permission.READ_PHONE_NUMBERS,
//                                Manifest.permission.GET_ACCOUNTS,
//                                //定位
//                                Manifest.permission.ACCESS_COARSE_LOCATION,
//                                Manifest.permission.ACCESS_FINE_LOCATION,
//                                //相机
//                                Manifest.permission.CAMERA
//
////                                Manifest.permission.CHANGE_NETWORK_STATE,
////                                Manifest.permission.WRITE_SETTINGS
//                                //安装APK
//                                /*Manifest.permission.REQUEST_INSTALL_PACKAGES*/)
//                        .request(object : OnPermission {
//                            override fun hasPermission(granted: List<String>, isAll: Boolean) {}
//
//                            override fun noPermission(denied: List<String>, quick: Boolean) {
//                                if (quick) {
//                                    AlertDialog.Builder(activity).setTitle("提示")
//                                            .setMessage("为保证生意管家的正常运行，请打开相应权限")
//                                            .setPositiveButton("去打开") { dialog, which -> XXPermissions.gotoPermissionSettings(activity) }
//                                            .show()
//                                }
//                            }
//                        })
//
//
//                //            if (!PermissionsUtil.hasPermission(context, permissions)) {
//                //                PermissionsUtil.requestPermission(context, new PermissionListener() {
//                //                        @Override
//                //                    public void permissionGranted(@NonNull String[] permissions) {
//                //                        //用户授予了权限
//                //                        }
//                //
//                //                        @Override
//                //                    public void permissionDenied(@NonNull String[] permissions) {
//                //                        //用户拒绝了权限
//                //                        requestPermissions();
//                //                        }
//                //                }, permissions);
//                //            }
//            }
//        }


        /**
         * 傻逼安卓7适配Uri
         */
        @JvmStatic
        fun android7AdaptUri(context: Context, auth: String, file: File): Uri {
            if (Build.VERSION.SDK_INT < 24/*Build.VERSION_CODES.N*/) {
                return Uri.fromFile(file)
            } else {
                return FileProvider.getUriForFile(context, auth, file)
            }
        }


        //。。。我就直接让7.0以下的都不用这个WRITE_SETTINGS权限得了。。。
        @JvmStatic
        fun android6WriteSettings(context: Context) {
            try {
                context.startActivity(Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                    setData(Uri.parse("package:" + context.getPackageName()))
                })
            } catch (e: ActivityNotFoundException) {

            }
        }

        /**
         * 安卓6.0上要单独申请Uri权限，要不然相机要自杀。
         */
//    fun grantFuckUriPermission(context: Context,intent: Intent,uri: Uri,writable: Boolean){
//        var flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
//        if (writable){
//            flag = Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//        }
//        intent.addFlags(flag)
//        context.getPackageManager().queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY).forEach {
//            context.grantUriPermission(it.activityInfo.packageName,uri,flag)
//        }
//    }


        /**
         * 把Uri路径转成真实路径（谷歌你他妈烦不烦！
         */
        @JvmStatic
        fun getRealFilePath(context: Context, uri: Uri): String {
            if (null == uri) return ""
            var scheme = uri.getScheme()
            var data = ""
            if (scheme == null) {
                data = uri.getPath().toString()
            } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
                data = uri.getPath().toString()
            } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
                var cursor = context.getContentResolver().query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null);
                if (null != cursor) {
                    if (cursor.moveToFirst()) {
                        var index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                        if (index > -1) {
                            data = cursor.getString(index)
                        }
                    }
                    cursor.close()
                }
            }
            return data
        }
        
        
        @JvmStatic
        /**Android11 Media API
         * isCrop : 裁剪状态和取照片状态的地址不同
         */
        fun createRImgUri(context: Context,file: File): Uri? {
            //设置保存参数到ContentValues中
            val contentValues = ContentValues()
            //设置文件名
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, file.getName())
            //android Q中不再使用DATA字段，而用RELATIVE_PATH代替
            //RELATIVE_PATH是相对路径不是绝对路径;照片存储的地方为：存储/Pictures
            val dirs = file.parent.split(File.separator)
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,dirs[dirs.size - 2] + File.separator + dirs[dirs.size - 1])
            //设置文件类型
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/"+ when(file.extension.toUpperCase(Locale.ROOT)){
                "PNG" -> "PNG"
                "WEBP" -> "WEBP"
                else -> "JPEG"
            })
            //执行insert操作，向系统文件夹中添加文件
            //EXTERNAL_CONTENT_URI代表外部存储器，该值不变
//            return context.getContentResolver().insert(MediaStore.Images.Media.getContentUri("external"), contentValues)
            return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        }
    }


}