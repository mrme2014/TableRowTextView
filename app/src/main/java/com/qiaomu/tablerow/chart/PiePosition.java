package com.qiaomu.tablerow.chart;

import android.support.annotation.IntDef;

/**
 * Created by qiaomu on 2017/7/21.
 */
@IntDef({PiePosition.INSIDE, PiePosition.OUT_SIDE, PiePosition.OUT_TOP})
public @interface PiePosition {
    int INSIDE = 0;  //图形里面
    int OUT_SIDE = 1;//外面——线条外面
    int OUT_TOP = 2; //外面——线条上面
}
