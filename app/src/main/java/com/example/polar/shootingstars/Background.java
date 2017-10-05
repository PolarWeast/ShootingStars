package com.example.polar.shootingstars;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Background {

    private Bitmap image;
    private int x, y, dx;

    public Background(Bitmap res)
    {
        image = res;
        dx = GamePanel.MOVESPEED;
    }
    //scroll Background
    public void update()
    {
        y+=dx;
        if(y<0-GamePanel.HEIGHT){
            y=0;
        }
    }
    public void draw(Canvas canvas)
    {
        //draws new bg for scroll
        canvas.drawBitmap(image, x, y,null);
        if(y<0)
        {
            canvas.drawBitmap(image, x, y+GamePanel.HEIGHT, null);
        }
    }
}
