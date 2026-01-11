package src.system;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;

import src.SimYukkuri;
import src.base.Body;
import src.command.GadgetMenu;
import src.enums.BodyRank;
import src.enums.PublicRank;
import src.system.MainCommandListener.ButtonListener;
import src.system.MainCommandListener.GameSpeedComboBoxListener;
import src.system.MainCommandListener.MainItemComboBoxListener;
import src.system.MainCommandListener.OptionMenuListener;
import src.system.MainCommandListener.OptionPopupListener;
import src.system.MainCommandListener.SubItemComboBoxListener;

/**
 * メインコマンドUI（右ペイン）
 */
public class MainCommandUI {

	/** システムボタンテキスト */
	static enum SystemButtonLabel {
		ADDBODY(ResourceUtil.getInstance().read("system_addyukkuri")),
		SAVE(ResourceUtil.getInstance().read("save")),
		LOAD(ResourceUtil.getInstance().read("load")),
		PREV("<<"),
		LOG(ResourceUtil.getInstance().read("log")),
		NEXT(">>"),
		LOGCLEAR(ResourceUtil.getInstance().read("logclear")),
		;
        private final String label;
        SystemButtonLabel(String str) { this.label = str; }

        public String getLabel() {
        	return label;
        }
	}

	/** ツールボタン */
	static enum ToolButtonLabel {
		MOVE(ResourceUtil.getInstance().read("system_move")),
		BAG(ResourceUtil.getInstance().read("system_belongings")),
		;
        private final String label;
        ToolButtonLabel(String str) { this.label = str; }

        public String getLabel() {
        	return label;
        }
	}

	/** ステータスのラベル */
	static enum StatusLabel {
		MONEY(ResourceUtil.getInstance().read("system_money")),
		LABEL(ResourceUtil.getInstance().read("system_statusofyukkuri")),
		NAME(""),
		RANK(ResourceUtil.getInstance().read("system_rank")),
		PERSONALITY(ResourceUtil.getInstance().read("system_attitude")),
		INTEL(ResourceUtil.getInstance().read("system_intelligence")),
		DAMAGE(ResourceUtil.getInstance().read("system_damage")),
		STRESS(ResourceUtil.getInstance().read("system_stress")),
		HUNGER(ResourceUtil.getInstance().read("system_satis")),
		TANG(ResourceUtil.getInstance().read("system_taste")),
		SHIT(ResourceUtil.getInstance().read("system_unun")),
		LOVEPLAYER(ResourceUtil.getInstance().read("system_familiality")),
		;
        private final String label;
        StatusLabel(String str) { this.label = str; }

        public String getLabel() {
        	return label;
        }
	}

	/** オプションポップアップ */
	static enum OptionPopup {
		INI_RELOAD(ResourceUtil.getInstance().read("system_inireload"))
		;
		private final String label;
		OptionPopup(String str) { this.label = str; }

		public String getLabel() {
			return label;
		}
	}


	private static final String[] ATTITUDE_LEVEL_J = { "超善良", "善良", "普通", "ゲス", "ドゲス"};
	private static final String[] ATTITUDE_LEVEL_E = { "Very Nice", "Nice", "Normal", "Shithead", "Very Shithead"};
	private static final String[] INTEL_LEVEL_J = { "バッジ級", "普通", "餡子脳"};
	private static final String[] INTEL_LEVEL_E = { "Badge Class", "Normal", "Fool"};
	private static final String[] TANG_LEVEL_J = { "バカ舌", "普通", "肥えてる"};
	private static final String[] TANG_LEVEL_E = { "Paralyzed", "Normal", "Destroyed"};
	/** ゲームスピード */
	private static int selectedGameSpeed = 1;
	/** ズームスケール */
	private static int selectedZoomScale = 0;
	/** ゲームスピードコンボボックス */
	@SuppressWarnings("rawtypes")
	private static JComboBox gameSpeedCombo;
	/** メインアイテムコンボボックス */
	@SuppressWarnings("rawtypes")
	private static JComboBox mainItemCombo;
	/** サブアイテムコンボボックス */
	@SuppressWarnings("rawtypes")
	private static JComboBox subItemCombo;
	/** ゆっくりステータスラベル */
	private static JLabel[] yuStatusLabel = new JLabel[StatusLabel.values().length];
	/** ステータスアイコンラベル */
	private static JLabel[] statIconLabel = new JLabel[8];
	/** アイテムアイコンラベル */
	private static JLabel[] itemIconLabel = new JLabel[1];
	/** システムボタン */
	private static JButton[] systemButton = new JButton[SystemButtonLabel.values().length];
	/** その他ボタン */
	private static JToggleButton scriptButton, targetButton, pinButton, helpButton, optionButton;
	/** プレイヤーボタン */
	private static JToggleButton[] playerButton = new JToggleButton[ToolButtonLabel.values().length];
	/** オプションポップアップ */
	private static JPopupMenu optionPopup = new JPopupMenu();
	/** マップウィンドウ */
	private static MapWindow mapWindow;
	/** アイテムウィンドウ */
	private static ItemWindow itemWindow;

