package com.kiyosu.rpg;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

/**
 * Created by kiyosu on 2015/10/01.
 */
public class RPGView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    //シーン定数
    private final static int
        S_START   = 0,
        S_MAP     = 1,
        S_APPEAR  = 2,
        S_COMMAND = 3,
        S_ATTACK  = 4,
        S_DEFENCE = 5,
        S_ESCAPE  = 6;

    //the size of display
    private final static int
        W = 900,
        H = 400;

    //key constant
    private final static int
        KEY_NONE   = -1,
        KEY_LEFT   = 0,
        KEY_RIGHT  = 1,
        KEY_UP     = 2,
        KEY_DOWN   = 3,
        KEY_1      = 4,
        KEY_2      = 5,
        KEY_SELECT = 6;

    //Map constant
    private final static int[][] MAP = {
            {3, 3, 3, 3, 3, 3, 3, 3, 3, 3},
            {3, 1, 0, 0, 0, 0, 3, 0, 0, 3},
            {3, 0, 0, 0, 0, 0, 3, 3, 0, 3},
            {3, 0, 3, 3, 3, 3, 3, 3, 0, 3},
            {3, 0, 0, 3, 0, 0, 0, 3, 0, 3},
            {3, 3, 0, 3, 0, 3, 3, 3, 0, 3},
            {3, 0, 0, 3, 0, 0, 0, 0, 0, 3},
            {3, 0, 3, 3, 0, 3, 0, 3, 3, 3},
            {3, 0, 0, 0, 0, 3, 0, 0, 2, 3},
            {3, 3, 3, 3, 3, 3, 3, 3, 3, 3}
    };

    //Brave constant
    private final static int[]
        YU_MAXHP   = {0, 30, 50, 70},
        YU_ATTACK  = {0, 5, 10, 30},
        YU_DEFENCE = {0, 0, 5, 10},
        YU_EXP     = {0, 0, 3, 6};

    //enemy constant
    private final static String[]
        EN_NAME = {"いくと", "ティラノサウルス"};
    private final static int[]
        EN_MAXHP   = {10, 50},
        EN_ATTACK  = {10, 26},
        EN_DEFENCE = {0, 16},
        EN_EXP     = {1, 99};

    //system
    private SurfaceHolder holder;
    private Graphics      g;
    private Thread        thread;
    private int           init = S_START;
    private int           scene;
    private int           key;
    private Bitmap[]      bmp = new Bitmap[7];

    //brain parameter
    private int yuX   = 1;
    private int yuY   = 2;
    private int yuLV  = 1;
    private int yuHP  = 30;
    private int yuEXP = 0;

    //enemy parameter
    private int enType;
    private int enHP;

    //constructor
    public RPGView(Activity activity) {
        super(activity);

        //read bitmap
        for (int i = 0; i < 7; i++) {
            bmp[i] = readBitmap(activity, "rpg"+1);
        }

        //generate surface folder
        holder = getHolder();
        holder.setFormat(PixelFormat.RGBA_8888);
        holder.addCallback(this);

        //display size setting
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        int dw = H * p.x/p.y;
        holder.setFixedSize(dw, H);

        //generate Graphics
        g = new Graphics(holder);
        g.setOrigin((dw-W)/2, 0);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new Thread(this);
        thread.start();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread = null;
    }


    public void run() {
        while(thread != null) {
            //init scene
            if (init >= 0) {
                scene = init;
                //start
                if(scene == S_START) {
                    yuX = 1;
                    yuY = 2;
                    yuLV = 1;
                    yuHP = 30;
                    yuEXP = 0;
                }
                init = -1;
                key = KEY_NONE;
            }

            //map
            if (scene == S_MAP) {
                //move
                boolean flag = false;
                if (key == KEY_UP) {
                    if(MAP[yuY-1][yuX] <= 2) {
                        yuY--;
                        flag = true;
                    }
                    else if (key == KEY_DOWN) {
                        if(MAP[yuY+1][yuX] <= 2) {
                            yuY++;
                            flag = true;
                        }
                    }
                    else if (key == KEY_LEFT) {
                        if(MAP[yuY][yuX-1] <= 2) {
                            yuX--;
                            flag = true;
                        }
                    }
                    else if (key == KEY_RIGHT) {
                        if(MAP[yuY][yuX+1] <= 2) {
                            yuY++;
                            flag = true;
                        }
                    }
                }
                //calculate appear enemy
                if (flag) {
                    if (MAP[yuY][yuX]==0 && rand(10)==0) {
                        enType = 0;
                        init = S_APPEAR;
                    }
                    if (MAP[yuY][yuX]==1) yuHP = YU_MAXHP[yuLV];
                    if (MAP[yuY][yuX]==2) {
                        enType = 1;
                        init = S_APPEAR;
                    }
                }

                //for draw
                g.lock();
                for (int j = -3; j <= 3; j++) {
                    for (int i = -5; i <= 5; i++) {
                        int idx = 3;
                        if (0 <= yuX+i && yuX+i < MAP[0].length && 0 <= yuY+j && yuY+j < MAP[0].length) {
                            idx = MAP[yuY+j][yuX+i];
                        }
                        g.drawBitmap(bmp[idx], W/2-40+80*i, H/2-40+80*j);
                    }
                }
                g.drawBitmap(bmp[4], W / 2 - 40, H / 2 - 40);
                drawStatus();
                g.unlock();
            }

            //appear process
            else if (scene == S_APPEAR) {
                //init
                enHP = EN_MAXHP[enType];
                //flush
                sleep(300);
                for(int i = 0; i < 6; i++) {
                    g.lock();
                    if (i%2 == 0) {
                        g.setColor(Color.rgb(0, 0, 0));
                    }
                    else {
                        g.setColor(Color.rgb(255, 255, 255));
                    }
                    g.unlock();
                    sleep(100);
                }

                //message
                drawBattle(EN_NAME[enType] + "が現れた");
                waitSelect();
                init = S_COMMAND;
            }

            //command
            else if (scene == S_COMMAND) {
                drawBattle("   1.攻撃   2.逃げる");
                key = KEY_NONE;
                while (init == -1) {
                    if (key == KEY_1) init = S_ATTACK;
                    if (key == KEY_2) init = S_ESCAPE;
                    sleep(100);
                }
            }

            //attack process
            else if (scene == S_ATTACK) {
                //message
                drawBattle("勇者の攻撃");
                waitSelect();

                //flush
                for (int i = 0; i < 10; i++) {
                    drawBattle("勇者の攻撃", i%2==0);
                    sleep(100);
                }

                //attack calculation
                int damage = YU_ATTACK[yuLV]-EN_DEFENCE[enType]+rand(10);
                if (damage <= 1) damage = 1;
                if (damage >= 99) damage = 99;

                //message
                drawBattle(damage + "ダメージを与えた");
                waitSelect();

                //calculate HP
                enHP -= damage;
                if (enHP <= 0) enHP = 0;

                //victory
                init = S_DEFENCE;
                if (enHP == 0) {
                    //message
                    drawBattle(EN_NAME[enType] + "を倒した");
                    waitSelect();

                    //calculate EXP
                    yuEXP += EN_EXP[enType];
                    if (yuLV<3 && YU_EXP[yuLV+1]<=yuEXP) {
                        yuLV++;
                        drawBattle("勇者はレベルアップした");
                        waitSelect();
                    }

                    //ending
                    if (enType == 1) {
                        g.lock();
                        g.setColor(Color.rgb(0, 0, 0));
                        g.fillRect(0, 0, W, H);
                        g.setColor(Color.rgb(255, 255, 255));
                        g.setTextSize(32);
                        String str = "Fin.";
                        g.drawText(str, (W-g.measureText(str))/2, 180-(int)g.getFontMetrics().top);
                        g.unlock();
                        waitSelect();
                        init = S_START;
                    }
                    init = S_MAP;
                }
            }

            //defence calculation
            else if (scene == S_DEFENCE) {
                //message
                drawBattle(EN_NAME[enType] + "の攻撃");
                waitSelect();

                //flush
                for (int i = 0; i < 10; i++) {
                    if (i%2 ==0) {
                        g.lock();
                        g.setColor(Color.rgb(255, 255, 255));
                        g.fillRect(0, 0, W, H);
                        g.unlock();
                    }
                    else {
                        drawBattle(EN_NAME[enType] + "の攻撃");
                    }
                    sleep(100);
                }

                //calculate for defence
                int damage = EN_ATTACK[enType]-YU_DEFENCE[yuLV]+rand(10);
                if (damage <= 1) damage = 1;
                if (damage >=99) damage = 99;

                //message
                drawBattle(damage + "ダメージを受けた");
                waitSelect();

                //calculate HP
                yuHP -= damage;
                if (yuHP <= 0) yuHP = 0;

                //Lose
                init = S_COMMAND;
                if (yuHP == 0) {
                    drawBattle("勇者は力尽きた");
                    waitSelect();
                    init = S_START;
                }
            }

            //escape
            else if (scene == S_ESCAPE) {
                //message
                drawBattle("勇者は逃げ出した");
                waitSelect();

                //calculation for escape
                init = S_MAP;
                if (enType == 1 || rand(100) <= 10) {
                    drawBattle(EN_NAME[enType]+"は回りこんだ");
                    waitSelect();
                    init = S_DEFENCE;
                }
            }

            //sleep
            key = KEY_NONE;
            sleep(200);
        }
    }

    private void drawBattle(String message) {
        drawBattle(message, enHP >= 0);
    }


    private void drawBattle(String message, boolean visible) {
        int color = (yuHP != 0) ? Color.rgb(0, 0, 0) : Color.rgb(255, 0, 0);
        g.lock();
        g.setColor(color);
        g.fillRect(0, 0, W, H);
        drawStatus();
        if (visible) {
            g.drawBitmap(bmp[5+enType], (W-bmp[5+enType].getWidth())/2, H-100-bmp[5+enType].getHeight());
        }
        g.setColor(Color.rgb(255, 255, 255));
        g.fillRect((W - 504) / 2, H - 122, 504, 104);
        g.setColor(color);
        g.fillRect((W - 500) / 2, H - 120, 500, 100);
        g.setColor(Color.rgb(255, 255, 255));
        g.setTextSize(32);
        g.drawText(message, (W - 500) / 2 + 50, 370 - (int) g.getFontMetrics().top);
        g.unlock();
    }


    //show status
    private void drawStatus() {
        int color = (yuHP != 0) ? Color.rgb(0, 0, 0) : Color.rgb(255, 0, 0);
        g.setColor(Color.rgb(255, 255, 255));
        g.fillRect((W - 500) / 2, 10, 504, 54);
        g.setColor(color);
        g.fillRect((W - 500) / 2, 0, 500, 50);
        g.setColor(Color.rgb(255, 255, 255));
        g.setTextSize(32);
        g.drawText("勇者 LV" + yuLV + "  HP" + yuHP + "/" + YU_MAXHP, (W - 500) / 2 + 80, 15 - (int) g.getFontMetrics().top);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX = (int)(event.getX()*W/getWidth());
        int touchY = (int)(event.getY()*W/getHeight());
        int touchAction = event.getAction();
        if(touchAction ==MotionEvent.ACTION_DOWN) {
            if (scene == S_MAP) {
                if (Math.abs(touchX-W/2) > Math.abs(touchY-H/2)) {
                    key = (touchX-W/2 < 0) ? KEY_LEFT:KEY_RIGHT;
                }
                else {
                    key = (touchY-H/2 < 0) ? KEY_UP:KEY_DOWN;
                }
            }
            else if (scene == S_APPEAR || scene == S_ATTACK || scene == S_DEFENCE || scene == S_ESCAPE) {
                key = KEY_SELECT;
            }
            else if (scene == S_COMMAND) {
                if (W/2-250 < touchX && touchX < W/2 && H-190 < touchY && touchY < H) {
                    key = KEY_1;
                }
                else if (W/2 < touchX && touchX < W/2+250 && H-190 < touchY && touchY < H) {
                    key = KEY_2;
                }
            }
        }
        return true;
    }


    //wait fot key
    private void waitSelect() {
        key = KEY_NONE;
        while (key!=KEY_SELECT) sleep(100);
    }


    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
        }
    }


    //generates Random number
    private static Random rand = new Random();
    public static int rand(int num) {
        return (rand.nextInt()>>>1)%num;
    }

    private static Bitmap readBitmap(Context context, String name) {
        int resID = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        return BitmapFactory.decodeResource(context.getResources(), resID);
    }
}
