package org.simyukkuri.engine;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JOptionPane;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.MyPane;
import org.simyukkuri.draw.TerrainField;
import org.simyukkuri.entity.core.attachment.impl.ANYDAmpoule;
import org.simyukkuri.entity.core.attachment.impl.AccelAmpoule;
import org.simyukkuri.entity.core.attachment.impl.Ants;
import org.simyukkuri.entity.core.attachment.impl.Badge;
import org.simyukkuri.entity.core.attachment.impl.BreedingAmpoule;
import org.simyukkuri.entity.core.attachment.impl.Fire;
import org.simyukkuri.entity.core.attachment.impl.HungryAmpoule;
import org.simyukkuri.entity.core.attachment.impl.Needle;
import org.simyukkuri.entity.core.attachment.impl.OrangeAmpoule;
import org.simyukkuri.entity.core.attachment.impl.PoisonAmpoule;
import org.simyukkuri.entity.core.attachment.impl.StopAmpoule;
import org.simyukkuri.entity.core.attachment.impl.VeryShitAmpoule;
import org.simyukkuri.entity.core.effect.impl.BakeSmoke;
import org.simyukkuri.entity.core.effect.impl.Hit;
import org.simyukkuri.entity.core.effect.impl.Mix;
import org.simyukkuri.entity.core.effect.impl.Steam;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.entity.core.world.item.AutoFeeder;
import org.simyukkuri.entity.core.world.item.Bed;
import org.simyukkuri.entity.core.world.item.BeltconveyorObj;
import org.simyukkuri.entity.core.world.item.BreedingPool;
import org.simyukkuri.entity.core.world.item.Diffuser;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.entity.core.world.item.FoodMaker;
import org.simyukkuri.entity.core.world.item.GarbageChute;
import org.simyukkuri.entity.core.world.item.GarbageStation;
import org.simyukkuri.entity.core.world.item.Generator;
import org.simyukkuri.entity.core.world.item.HotPlate;
import org.simyukkuri.entity.core.world.item.House;
import org.simyukkuri.entity.core.world.item.MachinePress;
import org.simyukkuri.entity.core.world.item.Mixer;
import org.simyukkuri.entity.core.world.item.OrangePool;
import org.simyukkuri.entity.core.world.item.ProcessorPlate;
import org.simyukkuri.entity.core.world.item.ProductChute;
import org.simyukkuri.entity.core.world.item.StickyPlate;
import org.simyukkuri.entity.core.world.item.Stone;
import org.simyukkuri.entity.core.world.bodylinked.Okazari;
import org.simyukkuri.entity.core.world.item.Sui;
import org.simyukkuri.entity.core.world.item.Toilet;
import org.simyukkuri.entity.core.world.item.Toy;
import org.simyukkuri.entity.core.world.item.Trampoline;
import org.simyukkuri.entity.core.world.item.Trash;
import org.simyukkuri.entity.core.world.item.Yunba;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.field.impl.Beltconveyor;
import org.simyukkuri.field.impl.Farm;
import org.simyukkuri.field.impl.Pool;
import org.simyukkuri.ui.LoadWindow;
import org.simyukkuri.util.GameText;
import org.simyukkuri.util.GameWorld;

/**
 * MyPane の画像ロード処理をまとめる helper.
 */
public final class ImageLoadService {
	private ImageLoadService() {
	}

