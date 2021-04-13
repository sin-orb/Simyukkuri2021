package src.system;

import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;

public class Cash {

	public static void addCash(int val) {
		SimYukkuri.world.getPlayer().addCash(val);
	}

	public static long buyItem(Obj item) {
		addCash(-item.getValue());
		return item.getValue();
	}

	public static long buyYukkuri(Body body) {
		int val = 0;
		val = body.getYcost();
		switch(body.getBodyAgeState()) {
			case BABY:
				val /= 3;
				break;
			case CHILD:
				val /= 2;
				break;
			case ADULT:
				val /= 1;
				break;
		}
		addCash(-val);
		return val;
	}

	public static long sellYukkuri(Body body) {
		int val = 0;
		//工業製品として出荷
		//加工品チェック
		if((body.isPealed() && body.isCrushed()) || body.isPacked()){
			val = body.getSellingPrice(1);
			// 年齢補正
			switch(body.getBodyAgeState()) {
				case BABY:
					val /= 27;
					break;
				case CHILD:
					val /= 8;
					break;
				case ADULT:
					break;
			}
			//ストレス度査定
			float G =body.getStress()/body.getStressLimit();
			if(G<=0)G=0;
			//else if(G<=2)G*=1;
			else if(G>2 && G<=20) G=(G/6)+(5/3);
			else G=5;
			val *=G;
			addCash(val);
			return val;
		}

		//飼いゆとして出荷
		// 無価値チェック
		if(body.isSick() || body.isDead() || body.isDamaged()
				|| body.getCriticalDamegeType() != null || body.isGotBurned()) {
			return 0;
		}
		// 基本価値
		val = body.getSellingPrice(0);
		// 年齢補正
		switch(body.getBodyAgeState()) {
			case BABY:
				val /= 2;
				break;
			case CHILD:
				//無補正
				break;
			case ADULT:
				val /= 3;
				break;
		}
		// 増減額
		if(body.hasDisorder()) val /= 4;
		if(!body.hasOkazari()) val /= 2;
		switch(body.getAttitude()) {
			case VERY_NICE:
				val *= 4;
				break;
			case NICE:
				val *= 2;
				break;
			case AVERAGE:
				//無補正
				break;
			case SHITHEAD:
				val /= 8;
				break;
			case SUPER_SHITHEAD:
				val /= 20;
				break;
		}
		switch(body.getIntelligence()) {
			case WISE:
				if(!body.isRude())val *= 3/2;
				break;
			case AVERAGE:
				//無補正
				break;
			case FOOL:
				val /= 10;
				break;
		}
		addCash(val);
		return val;
	}
}