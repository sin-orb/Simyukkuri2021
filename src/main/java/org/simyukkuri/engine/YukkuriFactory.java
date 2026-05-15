package org.simyukkuri.engine;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Alice;
import org.simyukkuri.entity.core.living.yukkuri.impl.Ayaya;
import org.simyukkuri.entity.core.living.yukkuri.impl.Chen;
import org.simyukkuri.entity.core.living.yukkuri.impl.Chiruno;
import org.simyukkuri.entity.core.living.yukkuri.impl.Deibu;
import org.simyukkuri.entity.core.living.yukkuri.impl.DosMarisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Eiki;
import org.simyukkuri.entity.core.living.yukkuri.impl.Fran;
import org.simyukkuri.entity.core.living.yukkuri.impl.HybridYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Kimeemaru;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.MarisaKotatsumuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.MarisaReimu;
import org.simyukkuri.entity.core.living.yukkuri.impl.MarisaTsumuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Meirin;
import org.simyukkuri.entity.core.living.yukkuri.impl.Myon;
import org.simyukkuri.entity.core.living.yukkuri.impl.Nitori;
import org.simyukkuri.entity.core.living.yukkuri.impl.Patch;
import org.simyukkuri.entity.core.living.yukkuri.impl.Ran;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.entity.core.living.yukkuri.impl.ReimuMarisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Remirya;
import org.simyukkuri.entity.core.living.yukkuri.impl.Sakuya;
import org.simyukkuri.entity.core.living.yukkuri.impl.Suwako;
import org.simyukkuri.entity.core.living.yukkuri.impl.Tarinai;
import org.simyukkuri.entity.core.living.yukkuri.impl.TarinaiReimu;
import org.simyukkuri.entity.core.living.yukkuri.impl.Tenko;
import org.simyukkuri.entity.core.living.yukkuri.impl.Udonge;
import org.simyukkuri.entity.core.living.yukkuri.impl.WasaReimu;
import org.simyukkuri.entity.core.living.yukkuri.impl.Yurusanae;
import org.simyukkuri.entity.core.living.yukkuri.impl.Yuuka;
import org.simyukkuri.entity.core.living.yukkuri.impl.Yuyuko;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.util.GameRandom;

/**
 * ゆっくり生成の分岐をまとめた factory.
 */
