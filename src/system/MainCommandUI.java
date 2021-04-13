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



public class MainCommandUI {

	// システムボタンテキスト
	static enum SystemButtonLabel {
		ADDBODY("ゆっくり追加"),
		SAVE("セーブ"),
		LOAD("ロード"),
		PREV("<<"),
		LOG("ログ"),
		NEXT(">>"),
		LOGCLEAR("ログクリア"),
		;
        public String label;
        SystemButtonLabel(String str) { this.label = str; }
	}

	// ツールボタン
	static enum ToolButtonLabel {
		MOVE("移動"),
		BAG("持ち物"),
		;
        public String label;
        ToolButtonLabel(String str) { this.label = str; }
	}

	// ステータスのラベル
	static enum StatusLabel {
		MONEY(" 所持金: "),
		LABEL("ゆっくりの状態"),
		NAME(""),
		RANK(" ランク: "),
		PERSONALITY(" 性格: "),
		INTEL(" 知能: "),
		DAMAGE(" ダメージ: "),
		STRESS(" ストレス: "),
		HUNGER(" 満腹度: "),
		TANG(" 味覚: "),
		SHIT(" うんうん: "),
		LOVEPLAYER(" なつき度: "),
		;
        public String label;
        StatusLabel(String str) { this.label = str; }
	}

	// オプションポップアップ
	static enum OptionPopup {
		INI_RELOAD("iniファイル再読み込み")
		;
		public String label;
		OptionPopup(String str) { this.label = str; }
	}


	private static final String[] ATTITUDE_LEVEL_J = { "超善良", "善良", "普通", "ゲス", "ドゲス"};
	private static final String[] INTEL_LEVEL_J = { "バッジ級", "普通", "餡子脳"};
	private static final String[] TANG_LEVEL_J = { "バカ舌", "普通", "肥えてる"};

	public static int selectedGameSpeed = 1;
	public static int selectedZoomScale = 0;
	public static JComboBox gameSpeedCombo;
	public static JComboBox mainItemCombo;
	public static JComboBox subItemCombo;
	public static JLabel[] yuStatusLabel = new JLabel[StatusLabel.values().length];
	public static JLabel[] statIconLabel = new JLabel[8];
	public static JLabel[] itemIconLabel = new JLabel[1];

	public static JButton[] systemButton = new JButton[SystemButtonLabel.values().length];
	public static JToggleButton scriptButton, targetButton, pinButton, helpButton, optionButton;
	public static JToggleButton[] playerButton = new JToggleButton[ToolButtonLabel.values().length];

	public static JPopupMenu optionPopup = new JPopupMenu();
	public static MapWindow mapWindow;
	public static ItemWindow itemWindow;

	// メニューエリアの幅
	public static final int MENU_PANE_X = 124;

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
		mainItemCombo.setModel(GadgetMenu.mainModel);
		retpanel.add(mainItemCombo);

		SubItemComboBoxListener sil = listenerPack.new SubItemComboBoxListener();
		subItemCombo = new JComboBox();
		subItemCombo.setFocusable(false);
		subItemCombo.addItemListener(sil);
		subItemCombo.setModel(GadgetMenu.toolModel);
		retpanel.add(subItemCombo);

		ButtonListener buttonListener = listenerPack.new ButtonListener();
		int butID;

		// ゆっくり追加ボタン
		butID = SystemButtonLabel.ADDBODY.ordinal();
		systemButton[butID] = new JButton(SystemButtonLabel.ADDBODY.label);
		systemButton[butID].addActionListener(buttonListener);
		systemButton[butID].setFocusable(false);
		retpanel.add(systemButton[butID]);

		// セーブ・ロード
		JPanel saveLoad = new JPanel();
		butID = SystemButtonLabel.SAVE.ordinal();
		saveLoad.setLayout(new GridLayout(1, 2, 0, 0));
		saveLoad.setBorder(new EmptyBorder(0, 0, 0, 0));
		systemButton[butID] = new JButton(SystemButtonLabel.SAVE.label);
		systemButton[butID].addActionListener(buttonListener);
		systemButton[butID].setMargin(new Insets(0, 0, 0, 0));
		systemButton[butID].setFocusable(false);
		saveLoad.add(systemButton[butID]);

		butID = SystemButtonLabel.LOAD.ordinal();
		systemButton[butID] = new JButton(SystemButtonLabel.LOAD.label);
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
		systemButton[butID] = new JButton(SystemButtonLabel.PREV.label);
		systemButton[butID].addActionListener(buttonListener);
		systemButton[butID].setMargin(new Insets(0, 0, 0, 0));
		systemButton[butID].setFocusable(false);
		log.add(systemButton[butID]);