	/** メニューエリアの幅 */
	public static final int MENU_PANE_X = 124;
	/**
	 * インターフェイス作成
	 * @param windowHeight ウィンドウの高さ
	 * @return パネルインスタンス
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static JPanel createInterface(int windowHeight) {

		JPanel retpanel = new JPanel();
		retpanel.setLayout(new GridLayout(0, 1, 0, 0));
		retpanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		retpanel.setPreferredSize(new Dimension(MENU_PANE_X, windowHeight));
		retpanel.setMinimumSize(new Dimension(MENU_PANE_X, windowHeight));

		MainCommandListener listenerPack = new MainCommandListener();

		// コンボボックス
		GameSpeedComboBoxListener gsl = listenerPack.new GameSpeedComboBoxListener();
		gameSpeedCombo = new JComboBox();
		gameSpeedCombo.setFocusable(false);
		gameSpeedCombo.addItemListener(gsl);
		gameSpeedCombo.setModel(new DefaultComboBoxModel(GadgetMenu.GameSpeed.values()));
		gameSpeedCombo.setSelectedIndex(1);
		retpanel.add(gameSpeedCombo);
		// 選択コマンド
		MainItemComboBoxListener mil = listenerPack.new MainItemComboBoxListener();
		mainItemCombo = new JComboBox();
		mainItemCombo.setFocusable(false);
		mainItemCombo.addItemListener(mil);
		mainItemCombo.setModel(GadgetMenu.getMainModel());
		retpanel.add(mainItemCombo);

		SubItemComboBoxListener sil = listenerPack.new SubItemComboBoxListener();
		subItemCombo = new JComboBox();
		subItemCombo.setFocusable(false);
		subItemCombo.addItemListener(sil);
		subItemCombo.setModel(GadgetMenu.getToolModel());
		retpanel.add(subItemCombo);

		ButtonListener buttonListener = listenerPack.new ButtonListener();
		int butID;

		// ゆっくり追加ボタン
		butID = SystemButtonLabel.ADDBODY.ordinal();
		systemButton[butID] = new JButton(SystemButtonLabel.ADDBODY.getLabel());
		systemButton[butID].addActionListener(buttonListener);
		systemButton[butID].setFocusable(false);
		retpanel.add(systemButton[butID]);

		// セーブ・ロード
		JPanel saveLoad = new JPanel();
		butID = SystemButtonLabel.SAVE.ordinal();
		saveLoad.setLayout(new GridLayout(1, 2, 0, 0));
		saveLoad.setBorder(new EmptyBorder(0, 0, 0, 0));
		systemButton[butID] = new JButton(SystemButtonLabel.SAVE.getLabel());
		systemButton[butID].addActionListener(buttonListener);
		systemButton[butID].setMargin(new Insets(0, 0, 0, 0));
		systemButton[butID].setFocusable(false);
		saveLoad.add(systemButton[butID]);

		butID = SystemButtonLabel.LOAD.ordinal();
		systemButton[butID] = new JButton(SystemButtonLabel.LOAD.getLabel());
		systemButton[butID].addActionListener(buttonListener);
		systemButton[butID].setMargin(new Insets(0, 0, 0, 0));
		systemButton[butID].setFocusable(false);
		saveLoad.add(systemButton[butID]);
		retpanel.add(saveLoad);

		// ログ
		JPanel log = new JPanel();
		butID = SystemButtonLabel.PREV.ordinal();
		log.setLayout(new GridLayout(1, 3, 0, 0));
		log.setBorder(new EmptyBorder(0, 0, 0, 0));
		systemButton[butID] = new JButton(SystemButtonLabel.PREV.getLabel());
		systemButton[butID].addActionListener(buttonListener);
		systemButton[butID].setMargin(new Insets(0, 0, 0, 0));
		systemButton[butID].setFocusable(false);
		log.add(systemButton[butID]);

		butID = SystemButtonLabel.LOG.ordinal();
		systemButton[butID] = new JButton(SystemButtonLabel.LOG.getLabel());
		systemButton[butID].addActionListener(buttonListener);
		systemButton[butID].setMargin(new Insets(0, 0, 0, 0));
		systemButton[butID].setFocusable(false);
		log.add(systemButton[butID]);

		butID = SystemButtonLabel.NEXT.ordinal();
		systemButton[butID] = new JButton(SystemButtonLabel.NEXT.getLabel());
		systemButton[butID].addActionListener(buttonListener);
		systemButton[butID].setMargin(new Insets(0, 0, 0, 0));
		systemButton[butID].setFocusable(false);
		log.add(systemButton[butID]);
		retpanel.add(log);

		// ログクリアボタン
		butID = SystemButtonLabel.LOGCLEAR.ordinal();
		systemButton[butID] = new JButton(SystemButtonLabel.LOGCLEAR.getLabel());
		systemButton[butID].addActionListener(buttonListener);
		systemButton[butID].setFocusable(false);
		retpanel.add(systemButton[butID]);

		// セリフ、カーソル表示切替ボタン
		Image[] icon = IconPool.getButtonIconImageArray();
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new GridLayout(1, 0, 0, 0));
		buttonPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		scriptButton = new JToggleButton(new ImageIcon(icon[IconPool.ButtonIcon.POPUP_OFF.ordinal()]));
		scriptButton.addActionListener(buttonListener);
		scriptButton.setMargin(new Insets(0, 0, 0, 0));
		scriptButton.setFocusable(false);
		buttonPane.add(scriptButton);

		targetButton = new JToggleButton(new ImageIcon(icon[IconPool.ButtonIcon.TARGET.ordinal()]));
		targetButton.addActionListener(buttonListener);
		targetButton.setMargin(new Insets(0, 0, 0, 0));
		targetButton.setFocusable(false);
		buttonPane.add(targetButton);

		helpButton = new JToggleButton(new ImageIcon(icon[IconPool.ButtonIcon.HELP_OFF.ordinal()]));
		helpButton.addActionListener(buttonListener);
		helpButton.setMargin(new Insets(0, 0, 0, 0));
		helpButton.setFocusable(false);
		buttonPane.add(helpButton);

		optionButton = new JToggleButton(new ImageIcon(icon[IconPool.ButtonIcon.OPTION.ordinal()]));
		optionButton.addActionListener(buttonListener);
		optionButton.setMargin(new Insets(0, 0, 0, 0));
		optionButton.setFocusable(false);
		buttonPane.add(optionButton);
		retpanel.add(buttonPane);

		// オプションポップアップ
		OptionMenuListener oml = listenerPack.new OptionMenuListener();
		OptionPopupListener opl = listenerPack.new OptionPopupListener();
		optionPopup.addPopupMenuListener(opl);
		int size = OptionPopup.values().length;
		for(int i = 0; i < size; i++) {
			JMenuItem menu = new JMenuItem(OptionPopup.values()[i].getLabel());
			menu.addActionListener(oml);
			menu.setActionCommand(OptionPopup.values()[i].name());
			optionPopup.add(menu);
		}

		// プレイヤー情報
		JPanel action = new JPanel();
		butID = ToolButtonLabel.MOVE.ordinal();
		action.setLayout(new GridLayout(1, 2, 0, 0));
		action.setBorder(new EmptyBorder(0, 0, 0, 0));
		playerButton[butID] = new JToggleButton(ToolButtonLabel.MOVE.getLabel());
		playerButton[butID].addActionListener(buttonListener);
		playerButton[butID].setMargin(new Insets(0, 0, 0, 0));
		playerButton[butID].setFocusable(false);
		action.add(playerButton[butID]);

		butID = ToolButtonLabel.BAG.ordinal();
		playerButton[butID] = new JToggleButton(ToolButtonLabel.BAG.getLabel());
		playerButton[butID].addActionListener(buttonListener);
		playerButton[butID].setMargin(new Insets(0, 0, 0, 0));
		playerButton[butID].setFocusable(false);
		action.add(playerButton[butID]);
		retpanel.add(action);

		// アイテムアイコン
		JPanel itemPane = new JPanel();
		itemPane.setLayout(new GridLayout(1, 6, 0, 0));
		for(int i = 0; i < itemIconLabel.length; i++) {
			itemIconLabel[i] = new JLabel();
			itemPane.add(itemIconLabel[i]);
		}
		retpanel.add(itemPane);

		// ステータス
		StatusLabel[] enums = StatusLabel.values();
		JPanel item = new JPanel();
		item.setLayout(new GridLayout(1, 11, 0, 0));
		item.setBorder(new EmptyBorder(0, 0, 0, 0));
		for(int i = 0; i < yuStatusLabel.length; i++) {
			if(i == 1) {
				yuStatusLabel[i] = new JLabel(enums[i].getLabel());
			} else {
				yuStatusLabel[i] = new JLabel(enums[i].getLabel() + " - ");
			}
			item.add(yuStatusLabel[i]);
		}

		retpanel.add(yuStatusLabel[StatusLabel.MONEY.ordinal()]);
		retpanel.add(yuStatusLabel[StatusLabel.LABEL.ordinal()]);
		JPanel namePane = new JPanel();
		namePane.setLayout(new BoxLayout(namePane, BoxLayout.X_AXIS));
		pinButton = new JToggleButton(new ImageIcon(icon[IconPool.ButtonIcon.PIN.ordinal()]));
		pinButton.addActionListener(buttonListener);
		pinButton.setMargin(new Insets(0, 0, 0, 0));
		pinButton.setFocusable(false);
		namePane.add(pinButton);
		namePane.add(yuStatusLabel[StatusLabel.NAME.ordinal()]);
		retpanel.add(namePane);

		retpanel.add(yuStatusLabel[StatusLabel.RANK.ordinal()]);
		retpanel.add(yuStatusLabel[StatusLabel.PERSONALITY.ordinal()]);
		retpanel.add(yuStatusLabel[StatusLabel.INTEL.ordinal()]);
		retpanel.add(yuStatusLabel[StatusLabel.DAMAGE.ordinal()]);
		retpanel.add(yuStatusLabel[StatusLabel.STRESS.ordinal()]);
		retpanel.add(yuStatusLabel[StatusLabel.HUNGER.ordinal()]);
		retpanel.add(yuStatusLabel[StatusLabel.TANG.ordinal()]);
		retpanel.add(yuStatusLabel[StatusLabel.SHIT.ordinal()]);
		retpanel.add(item);

		// 状態異常アイコン
		JPanel statPane = new JPanel();
		statPane.setLayout(new GridLayout(1, 6, 0, 0));
		for(int i = 0; i < statIconLabel.length; i++) {
			statIconLabel[i] = new JLabel();
			statPane.add(statIconLabel[i]);
		}
		retpanel.add(statPane);

		// このペインから呼ばれるサブウィンドウ作成
		mapWindow = new MapWindow(SimYukkuri.simYukkuri);
		itemWindow = new ItemWindow(SimYukkuri.simYukkuri);

		// プレイヤー情報は表示
		showPlayerStatus();

		return retpanel;
	}
	/**
	 * ステータスをクリアする.
	 */
	public static void clearStatus() {
		yuStatusLabel[StatusLabel.MONEY.ordinal()].setText(StatusLabel.MONEY.getLabel() + SimYukkuri.world.getPlayer().getCash());
		yuStatusLabel[StatusLabel.NAME.ordinal()].setText("");
		yuStatusLabel[StatusLabel.RANK.ordinal()].setText(StatusLabel.RANK.getLabel());
		yuStatusLabel[StatusLabel.PERSONALITY.ordinal()].setText(StatusLabel.PERSONALITY.getLabel());
		yuStatusLabel[StatusLabel.INTEL.ordinal()].setText(StatusLabel.INTEL.getLabel());
		yuStatusLabel[StatusLabel.DAMAGE.ordinal()].setText(StatusLabel.DAMAGE.getLabel());
		yuStatusLabel[StatusLabel.STRESS.ordinal()].setText(StatusLabel.STRESS.getLabel());
		yuStatusLabel[StatusLabel.HUNGER.ordinal()].setText(StatusLabel.HUNGER.getLabel());
		yuStatusLabel[StatusLabel.TANG.ordinal()].setText(StatusLabel.TANG.getLabel());
		yuStatusLabel[StatusLabel.SHIT.ordinal()].setText(StatusLabel.SHIT.getLabel());

		pinButton.setSelected(false);

		statIconLabel[0].setIcon(null);
		statIconLabel[0].setToolTipText(null);
		statIconLabel[1].setIcon(null);
		statIconLabel[1].setToolTipText(null);
		statIconLabel[2].setIcon(null);
		statIconLabel[2].setToolTipText(null);
		statIconLabel[3].setIcon(null);
		statIconLabel[3].setToolTipText(null);
		statIconLabel[4].setIcon(null);
		statIconLabel[4].setToolTipText(null);
	}
	/**
	 * プレイヤーのステータスを表示する.
	 */
	public static void showPlayerStatus() {
		if( SimYukkuri.world == null )
		{
			return;
		}
		// 現金更新
		yuStatusLabel[StatusLabel.MONEY.ordinal()].setText(StatusLabel.MONEY.getLabel() + SimYukkuri.world.getPlayer().getCash());

		IconPool.StatusIcon[] stat = IconPool.StatusIcon.values();
		ImageIcon[] img = IconPool.getStatusIconImageArray();
		//　精子餡保持状態更新
		if(SimYukkuri.sperm != null) {
			itemIconLabel[0].setIcon(img[IconPool.StatusIcon.SPERM.ordinal()]);
			itemIconLabel[0].setToolTipText(stat[IconPool.StatusIcon.SPERM.ordinal()].getHelp());
		} else {
			itemIconLabel[0].setIcon(null);
			itemIconLabel[0].setToolTipText(null);
		}

	}
	/**
	 * ゆっくりのステータスを表示する.
	 * @param b ゆっくり
	 */
	public static void showStatus(Body b) {
		int damage = 100 * b.getDamage() / b.getDamageLimit();
		int hungry = 100 * b.getHungry() / b.getHungryLimit();
		int shit = 100 * b.getShit() / b.getShitLimit();
		int stress = 100 * b.getStress() / b.getStressLimit();
		int nLovePlayer = 100 * b.getnLovePlayer() / b.getLOVEPLAYERLIMITorg();

		yuStatusLabel[StatusLabel.MONEY.ordinal()].setText(StatusLabel.MONEY.getLabel() + SimYukkuri.world.getPlayer().getCash());
		yuStatusLabel[StatusLabel.NAME.ordinal()].setText(" " + (ResourceUtil.IS_JP ? b.getNameJ() : b.getNameE()));
		yuStatusLabel[StatusLabel.RANK.ordinal()].setText(StatusLabel.RANK.getLabel() + BodyRank.values()[b.getBodyRank().ordinal()].getDisplayName());
		yuStatusLabel[StatusLabel.PERSONALITY.ordinal()].setText(StatusLabel.PERSONALITY.getLabel() +
				(ResourceUtil.IS_JP ? ATTITUDE_LEVEL_J[b.getAttitude().ordinal()] : ATTITUDE_LEVEL_E[b.getAttitude().ordinal()]));
		yuStatusLabel[StatusLabel.INTEL.ordinal()].setText(StatusLabel.INTEL.getLabel() + 
				(ResourceUtil.IS_JP ? INTEL_LEVEL_J[b.getIntelligence().ordinal()] : INTEL_LEVEL_E[b.getIntelligence().ordinal()] ));
		yuStatusLabel[StatusLabel.DAMAGE.ordinal()].setText(StatusLabel.DAMAGE.getLabel() + damage + "%");
		yuStatusLabel[StatusLabel.STRESS.ordinal()].setText(StatusLabel.STRESS.getLabel() + stress + "%");
		yuStatusLabel[StatusLabel.HUNGER.ordinal()].setText(StatusLabel.HUNGER.getLabel() + hungry + "%");
		yuStatusLabel[StatusLabel.TANG.ordinal()].setText(StatusLabel.TANG.getLabel() + 
				(ResourceUtil.IS_JP ? TANG_LEVEL_J[b.getTangType().ordinal()] : TANG_LEVEL_E[b.getTangType().ordinal()]));
		yuStatusLabel[StatusLabel.SHIT.ordinal()].setText(StatusLabel.SHIT.getLabel() + shit + "%");
		yuStatusLabel[StatusLabel.LOVEPLAYER.ordinal()].setText(StatusLabel.LOVEPLAYER.getLabel() + nLovePlayer + "%");

		pinButton.setSelected(b.isPin());

		IconPool.StatusIcon[] stat = IconPool.StatusIcon.values();
		ImageIcon[] img = IconPool.getStatusIconImageArray();

		if(b.isAnalClose()) {
			statIconLabel[0].setIcon(img[IconPool.StatusIcon.UNSHIT.ordinal()]);
			statIconLabel[0].setToolTipText(stat[IconPool.StatusIcon.UNSHIT.ordinal()].getHelp());
		}
		else {
			statIconLabel[0].setIcon(null);
			statIconLabel[0].setToolTipText(null);
		}
		if(b.isStalkCastration()) {
			statIconLabel[1].setIcon(img[IconPool.StatusIcon.UNSTALK.ordinal()]);
			statIconLabel[1].setToolTipText(stat[IconPool.StatusIcon.UNSTALK.ordinal()].getHelp());
		}
		else {
			statIconLabel[1].setIcon(null);
			statIconLabel[1].setToolTipText(null);
		}
		if(b.isBodyCastration()) {
			statIconLabel[2].setIcon(img[IconPool.StatusIcon.UNBABY.ordinal()]);
			statIconLabel[2].setToolTipText(stat[IconPool.StatusIcon.UNBABY.ordinal()].getHelp());
		}
		else {
			statIconLabel[2].setIcon(null);
			statIconLabel[2].setToolTipText(null);
		}
		if(b.isPredatorType()) {
			statIconLabel[3].setIcon(img[IconPool.StatusIcon.PREDATOR.ordinal()]);
			statIconLabel[3].setToolTipText(stat[IconPool.StatusIcon.PREDATOR.ordinal()].getHelp());
		}
		else {
			statIconLabel[3].setIcon(null);
			statIconLabel[3].setToolTipText(null);
		}
		if(b.isRaper()) {
			statIconLabel[4].setIcon(img[IconPool.StatusIcon.RAPER.ordinal()]);
			statIconLabel[4].setToolTipText(stat[IconPool.StatusIcon.RAPER.ordinal()].getHelp());
		}
		else {
			statIconLabel[4].setIcon(null);
			statIconLabel[4].setToolTipText(null);
		}
		if(b.isbPenipeniCutted()) {
			statIconLabel[5].setIcon(img[IconPool.StatusIcon.PENIPENICUT.ordinal()]);
			statIconLabel[5].setToolTipText(stat[IconPool.StatusIcon.PENIPENICUT.ordinal()].getHelp());
		}
		else {
			statIconLabel[5].setIcon(null);
			statIconLabel[5].setToolTipText(null);
		}
		if(b.isbPheromone()) {
			statIconLabel[6].setIcon(img[IconPool.StatusIcon.PHEROMONE.ordinal()]);
			statIconLabel[6].setToolTipText(stat[IconPool.StatusIcon.PHEROMONE.ordinal()].getHelp());
		}
		else {
			statIconLabel[6].setIcon(null);
			statIconLabel[6].setToolTipText(null);
		}
		if(b.getPublicRank() == PublicRank.UnunSlave) {
			statIconLabel[7].setIcon(img[IconPool.StatusIcon.UNUNSLAVE.ordinal()]);
			statIconLabel[7].setToolTipText(stat[IconPool.StatusIcon.UNUNSLAVE.ordinal()].getHelp());
		}
		else {
			statIconLabel[7].setIcon(null);
			statIconLabel[7].setToolTipText(null);
		}
	}

