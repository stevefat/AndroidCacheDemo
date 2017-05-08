package com.stevefat.cachdemo;

import android.support.annotation.IntDef;

/**
 * Author : stevefat
 * Email :ngh8897@gmail.com
 * Created : 17-5-6 下午4:26
 */
public class Strategy {

    public static final int MEMORY_ONLY = 0;
    public static final int MEMORY_FIRST = 1;
    public static final int DISK_ONLY = 3;

    @IntDef({MEMORY_ONLY, MEMORY_FIRST, DISK_ONLY})
    public @interface  type{}
}
