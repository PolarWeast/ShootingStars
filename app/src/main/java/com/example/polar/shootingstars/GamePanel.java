package com.example.polar.shootingstars;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;


public class GamePanel extends SurfaceView implements SurfaceHolder.Callback
{
    public static final int WIDTH = 720;
    public static final int HEIGHT = 1280;
    public static final int MOVESPEED = -5;
    private long sparkleStartTime;
    private MainThread thread;
    private Background bg;
    private Player player;
    private ArrayList<Sparklepuff> sparkle;


    public GamePanel(Context context)
    {
        super(context);


        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);

        //make gamePanel focusable so it can handle events
        setFocusable(true);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        int counter = 0;
        while(retry && counter<1000)
        {
            try{thread.setRunning(false);
                thread.join();
                retry = false;

            }catch(InterruptedException e){e.printStackTrace();}
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        //assets for game
        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.sstarbg));
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.coolstar), 56, 56, 1);
        sparkle = new ArrayList<Sparklepuff>();

        sparkleStartTime= System.nanoTime();

        //safely start the game loop
        thread.setRunning(true);
        thread.start();

    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        //When player touches screen
        if(event.getAction()==MotionEvent.ACTION_DOWN){

            float x = (int)event.getX();

            if(!player.getPlaying())
            {
                player.setPlaying(true);
            }
            else
            {
                if (x < getWidth() / 2) {
                    player.setLeft(true);
                }
                if (x > getWidth() / 2) {
                    player.setRight(true);
                }

            }
            return true;
        }
        //No Touch Detected
        if(event.getAction()==MotionEvent.ACTION_UP)
        {
            player.setLeft(false);
            player.setRight(false);
            return true;
        }

        return super.onTouchEvent(event);
    }
    //calls for update constantly
    public void update()
    {
        if(player.getPlaying()) {

            bg.update();
            player.update();

            long elapsed = (System.nanoTime() - sparkleStartTime)/1000000;
            if(elapsed > 120){
                sparkle.add(new Sparklepuff(player.getX()+28, player.getY()));
                sparkleStartTime = System.nanoTime();
            }

            for(int i = 0; i<sparkle.size();i++)
            {
                sparkle.get(i).update();
                if(sparkle.get(i).getY()>310)
                {
                    sparkle.remove(i);
                }
            }
        }
    }
    @Override
    public void draw(Canvas canvas)
    {
        //bg scaleable
        final float scaleFactorX = getWidth()/(WIDTH*1.f);
        final float scaleFactorY = getHeight()/(HEIGHT*1.f);

        if(canvas!=null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            bg.draw(canvas);
            player.draw(canvas);
            for(Sparklepuff sp: sparkle)
            {
                sp.draw(canvas);
            }

            canvas.restoreToCount(savedState);
        }
    }


}