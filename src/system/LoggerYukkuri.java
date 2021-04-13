package src.system;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
//import java.io.ByteArrayInputStream;
import java.io.IOException;
//import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
//import java.util.List;
import java.util.logging.FileHandler;
//import java.util.logging.Formatter;
import java.util.logging.Level;
//import java.util.logging.LogManager;
import java.util.logging.Logger;

import src.SimYukkuri;
import src.base.Body;
import src.draw.Terrarium;
import src.draw.Translate;
import src.yukkuri.Deibu;
import src.yukkuri.Kimeemaru;
import src.yukkuri.MarisaKotatsumuri;
import src.yukkuri.MarisaTsumuri;
import src.yukkuri.WasaReimu;

public class LoggerYukkuri {

	public static final int TICK = 1;
	public static final int NUM_OF_NORMAL = 0;
	public static final int NUM_OF_PREDATOR = 1;
	public static final int NUM_OF_RARE = 2;
	public static final int NUM_OF_TARINAI = 3;
	public static final int NUM_OF_HYBRID = 4;
	public static final int NUM_OF_BABY = 5;
	public static final int NUM_OF_CHILD = 6;
	public static final int NUM_OF_ADULT = 7;
	public static final int NUM_OF_SICK = 8;
	public static final int NUM_OF_SHIT = 9;
	public static final int NUM_OF_CASH = 10;
	public static final int NUM_OF_LOGDATA_TYPE = 11;

	public static boolean show = false;
	public static int clearLogTime = 0;

	private static final int NUM_OF_GRAPH_DATA = 120;
	private static int logPointer = 0;
	private static boolean overwrapped = false;
	private static final Object lock = new Object();

	private static long[] prevLogData = new long[NUM_OF_LOGDATA_TYPE];
	private static long[] logDataSum = new long[NUM_OF_LOGDATA_TYPE];
	private static long[][] logList = new long[NUM_OF_GRAPH_DATA][NUM_OF_LOGDATA_TYPE];

	private static Color backColor = new Color(0, 0, 0, 160);
	private static Color textColor1 = new Color(255, 255, 255, 255);
	private static Font textFontTitle = new Font("Dialog", Font.BOLD, 40);
	private static Font textFonttext = new Font("Dialog", Font.PLAIN, 20);

	private static int logPage = 0;
	public static final Logger logger = Logger.getLogger("SampleLogging");

