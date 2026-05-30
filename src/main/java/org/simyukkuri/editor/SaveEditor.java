package org.simyukkuri.editor;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.engine.SaveDataCodec;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.Attachment;
import org.simyukkuri.entity.core.attachment.impl.AccelAmpoule;
import org.simyukkuri.entity.core.attachment.impl.Ants;
import org.simyukkuri.entity.core.attachment.impl.AnydAmpoule;
import org.simyukkuri.entity.core.attachment.impl.Badge;
import org.simyukkuri.entity.core.attachment.impl.BreedingAmpoule;
import org.simyukkuri.entity.core.attachment.impl.Fire;
import org.simyukkuri.entity.core.attachment.impl.HungryAmpoule;
import org.simyukkuri.entity.core.attachment.impl.Needle;
import org.simyukkuri.entity.core.attachment.impl.OrangeAmpoule;
import org.simyukkuri.entity.core.attachment.impl.PoisonAmpoule;
import org.simyukkuri.entity.core.attachment.impl.StopAmpoule;
import org.simyukkuri.entity.core.attachment.impl.VeryShitAmpoule;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.HybridYukkuri;
import org.simyukkuri.entity.core.meta.Player;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.entity.core.world.bodylinked.Okazari;
import org.simyukkuri.entity.core.world.bodylinked.Okazari.OkazariType;
import org.simyukkuri.entity.core.world.item.AutoFeeder;
import org.simyukkuri.entity.core.world.item.Bed;
import org.simyukkuri.entity.core.world.item.BreedingPool;
import org.simyukkuri.entity.core.world.item.Diffuser;
import org.simyukkuri.entity.core.world.item.Diffuser.SteamType;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.entity.core.world.item.Food.FoodType;
import org.simyukkuri.entity.core.world.item.FoodMaker;
import org.simyukkuri.entity.core.world.item.House;
import org.simyukkuri.entity.core.world.item.House.HouseTable;
import org.simyukkuri.entity.core.world.item.OrangePool;
import org.simyukkuri.entity.core.world.item.ProcessorPlate;
import org.simyukkuri.entity.core.world.item.ProcessorPlate.ProcessType;
import org.simyukkuri.entity.core.world.item.StickyPlate;
import org.simyukkuri.entity.core.world.item.Stone;
import org.simyukkuri.entity.core.world.item.Toilet;
import org.simyukkuri.entity.core.world.item.Toy;
import org.simyukkuri.entity.core.world.item.Trampoline;
import org.simyukkuri.entity.core.world.item.Yunba;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.FavItemType;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.enums.TakeoutItemType;
import org.simyukkuri.enums.YukkuriRank;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.field.FieldShape;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.field.impl.Farm;
import org.simyukkuri.field.impl.Pool;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.util.GameLocale;
import org.simyukkuri.util.GameText;
import org.simyukkuri.util.LocaleSource;

/** セーブデータエディター本体クラス。 */
public class SaveEditor {

    // ── i18n ショートハンド ──────────────────────────────────────

    /** プロパティキーを読んで返す。`\n` エスケープを改行に変換する。 */
    static String getString(String key) {
        String v = GameText.read(key);
        if (v == null) {
            return key;
        }
        return v.replace("\\n", "\n");
    }

    // ── ラベル付き enum アイテム ──────────────────────────────────

    static class LabeledItem {
        final String enumName;
        final String label;

        LabeledItem(String n, String l) {
            enumName = n;
            label = l;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    // ── ObjectEntry: エンティティ + 元マップ（削除用） ──────────────

    @SuppressWarnings({"rawtypes"})
    static class ObjectEntry {
        final Entity entity;
        final Map sourceMap;

        ObjectEntry(Entity e, Map m) {
            entity = e;
            sourceMap = m;
        }

        void remove() {
            sourceMap.remove(entity.getObjId());
        }
    }

    // ── FieldEntry: FieldShape + 元リスト（削除用）──────────────────

    @SuppressWarnings("rawtypes")
    static class FieldEntry {
        final FieldShape shape;
        final List sourceList;
        final WorldState worldState;

        FieldEntry(FieldShape s, List src, WorldState ws) {
            shape = s;
            sourceList = src;
            worldState = ws;
        }

        void remove() {
            if (shape instanceof Barrier) {
                Barrier b = (Barrier) shape;
                if (worldState.getWallGrid() != null) {
                    WorldState.setWallLine(worldState.getWallGrid(),
                            b.getStartX(), b.getStartY(), b.getEndX(), b.getEndY(),
                            false, b.getAttribute());
                }
            }
            sourceList.remove(shape);
        }

        @Override
        public String toString() {
            if (shape instanceof Barrier) {
                Barrier b = (Barrier) shape;
                return getString("editor_field_barrier") + " [" + barrierAttrLabel(b.getAttribute()) + "] "
                        + coordStr();
            } else if (shape instanceof Farm) {
                return getString("editor_field_farm") + " " + coordStr();
            } else if (shape instanceof Pool) {
                return getString("editor_field_pool") + " " + coordStr();
            }
            return shape.getClass().getSimpleName() + " " + coordStr();
        }

        private String coordStr() {
            return "(" + shape.getStartX() + "," + shape.getStartY() + ")-("
                    + shape.getEndX() + "," + shape.getEndY() + ")";
        }
    }

    static String barrierAttrLabel(int attr) {
        switch (attr) {
            case FieldShape.BARRIER_GAP_MINI: return getString("editor_barrier_gap_mini");
            case FieldShape.BARRIER_GAP_BIG:  return getString("editor_barrier_gap_big");
            case FieldShape.BARRIER_NET_MINI: return getString("editor_barrier_net_mini");
            case FieldShape.BARRIER_NET_BIG:  return getString("editor_barrier_net_big");
            case FieldShape.BARRIER_WALL:     return getString("editor_barrier_wall");
            case FieldShape.BARRIER_ITEM:     return getString("editor_barrier_item");
            case FieldShape.BARRIER_YUKKURI:  return getString("editor_barrier_yukkuri");
            case FieldShape.BARRIER_NOUNUN:   return getString("editor_barrier_nounun");
            case FieldShape.BARRIER_KEKKAI:   return getString("editor_barrier_kekkai");
            default: return String.valueOf(attr);
        }
    }

    // ── enum ラベル: static final は廃止、ファクトリメソッドへ ─────
    // (static initializer は main() より前に走るため言語設定前になる)

    static LabeledItem[] makeAgeStateItems() {
        return new LabeledItem[]{
            new LabeledItem("BABY",  getString("editor_age_baby")),
            new LabeledItem("CHILD", getString("editor_age_child")),
            new LabeledItem("ADULT", getString("editor_age_adult")),
        };
    }

    static LabeledItem[] makeAttitudeItems() {
        return new LabeledItem[]{
            new LabeledItem("VERY_NICE",      getString("editor_attitude_very_nice")),
            new LabeledItem("NICE",           getString("editor_attitude_nice")),
            new LabeledItem("AVERAGE",        getString("editor_attitude_average")),
            new LabeledItem("SHITHEAD",       getString("editor_attitude_shithead")),
            new LabeledItem("SUPER_SHITHEAD", getString("editor_attitude_super_shithead")),
        };
    }

    static LabeledItem[] makeIntelItems() {
        return new LabeledItem[]{
            new LabeledItem("WISE",    getString("editor_intel_wise")),
            new LabeledItem("AVERAGE", getString("editor_intel_average")),
            new LabeledItem("FOOL",    getString("editor_intel_fool")),
        };
    }

    static LabeledItem[] makeHappinessItems() {
        return new LabeledItem[]{
            new LabeledItem("VERY_HAPPY", getString("editor_happy_very_happy")),
            new LabeledItem("HAPPY",      getString("editor_happy_happy")),
            new LabeledItem("AVERAGE",    getString("editor_happy_average")),
            new LabeledItem("SAD",        getString("editor_happy_sad")),
            new LabeledItem("VERY_SAD",   getString("editor_happy_very_sad")),
        };
    }

    static LabeledItem[] makeRankItems() {
        return new LabeledItem[]{
            new LabeledItem("KAIYU",        getString("editor_rank_kaiyu")),
            new LabeledItem("SUTEYU",       getString("editor_rank_suteyu")),
            new LabeledItem("NORAYU_CLEAN", getString("editor_rank_norayu_clean")),
            new LabeledItem("NORAYU",       getString("editor_rank_norayu")),
            new LabeledItem("YASEIYU",      getString("editor_rank_yaseiyu")),
        };
    }

    static LabeledItem[] makeFoodTypeItems() {
        return new LabeledItem[]{
            new LabeledItem("SWEETS1",       getString("command_food_sweet1")),
            new LabeledItem("SWEETS2",       getString("command_food_sweet2")),
            new LabeledItem("STALK",         getString("item_stalk")),
            new LabeledItem("FOOD",          getString("command_food_normal")),
            new LabeledItem("BITTER",        getString("command_food_bitter")),
            new LabeledItem("LEMONPOP",      getString("command_food_ramune")),
            new LabeledItem("HOT",           getString("command_food_hot")),
            new LabeledItem("VIYUGRA",       getString("command_food_viagra")),
            new LabeledItem("WASTE",         getString("command_food_garbage")),
            new LabeledItem("SWEETS_NORA1",  getString("command_food_sweet1") + getString("editor_suffix_nora")),
            new LabeledItem("SWEETS_NORA2",  getString("command_food_sweet2") + getString("editor_suffix_nora")),
            new LabeledItem("FOOD_NORA",     getString("command_food_normal") + getString("editor_suffix_nora")),
            new LabeledItem("BITTER_NORA",   getString("command_food_bitter") + getString("editor_suffix_nora")),
            new LabeledItem("LEMONPOP_NORA", getString("command_food_ramune") + getString("editor_suffix_nora")),
            new LabeledItem("HOT_NORA",      getString("command_food_hot")    + getString("editor_suffix_nora")),
            new LabeledItem("VIYUGRA_NORA",  getString("command_food_viagra") + getString("editor_suffix_nora")),
            new LabeledItem("WASTE_NORA",    getString("command_food_garbage") + getString("editor_suffix_nora")),
            new LabeledItem("SWEETS_YASEI1", getString("command_food_sweet1") + getString("editor_suffix_yasei")),
            new LabeledItem("SWEETS_YASEI2", getString("command_food_sweet2") + getString("editor_suffix_yasei")),
            new LabeledItem("FOOD_YASEI",    getString("command_food_normal") + getString("editor_suffix_yasei")),
            new LabeledItem("BITTER_YASEI",  getString("command_food_bitter") + getString("editor_suffix_yasei")),
            new LabeledItem("LEMONPOP_YASEI", getString("command_food_ramune") + getString("editor_suffix_yasei")),
            new LabeledItem("HOT_YASEI",     getString("command_food_hot")    + getString("editor_suffix_yasei")),
            new LabeledItem("VIYUGRA_YASEI", getString("command_food_viagra") + getString("editor_suffix_yasei")),
            new LabeledItem("WASTE_YASEI",   getString("command_food_garbage") + getString("editor_suffix_yasei")),
            new LabeledItem("VOMIT",         getString("game_toan")),
            new LabeledItem("SHIT",          getString("item_veryshit")),
            new LabeledItem("BODY",          getString("editor_food_body")),
        };
    }

    static String[] makeYunbaColorItems() {
        String[] src = Yunba.getColorList();
        if (src != null) {
            return src;
        }
        return new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8" };
    }

    static LabeledItem[] makeLiquidTypeItems() {
        List<LabeledItem> list = new ArrayList<LabeledItem>();
        list.add(new LabeledItem("-1", getString("editor_liquid_auto")));
        for (YukkuriType t : NON_HYBRID_TYPES) {
            list.add(new LabeledItem(String.valueOf(t.getTypeId()), t.getJapaneseName()));
        }
        return list.toArray(new LabeledItem[0]);
    }

    /** おかざり種類リスト（インデックス0=なし、1=DEFAULT、2以降=OkazariType.values()順）*/
    static String[] makeOkazariDisplay() {
        OkazariType[] types = OkazariType.values();
        String[] arr = new String[types.length + 1];
        arr[0] = getString("editor_okazari_none");
        for (int i = 0; i < types.length; i++) {
            switch (types[i]) {
                case DEFAULT:
                    arr[i + 1] = getString("editor_okazari_default");
                    break;
                case BABY1:
                    arr[i + 1] = getString("editor_okazari_baby1");
                    break;
                case BABY2:
                    arr[i + 1] = getString("editor_okazari_baby2");
                    break;
                case CHILD1:
                    arr[i + 1] = getString("editor_okazari_child1");
                    break;
                case CHILD2:
                    arr[i + 1] = getString("editor_okazari_child2");
                    break;
                case ADULT1:
                    arr[i + 1] = getString("editor_okazari_adult1");
                    break;
                case ADULT2:
                    arr[i + 1] = getString("editor_okazari_adult2");
                    break;
                case ADULT3:
                    arr[i + 1] = getString("editor_okazari_adult3");
                    break;
                default:
                    arr[i + 1] = types[i].name();
                    break;
            }
        }
        return arr;
    }

    // ── ゆっくり種類リスト（ハイブリッド別扱い） ───────────────────

    static final YukkuriType[] NON_HYBRID_TYPES;

    static {
        List<YukkuriType> list = new ArrayList<YukkuriType>();
        for (YukkuriType t : YukkuriType.values()) {
            if (t != YukkuriType.HYBRIDYUKKURI) {
                list.add(t);
            }
        }
        list.sort(new Comparator<YukkuriType>() {
            @Override
            public int compare(YukkuriType a, YukkuriType b) {
                return Integer.compare(a.getTypeId(), b.getTypeId());
            }
        });
        NON_HYBRID_TYPES = list.toArray(new YukkuriType[0]);
    }

    // ── オブジェクト種類（第2列はプロパティキー） ─────────────────

    static final String[][] OBJECT_TYPES = {
        {"Food",           "editor_obj_food"},
        {"Toilet",         "editor_obj_toilet"},
        {"Bed",            "editor_obj_bed"},
        {"Toy",            "editor_obj_toy"},
        {"Stone",          "editor_obj_stone"},
        {"House",          "editor_obj_house"},
        {"Diffuser",       "editor_obj_diffuser"},
        {"HotPlate",       "editor_obj_hotplate"},
        {"StickyPlate",    "editor_obj_stickyplate"},
        {"ProcessorPlate", "editor_obj_processorplate"},
        {"BreedingPool",   "editor_obj_breedingpool"},
        {"OrangePool",     "editor_obj_orangepool"},
        {"Trampoline",     "editor_obj_trampoline"},
        {"FoodMaker",      "editor_obj_foodmaker"},
        {"AutoFeeder",     "editor_obj_autofeeder"},
        {"MachinePress",   "editor_obj_machinepress"},
        {"Yunba",          "editor_obj_yunba"},
    };

    // ── ゆっくり種類ラベル ──────────────────────────────────────

    static String yukkuriLabel(String simpleClassName) {
        YukkuriType t = YukkuriType.fromClassName(simpleClassName);
        return (t != null) ? t.getJapaneseName() : simpleClassName;
    }

    // ── % ヘルパー ─────────────────────────────────────────────

    static int pct(int val, int max) {
        if (max <= 0) {
            return 0;
        }
        return Math.max(0, Math.min(100, val * 100 / max));
    }

    static int hungerPct(Yukkuri y) {
        if (y.getAgeState() == null) {
            return 0;
        }
        return pct(y.getHungry(), y.getHungryLimit());
    }

    static int hpPct(Yukkuri y) {
        if (y.getAgeState() == null) {
            return 100;
        }
        int lim = y.getDamageLimit();
        return pct(lim - y.getDamage(), lim);
    }

    static int stressPct(Yukkuri y) {
        if (y.getAgeState() == null) {
            return 0;
        }
        return pct(y.getStress(), y.getStressLimit());
    }

    // ── ID リスト表示ヘルパー ──────────────────────────────────

    static String idListText(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return getString("editor_none");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(ids.get(i));
        }
        return sb.toString();
    }

    static String carryTypeLabel(TakeoutItemType t) {
        switch (t) {
            case YUKKURI: return getString("editor_carry_yukkuri");
            case FOOD:    return getString("editor_carry_food");
            case SHIT:    return getString("editor_carry_shit");
            case TOY:     return getString("editor_carry_toy");
            default:      return t.name();
        }
    }

