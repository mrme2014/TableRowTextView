package com.qiaomu.tablerow.chart;

import android.support.annotation.IntDef;

/**
 * Created by qiaomu on 2017/7/21.
 */
@IntDef({PieAnimation.ONE_BY_ONE,
        PieAnimation.ONE_AND_ONE,
        PieAnimation.NONE})
public @interface PieAnimation {
    int ONE_BY_ONE = 0;   //一个接一个
    int ONE_AND_ONE = 1;  //所有的一起开始
    int NONE = 2;         //没有
}
