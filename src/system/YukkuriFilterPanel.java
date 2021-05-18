package src.system;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import src.SimYukkuri;
import src.enums.YukkuriType;

public class YukkuriFilterPanel {
	boolean[] beforeSelectedType;
	static YukkuriType[] yukkuriTypes =
		{
		// 通常種
		YukkuriType.MARISA,
		YukkuriType.MARISATSUMURI,
		YukkuriType.MARISAKOTATSUMURI,
		YukkuriType.DOSMARISA,
		YukkuriType.REIMU,
		YukkuriType.WASAREIMU,
		YukkuriType.DEIBU,
		YukkuriType.ALICE,
		YukkuriType.PATCH,
		YukkuriType.CHEN,
		YukkuriType.MYON,
		// 希少種
		YukkuriType.YURUSANAE,
		YukkuriType.AYAYA,
		YukkuriType.KIMEEMARU,
		YukkuriType.TENKO,
		YukkuriType.UDONGE,
		YukkuriType.MEIRIN,
		YukkuriType.SUWAKO,
		YukkuriType.CHIRUNO,
		YukkuriType.EIKI,
		YukkuriType.RAN,
		YukkuriType.NITORI,
		YukkuriType.YUUKA,
		YukkuriType.SAKUYA,
		// 捕食種
		YukkuriType.REMIRYA,
		YukkuriType.FRAN,
		YukkuriType.YUYUKO,		
		// その他
		YukkuriType.TARINAI,
		YukkuriType.TARINAIREIMU,
		YukkuriType.MARISAREIMU,
		YukkuriType.REIMUMARISA,
		YukkuriType.HYBRIDYUKKURI,
		};
	/** 選択アクション */
	public static enum Action {
		SELECT_ALL("全選択", ""),
		DSELECT_ALL("全選択解除", ""),
		;
        private String name;
        Action(String nameJ, String nameE) { this.name = nameJ; }
        public String toString() { return name; }
	}
	/** コンストラクタ */
	public YukkuriFilterPanel() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	/**
	 * フィルターパネルを開く.
	 * @param strHead "対象設定"など
	 * @param strTop トップに表示する文字列
	 * @param istrOptionList フィルターするゆっくりの性質リスト
	 * @param ioResultSelectType 選ばれるタイプのリスト
	 * @param obOptionSelection 初期選択配列
	 * @return OKされたかどうか
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "unused", "static-access" })
	public static boolean openFilterPanel( String strHead, String strTop, List<String> istrOptionList,List<YukkuriType> ioResultSelectType, List<Boolean> obOptionSelection )
	{
	    List<YukkuriType> retSelectedType = new LinkedList<YukkuriType>();
	    int nListSize = yukkuriTypes.length;
	    int nOptionListSize = 0;
	    if( istrOptionList != null )
	    {
	    	nOptionListSize = istrOptionList.size();
	    }
	
	    JComboBox cb1 = new JComboBox();
	    JCheckBox[] checkBox = new JCheckBox[nListSize+nOptionListSize];
	    JPanel mainPanel = new JPanel();
	    JPanel yukkuriPanel = new JPanel();
	    JPanel optionPanel = new JPanel();
	    JPanel buttonPanel = new JPanel();
	    JPanel centerPanel = new JPanel();
	    JPanel center2Panel = new JPanel();

	    // レイアウト
	    GridBagLayout layout = new GridBagLayout();
	    mainPanel.setLayout(layout);
	    GridBagConstraints constraints = new GridBagConstraints();
	    constraints.gridx = 0;
	    constraints.gridy = 0;
	    constraints.fill = GridBagConstraints.HORIZONTAL ;
	    layout.setConstraints(center2Panel, constraints);// 制約の設定    
	    mainPanel.add(center2Panel);
	    constraints.gridx = 0;
	    constraints.gridy = 1;
	    constraints.fill = GridBagConstraints.HORIZONTAL ;
	    layout.setConstraints(yukkuriPanel, constraints);// 制約の設定    
	    mainPanel.add(yukkuriPanel);
	    constraints.gridx = 0;
	    constraints.gridy = 2;
	    constraints.fill = GridBagConstraints.HORIZONTAL ;
	    layout.setConstraints(optionPanel, constraints);// 制約の設定    
	    mainPanel.add(optionPanel);
	    constraints.gridx = 0;
	    constraints.gridy = 3;
	    constraints.anchor = GridBagConstraints.WEST;
	    constraints.fill = GridBagConstraints.HORIZONTAL ;
	    layout.setConstraints(buttonPanel, constraints);// 制約の設定    
	    mainPanel.add(buttonPanel);
	    	    
	    yukkuriPanel.setLayout(new GridLayout(9, 4));
	    optionPanel.setLayout(new GridLayout(2, 5));
	    buttonPanel.setLayout(new GridLayout(1, 5));
	
	    // 枠線設定
		LineBorder border = new LineBorder(Color.BLACK, 1, true);
		yukkuriPanel.setBorder(border);
		optionPanel.setBorder(border);
		//buttonPanel.setBorder(border);

   
	    String names2[] = new String[nListSize+nOptionListSize];
	    for(int k = 0; k < nListSize ; k++)
	    {
	    	String strTemp = yukkuriTypes[k].nameJ;
	    	if( strTemp.length() != 0 )
	    	{
	    		names2[k] = yukkuriTypes[k].nameJ;
	    	}else{
	    		names2[k] = yukkuriTypes[k].toString();
	    	}
	    }
	    
	    if( istrOptionList != null )
	    {
		    for(int k = nListSize; k < nListSize+nOptionListSize ; k++)
		    {
	    		names2[k] = istrOptionList.get(k-nListSize);	    	
		    }
	    }
	    
	    JLabel l1 = new JLabel(strHead);
	    centerPanel.add(l1);
	    cb1 = new JComboBox(names2);
	    cb1.setSelectedIndex(0);
	    centerPanel.add(cb1);
	    int checkIdx = cb1.getSelectedIndex();
	    JLabel l2 = new JLabel(strTop);
	    center2Panel.add(l2);
	    JLabel l3 = new JLabel("");
	    
	    center2Panel.add(l3);
	    for(int i = 0; i < nListSize; i++)
	    {
	        checkBox[i] = new JCheckBox(names2[i].toString());
	        if( ioResultSelectType != null && ioResultSelectType.size() != 0)
	        {
	        	if( ioResultSelectType.contains( yukkuriTypes[i] ))
	        	{
	        		checkBox[i].setSelected(true);
	        	}else{
	        		checkBox[i].setSelected(false);
	        	}
	        }
	        yukkuriPanel.add(checkBox[i]);
	    }
	    
	    for(int i = nListSize; i < nListSize + nOptionListSize; i++)
	    {
	        checkBox[i] = new JCheckBox(names2[i].toString());
	        if( obOptionSelection != null && nOptionListSize == obOptionSelection.size() )
	        {
	        	if( obOptionSelection.get( i - nListSize ))
	        	{
	        		checkBox[i].setSelected(true);
	        	}else{
	        		checkBox[i].setSelected(false);
	        	}
	        }
	        optionPanel.add(checkBox[i]);
	    }

		ButtonListener buttonListener = new ButtonListener();
		buttonListener.checkbox = checkBox;
		
		Action[] action = Action.values();
		for(int i = 0; i < action.length; i++) {
			JButton but = new JButton(action[i].toString());
			but.setActionCommand(action[i].name());
			but.addActionListener(buttonListener);
			buttonPanel.add(but);
		}
		
	    int dlgRet = JOptionPane.showConfirmDialog(SimYukkuri.mypane, mainPanel, strHead, 2, -1);
	    if(dlgRet == 0)
	    {
	        for(int i = 0; i < nListSize; i++)
	        {
	        	if( checkBox[i].isSelected() )
	        	{
	        		retSelectedType.add( yukkuriTypes[i] );
	        	}
	        }
	        ioResultSelectType.clear();
	        ioResultSelectType.addAll(retSelectedType);
	        
	        if( obOptionSelection != null )
	        {
		        obOptionSelection.clear();
		        for(int i = nListSize; i < nListSize + nOptionListSize; i++)
		        {
		        	if( checkBox[i].isSelected() )
		        	{
		        		obOptionSelection.add(true);
		        	}else{
		        		obOptionSelection.add(false);
		        	}
		        }
	        }
	        
		    return true;
	    }
	    
	    return false;
	}
	/**
	 * ボタンリスナ
	 */
	public static class ButtonListener implements ActionListener {

		public static JCheckBox[] checkbox;
		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			Action select = Action.valueOf(command);
			if( checkbox == null || checkbox.length == 0)
			{
				return;
			}

			switch(select){
			case SELECT_ALL:
				for( JCheckBox cb:checkbox)
				{
					cb.setSelected(true);
				}
				break;
			case DSELECT_ALL:
				for( JCheckBox cb:checkbox)
				{
					cb.setSelected(false);
				}
				break;
			default:
				break;
			}
		}
	}
}
