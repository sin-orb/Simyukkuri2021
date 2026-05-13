package src.draw;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JOptionPane;

import src.SimYukkuri;
import src.entity.core.attachment.impl.ANYDAmpoule;
import src.entity.core.attachment.impl.AccelAmpoule;
import src.entity.core.attachment.impl.Ants;
import src.entity.core.attachment.impl.Badge;
import src.entity.core.attachment.impl.BreedingAmpoule;
import src.entity.core.attachment.impl.Fire;
import src.entity.core.attachment.impl.HungryAmpoule;
import src.entity.core.attachment.impl.Needle;
import src.entity.core.attachment.impl.OrangeAmpoule;
import src.entity.core.attachment.impl.PoisonAmpoule;
import src.entity.core.attachment.impl.StopAmpoule;
import src.entity.core.attachment.impl.VeryShitAmpoule;
import src.entity.core.effect.impl.BakeSmoke;
import src.entity.core.effect.impl.Hit;
import src.entity.core.effect.impl.Mix;
import src.entity.core.effect.impl.Steam;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.world.bodylinked.Stalk;
import src.entity.core.world.item.AutoFeeder;
import src.entity.core.world.item.Bed;
import src.entity.core.world.item.BeltconveyorObj;
import src.entity.core.world.item.BreedingPool;
import src.entity.core.world.item.Diffuser;
import src.entity.core.world.item.Food;
import src.entity.core.world.item.FoodMaker;
import src.entity.core.world.item.GarbageChute;
import src.entity.core.world.item.GarbageStation;
import src.entity.core.world.item.Generator;
import src.entity.core.world.item.HotPlate;
import src.entity.core.world.item.House;
import src.entity.core.world.item.MachinePress;
import src.entity.core.world.item.Mixer;
import src.entity.core.world.item.OrangePool;
import src.entity.core.world.item.ProcesserPlate;
import src.entity.core.world.item.ProductChute;
import src.entity.core.world.item.StickyPlate;
import src.entity.core.world.item.Stone;
import src.entity.core.world.item.Sui;
import src.entity.core.world.item.Toilet;
import src.entity.core.world.item.Toy;
import src.entity.core.world.item.Trampoline;
import src.entity.core.world.item.Trash;
import src.entity.core.world.item.Yunba;
import src.enums.YukkuriType;
import src.field.impl.Beltconveyor;
import src.field.impl.Farm;
import src.field.impl.Pool;
import src.system.LoadWindow;
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
				src.entity.core.living.yukkuri.YukkuriSprite.loadShadowImages(loader, pane);
				src.entity.core.world.mobile.Shit.loadImages(loader, pane);
				src.entity.core.world.mobile.Vomit.loadImages(loader, pane);
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
				src.entity.core.living.yukkuri.impl.Alice.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.Ayaya.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.Chen.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.Chiruno.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.Deibu.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.DosMarisa.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.Eiki.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.Fran.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.Kimeemaru.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.Marisa.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.MarisaKotatsumuri.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.MarisaReimu.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.MarisaTsumuri.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.Meirin.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.Myon.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.Nitori.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.Patch.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.Ran.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.Reimu.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.ReimuMarisa.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.Remirya.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.Sakuya.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.Suwako.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.Tarinai.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.TarinaiReimu.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.Tenko.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.Udonge.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.WasaReimu.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.Yurusanae.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.Yuuka.loadIniFile(loader);
				src.entity.core.living.yukkuri.impl.Yuyuko.loadIniFile(loader);

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
				Class<?> c = Class.forName("src.entity.core.living.yukkuri.impl." + type.getClassName());
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
