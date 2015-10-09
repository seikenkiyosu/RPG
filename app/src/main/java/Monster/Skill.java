package Monster;

import com.kiyosu.rpg.RPGView;

public class Skill {
    //スキル名
    public static String NAME[] = {
            "",
            "アンパンチ",
            "アンキック",
            "キック",
            "アンドライブ"
    };

    //スキル発動
    public static void Skillcast(RPGView rpg, int skillnumber, Monster attacker, Monster defender) {
        switch (skillnumber) {
            case 1:
                Anpanch(rpg, attacker, defender);
                break;
            case 2:
                Anchop(rpg, attacker, defender);
                break;
            case 3:
                Kick(rpg, attacker, defender);
                break;
            case 4:
                Ankick(rpg, attacker, defender);
                break;
            default:
        }
    }

    /*スキル番号ごとにスキル定義*/
    //1
    private static void Anpanch(RPGView rpg, Monster attacker, Monster defender) {
        int sp = 1;
        if (attacker.SP - sp >= 0) {   //SPが足りたら
            //スキル消費
            attacker.SP -= sp;
            //エフェクト
            for (int i = 0; i < 20; i++) {
                rpg.drawBattle(attacker.NAME + "のアンパンチ", i % 2 == 0);
                rpg.sleep(50);
            }
            //効果
            int damage = 10;
            rpg.drawBattle(defender.NAME + "に" + damage + "ダメージ!");
            rpg.waitSelect();
            defender.HP -= damage;
            if (defender.HP <= 0) defender.HP = 0;

        } else {  //SPが足りない場合
            rpg.drawBattle("SPが足りない!");
            rpg.waitSelect();
        }
    }

    //2
    private static void Anchop(RPGView rpg, Monster attacker, Monster defender) {
        int sp = 1;
        if (attacker.SP - sp >= 0) {   //SPが足りたら
            //スキル消費
            attacker.SP -= sp;
            //エフェクト
            for (int i = 0; i < 20; i++) {
                rpg.drawBattle(attacker.NAME + "のアンチョップ" , i % 2 == 0);
                rpg.sleep(50);
            }
            //効果
            int damage = 20;
            rpg.drawBattle(defender.NAME + "に" + damage + "ダメージ!");
            rpg.waitSelect();
            defender.HP -= damage;
            if (defender.HP <= 0) defender.HP = 0;

        } else {  //SPが足りない場合
            rpg.drawBattle("SPが足りない!");
            rpg.waitSelect();
        }
    }

    //3
    private static void Kick(RPGView rpg, Monster attacker, Monster defender) {
        int sp = 4;
        if (attacker.SP - sp >= 0) {   //SPが足りたら
            //スキル消費
            attacker.SP -= sp;
            //エフェクト
            for (int i = 0; i < 20; i++) {
                rpg.drawBattle(attacker.NAME + "のキック!", i % 2 == 0);
                rpg.sleep(50);
            }
            //効果
            int damage = 30;
            rpg.drawBattle(defender.NAME + "に" + damage + "ダメージ!");
            rpg.waitSelect();
            defender.HP -= damage;
            if (defender.HP <= 0) defender.HP = 0;

        } else {  //SPが足りない場合
            rpg.drawBattle("SPが足りない!");
            rpg.waitSelect();
        }
    }

    //4
    private static void Ankick(RPGView rpg, Monster attacker, Monster defender) {
        int sp = 4;
        if (attacker.SP - sp >= 0) {   //SPが足りたら
            //スキル消費
            attacker.SP -= sp;
            //エフェクト
            for (int i = 0; i < 20; i++) {
                rpg.drawBattle(attacker.NAME + "のアンキック!", i % 2 == 0);
                rpg.sleep(50);
            }
            //効果
            int damage = 40;
            rpg.drawBattle(defender.NAME + "に" + damage + "ダメージを与えた");
            rpg.waitSelect();
            defender.HP -= damage;
            if (defender.HP <= 0) defender.HP = 0;

        } else {  //SPが足りない場合
            rpg.drawBattle("SPが足りない!");
            rpg.waitSelect();
        }
    }
}
