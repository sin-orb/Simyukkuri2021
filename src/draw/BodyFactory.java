package src.draw;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import src.SimYukkuri;
import src.base.Yukkuri;
import src.enums.AgeState;
import src.enums.YukkuriType;
import src.game.Dna;
import src.util.GameRandom;
import src.yukkuri.*;

/**
 * ゆっくり生成の分岐をまとめた factory.
 */
public final class BodyFactory {
	private BodyFactory() {
	}

	/**
	 * 生成後に家族関係を結ぶ callback.
	 */
	@FunctionalInterface
	public interface FamilyLinker {
		void link(Yukkuri mama, Yukkuri papa, Yukkuri child);
	}

	/**
	 * Yukkuri を生成する.
	 *
	 * @param x 発生場所X座標
	 * @param y 発生場所Y座標
	 * @param z 発生場所Z座標
	 * @param type ゆっくり種別
	 * @param dna 赤ゆのDNA
	 * @param age 追加時年齢
	 * @param p1 母親
	 * @param p2 父親
	 * @param buildNewFamily 家族を新規作成するか
	 * @param imageLoader 画像読み込み要求を受ける callback
	 * @param dosMaker DOS個体を生成するかどうかの callback
	 * @return 生成された Yukkuri
	 */
	public static Yukkuri create(int x, int y, int z, int type, Dna dna, AgeState age, Yukkuri p1, Yukkuri p2,
			boolean buildNewFamily, Consumer<YukkuriType> imageLoader, BooleanSupplier dosMaker) {
		return create(x, y, z, type, dna, age, p1, p2, buildNewFamily, imageLoader, dosMaker, null);
	}

