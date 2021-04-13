package src.logic;

import src.attachment.Badge;
import src.base.Body;
import src.enums.Attitude;
import src.enums.BodyRank;
import src.enums.Intelligence;



/***************************************************
	バッジ関連
 */
public class BadgeLogic {

//	private static Random rnd = new Random();
	
	public static boolean badgeTest( Body b )
	{
		if( b == null || b.isDead()|| b.isRemoved())
		{
			return false;
		}

//		public static enum Attitude { VERY_NICE, NICE, AVERAGE, SHITHEAD, SUPER_SHITHEAD };
//		public static enum Intelligence { WISE, AVERAGE, FOOL };		

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
//	
//    public static void setupBadgeTest(int badgeType)
//    {
//        int testScore = 0;
//        int passingScore = 0;
//        int isRude = 0;
//
//        switch(badgeType)
//        {
//        case 0: // '\0'
//            clearActions();
//            if(intelligence == ConstantValues.Intelligence.WISE)
//            {
//                String tooSmart = (new StringBuilder(String.valueOf(getNameE()))).append(" doesn't want a fake mister badge like that!").toString();
//                String tooSmartRude = (new StringBuilder(String.valueOf(getNameE()))).append(" doesn't want a fake mister badge like that! Go die easy!").toString();
//                if(isRude())
//                    setMessage(tooSmartRude, 30, true, true);
//                else
//                    setMessage(tooSmart, 30, true, true);
//                setAngry();
//                stay(30);
//            } else
//            {
//                passingScore = 0;
//                String readyFakeTest;
//                if(isRude())
//                {
//                    readyFakeTest = (new StringBuilder(String.valueOf(getNameE()))).append(" deserves a badge so that shitty slave has to give ").append(getNameE()).append(" anything ").append(getNameE()).append(" wants!").toString();
//                    isRude = 1;
//                } else
//                {
//                    readyFakeTest = (new StringBuilder(String.valueOf(getNameE()))).append(" is ready to become a badged yukkuri and take it even easier!").toString();
//                    isRude = 0;
//                }
//                setMessage(readyFakeTest, 60, false, true);
//                stay(210);
//                testBadge = 1;
//                isBusy = true;
//            }
//            break;
//        }
//    }
//
//    public static void runBadgeTest(int localSwitch)
//    {
//        String demoStretchAdult = "Demonstrating Stretch~Stretch~!";
//        String demoStretchBaby = "Demonshtwating Stwetch~Stwetch~ eajy!";
//        switch(localSwitch)
//        {
//        case 1: // '\001'
//            localCounter++;
//            stay(50);
//            if(localCounter > 60 && localStep == 0)
//            {
//                if(isAdult())
//                    setMessage(demoStretchAdult, 60, true, false);
//                else
//                    setMessage(demoStretchBaby, 60, true, true);
//                clearActions();
//                stay(60);
//                nobinobi = true;
//                stay(100);
//                localCounter = 0;
//                localStep++;
//            }
//            if(localCounter > 70 && localStep == 1)
//            {
//                localStep++;
//                clearActions();
//                localCounter = 0;
//                stay(50);
//                String successRude = (new StringBuilder("Mister badge is all ")).append(getNameE()).append("'s , so bring sweet-sweets old geezer!").toString();
//                String success = (new StringBuilder("Mister badge is all ")).append(getNameE()).append("'s! Take it easy!").toString();
//                if(isRude())
//                    setMessage(successRude, 30, true, false);
//                else
//                    setMessage(success, 30, true, true);
//                addAttachment(new FakeBadge(this));
//            }
//            if(localCounter > 40 && localStep == 2)
//            {
//                localStep = 0;
//                testBadge = 0;
//                isBusy = false;
//            }
//            break;
//        }
//    }
		
}

