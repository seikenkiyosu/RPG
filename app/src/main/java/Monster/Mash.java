package Monster;

public class Mash extends Monster {
    private final int MAXLV = 5;
    private String name = "マッシュ";
    private int
            maxHP[]    = {0,10,14,18,22,26},
            maxSP[]    = {0,2,2,2,2,2},
            attack[]   = {0,5,7,9,11,13},
            defence[]  = {0,2,4,6,8,10},
            speed[]    = {0,2,4,6,8,10},
            exp[]      = {0,3,4,5,6,7},
            dropexp[]  = {0,1,2,3,4,5},
            dropmoney[]  = {0,1,2,3,4,5};
    private int
            skill[]      = {0},
            lvforskill[] = {0};

    public Mash(int monsternumber, int level) {
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
