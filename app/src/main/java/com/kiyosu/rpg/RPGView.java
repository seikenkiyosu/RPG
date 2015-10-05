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

import Monster.*;

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
        W = 800,
        H = 480;

    //key constant
    private final static int
        KEY_NONE   = -1,
        KEY_LEFT   = 0,
        KEY_RIGHT  = 1,
        KEY_UP     = 2,
        KEY_DOWN   = 3,
        KEY_1      = 4,
        KEY_2      = 5,
        KEY_3      = 6,
        KEY_4      = 7,
        KEY_SELECT = 8;

    private boolean isdefence = false;
    private boolean isskill = false;

    //Map constant
    private final static int MAPkind = 6;
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
        YU_MAXHP   = {0,  30,  50,   70, 100, 120,  140, 160, 190, 220, 250, 300},
        YU_MAXSP   = {0,   5,   7,    8,  10,  12,   14,  16,  18,  20,  22,  24},
        YU_ATTACK  = {0,   5,  10,   30,  35,  40,   45,  55,  65,  75,  80, 100},
        YU_DEFENCE = {0,   0,   5,   10,  15,  20,   25,  30,  35,  40,  45,  50},
        YU_EXP     = {0,   1,   1,    3,   6,  10,   20,  50, 100, 180, 280, 400};


    private final static String[]
        SKILL = {
            "",
            "アンパンチ",
            "アンキック",
            "はかいこうせん",
            "アンドライブ"
    };

    //enemy constant
    private final static int MonsterNum = 3;
    private final static String[]
        EN_NAME = {"大怪獣せいけーん", "マッシュ", "トゲマッシュ"};
    private final static int[]
        EN_MAXHP   = {           50,        10,            20},
        EN_ATTACK  = {           26,        10,            15},
        EN_DEFENCE = {           16,         0,             3},
        EN_EXP     = {           30,         1,             3},
        EN_ESP     = {            0,         1,            10}, //escape
        EN_DM      = {            1,         5,            20}; //Drop Money

    //system
    private SurfaceHolder holder;
    private Graphics      g;
    private Thread        thread;
    private int           init = S_START;
    private int           scene;
    private int           key;
    private Bitmap[]      bmpmaps = new Bitmap[MAPkind];
    private Bitmap[]      bmpmonster = new Bitmap[MonsterNum];

    //brain parameter
    private int yuX   = 1;
    private int yuY   = 2;
    private int yuLV  = 1;
    private int yuHP  = 30;
    private int yuSP  = 5;
    private int yuEXP = 0;

    private int[]
            yu_SKILL = {1, 2, 3, 4};

    private int Money = 0;

    //enemy parameter
    private int enType;
    private int enHP;

    //constructor
    public RPGView(Activity activity) {
        super(activity);
        //read brain parameter

        //read bitmap
        for (int i = 0; i < MAPkind; i++) {
            bmpmaps[i] = readBitmap(activity, "map"+i);
        }
        for (int i = 0; i < MonsterNum; i++) {
           bmpmonster[i] = readBitmap(activity, "monster"+i);
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
                //start(yuのステータスは死んだときに意味がある)
                if(scene == S_START) {
                    scene = S_MAP;
                    yuX   = 1;
                    yuY   = 2;
//                    yuLV  = 1;
                    yuHP  = YU_MAXHP[yuLV];
//                    yuEXP = 0;
                }
                init = -1;
                key = KEY_NONE;
            }

            //map
            switch (scene) {
                case S_MAP:
                    //move
                    boolean flag = false;
                    if (key == KEY_UP) {    //勇者の移動
                        if (MAP[yuY - 1][yuX] <= 2) {     //上
                            yuY--;
                            flag = true;
                        }
                    } else if (key == KEY_DOWN) {
                        if (MAP[yuY + 1][yuX] <= 2) {      //下
                            yuY++;
                            flag = true;
                        }
                    } else if (key == KEY_LEFT) {         //左
                        if (MAP[yuY][yuX - 1] <= 2) {
                            yuX--;
                            flag = true;
                        }
                    } else if (key == KEY_RIGHT) {        //右
                        if (MAP[yuY][yuX + 1] <= 2) {
                            yuX++;
                            flag = true;
                        }
                    }
                    //モンスター出現確率
                    if (flag) {
                        if (MAP[yuY][yuX] == 0 && rand(100) < 10) {
                            int r = rand(100);
                            if (r < 25) {
                                enType = 2;
                            } else {
                                enType = 1;
                            }
                            init = S_APPEAR;
                        } else if (MAP[yuY][yuX] == 1) {    //宿屋
                            yuHP = YU_MAXHP[yuLV];
                            yuSP = YU_MAXSP[yuLV];
                        } else if (MAP[yuY][yuX] == 2) {    //ボス出現
                            enType = 0;
                            init = S_APPEAR;
                        }
                    }

                    //for draw
                    if (init != S_APPEAR) {
                        g.lock();
                        for (int j = -3; j <= 3; j++) {
                            for (int i = -5; i <= 5; i++) {
                                int idx = 3;
                                if (0 <= yuX + i && yuX + i < MAP[0].length && 0 <= yuY + j && yuY + j < MAP[0].length) {
                                    idx = MAP[yuY + j][yuX + i];
                                }
                                g.drawBitmap(bmpmaps[idx], W / 2 - 40 + 80 * i, H / 2 - 40 + 80 * j);   //マップフィールド描画
                            }
                        }
                        g.drawBitmap(bmpmaps[4], W / 2 - 40, H / 2 - 40);   //マップ上に勇者描画
                        g.drawBitmap(bmpmaps[5], W / 2 - 40 + 80 * (-4), H / 2 - 40 + 80);       //マップ上に十字キー描画
                        drawStatus();
                        g.unlock();
                    }
                    break;
                //appear process
                case S_APPEAR:
                    //init
                    enHP = EN_MAXHP[enType];
                    //戦闘前のフラッシュ
                    sleep(300);
                    for (int i = 0; i <= 6; i++) {
                        g.lock();
                        if (enType >= 1) {
                            if (i % 2 == 0) {
                                g.setColor(Color.rgb(0, 0, 0));
                            } else {
                                g.setColor(Color.rgb(255, 255, 255));
                            }
                        }
                        //ボスフラッシュ
                        else if (enType == 0) {
                            if (i % 2 == 0) {
                                g.setColor(Color.rgb(140, 140, 140));
                            } else {
                                g.setColor(Color.rgb(255, 255, 255));
                            }
                        }
                        g.fillRect(0, 0, W, H);
                        g.unlock();
                        sleep(100);
                    }

                    //message
                    drawBattle(EN_NAME[enType] + "が現れた");
                    waitSelect();
                    init = S_COMMAND;
                    break;

                //command
                case S_COMMAND :
                    drawBattle("攻撃", "防御", "スキル", "逃げる");
                    key = KEY_NONE;
                    while (init == -1) {
                        if (key == KEY_1) init = S_ATTACK;
                        else if (key == KEY_2) { isdefence = true; init = S_DEFENCE; }
                        else if (key == KEY_3) { isskill = true; init = S_ATTACK; }
                        else if (key == KEY_4) init = S_ESCAPE;
                        sleep(100);
                    }
                    break;

                //attack process
                case S_ATTACK:
                    if (!isskill) {
                        drawBattle("勇者の攻撃");
                        waitSelect();
                        if (rand(100) <= 90) {
                            //flush
                            for (int i = 0; i < 10; i++) {
                                drawBattle("勇者の攻撃", i % 2 == 0);
                                sleep(100);
                            }

                            //attack calculation
                            int damage = YU_ATTACK[yuLV] - EN_DEFENCE[enType] + rand(10);
                            if (damage <= 1) damage = 1;

                            //会心の一撃
                            if (rand(100) <= 8) {
                                drawBattle("急所に当たった!");
                                waitSelect();
                                damage += EN_DEFENCE[enType];
                                damage *= 2;
                            }

                            //message
                            drawBattle(damage + "ダメージを与えた");
                            waitSelect();

                            //calculate HP
                            enHP -= damage;
                            if (enHP <= 0) enHP = 0;
                        } else {
                            drawBattle(EN_NAME[enType] + "は回避した");
                            waitSelect();
                        }
                    }
                    //スキルフェーズ
                    else {
                        drawBattle(SKILL[yu_SKILL[0]], SKILL[yu_SKILL[1]], SKILL[yu_SKILL[2]], SKILL[yu_SKILL[3]]);
                        waitSelect();
                        int choice = -1;
                        key = KEY_NONE;
                        scene = S_COMMAND;
                        while (choice == -1) {
                            if (key == KEY_1 && yu_SKILL[0] != 0) choice = yu_SKILL[0];
                            else if (key == KEY_2 && yu_SKILL[1] != 0) choice = yu_SKILL[1];
                            else if (key == KEY_3 && yu_SKILL[2] != 0) choice = yu_SKILL[2];
                            else if (key == KEY_4 && yu_SKILL[3] != 0) choice = yu_SKILL[3];
                            sleep(100);
                        }
                        scene = S_ATTACK;


                        int damage = 0;
                        switch (SKILL[yu_SKILL[choice-1]]) {
                            case "アンパンチ":
                                damage = 20;
                                break;
                            case "アンキック":
                                damage = 50;
                                break;
                            case "はかいこうせん":
                                damage = 100;
                                break;
                            case "アンドライブ":
                                damage = 1000;
                                break;
                        }

                        for (int i = 0; i < 20; i++) {
                            drawBattle("勇者の" + SKILL[yu_SKILL[choice-1]], i%2==0);
                            sleep(50);
                        }

                        drawBattle(damage + "ダメージを与えた");
                        waitSelect();

                        //calculate HP
                        enHP -= damage;
                        if (enHP <= 0) enHP = 0;

                        isskill = false;
                    }

                    init = S_DEFENCE;
                    //victory
                    if (enHP == 0) {
                        //message
                        drawBattle(EN_NAME[enType] + "を倒した", false);
                        waitSelect();
                        drawBattle("勇者は " + EN_EXP[enType] + " 経験値を手に入れた", false);
                        waitSelect();

                        //calculate EXP
                        yuEXP += EN_EXP[enType];
                        while (YU_EXP[yuLV] <= yuEXP) {
                            yuEXP -= YU_EXP[yuLV];
                            yuLV++;
                            yuHP = YU_MAXHP[yuLV] * yuHP / YU_MAXHP[yuLV-1];    //体力回復
                            yuSP = YU_MAXSP[yuLV] * yuSP / YU_MAXSP[yuLV-1];    //SP回復
                            drawBattle("勇者は LV " + yuLV + " にアップした", false);
                            waitSelect();
                        }

                        drawBattle("次のレベルアップまで  ", ("あと ") + (YU_EXP[yuLV] - yuEXP) + (" 経験値"), false);
                        waitSelect();

                        drawBattle(EN_DM[enType] + "G を手に入れた", false);
                        Money += EN_DM[enType];
                        waitSelect();

                        //ending
                        if (enType == 0) {  //ボスだったら
                            g.lock();
                            g.setColor(Color.rgb(0, 0, 0));
                            g.fillRect(0, 0, W, H);
                            g.setColor(Color.rgb(255, 255, 255));
                            g.setTextSize(40);
                            String str = "Fin.";
                            g.drawText(str, (W - g.measureText(str)) / 2, 180 - (int) g.getFontMetrics().top);
                            g.unlock();
                            waitSelect();
                            init = S_START;
                        }
                        init = S_MAP;
                    }
                    break;

                //defence calculation
                case S_DEFENCE:
                    //message
                    if (EN_ESP[enType] <= rand(100)) {
                        drawBattle(EN_NAME[enType] + "の攻撃");
                        waitSelect();
                        if (rand(100) <= 90) {
                            //flush
                            for (int i = 0; i < 10; i++) {
                                if (i % 2 == 0) {
                                    g.lock();
                                    g.setColor(Color.rgb(255, 255, 255));
                                    g.fillRect(0, 0, W, H);
                                    g.unlock();
                                } else {
                                    drawBattle(EN_NAME[enType] + "の攻撃");
                                }
                                sleep(100);
                            }
                            //calculate for defence
                            int damage = EN_ATTACK[enType] - YU_DEFENCE[yuLV] + rand(10);
                            if (damage <= 1) damage = 1;

                            //会心の一撃
                            if (rand(100) <= 8) {
                                drawBattle("痛恨の一撃!");
                                waitSelect();
                                damage += YU_DEFENCE[yuLV];
                                damage *= 2;
                            }

                            if (isdefence) {    //防御をしていたら
                                damage /= 3;
                                isdefence = false;
                            }

                            //message
                            drawBattle(damage + "ダメージを受けた");
                            waitSelect();

                            //calculate HP
                            yuHP -= damage;
                            if (yuHP <= 0) yuHP = 0;
                        } else {
                            drawBattle("勇者は回避した");
                            waitSelect();
                        }
                        //Lose
                        init = S_COMMAND;
                        if (yuHP == 0) {
                            drawBattle("勇者は力尽きた");
                            waitSelect();
                            init = S_START;
                        }
                    }
                    else {
                        drawBattle(EN_NAME[enType] + "は逃げ出した", false);
                        waitSelect();
                        init = S_START;
                    }
                    break;
               //escape
                case S_ESCAPE :
                    //message
                    drawBattle("勇者は逃げ出した");
                    waitSelect();

                    //calculation for escape
                    init = S_MAP;
                    if (enType == 0 || rand(100) <= 50 && enType >= 1) {
                        drawBattle(EN_NAME[enType]+"は回りこんだ");
                        waitSelect();
                        init = S_DEFENCE;
                    }
                    break;
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
            g.drawBitmap(bmpmonster[enType], W / 2 - (bmpmonster[enType].getWidth()) / 2, H / 2 - bmpmonster[enType].getHeight() + 80);
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

    private void drawBattle(String message1, String message2, boolean visible) {
        int color = (yuHP != 0) ? Color.rgb(0, 0, 0) : Color.rgb(255, 0, 0);
        g.lock();
        g.setColor(color);
        g.fillRect(0, 0, W, H);
        drawStatus();
        if (visible) {
            g.drawBitmap(bmpmonster[enType], W / 2 - (bmpmonster[enType].getWidth()) / 2, H / 2 - bmpmonster[enType].getHeight() + 80);
        }
        g.setColor(Color.rgb(255, 255, 255));
        g.fillRect((W - 504) / 2, H - 122, 504, 104);
        g.setColor(color);
        g.fillRect((W - 500) / 2, H - 120, 500, 100);
        g.setColor(Color.rgb(255, 255, 255));
        g.setTextSize(32);

        g.drawText(message1, (W - 500) / 2 + 50, 370 - (int) g.getFontMetrics().top);
        g.drawText(message2, (W - 500) / 2 + 50, 370 - (int) g.getFontMetrics().top*2 +10);
        g.unlock();
    }

    private void drawBattle (String message1, String message2, String message3, String message4) {
        int color = (yuHP != 0) ? Color.rgb(0, 0, 0) : Color.rgb(255, 0, 0);
        g.lock();
        g.setColor(color);
        g.fillRect(0, 0, W, H);
        drawStatus();
        g.drawBitmap(bmpmonster[enType], W / 2 - (bmpmonster[enType].getWidth()) / 2, H / 2 - bmpmonster[enType].getHeight() + 80);

        g.setColor(Color.rgb(255, 255, 255));
        g.fillRect((W - 504) / 2, H - 122, 504, 104);
        g.setColor(color);
        g.fillRect((W - 500) / 2, H - 120, 500, 100);
        g.setColor(Color.rgb(255, 255, 255));
        g.setTextSize(26);

        g.drawText(message1, (W - 500) / 2 + 50, 370 - (int) g.getFontMetrics().top);
        g.drawText(message2, (W - 500) / 2 + 270, 370 - (int) g.getFontMetrics().top);
        g.drawText(message3, (W - 500) / 2 + 50, 370 - (int) g.getFontMetrics().top*2 +10);
        g.drawText(message4, (W - 500) / 2 + 270, 370 - (int) g.getFontMetrics().top*2 +10);
        g.unlock();
    }

    //show status
    private void drawStatus() {
//        int color = (yuHP != 0) ? Color.rgb(0, 0, 0) : Color.rgb(255, 0, 0);    //黒or赤
        int color = (yuHP != 0) ? Color.rgb(231, 232, 226) : Color.rgb(255, 0, 0);    //シルバーグレーor赤
//        g.setColor(Color.rgb(255, 255, 255));
//        g.fillRect((W - 504)/2, 8, 504, 54);
        g.setColor(color);
//        g.fillRect((W - 500) / 2, 10, 500, 50);
//        g.fillRect((W - 300) / 2, 10, 300, 30);
        g.fillRect(50, 10, 150, 100);
        g.fillRect(W-130, 10, 120, 40);

//        g.setColor(Color.rgb(255, 255, 255));   //白
        g.setColor(Color.rgb(0, 0, 0));   //白

//        g.setTextSize(32);
        g.setTextSize(24);
//        g.drawText("勇者 " + "LV." + yuLV, (W - 300) / 2 + 60, 15 - (int) g.getFontMetrics().top);
        g.drawText("勇者   " + "Lv." + yuLV, 60, 15 - (int) g.getFontMetrics().top);
        g.drawText("HP  " + yuHP + "/" + YU_MAXHP[yuLV], 60, 15 - (int) g.getFontMetrics().top*2+5);
        g.drawText("SP  " + yuSP + "/" + YU_MAXSP[yuLV], 60, 15 - (int) g.getFontMetrics().top*3+8);
        g.drawText(Money + "G", W-130+10, 15 - (int)g.getFontMetrics().top);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX = (int)(event.getX()*W/getWidth());
        int touchY = (int)(event.getY()*H/getHeight());
        int touchAction = event.getAction();
        if(touchAction ==MotionEvent.ACTION_DOWN) {
            if (scene == S_MAP) {
//                if (Math.abs(touchX-(W/2)) > Math.abs(touchY-H/2)) {
//                    key = (touchX-W/2 < 0) ? KEY_LEFT:KEY_RIGHT;
//                }
//                else {
//                    key = (touchY-H/2 < 0) ? KEY_UP:KEY_DOWN;
//                }
                //十字キー
                if (W/2-40+80*(-4) < touchX && touchX < W/2-40+80*(-2) && H/2-40+80 < touchY && touchY < H/2-40+80*3) {
                    if (Math.abs(touchX - (W / 2 - 40 + 80 * (-3))) > Math.abs(touchY - (H / 2 - 40 + 80 * 2))) {
                        key = (touchX - (W / 2 - 40 + 80 * (-3)) < 0) ? KEY_LEFT : KEY_RIGHT;
                    } else {
                        key = (touchY - (H / 2 - 40 + 80 * 2) < 0) ? KEY_UP : KEY_DOWN;
                    }
                }
            }
            else if (scene == S_APPEAR || scene == S_ATTACK || scene == S_DEFENCE || scene == S_ESCAPE) {
                key = KEY_SELECT;
            }
            else if (scene == S_COMMAND) {
                if (W/2-250 < touchX && touchX < W/2 && H-190 < touchY && touchY < H-70) {
                    key = KEY_1;
                }
                else if (W/2 < touchX && touchX < W/2+250 && H-190 < touchY && touchY < H-70) {
                    key = KEY_2;
                }
                else if (W/2-250 < touchX && touchX < W/2 && H-70 < touchY && touchY < H) {
                    key = KEY_3;
                }
                else if (W/2 < touchX && touchX < W/2+250 && H-70 < touchY && touchY < H) {
                    key = KEY_4;
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
        return (rand.nextInt(num));
    }

    private static Bitmap readBitmap(Context context, String name) {
        int resID = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        return BitmapFactory.decodeResource(context.getResources(), resID);
    }
}