	public static int getSelectedGameSpeed() {
		return selectedGameSpeed;
	}
	
	public static void setSelectedGameSpeed(int selectedGameSpeed) {
		MainCommandUI.selectedGameSpeed = selectedGameSpeed;
	}
	
	public static int getSelectedZoomScale() {
		return selectedZoomScale;
	}
	
	public static void setSelectedZoomScale(int selectedZoomScale) {
		MainCommandUI.selectedZoomScale = selectedZoomScale;
	}
	
	@SuppressWarnings("rawtypes")
	public static JComboBox getGameSpeedCombo() {
		return gameSpeedCombo;
	}
	
	@SuppressWarnings("rawtypes")
	public static void setGameSpeedCombo(JComboBox gameSpeedCombo) {
		MainCommandUI.gameSpeedCombo = gameSpeedCombo;
	}
	
	@SuppressWarnings("rawtypes")
	public static JComboBox getMainItemCombo() {
		return mainItemCombo;
	}
	
	@SuppressWarnings("rawtypes")
	public static void setMainItemCombo(JComboBox mainItemCombo) {
		MainCommandUI.mainItemCombo = mainItemCombo;
	}
	
	@SuppressWarnings("rawtypes")
	public static JComboBox getSubItemCombo() {
		return subItemCombo;
	}
	
