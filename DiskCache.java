package com.stevefat.cachdemo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Author : stevefat
 * Email :ngh8897@gmail.com
 * Created : 17-5-6 下午2:32
 */
public class DiskCache implements Cache {

    DiskLruCache diskLruCache = null;

    public DiskCache(Context context) {
        init(context);
    }

    private void init(Context context) {
        try {
            File file = getDiskLruCacheDir(context, "cachdemo");
            if (!file.exists()) {
                file.mkdirs();
            }
            diskLruCache = DiskLruCache.open(file, getAppVersion(context), 1, 10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getDiskLruCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        cachePath += File.separator + uniqueName;

        return new File(cachePath);
    }

    @Override
    public String get(String key) {
        String result = null;
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(hashKeyForDisk(key));
            if (snapshot != null) {
                result = snapshot.getString(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    @Override
    public void put(String key, String str) {

        DiskLruCache.Editor editor = null;
        try {
            editor = diskLruCache.edit(hashKeyForDisk(key));
            if (editor != null) {
                editor.set(0, str);
                editor.commit();
            }
            diskLruCache.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean remove(String key) {
        try {
            return diskLruCache.remove(hashKeyForDisk(key));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String hashKeyForDisk(String key) {
        String cachKey;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(key.getBytes());
            cachKey = byteToHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            cachKey = String.valueOf(key.hashCode());
        }
        return cachKey;

    }

    public String byteToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();


    }


    public int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