    static String favTypeLabel(FavItemType t) {
        switch (t) {
            case BALL: return getString("editor_fav_ball");
            case BED:  return getString("editor_fav_bed");
            case SUI:  return getString("editor_fav_sui");
            default:   return t.name();
        }
    }

    //
    // main
    //
    /** エディターを起動する。
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        for (String arg : args) {
            if ("--lang=en".equals(arg)) {
                GameLocale.setOverride(new LocaleSource() {
                    @Override
                    public Locale getLocale() {
                        return Locale.ENGLISH;
                    }
                });
            } else if ("--lang=ja".equals(arg)) {
                GameLocale.setOverride(new LocaleSource() {
                    @Override
                    public Locale getLocale() {
                        return Locale.JAPANESE;
                    }
                });
            }
        }
        Translate.setWorldSize(1000, 1000, 200);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            /* ignore */
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new EditorFrame().setVisible(true);
            }
        });
    }

    //
    // メインフレーム
    //
    static class EditorFrame extends JFrame {

        private World world;
        private WorldState currentWorldState;
        private File currentFile;
        private File lastDir = new File(System.getProperty("user.home"));

        private final JComboBox<String> fieldSelector = new JComboBox<>();
        private final YukkuriTableModel yukkuriModel  = new YukkuriTableModel();
        private final ObjectTableModel  objectModel   = new ObjectTableModel();
        private final YukkuriEditPanel  yukkuriEditPanel = new YukkuriEditPanel();
        private final ObjectEditPanel   objectEditPanel  = new ObjectEditPanel();
        private final FieldEditPanel    fieldEditPanel   = new FieldEditPanel();
        private final PlayerEditPanel   playerEditPanel  = new PlayerEditPanel();
        private final WorldEditPanel    worldEditPanel   = new WorldEditPanel();
        private JTable yukkuriTable;
        private JTable objectTable;
        private final DefaultListModel<FieldEntry> fieldListModel = new DefaultListModel<FieldEntry>();
        private final JList<FieldEntry> fieldList = new JList<FieldEntry>(fieldListModel);
        private JTabbedPane tabs;
        private JLabel statusBar;
        private TableRowSorter<YukkuriTableModel> yukkuriSorter;
        private TableRowSorter<ObjectTableModel>  objectSorter;

        EditorFrame() {
            super(getString("editor_title"));
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1300, 850);
            setLocationRelativeTo(null);
            buildUi();
        }

        private void buildUi() {
            // メニューバー
            JMenuItem openItem   = new JMenuItem(getString("editor_menu_open"));
            JMenuItem saveItem   = new JMenuItem(getString("editor_menu_save"));
            JMenuItem saveAsItem = new JMenuItem(getString("editor_menu_saveas"));
            openItem.addActionListener(e -> onOpen());
            saveItem.addActionListener(e -> onSave());
            saveAsItem.addActionListener(e -> onSaveAs());
            JMenuItem exitItem   = new JMenuItem(getString("editor_menu_exit"));
            exitItem.addActionListener(e -> System.exit(0));
            JMenu fileMenu = new JMenu(getString("editor_menu_file"));
            fileMenu.add(openItem);
            fileMenu.add(saveItem);
            fileMenu.add(saveAsItem);
            fileMenu.addSeparator();
            fileMenu.add(exitItem);
            JMenuBar menuBar = new JMenuBar();
            menuBar.add(fileMenu);
            setJMenuBar(menuBar);

            // フィールド選択バー
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
            topPanel.add(new JLabel(getString("editor_field_label")));
            fieldSelector.setPreferredSize(new Dimension(200, 26));
            fieldSelector.addActionListener(e -> onFieldSelected());
            topPanel.add(fieldSelector);
            add(topPanel, BorderLayout.NORTH);

            // スプリットペイン
            JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            split.setDividerLocation(680);
            split.setResizeWeight(0.6);

            // 左: タブ
            tabs = new JTabbedPane();

            // ゆっくりタブ
            yukkuriTable = new JTable(yukkuriModel);
            yukkuriTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            yukkuriTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            applyColumnWidths(yukkuriTable, new int[]{50, 100, 55, 55, 60, 55, 82, 55, 72, 72});
            yukkuriSorter = new TableRowSorter<>(yukkuriModel);
            yukkuriTable.setRowSorter(yukkuriSorter);
            DefaultTableCellRenderer pctRenderer = new DefaultTableCellRenderer() {
                @Override
                protected void setValue(Object v) {
                    setText(v instanceof Integer ? v + "%" : (v != null ? v.toString() : ""));
                }
            };
            pctRenderer.setHorizontalAlignment(JLabel.CENTER);
            yukkuriTable.getColumnModel().getColumn(4).setCellRenderer(pctRenderer);
            yukkuriTable.getColumnModel().getColumn(5).setCellRenderer(pctRenderer);
            yukkuriTable.getColumnModel().getColumn(6).setCellRenderer(pctRenderer);
            yukkuriTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    int row = yukkuriTable.getSelectedRow();
                    if (row >= 0) {
                        row = yukkuriTable.convertRowIndexToModel(row);
                    }
                    yukkuriEditPanel.setYukkuri(row >= 0 ? yukkuriModel.getYukkuri(row) : null);
                }
            });

            JTextField yukkuriFilter = new JTextField();
            yukkuriFilter.setPreferredSize(new Dimension(160, 24));
            yukkuriFilter.getDocument().addDocumentListener(makeFilterListener(() -> {
                String s = yukkuriFilter.getText().trim();
                yukkuriSorter.setRowFilter(
                        s.isEmpty() ? null : makeContainsFilter(s, yukkuriModel.getColumnCount()));
            }));

            JButton addYukkuriBtn = new JButton(getString("editor_btn_add"));
            addYukkuriBtn.addActionListener(e -> onAddYukkuri());
            JButton delYukkuriBtn = new JButton(getString("editor_btn_delete"));
            delYukkuriBtn.addActionListener(e -> onDeleteYukkuri());
            JPanel yukkuriHead = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
            yukkuriHead.add(addYukkuriBtn);
            yukkuriHead.add(delYukkuriBtn);
            JLabel yukkuriFilterLabel = new JLabel(getString("editor_search"));
            yukkuriHead.add(yukkuriFilterLabel);
            yukkuriHead.add(yukkuriFilter);
            JPanel yukkuriPanel = new JPanel(new BorderLayout());
            yukkuriPanel.add(yukkuriHead, BorderLayout.NORTH);
            yukkuriPanel.add(new JScrollPane(yukkuriTable), BorderLayout.CENTER);
            tabs.addTab(getString("editor_tab_yukkuri") + " (0)", yukkuriPanel);

            // オブジェクトタブ
            objectTable = new JTable(objectModel);
            objectTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            objectTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            applyColumnWidths(objectTable, new int[]{60, 160, 65, 65});
            objectSorter = new TableRowSorter<>(objectModel);
            objectTable.setRowSorter(objectSorter);
            objectTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    int row = objectTable.getSelectedRow();
                    if (row >= 0) {
                        row = objectTable.convertRowIndexToModel(row);
                    }
                    objectEditPanel.setEntity(row >= 0 ? objectModel.getEntry(row).entity : null);
                }
            });

            JTextField objectFilter = new JTextField();
            objectFilter.setPreferredSize(new Dimension(160, 24));
            objectFilter.getDocument().addDocumentListener(makeFilterListener(() -> {
                String s = objectFilter.getText().trim();
                objectSorter.setRowFilter(
                        s.isEmpty() ? null : makeContainsFilter(s, objectModel.getColumnCount()));
            }));

            JButton addObjectBtn = new JButton(getString("editor_btn_add"));
            addObjectBtn.addActionListener(e -> onAddObject());
            JButton delObjectBtn = new JButton(getString("editor_btn_delete"));
            delObjectBtn.addActionListener(e -> onDeleteObject());
            JPanel objectHead = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
            objectHead.add(addObjectBtn);
            objectHead.add(delObjectBtn);
            objectHead.add(new JLabel(getString("editor_search")));
            objectHead.add(objectFilter);
            JPanel objectPanel = new JPanel(new BorderLayout());
            objectPanel.add(objectHead, BorderLayout.NORTH);
            objectPanel.add(new JScrollPane(objectTable), BorderLayout.CENTER);
            tabs.addTab(getString("editor_tab_object") + " (0)", objectPanel);

            // フィールドシェイプタブ
            fieldList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            fieldList.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    int idx = fieldList.getSelectedIndex();
                    fieldEditPanel.setEntry(idx >= 0 ? fieldListModel.getElementAt(idx) : null);
                }
            });
            JButton delFieldBtn = new JButton(getString("editor_btn_delete"));
            delFieldBtn.addActionListener(e -> onDeleteField());
            JPanel fieldHead = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
            fieldHead.add(delFieldBtn);
            JPanel fieldTabPanel = new JPanel(new BorderLayout());
            fieldTabPanel.add(fieldHead, BorderLayout.NORTH);
            fieldTabPanel.add(new JScrollPane(fieldList), BorderLayout.CENTER);
            tabs.addTab(getString("editor_tab_field") + " (0)", fieldTabPanel);

            split.setLeftComponent(tabs);

            // プレイヤータブ (左)
            JPanel playerTabPanel = new JPanel(new BorderLayout());
            JLabel playerHint = new JLabel(getString("editor_player_hint"), JLabel.CENTER);
            playerHint.setForeground(Color.GRAY);
            playerTabPanel.add(playerHint, BorderLayout.CENTER);
            tabs.addTab(getString("editor_tab_player"), playerTabPanel);

            JPanel worldTabPanel = new JPanel(new BorderLayout());
            JLabel worldHint = new JLabel(getString("editor_world_hint"), JLabel.CENTER);
            worldHint.setForeground(Color.GRAY);
            worldTabPanel.add(worldHint, BorderLayout.CENTER);
            tabs.addTab(getString("editor_tab_world"), worldTabPanel);

            // 右: 編集パネル
            final JPanel rightPanel = new JPanel(new CardLayout());
            rightPanel.add(yukkuriEditPanel, "yukkuri");
            rightPanel.add(objectEditPanel, "object");
            rightPanel.add(fieldEditPanel,  "field");
            rightPanel.add(playerEditPanel, "player");
            rightPanel.add(worldEditPanel,  "world");
            tabs.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    CardLayout cl = (CardLayout) rightPanel.getLayout();
                    int ti = tabs.getSelectedIndex();
                    String key = ti == 0 ? "yukkuri"
                            : ti == 1 ? "object"
                            : ti == 2 ? "field"
                            : ti == 3 ? "player"
                            : "world";
                    cl.show(rightPanel, key);
                }
            });
            split.setRightComponent(rightPanel);
            add(split, BorderLayout.CENTER);

            // ステータスバー
            statusBar = new JLabel("  " + getString("editor_status_default"));
            statusBar.setBorder(BorderFactory.createEtchedBorder());
            add(statusBar, BorderLayout.SOUTH);
            yukkuriEditPanel.setStatusLabel(statusBar);
            objectEditPanel.setStatusLabel(statusBar);
            fieldEditPanel.setStatusLabel(statusBar);
            playerEditPanel.setStatusLabel(statusBar);
            worldEditPanel.setStatusLabel(statusBar);
        }

        private void applyColumnWidths(JTable t, int[] widths) {
            for (int i = 0; i < widths.length && i < t.getColumnCount(); i++) {
                t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            }
        }

        private DocumentListener makeFilterListener(Runnable update) {
            return new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    update.run();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    update.run();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    update.run();
                }
            };
        }

        private <M extends AbstractTableModel> RowFilter<M, Integer> makeContainsFilter(
                String text, int colCount) {
            final String lower = text.toLowerCase();
            return new RowFilter<M, Integer>() {
                @Override
                public boolean include(Entry<? extends M, ? extends Integer> entry) {
                    for (int i = 0; i < colCount; i++) {
                        Object v = entry.getValue(i);
                        if (v != null && v.toString().toLowerCase().contains(lower)) {
                            return true;
                        }
                    }
                    return false;
                }
            };
        }

        // ── ファイル操作 ─────────────────────────────────────────

        private void onOpen() {
            JFileChooser fc = new JFileChooser(currentFile != null ? currentFile.getParentFile() : lastDir);
            fc.setDialogTitle(getString("editor_dlg_open"));
            if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            try {
                currentFile = fc.getSelectedFile();
                lastDir = currentFile.getParentFile();
                world = SaveDataCodec.load(currentFile);
                applyWorldSize(world);
                playerEditPanel.setPlayer(world.getPlayer());
                worldEditPanel.setWorld(world);
                refreshFieldSelector();
                setTitle(getString("editor_title") + "  —  " + currentFile.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        getString("editor_err_load") + "\n" + ex.getMessage(),
                        getString("editor_err_title"), JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }

        private void applyWorldSize(World w) {
            int wt = Math.max(0, Math.min(w.getWindowType(), SimYukkuri.PAINT_PANE_X.length - 1));
            int si = Math.max(0, Math.min(w.getTerrariumSizeIndex(), SimYukkuri.fieldScaleData.length - 1));
            Translate.setCanvasSize(
                    SimYukkuri.PAINT_PANE_X[wt],
                    SimYukkuri.PAINT_PANE_Y[wt],
                    SimYukkuri.fieldScaleData[si],
                    SimYukkuri.bufferSizeData[si],
                    SimYukkuri.fieldZoomRate[si]);
            // windowType=1 (1260x980) は小さいエディターではレイアウトが崩れるので拡張する
            int minW = SimYukkuri.PAINT_PANE_X[wt] + 300;
            int minH = SimYukkuri.PAINT_PANE_Y[wt] + 150;
            if (getWidth() < minW || getHeight() < minH) {
                setSize(Math.max(getWidth(), minW), Math.max(getHeight(), minH));
            }
        }

        private void onSave() {
            if (world == null || currentFile == null) {
                onSaveAs();
                return;
            }
            if (JOptionPane.showConfirmDialog(this,
                    getString("editor_dlg_save_confirm"), getString("editor_dlg_save_title"),
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
            try {
                SaveDataCodec.save(world, currentFile);
                statusBar.setText("  " + String.format(getString("editor_status_saved"), currentFile.getName()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        getString("editor_err_save") + "\n" + ex.getMessage(),
                        getString("editor_err_title"), JOptionPane.ERROR_MESSAGE);
            }
        }

        private void onSaveAs() {
            if (world == null) {
                JOptionPane.showMessageDialog(this, getString("editor_nodata"));
                return;
            }
            JFileChooser fc = new JFileChooser(currentFile != null ? currentFile.getParentFile() : lastDir);
            fc.setDialogTitle(getString("editor_dlg_saveas"));
            if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            currentFile = fc.getSelectedFile();
            lastDir = currentFile.getParentFile();
            onSave();
        }

        // ── フィールド選択 ────────────────────────────────────────

        private static String fieldName(int i) {
            switch (i) {
                case 0: return GameText.read("system_myroom");
                case 1: return GameText.read("system_yard");
                case 2: return GameText.read("system_road");
                case 3: return GameText.read("system_park1");
                case 4: return GameText.read("system_park2");
                case 5: return GameText.read("system_forest1");
                case 6: return GameText.read("system_forest2");
                case 7: return GameText.read("system_plant1");
                case 8: return GameText.read("system_plant2");
                case 9: return GameText.read("system_disposer");
                default: return getString("editor_field_label") + " " + (i + 1);
            }
        }

        private void refreshFieldSelector() {
            fieldSelector.removeAllItems();
            if (world == null) {
                return;
            }
            List<WorldState> states = world.getWorldStates();
            if (states == null) {
                return;
            }
            for (int i = 0; i < states.size(); i++) {
                fieldSelector.addItem(fieldName(i));
            }
            onFieldSelected();
        }

        private void onFieldSelected() {
            if (world == null) {
                return;
            }
            List<WorldState> states = world.getWorldStates();
            if (states == null) {
                return;
            }
            int idx = fieldSelector.getSelectedIndex();
            if (idx < 0 || idx >= states.size()) {
                return;
            }
            currentWorldState = states.get(idx);
            refreshTables();
        }

        void refreshTables() {
            if (currentWorldState == null) {
                return;
            }
            List<Yukkuri> yuks = new ArrayList<Yukkuri>(currentWorldState.getYukkuriRegistry().values());
            yukkuriModel.setData(yuks);
            yukkuriEditPanel.setWorldState(currentWorldState);
            yukkuriEditPanel.setYukkuri(null);

            List<ObjectEntry> objs = collectObjects(currentWorldState);
            objectModel.setData(objs);
            objectEditPanel.setEntity(null);

            fieldListModel.clear();
            fieldEditPanel.setEntry(null);
            List<Barrier> barriers = currentWorldState.getBarriers();
            if (barriers != null) {
                for (Barrier b : barriers) {
                    fieldListModel.addElement(new FieldEntry(b, barriers, currentWorldState));
                }
            }
            List<Farm> farms = currentWorldState.getFarms();
            if (farms != null) {
                for (Farm f : farms) {
                    fieldListModel.addElement(new FieldEntry(f, farms, currentWorldState));
                }
            }
            List<Pool> pools = currentWorldState.getPools();
            if (pools != null) {
                for (Pool p : pools) {
                    fieldListModel.addElement(new FieldEntry(p, pools, currentWorldState));
                }
            }

            tabs.setTitleAt(0, getString("editor_tab_yukkuri") + " (" + yuks.size() + ")");
            tabs.setTitleAt(1, getString("editor_tab_object") + " (" + objs.size() + ")");
            tabs.setTitleAt(2, getString("editor_tab_field") + " (" + fieldListModel.getSize() + ")");
        }

        // ── ゆっくり追加/削除 ─────────────────────────────────────

        private void onAddYukkuri() {
            if (currentWorldState == null) {
                noDataWarn();
                return;
            }

            List<String> displayList = new ArrayList<String>();
            displayList.add(getString("editor_hybrid_label") + "  (HybridYukkuri)");
            for (YukkuriType t : NON_HYBRID_TYPES) {
                displayList.add(t.getJapaneseName() + "  (" + t.getClassName() + ")");
            }
            final JComboBox<String> typeBox = new JComboBox<>(displayList.toArray(new String[0]));
            NumericField xf = new NumericField(6);
            xf.setText("500");
            NumericField yf = new NumericField(6);
            yf.setText("500");

            final JLabel p1Lbl = new JLabel(getString("editor_dlg_parent1"));
            final JLabel p2Lbl = new JLabel(getString("editor_dlg_parent2"));
            String[] nonHybridNames = new String[NON_HYBRID_TYPES.length];
            for (int i = 0; i < NON_HYBRID_TYPES.length; i++) {
                nonHybridNames[i] = NON_HYBRID_TYPES[i].getJapaneseName() + "  (" + NON_HYBRID_TYPES[i].getClassName() + ")";
            }
            final JComboBox<String> parent1Box = new JComboBox<>(nonHybridNames);
            final JComboBox<String> parent2Box = new JComboBox<>(nonHybridNames);

            final JPanel dlg = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(4, 6, 4, 6);
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;
            addDlgRow(dlg, c, 0, getString("editor_dlg_type"), typeBox);
            addDlgRow(dlg, c, 1, getString("editor_lbl_x"), xf);
            addDlgRow(dlg, c, 2, getString("editor_lbl_y"), yf);
            addDlgRow(dlg, c, 3, p1Lbl, parent1Box);
            addDlgRow(dlg, c, 4, p2Lbl, parent2Box);

            updateHybridVisibility(typeBox.getSelectedIndex() == 0, p1Lbl, parent1Box, p2Lbl, parent2Box);
            typeBox.addActionListener(ev ->
                    updateHybridVisibility(typeBox.getSelectedIndex() == 0,
                            p1Lbl, parent1Box, p2Lbl, parent2Box));

            if (JOptionPane.showConfirmDialog(this, dlg, getString("editor_dlg_add_yukkuri"),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
                return;
            }

            try {
                boolean isHybrid = typeBox.getSelectedIndex() == 0;
                final int x = parseInt(xf, 500);
                final int y = parseInt(yf, 500);
                Yukkuri newY;
                String label;

                if (isHybrid) {
                    HybridYukkuri hy = new HybridYukkuri();
                    YukkuriType t1 = NON_HYBRID_TYPES[parent1Box.getSelectedIndex()];
                    YukkuriType t2 = NON_HYBRID_TYPES[parent2Box.getSelectedIndex()];
                    Yukkuri d1 = createYukkuri(t1.getClassName());
                    Yukkuri d2 = createYukkuri(t2.getClassName());
                    setField(hy, "dorei",  d1);
                    setField(hy, "dorei2", d2);
                    setField(hy, "dorei3", d1);
                    setField(hy, "dorei4", d2);
                    newY = hy;
                    label = getString("editor_hybrid_label") + "(" + t1.getJapaneseName() + "×" + t2.getJapaneseName() + ")";
                } else {
                    YukkuriType t = NON_HYBRID_TYPES[typeBox.getSelectedIndex() - 1];
                    newY = createYukkuri(t.getClassName());
                    label = t.getJapaneseName();
                }

                int newId = nextId(currentWorldState);
                newY.setUniqueId(newId);
                newY.setObjId(newId);
                newY.setX(x);
                newY.setY(y);
                currentWorldState.getYukkuriRegistry().put(newId, newY);
                refreshTables();
                statusBar.setText("  " + String.format(getString("editor_status_added"), label, newId));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        getString("editor_err_add") + "\n" + ex.getMessage(),
                        getString("editor_err_title"), JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }

        private void updateHybridVisibility(boolean hybrid,
                JLabel l1, JComboBox<String> b1, JLabel l2, JComboBox<String> b2) {
            l1.setVisible(hybrid);
            b1.setVisible(hybrid);
            l2.setVisible(hybrid);
            b2.setVisible(hybrid);
        }

        @SuppressWarnings("unchecked")
        private Yukkuri createYukkuri(String simpleClassName) throws Exception {
            String fullName = "org.simyukkuri.entity.core.living.yukkuri.impl." + simpleClassName;
            Class<? extends Yukkuri> cls = (Class<? extends Yukkuri>) Class.forName(fullName);
            return cls.newInstance();
        }

        private void setField(Object obj, String fieldName, Object value) throws Exception {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(obj, value);
        }

        private void onDeleteYukkuri() {
            if (currentWorldState == null) {
                noDataWarn();
                return;
            }
            int[] rows = yukkuriTable.getSelectedRows();
            if (rows.length == 0) {
                JOptionPane.showMessageDialog(this, getString("editor_err_select_yukkuri"));
                return;
            }
            List<Yukkuri> toDelete = new ArrayList<>();
            for (int r : rows) {
                toDelete.add(yukkuriModel.getYukkuri(yukkuriTable.convertRowIndexToModel(r)));
            }
            String msg;
            if (toDelete.size() == 1) {
                Yukkuri y = toDelete.get(0);
                msg = String.format(getString("editor_dlg_del_confirm"), yukkuriLabel(y.getClass().getSimpleName()), y.getUniqueId());
            } else {
                msg = String.format(getString("editor_dlg_del_multi_confirm"), toDelete.size());
            }
            if (JOptionPane.showConfirmDialog(this, msg, getString("editor_dlg_del_title"),
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
            for (Yukkuri y : toDelete) {
                currentWorldState.getYukkuriRegistry().remove(y.getUniqueId());
            }
            refreshTables();
            if (toDelete.size() == 1) {
                Yukkuri y = toDelete.get(0);
                String dname = yukkuriLabel(y.getClass().getSimpleName());
                statusBar.setText("  " + String.format(getString("editor_status_deleted"), dname, y.getUniqueId()));
            } else {
                statusBar.setText("  " + String.format(getString("editor_status_deleted_multi"), toDelete.size()));
            }
        }

        // ── オブジェクト追加/削除 ─────────────────────────────────

        private void onAddObject() {
            if (currentWorldState == null) {
                noDataWarn();
                return;
            }
            String[] displayNames = new String[OBJECT_TYPES.length];
            for (int i = 0; i < OBJECT_TYPES.length; i++) {
                displayNames[i] = getString(OBJECT_TYPES[i][1]) + "  (" + OBJECT_TYPES[i][0] + ")";
            }
            NumericField xf = new NumericField(6);
            xf.setText("500");
            NumericField yf = new NumericField(6);
            yf.setText("500");
            final JComboBox<String> typeBox = new JComboBox<>(displayNames);
            final JPanel dlg = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(4, 6, 4, 6);
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;
            addDlgRow(dlg, c, 0, getString("editor_dlg_type"), typeBox);
            addDlgRow(dlg, c, 1, getString("editor_lbl_x"), xf);
            addDlgRow(dlg, c, 2, getString("editor_lbl_y"), yf);

            if (JOptionPane.showConfirmDialog(this, dlg, getString("editor_dlg_add_object"),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
                return;
            }

            try {
                int sel = typeBox.getSelectedIndex();
                String className = "org.simyukkuri.entity.core.world.item." + OBJECT_TYPES[sel][0];
                Entity newE = (Entity) Class.forName(className).newInstance();
                int newId = nextId(currentWorldState);
                newE.setObjId(newId);
                newE.setX(parseInt(xf, 500));
                newE.setY(parseInt(yf, 500));
                putToWorldState(currentWorldState, OBJECT_TYPES[sel][0], newId, newE);
                refreshTables();
                statusBar.setText("  " + String.format(
                        getString("editor_status_added"), getString(OBJECT_TYPES[sel][1]), newId));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        getString("editor_err_add") + "\n" + ex.getMessage(),
                        getString("editor_err_title"), JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        private void putToWorldState(WorldState ws, String className, int id, Entity e) {
            Map m;
            switch (className) {
                case "Food":
                    m = ws.getFoods();
                    break;
                case "Toilet":
                    m = ws.getToilets();
                    break;
                case "Bed":
                    m = ws.getBeds();
                    break;
                case "Toy":
                    m = ws.getToys();
                    break;
                case "Stone":
                    m = ws.getStones();
                    break;
                case "House":
                    m = ws.getHouses();
                    break;
                case "Diffuser":
                    m = ws.getDiffusers();
                    break;
                case "HotPlate":
                    m = ws.getHotPlates();
                    break;
                case "StickyPlate":
                    m = ws.getStickyPlates();
                    break;
                case "ProcessorPlate":
                    m = ws.getProcessorPlates();
                    break;
                case "BreedingPool":
                    m = ws.getBreedingPools();
                    break;
                case "OrangePool":
                    m = ws.getOrangePools();
                    break;
                case "Trampoline":
                    m = ws.getTrampolines();
                    break;
                case "FoodMaker":
                    m = ws.getFoodMakers();
                    break;
                case "AutoFeeder":
                    m = ws.getAutoFeeders();
                    break;
                case "MachinePress":
                    m = ws.getMachinePresses();
                    break;
                case "Yunba":
                    m = ws.getYunbas();
                    break;
                default:
                    throw new IllegalArgumentException("unknown type: " + className);
            }
            if (m == null) {
                throw new IllegalStateException("map not initialized: " + className);
            }
            m.put(id, e);
        }

        private void onDeleteObject() {
            if (currentWorldState == null) {
                noDataWarn();
                return;
            }
            int[] rows = objectTable.getSelectedRows();
            if (rows.length == 0) {
                JOptionPane.showMessageDialog(this, getString("editor_err_select_object"));
                return;
            }
            List<ObjectEntry> toDelete = new ArrayList<>();
            for (int r : rows) {
                toDelete.add(objectModel.getEntry(objectTable.convertRowIndexToModel(r)));
            }
            String msg;
            if (toDelete.size() == 1) {
                Entity e = toDelete.get(0).entity;
                msg = String.format(getString("editor_dlg_del_confirm"), e.getClass().getSimpleName(), e.getObjId());
            } else {
                msg = String.format(getString("editor_dlg_del_multi_confirm"), toDelete.size());
            }
            if (JOptionPane.showConfirmDialog(this, msg, getString("editor_dlg_del_title"),
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
            for (ObjectEntry entry : toDelete) {
                entry.remove();
            }
            refreshTables();
            if (toDelete.size() == 1) {
                Entity e = toDelete.get(0).entity;
                statusBar.setText("  " + String.format(getString("editor_status_deleted"), e.getClass().getSimpleName(), e.getObjId()));
            } else {
                statusBar.setText("  " + String.format(getString("editor_status_deleted_multi"), toDelete.size()));
            }
        }

        private void onDeleteField() {
            if (currentWorldState == null) {
                noDataWarn();
                return;
            }
            int[] indices = fieldList.getSelectedIndices();
            if (indices.length == 0) {
                JOptionPane.showMessageDialog(this, getString("editor_err_select_object"));
                return;
            }
            List<FieldEntry> toDelete = new ArrayList<>();
            for (int i : indices) {
                toDelete.add(fieldListModel.getElementAt(i));
            }
            String msg;
            if (toDelete.size() == 1) {
                msg = String.format(getString("editor_dlg_del_field_confirm"), toDelete.get(0).toString());
            } else {
                msg = String.format(getString("editor_dlg_del_multi_confirm"), toDelete.size());
            }
            if (JOptionPane.showConfirmDialog(this, msg, getString("editor_dlg_del_title"),
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
            for (FieldEntry entry : toDelete) {
                entry.remove();
            }
            refreshTables();
            if (toDelete.size() == 1) {
                statusBar.setText("  " + getString("editor_status_field_deleted"));
            } else {
                statusBar.setText("  " + String.format(getString("editor_status_deleted_multi"), toDelete.size()));
            }
        }

        // ── ユーティリティ ────────────────────────────────────────

        private void noDataWarn() {
            JOptionPane.showMessageDialog(this, getString("editor_nodata"));
        }

        private void addDlgRow(JPanel p, GridBagConstraints c, int row, String label, JComponent field) {
            addDlgRow(p, c, row, new JLabel(label), field);
        }

        private void addDlgRow(JPanel p, GridBagConstraints c, int row, JLabel label, JComponent field) {
            c.gridx = 0;
            c.gridy = row;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.EAST;
            c.fill = GridBagConstraints.NONE;
            p.add(label, c);
            c.gridx = 1;
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;
            p.add(field, c);
        }

        private int nextId(WorldState ws) {
            int max = 0;
            for (int id : ws.getYukkuriRegistry().keySet()) {
                max = Math.max(max, id);
            }
            for (ObjectEntry oe : collectObjects(ws)) {
                max = Math.max(max, oe.entity.getObjId());
            }
            return max + 1;
        }

        List<ObjectEntry> collectObjects(WorldState ws) {
            List<ObjectEntry> objs = new ArrayList<ObjectEntry>();
            addEntries(objs, ws.getFoods());
            addEntries(objs, ws.getToilets());
            addEntries(objs, ws.getBeds());
            addEntries(objs, ws.getToys());
            addEntries(objs, ws.getStones());
            addEntries(objs, ws.getBreedingPools());
            addEntries(objs, ws.getOrangePools());
            addEntries(objs, ws.getHotPlates());
            addEntries(objs, ws.getStickyPlates());
            addEntries(objs, ws.getProcessorPlates());
            addEntries(objs, ws.getDiffusers());
            addEntries(objs, ws.getHouses());
            addEntries(objs, ws.getTrashObjects());
            addEntries(objs, ws.getFoodMakers());
            addEntries(objs, ws.getMachinePresses());
            addEntries(objs, ws.getTrampolines());
            addEntries(objs, ws.getAutoFeeders());
            addEntries(objs, ws.getYunbas());
            return objs;
        }

        private <T extends Entity> void addEntries(List<ObjectEntry> dest, Map<Integer, T> src) {
            if (src != null) {
                for (T e : src.values()) {
                    dest.add(new ObjectEntry(e, src));
                }
            }
        }
    }

    //
    // ゆっくりテーブルモデル
    //
    static class YukkuriTableModel extends AbstractTableModel {

        static final class OrdinalLabel implements Comparable<OrdinalLabel> {

            private final int ordinal;
            private final String label;

            OrdinalLabel(int ordinal, String label) {
                this.ordinal = ordinal;
                this.label = label;
            }

            @Override
            public int compareTo(OrdinalLabel other) {
                return Integer.compare(this.ordinal, other.ordinal);
            }

            @Override
            public String toString() {
                return label;
            }
        }

        private static final String[] COL_KEYS = {
            "editor_col_id", "editor_col_kind", "editor_col_x", "editor_col_y",
            "editor_col_hunger", "editor_col_hp", "editor_col_stress", "editor_col_growth",
            "editor_col_attitude", "editor_col_intelligence"
        };
        private List<Yukkuri> list = new ArrayList<Yukkuri>();

        void setData(List<Yukkuri> data) {
            list = data;
            fireTableDataChanged();
        }

        Yukkuri getYukkuri(int row) {
            return list.get(row);
        }

        @Override
        public int getRowCount() {
            return list.size();
        }

        @Override
        public int getColumnCount() {
            return COL_KEYS.length;
        }

        @Override
        public String getColumnName(int col) {
            return getString(COL_KEYS[col]);
        }

        @Override
        public Class<?> getColumnClass(int col) {
            switch (col) {
                case 0:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    return Integer.class;
                case 7:
                case 8:
                case 9:
                    return OrdinalLabel.class;
                default:
                    return String.class;
            }
        }

        @Override
        public Object getValueAt(int row, int col) {
            Yukkuri y = list.get(row);
            switch (col) {
                case 0: return y.getUniqueId();
                case 1: return yukkuriLabel(y.getClass().getSimpleName());
                case 2: return y.getX();
                case 3: return y.getY();
                case 4: return hungerPct(y);
                case 5: return hpPct(y);
                case 6: return stressPct(y);
                case 7: return ageOrdinalLabel(y.getAgeState());
                case 8: return attitudeOrdinalLabel(y.getAttitude());
                case 9: return intelligenceOrdinalLabel(y.getIntelligence());
                default: return "";
            }
        }

        private static OrdinalLabel ageOrdinalLabel(AgeState s) {
            return new OrdinalLabel(s == null ? -1 : s.ordinal(), ageLabel(s));
        }

        private static OrdinalLabel attitudeOrdinalLabel(org.simyukkuri.enums.Attitude a) {
            return new OrdinalLabel(a == null ? -1 : a.ordinal(), attitudeLabel(a));
        }

        private static OrdinalLabel intelligenceOrdinalLabel(org.simyukkuri.enums.Intelligence i) {
            return new OrdinalLabel(i == null ? -1 : i.ordinal(), intelligenceLabel(i));
        }

        private static String attitudeLabel(org.simyukkuri.enums.Attitude a) {
            if (a == null) {
                return "?";
            }
            switch (a) {
                case VERY_NICE:
                    return getString("editor_attitude_very_nice");
                case NICE:
                    return getString("editor_attitude_nice");
                case AVERAGE:
                    return getString("editor_attitude_average");
                case SHITHEAD:
                    return getString("editor_attitude_shithead");
                case SUPER_SHITHEAD:
                    return getString("editor_attitude_super_shithead");
                default:
                    return a.name();
            }
        }

        private static String intelligenceLabel(org.simyukkuri.enums.Intelligence i) {
            if (i == null) {
                return "?";
            }
            switch (i) {
                case WISE:
                    return getString("editor_intel_wise");
                case AVERAGE:
                    return getString("editor_intel_average");
                case FOOL:
                    return getString("editor_intel_fool");
                default:
                    return i.name();
            }
        }

        private static String ageLabel(AgeState s) {
            if (s == null) {
                return "?";
            }
            switch (s) {
                case BABY:
                    return getString("editor_age_baby");
                case CHILD:
                    return getString("editor_age_child");
                case ADULT:
                    return getString("editor_age_adult");
                default:
                    return s.name();
            }
        }
    }

    //
    // オブジェクトテーブルモデル
    //
    static class ObjectTableModel extends AbstractTableModel {

        private static final String[] COL_KEYS = {
            "editor_col_id", "editor_col_kind", "editor_col_x", "editor_col_y"
        };
        private List<ObjectEntry> list = new ArrayList<ObjectEntry>();

        void setData(List<ObjectEntry> data) {
            list = data;
            fireTableDataChanged();
        }

        ObjectEntry getEntry(int row) {
            return list.get(row);
        }

        @Override
        public int getRowCount() {
            return list.size();
        }

        @Override
        public int getColumnCount() {
            return COL_KEYS.length;
        }

        @Override
        public String getColumnName(int col) {
            return getString(COL_KEYS[col]);
        }

        @Override
        public Class<?> getColumnClass(int col) {
            return (col == 0 || col == 2 || col == 3) ? Integer.class : String.class;
        }

        @Override
        public Object getValueAt(int row, int col) {
            Entity e = list.get(row).entity;
            switch (col) {
                case 0: return e.getObjId();
                case 1: return e.getClass().getSimpleName();
                case 2: return e.getX();
                case 3: return e.getY();
                default: return "";
            }
        }
    }

    //
    // ゆっくり編集パネル
    //
    static class YukkuriEditPanel extends JPanel {

        private Yukkuri current;
        private WorldState worldState;
        private JLabel statusLabel;

        // 読み取り専用
        private final JLabel nameLabel         = new JLabel(getString("editor_unselected"));
        private final JLabel idDisplay         = new JLabel(getString("editor_dash"));
        private final JLabel hybridParentsLabel = new JLabel(getString("editor_dash"));
        private final JLabel eventLabel        = new JLabel(getString("editor_dash"));
        private final JLabel canActLabel       = new JLabel(getString("editor_dash"));
        private final JLabel carryLabel        = new JLabel(getString("editor_dash"));

        // 基本ステータス
        private final NumericField xField          = new NumericField(8);
        private final NumericField yField          = new NumericField(8);
        private final NumericField hungerPctField  = new NumericField(5);
        private final NumericField hpPctField      = new NumericField(5);
        private final NumericField stressPctField  = new NumericField(5);
        private final NumericField ageField        = new NumericField(10);
        private final JTextField   nameEditField   = new JTextField(12);
        private final NumericField ankoField       = new NumericField(8);
        private final NumericField sickPeriodField = new NumericField(8);
        private final NumericField attitudePtField = new NumericField(8);
        private final NumericField antCountField   = new NumericField(6);

        // 関係 (IDカンマ区切り or 単一ID)
        private final JTextField elderSisterField  = new JTextField(20);
        private final JTextField sisterField       = new JTextField(20);
        private final JTextField childrenField     = new JTextField(20);
        private final JTextField partnerField      = new JTextField(10);

        // おかざり（コンストラクタで初期化）
        private final JComboBox<String> okazariBox;

        // enum 選択（コンストラクタで初期化）
        private final JComboBox<LabeledItem> ageStateBox;
        private final JComboBox<LabeledItem> attitudeBox;
        private final JComboBox<LabeledItem> intelligenceBox;
        private final JComboBox<LabeledItem> happinessBox;
        private final JComboBox<LabeledItem> rankBox;

        // フラグ
        private final JCheckBox deadCheck       = new JCheckBox();
        private final JCheckBox burnedCheck     = new JCheckBox();
        private final JCheckBox crushedCheck    = new JCheckBox();
        private final JCheckBox pealedCheck     = new JCheckBox();
        private final JCheckBox packedCheck     = new JCheckBox();
        private final JCheckBox hasPantsCheck   = new JCheckBox();
        private final JCheckBox sleepCheck      = new JCheckBox();
        private final JCheckBox excitingCheck   = new JCheckBox();
        private final JCheckBox dirtyCheck      = new JCheckBox();
        private final JCheckBox ununSlaveCheck  = new JCheckBox();

        // お気に入りアイテム
        private final JLabel favBallLabel = new JLabel(getString("editor_dash"));
        private final JLabel favBedLabel  = new JLabel(getString("editor_dash"));
        private final JLabel favSuiLabel  = new JLabel(getString("editor_dash"));

        // アタッチメント
        private static final Class<?>[] ATTACH_CLASSES = {
            OrangeAmpoule.class, AccelAmpoule.class, StopAmpoule.class,
            HungryAmpoule.class, VeryShitAmpoule.class, PoisonAmpoule.class,
            BreedingAmpoule.class, AnydAmpoule.class,
            Needle.class, Ants.class, Badge.class, Fire.class,
        };
        private final DefaultListModel<Attachment> attachModel = new DefaultListModel<>();
        private final JList<Attachment> attachList = new JList<>(attachModel);

        YukkuriEditPanel() {
            // static initializer より後に実行されるのでL()が安全に使える
            ageStateBox     = new JComboBox<>(makeAgeStateItems());
            attitudeBox     = new JComboBox<>(makeAttitudeItems());
            intelligenceBox = new JComboBox<>(makeIntelItems());
            happinessBox    = new JComboBox<>(makeHappinessItems());
            rankBox         = new JComboBox<>(makeRankItems());
            okazariBox      = new JComboBox<>(makeOkazariDisplay());

            setLayout(new BorderLayout());
            JScrollPane scroll = new JScrollPane(buildForm());
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            scroll.setBorder(BorderFactory.createTitledBorder(getString("editor_panel_yukkuri")));
            add(scroll, BorderLayout.CENTER);
            setYukkuri(null);
        }

        void setStatusLabel(JLabel lbl) {
            this.statusLabel = lbl;
        }

        void setWorldState(WorldState ws) {
            this.worldState = ws;
        }

        private JPanel buildForm() {
            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(3, 6, 3, 6);
            c.anchor = GridBagConstraints.WEST;

            int row = 0;

            row = addSectionHeader(form, c, row, getString("editor_sec_identity"));
            row = addRow(form, c, row, getString("editor_lbl_kind"),           nameLabel);
            row = addRow(form, c, row, getString("editor_lbl_uid"),            idDisplay);
            row = addRow(form, c, row, getString("editor_lbl_hybrid_parents"), hybridParentsLabel);

            row = addSectionHeader(form, c, row, getString("editor_sec_position"));
            row = addRow(form, c, row, getString("editor_lbl_x"),    xField);
            row = addRow(form, c, row, getString("editor_lbl_y"),    yField);
            row = addRow(form, c, row, getString("editor_lbl_name"), nameEditField);

            row = addSectionHeader(form, c, row, getString("editor_sec_status"));
            row = addRow(form, c, row, getString("editor_lbl_hunger"),      hungerPctField);
            row = addRow(form, c, row, getString("editor_lbl_hp"),          hpPctField);
            row = addRow(form, c, row, getString("editor_lbl_stress"),      stressPctField);
            row = addRow(form, c, row, getString("editor_lbl_age"),         ageField);
            row = addRow(form, c, row, getString("editor_lbl_anko"),        ankoField);
            row = addRow(form, c, row, getString("editor_lbl_yukabi"),      sickPeriodField);
            row = addRow(form, c, row, getString("editor_lbl_attitude_pt"), attitudePtField);
            row = addRow(form, c, row, getString("editor_lbl_ant_count"),   antCountField);

            row = addSectionHeader(form, c, row, getString("editor_sec_attr"));
            row = addRow(form, c, row, getString("editor_lbl_age_state"),   ageStateBox);
            row = addRow(form, c, row, getString("editor_lbl_attitude"),    attitudeBox);
            row = addRow(form, c, row, getString("editor_lbl_intelligence"), intelligenceBox);
            row = addRow(form, c, row, getString("editor_lbl_happiness"),   happinessBox);
            row = addRow(form, c, row, getString("editor_lbl_rank"),        rankBox);

            row = addSectionHeader(form, c, row, getString("editor_sec_equip"));
            row = addRow(form, c, row, getString("editor_lbl_okazari"), okazariBox);

            row = addSectionHeader(form, c, row, getString("editor_sec_family"));
            row = addRow(form, c, row, getString("editor_lbl_partner"),       partnerField);
            row = addRow(form, c, row, getString("editor_lbl_elder_sisters"), elderSisterField);
            row = addRow(form, c, row, getString("editor_lbl_sisters"),       sisterField);
            row = addRow(form, c, row, getString("editor_lbl_children"),      childrenField);

            row = addSectionHeader(form, c, row, getString("editor_sec_flags"));
            row = addRow(form, c, row, getString("editor_lbl_dead"),     deadCheck);
            row = addRow(form, c, row, getString("editor_lbl_burned"),   burnedCheck);
            row = addRow(form, c, row, getString("editor_lbl_crushed"),  crushedCheck);
            row = addRow(form, c, row, getString("editor_lbl_pealed"),   pealedCheck);
            row = addRow(form, c, row, getString("editor_lbl_packed"),   packedCheck);
            row = addRow(form, c, row, getString("editor_lbl_pants"),    hasPantsCheck);
            row = addRow(form, c, row, getString("editor_lbl_sleeping"),  sleepCheck);
            row = addRow(form, c, row, getString("editor_lbl_exciting"),  excitingCheck);
            row = addRow(form, c, row, getString("editor_lbl_dirty"),     dirtyCheck);
            row = addRow(form, c, row, getString("editor_lbl_unun_slave"), ununSlaveCheck);

            row = addSectionHeader(form, c, row, getString("editor_sec_state"));
            row = addRow(form, c, row, getString("editor_lbl_event"),   eventLabel);
            row = addRow(form, c, row, getString("editor_lbl_can_act"), canActLabel);

            row = addSectionHeader(form, c, row, getString("editor_sec_carry"));
            JPanel carryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            carryPanel.add(carryLabel);
            JButton clearCarryBtn = new JButton(getString("editor_btn_clear_all"));
            clearCarryBtn.addActionListener(e -> {
                if (current != null) {
                    current.getCarryItems().clear();
                    refreshCarry();
                }
            });
            carryPanel.add(clearCarryBtn);
            row = addRowRaw(form, c, row, getString("editor_lbl_carry"), carryPanel);

            row = addSectionHeader(form, c, row, getString("editor_sec_fav"));
            row = addFavRow(form, c, row, getString("editor_lbl_fav_ball"), favBallLabel, FavItemType.BALL);
            row = addFavRow(form, c, row, getString("editor_lbl_fav_bed"),  favBedLabel,  FavItemType.BED);
            row = addFavRow(form, c, row, getString("editor_lbl_fav_sui"),  favSuiLabel,  FavItemType.SUI);

            row = addSectionHeader(form, c, row, getString("editor_sec_attach"));
            attachList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            JScrollPane attachScroll = new JScrollPane(attachList);
            attachScroll.setPreferredSize(new Dimension(300, 100));
            c.gridx = 0;
            c.gridy = row;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1.0;
            c.weighty = 0.2;
            form.add(attachScroll, c);
            c.weighty = 0;
            c.fill = GridBagConstraints.NONE;
            c.weightx = 0;
            row++;
            JButton addAttachBtn = new JButton(getString("editor_btn_add"));
            addAttachBtn.addActionListener(e -> onAddAttachment());
            JButton delAttachBtn = new JButton(getString("editor_btn_delete"));
            delAttachBtn.addActionListener(e -> onRemoveAttachment());
            JPanel attachBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            attachBtnPanel.add(addAttachBtn);
            attachBtnPanel.add(delAttachBtn);
            c.gridx = 0;
            c.gridy = row;
            c.gridwidth = 2;
            c.anchor = GridBagConstraints.WEST;
            form.add(attachBtnPanel, c);
            row++;

            JButton applyBtn = new JButton(getString("editor_btn_apply"));
            applyBtn.addActionListener(e -> apply());
            c.gridx = 0;
            c.gridy = row;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.CENTER;
            c.insets = new Insets(12, 6, 6, 6);
            form.add(applyBtn, c);

            c.gridy = row + 1;
            c.weighty = 1.0;
            c.fill = GridBagConstraints.VERTICAL;
            form.add(new JPanel(), c);

            return form;
        }

        private int addFavRow(JPanel form, GridBagConstraints c, int row,
                String label, final JLabel lbl, final FavItemType type) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            panel.add(lbl);
            JButton clrBtn = new JButton(getString("editor_btn_delete"));
            clrBtn.addActionListener(e -> {
                if (current != null) {
                    current.getFavoriteItems().remove(type);
                    refreshFavItems();
                }
            });
            panel.add(clrBtn);
            return addRowRaw(form, c, row, label, panel);
        }

        private int addSectionHeader(JPanel p, GridBagConstraints c, int row, String text) {
            c.gridx = 0;
            c.gridy = row;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.WEST;
            c.insets = new Insets(8, 4, 2, 4);
            JLabel lbl = new JLabel(text);
            lbl.setForeground(new Color(0, 70, 150));
            p.add(lbl, c);
            c.gridwidth = 1;
            c.insets = new Insets(3, 6, 3, 6);
            return row + 1;
        }

        private int addRow(JPanel p, GridBagConstraints c, int row, String label, JComponent field) {
            c.gridx = 0;
            c.gridy = row;
            c.gridwidth = 1;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.EAST;
            c.insets = new Insets(3, 6, 3, 3);
            p.add(new JLabel(label), c);
            c.gridx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.WEST;
            c.insets = new Insets(3, 3, 3, 6);
            p.add(field, c);
            return row + 1;
        }

        private int addRowRaw(JPanel p, GridBagConstraints c, int row, String label, JPanel panel) {
            c.gridx = 0;
            c.gridy = row;
            c.gridwidth = 1;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.EAST;
            c.insets = new Insets(3, 6, 3, 3);
            p.add(new JLabel(label), c);
            c.gridx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.WEST;
            c.insets = new Insets(3, 3, 3, 6);
            p.add(panel, c);
            return row + 1;
        }

        void setYukkuri(Yukkuri y) {
            current = y;
            if (y != null) {
                nameLabel.setText(yukkuriLabel(y.getClass().getSimpleName()));
                idDisplay.setText(String.valueOf(y.getUniqueId()));
                hybridParentsLabel.setText(hybridParentsText(y));
                xField.setText(String.valueOf(y.getX()));
                yField.setText(String.valueOf(y.getY()));
                nameEditField.setText(safeGetMyName(y));
                hungerPctField.setText(String.valueOf(hungerPct(y)));
                hpPctField.setText(String.valueOf(hpPct(y)));
                stressPctField.setText(String.valueOf(stressPct(y)));
                ageField.setText(String.valueOf(y.getAge()));
                ankoField.setText(String.valueOf(y.getAnkoAmount()));
                sickPeriodField.setText(String.valueOf(y.getSickPeriod()));
                attitudePtField.setText(String.valueOf(y.getAttitudePoint()));
                antCountField.setText(String.valueOf(y.getAntCount()));

                selectLabeled(ageStateBox,     y.getAgeState()     != null ? y.getAgeState().name()     : null);
                selectLabeled(attitudeBox,     y.getAttitude()     != null ? y.getAttitude().name()     : null);
                selectLabeled(intelligenceBox, y.getIntelligence() != null ? y.getIntelligence().name() : null);
                selectLabeled(happinessBox,    y.getHappiness()    != null ? y.getHappiness().name()    : null);
                selectLabeled(rankBox,         y.getRank()         != null ? y.getRank().name()         : null);

                // おかざり
                Okazari oz = y.getOkazaris();
                if (oz == null) {
                    okazariBox.setSelectedIndex(0);
                } else {
                    OkazariType ot = oz.getOkazariType();
                    int idx = 0;
                    if (ot != null) {
                        OkazariType[] vals = OkazariType.values();
                        for (int i = 0; i < vals.length; i++) {
                            if (vals[i] == ot) {
                                idx = i + 1;
                                break;
                            }
                        }
                    }
                    okazariBox.setSelectedIndex(idx);
                }

                partnerField.setText(String.valueOf(y.getPartner()));
                elderSisterField.setText(idListText(y.getElderSisters()));
                sisterField.setText(idListText(y.getSisters()));
                childrenField.setText(idListText(y.getChildren()));

                deadCheck.setSelected(y.isDead());
                burnedCheck.setSelected(y.isBurned());
                crushedCheck.setSelected(y.isCrushed());
                pealedCheck.setSelected(y.isPealed());
                packedCheck.setSelected(y.isPacked());
                hasPantsCheck.setSelected(y.isHasPants());
                sleepCheck.setSelected(y.isSleeping());
                excitingCheck.setSelected(y.isExciting());
                dirtyCheck.setSelected(y.isDirty());
                ununSlaveCheck.setSelected(y.getPublicRank() == PublicRank.UNUN_SLAVE);

                refreshReadOnly();
                refreshAttach();
            } else {
                nameLabel.setText(getString("editor_unselected"));
                idDisplay.setText(getString("editor_dash"));
                hybridParentsLabel.setText(getString("editor_dash"));
                xField.setText("");
                yField.setText("");
                nameEditField.setText("");
                hungerPctField.setText("");
                hpPctField.setText("");
                stressPctField.setText("");
                ageField.setText("");
                ankoField.setText("");
                sickPeriodField.setText("");
                attitudePtField.setText("");
                antCountField.setText("");
                okazariBox.setSelectedIndex(0);
                partnerField.setText("");
                elderSisterField.setText("");
                sisterField.setText("");
                childrenField.setText("");
                ageStateBox.setSelectedIndex(0);
                attitudeBox.setSelectedIndex(0);
                intelligenceBox.setSelectedIndex(0);
                happinessBox.setSelectedIndex(0);
                rankBox.setSelectedIndex(0);
                deadCheck.setSelected(false);
                burnedCheck.setSelected(false);
                crushedCheck.setSelected(false);
                pealedCheck.setSelected(false);
                packedCheck.setSelected(false);
                hasPantsCheck.setSelected(false);
                sleepCheck.setSelected(false);
                excitingCheck.setSelected(false);
                dirtyCheck.setSelected(false);
                ununSlaveCheck.setSelected(false);
                eventLabel.setText(getString("editor_dash"));
                canActLabel.setText(getString("editor_dash"));
                carryLabel.setText(getString("editor_dash"));
                favBallLabel.setText(getString("editor_dash"));
                favBedLabel.setText(getString("editor_dash"));
                favSuiLabel.setText(getString("editor_dash"));
                attachModel.clear();
            }
        }

        private void refreshReadOnly() {
            if (current == null) {
                return;
            }
            try {
                String ev = current.getCurrentEvent() != null
                        ? current.getCurrentEvent().toString()
                        : getString("editor_none");
                eventLabel.setText(ev);
            } catch (Exception e) {
                eventLabel.setText(getString("editor_none"));
            }
            try {
                boolean ca = current.canAction();
                boolean dm = current.isDontMove();
                String ok = getString("editor_mark_ok");
                String ng = getString("editor_mark_ng");
                canActLabel.setText(String.format(getString("editor_can_act_fmt"),
                        ca ? ok : ng, !dm ? ok : ng));
            } catch (Exception e) {
                canActLabel.setText(getString("editor_none"));
            }
            refreshCarry();
            refreshFavItems();
        }

        private void refreshCarry() {
            if (current == null) {
                carryLabel.setText(getString("editor_dash"));
                return;
            }
            Map<TakeoutItemType, Integer> items = current.getCarryItems();
            if (items == null || items.isEmpty()) {
                carryLabel.setText(getString("editor_none"));
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (TakeoutItemType t : TakeoutItemType.values()) {
                Integer id = items.get(t);
                if (id != null) {
                    if (sb.length() > 0) {
                        sb.append("  ");
                    }
                    sb.append(carryTypeLabel(t)).append("(ID=").append(id).append(")");
                }
            }
            carryLabel.setText(sb.length() > 0 ? sb.toString() : getString("editor_none"));
        }

        private void refreshFavItems() {
            if (current == null) {
                favBallLabel.setText(getString("editor_dash"));
                favBedLabel.setText(getString("editor_dash"));
                favSuiLabel.setText(getString("editor_dash"));
                return;
            }
            Map<FavItemType, Integer> favs = current.getFavoriteItems();
            favBallLabel.setText(favItemText(favs, FavItemType.BALL));
            favBedLabel.setText(favItemText(favs, FavItemType.BED));
            favSuiLabel.setText(favItemText(favs, FavItemType.SUI));
        }

        private String favItemText(Map<FavItemType, Integer> favs, FavItemType t) {
            if (favs == null) {
                return getString("editor_none");
            }
            Integer id = favs.get(t);
            return (id == null || id == -1) ? getString("editor_none") : "ID=" + id;
        }

        private void refreshAttach() {
            attachModel.clear();
            if (current == null || current.getAttach() == null) {
                return;
            }
            for (Attachment at : current.getAttach()) {
                attachModel.addElement(at);
            }
        }

        private void onAddAttachment() {
            if (current == null) {
                return;
            }
            String[] names = new String[ATTACH_CLASSES.length];
            for (int i = 0; i < ATTACH_CLASSES.length; i++) {
                try {
                    names[i] = ATTACH_CLASSES[i].newInstance().toString();
                } catch (Exception ex) {
                    names[i] = ATTACH_CLASSES[i].getSimpleName();
                }
            }
            JComboBox<String> typeBox = new JComboBox<>(names);
            if (JOptionPane.showConfirmDialog(this, typeBox,
                    getString("editor_dlg_add_attach"),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
                return;
            }
            try {
                Attachment at = (Attachment) ATTACH_CLASSES[typeBox.getSelectedIndex()].newInstance();
                at.setParent(current.getUniqueId());
                current.getAttach().add(at);
                refreshAttach();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        getString("editor_err_add") + "\n" + ex.getMessage(),
                        getString("editor_err_title"), JOptionPane.ERROR_MESSAGE);
            }
        }

        private void onRemoveAttachment() {
            if (current == null) {
                return;
            }
            int[] indices = attachList.getSelectedIndices();
            if (indices.length == 0) {
                JOptionPane.showMessageDialog(this, getString("editor_err_select_item"));
                return;
            }
            List<Attachment> toRemove = new ArrayList<>();
            for (int i : indices) {
                toRemove.add(attachModel.getElementAt(i));
            }
            String msg = toRemove.size() == 1
                    ? String.format(getString("editor_dlg_del_confirm"), toRemove.get(0).toString(), toRemove.get(0).getObjId())
                    : String.format(getString("editor_dlg_del_multi_confirm"), toRemove.size());
            if (JOptionPane.showConfirmDialog(this, msg, getString("editor_dlg_del_title"),
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
            for (Attachment at : toRemove) {
                current.getAttach().remove(at);
            }
            refreshAttach();
        }

        private static String safeGetMyName(Yukkuri y) {
            try {
                String n = y.getMyName();
                return n != null ? n : "";
            } catch (Exception e) {
                return "";
            }
        }

        private void apply() {
            if (current == null) {
                return;
            }
            try {
                current.setX(parseInt(xField, current.getX()));
                current.setY(parseInt(yField, current.getY()));

                String newName = nameEditField.getText().trim();
                if (!newName.isEmpty()) {
                    String[] names = current.getMyNames();
                    AgeState as = current.getAgeState();
                    if (names == null) {
                        names = new String[]{"", "", ""};
                        current.setMyNames(names);
                    }
                    int idx = (as != null && as.ordinal() < names.length) ? as.ordinal() : 0;
                    names[idx] = newName;
                }

                if (current.getAgeState() != null) {
                    int hp     = clamp(parseInt(hpPctField,     hpPct(current)),     0, 100);
                    int hunger = clamp(parseInt(hungerPctField, hungerPct(current)), 0, 100);
                    int stress = clamp(parseInt(stressPctField, stressPct(current)), 0, 100);
                    current.setHungry(hunger * current.getHungryLimit() / 100);
                    current.setDamage(current.getDamageLimit() - hp * current.getDamageLimit() / 100);
                    current.setStress(stress * current.getStressLimit() / 100);
                }

                current.setAge(parseLong(ageField, current.getAge()));
                current.setAnkoAmount(parseInt(ankoField, current.getAnkoAmount()));
                current.setSickPeriod(parseInt(sickPeriodField, current.getSickPeriod()));
                current.setAttitudePoint(parseInt(attitudePtField, current.getAttitudePoint()));
                current.setAntCount(parseInt(antCountField, current.getAntCount()));

                LabeledItem sel;
                sel = (LabeledItem) ageStateBox.getSelectedItem();
                if (sel != null) {
                    current.setAgeState(AgeState.valueOf(sel.enumName));
                }
                sel = (LabeledItem) attitudeBox.getSelectedItem();
                if (sel != null) {
                    current.setAttitude(Attitude.valueOf(sel.enumName));
                }
                sel = (LabeledItem) intelligenceBox.getSelectedItem();
                if (sel != null) {
                    current.setIntelligence(Intelligence.valueOf(sel.enumName));
                }
                sel = (LabeledItem) happinessBox.getSelectedItem();
                if (sel != null) {
                    current.setHappiness(Happiness.valueOf(sel.enumName));
                }
                sel = (LabeledItem) rankBox.getSelectedItem();
                if (sel != null) {
                    current.setRank(YukkuriRank.valueOf(sel.enumName));
                }

                int okazariIdx = okazariBox.getSelectedIndex();
                if (okazariIdx == 0) {
                    current.setOkazaris(null);
                } else {
                    OkazariType ot = OkazariType.values()[okazariIdx - 1];
                    Okazari o = new Okazari();
                    o.setOwner(current.getUniqueId());
                    o.setOkazariType(ot);
                    current.setOkazaris(o);
                }

                String partnerText = partnerField.getText().trim();
                if (!partnerText.isEmpty()) {
                    try {
                        int pid = Integer.parseInt(partnerText);
                        if (pid != -1 && worldState != null
                                && !worldState.getYukkuriRegistry().containsKey(pid)) {
                            JOptionPane.showMessageDialog(this,
                                    String.format(getString("editor_err_partner_notfound"), pid));
                            return;
                        }
                        current.setPartner(pid);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, getString("editor_err_partner_format"));
                        return;
                    }
                }

                List<Integer> newElders = parseIdList(elderSisterField, getString("editor_val_elder_sisters"));
                if (newElders == null) {
                    return;
                }
                List<Integer> newSisters = parseIdList(sisterField, getString("editor_val_sisters"));
                if (newSisters == null) {
                    return;
                }
                List<Integer> newChildren = parseIdList(childrenField, getString("editor_val_children"));
                if (newChildren == null) {
                    return;
                }
                current.setElderSisters(new LinkedList<Integer>(newElders));
                current.setSisters(new LinkedList<Integer>(newSisters));
                current.setChildren(new LinkedList<Integer>(newChildren));

                current.setDead(deadCheck.isSelected());
                current.setBurned(burnedCheck.isSelected());
                current.setCrushed(crushedCheck.isSelected());
                current.setPealed(pealedCheck.isSelected());
                current.setPacked(packedCheck.isSelected());
                current.setHasPants(hasPantsCheck.isSelected());
                current.setSleeping(sleepCheck.isSelected());
                current.setExciting(excitingCheck.isSelected());
                current.setDirty(dirtyCheck.isSelected());
                current.setPublicRank(ununSlaveCheck.isSelected() ? PublicRank.UNUN_SLAVE : PublicRank.NONE);

                setYukkuri(current);
                if (statusLabel != null) {
                    statusLabel.setText("  " + String.format(getString("editor_status_applied"),
                            yukkuriLabel(current.getClass().getSimpleName()), current.getUniqueId()));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        getString("editor_err_apply") + "\n" + ex.getMessage(),
                        getString("editor_err_title"), JOptionPane.ERROR_MESSAGE);
            }
        }

        private List<Integer> parseIdList(JTextField field, String fieldName) {
            String text = field.getText().trim();
            List<Integer> ids = new ArrayList<Integer>();
            String noneStr = getString("editor_none");
            if (text.isEmpty() || noneStr.equalsIgnoreCase(text) || getString("editor_dash").equals(text)) {
                return ids;
            }
            for (String s : text.split("\\s*,\\s*")) {
                if (s.isEmpty()) {
                    continue;
                }
                int id;
                try {
                    id = Integer.parseInt(s.trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            String.format(getString("editor_err_id_invalid"), fieldName, s));
                    return null;
                }
                if (worldState != null && !worldState.getYukkuriRegistry().containsKey(id)) {
                    JOptionPane.showMessageDialog(this,
                            String.format(getString("editor_err_id_notfound"), fieldName, id));
                    return null;
                }
                ids.add(id);
            }
            return ids;
        }

        private static int clamp(int v, int min, int max) {
            return Math.max(min, Math.min(max, v));
        }

        private static String hybridParentsText(Yukkuri y) {
            if (!(y instanceof HybridYukkuri)) {
                return getString("editor_dash");
            }
            HybridYukkuri hy = (HybridYukkuri) y;
            return hybridParentName(hy, "dorei") + " × " + hybridParentName(hy, "dorei2");
        }

        private static String hybridParentName(HybridYukkuri hy, String fieldName) {
            try {
                Field f = HybridYukkuri.class.getDeclaredField(fieldName);
                f.setAccessible(true);
                Yukkuri parent = (Yukkuri) f.get(hy);
                return parent != null ? yukkuriLabel(parent.getClass().getSimpleName()) : "?";
            } catch (Exception e) {
                return "?";
            }
        }
    }

    //
    // オブジェクト編集パネル
    //
    static class ObjectEditPanel extends JPanel {

        private Entity current;
        private JLabel statusLabel;

        private final JLabel kindLabel  = new JLabel(getString("editor_unselected"));
        private final JLabel idDisplay  = new JLabel(getString("editor_dash"));
        private final NumericField xField = new NumericField(8);
        private final NumericField yField = new NumericField(8);

        private final JPanel propPanel = new JPanel(new GridBagLayout());
        private final List<Runnable> propAppliers = new ArrayList<Runnable>();
        private GridBagConstraints pbc;
        private int prow;

        ObjectEditPanel() {
            setLayout(new BorderLayout());
            JScrollPane scroll = new JScrollPane(buildForm());
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            scroll.setBorder(BorderFactory.createTitledBorder(getString("editor_panel_object")));
            add(scroll, BorderLayout.CENTER);
        }

        void setStatusLabel(JLabel lbl) {
            this.statusLabel = lbl;
        }

        private JPanel buildForm() {
            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(3, 6, 3, 6);
            int row = 0;
            row = addBaseRow(form, c, row, getString("editor_lbl_kind"),   kindLabel);
            row = addBaseRow(form, c, row, getString("editor_lbl_obj_id"), idDisplay);
            row = addBaseRow(form, c, row, getString("editor_lbl_x"),      xField);
            row = addBaseRow(form, c, row, getString("editor_lbl_y"),      yField);

            c.gridx = 0;
            c.gridy = row;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.NORTHWEST;
            c.insets = new Insets(0, 0, 0, 0);
            c.weightx = 1.0;
            form.add(propPanel, c);
            row++;

            JButton applyBtn = new JButton(getString("editor_btn_apply"));
            applyBtn.addActionListener(e -> apply());
            c.gridx = 0;
            c.gridy = row;
            c.gridwidth = 2;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.CENTER;
            c.insets = new Insets(10, 6, 6, 6);
            form.add(applyBtn, c);

            c.gridy = row + 1;
            c.weighty = 1.0;
            c.fill = GridBagConstraints.VERTICAL;
            form.add(new JPanel(), c);
            return form;
        }

        private int addBaseRow(JPanel p, GridBagConstraints c, int row, String label, JComponent field) {
            c.gridx = 0;
            c.gridy = row;
            c.gridwidth = 1;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.EAST;
            c.insets = new Insets(3, 6, 3, 3);
            c.weightx = 0;
            p.add(new JLabel(label), c);
            c.gridx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.WEST;
            c.insets = new Insets(3, 3, 3, 6);
            c.weightx = 1.0;
            p.add(field, c);
            return row + 1;
        }

        // ── 動的プロパティ追加ヘルパー ────────────────────────────

        private void initProp() {
            propPanel.removeAll();
            propAppliers.clear();
            prow = 0;
            pbc = new GridBagConstraints();
            pbc.insets = new Insets(3, 6, 3, 6);
        }

        private void addPropHeader(String title) {
            pbc.gridx = 0;
            pbc.gridy = prow;
            pbc.gridwidth = 2;
            pbc.fill = GridBagConstraints.HORIZONTAL;
            pbc.anchor = GridBagConstraints.WEST;
            pbc.insets = new Insets(8, 4, 2, 4);
            pbc.weightx = 1.0;
            JLabel lbl = new JLabel(title);
            lbl.setForeground(new Color(0, 70, 150));
            propPanel.add(lbl, pbc);
            prow++;
            pbc.insets = new Insets(3, 6, 3, 6);
            pbc.weightx = 0;
        }

        private void addPropRow(String label, JComponent field) {
            pbc.gridx = 0;
            pbc.gridy = prow;
            pbc.gridwidth = 1;
            pbc.fill = GridBagConstraints.NONE;
            pbc.anchor = GridBagConstraints.EAST;
            pbc.insets = new Insets(3, 6, 3, 3);
            pbc.weightx = 0;
            propPanel.add(new JLabel(label), pbc);
            pbc.gridx = 1;
            pbc.fill = GridBagConstraints.HORIZONTAL;
            pbc.anchor = GridBagConstraints.WEST;
            pbc.insets = new Insets(3, 3, 3, 6);
            pbc.weightx = 1.0;
            propPanel.add(field, pbc);
            prow++;
        }

        private void addPropWide(JComponent comp) {
            pbc.gridx = 0;
            pbc.gridy = prow;
            pbc.gridwidth = 2;
            pbc.fill = GridBagConstraints.HORIZONTAL;
            pbc.anchor = GridBagConstraints.WEST;
            pbc.insets = new Insets(3, 6, 3, 6);
            pbc.weightx = 1.0;
            propPanel.add(comp, pbc);
            prow++;
        }

        private NumericField addPropNum(String lbl, int val) {
            NumericField f = new NumericField(8);
            f.setText(String.valueOf(val));
            addPropRow(lbl, f);
            return f;
        }

        private JCheckBox addPropCheck(String lbl, boolean val) {
            JCheckBox cb = new JCheckBox();
            cb.setSelected(val);
            addPropRow(lbl, cb);
            return cb;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        private JComboBox addPropEnum(String lbl, Enum[] vals, Enum sel) {
            JComboBox box = new JComboBox(vals);
            box.setSelectedItem(sel);
            addPropRow(lbl, box);
            return box;
        }

        private JComboBox<LabeledItem> addPropLabeledEnum(String lbl, LabeledItem[] items, String selName) {
            JComboBox<LabeledItem> box = new JComboBox<>(items);
            for (int i = 0; i < items.length; i++) {
                if (items[i].enumName.equals(selName)) {
                    box.setSelectedIndex(i);
                    break;
                }
            }
            addPropRow(lbl, box);
            return box;
        }

        // ── エンティティ種別ごとのプロパティ構築 ─────────────────

        @SuppressWarnings({"rawtypes"})
        private void buildProperties(Entity e) {
            if (e instanceof Food) {
                final Food food = (Food) e;
                addPropHeader(getString("editor_prop_hdr_food"));
                final JComboBox<LabeledItem> ftBox = addPropLabeledEnum(
                        getString("editor_prop_kind"), makeFoodTypeItems(), food.getFoodType().name());
                final NumericField amtF = addPropNum(getString("editor_prop_amount"), food.getAmount());
                propAppliers.add(new Runnable() {
                    public void run() {
                        food.setFoodType(FoodType.valueOf(((LabeledItem) ftBox.getSelectedItem()).enumName));
                        food.setAmount(parseInt(amtF, food.getAmount()));
                    }
                });

            } else if (e instanceof Toilet) {
                final Toilet t = (Toilet) e;
                addPropHeader(getString("editor_prop_hdr_toilet"));
                Toilet.ToiletType curType = t.isForSlave() ? Toilet.ToiletType.SLAVE
                        : t.getAutoClean() ? Toilet.ToiletType.CLEAN : Toilet.ToiletType.NORMAL;
                final JComboBox typeBox = addPropEnum(
                        getString("editor_prop_type"), Toilet.ToiletType.values(), curType);
                final JComboBox rankBox = addPropEnum(
                        getString("editor_prop_rank"), WorldEntity.ItemRank.values(), t.getItemRank());
                propAppliers.add(new Runnable() {
                    public void run() {
                        Toilet.ToiletType sel = (Toilet.ToiletType) typeBox.getSelectedItem();
                        t.setAutoClean(sel == Toilet.ToiletType.CLEAN);
                        t.setForSlave(sel == Toilet.ToiletType.SLAVE);
                        t.setItemRank((WorldEntity.ItemRank) rankBox.getSelectedItem());
                    }
                });

            } else if (e instanceof Bed) {
                final Bed b = (Bed) e;
                addPropHeader(getString("editor_prop_hdr_bed"));
                final JComboBox rankBox = addPropEnum(
                        getString("editor_prop_rank"), WorldEntity.ItemRank.values(), b.getItemRank());
                propAppliers.add(new Runnable() {
                    public void run() {
                        b.setItemRank((WorldEntity.ItemRank) rankBox.getSelectedItem());
                    }
                });

            } else if (e instanceof Diffuser) {
                final Diffuser d = (Diffuser) e;
                addPropHeader(getString("editor_prop_hdr_diffuser"));
                SteamType[] steamTypes = SteamType.values();
                boolean[] arr = d.getSteamType();
                final JCheckBox[] checks = new JCheckBox[steamTypes.length];
                JPanel steamPanel = new JPanel(new java.awt.GridLayout(0, 2, 6, 2));
                for (int i = 0; i < steamTypes.length; i++) {
                    checks[i] = new JCheckBox(steamTypes[i].toString());
                    checks[i].setSelected(arr != null && i < arr.length && arr[i]);
                    steamPanel.add(checks[i]);
                }
                addPropWide(steamPanel);
                propAppliers.add(new Runnable() {
                    public void run() {
                        boolean[] newArr = new boolean[SteamType.values().length];
                        for (int i = 0; i < checks.length; i++) {
                            newArr[i] = checks[i].isSelected();
                        }
                        d.setSteamType(newArr);
                        d.setSteamNum(0);
                    }
                });

            } else if (e instanceof BreedingPool) {
                final BreedingPool bp = (BreedingPool) e;
                addPropHeader(getString("editor_prop_hdr_breedingpool"));
                final JCheckBox hqCheck    = addPropCheck(getString("editor_prop_highquality"), bp.isHighQuality());
                final JCheckBox stalkCheck = addPropCheck(getString("editor_prop_stalkpool"),   bp.isStalkPool());
                LabeledItem[] lqtItems = makeLiquidTypeItems();
                int curLqt = bp.getLiquidYukkuriType();
                String curLqtStr = String.valueOf(curLqt);
                final JComboBox<LabeledItem> lqtBox = new JComboBox<LabeledItem>(lqtItems);
                for (int i = 0; i < lqtItems.length; i++) {
                    if (lqtItems[i].enumName.equals(curLqtStr)) {
                        lqtBox.setSelectedIndex(i);
                        break;
                    }
                }
                addPropRow(getString("editor_prop_liquidtype"), lqtBox);
                propAppliers.add(new Runnable() {
                    public void run() {
                        bp.setHighQuality(hqCheck.isSelected());
                        bp.setStalkPool(stalkCheck.isSelected());
                        bp.setLiquidYukkuriType(Integer.parseInt(((LabeledItem) lqtBox.getSelectedItem()).enumName));
                    }
                });

            } else if (e instanceof OrangePool) {
                final OrangePool op = (OrangePool) e;
                addPropHeader(getString("editor_prop_hdr_orangepool"));
                final JCheckBox rescueCheck = addPropCheck(getString("editor_prop_rescue"), op.isRescue());
                final JComboBox rankBox     = addPropEnum(
                        getString("editor_prop_rank"), WorldEntity.ItemRank.values(), op.getItemRank());
                propAppliers.add(new Runnable() {
                    public void run() {
                        op.setRescue(rescueCheck.isSelected());
                        op.setItemRank((WorldEntity.ItemRank) rankBox.getSelectedItem());
                    }
                });

            } else if (e instanceof House) {
                final House h = (House) e;
                addPropHeader(getString("editor_prop_hdr_house"));
                final JComboBox typeBox = addPropEnum(
                        getString("editor_prop_type"), HouseTable.values(), h.getHouseType());
                final JComboBox rankBox = addPropEnum(
                        getString("editor_prop_rank"), WorldEntity.ItemRank.values(), h.getItemRank());
                propAppliers.add(new Runnable() {
                    public void run() {
                        h.setHouseType((HouseTable) typeBox.getSelectedItem());
                        h.setItemRank((WorldEntity.ItemRank) rankBox.getSelectedItem());
                    }
                });

            } else if (e instanceof ProcessorPlate) {
                final ProcessorPlate pp = (ProcessorPlate) e;
                addPropHeader(getString("editor_prop_hdr_processorplate"));
                final JComboBox ptBox = addPropEnum(
                        getString("editor_prop_processtype"), ProcessType.values(), pp.getEnumProcessType());
                propAppliers.add(new Runnable() {
                    public void run() {
                        pp.setEnumProcessType((ProcessType) ptBox.getSelectedItem());
                    }
                });

            } else if (e instanceof StickyPlate) {
                final StickyPlate sp = (StickyPlate) e;
                addPropHeader(getString("editor_prop_hdr_stickyplate"));
                final JCheckBox fixBackCheck = addPropCheck(getString("editor_prop_fixback"), sp.isFixBack());
                final JComboBox rankBox = addPropEnum(
                        getString("editor_prop_rank"), WorldEntity.ItemRank.values(), sp.getItemRank());
                propAppliers.add(new Runnable() {
                    public void run() {
                        sp.setFixBack(fixBackCheck.isSelected());
                        sp.setItemRank((WorldEntity.ItemRank) rankBox.getSelectedItem());
                    }
                });

            } else if (e instanceof Stone) {
                final Stone s = (Stone) e;
                addPropHeader(getString("editor_prop_hdr_stone"));
                final JComboBox rankBox = addPropEnum(
                        getString("editor_prop_rank"), WorldEntity.ItemRank.values(), s.getItemRank());
                propAppliers.add(new Runnable() {
                    public void run() {
                        s.setItemRank((WorldEntity.ItemRank) rankBox.getSelectedItem());
                    }
                });

            } else if (e instanceof Toy) {
                final Toy toy = (Toy) e;
                addPropHeader(getString("editor_prop_hdr_toy"));
                final JComboBox rankBox = addPropEnum(
                        getString("editor_prop_rank"), WorldEntity.ItemRank.values(), toy.getItemRank());
                propAppliers.add(new Runnable() {
                    public void run() {
                        toy.setItemRank((WorldEntity.ItemRank) rankBox.getSelectedItem());
                    }
                });

            } else if (e instanceof FoodMaker) {
                final FoodMaker fm = (FoodMaker) e;
                addPropHeader(getString("editor_prop_hdr_foodmaker"));
                final NumericField stockF  = addPropNum(getString("editor_prop_stock"),       fm.getStockFood());
                final NumericField amtF    = addPropNum(getString("editor_prop_foodamount"),   fm.getFoodAmount());
                final JCheckBox readyCheck = addPropCheck(getString("editor_prop_processready"), fm.isProcessReady());
                propAppliers.add(new Runnable() {
                    public void run() {
                        fm.setStockFood(parseInt(stockF, fm.getStockFood()));
                        fm.setFoodAmount(parseInt(amtF, fm.getFoodAmount()));
                        fm.setProcessReady(readyCheck.isSelected());
                    }
                });

            } else if (e instanceof AutoFeeder) {
                final AutoFeeder af = (AutoFeeder) e;
                addPropHeader(getString("editor_prop_hdr_autofeeder"));
                AutoFeeder.FeedType[] feedTypes = AutoFeeder.FeedType.values();
                int safeType = (af.getType() >= 0 && af.getType() < feedTypes.length) ? af.getType() : 0;
                final JComboBox typeCb = addPropEnum(
                        getString("editor_prop_type"), feedTypes, feedTypes[safeType]);
                AutoFeeder.FeedMode[] feedModes = AutoFeeder.FeedMode.values();
                int safeMode = (af.getMode() >= 0 && af.getMode() < feedModes.length) ? af.getMode() : 0;
                final JComboBox modeCb = addPropEnum(
                        getString("editor_prop_mode"), feedModes, feedModes[safeMode]);
                final NumericField intervalF = addPropNum(
                        getString("editor_prop_feedinterval"), af.getFeedingInterval());
                final NumericField pF        = addPropNum(
                        getString("editor_prop_feedp"), af.getFeedingP());
                propAppliers.add(new Runnable() {
                    public void run() {
                        af.setType(((AutoFeeder.FeedType) typeCb.getSelectedItem()).ordinal());
                        af.setMode(((AutoFeeder.FeedMode) modeCb.getSelectedItem()).ordinal());
                        af.setFeedingInterval(parseInt(intervalF, af.getFeedingInterval()));
                        af.setFeedingP(parseInt(pF, af.getFeedingP()));
                    }
                });

            } else if (e instanceof Trampoline) {
                final Trampoline tr = (Trampoline) e;
                addPropHeader(getString("editor_prop_hdr_trampoline"));
                Trampoline.TrampolineType[] ttypes = Trampoline.TrampolineType.values();
                int safeOpt = (tr.getOption() >= 0 && tr.getOption() < ttypes.length) ? tr.getOption() : 0;
                final JComboBox optBox = addPropEnum(
                        getString("editor_prop_option"), ttypes, ttypes[safeOpt]);
                final NumericField acc1F = addPropNum(getString("editor_prop_accident1"), tr.getAccident1());
                final NumericField acc2F = addPropNum(getString("editor_prop_accident2"), tr.getAccident2());
                propAppliers.add(new Runnable() {
                    public void run() {
                        tr.setOption(((Trampoline.TrampolineType) optBox.getSelectedItem()).ordinal());
                        tr.setAccident1(parseInt(acc1F, tr.getAccident1()));
                        tr.setAccident2(parseInt(acc2F, tr.getAccident2()));
                    }
                });

            } else if (e instanceof Yunba) {
                final Yunba yn = (Yunba) e;
                addPropHeader(getString("editor_prop_hdr_yunba"));

                // カラー
                String[] colList = makeYunbaColorItems();
                int safeColor = (yn.getColor() >= 0 && yn.getColor() < colList.length) ? yn.getColor() : 0;
                final JComboBox<String> colorBox = new JComboBox<String>(colList);
                colorBox.setSelectedIndex(safeColor);
                addPropRow(getString("editor_prop_color"), colorBox);

                // ランク
                final JComboBox rankBox = addPropEnum(
                        getString("editor_prop_rank"), WorldEntity.ItemRank.values(), yn.getItemRank());

                // actionFlags の null ガード
                Yunba.Action[] acts = Yunba.Action.values();
                boolean[][] af = yn.getActionFlags();
                if (af == null) {
                    af = new boolean[acts.length][3];
                    yn.setActionFlags(af);
                }
                boolean[][] af2 = yn.getActionFlags2();
                if (af2 == null) {
                    af2 = new boolean[1][5];
                    yn.setActionFlags2(af2);
                }
                boolean[][] af3 = yn.getActionFlags3();
                if (af3 == null) {
                    af3 = new boolean[1][3];
                    yn.setActionFlags3(af3);
                }
                final boolean[][] finalAf  = af;
                final boolean[][] finalAf2 = af2;
                final boolean[][] finalAf3 = af3;

                // 最初の6アクション × 3成長段階
                addPropHeader(getString("editor_prop_hdr_yunba_action"));
                JPanel actionGrid = new JPanel(new GridLayout(0, 4, 4, 2));
                actionGrid.add(new JLabel(""));
                actionGrid.add(new JLabel(getString("enums_babyyu"),  JLabel.CENTER));
                actionGrid.add(new JLabel(getString("enums_childyu"), JLabel.CENTER));
                actionGrid.add(new JLabel(getString("enums_adultyu"), JLabel.CENTER));
                final JCheckBox[][] flagCbs = new JCheckBox[6][3];
                for (int i = 0; i < 6; i++) {
                    actionGrid.add(new JLabel(acts[i].toString()));
                    for (int j = 0; j < 3; j++) {
                        flagCbs[i][j] = new JCheckBox("");
                        flagCbs[i][j].setSelected(af[i][j]);
                        actionGrid.add(flagCbs[i][j]);
                    }
                }
                addPropWide(actionGrid);

                // その他アクション (index 6+)
                addPropHeader(getString("editor_prop_hdr_yunba_extra"));
                int extraCount = acts.length - 6;
                JPanel extraGrid = new JPanel(new GridLayout(0, 2, 4, 2));
                final JCheckBox[] extraCbs = new JCheckBox[extraCount];
                for (int i = 0; i < extraCount; i++) {
                    extraCbs[i] = new JCheckBox(acts[6 + i].toString());
                    extraCbs[i].setSelected(af[6 + i][0]);
                    extraGrid.add(extraCbs[i]);
                }
                addPropWide(extraGrid);

                // 気質フィルター（処理対象外）
                addPropHeader(getString("editor_prop_hdr_yunba_attitude"));
                JPanel attGrid = new JPanel(new GridLayout(1, 5, 4, 2));
                String[] attLabels = {
                    getString("attitude_verynice"), getString("attitude_nice"),
                    getString("attitude_normal"), getString("attitude_shithead"),
                    getString("attitude_supershithead")
                };
                final JCheckBox[] attCbs = new JCheckBox[5];
                for (int i = 0; i < 5; i++) {
                    attCbs[i] = new JCheckBox(attLabels[i]);
                    attCbs[i].setSelected(af2[0][i]);
                    attGrid.add(attCbs[i]);
                }
                addPropWide(attGrid);

                // 知性フィルター（処理対象外）
                addPropHeader(getString("editor_prop_hdr_yunba_intel"));
                JPanel intelGrid = new JPanel(new GridLayout(1, 3, 4, 2));
                String[] intelLabels = {
                    getString("intel_badge"), getString("intel_normal"), getString("intel_fool")
                };
                final JCheckBox[] intelCbs = new JCheckBox[3];
                for (int i = 0; i < 3; i++) {
                    intelCbs[i] = new JCheckBox(intelLabels[i]);
                    intelCbs[i].setSelected(af3[0][i]);
                    intelGrid.add(intelCbs[i]);
                }
                addPropWide(intelGrid);

                propAppliers.add(new Runnable() {
                    public void run() {
                        yn.setColor(colorBox.getSelectedIndex());
                        yn.setItemRank((WorldEntity.ItemRank) rankBox.getSelectedItem());
                        for (int i = 0; i < 6; i++) {
                            for (int j = 0; j < 3; j++) {
                                finalAf[i][j] = flagCbs[i][j].isSelected();
                            }
                        }
                        for (int i = 0; i < extraCbs.length; i++) {
                            finalAf[6 + i][0] = extraCbs[i].isSelected();
                        }
                        for (int i = 0; i < 5; i++) {
                            finalAf2[0][i] = attCbs[i].isSelected();
                        }
                        for (int i = 0; i < 3; i++) {
                            finalAf3[0][i] = intelCbs[i].isSelected();
                        }
                        // check フラグ再導出
                        yn.setYukkuriCheck(false);
                        yn.setShitCheck(false);
                        yn.setStalkCheck(false);
                        yn.setNorndCheck(false);
                        yn.setKillCheck(false);
                        yn.setMineutiCheck(false);
                        yn.setNoDamageFallCheck(false);
                        yn.setFoodCheck(false);
                        for (int i = 0; i < finalAf.length; i++) {
                            for (int j = 0; j < 3; j++) {
                                if (i == Yunba.Action.SHIT.ordinal()) {
                                    if (finalAf[i][0]) {
                                        yn.setShitCheck(true);
                                    }
                                } else if (i == Yunba.Action.STALK.ordinal()) {
                                    if (finalAf[i][0]) {
                                        yn.setStalkCheck(true);
                                    }
                                } else if (i == Yunba.Action.NORND.ordinal()) {
                                    if (finalAf[i][0]) {
                                        yn.setNorndCheck(true);
                                    }
                                } else if (i == Yunba.Action.KILL.ordinal()) {
                                    if (finalAf[i][0]) {
                                        yn.setKillCheck(true);
                                    }
                                } else if (i == Yunba.Action.MINEUTI.ordinal()) {
                                    if (finalAf[i][0]) {
                                        yn.setMineutiCheck(true);
                                    }
                                } else if (i == Yunba.Action.NODAMAGE_FALL.ordinal()) {
                                    if (finalAf[i][0]) {
                                        yn.setNoDamageFallCheck(true);
                                    }
                                } else if (i == Yunba.Action.EMPFOOD.ordinal()) {
                                    if (finalAf[i][0]) {
                                        yn.setFoodCheck(true);
                                    }
                                } else {
                                    if (finalAf[i][j]) {
                                        yn.setYukkuriCheck(true);
                                    }
                                }
                            }
                        }
                        yn.recalcDrawLayer();
                    }
                });
            }
            // HotPlate, MachinePress は固有プロパティ編集不要
        }

        void setEntity(Entity e) {
            current = e;
            initProp();
            if (e != null) {
                kindLabel.setText(e.getClass().getSimpleName());
                idDisplay.setText(String.valueOf(e.getObjId()));
                xField.setText(String.valueOf(e.getX()));
                yField.setText(String.valueOf(e.getY()));
                buildProperties(e);
            } else {
                kindLabel.setText(getString("editor_unselected"));
                idDisplay.setText(getString("editor_dash"));
                xField.setText("");
                yField.setText("");
            }
            propPanel.revalidate();
            propPanel.repaint();
            revalidate();
            repaint();
        }

        private void apply() {
            if (current == null) {
                return;
            }
            try {
                current.setX(parseInt(xField, current.getX()));
                current.setY(parseInt(yField, current.getY()));
                for (Runnable r : propAppliers) {
                    r.run();
                }
                if (statusLabel != null) {
                    statusLabel.setText("  " + String.format(getString("editor_status_applied"),
                            current.getClass().getSimpleName(), current.getObjId()));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        getString("editor_err_apply") + "\n" + ex.getMessage(),
                        getString("editor_err_title"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //
    // フィールドシェイプ編集パネル
    //
    static class FieldEditPanel extends JPanel {

        private JLabel statusLabel;
        private FieldEntry current;
        private final JPanel propPanel = new JPanel(new GridBagLayout());
        private final List<Runnable> propAppliers = new ArrayList<Runnable>();
        private JButton applyBtn;

        void setStatusLabel(JLabel lbl) {
            statusLabel = lbl;
        }

        FieldEditPanel() {
            setLayout(new BorderLayout(0, 4));
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            JScrollPane scroll = new JScrollPane(propPanel);
            scroll.setBorder(null);
            add(scroll, BorderLayout.CENTER);
            applyBtn = new JButton(getString("editor_btn_apply"));
            applyBtn.setEnabled(false);
            applyBtn.addActionListener(e -> onApply());
            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            btnRow.add(applyBtn);
            add(btnRow, BorderLayout.SOUTH);
        }

        void setEntry(FieldEntry entry) {
            current = entry;
            propPanel.removeAll();
            propAppliers.clear();
            if (entry == null) {
                applyBtn.setEnabled(false);
                propPanel.revalidate();
                propPanel.repaint();
                return;
            }
            buildFields(entry);
            applyBtn.setEnabled(true);
            propPanel.revalidate();
            propPanel.repaint();
        }

        private void addRow(String lbl, JComponent field) {
            GridBagConstraints cl = new GridBagConstraints();
            cl.gridx = 0;
            cl.gridy = GridBagConstraints.RELATIVE;
            cl.anchor = GridBagConstraints.WEST;
            cl.insets = new Insets(2, 2, 2, 6);
            propPanel.add(new JLabel(lbl), cl);
            GridBagConstraints cf = new GridBagConstraints();
            cf.gridx = 1;
            cf.gridy = GridBagConstraints.RELATIVE;
            cf.fill = GridBagConstraints.HORIZONTAL;
            cf.weightx = 1.0;
            cf.insets = new Insets(2, 0, 2, 2);
            propPanel.add(field, cf);
        }

        private NumericField addNum(String lbl, int val) {
            NumericField f = new NumericField(8);
            f.setText(String.valueOf(val));
            addRow(lbl, f);
            return f;
        }

        private void buildFields(FieldEntry entry) {
            FieldShape s = entry.shape;
            // 座標（共通）
            final JTextField sx = new JTextField(String.valueOf(s.getStartX()), 8);
            final JTextField sy = new JTextField(String.valueOf(s.getStartY()), 8);
            final JTextField ex = new JTextField(String.valueOf(s.getEndX()), 8);
            final JTextField ey = new JTextField(String.valueOf(s.getEndY()), 8);
            sx.setEditable(false);
            sy.setEditable(false);
            ex.setEditable(false);
            ey.setEditable(false);
            addRow(getString("editor_field_startx"), sx);
            addRow(getString("editor_field_starty"), sy);
            addRow(getString("editor_field_endx"),   ex);
            addRow(getString("editor_field_endy"),   ey);

            if (s instanceof Barrier) {
                Barrier b = (Barrier) s;
                JTextField attrLbl = new JTextField(barrierAttrLabel(b.getAttribute()));
                attrLbl.setEditable(false);
                addRow(getString("editor_field_attr"), attrLbl);

            } else if (s instanceof Farm) {
                Farm farm = (Farm) s;
                final NumericField amtF = addNum(getString("editor_prop_amount"), farm.getAmount());
                propAppliers.add(new Runnable() {
                    public void run() {
                        farm.setAmount(parseInt(amtF, farm.getAmount()));
                    }
                });

            } else if (s instanceof Pool) {
                // 現状は座標表示のみ
            }

            // spacer
            GridBagConstraints cs = new GridBagConstraints();
            cs.gridx = 0;
            cs.gridy = GridBagConstraints.RELATIVE;
            cs.gridwidth = 2;
            cs.weighty = 1.0;
            cs.fill = GridBagConstraints.VERTICAL;
            propPanel.add(new JPanel(), cs);
        }

        private int parseInt(NumericField f, int fallback) {
            try {
                return Integer.parseInt(f.getText().trim());
            } catch (NumberFormatException e) {
                return fallback;
            }
        }

        private void onApply() {
            if (current == null) {
                return;
            }
            for (Runnable r : propAppliers) {
                r.run();
            }
            if (statusLabel != null) {
                statusLabel.setText("  " + getString("editor_status_field_applied"));
            }
        }
    }

    //
    // プレイヤー編集パネル
    //
    static class PlayerEditPanel extends JPanel {

        private Player current;
        private JLabel statusLabel;

        private final JTextField               cashField  = new JTextField(12);
        private final JLabel                   holdLabel  = new JLabel(getString("editor_dash"));
        private final DefaultListModel<String> invModel      = new DefaultListModel<>();
        private final JList<String>            invList       = new JList<>(invModel);
        private final List<Entity>             invItems      = new ArrayList<>();
        private JButton                        editYukkuriBtn;

        PlayerEditPanel() {
            invList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            setLayout(new BorderLayout());
            JScrollPane scroll = new JScrollPane(buildForm());
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            scroll.setBorder(BorderFactory.createTitledBorder(getString("editor_panel_player")));
            add(scroll, BorderLayout.CENTER);
            setPlayer(null);
        }

        void setStatusLabel(JLabel lbl) {
            this.statusLabel = lbl;
        }

        private JPanel buildForm() {
            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(3, 6, 3, 6);
            c.anchor = GridBagConstraints.WEST;
            int row = 0;

            row = addHdr(form, c, row, getString("editor_sec_player_status"));
            row = addFormRow(form, c, row, getString("editor_lbl_cash"), cashField);

            JPanel holdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            holdPanel.add(holdLabel);
            JButton clearHoldBtn = new JButton(getString("editor_btn_clear_hold"));
            clearHoldBtn.addActionListener(e -> {
                if (current != null) {
                    current.setHoldItem(null);
                    holdLabel.setText(getString("editor_none"));
                }
            });
            holdPanel.add(clearHoldBtn);
            row = addRawRow(form, c, row, getString("editor_lbl_hold_item"), holdPanel);

            row = addHdr(form, c, row, getString("editor_sec_inventory"));

            JScrollPane invScroll = new JScrollPane(invList);
            invScroll.setPreferredSize(new Dimension(300, 150));
            c.gridx = 0;
            c.gridy = row;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1.0;
            c.weighty = 0.3;
            c.insets = new Insets(3, 6, 3, 6);
            form.add(invScroll, c);
            c.weighty = 0;
            row++;

            invList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && editYukkuriBtn != null) {
                    int[] sel = invList.getSelectedIndices();
                    editYukkuriBtn.setEnabled(sel.length == 1 && sel[0] < invItems.size()
                            && invItems.get(sel[0]) instanceof Yukkuri);
                }
            });

            JButton addFoodBtn = new JButton(getString("editor_btn_add_food"));
            addFoodBtn.addActionListener(e -> onAddFood());
            JButton addInvYukkuriBtn = new JButton(getString("editor_btn_add_yukkuri"));
            addInvYukkuriBtn.addActionListener(e -> onAddInventoryYukkuri());
            editYukkuriBtn = new JButton(getString("editor_btn_edit_yukkuri"));
            editYukkuriBtn.setEnabled(false);
            editYukkuriBtn.addActionListener(e -> onEditInventoryYukkuri());
            JButton removeBtn = new JButton(getString("editor_btn_delete"));
            removeBtn.addActionListener(e -> onRemoveItem());
            JPanel invBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            invBtnPanel.add(addFoodBtn);
            invBtnPanel.add(addInvYukkuriBtn);
            invBtnPanel.add(removeBtn);
            invBtnPanel.add(editYukkuriBtn);
            c.gridx = 0;
            c.gridy = row;
            c.gridwidth = 2;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.WEST;
            c.insets = new Insets(3, 6, 3, 6);
            form.add(invBtnPanel, c);
            row++;

            JButton applyBtn = new JButton(getString("editor_btn_apply"));
            applyBtn.addActionListener(e -> apply());
            c.gridx = 0;
            c.gridy = row;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.CENTER;
            c.insets = new Insets(12, 6, 6, 6);
            form.add(applyBtn, c);

            c.gridy = row + 1;
            c.weighty = 1.0;
            c.fill = GridBagConstraints.VERTICAL;
            form.add(new JPanel(), c);
            return form;
        }

        private int addHdr(JPanel p, GridBagConstraints c, int row, String text) {
            c.gridx = 0;
            c.gridy = row;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.WEST;
            c.insets = new Insets(8, 4, 2, 4);
            c.weightx = 1.0;
            JLabel lbl = new JLabel(text);
            lbl.setForeground(new Color(0, 70, 150));
            p.add(lbl, c);
            c.gridwidth = 1;
            c.insets = new Insets(3, 6, 3, 6);
            c.weightx = 0;
            return row + 1;
        }

        private int addFormRow(JPanel p, GridBagConstraints c, int row, String label, JComponent field) {
            c.gridx = 0;
            c.gridy = row;
            c.gridwidth = 1;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.EAST;
            c.insets = new Insets(3, 6, 3, 3);
            c.weightx = 0;
            p.add(new JLabel(label), c);
            c.gridx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.WEST;
            c.insets = new Insets(3, 3, 3, 6);
            c.weightx = 1.0;
            p.add(field, c);
            return row + 1;
        }

        private int addRawRow(JPanel p, GridBagConstraints c, int row, String label, JPanel panel) {
            c.gridx = 0;
            c.gridy = row;
            c.gridwidth = 1;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.EAST;
            c.insets = new Insets(3, 6, 3, 3);
            c.weightx = 0;
            p.add(new JLabel(label), c);
            c.gridx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.WEST;
            c.insets = new Insets(3, 3, 3, 6);
            c.weightx = 1.0;
            p.add(panel, c);
            return row + 1;
        }

        void setPlayer(Player p) {
            current = p;
            if (p != null) {
                cashField.setText(String.valueOf(p.getCash()));
                Entity h = p.getHoldItem();
                holdLabel.setText(h != null ? h.getClass().getSimpleName() : getString("editor_none"));
                refreshInvList();
            } else {
                cashField.setText("");
                holdLabel.setText(getString("editor_dash"));
                invModel.clear();
                invItems.clear();
            }
        }

        private void refreshInvList() {
            invModel.clear();
            invItems.clear();
            if (current == null) {
                return;
            }
            List<Entity> items = current.getItemForSave();
            if (items == null) {
                return;
            }
            for (Entity e : items) {
                invItems.add(e);
                invModel.addElement(describeItem(e));
            }
        }

        private static String describeItem(Entity e) {
            if (e instanceof Food) {
                Food f = (Food) e;
                String label = foodTypeLabel(f.getFoodType());
                return getString("editor_item_food") + " [" + label + "] ×" + f.getAmount();
            }
            if (e instanceof Yukkuri) {
                Yukkuri y = (Yukkuri) e;
                return yukkuriLabel(y.getClass().getSimpleName());
            }
            return e.getClass().getSimpleName();
        }

        private static String foodTypeLabel(FoodType type) {
            if (type == null) {
                return "?";
            }
            for (LabeledItem item : makeFoodTypeItems()) {
                if (type.name().equals(item.enumName)) {
                    return item.label;
                }
            }
            return type.name();
        }

        @SuppressWarnings({"unchecked"})
        private void onAddInventoryYukkuri() {
            if (current == null) {
                return;
            }
            List<String> displayList = new ArrayList<String>();
            displayList.add(getString("editor_hybrid_label") + "  (HybridYukkuri)");
            for (YukkuriType t : NON_HYBRID_TYPES) {
                displayList.add(t.getJapaneseName() + "  (" + t.getClassName() + ")");
            }
            final JComboBox<String> typeBox = new JComboBox<>(displayList.toArray(new String[0]));
            String[] nonHybridNames = new String[NON_HYBRID_TYPES.length];
            for (int i = 0; i < NON_HYBRID_TYPES.length; i++) {
                nonHybridNames[i] = NON_HYBRID_TYPES[i].getJapaneseName()
                        + "  (" + NON_HYBRID_TYPES[i].getClassName() + ")";
            }
            final JLabel p1Lbl = new JLabel(getString("editor_dlg_parent1"));
            final JLabel p2Lbl = new JLabel(getString("editor_dlg_parent2"));
            final JComboBox<String> parent1Box = new JComboBox<>(nonHybridNames);
            final JComboBox<String> parent2Box = new JComboBox<>(nonHybridNames);
            final JPanel dlg = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(4, 6, 4, 6);
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;
            addDlgRow(dlg, c, 0, getString("editor_dlg_type"), typeBox);
            addDlgRow(dlg, c, 1, p1Lbl, parent1Box);
            addDlgRow(dlg, c, 2, p2Lbl, parent2Box);
            updateHybridVisibility(typeBox.getSelectedIndex() == 0, p1Lbl, parent1Box, p2Lbl, parent2Box);
            typeBox.addActionListener(ev ->
                    updateHybridVisibility(typeBox.getSelectedIndex() == 0,
                            p1Lbl, parent1Box, p2Lbl, parent2Box));
            if (JOptionPane.showConfirmDialog(this, dlg, getString("editor_dlg_add_yukkuri_inv"),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
                return;
            }
            try {
                boolean isHybrid = typeBox.getSelectedIndex() == 0;
                Yukkuri newY;
                if (isHybrid) {
                    HybridYukkuri hybrid = new HybridYukkuri();
                    YukkuriType type1 = NON_HYBRID_TYPES[parent1Box.getSelectedIndex()];
                    YukkuriType type2 = NON_HYBRID_TYPES[parent2Box.getSelectedIndex()];
                    String fullPkg = "org.simyukkuri.entity.core.living.yukkuri.impl.";
                    Yukkuri donor1 = ((Class<? extends Yukkuri>) Class.forName(
                            fullPkg + type1.getClassName())).newInstance();
                    Field doreiField1 = hybrid.getClass().getDeclaredField("dorei");
                    doreiField1.setAccessible(true);
                    doreiField1.set(hybrid, donor1);
                    Yukkuri donor2 = ((Class<? extends Yukkuri>) Class.forName(
                            fullPkg + type2.getClassName())).newInstance();
                    Field doreiField2 = hybrid.getClass().getDeclaredField("dorei2");
                    doreiField2.setAccessible(true);
                    doreiField2.set(hybrid, donor2);
                    Field doreiField3 = hybrid.getClass().getDeclaredField("dorei3");
                    doreiField3.setAccessible(true);
                    doreiField3.set(hybrid, donor1);
                    Field doreiField4 = hybrid.getClass().getDeclaredField("dorei4");
                    doreiField4.setAccessible(true);
                    doreiField4.set(hybrid, donor2);
                    newY = hybrid;
                } else {
                    YukkuriType yukkuriType = NON_HYBRID_TYPES[typeBox.getSelectedIndex() - 1];
                    String fullName = "org.simyukkuri.entity.core.living.yukkuri.impl."
                            + yukkuriType.getClassName();
                    newY = ((Class<? extends Yukkuri>) Class.forName(fullName)).newInstance();
                }
                if (current.getItemForSave() == null) {
                    current.setItemForSave(new LinkedList<Entity>());
                }
                current.getItemForSave().add(newY);
                refreshInvList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        getString("editor_err_add") + "\n" + ex.getMessage(),
                        getString("editor_err_title"), JOptionPane.ERROR_MESSAGE);
            }
        }

        private static void addDlgRow(JPanel panel, GridBagConstraints c,
                int rowIndex, Object label, JComponent field) {
            c.gridx = 0;
            c.gridy = rowIndex;
            c.gridwidth = 1;
            if (label instanceof String) {
                panel.add(new JLabel((String) label), c);
            } else if (label instanceof JLabel) {
                panel.add((JLabel) label, c);
            }
            c.gridx = 1;
            panel.add(field, c);
        }

        private static void updateHybridVisibility(boolean hybrid,
                JLabel p1Lbl, JComboBox<String> parent1Box,
                JLabel p2Lbl, JComboBox<String> parent2Box) {
            p1Lbl.setVisible(hybrid);
            parent1Box.setVisible(hybrid);
            p2Lbl.setVisible(hybrid);
            parent2Box.setVisible(hybrid);
        }

        private void onAddFood() {
            if (current == null) {
                return;
            }
            NumericField amtF = new NumericField(6);
            amtF.setText("10");
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(4, 6, 4, 6);
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;
            JPanel dlg = new JPanel(new GridBagLayout());
            c.gridx = 0;
            c.gridy = 0;
            dlg.add(new JLabel(getString("editor_prop_kind")), c);
            c.gridx = 1;
            JComboBox<LabeledItem> ftBox = new JComboBox<>(makeFoodTypeItems());
            dlg.add(ftBox, c);
            c.gridx = 0;
            c.gridy = 1;
            dlg.add(new JLabel(getString("editor_prop_amount")), c);
            c.gridx = 1;
            dlg.add(amtF, c);
            if (JOptionPane.showConfirmDialog(this, dlg, getString("editor_dlg_add_food"),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
                return;
            }
            Food food = new Food();
            LabeledItem sel = (LabeledItem) ftBox.getSelectedItem();
            food.setFoodType(FoodType.valueOf(sel.enumName));
            food.setAmount(parseInt(amtF, 10));
            if (current.getItemForSave() == null) {
                current.setItemForSave(new LinkedList<Entity>());
            }
            current.getItemForSave().add(food);
            refreshInvList();
        }

        private void onRemoveItem() {
            if (current == null) {
                return;
            }
            int[] indices = invList.getSelectedIndices();
            if (indices.length == 0) {
                JOptionPane.showMessageDialog(this, getString("editor_err_select_item"));
                return;
            }
            List<Entity> toRemove = new ArrayList<>();
            for (int i : indices) {
                if (i < invItems.size()) {
                    toRemove.add(invItems.get(i));
                }
            }
            if (toRemove.isEmpty()) {
                return;
            }
            String msg = toRemove.size() == 1
                    ? String.format(getString("editor_dlg_del_confirm"), toRemove.get(0).getClass().getSimpleName(), toRemove.get(0).getObjId())
                    : String.format(getString("editor_dlg_del_multi_confirm"), toRemove.size());
            if (JOptionPane.showConfirmDialog(this, msg, getString("editor_dlg_del_title"),
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
            for (Entity e : toRemove) {
                current.getItemForSave().remove(e);
            }
            refreshInvList();
        }

        private void onEditInventoryYukkuri() {
            int idx = invList.getSelectedIndex();
            if (idx < 0 || idx >= invItems.size()) {
                return;
            }
            Entity e = invItems.get(idx);
            if (!(e instanceof Yukkuri)) {
                return;
            }
            Yukkuri y = (Yukkuri) e;

            YukkuriEditPanel panel = new YukkuriEditPanel();
            panel.setYukkuri(y);
            panel.setPreferredSize(new Dimension(420, 700));

            JDialog dlg = new JDialog();
            dlg.setTitle(getString("editor_dlg_edit_inv_yukkuri"));
            dlg.setModal(true);
            dlg.add(panel);
            dlg.pack();
            dlg.setLocationRelativeTo(this);
            dlg.setVisible(true);

            refreshInvList();
        }

        private void apply() {
            if (current == null) {
                return;
            }
            try {
                current.setCash(parseLong(cashField, current.getCash()));
                if (statusLabel != null) {
                    statusLabel.setText("  " + getString("editor_status_player_applied"));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        getString("editor_err_apply") + "\n" + ex.getMessage(),
                        getString("editor_err_title"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //
    // ワールド設定パネル
    //
    static class WorldEditPanel extends JPanel {

        private static final String[] WINDOW_MODES = {
            GameText.read("window_900_700"),
            GameText.read("window_1260_980"),
            GameText.read("full_screen")
        };
        private static final String[] FIELD_SCALES = SimYukkuri.fieldScaleTbl;

        private World current;
        private JLabel statusLabel;

        private final JComboBox<String> windowTypeCombo     = new JComboBox<>(WINDOW_MODES);
        private final JComboBox<String> terrariumSizeCombo  = new JComboBox<>(FIELD_SCALES);
        private final JCheckBox         unyoCheck           = new JCheckBox(GameText.read("unyo_on"));

        WorldEditPanel() {
            setLayout(new BorderLayout());
            JScrollPane scroll = new JScrollPane(buildForm());
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            scroll.setBorder(BorderFactory.createTitledBorder(getString("editor_panel_world")));
            add(scroll, BorderLayout.CENTER);
            setWorld(null);
        }

        void setStatusLabel(JLabel lbl) {
            this.statusLabel = lbl;
        }

        private JPanel buildForm() {
            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(3, 6, 3, 6);
            c.anchor = GridBagConstraints.WEST;
            int row = 0;

            row = addHdr(form, c, row, getString("editor_sec_window"));
            row = addFormRow(form, c, row, getString("editor_lbl_window_mode"),   windowTypeCombo);
            row = addFormRow(form, c, row, getString("editor_lbl_field_scale"),   terrariumSizeCombo);

            row = addHdr(form, c, row, getString("editor_sec_game_options"));
            c.gridx = 0;
            c.gridy = row;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.WEST;
            c.insets = new Insets(3, 6, 3, 6);
            form.add(unyoCheck, c);
            row++;

            JButton applyBtn = new JButton(getString("editor_btn_apply"));
            applyBtn.addActionListener(e -> apply());
            c.gridx = 0;
            c.gridy = row;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.CENTER;
            c.insets = new Insets(10, 6, 6, 6);
            form.add(applyBtn, c);

            return form;
        }

        private int addHdr(JPanel p, GridBagConstraints c, int row, String text) {
            c.gridx = 0;
            c.gridy = row;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.WEST;
            c.insets = new Insets(8, 6, 2, 6);
            c.weightx = 1.0;
            JLabel lbl = new JLabel(text);
            lbl.setFont(lbl.getFont().deriveFont(java.awt.Font.BOLD));
            p.add(lbl, c);
            c.weightx = 0;
            return row + 1;
        }

        private int addFormRow(JPanel p, GridBagConstraints c, int row, String label, JComponent field) {
            c.gridx = 0;
            c.gridy = row;
            c.gridwidth = 1;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.EAST;
            c.insets = new Insets(3, 6, 3, 3);
            c.weightx = 0;
            p.add(new JLabel(label), c);
            c.gridx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.WEST;
            c.insets = new Insets(3, 3, 3, 6);
            c.weightx = 1.0;
            p.add(field, c);
            return row + 1;
        }

        void setWorld(World w) {
            current = w;
            if (w != null) {
                int wt = Math.max(0, Math.min(w.getWindowType(), WINDOW_MODES.length - 1));
                int ts = Math.max(0, Math.min(w.getTerrariumSizeIndex(), FIELD_SCALES.length - 1));
                windowTypeCombo.setSelectedIndex(wt);
                terrariumSizeCombo.setSelectedIndex(ts);
                unyoCheck.setSelected(w.isUnyo());
            } else {
                windowTypeCombo.setSelectedIndex(1);
                terrariumSizeCombo.setSelectedIndex(1);
                unyoCheck.setSelected(true);
            }
            setEnabled(w != null);
            windowTypeCombo.setEnabled(w != null);
            terrariumSizeCombo.setEnabled(w != null);
            unyoCheck.setEnabled(w != null);
        }

        private void apply() {
            if (current == null) {
                return;
            }
            current.setWindowType(windowTypeCombo.getSelectedIndex());
            current.setTerrariumSizeIndex(terrariumSizeCombo.getSelectedIndex());
            current.setUnyo(unyoCheck.isSelected());
            if (statusLabel != null) {
                statusLabel.setText("  " + getString("editor_status_world_applied"));
            }
        }
    }

    //
    // 数値専用 JTextField
    //
    static class NumericField extends JTextField {
        NumericField(int cols) {
            super(cols);
            ((AbstractDocument) getDocument()).setDocumentFilter(new DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int off, String str, AttributeSet a)
                        throws BadLocationException {
                    if (str != null && str.matches("\\d*")) {
                        fb.insertString(off, str, a);
                    }
                }

                @Override
                public void replace(FilterBypass fb, int off, int len, String str, AttributeSet a)
                        throws BadLocationException {
                    if (str != null && str.matches("\\d*")) {
                        fb.replace(off, len, str, a);
                    }
                }
            });
        }
    }

    //
    // ユーティリティ
    //
    private static void selectLabeled(JComboBox<LabeledItem> box, String enumName) {
        if (enumName == null) {
            box.setSelectedIndex(0);
            return;
        }
        for (int i = 0; i < box.getItemCount(); i++) {
            if (enumName.equals(box.getItemAt(i).enumName)) {
                box.setSelectedIndex(i);
                return;
            }
        }
        box.setSelectedIndex(0);
    }

    static int parseInt(JTextField f, int def) {
        try {
            return Integer.parseInt(f.getText().trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    static long parseLong(JTextField f, long def) {
        try {
            return Long.parseLong(f.getText().trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
