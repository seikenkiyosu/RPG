package com.kiyosu.rpg;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.view.SurfaceHolder;

/**
 * Created by kiyosu on 2015/10/01.
 */
public class Graphics {
    private SurfaceHolder holder;
    private Paint         paint;
    private Canvas        canvas;
    private int           originx;
    private int           originy;

    //constructor
    public Graphics(SurfaceHolder holder) {
        this.holder = holder;
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public void lock() {
        canvas = holder.lockCanvas();
        if(canvas == null) return;
        canvas.translate(originx, originy);
    }

    public void unlock() {
        if (canvas == null) return;
        holder.unlockCanvasAndPost(canvas);
    }

    public void setOrigin(int x, int y) {
        originx = x;
        originy = y;
    }

    public int getOriginx() {
        return originx;
    }

    public int getOriginy() {
        return originy;
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    public void setTextSize(int fontsSize) {
        paint.setTextSize(fontsSize);
    }

    public FontMetrics getFontMetrics() {
        return paint.getFontMetrics();
    }

    public int measureText(String string) {
        return (int)paint.measureText(string);
    }

    public void fillRect(int x, int y, int w, int h) {
        if (canvas == null) return;
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(new Rect(x, y, x + w, y + h), paint);
    }

    public void drawBitmap(Bitmap bitmap, int x, int y) {
        if (canvas == null) return;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Rect src = new Rect(0, 0, w, h);
        Rect dst = new Rect(x, y, x+w, y+h);
        canvas.drawBitmap(bitmap, src, dst, null);
    }

    public void drawMonsterInMap(Bitmap bitmap, int x, int y) {
        if (canvas == null) return;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Rect src = new Rect(0, 0, w, h);
        Rect dst = new Rect(x, y, x+w/3, y+h/3);
        canvas.drawBitmap(bitmap, src, dst, null);
    }

    public void drawText(String string, int x, int y) {
        if(canvas == null) return;
        canvas.drawText(string, x, y, paint);
    }
}
