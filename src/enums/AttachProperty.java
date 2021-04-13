package src.enums;
/** ゆっくりの体につくアタッチメントのプロパティ */
public enum AttachProperty {
	/** 赤ゆ用画像サイズ 原画をこの値で割る */BABY_SIZE, 
	/** 子ゆ用画像サイズ  原画をこの値で割る*/CHILD_SIZE,
	/** 成ゆ用画像サイズ 原画をこの値で割る */ADULT_SIZE,
	/** 親オブジェクトの位置基準 0:顔、お飾り向けの元サイズ 1:妊娠などの膨らみも含むサイズ */OFS_ORIGIN,
	/** アニメ速度 */ANIME_INTERVAL,	
	/** アニメループ回数 */ANIME_LOOP,	
	/** アニメ画像枚数 */ANIME_FRAMES, 
}