	public static void outputLogFile(String str) {
		// this.getClass().getSimpleName()
		if (logger.getHandlers() == null || logger.getHandlers().length == 0) {
			try {
				FileHandler fileHandler = new FileHandler(
						"SampleLogging%u.%g.log", // 出力ログファイル名は"SampleLogging.log
						10000, // 最大バイト数
						10, // ログファイル数
						true); // 追加モード
				// テキスト形式で、CustomLogFormatterを設定
				fileHandler.setFormatter(new CustomLogFormatter()); // ログフォーマット設定
				// ログの出力先を追加
				logger.addHandler(fileHandler);
				// ログの出力レベルを設定（ここではすべて出力するように設定)
				logger.setLevel(Level.ALL);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		logger.info(str);
	}

	public static void setLogPage(int p) {
		logPage = p;
		if (logPage < 0)
			logPage = 4;
		if (logPage >= 4)
			logPage = 0;
	}

	public static void addLogPage(int p) {
		logPage += p;
		if (logPage < 0)
			logPage = 4;
		if (logPage >= 4)
			logPage = 0;
	}

	public static void run() {

		long logData[] = new long[NUM_OF_LOGDATA_TYPE];
		ArrayList<Body> bodyList = SimYukkuri.world.currentMap.body;

		try {
			for (Body b : bodyList) {
				if (!b.isDead()) {
					if (b.isPredatorType()) {
						// 捕食種
						logData[NUM_OF_PREDATOR]++;
					} else if (b.isHybrid()) {
						// ハイブリッド
						logData[NUM_OF_HYBRID]++;
					} else if (b.isIdiot()) {
						// 足りない
						logData[NUM_OF_TARINAI]++;
					} else if (b.getType() < 100 || b.getType() == Deibu.type
							|| b.getType() == MarisaTsumuri.type || b.getType() == MarisaKotatsumuri.type
							|| b.getType() == WasaReimu.type) {
						// 通常種
						logData[NUM_OF_NORMAL]++;
					} else if ((b.getType() >= 1000 && b.getType() < 2000) || b.getType() == Kimeemaru.type) {
						// 希少種
						logData[NUM_OF_RARE]++;
					}

					switch (b.getBodyAgeState()) {
					case BABY:
						logData[NUM_OF_BABY]++;
						break;
					case CHILD:
						logData[NUM_OF_CHILD]++;
						break;
					default:
					case ADULT:
						logData[NUM_OF_ADULT]++;
						break;
					}

					if (b.isSick()) {
						logData[NUM_OF_SICK]++;
					}
				}
			}
		} catch (Exception e) {
			// list変更等の際は握りつぶしてしまう
		}

		logData[NUM_OF_SHIT] = SimYukkuri.world.currentMap.shit.size();

		logData[NUM_OF_CASH] = SimYukkuri.world.getPlayer().getCash();

		synchronized (lock) {
			for (int i = 0; i < NUM_OF_LOGDATA_TYPE; i++) {
				logList[logPointer % NUM_OF_GRAPH_DATA][i] = logData[i];
				logDataSum[i] += logData[i] - prevLogData[i];
				prevLogData[i] = logData[i];
			}
			logPointer++;
			if (logPointer >= NUM_OF_GRAPH_DATA) {
				overwrapped = true;
			}
		}
	}

	//	public static int getNumOfLogData(){
	//		return logList.size();
	//	}

	public static long[] getLog(int logRecord) {
		synchronized (lock) {
			if ((logRecord < 0) || (logRecord >= NUM_OF_GRAPH_DATA)) {
				return null;
			}
			if (!overwrapped && (logPointer - NUM_OF_GRAPH_DATA + logRecord < 0)) {
				return null;
			}
			return logList[(logPointer + logRecord) % NUM_OF_GRAPH_DATA];
		}
	}

	public static long[] getNumOfObjSumLog() {
		return logDataSum;
	}

	public static long[] getNumOfObjNowLog() {
		return prevLogData;
	}

	public static void clearLog() {
		synchronized (lock) {
			logPointer = 0;
			overwrapped = false;
		}
		run();
	}

	public static void displayLog(Graphics2D g2) {
		g2.setColor(backColor);
		g2.fillRect(0, 0, Translate.canvasW, Translate.canvasH);

		// 空きメモリ表示
		g2.setColor(textColor1);
		g2.setFont(textFonttext);

		DecimalFormat f1 = new DecimalFormat("#,###KB");
		DecimalFormat f2 = new DecimalFormat("##.#");
		long free = Runtime.getRuntime().freeMemory() / 1024;
		long total = Runtime.getRuntime().totalMemory() / 1024;
		long max = Runtime.getRuntime().maxMemory() / 1024;
		long used = total - free;
		double ratio = (used * 100 / (double) total);
		String info = "メモリ使用量=" + f1.format(used) + " (" + f2.format(ratio) + "%)、" + "使用可能最大値=" + f1.format(max);
		g2.drawString(info, 20, 40);

		g2.setColor(textColor1);
		g2.setFont(textFontTitle);
		//		int numOfLogData = getNumOfLogData();
		//		int NUM_OF_GRAPH_DATA = 120;
		int GRAPH_WIDTH = 700;
		int GRAPH_HEIGHT = 200;
		int GRAPH_OFFSETX = 100;
		int GRAPH_OFFSETY = 300;
		int LEGEND_OFFSETX = 120;
		//		int LEGEND_OFFSETY = 140;

		long[] logData = new long[NUM_OF_LOGDATA_TYPE * NUM_OF_GRAPH_DATA];
		long[] numOfObjNowLog = getNumOfObjNowLog();
		long numOfMaxYukkuri = 0;
		long numOfMaxUnun = 0;
		long numOfMaxCash = 0;
		long numOfSumYukkuri = 0;
		long[] logDataTmp;

		for (int i = 0; i < NUM_OF_GRAPH_DATA; i++) { //グラフの最大値を求めるため描写する一定時間のデータ処理
			logDataTmp = getLog(i);

			if (logDataTmp != null) {
				numOfSumYukkuri = 0;
				for (int j = 0; j < NUM_OF_HYBRID + 1; j++) {
					numOfSumYukkuri += logDataTmp[j];
				}
				if (numOfMaxYukkuri < numOfSumYukkuri) {
					numOfMaxYukkuri = numOfSumYukkuri;
				}
				if (numOfMaxUnun < logDataTmp[NUM_OF_SHIT]) {
					numOfMaxUnun = logDataTmp[NUM_OF_SHIT];
				}

				if (numOfMaxCash < logDataTmp[NUM_OF_CASH]) {
					numOfMaxCash = logDataTmp[NUM_OF_CASH];
				}
				for (int j = 0; j < NUM_OF_LOGDATA_TYPE; j++) {
					logData[NUM_OF_LOGDATA_TYPE * i + j] = logDataTmp[j];
				}
			} else {
				for (int j = 0; j < NUM_OF_LOGDATA_TYPE; j++) {
					logData[NUM_OF_LOGDATA_TYPE * i + j] = 0;
				}
			}
		}
		int[] xp = new int[NUM_OF_GRAPH_DATA * 2];
		int[] yp = new int[NUM_OF_GRAPH_DATA * 2];
		for (int i = 0; i < NUM_OF_GRAPH_DATA; i++) {
			xp[i] = GRAPH_WIDTH * i / (NUM_OF_GRAPH_DATA - 1) + GRAPH_OFFSETX;
			xp[NUM_OF_GRAPH_DATA * 2 - i - 1] = GRAPH_WIDTH * i / (NUM_OF_GRAPH_DATA - 1) + GRAPH_OFFSETX;
			yp[i] = GRAPH_HEIGHT + GRAPH_OFFSETY;
			yp[NUM_OF_GRAPH_DATA + i] = GRAPH_HEIGHT + GRAPH_OFFSETY;
		}

		switch (logPage) {
		case 0:
			g2.drawString("現在のゆっくりの個体数(種族別)", 100, 100);
			g2.setFont(textFonttext);
			for (int i = 0; i < NUM_OF_HYBRID + 1; i++) {
				g2.setColor(Color.WHITE);
				g2.drawString(Long.toString(numOfObjNowLog[i]), LEGEND_OFFSETX + 130, 30 * i + 140);
				switch (i) {
				case NUM_OF_NORMAL:
					g2.drawString("通常種", LEGEND_OFFSETX, 30 * i + 140);
					g2.setColor(Color.LIGHT_GRAY);
					break;
				case NUM_OF_PREDATOR:
					g2.drawString("捕食種", LEGEND_OFFSETX, 30 * i + 140);
					g2.setColor(Color.RED);
					break;
				case NUM_OF_RARE:
					g2.drawString("希少種", LEGEND_OFFSETX, 30 * i + 140);
					g2.setColor(Color.YELLOW);
					break;
				case NUM_OF_TARINAI:
					g2.drawString("足りないゆ", LEGEND_OFFSETX, 30 * i + 140);
					g2.setColor(Color.ORANGE);
					break;
				case NUM_OF_HYBRID:
					g2.drawString("ハイブリッド", LEGEND_OFFSETX, 30 * i + 140);
					g2.setColor(Color.MAGENTA);
					break;
				}
				g2.fillRect(LEGEND_OFFSETX + 180, 30 * i + 130, 10, 10);
				for (int k = 0; k < NUM_OF_GRAPH_DATA; k++) {
					if (logData[NUM_OF_LOGDATA_TYPE * k + i] != 0) {
						yp[k] -= logData[NUM_OF_LOGDATA_TYPE * k + i] * GRAPH_HEIGHT / numOfMaxYukkuri;
					} else {
						yp[k] -= logData[NUM_OF_LOGDATA_TYPE * k + i] * GRAPH_HEIGHT / 1;
					}
				}
				g2.fillPolygon(xp, yp, NUM_OF_GRAPH_DATA * 2);
				for (int k = 0; k < NUM_OF_GRAPH_DATA; k++) {
					yp[NUM_OF_GRAPH_DATA * 2 - k - 1] = yp[k];
				}
			}
			g2.setColor(Color.WHITE);
			g2.drawRect(GRAPH_OFFSETX, GRAPH_OFFSETY, GRAPH_WIDTH, GRAPH_HEIGHT);
			g2.drawString(Long.toString(numOfMaxYukkuri),
					GRAPH_OFFSETX - String.valueOf(numOfMaxYukkuri).length() * 10 - 2, GRAPH_OFFSETY);
			g2.drawString(Long.toString(numOfMaxYukkuri / 2),
					GRAPH_OFFSETX - String.valueOf(numOfMaxYukkuri).length() * 10 - 2,
					GRAPH_OFFSETY + GRAPH_HEIGHT / 2);
			g2.drawString("0", GRAPH_OFFSETX - 12, GRAPH_OFFSETY + GRAPH_HEIGHT);
			break;

		case 1:
			g2.drawString("現在のゆっくりの個体数(年代別)", 100, 100);
			g2.setFont(textFonttext);
			for (int i = 0; i < 3; i++) {
				g2.setColor(Color.WHITE);
				g2.drawString(Long.toString(numOfObjNowLog[i + NUM_OF_BABY]), LEGEND_OFFSETX + 130, 30 * i + 140);
				switch (i) {
				case 0:
					g2.drawString("赤ゆ", LEGEND_OFFSETX, 30 * i + 140);
					g2.setColor(Color.ORANGE);
					break;
				case 1:
					g2.drawString("子ゆ", LEGEND_OFFSETX, 30 * i + 140);
					g2.setColor(Color.YELLOW);
					break;
				case 2:
					g2.drawString("成ゆ", LEGEND_OFFSETX, 30 * i + 140);
					g2.setColor(Color.GREEN);
					break;
				}
				g2.fillRect(LEGEND_OFFSETX + 180, 30 * i + 130, 10, 10);
				for (int k = 0; k < NUM_OF_GRAPH_DATA; k++) {
					if (logData[NUM_OF_LOGDATA_TYPE * k + i + NUM_OF_BABY] != 0) {
						yp[k] -= logData[NUM_OF_LOGDATA_TYPE * k + i + NUM_OF_BABY] * GRAPH_HEIGHT / numOfMaxYukkuri;
					} else {
						yp[k] -= logData[NUM_OF_LOGDATA_TYPE * k + i + NUM_OF_BABY] * GRAPH_HEIGHT / 1;
					}
				}
				g2.fillPolygon(xp, yp, NUM_OF_GRAPH_DATA * 2);

				for (int k = 0; k < NUM_OF_GRAPH_DATA; k++) {
					yp[NUM_OF_GRAPH_DATA * 2 - k - 1] = yp[k];
				}
			}
			g2.setColor(Color.WHITE);
			g2.drawRect(GRAPH_OFFSETX, GRAPH_OFFSETY, GRAPH_WIDTH, GRAPH_HEIGHT);
			g2.drawString(Long.toString(numOfMaxYukkuri),
					GRAPH_OFFSETX - String.valueOf(numOfMaxYukkuri).length() * 10 - 2, GRAPH_OFFSETY);
			g2.drawString(Long.toString(numOfMaxYukkuri / 2),
					GRAPH_OFFSETX - String.valueOf(numOfMaxYukkuri).length() * 10 - 2,
					GRAPH_OFFSETY + GRAPH_HEIGHT / 2);
			g2.drawString("0", GRAPH_OFFSETX - 12, GRAPH_OFFSETY + GRAPH_HEIGHT);
			break;

		case 2:
			g2.drawString("ゆかび感染状況", 100, 100);
			g2.setFont(textFonttext);
			for (int i = 0; i < 2; i++) {
				g2.setColor(Color.WHITE);
				switch (i) {
				case 0:
					g2.drawString(Long.toString(numOfObjNowLog[NUM_OF_SICK]), LEGEND_OFFSETX + 130, 30 * i + 140);
					g2.drawString("発症/感染", LEGEND_OFFSETX, 30 * i + 140);
					g2.setColor(Color.GREEN);
					break;
				case 1:
					g2.drawString(Long.toString(numOfSumYukkuri - numOfObjNowLog[NUM_OF_SICK]), LEGEND_OFFSETX + 130,
							30 * i + 140);
					g2.drawString("未感染", LEGEND_OFFSETX, 30 * i + 140);
					g2.setColor(Color.LIGHT_GRAY);
					break;
				}
				g2.fillRect(LEGEND_OFFSETX + 180, 30 * i + 130, 10, 10);
				for (int k = 0; k < NUM_OF_GRAPH_DATA; k++) {
					if (numOfMaxYukkuri == 0) {
						if (i == 0) {
							yp[k] -= logData[NUM_OF_LOGDATA_TYPE * k + i + NUM_OF_SICK] * GRAPH_HEIGHT / 1;
						} else {
							yp[k] -= (logData[NUM_OF_LOGDATA_TYPE * k] + logData[NUM_OF_LOGDATA_TYPE * k + 1]
									+ logData[NUM_OF_LOGDATA_TYPE * k + 2] + logData[NUM_OF_LOGDATA_TYPE * k + 3]
									+ logData[NUM_OF_LOGDATA_TYPE * k + 4]
									- logData[NUM_OF_LOGDATA_TYPE * k + NUM_OF_SICK]) * GRAPH_HEIGHT / 1;
						}
					} else {
						if (i == 0) {
							yp[k] -= logData[NUM_OF_LOGDATA_TYPE * k + i + NUM_OF_SICK] * GRAPH_HEIGHT
									/ numOfMaxYukkuri;
						} else {
							yp[k] -= (logData[NUM_OF_LOGDATA_TYPE * k] + logData[NUM_OF_LOGDATA_TYPE * k + 1]
									+ logData[NUM_OF_LOGDATA_TYPE * k + 2] + logData[NUM_OF_LOGDATA_TYPE * k + 3]
									+ logData[NUM_OF_LOGDATA_TYPE * k + 4]
									- logData[NUM_OF_LOGDATA_TYPE * k + NUM_OF_SICK]) * GRAPH_HEIGHT / numOfMaxYukkuri;
						}
					}
				}
				g2.fillPolygon(xp, yp, NUM_OF_GRAPH_DATA * 2);
				for (int k = 0; k < NUM_OF_GRAPH_DATA; k++) {
					yp[NUM_OF_GRAPH_DATA * 2 - k - 1] = yp[k];
				}
			}
			g2.setColor(Color.WHITE);
			g2.drawRect(GRAPH_OFFSETX, GRAPH_OFFSETY, GRAPH_WIDTH, GRAPH_HEIGHT);
			g2.drawString(Long.toString(numOfMaxYukkuri),
					GRAPH_OFFSETX - String.valueOf(numOfMaxYukkuri).length() * 10 - 2, GRAPH_OFFSETY);
			g2.drawString(Long.toString(numOfMaxYukkuri / 2),
					GRAPH_OFFSETX - String.valueOf(numOfMaxYukkuri).length() * 10 - 2,
					GRAPH_OFFSETY + GRAPH_HEIGHT / 2);
			g2.drawString("0", GRAPH_OFFSETX - 12, GRAPH_OFFSETY + GRAPH_HEIGHT);
			break;

		case 3:
			g2.drawString("うんうんの数", 100, 100);
			g2.setFont(textFonttext);
			g2.drawString("うんうん", LEGEND_OFFSETX, 140);
			g2.drawString(Long.toString(numOfObjNowLog[NUM_OF_SHIT]), LEGEND_OFFSETX + 130, 140);
			g2.setColor(Color.GRAY);
			for (int k = 0; k < NUM_OF_GRAPH_DATA; k++) {
				if (logData[NUM_OF_LOGDATA_TYPE * k + NUM_OF_SHIT] != 0) {
					yp[k] -= logData[NUM_OF_LOGDATA_TYPE * k + NUM_OF_SHIT] * GRAPH_HEIGHT / numOfMaxUnun;
				} else {
					yp[k] -= logData[NUM_OF_LOGDATA_TYPE * k + NUM_OF_SHIT] * GRAPH_HEIGHT / 1;
				}
			}
			g2.fillRect(LEGEND_OFFSETX + 180, 130, 10, 10);
			g2.fillPolygon(xp, yp, NUM_OF_GRAPH_DATA * 2);
			for (int k = 0; k < NUM_OF_GRAPH_DATA; k++) {
				yp[NUM_OF_GRAPH_DATA * 2 - k - 1] = yp[k];
			}
			g2.setColor(Color.WHITE);
			g2.drawRect(GRAPH_OFFSETX, GRAPH_OFFSETY, GRAPH_WIDTH, GRAPH_HEIGHT);
			g2.drawString(Long.toString(numOfMaxUnun), GRAPH_OFFSETX - String.valueOf(numOfMaxUnun).length() * 10 - 2,
					GRAPH_OFFSETY);
			g2.drawString(Long.toString(numOfMaxUnun / 2),
					GRAPH_OFFSETX - String.valueOf(numOfMaxUnun).length() * 10 - 2, GRAPH_OFFSETY + GRAPH_HEIGHT / 2);
			g2.drawString("0", GRAPH_OFFSETX - 12, GRAPH_OFFSETY + GRAPH_HEIGHT);
			break;
		case 4:
			g2.drawString("収入･支出", 100, 100);
			g2.setFont(textFonttext);
			for (int i = 0; i < 1; i++) {
				g2.setColor(Color.WHITE);
				g2.drawString(Long.toString(numOfObjNowLog[i + NUM_OF_CASH]), LEGEND_OFFSETX + 130, 30 * i + 140);

				g2.drawString(
						Long.toString(numOfObjNowLog[NUM_OF_CASH] - logData[NUM_OF_LOGDATA_TYPE * 59 + NUM_OF_CASH]),
						LEGEND_OFFSETX + 130, 30 * (i + 1) + 140);
				g2.drawString("所持金", LEGEND_OFFSETX, 30 * i + 140);
				g2.drawString("収益(￥/分)", LEGEND_OFFSETX, 30 * (i + 1) + 140);
				g2.setColor(Color.YELLOW);
				if (i == 0) {
					for (int k = 0; k < NUM_OF_GRAPH_DATA; k++) {
						if (logData[NUM_OF_LOGDATA_TYPE * k + i + NUM_OF_CASH] > 0) {
							yp[k] -= logData[NUM_OF_LOGDATA_TYPE * k + i + NUM_OF_CASH] * GRAPH_HEIGHT / numOfMaxCash;
						} else if (logData[NUM_OF_LOGDATA_TYPE * k + i + NUM_OF_CASH] == 0) {
							yp[k] -= logData[NUM_OF_LOGDATA_TYPE * k + i + NUM_OF_CASH] * GRAPH_HEIGHT / 1;
						}
					}
					g2.fillPolygon(xp, yp, NUM_OF_GRAPH_DATA * 2);
				}

				for (int k = 0; k < NUM_OF_GRAPH_DATA; k++) {
					yp[NUM_OF_GRAPH_DATA * 2 - k - 1] = yp[k];
				}
				g2.setColor(Color.WHITE);
				g2.drawRect(GRAPH_OFFSETX, GRAPH_OFFSETY, GRAPH_WIDTH, GRAPH_HEIGHT);
				g2.drawString(Long.toString(numOfMaxCash),
						GRAPH_OFFSETX - String.valueOf(numOfMaxCash).length() * 10 - 2, GRAPH_OFFSETY);
				g2.drawString(Long.toString(numOfMaxCash / 2),
						GRAPH_OFFSETX - String.valueOf(numOfMaxCash).length() * 10 - 2,
						GRAPH_OFFSETY + GRAPH_HEIGHT / 2);
				g2.drawString("0", GRAPH_OFFSETX - 12, GRAPH_OFFSETY + GRAPH_HEIGHT);
			}
			break;
		default:
			break;
		}

		int operationTime = Terrarium.operationTime / 10;
		int opetmp = operationTime % NUM_OF_GRAPH_DATA;
		for (int i = 0; i < NUM_OF_GRAPH_DATA / 30 + 1; i++) {
			int graphx = GRAPH_WIDTH - (((GRAPH_WIDTH) * 30 * i) / NUM_OF_GRAPH_DATA)
					- ((opetmp % 30) * GRAPH_WIDTH / NUM_OF_GRAPH_DATA) + GRAPH_OFFSETX;
			int time = operationTime - 30 * i;
			if (GRAPH_WIDTH - (GRAPH_WIDTH * 30 * i / NUM_OF_GRAPH_DATA + opetmp) >= 0 && time >= 0) {
				g2.drawLine(graphx, GRAPH_OFFSETY, graphx, GRAPH_OFFSETY + GRAPH_HEIGHT);
				int offsetX = String.valueOf(((Integer.toString(time / 3600)) + ":" + (Integer.toString(time / 60 % 60))
						+ ":" + Integer.toString(time % 60 / 30 * 30))).length() * 5 - 4;
				g2.drawString(
						((Integer.toString(time / 3600)) + ":" + (Integer.toString(time / 60 % 60)) + ":"
								+ Integer.toString(time % 60 / 30 * 30)),
						graphx - offsetX, GRAPH_OFFSETY + GRAPH_HEIGHT + 18);
				int day = (operationTime + 60 - 30 * i) / 240;
				int hour = (time / 30 * 3 + 6) % 24;
				g2.drawString(("(" + (Integer.toString(day)) + "日目:" + (Integer.toString(hour)) + "時)"),
						graphx - offsetX, GRAPH_OFFSETY + GRAPH_HEIGHT + 36);
			}
		}
	}
}