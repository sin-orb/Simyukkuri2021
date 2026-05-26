package org.simyukkuri.entity.core.world.bodylinked;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.enums.Direction;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.enums.Type;
import org.simyukkuri.enums.WorldEntityKind;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.system.ItemMenu.GetMenuTarget;
import org.simyukkuri.util.GameImages;
import org.simyukkuri.util.GameWorld;

/**
 * 茎
 */
public class Stalk extends WorldEntity {

	private static final long serialVersionUID = -7644967944795406729L;
	private static final int IMAGE_COUNT = 1; // このクラスの総使用画像数
	private static transient BufferedImage[] imageLayers = new BufferedImage[IMAGE_COUNT * 2 + 1];
	private static Rectangle4y boundary = new Rectangle4y();

	private int plantYukkuri = -1; // この茎が生えてる親
	private List<Integer> bindBabies = new LinkedList<Integer>(); // この茎にぶら下がってる子のID
	/** （食べたときの）量 */
	private int amount = 0;
	private UUID stalkId = UUID.randomUUID();

	/**
	 * StalkのUUIDを取得する.
	 * 
	 * @return StalkのUUID
	 */
	public UUID getStalkId() {
		return stalkId;
	}

	/**
	 * StalkのUUIDを設定する.
	 * 
	 * @param id StalkのUUID
	 */
	public void setStalkId(UUID id) {
		this.stalkId = id;
	}

	/**
	 * イメージをロードする.
	 * 
	 * @param loader ローダ
	 * @param io     イメージオブザーバ
	 * @throws IOException IO例外
	 */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		final String path = "images/yukkuri/general/";
		for (int i = 0; IMAGE_COUNT > i; i++) {
			imageLayers[i * 2] = ImageIO
					.read(loader.getResourceAsStream(path + "stalk" + String.format("%03d", i + 1) + ".png"));
			imageLayers[i * 2 + 1] = ModLoader.flipImage(imageLayers[i * 2]);
		}
		imageLayers[IMAGE_COUNT * 2] = GameImages.read(loader.getResourceAsStream(path + "stalk_shadow.png"));
		boundary.setWidth(imageLayers[0].getWidth(io));
		boundary.setHeight(imageLayers[0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() - 1);
	}