	/**
	 * Yukkuri を生成する.
	 *
	 * @param x 発生場所X座標
	 * @param y 発生場所Y座標
	 * @param z 発生場所Z座標
	 * @param type ゆっくり種別
	 * @param dna 赤ゆのDNA
	 * @param age 追加時年齢
	 * @param p1 母親
	 * @param p2 父親
	 * @param buildNewFamily 家族を新規作成するか
	 * @param imageLoader 画像読み込み要求を受ける callback
	 * @param dosMaker DOS個体を生成するかどうかの callback
	 * @param familyLinker 家族関係の接続 callback
	 * @return 生成された Yukkuri
	 */
	public static Yukkuri create(int x, int y, int z, int type, Dna dna, AgeState age, Yukkuri p1, Yukkuri p2,
			boolean buildNewFamily, Consumer<YukkuriType> imageLoader, BooleanSupplier dosMaker,
			FamilyLinker familyLinker) {
		Yukkuri papa = p2;
		Yukkuri mama = p1;
		if (papa == null && dna != null) {
			papa = src.util.BodyRegistry.getBodyInstance(dna.getFather());
		}
		Yukkuri body;
		switch (type) {
			case Marisa.type:
				imageLoader.accept(YukkuriType.MARISA);
				body = new Marisa(x, y, z, age, mama, papa);
				break;
			case Reimu.type:
				imageLoader.accept(YukkuriType.REIMU);
				body = new Reimu(x, y, z, age, mama, papa);
				break;
			case Alice.type:
				imageLoader.accept(YukkuriType.ALICE);
				body = new Alice(x, y, z, age, mama, papa);
				break;
			case Patch.type:
				imageLoader.accept(YukkuriType.PATCH);
				body = new Patch(x, y, z, age, mama, papa);
				break;
			case Chen.type:
				imageLoader.accept(YukkuriType.CHEN);
				body = new Chen(x, y, z, age, mama, papa);
				break;
			case Myon.type:
				imageLoader.accept(YukkuriType.MYON);
				body = new Myon(x, y, z, age, mama, papa);
				break;
			case WasaReimu.type:
				imageLoader.accept(YukkuriType.REIMU);
				imageLoader.accept(YukkuriType.WASAREIMU);
				body = new WasaReimu(x, y, z, age, mama, papa);
				break;
			case MarisaTsumuri.type:
				imageLoader.accept(YukkuriType.MARISA);
				imageLoader.accept(YukkuriType.MARISATSUMURI);
				body = new MarisaTsumuri(x, y, z, age, mama, papa);
				break;
			case MarisaKotatsumuri.type:
				imageLoader.accept(YukkuriType.MARISAKOTATSUMURI);
				body = new MarisaKotatsumuri(x, y, z, age, mama, papa);
				break;
			case Deibu.type:
				imageLoader.accept(YukkuriType.REIMU);
				imageLoader.accept(YukkuriType.DEIBU);
				body = new Deibu(x, y, z, age, mama, papa);
				break;
			case DosMarisa.type:
				if (dosMaker.getAsBoolean()) {
					imageLoader.accept(YukkuriType.DOSMARISA);
					body = new DosMarisa(x, y, z, age, mama, papa);
					break;
				}
				imageLoader.accept(YukkuriType.MARISA);
				body = new Marisa(x, y, z, age, mama, papa);
				break;
			case Tarinai.type:
				imageLoader.accept(YukkuriType.TARINAI);
				body = new Tarinai(x, y, z, age, mama, papa);
				break;
			case TarinaiReimu.type:
				imageLoader.accept(YukkuriType.TARINAI);
				imageLoader.accept(YukkuriType.TARINAIREIMU);
				body = new TarinaiReimu(x, y, z, age, mama, papa);
				break;
			case MarisaReimu.type:
				imageLoader.accept(YukkuriType.REIMU);
				imageLoader.accept(YukkuriType.MARISAREIMU);
				body = new MarisaReimu(x, y, z, age, mama, papa);
				break;
			case ReimuMarisa.type:
				imageLoader.accept(YukkuriType.MARISA);
				imageLoader.accept(YukkuriType.REIMUMARISA);
				body = new ReimuMarisa(x, y, z, age, mama, papa);
				break;
			case HybridYukkuri.type:
				imageLoader.accept(YukkuriType.HYBRIDYUKKURI);
				body = new HybridYukkuri(x, y, z, age, mama, papa);
				break;
			case Remirya.type:
				imageLoader.accept(YukkuriType.REMIRYA);
				body = new Remirya(x, y, z, age, mama, papa);
				break;
			case Fran.type:
				imageLoader.accept(YukkuriType.FRAN);
				body = new Fran(x, y, z, age, mama, papa);
				break;
			case Ayaya.type:
				imageLoader.accept(YukkuriType.AYAYA);
				body = new Ayaya(x, y, z, age, mama, papa);
				break;
			case Chiruno.type:
				imageLoader.accept(YukkuriType.CHIRUNO);
				body = new Chiruno(x, y, z, age, mama, papa);
				break;
			case Eiki.type:
				imageLoader.accept(YukkuriType.EIKI);
				body = new Eiki(x, y, z, age, mama, papa);
				break;
			case Kimeemaru.type:
				imageLoader.accept(YukkuriType.KIMEEMARU);
				body = new Kimeemaru(x, y, z, age, mama, papa);
				break;
			case Meirin.type:
				imageLoader.accept(YukkuriType.MEIRIN);
				body = new Meirin(x, y, z, age, mama, papa);
				break;
			case Nitori.type:
				imageLoader.accept(YukkuriType.NITORI);
				body = new Nitori(x, y, z, age, mama, papa);
				break;
			case Ran.type:
				imageLoader.accept(YukkuriType.RAN);
				body = new Ran(x, y, z, age, mama, papa);
				break;
			case Suwako.type:
				imageLoader.accept(YukkuriType.SUWAKO);
				body = new Suwako(x, y, z, age, mama, papa);
				break;
			case Tenko.type:
				imageLoader.accept(YukkuriType.TENKO);
				body = new Tenko(x, y, z, age, mama, papa);
				break;
			case Udonge.type:
				imageLoader.accept(YukkuriType.UDONGE);
				body = new Udonge(x, y, z, age, mama, papa);
				break;
			case Yurusanae.type:
				imageLoader.accept(YukkuriType.YURUSANAE);
				body = new Yurusanae(x, y, z, age, mama, papa);
				break;
			case Yuyuko.type:
				imageLoader.accept(YukkuriType.YUYUKO);
				body = new Yuyuko(x, y, z, age, mama, papa);
				break;
			case Yuuka.type:
				imageLoader.accept(YukkuriType.YUUKA);
				body = new Yuuka(x, y, z, age, mama, papa);
				break;
			case Sakuya.type:
				imageLoader.accept(YukkuriType.SAKUYA);
				body = new Sakuya(x, y, z, age, mama, papa);
				break;
			default:
				imageLoader.accept(YukkuriType.MARISA);
				body = new Marisa(x, y, z, age, mama, papa);
				break;
		}

		applyBodyPostProcess(body, dna, mama, papa, buildNewFamily, familyLinker);
		return body;
	}

	private static void applyBodyPostProcess(Yukkuri body, Dna dna, Yukkuri mama, Yukkuri papa, boolean buildNewFamily,
			FamilyLinker familyLinker) {
		if (dna != null) {
			if (dna.getAttitude() != null) {
				body.setAttitude(dna.getAttitude());
			}
			if (dna.getIntelligence() != null) {
				body.setIntelligence(dna.getIntelligence());
			}
		}
		applyNagasiMode(body, mama, papa);
		if (buildNewFamily && familyLinker != null) {
			familyLinker.link(mama, papa, body);
		}
	}

	private static void applyNagasiMode(Yukkuri body, Yukkuri mama, Yukkuri papa) {
		if (SimYukkuri.NAGASI_MODE == 2) {
			int parentCount = 0;
			if (mama != null && mama.isImageNagasiMode()) {
				parentCount++;
			}
			if (papa != null && papa.isImageNagasiMode()) {
				parentCount++;
			}
			if (parentCount == 0) {
				if (GameRandom.nextInt(20) == 0) {
					body.setImageNagasiMode(true);
				}
			} else if (parentCount == 1) {
				if (GameRandom.nextBoolean()) {
					body.setImageNagasiMode(true);
				}
			} else {
				if (GameRandom.nextInt(20) != 0) {
					body.setImageNagasiMode(true);
				}
			}
		} else if (mama != null && mama.isImageNagasiMode()) {
			body.setImageNagasiMode(true);
		}
	}
}