		butID = SystemButtonLabel.LOG.ordinal();
		systemButton[butID] = new JButton(SystemButtonLabel.LOG.label);
		systemButton[butID].addActionListener(buttonListener);
		systemButton[butID].setMargin(new Insets(0, 0, 0, 0));
		systemButton[butID].setFocusable(false);
		log.add(systemButton[butID]);

		butID = SystemButtonLabel.NEXT.ordinal();
		systemButton[butID] = new JButton(SystemButtonLabel.NEXT.label);
		systemButton[butID].addActionListener(buttonListener);
		systemButton[butID].setMargin(new Insets(0, 0, 0, 0));
		systemButton[butID].setFocusable(false);
		log.add(systemButton[butID]);
		retpanel.add(log);

		// ログクリアボタン
		butID = SystemButtonLabel.LOGCLEAR.ordinal();
		systemButton[butID] = new JButton(SystemButtonLabel.LOGCLEAR.label);
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
			JMenuItem menu = new JMenuItem(OptionPopup.values()[i].label);
			menu.addActionListener(oml);
			menu.setActionCommand(OptionPopup.values()[i].name());
			optionPopup.add(menu);
		}

		// プレイヤー情報
		JPanel action = new JPanel();
		butID = ToolButtonLabel.MOVE.ordinal();
		action.setLayout(new GridLayout(1, 2, 0, 0));
		action.setBorder(new EmptyBorder(0, 0, 0, 0));
		playerButton[butID] = new JToggleButton(ToolButtonLabel.MOVE.label);
		playerButton[butID].addActionListener(buttonListener);
		playerButton[butID].setMargin(new Insets(0, 0, 0, 0));
		playerButton[butID].setFocusable(false);
		action.add(playerButton[butID]);

		butID = ToolButtonLabel.BAG.ordinal();
		playerButton[butID] = new JToggleButton(ToolButtonLabel.BAG.label);
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
				yuStatusLabel[i] = new JLabel(enums[i].label);
			} else {
				yuStatusLabel[i] = new JLabel(enums[i].label + " - ");
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

	public static void clearStatus() {
		yuStatusLabel[StatusLabel.MONEY.ordinal()].setText(StatusLabel.MONEY.label + SimYukkuri.world.getPlayer().cash);
		yuStatusLabel[StatusLabel.NAME.ordinal()].setText("");
		yuStatusLabel[StatusLabel.RANK.ordinal()].setText(StatusLabel.RANK.label);
		yuStatusLabel[StatusLabel.PERSONALITY.ordinal()].setText(StatusLabel.PERSONALITY.label);
		yuStatusLabel[StatusLabel.INTEL.ordinal()].setText(StatusLabel.INTEL.label);
		yuStatusLabel[StatusLabel.DAMAGE.ordinal()].setText(StatusLabel.DAMAGE.label);
		yuStatusLabel[StatusLabel.STRESS.ordinal()].setText(StatusLabel.STRESS.label);
		yuStatusLabel[StatusLabel.HUNGER.ordinal()].setText(StatusLabel.HUNGER.label);
		yuStatusLabel[StatusLabel.TANG.ordinal()].setText(StatusLabel.TANG.label);
		yuStatusLabel[StatusLabel.SHIT.ordinal()].setText(StatusLabel.SHIT.label);

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

	public static void showPlayerStatus() {
		if( SimYukkuri.world == null )
		{
			return;
		}
		// 現金更新
		yuStatusLabel[StatusLabel.MONEY.ordinal()].setText(StatusLabel.MONEY.label + SimYukkuri.world.getPlayer().getCash());

		IconPool.StatusIcon[] stat = IconPool.StatusIcon.values();
		ImageIcon[] img = IconPool.getStatusIconImageArray();
		//　精子餡保持状態更新
		if(SimYukkuri.sperm != null) {
			itemIconLabel[0].setIcon(img[IconPool.StatusIcon.SPERM.ordinal()]);
			itemIconLabel[0].setToolTipText(stat[IconPool.StatusIcon.SPERM.ordinal()].help);
		} else {
			itemIconLabel[0].setIcon(null);
			itemIconLabel[0].setToolTipText(null);
		}

	}

	public static void showStatus(Body b) {
		int damage = 100 * b.getDamage() / b.getDamageLimit();
		int hungry = 100 * b.getHungry() / b.getHungryLimit();
		int shit = 100 * b.getShit() / b.getShitLimit();
		int stress = 100 * b.getStress() / b.getStressLimit();
		int nLovePlayer = 100 * b.getnLovePlayer() / b.getLOVEPLAYERLIMIT();

		yuStatusLabel[StatusLabel.MONEY.ordinal()].setText(StatusLabel.MONEY.label + SimYukkuri.world.getPlayer().getCash());
		yuStatusLabel[StatusLabel.NAME.ordinal()].setText(" " + b.getNameJ());
		yuStatusLabel[StatusLabel.RANK.ordinal()].setText(StatusLabel.RANK.label + BodyRank.values()[b.getBodyRank().ordinal()].displayName);
		yuStatusLabel[StatusLabel.PERSONALITY.ordinal()].setText(StatusLabel.PERSONALITY.label + ATTITUDE_LEVEL_J[b.getAttitude().ordinal()]);
		yuStatusLabel[StatusLabel.INTEL.ordinal()].setText(StatusLabel.INTEL.label + INTEL_LEVEL_J[b.getIntelligence().ordinal()]);
		yuStatusLabel[StatusLabel.DAMAGE.ordinal()].setText(StatusLabel.DAMAGE.label + damage + "%");
		yuStatusLabel[StatusLabel.STRESS.ordinal()].setText(StatusLabel.STRESS.label + stress + "%");
		yuStatusLabel[StatusLabel.HUNGER.ordinal()].setText(StatusLabel.HUNGER.label + hungry + "%");
		yuStatusLabel[StatusLabel.TANG.ordinal()].setText(StatusLabel.TANG.label + TANG_LEVEL_J[b.getTangType().ordinal()]);
		yuStatusLabel[StatusLabel.SHIT.ordinal()].setText(StatusLabel.SHIT.label + shit + "%");
		yuStatusLabel[StatusLabel.LOVEPLAYER.ordinal()].setText(StatusLabel.LOVEPLAYER.label + nLovePlayer + "%");

		pinButton.setSelected(b.isPin());

		IconPool.StatusIcon[] stat = IconPool.StatusIcon.values();
		ImageIcon[] img = IconPool.getStatusIconImageArray();

		if(b.isAnalClose()) {
			statIconLabel[0].setIcon(img[IconPool.StatusIcon.UNSHIT.ordinal()]);
			statIconLabel[0].setToolTipText(stat[IconPool.StatusIcon.UNSHIT.ordinal()].help);
		}
		else {
			statIconLabel[0].setIcon(null);
			statIconLabel[0].setToolTipText(null);
		}
		if(b.isStalkCastration()) {
			statIconLabel[1].setIcon(img[IconPool.StatusIcon.UNSTALK.ordinal()]);
			statIconLabel[1].setToolTipText(stat[IconPool.StatusIcon.UNSTALK.ordinal()].help);
		}
		else {
			statIconLabel[1].setIcon(null);
			statIconLabel[1].setToolTipText(null);
		}
		if(b.isBodyCastration()) {
			statIconLabel[2].setIcon(img[IconPool.StatusIcon.UNBABY.ordinal()]);
			statIconLabel[2].setToolTipText(stat[IconPool.StatusIcon.UNBABY.ordinal()].help);
		}
		else {
			statIconLabel[2].setIcon(null);
			statIconLabel[2].setToolTipText(null);
		}
		if(b.isPredatorType()) {
			statIconLabel[3].setIcon(img[IconPool.StatusIcon.PREDATOR.ordinal()]);
			statIconLabel[3].setToolTipText(stat[IconPool.StatusIcon.PREDATOR.ordinal()].help);
		}
		else {
			statIconLabel[3].setIcon(null);
			statIconLabel[3].setToolTipText(null);
		}
		if(b.isRaper()) {
			statIconLabel[4].setIcon(img[IconPool.StatusIcon.RAPER.ordinal()]);
			statIconLabel[4].setToolTipText(stat[IconPool.StatusIcon.RAPER.ordinal()].help);
		}
		else {
			statIconLabel[4].setIcon(null);
			statIconLabel[4].setToolTipText(null);
		}
		if(b.isbPenipeniCutted()) {
			statIconLabel[5].setIcon(img[IconPool.StatusIcon.PENIPENICUT.ordinal()]);
			statIconLabel[5].setToolTipText(stat[IconPool.StatusIcon.PENIPENICUT.ordinal()].help);
		}
		else {
			statIconLabel[5].setIcon(null);
			statIconLabel[5].setToolTipText(null);
		}
		if(b.isbPheromone()) {
			statIconLabel[6].setIcon(img[IconPool.StatusIcon.PHEROMONE.ordinal()]);
			statIconLabel[6].setToolTipText(stat[IconPool.StatusIcon.PHEROMONE.ordinal()].help);
		}
		else {
			statIconLabel[6].setIcon(null);
			statIconLabel[6].setToolTipText(null);
		}
		if(b.getPublicRank() == PublicRank.UnunSlave) {
			statIconLabel[7].setIcon(img[IconPool.StatusIcon.UNUNSLAVE.ordinal()]);
			statIconLabel[7].setToolTipText(stat[IconPool.StatusIcon.UNUNSLAVE.ordinal()].help);
		}
		else {
			statIconLabel[7].setIcon(null);
			statIconLabel[7].setToolTipText(null);
		}
	}

}




