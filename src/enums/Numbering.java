package src.enums;



/**************************************************
	採番クラス
*/
public enum Numbering {
    INSTANCE;
    // ゆっくりの固体識別ID
    private int yukkuriID = 0;
    /**
     * ゆっくりのユニークIDを採番する.
     * @return ユニークID
     */
    public synchronized int numberingYukkuriID() {
    	yukkuriID++;
    	return yukkuriID;
    }
    /**
     * ユニークIDを取得する.
     * @return ユニークID
     */
    public int getYukkuriID() {
    	return yukkuriID;
    }
    /**
     * ユニークIDを設定する.
     * @param id ユニークID
     */
    public void setYukkuriID(int id) {
    	yukkuriID = id;
    }
}

