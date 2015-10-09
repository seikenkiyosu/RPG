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

import Field.Field;
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

    //味方モンスター
    static Monster party[] = new Monster[3];  //味方
    private int stage[];    //ステージ1-1
    private int
        positionX,
        positionY;

    //敵モンスター
    static Monster enemy[] = new Monster[3];  //敵

    //マップ
    private int MAP[][];

    //system
    private SurfaceHolder holder;
    private Graphics      g;
    private Thread        thread;
    private int           init = S_START;
    public int            scene;
    public int            key;
    private Bitmap        bmparroykey;  //十字キーのためのビットマップ
    private Bitmap[]      bmpmaps;      //マップのためのビットマップ
    private Bitmap[]      bmpmonster;   //モンスターのためのビットマップ

    //持っているお金
    private int Money = 0;

    //コンストラクタ
    public RPGView(Activity activity) {
        super(activity);
        //パーティ読み込み
        party[0] = Monster.MonsterOutput(1, 1);    //ダークマッシュをパーティに

        //最初のステージ
        stage = new int[2];
        stage[0] = 1;
        stage[1] = 2;


        //map読み込み
        MAP = new int[Field.map[stage[0]-1][stage[1]-1].length][];
        for (int i = 0; i < Field.map[stage[0]-1][stage[1]-1].length; i++) {
            MAP[i] = new int[Field.map[stage[0]-1][stage[1]-1][i].length];
            for (int j = 0; j < Field.map[stage[0]-1][stage[1]-1][i].length; j++) {
                MAP[i][j] = Field.map[stage[0]-1][stage[1]-1][i][j];
            }
        }

        //bitmap読み込み
        bmparroykey = readBitmap(activity, "arroykey");
        bmpmaps = new Bitmap[Field.MAPKINDNUM];
        bmpmonster = new Bitmap[Monster.MONSTERNUM+1];
        for (int i = 0; i < Field.MAPKINDNUM; i++) {
            bmpmaps[i] = readBitmap(activity, "map"+i);
        }
        for (int i = 1; i <= Monster.MONSTERNUM; i++) {
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


    //サーフェイス生成
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new Thread(this);
        thread.start();
    }

    //サーフェイス変更
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    //サーフェイス終了
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread = null;
    }


    //スレッドの処理
    public void run() {
        while(thread != null) {
            //init scene
            if (init >= 0) {
                scene = init;
                //start(yuのステータスは死んだときに意味がある)
                if(scene == S_START) {
                    scene = S_MAP;
                    positionX   = 1;
                    positionY   = 2;
                    //パーティ全回復
                    for (int i = 0; i < party.length; i++) {
                        if (party[i] != null) {
                            party[i].HP = party[i].MAXHP[party[i].LV];
                            party[i].SP = party[i].MAXSP[party[i].LV];
                        }
                    }
                }
                init = -1;
                key = KEY_NONE;
            }

            //map
            switch (scene) {
                case S_MAP:
                    //マップ移動
                    boolean flag = false;
                    if (key == KEY_UP) {    //勇者の移動
                        if (0<=positionY-1) {
                            if (MAP[positionY - 1][positionX] <= 2) {     //上
                                positionY--;
                                flag = true;
                            }
                        }
                    } else if (key == KEY_DOWN) {
                        if (positionY+1<=MAP.length-1)
                        if (MAP[positionY+1][positionX] <= 2) {      //下
                            positionY++;
                            flag = true;
                        }
                    } else if (key == KEY_LEFT) {   //左
                        if (0<=positionX-1) {
                            if (MAP[positionY][positionX-1] <= 2) {
                                positionX--;
                                flag = true;
                            }
                        }
                    } else if (key == KEY_RIGHT) {        //右
                        if (positionX+1 <= MAP[positionY].length-1) {
                            if (MAP[positionY][positionX+1] <= 2) {
                                positionX++;
                                flag = true;
                            }
                        }
                    }

                    //マップ上のイベント
                    if (flag) {
                        if (MAP[positionY][positionX] == 0 && rand(100) < 10) {
                            enemy = new Monster[1];
                            int r = rand(100);
                            if (r < 75) {
                                enemy[0] = Monster.MonsterOutput(1, 1);
                            } else {
                                enemy[0] = Monster.MonsterOutput(2, 1);
                            }
                            init = S_APPEAR;
                        } else if (MAP[positionY][positionX] == 1) {    //宿屋
                            party[0].HP = party[0].MAXHP[party[0].LV];
                            party[0].SP = party[0].MAXSP[party[0].LV];
                        } else if (MAP[positionY][positionX] == 2) {    //ボス出現
                            enemy[0] = Monster.MonsterOutput(3, 1);
                            init = S_APPEAR;
                        }
                    }

                    //マップ描画
                    if (init != S_APPEAR) {
                        g.lock();
                        for (int j = -3; j <= 3; j++) {
                            for (int i = -5; i <= 5; i++) {
                                int idx = 3;
                                if (0 <= positionX+i && positionX+i < MAP[0].length && 0 <= positionY+j && positionY+j < MAP.length) {
                                    idx = MAP[positionY+j][positionX+i];    //フィールド内部を描画
                                }
                                g.drawBitmap(bmpmaps[idx], W/2-40+80*i, H/2-40+80*j);   //周りの木を描画
                            }
                        }
                        g.drawMonsterInMap(bmpmonster[party[0].MONSTERNUMBER], W/2-40, H/2-40);
                        g.drawBitmap(bmparroykey, W/2-40+80*(-4), H/2-40+80);       //マップ上に十字キー描画
                        drawStatus();
                        g.unlock();

                    }
                    break;

                //モンスター出現
                case S_APPEAR:
                    //戦闘前のフラッシュ
                    sleep(300);
                    for (int i = 0; i <= 6; i++) {
                        g.lock();
                        if (i % 2 == 0) {
                            g.setColor(Color.rgb(0, 0, 0));
                        } else {
                            g.setColor(Color.rgb(255, 255, 255));
                        }
                        g.fillRect(0, 0, W, H);
                        g.unlock();
                        sleep(100);
                    }

                    //message
                    drawBattle(enemy[0].NAME + "が現れた");
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
                        drawBattle(party[0].NAME + "の攻撃!");
                        waitSelect();
                        if (rand(100) <= 90) {
                            //flush
                            for (int i = 0; i < 10; i++) {
                                drawBattle(party[0].NAME + "の攻撃!", i % 2 == 0);
                                sleep(100);
                            }

                            //attack calculation
                            int damage = party[0].ATTACK[party[0].LV] - enemy[0].DEFENCE[enemy[0].LV] + rand(10);
                            if (damage <= 1) damage = 1;

                            //会心の一撃
                            if (rand(100) <= 8) {
                                drawBattle("急所に当たった!");
                                waitSelect();
                                damage += enemy[0].DEFENCE[enemy[0].LV];
                                damage *= 2;
                            }

                            //message
                            drawBattle(damage + "ダメージを与えた");
                            waitSelect();

                            //calculate HP
                            enemy[0].HP -= damage;
                            if (enemy[0].HP <= 0) enemy[0].HP = 0;
                        } else {
                            drawBattle(enemy[0].NAME + "は回避した");
                            waitSelect();
                        }
                    }
                    //スキルフェーズ
                    else {
                        drawBattle(Skill.NAME[party[0].SKILL[0]], Skill.NAME[party[0].SKILL[1]], Skill.NAME[party[0].SKILL[2]], Skill.NAME[party[0].SKILL[3]]);
                        waitSelect();
                        int choice = -1;
                        key = KEY_NONE;
                        scene = S_COMMAND;
                        while (choice == -1) {
                                 if (key == KEY_1 && party[0].SKILL[0] != 0) choice = party[0].SKILL[0];
                            else if (key == KEY_2 && party[0].SKILL[1] != 0) choice = party[0].SKILL[1];
                            else if (key == KEY_3 && party[0].SKILL[2] != 0) choice = party[0].SKILL[2];
                            else if (key == KEY_4 && party[0].SKILL[3] != 0) choice = party[0].SKILL[3];
                            sleep(100);
                        }
                        scene = S_ATTACK;

                        Skill.Skillcast(this, choice, party[0], enemy[0]);

                        isskill = false;
                    }

                    init = S_DEFENCE;
                    //victory
                    if (enemy[0].HP == 0) {
                        //message
                        drawBattle(enemy[0].NAME + "を倒した", false);
                        waitSelect();
                        drawBattle(enemy[0].DROPEXP[enemy[0].LV] + " 経験値を手に入れた", false);
                        waitSelect();

                        //calculate EXP
                        party[0].GETEXP += enemy[0].DROPEXP[enemy[0].LV];
                        while (party[0].EXP[party[0].LV] <= party[0].GETEXP) {
                            party[0].GETEXP -= party[0].EXP[party[0].LV];
                            party[0].LV++;
                            party[0].HP = party[0].MAXHP[party[0].LV] * party[0].HP / party[0].MAXHP[party[0].LV-1];    //体力回復(レベルアップ前のHPゲージを保つ計算)
                            party[0].SP = party[0].MAXSP[party[0].LV] * party[0].SP / party[0].MAXSP[party[0].LV-1];    //SP回復
                            drawBattle(party[0].NAME + "は", "LV " + party[0].LV + " にアップした", false);
                            waitSelect();
                        }

                        drawBattle("次のレベルアップまで  ", ("あと ") + (party[0].EXP[party[0].LV] - party[0].GETEXP) + (" 経験値"), false);
                        waitSelect();

                        //ドロップマネー
                        drawBattle(enemy[0].DROPMAONEY[enemy[0].LV] + "G を手に入れた", false);
                        Money += enemy[0].DROPMAONEY[enemy[0].LV];
                        waitSelect();

                        init = S_MAP;
                    }
                    break;

                //防御
                case S_DEFENCE:
                    //message
                    if (enemy[0].ESCAPEPERCENT <= rand(100)) {
                        drawBattle(enemy[0].NAME + "の攻撃");
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
                                    drawBattle(enemy[0].NAME + "の攻撃");
                                }
                                sleep(100);
                            }
                            //calculate for defence
                            int damage = enemy[0].ATTACK[enemy[0].LV] - party[0].DEFENCE[party[0].LV] + rand(10);
                            if (damage <= 1) damage = 1;

                            //会心の一撃
                            if (rand(100) <= 8) {
                                drawBattle("痛恨の一撃!");
                                waitSelect();
                                damage += party[0].DEFENCE[party[0].LV];
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
                            party[0].HP -= damage;
                            if (party[0].HP <= 0) party[0].HP = 0;
                        } else {
                            drawBattle(party[0].NAME + "は回避した");
                            waitSelect();
                        }
                        //Lose
                        init = S_COMMAND;
                        if (party[0].HP == 0) {
                            drawBattle("全滅してしまった");
                            waitSelect();
                            init = S_START;
                        }
                    }
                    else {
                        drawBattle(enemy[0].NAME + "は逃げ出した", false);
                        waitSelect();
                        init = S_START;
                    }
                    break;

               //escape
                case S_ESCAPE :
                    //message
                    drawBattle(party[0].NAME + "逃げ出した");
                    waitSelect();

                    //calculation for escape
                    init = S_MAP;
                    if (rand(100) <= 60) {
                        drawBattle(enemy[0].NAME+"は回りこんだ");
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


    public void drawBattle(String message) {
        drawBattle(message, enemy[0].HP >= 0);
    }


    public void drawBattle(String message, boolean visible) {
        int color = (party[0].HP != 0) ? Color.rgb(0, 0, 0) : Color.rgb(255, 0, 0);
        g.lock();
        g.setColor(color);
        g.fillRect(0, 0, W, H);
        drawStatus();
        if (visible) {
            g.drawBitmap(bmpmonster[enemy[0].MONSTERNUMBER],
                    W / 2 - (bmpmonster[enemy[0].MONSTERNUMBER].getWidth()) / 2,
                    H / 2 - bmpmonster[enemy[0].MONSTERNUMBER].getHeight() + 80);
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

    public void drawBattle(String message1, String message2, boolean visible) {
        int color = (party[0].HP != 0) ? Color.rgb(0, 0, 0) : Color.rgb(255, 0, 0);
        g.lock();
        g.setColor(color);
        g.fillRect(0, 0, W, H);
        drawStatus();
        if (visible) {
            g.drawBitmap(bmpmonster[enemy[0].MONSTERNUMBER]
                    , W / 2 - (bmpmonster[enemy[0].MONSTERNUMBER].getWidth()) / 2
                    , H / 2 - bmpmonster[enemy[0].MONSTERNUMBER].getHeight() + 80);
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

    public void drawBattle (String message1, String message2, String message3, String message4) {
        int color = (party[0].HP != 0) ? Color.rgb(0, 0, 0) : Color.rgb(255, 0, 0);
        g.lock();
        g.setColor(color);
        g.fillRect(0, 0, W, H);
        drawStatus();
        g.drawBitmap(bmpmonster[enemy[0].MONSTERNUMBER]
                , W / 2 - (bmpmonster[enemy[0].MONSTERNUMBER].getWidth()) / 2
                , H / 2 - bmpmonster[enemy[0].MONSTERNUMBER].getHeight() + 80);

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
    public void drawStatus() {
        int color = (party[0].HP != 0) ? Color.rgb(231, 232, 226) : Color.rgb(255, 0, 0);    //シルバーグレーor赤
        g.setColor(color);
        g.fillRect(50, 10, 200, 90);     //味方のステータス表示
        g.fillRect(W - 130, 10, 120, 40);   //金表示

        g.setColor(Color.rgb(0, 0, 0));   //白

        g.setTextSize(20);
        g.drawText(party[0].NAME + " Lv." + party[0].LV, 60, 15 - (int) g.getFontMetrics().top);
        g.drawText("HP  " + party[0].HP + "/" + party[0].MAXHP[party[0].LV], 60, 15 - (int) g.getFontMetrics().top*2+5);
        g.drawText("SP  " + party[0].SP + "/" + party[0].MAXSP[party[0].LV], 60, 15 - (int) g.getFontMetrics().top*3+8);
        g.drawText(Money + "G", W-130+10, 15 - (int)g.getFontMetrics().top);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX = (int)(event.getX()*W/getWidth());
        int touchY = (int)(event.getY()*H/getHeight());
        int touchAction = event.getAction();
        if(touchAction ==MotionEvent.ACTION_DOWN) {
            if (scene == S_MAP) {
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
    public void waitSelect() {
        key = KEY_NONE;
        while (key!=KEY_SELECT) sleep(100);
    }


    public void sleep(int time) {
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
        int resID = context.getResources().getIdentifier(name, "drawables", context.getPackageName());
        return BitmapFactory.decodeResource(context.getResources(), resID);
    }
}
