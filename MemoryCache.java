package com.stevefat.cachdemo;


import android.util.LruCache;

/**
 * Author : stevefat
 * Email :ngh8897@gmail.com
 * Created : 17-5-6 下午2:14
 */
public class MemoryCache implements Cache {

    //缓存集合
    LruCache<String, String> memoryCache;
    EvictedListener evictedListener;

    public MemoryCache() {
        init();

    }

    public MemoryCache(EvictedListener evictedListener) {
        init();
        this.evictedListener = evictedListener;
    }

    public boolean hasEvictedListener() {
        return evictedListener != null;
    }

    private void init() {
        //计算可用最大的内存
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        //取其中的1/3　作为缓存
        int maxCache = maxMemory / 3;
        //
        memoryCache = new LruCache<String, String>(maxCache) {
            @Override
            protected int sizeOf(String key, String value) {
                //对存入的数据进行数据测量
                return value.getBytes().length;
            }

            //TODO:移除数据　　　暂时不理解
            @Override
            protected void entryRemoved(boolean evicted, String key, String oldValue, String newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                if (evicted) {
                    if (evictedListener != null) {
                        evictedListener.handleEvictEntry(key, oldValue);
                    }
                }
            }
        };

    }


    @Override
    public String get(String key) {
        return memoryCache.get(key);
    }

    @Override
    public void put(String key, String str) {
        memoryCache.put(key, str);
    }

    @Override
    public boolean remove(String key) {
        return Boolean.parseBoolean(memoryCache.remove(key));
    }


    public void setEvictedListener(EvictedListener evictedListener) {
        this.evictedListener = evictedListener;
    }


    public interface EvictedListener {

        void handleEvictEntry(String evictKey, String evictValue);
    }

}
