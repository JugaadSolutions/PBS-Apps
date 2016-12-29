package com.mytrintrin.www.pbs;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by gwr on 12/9/16.
 */

public class extra {


/* to create table
    TableLayout tl = (TableLayout) findViewById(R.id.membertable);
    TableRow tr = new TableRow(this);
    tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    Button b = new Button(this);
    b.setText("Dynamic Button");
    b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    tr.addView(b);
    tr.setBackgroundResource(R.drawable.sf_gradient_03);
    tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));*/







/* to create table format programitically

    TableLayout tv=(TableLayout) findViewById(R.id.membertable);
    tv.removeAllViewsInLayout();
    TableRow tr=new TableRow(Members.this);

    tr.setLayoutParams(new ViewGroup.LayoutParams(
    ViewGroup.LayoutParams.MATCH_PARENT,
    ViewGroup.LayoutParams.WRAP_CONTENT));

    if(flag==1){

        TextView b3=new TextView(Members.this);
        b3.setText("column heading 1");
        b3.setTextColor(Color.BLUE);
        b3.setTextSize(15);
        tr.addView(b3);

        TextView b4=new TextView(Members.this);
        b4.setPadding(10, 0, 0, 0);
        b4.setTextSize(15);
        b4.setText("column heading 2");
        b4.setTextColor(Color.BLUE);
        tr.addView(b4);

        TextView b5=new TextView(Members.this);
        b5.setPadding(10, 0, 0, 0);
        b5.setText("column heading 3");
        b5.setTextColor(Color.BLUE);
        b5.setTextSize(15);
        tr.addView(b5);
        tv.addView(tr);

        final View vline = new View(Members.this);
        vline.setLayoutParams(new
                TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
        vline.setBackgroundColor(Color.BLUE);
        tv.addView(vline);
        flag=0;
    }

    else
    {
        TextView b=new TextView(Members.this);
        b.setText(name);
        b.setTextColor(Color.RED);
        b.setTextSize(15);
        tr.addView(b);

    }*/





}
