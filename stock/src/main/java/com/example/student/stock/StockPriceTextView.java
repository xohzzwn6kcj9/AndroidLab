package com.example.student.stock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by student on 2015-08-06.
 */
public class StockPriceTextView extends TextView {

    Context context;
    StockData data;

    public StockPriceTextView(Context context) {
        super(context);
        this.context = context;
    }

    public StockPriceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public StockPriceTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void setData(@NonNull StockData data){
        this.data = data;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int var = data.getChange();
        if(var>0){
            setTextColor(Color.RED);
        }
        else if(var < 0){
            setTextColor(Color.BLUE);
        }
        else{
            setTextColor(Color.WHITE);
        }
        super.onDraw(canvas);
    }
}
