package src.enums;



/**************************************************
	採番クラス
*/
public enum Numbering {
    INSTANCE;
    // ゆっくりの固体識別ID
    private int yukkuriID = 0;
    // ObjのユニークID
    private int objId = 0;
    
    /**
     * ObjのユニークIDを採番する.
     * @return ObjのユニークID
     */
    public synchronized int numberingObjId() {
    	objId++;
    	return objId;
    }
    /**
     * ObjのユニークIDを取得する.
     * @return ObjのユニークID
     */
    public int getObjId() {
		return objId;
	}
    /**
     * ObjのユニークIDを設定する.
     * @param objId ObjのユニークID
     */
	public void setObjId(int objId) {
		this.objId = objId;
	}
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