public final class YukkuriFactory {
	private YukkuriFactory() {
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
	 * @param x              発生場所X座標
	 * @param y              発生場所Y座標
	 * @param z              発生場所Z座標
	 * @param type           ゆっくり種別
	 * @param dna            赤ゆのDNA
	 * @param age            追加時年齢
	 * @param p1             母親
	 * @param p2             父親
	 * @param buildNewFamily 家族を新規作成するか
	 * @param imageLoader    画像読み込み要求を受ける callback
	 * @param dosMaker       DOS個体を生成するかどうかの callback
	 * @return 生成された Yukkuri
	 */
	public static Yukkuri create(int x, int y, int z, YukkuriType type, Dna dna, AgeState age, Yukkuri p1, Yukkuri p2,
			boolean buildNewFamily, Consumer<YukkuriType> imageLoader, BooleanSupplier dosMaker) {
		return create(x, y, z, type, dna, age, p1, p2, buildNewFamily, imageLoader, dosMaker, null);
	}

	/**
	 * Yukkuri を生成する.
	 *
	 * @param x              発生場所X座標
	 * @param y              発生場所Y座標
	 * @param z              発生場所Z座標
	 * @param type           ゆっくり種別
	 * @param dna            赤ゆのDNA
	 * @param age            追加時年齢
	 * @param p1             母親
	 * @param p2             父親
	 * @param buildNewFamily 家族を新規作成するか
	 * @param imageLoader    画像読み込み要求を受ける callback
	 * @param dosMaker       DOS個体を生成するかどうかの callback
	 * @param familyLinker   家族関係の接続 callback
	 * @return 生成された Yukkuri
	 */
	public static Yukkuri create(int x, int y, int z, YukkuriType type, Dna dna, AgeState age, Yukkuri p1, Yukkuri p2,
			boolean buildNewFamily, Consumer<YukkuriType> imageLoader, BooleanSupplier dosMaker,
			FamilyLinker familyLinker) {
		Yukkuri papa = p2;
		Yukkuri mama = p1;
		if (papa == null && dna != null) {
			papa = org.simyukkuri.util.YukkuriLookup.getYukkuriById(dna.getFather());
		}
		Yukkuri body;
		switch (type) {
			case MARISA:
				imageLoader.accept(YukkuriType.MARISA);
				body = new Marisa(x, y, z, age, mama, papa);
				break;
			case REIMU:
				imageLoader.accept(YukkuriType.REIMU);
				body = new Reimu(x, y, z, age, mama, papa);
				break;
			case ALICE:
				imageLoader.accept(YukkuriType.ALICE);
				body = new Alice(x, y, z, age, mama, papa);
				break;
			case PATCH:
				imageLoader.accept(YukkuriType.PATCH);
				body = new Patch(x, y, z, age, mama, papa);
				break;
			case CHEN:
				imageLoader.accept(YukkuriType.CHEN);
				body = new Chen(x, y, z, age, mama, papa);
				break;
			case MYON:
				imageLoader.accept(YukkuriType.MYON);
				body = new Myon(x, y, z, age, mama, papa);
				break;
			case WASAREIMU:
				imageLoader.accept(YukkuriType.REIMU);
				imageLoader.accept(YukkuriType.WASAREIMU);
				body = new WasaReimu(x, y, z, age, mama, papa);
				break;
			case MARISATSUMURI:
				imageLoader.accept(YukkuriType.MARISA);
				imageLoader.accept(YukkuriType.MARISATSUMURI);
				body = new MarisaTsumuri(x, y, z, age, mama, papa);
				break;
			case MARISAKOTATSUMURI:
				imageLoader.accept(YukkuriType.MARISAKOTATSUMURI);
				body = new MarisaKotatsumuri(x, y, z, age, mama, papa);
				break;
			case DEIBU:
				imageLoader.accept(YukkuriType.REIMU);
				imageLoader.accept(YukkuriType.DEIBU);
				body = new Deibu(x, y, z, age, mama, papa);
				break;
			case DOSMARISA:
				if (dosMaker.getAsBoolean()) {
					imageLoader.accept(YukkuriType.DOSMARISA);
					body = new DosMarisa(x, y, z, age, mama, papa);
					break;
				}
				imageLoader.accept(YukkuriType.MARISA);
				body = new Marisa(x, y, z, age, mama, papa);
				break;
			case TARINAI:
				imageLoader.accept(YukkuriType.TARINAI);
				body = new Tarinai(x, y, z, age, mama, papa);
				break;
			case TARINAIREIMU:
				imageLoader.accept(YukkuriType.TARINAI);
				imageLoader.accept(YukkuriType.TARINAIREIMU);
				body = new TarinaiReimu(x, y, z, age, mama, papa);
				break;
			case MARISAREIMU:
				imageLoader.accept(YukkuriType.REIMU);
				imageLoader.accept(YukkuriType.MARISAREIMU);
				body = new MarisaReimu(x, y, z, age, mama, papa);
				break;
			case REIMUMARISA:
				imageLoader.accept(YukkuriType.MARISA);
				imageLoader.accept(YukkuriType.REIMUMARISA);
				body = new ReimuMarisa(x, y, z, age, mama, papa);
				break;
			case HYBRIDYUKKURI:
				imageLoader.accept(YukkuriType.HYBRIDYUKKURI);
				body = new HybridYukkuri(x, y, z, age, mama, papa);
				break;
			case REMIRYA:
				imageLoader.accept(YukkuriType.REMIRYA);
				body = new Remirya(x, y, z, age, mama, papa);
				break;
			case FRAN:
				imageLoader.accept(YukkuriType.FRAN);
				body = new Fran(x, y, z, age, mama, papa);
				break;
			case AYAYA:
				imageLoader.accept(YukkuriType.AYAYA);
				body = new Ayaya(x, y, z, age, mama, papa);
				break;
			case CHIRUNO:
				imageLoader.accept(YukkuriType.CHIRUNO);
				body = new Chiruno(x, y, z, age, mama, papa);
				break;
			case EIKI:
				imageLoader.accept(YukkuriType.EIKI);
				body = new Eiki(x, y, z, age, mama, papa);
				break;
			case KIMEEMARU:
				imageLoader.accept(YukkuriType.KIMEEMARU);
				body = new Kimeemaru(x, y, z, age, mama, papa);
				break;
			case MEIRIN:
				imageLoader.accept(YukkuriType.MEIRIN);
				body = new Meirin(x, y, z, age, mama, papa);
				break;
			case NITORI:
				imageLoader.accept(YukkuriType.NITORI);
				body = new Nitori(x, y, z, age, mama, papa);
				break;
			case RAN:
				imageLoader.accept(YukkuriType.RAN);
				body = new Ran(x, y, z, age, mama, papa);
				break;
			case SUWAKO:
				imageLoader.accept(YukkuriType.SUWAKO);
				body = new Suwako(x, y, z, age, mama, papa);
				break;
			case TENKO:
				imageLoader.accept(YukkuriType.TENKO);
				body = new Tenko(x, y, z, age, mama, papa);
				break;
			case UDONGE:
				imageLoader.accept(YukkuriType.UDONGE);
				body = new Udonge(x, y, z, age, mama, papa);
				break;
			case YURUSANAE:
				imageLoader.accept(YukkuriType.YURUSANAE);
				body = new Yurusanae(x, y, z, age, mama, papa);
				break;
			case YUYUKO:
				imageLoader.accept(YukkuriType.YUYUKO);
				body = new Yuyuko(x, y, z, age, mama, papa);
				break;
			case YUUKA:
				imageLoader.accept(YukkuriType.YUUKA);
				body = new Yuuka(x, y, z, age, mama, papa);
				break;
			case SAKUYA:
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
