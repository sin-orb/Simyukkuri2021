package org.simyukkuri.system;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Deibu;
import org.simyukkuri.entity.core.living.yukkuri.impl.Kimeemaru;
import org.simyukkuri.entity.core.living.yukkuri.impl.MarisaKotatsumuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.MarisaTsumuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.WasaReimu;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.util.GameText;
import org.simyukkuri.util.GameWorld;

/**
 * ゆっくりの個体数・年齢層・感染状況・フン数・所持金などを時系列で記録し、
 * グラフとして画面表示するロギングユーティリティ.
 * 最大 120 ティック分のデータをリングバッファで保持する.
 */
public class LoggerYukkuri {
	/** 処理の最小単位（ティック） */
	public static final int TICK = 1;
	/** ログデータ格納の順番 */
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
	/** ログを見せるか否か */
	private static boolean show = false;
	/** ログクリアをした時間 */
	private static int clearLogTime = 0;

	private static final int NUM_OF_GRAPH_DATA = 120;
	private static int logPointer = 0;
	private static boolean overwrapped = false;
	private static final Object lock = new Object();

	private static long[] prevLogData = new long[NUM_OF_LOGDATA_TYPE];
	private static long[] logDataSum = new long[NUM_OF_LOGDATA_TYPE];
	private static long[][] logList = new long[NUM_OF_GRAPH_DATA][NUM_OF_LOGDATA_TYPE];

	private static Color backColor;
	private static Color textColor1;
	private static Font textFontTitle;
	private static Font textFonttext;

	private static void initGraphics() {
		if (backColor == null) {
			backColor = new Color(0, 0, 0, 160);
			textColor1 = new Color(255, 255, 255, 255);
			textFontTitle = new Font("Dialog", Font.BOLD, 40);
			textFonttext = new Font("Dialog", Font.PLAIN, 20);
		}
	}

	private static int logPage = 0;
	/** ロガー */
	public static final Logger logger = Logger.getLogger("SampleLogging");