	/**
	 * 指定フラグに応じてゲーム画像を一括ロードする。
	 *
	 * @param pane     描画パネル
	 * @param isBg     背景画像をロードするか
	 * @param isItem   アイテム画像をロードするか
	 * @param isEffect エフェクト画像をロードするか
	 * @param isBody   ゆっくり画像をロードするか
	 * @param isAttach アタッチメント画像をロードするか
	 * @param isIni    INIデータをロードするか
	 */
	public static void loadImages(MyPane pane, boolean isBg, boolean isItem, boolean isEffect, boolean isBody,
			boolean isAttach, boolean isIni) {
		try {
			LoadWindow win = new LoadWindow(SimYukkuri.getFrames()[0]);
			win.setVisible(true);

			ClassLoader loader = pane.getClass().getClassLoader();

			if (isBg) {
				win.addLine("Load Terrain");
				TerrainField.loadTerrain(GameWorld.get().getCurrentWorldState().getWorldIndex(), loader, pane);
			}

			if (isItem) {
				win.addLine("Load Item");
				Food.loadImages(loader, pane);
				Toilet.loadImages(loader, pane);
				Bed.loadImages(loader, pane);
				Toy.loadImages(loader, pane);
				Stone.loadImages(loader, pane);
				BeltconveyorObj.loadImages(loader, pane);
				Beltconveyor.loadImages(loader, pane);
				BreedingPool.loadImages(loader, pane);
				GarbageChute.loadImages(loader, pane);
				FoodMaker.loadImages(loader, pane);
				OrangePool.loadImages(loader, pane);
				ProductChute.loadImages(loader, pane);
				MachinePress.loadImages(loader, pane);
				Diffuser.loadImages(loader, pane);
				Yunba.loadImages(loader, pane);
				StickyPlate.loadImages(loader, pane);
				HotPlate.loadImages(loader, pane);
				ProcessorPlate.loadImages(loader, pane);
				Mixer.loadImages(loader, pane);
				AutoFeeder.loadImages(loader, pane);
				Sui.loadImages(loader, pane);
				Trash.loadImages(loader, pane);
				GarbageStation.loadImages(loader, pane);
				House.loadImages(loader, pane);
				Pool.loadImages(loader, pane);
				Farm.loadImages(loader, pane);
				Trampoline.loadImages(loader, pane);
				Generator.loadImages(loader, pane);
			}

			if (isEffect) {
				win.addLine("Load Effect");
				BakeSmoke.loadImages(loader, pane);
				Hit.loadImages(loader, pane);
				Mix.loadImages(loader, pane);
				Steam.loadImages(loader, pane);
			}

			if (isBody) {
				win.addLine("Load Shadow/Poo-poo/Vomit");
				org.simyukkuri.entity.core.living.yukkuri.YukkuriSpriteDelegate.loadShadowImages(loader, pane);
				org.simyukkuri.entity.core.world.mobile.Shit.loadImages(loader, pane);
				org.simyukkuri.entity.core.world.mobile.Vomit.loadImages(loader, pane);
			}

			if (isAttach) {
				win.addLine("Load Attachment");
				Stalk.loadImages(loader, pane);
				Fire.loadImages(loader, pane);
				Ants.loadImages(loader, pane);
				Needle.loadImages(loader, pane);
				OrangeAmpoule.loadImages(loader, pane);
				AccelAmpoule.loadImages(loader, pane);
				StopAmpoule.loadImages(loader, pane);
				Okazari.loadImages(loader, pane);
				ANYDAmpoule.loadImages(loader, pane);
				HungryAmpoule.loadImages(loader, pane);
				VeryShitAmpoule.loadImages(loader, pane);
				PoisonAmpoule.loadImages(loader, pane);
				BreedingAmpoule.loadImages(loader, pane);
				Badge.loadImages(loader, pane);
			}

			if (isIni) {
				win.addLine("Load Ini");
				org.simyukkuri.entity.core.living.yukkuri.impl.Alice.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.Ayaya.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.Chen.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.Chiruno.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.Deibu.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.DosMarisa.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.Eiki.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.Fran.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.Kimeemaru.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.Marisa.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.MarisaKotatsumuri.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.MarisaReimu.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.MarisaTsumuri.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.Meirin.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.Myon.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.Nitori.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.Patch.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.Ran.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.Reimu.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.ReimuMarisa.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.Remirya.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.Sakuya.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.Suwako.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.Tarinai.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.TarinaiReimu.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.Tenko.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.Udonge.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.WasaReimu.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.Yurusanae.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.Yuuka.loadIniFile(loader);
				org.simyukkuri.entity.core.living.yukkuri.impl.Yuyuko.loadIniFile(loader);

				SimYukkuri.NAGASI_MODE = org.simyukkuri.engine.ModLoader.loadYukkuriIniValue(loader,
						org.simyukkuri.engine.ModLoader.getDataWorldIniDir(),
						"play", "NAGASI_MODE");
			}

			win.setVisible(false);
			win = null;
			System.gc();
		} catch (IOException e1) {
			System.out.println("File I/O error");
		} catch (OutOfMemoryError e) {
			JOptionPane.showMessageDialog(null, "Out of Memory!!");
		}
	}

	/**
	 * 指定種別のゆっくり画像をリフレクション経由でロードする。
	 *
	 * @param pane 描画パネル
	 * @param type ゆっくり種別
	 */
	public static void loadYukkuriImage(MyPane pane, YukkuriType type) {
		synchronized (SimYukkuri.lock) {
			try {
				ClassLoader loader = pane.getClass().getClassLoader();
				Class<?> c = Class.forName("org.simyukkuri.entity.core.living.yukkuri.impl." + type.getClassName());
				Method m = c.getMethod("loadImages", ClassLoader.class, java.awt.image.ImageObserver.class);
				m.invoke(null, loader, pane);
			} catch (SecurityException | NoSuchMethodException | ClassNotFoundException | IllegalArgumentException
					| IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				JOptionPane.showMessageDialog(null, GameText.read("out_of_memory"));
			}
		}
	}

	/**
	 * 指定パネルのクラスローダを返す。画像読み込みに使用する。
	 *
	 * @param pane 描画パネル
	 * @return クラスローダ
	 */
	public static ClassLoader getImageLoader(MyPane pane) {
		return pane.getClass().getClassLoader();
	}
}
