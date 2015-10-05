package Monster;

public class Skill {
    private int skillnumber;    //スキルナンバー
    private String name;    //スキルの名前

    public Skill(int num) {
        skillnumber = num;
        switch (num) {
            case 1: name = "アンパンチ"; break;
            case 2: name = "アンチョップ"; break;
            case 3: name = "アンドライブ"; break;
            case 4: name = "アンキック"; break;
        }
    }
    public void cast() {
        switch (skillnumber) {
            case 1: Anpanch(); break;
            case 2: Anchop(); break;
            case 3: Andrive(); break;
            case 4: Ankick(); break;
        }
    }
    private int Anpanch() {
        int damage = 20;

        return damage;
    }
    private int Anchop() {
        int damage = 30;

        return damage;
    }
    private int Andrive() {
        int damage = 40;

        return damage;
    }
    private int Ankick() {
        int damage = 50;

        return damage;
    }
}