	/** 茎の画像をレイヤー配列にセットし、使用レイヤー数を返す。 */
	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if (option == 0) {
			layer[0] = imageLayers[1];
		} else {
			layer[0] = imageLayers[0];
		}
		return 1;
	}

	/** 実ゆが付いていない場合に茎の影画像を返す。付いている場合は null。 */
	@Override
	@Transient
	public BufferedImage getShadowImage() {
		if (plantYukkuri == -1) {
			return imageLayers[2];
		}
		return null;
	}

	/**
	 * 方向を設定する.
	 * 
	 * @param dir 方向
	 */
	public void setDirection(int dir) {
		if (dir == 0) {
			option = 0;
		} else {
			option = 1;
		}
	}

	/** 毎ティックの状態更新を行う。 */
	@Override
	public void upDate() {
		int i = 0;
		int babyX = 0;
		int babyZ = 0;
		if (getAttachedBabyIds() == null) {
			return;
		}
		Yukkuri parent = org.simyukkuri.util.YukkuriLookup.getYukkuriById(this.getPlantYukkuri());
		for (Integer j : getAttachedBabyIds()) {
			if (j == null) {
				i++;
				continue;
			}
			Yukkuri b = org.simyukkuri.util.YukkuriLookup.getYukkuriById(j);
			if (b == null) {
				i++;
				continue;
			}
			if (parent != null && b.isUnBirth()) {
				b.setParentLinkId(parent.getObjId());
				b.setBindStalk(this);
			}
			if (option == 0) {
				babyX = ((i % 5) * -5 + 14);
				b.setDirection(Direction.RIGHT);
			} else {
				babyX = ((i % 5) * -5 + 14) * -1;
				b.setDirection(Direction.LEFT);
			}
			babyZ = ((i % 5) * -2 + 14);
			b.setCalcX(getX() + babyX);
			b.setCalcY(getY() + 1);
			b.setCalcZ(getZ() + babyZ);
			b.kick(0, 0, 0);
			i++;
		}
	}

	/** ワールドからスタルクを除去する。 */
	@Override
	public void removeFromWorld() {
		remove();
		GameWorld.get().getCurrentWorldState().getStalks().remove(objId);
	}

	/**
	 * この茎をはやしているゆっくりを設定する.
	 * 
	 * @param b この茎をはやしているゆっくり
	 */
	@Transient
	public void setPlantYukkuri(Yukkuri b) {
		if (b == null) {
			plantYukkuri = -1;
		} else {
			plantYukkuri = b.getUniqueId();
		}
		bindObj = plantYukkuri;
	}

	/** 実ゆの ID を直接セットする。 */
	public void setPlantYukkuri(int plantYukkuri) {
		this.plantYukkuri = plantYukkuri;
	}

	/**
	 * この茎をはやしているゆっくりを取得する.
	 * 
	 * @return この茎をはやしているゆっくり
	 */
	public int getPlantYukkuri() {
		return plantYukkuri;
	}

	/** シリアライズ用の実ゆ ID を返す。 */
	public Integer getPyForSeri() {
		return plantYukkuri;
	}

	/** シリアライズ用の実ゆ ID をセットし、bindObj にも反映する。 */
	public void setPyForSeri(Integer s) {
		this.plantYukkuri = s;
		this.bindObj = s;
	}

	/*
	 * @Override
	 * public int getBindObj() {
	 * return plantYukkuri;
	 * }
	 */
	/**
	 * この茎に実ゆっくりを追加する.
	 * 
	 * @param b この茎に生やそうとしている実ゆっくり
	 */
	@Transient
	public void addAttachedBaby(Yukkuri b) {
		if (bindBabies.size() < 5) {
			bindBabies.add(b == null ? -1 : b.getUniqueId());
		}
	}

	/**
	 * この茎に生えている実ゆっくりを取得する.
	 * 
	 * @return この茎に生えている実ゆっくり
	 */
	public List<Integer> getAttachedBabyIds() {
		return bindBabies;
	}

	/**
	 * 茎から実ゆっくりをすべて取り除く.
	 */
	public void detachAttachedBabies() {
		if (plantYukkuri != -1) {
			Yukkuri planted = org.simyukkuri.util.YukkuriLookup.getYukkuriById(plantYukkuri);
			if (planted != null && planted.getStalks() != null) {
				planted.getStalks().set(planted.getStalks().indexOf(this), null);
			}
		}

		for (int i : bindBabies) {
			Yukkuri b = org.simyukkuri.util.YukkuriLookup.getYukkuriById(i);
			if (b != null) {
				b.setBindStalk(null);
			}
		}
	}

	/**
	 * X座標を設定する.
	 *
	 * @param x X座標
	 */
	@Transient
	public void setCalcX(int x) {
		if (x < 0 && plantYukkuri == -1) {
			this.x = 0;
		} else if (x > Translate.getWorldWidth() && plantYukkuri == -1) {
			this.x = Translate.getWorldWidth();
		} else {
			this.x = x;
		}
	}

	/**
	 * Y座標を設定する.
	 *
	 * @param y Y座標
	 */
	@Transient
	public void setCalcY(int y) {
		if (y < 0 && plantYukkuri == -1) {
			this.y = 0;
		} else if (y > Translate.getWorldHeight() && plantYukkuri == -1) {
			this.y = Translate.getWorldHeight();
		} else {
			this.y = y;
		}
	}

	/**
	 * Z座標を設定する.
	 *
	 * @param z Z座標
	 */
	@Transient
	public void setCalcZ(int z) {
		if (z < mostDepth && plantYukkuri == -1) {
			if (isFallingUnderGround()) {
				this.z = z;
			} else {
				this.z = mostDepth;
			}
		} else if (z > Translate.getWorldDepth() && plantYukkuri == -1) {
			this.z = Translate.getWorldDepth();
		} else {
			this.z = z;
		}
	}

	/**
	 * この茎がゆっくりから生えている状態であるかどうかを取得する.
	 * 
	 * @return この茎がゆっくりから生えている状態であるかどうか
	 */
	@Transient
	public boolean isPlantYukkuri() {
		for (int i : bindBabies) {
			Yukkuri b = org.simyukkuri.util.YukkuriLookup.getYukkuriById(i);
			if (b != null) {
				return true;
			}
		}
		return (plantYukkuri != -1);
	}

	/**
	 * 茎を食べる.
	 * 
	 * @param eatAmount 食べる量
	 */
	public void eatStalk(int eatAmount) {
		amount -= eatAmount;
		if (amount <= 0) {
			amount = 0;
			for (Integer i : bindBabies) {
				if (i == null) {
					continue;
				}
				Yukkuri b = org.simyukkuri.util.YukkuriLookup.getYukkuriById(i);
				if (b != null) {
					b.setBindStalk(null);
				}
			}
			remove();
			GameWorld.get().getCurrentWorldState().getStalks().remove(objId);
		}
	}

	/**
	 * Grab.
	 */
	@Override
	public void grab() {
		grabbed = true;
		if (takePlantYukkuri() != null) {
			takePlantYukkuri().removeStalk(this);
		}
		setPlantYukkuri(null);
	}

	/**
	 * 生えているゆっくりを取得する.
	 * 
	 * @return 生えているゆっくり
	 */
	public Yukkuri takePlantYukkuri() {
		return GameWorld.get().getCurrentWorldState().getYukkuriRegistry().get(plantYukkuri);
	}

	/** 毎ティックの状態更新を行う。 */
	@Override
	public TickResult clockTick() {
		setAge(getAge() + TICK);
		if (isRemoved()) {
			removeFromWorld();
			detachAttachedBabies();
			return TickResult.REMOVED;
		}
		if (!grabbed && plantYukkuri == -1) {
			if (vx != 0) {
				x += vx;
				if (x < 0) {
					x = 0;
					vx *= -1;
				} else if (x > Translate.getWorldWidth()) {
					x = Translate.getWorldWidth();
					vx *= -1;
				} else if (Barrier.onBarrier(x, y, getW() >> 2, getH() >> 2, Barrier.ITEM_BLOCK_FLAG)) {
					x -= vx;
					vx = 0;
				}
			}
			if (vy != 0) {
				y += vy;
				if (y < 0) {
					y = 0;
					vy *= -1;
				} else if (y > Translate.getWorldHeight()) {
					y = Translate.getWorldHeight();
					vy *= -1;
				} else if (Barrier.onBarrier(x, y, getW() >> 2, getH() >> 2, Barrier.ITEM_BLOCK_FLAG)) {
					y -= vy;
					vy = 0;
				}
			}
			if (z != 0 || vz != 0) {
				vz += 1;
				z -= vz;
				if (!isFallingUnderGround()) {
					if (z <= mostDepth) {
						z = mostDepth;
						vx = 0;
						vy = 0;
						vz = 0;
					}
				}
			}
		}
		upDate();
		calcPos();
		return TickResult.NONE;
	}

	/**
	 * コンストラクタ.
	 * 
	 * @param initX      初期X座標
	 * @param initY      初期Y座標
	 * @param initOption オプション
	 */
	public Stalk(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		objType = Type.OBJECT;
		worldEntityType = WorldEntityKind.STALK;
		amount = 100 * 24 * 5;
		GameWorld.get().getCurrentWorldState().getStalks().put(objId, this);
		GameWorld.get().getCurrentWorldState().registerEntity(objId, this);
		calcPos();
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public Stalk() {
		setBoundary(boundary);
		objType = Type.OBJECT;
		worldEntityType = WorldEntityKind.STALK;
		amount = 100 * 24 * 5;
		GameWorld.get().getCurrentWorldState().getStalks().put(objId, this);
		GameWorld.get().getCurrentWorldState().registerEntity(objId, this);
		calcPos();
	}

	/** 衝突判定対象タイプを返す（茎は 0 で判定なし）。 */
	@Override
	@Transient
	public int getHitCheckObjType() {
		return 0;
	}

	/** 衝突処理（茎は何もせず 0 を返す）。 */
	@Override
	public int objHitProcess(Entity o) {
		return 0;
	}

	/** 茎を除去し、紐付きゆっくりとの関係を解除する。 */
	@Override
	public void remove() {
		plantYukkuri = -1;
		for (Integer i : getAttachedBabyIds()) {
			if (i == null) {
				continue;
			}
			Yukkuri baby = org.simyukkuri.util.YukkuriLookup.getYukkuriById(i);
			if (baby != null) {
				baby.setBindStalk(null);
				baby.setBindObj(-1);
			}
		}
		bindBabies.clear();
		// GameWorld.get().getCurrentWorldState().getStalks().remove(this);
		super.remove();
	}

	// @Override
	// public String toString() {
	// Yukkuri p = org.simyukkuri.util.YukkuriLookup.getYukkuriById(plantYukkuri);
	// String ret = "";
	// ret += GameText.read("game_stalk1");
	// if (p != null) {
	// ret += (plantYukkuri == -1 ?
	// GameText.read("command_status_nothing") :
	// GameLocale.isJapanese() ?
	// p.getNameJ() : p.getNameE());
	// }
	// ret += GameText.read("game_stalk2");
	// if (bindBabies == null || bindBabies.size() == 0) {
	// ret += GameText.read("command_status_nothing");
	// } else {
	// for (Object o : bindBabies) {
	// if (o == null) {
	// continue;
	// } else {
	// Integer b = (Integer)o;
	// Yukkuri baby = org.simyukkuri.util.YukkuriLookup.getYukkuriById(b);
	// if (baby == null) {
	// ret += GameText.read("game_empty");
	// } else {
	// ret += GameLocale.isJapanese() ? baby.getNameJ() : baby.getNameE();
	// }
	// ret += ",";
	// }
	// }
	// ret = ret.substring(0, ret.length() - 1);
	// }
	// ret += ")";
	// return ret;
	// }

	/** 茎に実っている実ゆの数を返す。 */
	public int getAmount() {
		return amount;
	}

	/** 茎に実っている実ゆの数をセットする。 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/** 付きゆっくり（赤ゆ）の ID リストをセットする。 */
	public void setBindBabies(List<Integer> bindBaby) {
		this.bindBabies = bindBaby;
	}

	/** 茎をポップアップメニューの取得対象として返す。 */
	@Override
	public GetMenuTarget hasGetPopup() {
		return GetMenuTarget.STALK;
	}
}
