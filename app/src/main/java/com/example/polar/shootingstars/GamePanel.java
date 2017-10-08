package com.example.polar.shootingstars;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;


public class GamePanel extends SurfaceView implements SurfaceHolder.Callback
{
    public static final int WIDTH = 720;
    public static final int HEIGHT = 1280;
    public static final int MOVESPEED = -5;
    private long sparkleStartTime;
    private long asteroidStartTime;
    private MainThread thread;
    private Background bg;
    private Player player;
    private ArrayList<Sparklepuff> sparkle;
    private ArrayList<Asteroid>asteroids;
    private Random rand = new Random();


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
            counter++;
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
        asteroids = new ArrayList<Asteroid>();

        sparkleStartTime= System.nanoTime();
        asteroidStartTime= System.nanoTime();

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

            //add asteroids on timer
            long missileElapsed = (System.nanoTime()-asteroidStartTime)/1000000;
            if(missileElapsed >(2000 - player.getScore()/4)){

                System.out.println("making missile");
                //first missile always goes down the middle
                if(asteroids.size()==0)
                {
                    asteroids.add(new Asteroid(BitmapFactory.decodeResource(getResources(),R.drawable.
                            missile),HEIGHT + 10, WIDTH/2, 45, 15, player.getScore(), 13));
                }
                else
                {

                    asteroids.add(new Asteroid(BitmapFactory.decodeResource(getResources(),R.drawable.missile),
                            WIDTH+10, (int)(rand.nextDouble()*(HEIGHT)),45,15, player.getScore(),13));
                }

                //reset timer
                asteroidStartTime = System.nanoTime();
            }

            //loop through every missile and check collision and remove
            for(int i = 0; i<asteroids.size();i++)
            {
                //update missile
                asteroids.get(i).update();

                if(collision(asteroids.get(i),player))
                {
                    asteroids.remove(i);
                    player.setPlaying(false);
                    break;
                }
                //remove missile if it is way off the screen
                if(asteroids.get(i).getY()<-11)
                {
                    System.out.println("removing missile");
                    asteroids.remove(i);
                    break;
                }
            }

            //add sparkles on timer
            long elapsed = (System.nanoTime() - sparkleStartTime)/1000000;
            if(elapsed > 120){
                sparkle.add(new Sparklepuff(player.getX()+28, player.getY()));
                sparkleStartTime = System.nanoTime();
            }

            for(int i = 0; i<sparkle.size();i++)
            {
                sparkle.get(i).update();
                if(sparkle.get(i).getY()<10)
                {
                    sparkle.remove(i);
                }
            }
        }
    }
    public boolean collision(GameObject a, GameObject b)
    {
        if(Rect.intersects(a.getRectangle(),b.getRectangle()))
        {
            return true;
        }
        return false;
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
            //draw sparkle
            for(Sparklepuff sp: sparkle)
            {
                sp.draw(canvas);
            }

            //draw asteroids
            for(Asteroid m: asteroids)
            {
                m.draw(canvas);
            }

            canvas.restoreToCount(savedState);
        }
    }


}