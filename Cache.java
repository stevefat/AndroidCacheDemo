package com.stevefat.cachdemo;

/**
 * Author : stevefat
 * Email :ngh8897@gmail.com
 * Created : 17-5-6 下午2:14
 */
public interface Cache {
    //获取数据
    String get(String key);
    //保存数据
    void put(String key,String str);
    //移除数据
    boolean remove(String key);
}
