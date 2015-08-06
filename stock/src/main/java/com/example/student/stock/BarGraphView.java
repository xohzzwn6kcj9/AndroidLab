package com.example.student.stock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


//Main 화면의 막대 그래프
//그래프가 동적으로 변경되지는 않는다.
public class BarGraphView extends View {
    private StockData data;

    public BarGraphView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public BarGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public BarGraphView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public void setData(StockData data) {
        this.data = data;
        invalidate();
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(50, 15);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if (data != null) {
//			int width = getWidth();
//			int height = getHeight();
            int width=50;
            int height=15;


            //기준라인 흰색으로 그림
            canvas.translate(width / 2, 0);
            Paint p1=new Paint();
            p1.setColor(Color.WHITE);
            canvas.drawLine(0, 0, 0, height, p1);

            // 상승라인 그림
            if ((data.getMax() - data.getStart()) > 0) {

                Paint p=new Paint();
                p.setColor(Color.RED);
                canvas.drawLine(0, height/2, width/2, height/2, p);
            }
            // 하락라인 그림
            if ((data.getStart() - data.getMin()) > 0) {
                Paint p=new Paint();
                p.setColor(Color.BLUE);
                canvas.drawLine(0, height/2, -width/2, height/2, p);
            }


            if((data.getPrice()-data.getStart())>0){
                //현재가가 시작가에 비해 상승중이라면 막대 그리기
                Paint p=new Paint();
                p.setColor(Color.RED);
                int right=((width/2)*(data.getPrice()-data.getStart()))/(data.getMax()-data.getStart());

                canvas.drawRect(0, 0, right, height, p);
            }else {
                //현재가가 시작가에 비해 하락중이라면 막대 그리기
                Paint p=new Paint();
                p.setColor(Color.BLUE);
                int left=-((width/2)*(data.getStart()-data.getPrice()))/(data.getStart()-data.getMin());

                canvas.drawRect(left, 0, 0, height, p);
            }
        }
    }

}
