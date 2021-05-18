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
	 * @param b 受けさせるゆっくり
	 * @return 現状は常にTrue
	 */
	public static boolean badgeTest( Body b )
	{
		if( b == null || b.isDead()|| b.isRemoved())
		{
			return false;
		}

		Attitude eAtt = b.getAttitude();
		Intelligence eInt = b.getIntelligence();
		boolean bIdiot = b.isIdiot();
		BodyRank eBodyRank = b.getBodyRank();
		
		Badge.BadgeRank eBadgeRank = Badge.BadgeRank.FAKE;
		// 飼いゆ以外,足りないゆは偽バッチのみ
		if( eBodyRank != BodyRank.KAIYU || bIdiot )
		{
			eBadgeRank = Badge.BadgeRank.FAKE;
		}
		else{
			switch(eAtt)
			{
				case VERY_NICE:
					switch(eInt)
					{
						case WISE:
							eBadgeRank = Badge.BadgeRank.GOLD;
							break;
						case AVERAGE:
							eBadgeRank = Badge.BadgeRank.SILVER;
							break;
						case FOOL:
							eBadgeRank = Badge.BadgeRank.BRONZE;
							break;
						default:
							break;
					}				
					break;
				case NICE:
					switch(eInt)
					{
						case WISE:
							eBadgeRank = Badge.BadgeRank.GOLD;
							break;
						case AVERAGE:
							eBadgeRank = Badge.BadgeRank.SILVER;
							break;
						case FOOL:
							eBadgeRank = Badge.BadgeRank.BRONZE;
							break;
						default:
							break;
					}			
					break;
				case AVERAGE:
					switch(eInt)
					{
						case WISE:
							eBadgeRank = Badge.BadgeRank.SILVER;
							break;
						case AVERAGE:
							eBadgeRank = Badge.BadgeRank.BRONZE;
							break;
						case FOOL:
							eBadgeRank = Badge.BadgeRank.BRONZE;
							break;
						default:
							break;
					}			
					break;
				case SHITHEAD:
					switch(eInt)
					{
						case WISE:
							eBadgeRank = Badge.BadgeRank.BRONZE;
							break;
						case AVERAGE:
							eBadgeRank = Badge.BadgeRank.BRONZE;
							break;
						case FOOL:
							eBadgeRank = Badge.BadgeRank.FAKE;
							break;
						default:
							break;
					}			
					break;
				case SUPER_SHITHEAD:
					switch(eInt)
					{
						case WISE:
							eBadgeRank = Badge.BadgeRank.BRONZE;
							break;
						case AVERAGE:
							eBadgeRank = Badge.BadgeRank.FAKE;
							break;
						case FOOL:
							eBadgeRank = Badge.BadgeRank.FAKE;
							break;
						default:
							break;
					}			
					break;
				default:
					break;
			}
		}
		
		if(b.getAttachmentSize(Badge.class) != 0 ){
			b.removeAttachment(Badge.class, true);
		}
		else{
			b.addAttachment(new Badge(b, eBadgeRank));
			b.getInVain(true);
		}
		
		return true;
	}
}

