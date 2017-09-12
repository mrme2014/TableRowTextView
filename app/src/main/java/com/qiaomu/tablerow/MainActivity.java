package com.qiaomu.tablerow;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.qiaomu.tablerow.chart.PieChart;
import com.qiaomu.tablerow.chart.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TableRowTextView tableRow = (TableRowTextView) findViewById(R.id.tableRow);
        tableRow.setTextArray(
                getCharSequenceBuilder("主主胜主主主胜主", 0, 5, Color.RED),
                getCharSequenceBuilder("客胜", 0, 1, Color.RED),
                getCharSequenceBuilder("客胜主", 1, 2, Color.YELLOW),
                "胜",
                getCharSequenceBuilder("客胜主胜1234", 2, 5, Color.GREEN));



        TableRowTextView tableRow1 = (TableRowTextView) findViewById(R.id.tableRow1);
        tableRow1.setTextArray(
                getCharSequenceBuilder("tableRow1", 0, 5, Color.RED),
                getCharSequenceBuilder("qiaomu", 0, 1, Color.RED),
                getCharSequenceBuilder("qiaomu乔木", 1, 2, Color.YELLOW),
                "吆吆吆",
                getCharSequenceBuilder("algin_cellMode_celldivider_rowdivider", 2, 5, Color.RED));

    }

    private SpannableStringBuilder getCharSequenceBuilder(String host, int start, int end, int color) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(host);
        ssb.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new AbsoluteSizeSpan(color, false), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

}