	@SuppressWarnings("rawtypes")
	public static void setSubItemCombo(JComboBox subItemCombo) {
		MainCommandUI.subItemCombo = subItemCombo;
	}
	
	public static JLabel[] getYuStatusLabel() {
		return yuStatusLabel;
	}
	
	public static void setYuStatusLabel(JLabel[] yuStatusLabel) {
		MainCommandUI.yuStatusLabel = yuStatusLabel;
	}
	
	public static JLabel[] getStatIconLabel() {
		return statIconLabel;
	}
	
	public static void setStatIconLabel(JLabel[] statIconLabel) {
		MainCommandUI.statIconLabel = statIconLabel;
	}
	
	public static JLabel[] getItemIconLabel() {
		return itemIconLabel;
	}
	
	public static void setItemIconLabel(JLabel[] itemIconLabel) {
		MainCommandUI.itemIconLabel = itemIconLabel;
	}
	
	public static JButton[] getSystemButton() {
		return systemButton;
	}
	
	public static void setSystemButton(JButton[] systemButton) {
		MainCommandUI.systemButton = systemButton;
	}
	
	public static JToggleButton getScriptButton() {
		return scriptButton;
	}
	
	public static void setScriptButton(JToggleButton scriptButton) {
		MainCommandUI.scriptButton = scriptButton;
	}
	
