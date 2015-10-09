package Monster;


public class Monster {
    //全モンスターの数
    public static final int MONSTERNUM = 3;

    //モンスターナンバー
    public int MONSTERNUMBER;

    //モンスターの名前
    public String NAME;

    //レベルごとの能力
    public int
        MAXHP[],
        MAXSP[],
        ATTACK[],
        DEFENCE[],
        SPEED[],
        EXP[],   //次のレベルまでに必要な経験値
        DROPEXP[],   //倒したときに獲得する経験値
        DROPMAONEY[], //敵として戦ったときの報酬
        ESCAPEPERCENT;


    //覚えるスキル
    public int SKILL[];
    public int LVFORSKILL[];

    //動的なステータス
    public int
        LV,
        HP,
        SP,
        GETEXP; //今獲得している経験値

    public static Monster MonsterOutput(int monsternumber, int level) {
        switch (monsternumber) {
            case 1:
                Mash mash = new Mash(monsternumber, level);
                return mash;
            case 2:
                Togemash togemash = new Togemash(monsternumber, level);
                return togemash;
            case 3:
                Darkmash darkmash = new Darkmash(monsternumber, level);
                return darkmash;
            default:
        }
        return null;
    }
}


