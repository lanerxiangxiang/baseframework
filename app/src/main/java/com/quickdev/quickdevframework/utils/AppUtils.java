package com.quickdev.quickdevframework.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;


import com.quickdev.baseframework.utils.AppContextUtil;

import java.io.File;
import java.lang.reflect.Field;


public class AppUtils {
    /**
     * get this App install package name(applicationId)
     *
     * @return com.cake.page
     */
    public static String getAppPackageName() {
        return AppContextUtil.getContext().getApplicationInfo().packageName;
    }

    /**
     * get string app version
     */
    public static String getVersionName() {
        String versionName = "1.0";
        try {
            PackageManager pm = AppContextUtil.getContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(AppContextUtil.getContext().getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "1.0";
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return versionName;
    }

    /**
     * get int app version
     */
    public static int getVersionCode() {
        int versioncode = 0;
        try {
            PackageManager pm = AppContextUtil.getContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(AppContextUtil.getContext().getPackageName(), 0);
            versioncode = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versioncode;
    }

    /**
     * install APK
     * 6.0++ need Dynamic permissions,you need add this part permissions into your AndroidManifest.xml
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
     * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
     * 7.0++ need FileProvider to make Uri ,don't care for it ,I have already do it!
     * more info about FileProvider [https://developer.android.google.cn/reference/android/support/v4/content/FileProvider.html]
     * TODO 1:create xml folder in [res] and create file_paths.xml in this xml
     * <?xml version="1.0" encoding="utf-8"?>
     * <resources>
     * <paths xmlns:android="http://schemas.android.com/apk/res/android">
     * <root-path path="" name="camera_photos" />
     * <root-path path="." name="download" />
     * </paths>
     * </resources>
     * TODO 2: take under code into <application></application>
     * <provider
     * android:name="android.support.v4.content.FileProvider"
     * android:authorities="${applicationId}.fileprovider"
     * android:exported="false"
     * android:grantUriPermissions="true">
     * <meta-data
     * android:name="android.support.FILE_PROVIDER_PATHS"
     * android:resource="@xml/file_paths"/>
     * </provider>
     */
    public static void installApk(Context context, String downloadApk) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(downloadApk);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(context, AppUtils.getAppPackageName() + ".fileprovider", file);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        }
        context.startActivity(intent);

    }

    /**
     * get main module BuildConfig values
     *
     * @param context
     * @param fieldName
     * @return
     */
    public static Object getBuildConfigValue(Context context, String fieldName) {
        try {
            Class<?> clazz = Class.forName(context.getPackageName() + ".BuildConfig");
            Field field = clazz.getField(fieldName);
            return field.get(null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getMetaForKey(String key) {
        ApplicationInfo appInfo = null;
        String value = null;
        try {
            appInfo = AppContextUtil.getContext().getPackageManager().getApplicationInfo(AppContextUtil.getContext().getPackageName(), PackageManager.GET_META_DATA);
            value = appInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }
}