	public static JToggleButton getTargetButton() {
		return targetButton;
	}
	
	public static void setTargetButton(JToggleButton targetButton) {
		MainCommandUI.targetButton = targetButton;
	}
	
	public static JToggleButton getPinButton() {
		return pinButton;
	}
	
	public static void setPinButton(JToggleButton pinButton) {
		MainCommandUI.pinButton = pinButton;
	}
	
	public static JToggleButton getHelpButton() {
		return helpButton;
	}
	
	public static void setHelpButton(JToggleButton helpButton) {
		MainCommandUI.helpButton = helpButton;
	}
	
	public static JToggleButton getOptionButton() {
		return optionButton;
	}
	
	public static void setOptionButton(JToggleButton optionButton) {
		MainCommandUI.optionButton = optionButton;
	}
	
	public static JToggleButton[] getPlayerButton() {
		return playerButton;
	}
	
	public static void setPlayerButton(JToggleButton[] playerButton) {
		MainCommandUI.playerButton = playerButton;
	}
	
	public static JPopupMenu getOptionPopup() {
		return optionPopup;
	}
	
	public static void setOptionPopup(JPopupMenu optionPopup) {
		MainCommandUI.optionPopup = optionPopup;
	}
	
	public static MapWindow getMapWindow() {
		return mapWindow;
	}
	
	public static void setMapWindow(MapWindow mapWindow) {
		MainCommandUI.mapWindow = mapWindow;
	}
	
	public static ItemWindow getItemWindow() {
		return itemWindow;
	}
	
	public static void setItemWindow(ItemWindow itemWindow) {
		MainCommandUI.itemWindow = itemWindow;
	}
}





