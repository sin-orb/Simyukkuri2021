package src;

import java.awt.Color;

import src.enums.AgeState;
import src.system.BasicStrokeEX;

public class Const {
	/** 左向き */
	public static final int LEFT = 0;
	/** 右向き */
	public static final int RIGHT = 1;

	/** メ�?セージウィンドウ色 */
	public static final Color[][] WINDOW_COLOR = {
		//      輪郭�?                                           塗り                                              �?キス�?
		{new Color(0, 0, 0, 255), new Color(255, 255, 255, 200), new Color(0, 0, 0, 255)},
		{new Color(0, 0, 0, 255), new Color(200, 200, 255, 200), new Color(0, 0, 0, 255)},
		{new Color(0, 0, 255, 255), new Color(255, 255, 255, 200), new Color(0, 0, 0, 255)},
		{new Color(0, 0, 0, 255), new Color(255, 160, 160, 200), new Color(0, 0, 0, 255)},
		{new Color(255, 0, 128, 255), new Color(255, 255, 255, 200), new Color(0, 0, 0, 255)},
	};
	
	/** ねぎぃ?��時のウィンドウ色 */
	public static final Color[] NEGI_WINDOW_COLOR = {
			new Color(0, 0, 0, 255), new Color(255, 0, 0, 200), new Color(0, 0, 0, 255) 
	};
	
	/** 針刺した際�?�ダメージ */
	public static final int NEEDLE = 100;
	/** ハンマ�?�のダメージ */
	public static final int HAMMER = 100*24*2;
	/** メ�?セージの表示秒数 */
	public static final int HOLDMESSAGE = 20;		// 2sec
	/** 状態�?�保持限界時間 */
	public static final int STAYLIMIT = 20;		// 2sec
	/** �?んうん�?�残日数 */
	public static final int SHITSTAY = 100;
	// こ�?�段階ではマップ�?��?さが確定して�?な�?可能性がある�?�で仮の値
	/** Reference diagonal length (400x400) used for threshold checks. */
	public static final int DIAGONAL = (int)Math.sqrt(400.0 * 400.0 + 400.0 * 400.0);
	
	//以下画像�?�ロードで使用する値
	/** 赤�?/子ゆ/大人�?のボディのサイズ */
	public static final float[] BODY_SIZE = {0.25f, 0.5f, 1.0f};
	/** Stalk X-axis offset pattern for child positions. */
	public static final int STALK_OF_S_X[] = {0, 1, -1, 1, 0, -1, 0, 1};
	/** Stalk Y-axis offset pattern for child positions. */
	public static final int STALK_OF_S_Y[] = {0, 1, 0, -1, 1, -1, -1, 0};
	/** 体型の引き伸ばし限界　赤/�?/�? */
	public static final int EXT_FORCE_PULL_LIMIT[] = {20, 40, 80};
	/** 体型の押さえつけ限界　赤/�?/�? */
	public static final int EXT_FORCE_PUSH_LIMIT[] = {-10, -20, -40};
	/** 赤�?のINDEX(0) */
	public static final int BABY_INDEX = AgeState.BABY.ordinal();
	/** 子ゆのINDEX(1) */
	public static final int CHILD_INDEX = AgeState.CHILD.ordinal();
	/** 成ゆのINDEX(2) */
	public static final int ADULT_INDEX = AgeState.ADULT.ordinal();
	
	
	
	
	
	
	
	
	/** Message window border stroke per window color type. */
	public static final BasicStrokeEX[] WINDOW_STROKE = {
			new BasicStrokeEX(1.0f),
			new BasicStrokeEX(1.0f),
			new BasicStrokeEX(2.0f),
			new BasicStrokeEX(1.0f),
			new BasicStrokeEX(2.0f)
	};
}
