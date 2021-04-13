package src.system;

import java.util.HashMap;

/*****************************************************
	1アクションのメッセージ格納
*/

public class MessageMap
{
	// 現在有効なサブタグ
	public enum Tag {
		normal,
		rude,
		baby,
		child,
		adult,
		damage,
		footbake,
		pants,
		loveplayer,
		dislikeplayer,
		ununSlave,
		fool,
		wise,
	}
	public boolean normalFlag = false;
	public boolean rudeFlag = false;
	public boolean[] normalTag = null;
	public boolean[] rudeTag = null;
	public HashMap<String, String[]> map = null;

	public MessageMap()
	{
		rudeFlag = false;
		normalTag = new boolean[Tag.values().length];
		rudeTag = new boolean[Tag.values().length];
		map = new HashMap<String, String[]>();
	}
}

