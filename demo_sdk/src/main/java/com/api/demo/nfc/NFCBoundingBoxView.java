package com.api.demo.nfc;// NFCBoundingBoxView.java

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

public class NFCBoundingBoxView extends View {

    private Context context;

    public NFCBoundingBoxView(Context context) {
        super(context);

        this.context = context;
    }

    public NFCBoundingBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 创建一个 Paint 对象用于设置画笔属性
        Paint paint = new Paint();
        paint.setColor(Color.RED); // 设置颜色为红色
        paint.setStyle(Paint.Style.STROKE); // 设置画笔样式为描边
        paint.setStrokeWidth(5); // 设置描边宽度

/*        // 获取方框的坐标和尺寸
        int left = getWidth() - 184;
        int top = 190;
        int right = getWidth() - 2;
        int bottom = top + 294;

        // 在 canvas 上绘制方框
        canvas.drawRect(left, top, right, bottom, paint);*/


        /*// 屏幕分辨率
        int screenWidthPx = 1280;
        int screenHeightPx = 800;

        // 屏幕物理尺寸
        float screenWidthMm = 217.58f;
        float screenHeightMm = 136.31f;

        // 需要画的矩形尺寸
        float rectWidthMm = 31.37f;
        float rectHeightMm = 50f;

        // 位置信息
        float rightMarginMm = 0f;
        float topMarginMm = 32.44f;

        // 计算矩形在屏幕上的位置和尺寸（单位：像素）
        float rectRightPx = screenWidthPx - (rightMarginMm / screenWidthMm * screenWidthPx);
        float rectTopPx = topMarginMm / screenHeightMm * screenHeightPx;
        float rectWidthPx = rectWidthMm / screenWidthMm * screenWidthPx;
        float rectHeightPx = rectHeightMm / screenHeightMm * screenHeightPx;

        // 在Canvas上绘制矩形
        canvas.drawRect(rectRightPx - rectWidthPx, rectTopPx, rectRightPx, rectTopPx + rectHeightPx, paint);*/

        // 屏幕分辨率
        int screenWidthPx = 1280;
        int screenHeightPx = 800;

        // 屏幕物理尺寸
        float screenWidthMm = 217.58f;
        float screenHeightMm = 136.31f;

        // 需要画的矩形尺寸
        float rectWidthMm = 31.37f;
        float rectHeightMm = 50f;

        // 位置信息
        float rightMarginMm = 0f;
        float topMarginMm = 32.44f;

        // 计算框在屏幕上的位置（单位：像素）
        float top = topMarginMm; // mm
        float bottom = top + rectHeightMm; // mm

        // 将物理尺寸转换为像素
        /*DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayManager displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            Display[] displays = displayManager.getDisplays();
            for (Display display : displays) {
                display.getMetrics(displayMetrics);
                density = displayMetrics.density;
            }
        }*/
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        float pixelsPerMm = density * (screenWidthPx / screenWidthMm); // 计算每毫米的像素数
        int screenWidth = displayMetrics.widthPixels; // 获取屏幕宽度

        float boxWidth = (int) (rectWidthMm * pixelsPerMm); // 框的宽度
        float leftPx = screenWidth - boxWidth; // 框左边位置
        float rightPx = screenWidth - rightMarginMm; // 框右边位置
        float topPx = (int) (top * pixelsPerMm); // 框顶部位置
        float bottomPx = (int) (bottom * pixelsPerMm); // 框底部位置

        // 绘制框
        canvas.drawRect(leftPx - 2, topPx, rightPx - 2, bottomPx, paint);

        Log.d("tagg", "drawRect " + (leftPx - 2) + " " + topPx + " " + (rightPx - 2) + " " + bottomPx);
        // 设置文本属性
        paint.setColor(Color.RED);
        paint.setTextSize(20);
        paint.setStrokeWidth(2); // 设置描边宽度

        // 获取文本的宽度和高度
        /*String text = context.getString(R.string.nfc_area_text);
        float textWidth = paint.measureText(text);
        Paint.FontMetrics metrics = paint.getFontMetrics();
        float textHeight = metrics.bottom - metrics.top;

        // 计算文本的坐标
        float x = ((right-left) - textWidth) / 2;
        float y = ((bottom - top) - textHeight) / 2;

        // 在 canvas 上绘制文本
        canvas.drawText(text, left + x, top + y, paint);*/
    }
}
