package src.logic;

import src.attachment.Badge;
import src.base.Body;
import src.enums.Attitude;
import src.enums.BodyRank;
import src.enums.Intelligence;



/***************************************************
 * バッジ関連
 */
public class BadgeLogic {

	/**
	 * バッジ試験を受けさせる（自動的）
	 * @param body 受けさせるゆっくり
	 * @return 現状は常にTrue
	 */
	public static boolean badgeTest( Body body )
	{
		if( body == null || body.isDead()|| body.isRemoved())
		{
			return false;
		}

		Attitude attitude = body.getAttitude();
		Intelligence intelligence = body.getIntelligence();
		boolean isIdiot = body.isIdiot();
		BodyRank bodyRank = body.getBodyRank();
		
		Badge.BadgeRank badgeRank = Badge.BadgeRank.FAKE;
		// 飼いゆ以外,足りないゆは偽バッチのみ
		if( bodyRank != BodyRank.KAIYU || isIdiot )
		{
			badgeRank = Badge.BadgeRank.FAKE;
		}
		else{
			switch(attitude)
			{
				case VERY_NICE:
					switch(intelligence)
					{
						case WISE:
							badgeRank = Badge.BadgeRank.GOLD;
							break;
						case AVERAGE:
							badgeRank = Badge.BadgeRank.SILVER;
							break;
						case FOOL:
							badgeRank = Badge.BadgeRank.BRONZE;
							break;
						default:
							break;
					}				
					break;
				case NICE:
					switch(intelligence)
					{
						case WISE:
							badgeRank = Badge.BadgeRank.GOLD;
							break;
						case AVERAGE:
							badgeRank = Badge.BadgeRank.SILVER;
							break;
						case FOOL:
							badgeRank = Badge.BadgeRank.BRONZE;
							break;
						default:
							break;
					}			
					break;
				case AVERAGE:
					switch(intelligence)
					{
						case WISE:
							badgeRank = Badge.BadgeRank.SILVER;
							break;
						case AVERAGE:
							badgeRank = Badge.BadgeRank.BRONZE;
							break;
						case FOOL:
							badgeRank = Badge.BadgeRank.BRONZE;
							break;
						default:
							break;
					}			
					break;
				case SHITHEAD:
					switch(intelligence)
					{
						case WISE:
							badgeRank = Badge.BadgeRank.BRONZE;
							break;
						case AVERAGE:
							badgeRank = Badge.BadgeRank.BRONZE;
							break;
						case FOOL:
							badgeRank = Badge.BadgeRank.FAKE;
							break;
						default:
							break;
					}			
					break;
				case SUPER_SHITHEAD:
					switch(intelligence)
					{
						case WISE:
							badgeRank = Badge.BadgeRank.BRONZE;
							break;
						case AVERAGE:
							badgeRank = Badge.BadgeRank.FAKE;
							break;
						case FOOL:
							badgeRank = Badge.BadgeRank.FAKE;
							break;
						default:
							break;
					}			
					break;
				default:
					break;
			}
		}
		
		if(body.getAttachmentSize(Badge.class) != 0 ){
			body.removeAttachment(Badge.class);
		}
		else{
			body.addAttachment(new Badge(body, badgeRank));
			body.getInVain(true);
		}
		
		return true;
	}
}

