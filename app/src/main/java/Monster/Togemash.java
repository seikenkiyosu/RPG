package Monster;


public class Togemash extends Monster{
    private final int MAXLV = 5;
    private final String name = "トゲマッシュ";
    private int
            maxHP[]    = {0,20,24,28,32,36},
            maxSP[]    = {0,6,9,12,15,18},
            attack[]   = {0,6,9,12,15,18},
            defence[]  = {0,6,9,12,15,18},
            speed[]    = {0,6,9,12,15,18},
            exp[]      = {0,2,5,8,12,15},
            dropexp[]  = {0,2,5,8,12,15},
            dropmoney[]  = {0,50,100,150,200,250};
    private int
            skill[]      = {3},
            lvforskill[] = {0};

    public Togemash(int monsternumber, int level) {
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
