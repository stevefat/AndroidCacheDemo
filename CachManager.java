package com.stevefat.cachdemo;

import android.content.Context;
import android.text.TextUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Author : stevefat
 * Email :ngh8897@gmail.com
 * Created : 17-5-6 下午4:16
 */
public class CachManager {
    private static CachManager instance = null;

    private int mStrategy = Strategy.MEMORY_FIRST;
    //线程池
    private ExecutorService mExecutor = null;
    //内存缓存
    private MemoryCache mMemoryCache;
    //Disk缓存
    private DiskCache mDiskCache;


    public static CachManager getInstance(Context context) {
        return getInstance(context, Strategy.MEMORY_FIRST);
    }

    public static CachManager getInstance(Context context, int strategy) {
        if (instance == null) {
            synchronized (CachManager.class) {
                if (instance == null) {
                    instance = new CachManager(context.getApplicationContext(), strategy);
                }
            }
        } else {
            instance.setStrategy(strategy);
        }
        return instance;
    }

    public CachManager(Context context, int mStrategy) {
        this.mStrategy = mStrategy;
        init(context);
    }

    private void init(Context context) {
        mExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        mDiskCache = new DiskCache(context);
        mMemoryCache = new MemoryCache();
    }

    public void setStrategy(int mStrategy) {
        this.mStrategy = mStrategy;
        switch (mStrategy) {
            case Strategy.MEMORY_FIRST:
                if (!mMemoryCache.hasEvictedListener()) {
                    mMemoryCache.setEvictedListener(new MemoryCache.EvictedListener() {
                        @Override
                        public void handleEvictEntry(String evictKey, String evictValue) {
                            mDiskCache.put(evictKey, evictValue);
                        }
                    });
                }
                break;
            case Strategy.MEMORY_ONLY:
                if (!mMemoryCache.hasEvictedListener()) {
                    mMemoryCache.setEvictedListener(null);
                }
                break;
            case Strategy.DISK_ONLY:

                break;
        }
    }


    /**
     * 从缓存中读取value
     */
    public String readCache(final String key) {
        Future<String> ret = mExecutor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String result = null;
                switch (mStrategy) {
                    case Strategy.MEMORY_ONLY:
                        result = mMemoryCache.get(key);
                        break;
                    case Strategy.MEMORY_FIRST:
                        result = mMemoryCache.get(key);
                        if (result == null) {
                            result = mDiskCache.get(key);
                        }
                        break;
                    case Strategy.DISK_ONLY:
                        result = mDiskCache.get(key);
                        break;
                }
                return result;
            }
        });
        try {
            return ret.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将value 写入到缓存中
     */
    public void writeCache(final String key, final String value) {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                switch (mStrategy) {
                    case Strategy.MEMORY_FIRST:
                        mMemoryCache.put(key, value);
                        mDiskCache.put(key, value);
                        break;
                    case Strategy.MEMORY_ONLY:
                        mMemoryCache.put(key, value);
                        break;
                    case Strategy.DISK_ONLY:
                        mDiskCache.put(key, value);
                        break;
                }
            }
        });
    }

    public void remove(String key) {
        String result = mMemoryCache.get(key);
        if (TextUtils.isEmpty(result)) {
            mDiskCache.remove(key);
        } else {
            mMemoryCache.remove(key);
        }

    }
}
