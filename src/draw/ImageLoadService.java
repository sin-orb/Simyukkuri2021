package src.draw;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JOptionPane;

import src.SimYukkuri;
import src.attachment.ANYDAmpoule;
import src.attachment.AccelAmpoule;
import src.attachment.Ants;
import src.attachment.Badge;
import src.attachment.BreedingAmpoule;
import src.attachment.Fire;
import src.attachment.HungryAmpoule;
import src.attachment.Needle;
import src.attachment.OrangeAmpoule;
import src.attachment.PoisonAmpoule;
import src.attachment.StopAmpoule;
import src.attachment.VeryShitAmpoule;
import src.base.Body;
import src.enums.YukkuriType;
import src.effect.BakeSmoke;
import src.effect.Hit;
import src.effect.Mix;
import src.effect.Steam;
import src.item.AutoFeeder;
import src.item.Bed;
import src.field.impl.Beltconveyor;
import src.item.BeltconveyorObj;
import src.item.BreedingPool;
import src.item.Diffuser;
import src.field.impl.Farm;
import src.item.Food;
import src.item.FoodMaker;
import src.item.GarbageChute;
import src.item.GarbageStation;
import src.item.Generator;
import src.item.HotPlate;
import src.item.House;
import src.item.MachinePress;
import src.item.Mixer;
import src.item.OrangePool;
import src.field.impl.Pool;
import src.item.ProcesserPlate;
import src.item.ProductChute;
import src.item.StickyPlate;
import src.item.Stone;
import src.item.Sui;
import src.item.Toilet;
import src.item.Toy;
import src.item.Trampoline;
import src.item.Trash;
import src.item.Yunba;
import src.game.Stalk;
import src.system.LoadWindow;
import src.system.ResourceUtil;
import src.util.GameText;
import src.util.GameWorld;

/**
 * MyPane の画像ロード処理をまとめる helper.
 */
public final class ImageLoadService {
	private ImageLoadService() {
	}

	public static void loadImages(MyPane pane, boolean isBg, boolean isItem, boolean isEffect, boolean isBody,
			boolean isAttach, boolean isIni) {
		try {
			LoadWindow win = new LoadWindow(SimYukkuri.getFrames()[0]);
			win.setVisible(true);

			ClassLoader loader = pane.getClass().getClassLoader();

			if (isBg) {
				win.addLine("Load Terrain");
				TerrainField.loadTerrain(GameWorld.get().getCurrentMap().getMapIndex(), loader, pane);
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
				ProcesserPlate.loadImages(loader, pane);
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
				Body.loadShadowImages(loader, pane);
				src.game.Shit.loadImages(loader, pane);
				src.game.Vomit.loadImages(loader, pane);
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
				ANYDAmpoule.loadImages(loader, pane);
				HungryAmpoule.loadImages(loader, pane);
				VeryShitAmpoule.loadImages(loader, pane);
				PoisonAmpoule.loadImages(loader, pane);
				BreedingAmpoule.loadImages(loader, pane);
				Badge.loadImages(loader, pane);
			}

			if (isIni) {
				win.addLine("Load Ini");
				src.yukkuri.Alice.loadIniFile(loader);
				src.yukkuri.Ayaya.loadIniFile(loader);
				src.yukkuri.Chen.loadIniFile(loader);
				src.yukkuri.Chiruno.loadIniFile(loader);
				src.yukkuri.Deibu.loadIniFile(loader);
				src.yukkuri.DosMarisa.loadIniFile(loader);
				src.yukkuri.Eiki.loadIniFile(loader);
				src.yukkuri.Fran.loadIniFile(loader);
				src.yukkuri.Kimeemaru.loadIniFile(loader);
				src.yukkuri.Marisa.loadIniFile(loader);
				src.yukkuri.MarisaKotatsumuri.loadIniFile(loader);
				src.yukkuri.MarisaReimu.loadIniFile(loader);
				src.yukkuri.MarisaTsumuri.loadIniFile(loader);
				src.yukkuri.Meirin.loadIniFile(loader);
				src.yukkuri.Myon.loadIniFile(loader);
				src.yukkuri.Nitori.loadIniFile(loader);
				src.yukkuri.Patch.loadIniFile(loader);
				src.yukkuri.Ran.loadIniFile(loader);
				src.yukkuri.Reimu.loadIniFile(loader);
				src.yukkuri.ReimuMarisa.loadIniFile(loader);
				src.yukkuri.Remirya.loadIniFile(loader);
				src.yukkuri.Sakuya.loadIniFile(loader);
				src.yukkuri.Suwako.loadIniFile(loader);
				src.yukkuri.Tarinai.loadIniFile(loader);
				src.yukkuri.TarinaiReimu.loadIniFile(loader);
				src.yukkuri.Tenko.loadIniFile(loader);
				src.yukkuri.Udonge.loadIniFile(loader);
				src.yukkuri.WasaReimu.loadIniFile(loader);
				src.yukkuri.Yurusanae.loadIniFile(loader);
				src.yukkuri.Yuuka.loadIniFile(loader);
				src.yukkuri.Yuyuko.loadIniFile(loader);

				SimYukkuri.NAGASI_MODE = src.draw.ModLoader.loadBodyIniMapForInt(loader,
						src.draw.ModLoader.getDataWorldIniDir(),
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

	public static void loadBodyImage(MyPane pane, YukkuriType type) {
		synchronized (SimYukkuri.lock) {
			try {
				ClassLoader loader = pane.getClass().getClassLoader();
				Class<?> c = Class.forName("src.yukkuri." + type.getClassName());
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

	public static ClassLoader getImageLoader(MyPane pane) {
		return pane.getClass().getClassLoader();
	}
}
