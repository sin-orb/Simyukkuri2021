package org.simyukkuri.entity.core.living.yukkuri;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.Parent;
import org.simyukkuri.logic.YukkuriRelations;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;

/**
 * ゆっくりの家族関係と出生キューをまとめる委譲クラス。
 */
public final class YukkuriFamilyDelegate {
	private final Yukkuri body;

	/**
	 * 家族関係の委譲を生成する.
	 *
	 * @param body 対象のゆっくり
	 */
	public YukkuriFamilyDelegate(Yukkuri body) {
		this.body = body;
	}

	/**
	 * 母が子供の針をぐーりぐーりする.
	 *
	 * @param p 子供
	 */
	public void doGuriguri(Yukkuri p) {
		if (body.isDead() || p.isDead()) {
			return;
		}

		if (!body.canAction()) {
			return;
		}

		if (p.isAdult()) {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ExtractingNeedlePartner));
		} else {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ExtractingNeedleChild));
		}

		if (p.isNotNYD()) {
			p.setMessage(GameMessages.getMessage(p, MessagePool.Action.NeedlePain), 60, true, false);
			p.stayPurupuru(40);
			p.setHappiness(Happiness.VERY_SAD);
			p.setForceFace(ImageCode.PAIN.ordinal());
		}
		p.addStress(80);
		body.stay(40);
		body.setHappiness(Happiness.VERY_SAD);
		body.addStress(30);
		body.setForceFace(ImageCode.CRYING.ordinal());
	}

	/**
	 * 胎生妊娠している赤ゆのDNAを1件取り出す.
	 *
	 * @return 先頭の赤ゆDNA。なければ `null`
	 */
	public Dna getBabyTypesDequeue() {
		Dna babyType = null;
		if (body.getBabyTypes().size() > 0) {
			babyType = body.getBabyTypes().get(0);
			body.getBabyTypes().remove(0);
		}
		return babyType;
	}

	/**
	 * Removeされたゆっくりが姉妹リスト、子リストにいたら削除する.
	 */
	public void pruneRemovedFamilyMembers() {
		Yukkuri[] sisters = body.getYukkuriArray(body.getSisters());
		body.getSisters().clear();
		Set<Integer> set = new TreeSet<>();
		for (Yukkuri sister : sisters) {
			if (sister == null) {
				continue;
			}
			if (!sister.isRemoved()) {
				set.add(sister.getUniqueID());
			}
		}
		body.setSisters(new LinkedList<Integer>(set));
		Collections.sort(body.getSisters());

		Yukkuri[] elderSisters = body.getYukkuriArray(body.getElderSisters());
		body.getElderSisters().clear();
		set.clear();
		for (Yukkuri elderSister : elderSisters) {
			if (elderSister == null) {
				continue;
			}
			if (!elderSister.isRemoved()) {
				set.add(elderSister.getUniqueID());
			}
		}
		body.setElderSisters(new LinkedList<Integer>(set));
		Collections.sort(body.getElderSisters());

		Yukkuri[] children = body.getYukkuriArray(body.getChildren());
		body.getChildren().clear();
		set.clear();
		for (Yukkuri child : children) {
			if (child == null) {
				continue;
			}
			if (!child.isRemoved()) {
				set.add(child.getUniqueID());
			}
		}
		body.setChildren(new LinkedList<Integer>(set));
		Collections.sort(body.getChildren());
	}

	/**
	 * 親子関係をなくす.
	 */
	public void clearRelation() {
		if (YukkuriRelations.getParentYukkuri(body.getParents()[Parent.PAPA.ordinal()]) != null)
			if (YukkuriRelations.getParentYukkuri(body.getParents()[Parent.PAPA.ordinal()]).isRemoved())
				body.getParents()[Parent.PAPA.ordinal()] = -1;
		if (YukkuriRelations.getParentYukkuri(body.getParents()[Parent.MAMA.ordinal()]) != null)
			if (YukkuriRelations.getParentYukkuri(body.getParents()[Parent.MAMA.ordinal()]).isRemoved())
				body.getParents()[Parent.MAMA.ordinal()] = -1;
		if (body.getPartner() != -1) {
			Yukkuri partnerCandidate = YukkuriRelations.getPartnerYukkuri(body);
			if (partnerCandidate == null || partnerCandidate.isRemoved())
				body.setPartner(-1);
		}
	}
}
