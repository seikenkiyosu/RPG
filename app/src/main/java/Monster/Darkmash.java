package Monster;

/**
 * Created by seikenkiyosu on 15/10/08.
 */
public class Darkmash extends Monster {
    private final int MAXLV = 5;
    private String name = "ダークマッシュ";
    private int
            maxHP[]    = {0,30,35,40,45,50},
            maxSP[]    = {0,10,14,18,22,26},
            attack[]   = {0,10,14,18,22,26},
            defence[]  = {0,10,14,18,22,26},
            speed[]    = {0,10,14,18,22,26},
            exp[]      = {0,4,8,12,16,20},
            dropexp[]  = {0,4,8,12,16,20},
            dropmoney[]  = {0,100,200,300,400,500};
    private int
            skill[]      = {1,3},
            lvforskill[] = {1,3};

    public Darkmash(int monsternumber, int level) {
        MONSTERNUMBER = monsternumber;
        NAME = name;
        LV = level;

        MAXHP = new int[MAXLV+1];
        MAXSP = new int[MAXLV+1];
        ATTACK = new int[MAXLV+1];
        DEFENCE = new int[MAXLV+1];
        SPEED = new int[MAXLV+1];
        EXP = new int[MAXLV+1];
        DROPEXP = new int[MAXLV+1];
        DROPMAONEY = new int[MAXLV+1];
        SKILL = new int[MAXLV+1];
        LVFORSKILL = new int[MAXLV+1];
        for (int i = 0; i <= MAXLV; i++) {
            MAXHP[i] = maxHP[i];
            MAXSP[i] = maxSP[i];
            ATTACK[i] = attack[i];
            DEFENCE[i] = defence[i];
            SPEED[i] = speed[i];
            EXP[i] = exp[i];
            DROPEXP[i] = dropexp[i];
            DROPMAONEY[i] = dropmoney[i];
        }
        for (int i = 0; i < skill.length; i++) {
            SKILL[i] = skill[i];
            LVFORSKILL[i] = lvforskill[i];
        }

        HP = MAXHP[LV];
        SP = MAXSP[LV];
    }
}
