package src.enums;



/**************************************************
	採番クラス
*/
public enum Numbering {
    INSTANCE;
    // ゆっくりの固体識別ID
    private int yukkuriID = 0;

    public int numberingYukkuriID() {
    	yukkuriID++;
    	return yukkuriID;
    }
    public int getYukkuriID() {
    	return yukkuriID;
    }
    public void setYukkuriID(int id) {
    	yukkuriID = id;
    }
}

