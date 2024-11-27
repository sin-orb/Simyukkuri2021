package src.game;
import src.enums.Attitude;
import src.enums.Intelligence;
import src.yukkuri.Tarinai;



/****************************************
 *  ゆっくりの遺伝子情報
 */
public class Dna implements java.io.Serializable {

	private static final long serialVersionUID = -3273354546305188434L;
	/**
	 * 種別
	 * ゆっくりの種類とtypeの値 2021/04/10現在：
	 * 0000	まりさ
	 * 0001	れいむ
	 * 0002	ありす
	 * 0003	ぱちゅりー
	 * 0004	ちぇん
	 * 0005	みょん
	 * 1000	ゆるさなえ
	 * 1001	あや
	 * 1002	てんこ
	 * 1003	うどんげ
	 * 1004	めーりん
	 * 1005	すわこ
	 * 1006	ちるの
	 * 1007	えーき
	 * 1008	らん
	 * 1009	にとり
	 * 1010	ゆうか
	 * 1011	さくや
	 * 2000	たりないゆ
	 * 2001	わされいむ
	 * 2002	つむり
	 * 2003	きめぇまる
	 * 2004	こたつむり
	 * 2005	でいぶ
	 * 2006	ドスまりさ
	 * 2007	たりないれいむ
	 * 3000	れみりゃ
	 * 3001	ふらん
	 * 3002	ゆゆこ
	 * 10000	まりされいむ
	 * 10001	れいむまりさ
	 * 20000	ハイブリッド
	 * */
	private int type;	
	/**性格*/
	private Attitude attitude;
	/**知能*/
	private Intelligence intelligence;
	/**レイプでできた子か*/
	private boolean raperChild;
	/**父ゆ*/
	private int father;
	/**母ゆ*/
	private int mother;

	/**
	 * コンストラクタ
	 * <br>引数なしの場合、足りないゆ、知能、性格共に中庸のものが生成される
	 */
	public Dna() {
		type = Tarinai.type;
		attitude = Attitude.AVERAGE;
		intelligence = Intelligence.AVERAGE;
		raperChild = false;
	}
	/**
	 * コンストラクタ
	 * 
	 * @param t 種別
	 * @param att 性格
	 * @param intel 知能
	 * @param rape レイプでできた子か
	 */
	public Dna(int t, Attitude att, Intelligence intel, boolean rape) {
		type = t;
		attitude = att;
		intelligence = intel;
		raperChild = rape;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Attitude getAttitude() {
		return attitude;
	}
	public void setAttitude(Attitude attitude) {
		this.attitude = attitude;
	}
	public Intelligence getIntelligence() {
		return intelligence;
	}
	public void setIntelligence(Intelligence intelligence) {
		this.intelligence = intelligence;
	}
	public boolean isRaperChild() {
		return raperChild;
	}
	public void setRaperChild(boolean raperChild) {
		this.raperChild = raperChild;
	}
	public int getFather() {
		return father;
	}
	public void setFather(int father) {
		this.father = father;
	}
	public int getMother() {
		return mother;
	}
	public void setMother(int mother) {
		this.mother = mother;
	}
	
	
}