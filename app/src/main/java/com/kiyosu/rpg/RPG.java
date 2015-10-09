package com.kiyosu.rpg;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class RPG extends Activity {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        RPGView rpgView = new RPGView(this);
        setContentView(rpgView);
    }
}