	/**
	 * ファイルにログを出力する.
	 * 
	 * @param str 出力する文字列
	 */
	public static void outputLogFile(String str) {
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

	/**
	 * ログページを指定する.
	 * 
	 * @param p ページ
	 */
	public static void setLogPage(int p) {
		logPage = p;
		if (logPage < 0) {
			logPage = 3;
		}
		if (logPage >= 4) {
			logPage = 0;
		}
	}

	/**
	 * ログページを相対値で移動する。0 未満や 4 以上になると循環する。
	 *
	 * @param p 移動量（正で次のページ、負で前のページ）
	 */
	public static void addLogPage(int p) {
		logPage += p;
		if (logPage < 0) {
			logPage = 3;
		} else if (logPage >= 4) {
			logPage = 0;
		}
	}

	/**
	 * ログ画面を表示するかどうかを返す。
	 *
	 * @return 表示中なら true
	 */
	public static boolean isShow() {
		return show;
	}

	/**
	 * ログ画面の表示状態をセットする。
	 *
	 * @param show true で表示、false で非表示
	 */
	public static void setShow(boolean show) {
		LoggerYukkuri.show = show;
	}

	/**
	 * 最後にログをクリアした時刻（ティック）を返す。
	 *
	 * @return ログクリア時刻
	 */
	public static int getClearLogTime() {
		return clearLogTime;
	}

	/**
	 * ログクリア時刻をセットする。
	 *
	 * @param clearLogTime ログをクリアしたティック時刻
	 */
	public static void setClearLogTime(int clearLogTime) {
		LoggerYukkuri.clearLogTime = clearLogTime;
	}

	/**
	 * ロガーを実行する.
	 */
	public static void run() {
		long[] logData = new long[NUM_OF_LOGDATA_TYPE];
		try {
			for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentWorldState().getYukkuriRegistry().entrySet()) {
				Yukkuri b = entry.getValue();
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
					} else if (b.getType().getTypeID() < 100 || b.getType() == Deibu.type
							|| b.getType() == MarisaTsumuri.type || b.getType() == MarisaKotatsumuri.type
							|| b.getType() == WasaReimu.type) {
						// 通常種
						logData[NUM_OF_NORMAL]++;
					} else if ((b.getType().getTypeID() >= 1000 && b.getType().getTypeID() < 2000)
							|| b.getType() == Kimeemaru.type) {
						// 希少種
						logData[NUM_OF_RARE]++;
					}

					switch (b.getAgeState()) {
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

		logData[NUM_OF_SHIT] = GameWorld.get().getCurrentWorldState().getShit().size();

		logData[NUM_OF_CASH] = GameWorld.get().getPlayer().getCash();

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

	/**
	 * ログを取得する.
	 * 
	 * @param logRecord ログレコード
	 * @return ログリスト
	 */
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

	/**
	 * ログデータ合計を取得する.
	 * 
	 * @return ログデータ合計
	 */
	public static long[] getNumOfObjSumLog() {
		return logDataSum;
	}

	/**
	 * 過去ログデータを取得する.
	 * 
	 * @return 過去ログデータ
	 */
	public static long[] getNumOfObjNowLog() {
		return prevLogData;
	}

	/**
	 * ログをクリアする.
	 */
	public static void clearLog() {
		synchronized (lock) {
			logPointer = 0;
			overwrapped = false;
		}
		run();
	}

	/**
	 * ログを表示する.
	 * 
	 * @param g2 Graphics2D
	 */
	public static void displayLog(Graphics2D g2) {
		if (java.awt.GraphicsEnvironment.isHeadless()) {
			return;
		}
		initGraphics();
		g2.setColor(backColor);
		g2.fillRect(0, 0, Translate.getCanvasW(), Translate.getCanvasH());

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
		String info = GameText.read("system_memoryusing") + f1.format(used)
				+ " (" + f2.format(ratio) + "%)、" + GameText.read("system_maxmemory") + f1.format(max);
		g2.drawString(info, 20, 40);

		g2.setColor(textColor1);
		g2.setFont(textFontTitle);
		int graphWidth = 700;
		int graphHeight = 200;
		int graphOffsetX = 100;
		int graphOffsetY = 300;
		int legendOffsetX = 120;

		long[] logData = new long[NUM_OF_LOGDATA_TYPE * NUM_OF_GRAPH_DATA];
		long[] numOfObjNowLog = getNumOfObjNowLog();
		long numOfMaxYukkuri = 0;
		long numOfMaxUnun = 0;
		long numOfMaxCash = 0;
		long numOfSumYukkuri = 0;
		long[] logDataTmp;

		for (int i = 0; i < NUM_OF_GRAPH_DATA; i++) { // グラフの最大値を求めるため描写する一定時間のデータ処理
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
			xp[i] = graphWidth * i / (NUM_OF_GRAPH_DATA - 1) + graphOffsetX;
			xp[NUM_OF_GRAPH_DATA * 2 - i - 1] = graphWidth * i / (NUM_OF_GRAPH_DATA - 1) + graphOffsetX;
			yp[i] = graphHeight + graphOffsetY;
			yp[NUM_OF_GRAPH_DATA + i] = graphHeight + graphOffsetY;
		}

		switch (logPage) {
			case 0:
				g2.drawString(GameText.read("system_logcurrent"), 100, 100);
				g2.setFont(textFonttext);
				for (int i = 0; i < NUM_OF_HYBRID + 1; i++) {
					g2.setColor(Color.WHITE);
					g2.drawString(Long.toString(numOfObjNowLog[i]), legendOffsetX + 130, 30 * i + 140);
					switch (i) {
						case NUM_OF_NORMAL:
							g2.drawString(GameText.read("draw_normalsp"), legendOffsetX,
									30 * i + 140);
							g2.setColor(Color.LIGHT_GRAY);
							break;
						case NUM_OF_PREDATOR:
							g2.drawString(GameText.read("draw_predsp"), legendOffsetX, 30 * i + 140);
							g2.setColor(Color.RED);
							break;
						case NUM_OF_RARE:
							g2.drawString(GameText.read("draw_raresp"), legendOffsetX, 30 * i + 140);
							g2.setColor(Color.YELLOW);
							break;
						case NUM_OF_TARINAI:
							g2.drawString(GameText.read("item_tarinai"), legendOffsetX,
									30 * i + 140);
							g2.setColor(Color.ORANGE);
							break;
						case NUM_OF_HYBRID:
							g2.drawString(GameText.read("enums_hybrid"), legendOffsetX,
									30 * i + 140);
							g2.setColor(Color.MAGENTA);
							break;
						default:
							break;
					}
					g2.fillRect(legendOffsetX + 180, 30 * i + 130, 10, 10);
					for (int k = 0; k < NUM_OF_GRAPH_DATA; k++) {
						if (logData[NUM_OF_LOGDATA_TYPE * k + i] != 0) {
							yp[k] -= logData[NUM_OF_LOGDATA_TYPE * k + i] * graphHeight / numOfMaxYukkuri;
						} else {
							yp[k] -= logData[NUM_OF_LOGDATA_TYPE * k + i] * graphHeight / 1;
						}
					}
					g2.fillPolygon(xp, yp, NUM_OF_GRAPH_DATA * 2);
					for (int k = 0; k < NUM_OF_GRAPH_DATA; k++) {
						yp[NUM_OF_GRAPH_DATA * 2 - k - 1] = yp[k];
					}
				}
				g2.setColor(Color.WHITE);
				g2.drawRect(graphOffsetX, graphOffsetY, graphWidth, graphHeight);
				g2.drawString(Long.toString(numOfMaxYukkuri),
						graphOffsetX - String.valueOf(numOfMaxYukkuri).length() * 10 - 2, graphOffsetY);
				g2.drawString(Long.toString(numOfMaxYukkuri / 2),
						graphOffsetX - String.valueOf(numOfMaxYukkuri).length() * 10 - 2,
						graphOffsetY + graphHeight / 2);
				g2.drawString("0", graphOffsetX - 12, graphOffsetY + graphHeight);
				break;

			case 1:
				g2.drawString(GameText.read("system_logkotaisuu"), 100, 100);
				g2.setFont(textFonttext);
				for (int i = 0; i < 3; i++) {
					g2.setColor(Color.WHITE);
					g2.drawString(Long.toString(numOfObjNowLog[i + NUM_OF_BABY]), legendOffsetX + 130, 30 * i + 140);
					switch (i) {
						case 0:
							g2.drawString(GameText.read("enums_babyyu"), legendOffsetX,
									30 * i + 140);
							g2.setColor(Color.ORANGE);
							break;
						case 1:
							g2.drawString(GameText.read("enums_childyu"), legendOffsetX,
									30 * i + 140);
							g2.setColor(Color.YELLOW);
							break;
						case 2:
							g2.drawString(GameText.read("enums_adultyu"), legendOffsetX,
									30 * i + 140);
							g2.setColor(Color.GREEN);
							break;
						default:
							break;
					}
					g2.fillRect(legendOffsetX + 180, 30 * i + 130, 10, 10);
					for (int k = 0; k < NUM_OF_GRAPH_DATA; k++) {
						if (logData[NUM_OF_LOGDATA_TYPE * k + i + NUM_OF_BABY] != 0) {
							yp[k] -= logData[NUM_OF_LOGDATA_TYPE * k + i + NUM_OF_BABY] * graphHeight
									/ numOfMaxYukkuri;
						} else {
							yp[k] -= logData[NUM_OF_LOGDATA_TYPE * k + i + NUM_OF_BABY] * graphHeight / 1;
						}
					}
					g2.fillPolygon(xp, yp, NUM_OF_GRAPH_DATA * 2);

					for (int k = 0; k < NUM_OF_GRAPH_DATA; k++) {
						yp[NUM_OF_GRAPH_DATA * 2 - k - 1] = yp[k];
					}
				}
				g2.setColor(Color.WHITE);
				g2.drawRect(graphOffsetX, graphOffsetY, graphWidth, graphHeight);
				g2.drawString(Long.toString(numOfMaxYukkuri),
						graphOffsetX - String.valueOf(numOfMaxYukkuri).length() * 10 - 2, graphOffsetY);
				g2.drawString(Long.toString(numOfMaxYukkuri / 2),
						graphOffsetX - String.valueOf(numOfMaxYukkuri).length() * 10 - 2,
						graphOffsetY + graphHeight / 2);
				g2.drawString("0", graphOffsetX - 12, graphOffsetY + graphHeight);
				break;

			case 2:
				g2.drawString(GameText.read("system_yukabi"), 100, 100);
				g2.setFont(textFonttext);
				for (int i = 0; i < 2; i++) {
					g2.setColor(Color.WHITE);
					switch (i) {
						case 0:
							g2.drawString(Long.toString(numOfObjNowLog[NUM_OF_SICK]), legendOffsetX + 130,
									30 * i + 140);
							g2.drawString(GameText.read("system_kansen"), legendOffsetX,
									30 * i + 140);
							g2.setColor(Color.GREEN);
							break;
						case 1:
							g2.drawString(Long.toString(numOfSumYukkuri - numOfObjNowLog[NUM_OF_SICK]),
									legendOffsetX + 130,
									30 * i + 140);
							g2.drawString(GameText.read("system_mikansen"), legendOffsetX,
									30 * i + 140);
							g2.setColor(Color.LIGHT_GRAY);
							break;
						default:
							break;
					}
					g2.fillRect(legendOffsetX + 180, 30 * i + 130, 10, 10);
					for (int k = 0; k < NUM_OF_GRAPH_DATA; k++) {
						long sickValue = logData[NUM_OF_LOGDATA_TYPE * k + i + NUM_OF_SICK];
						long healthyValue = logData[NUM_OF_LOGDATA_TYPE * k]
								+ logData[NUM_OF_LOGDATA_TYPE * k + 1]
								+ logData[NUM_OF_LOGDATA_TYPE * k + 2]
								+ logData[NUM_OF_LOGDATA_TYPE * k + 3]
								+ logData[NUM_OF_LOGDATA_TYPE * k + 4]
								- logData[NUM_OF_LOGDATA_TYPE * k + NUM_OF_SICK];
						if (numOfMaxYukkuri == 0) {
							if (i == 0) {
								yp[k] -= sickValue * graphHeight / 1;
							} else {
								yp[k] -= healthyValue * graphHeight / 1;
							}
						} else {
							if (i == 0) {
								yp[k] -= sickValue * graphHeight / numOfMaxYukkuri;
							} else {
								yp[k] -= healthyValue * graphHeight / numOfMaxYukkuri;
							}
						}
					}
					g2.fillPolygon(xp, yp, NUM_OF_GRAPH_DATA * 2);
					for (int k = 0; k < NUM_OF_GRAPH_DATA; k++) {
						yp[NUM_OF_GRAPH_DATA * 2 - k - 1] = yp[k];
					}
				}
				g2.setColor(Color.WHITE);
				g2.drawRect(graphOffsetX, graphOffsetY, graphWidth, graphHeight);
				g2.drawString(Long.toString(numOfMaxYukkuri),
						graphOffsetX - String.valueOf(numOfMaxYukkuri).length() * 10 - 2, graphOffsetY);
				g2.drawString(Long.toString(numOfMaxYukkuri / 2),
						graphOffsetX - String.valueOf(numOfMaxYukkuri).length() * 10 - 2,
						graphOffsetY + graphHeight / 2);
				g2.drawString("0", graphOffsetX - 12, graphOffsetY + graphHeight);
				break;

			case 3:
				g2.drawString(GameText.read("system_numberofunun"), 100, 100);
				g2.setFont(textFonttext);
				g2.drawString(GameText.read("command_clean_shit"), legendOffsetX, 140);
				g2.drawString(Long.toString(numOfObjNowLog[NUM_OF_SHIT]), legendOffsetX + 130, 140);
				g2.setColor(Color.GRAY);
				for (int k = 0; k < NUM_OF_GRAPH_DATA; k++) {
					if (logData[NUM_OF_LOGDATA_TYPE * k + NUM_OF_SHIT] != 0) {
						yp[k] -= logData[NUM_OF_LOGDATA_TYPE * k + NUM_OF_SHIT] * graphHeight / numOfMaxUnun;
					} else {
						yp[k] -= logData[NUM_OF_LOGDATA_TYPE * k + NUM_OF_SHIT] * graphHeight / 1;
					}
				}
				g2.fillRect(legendOffsetX + 180, 130, 10, 10);
				g2.fillPolygon(xp, yp, NUM_OF_GRAPH_DATA * 2);
				for (int k = 0; k < NUM_OF_GRAPH_DATA; k++) {
					yp[NUM_OF_GRAPH_DATA * 2 - k - 1] = yp[k];
				}
				g2.setColor(Color.WHITE);
				g2.drawRect(graphOffsetX, graphOffsetY, graphWidth, graphHeight);
				g2.drawString(Long.toString(numOfMaxUnun),
						graphOffsetX - String.valueOf(numOfMaxUnun).length() * 10 - 2,
						graphOffsetY);
				g2.drawString(Long.toString(numOfMaxUnun / 2),
						graphOffsetX - String.valueOf(numOfMaxUnun).length() * 10 - 2,
						graphOffsetY + graphHeight / 2);
				g2.drawString("0", graphOffsetX - 12, graphOffsetY + graphHeight);
				break;
			default:
				break;
		}

		int operationTime = GameEnvironment.getOperationTime() / 10;
		int opetmp = operationTime % NUM_OF_GRAPH_DATA;
		for (int i = 0; i < NUM_OF_GRAPH_DATA / 30 + 1; i++) {
			int graphx = graphWidth - (((graphWidth) * 30 * i) / NUM_OF_GRAPH_DATA)
					- ((opetmp % 30) * graphWidth / NUM_OF_GRAPH_DATA) + graphOffsetX;
			int time = operationTime - 30 * i;
			if (graphWidth - (graphWidth * 30 * i / NUM_OF_GRAPH_DATA + opetmp) >= 0 && time >= 0) {
				g2.drawLine(graphx, graphOffsetY, graphx, graphOffsetY + graphHeight);
				int offsetX = String.valueOf(((Integer.toString(time / 3600)) + ":" + (Integer.toString(time / 60 % 60))
						+ ":" + Integer.toString(time % 60 / 30 * 30))).length() * 5 - 4;
				g2.drawString(
						((Integer.toString(time / 3600)) + ":" + (Integer.toString(time / 60 % 60)) + ":"
								+ Integer.toString(time % 60 / 30 * 30)),
						graphx - offsetX, graphOffsetY + graphHeight + 18);
				int day = (operationTime + 60 - 30 * i) / 240;
				int hour = (time / 30 * 3 + 6) % 24;
				g2.drawString("(" + Integer.toString(day) + GameText.read("system_nichime")
						+ Integer.toString(hour) + GameText.read("system_time"),
						graphx - offsetX, graphOffsetY + graphHeight + 36);
			}
		}
	}
}
