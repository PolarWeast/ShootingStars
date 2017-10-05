package com.example.polar.shootingstars;

import android.graphics.Bitmap;
import android.graphics.Canvas;


public class Player extends GameObject{
    private Bitmap spritesheet;
    private int score;
    private int up;
    private double dya;
    private boolean left;
    private boolean right;
    private boolean playing;
    private Animation animation = new Animation();
    private long startTime;

    public Player(Bitmap res, int w, int h, int numFrames) {

        x = GamePanel.WIDTH / 2;
        y = GamePanel.HEIGHT / 4;
        dy = 0;
        score = 0;
        height = h;
        width = w;

        Bitmap[] image = new Bitmap[numFrames];
        spritesheet = res;

        for (int i = 0; i < image.length; i++)
        {
            image[i] = Bitmap.createBitmap(spritesheet, i*width, 0, width, height);
        }

        animation.setFrames(image);
        animation.setDelay(10);
        startTime = System.nanoTime();

    }

    public void setLeft(boolean b){left = b;}
    public void setRight(boolean b){right = b;}
    public void setUp(int b){up = dx;}

    public void update()
    {
        long elapsed = (System.nanoTime()-startTime)/1000000;
        if(elapsed>100)
        {
            score++;
            startTime = System.nanoTime();
        }
        animation.update();
        //Player goes Left
        if(left){
            //dx = X acceleration
            dx = (int)(dya=-3.3);

        }

        //Player goes Right
        if(right){
            dx = (int)(dya=3.3);
        }



        if(dx>10)dx = 10;
        if(dx<-10)dx = -10;

        x += dx*2;
        dx = 0;
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(animation.getImage(),x,y,null);
    }
    public int getScore(){return score;}
    public boolean getPlaying(){return playing;}
    public void setPlaying(boolean b){playing = b;}
    public void resetDYA(){dya = 0;}
    public void resetScore(){score = 0;}
}


