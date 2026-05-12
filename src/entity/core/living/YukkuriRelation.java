package src.entity.core.living;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * ゆっくりの家族・社会的関係データをまとめた値オブジェクト。
 * BodyAttributes から分配された relation グループ。
 */
public class YukkuriRelation implements Serializable {
	private static final long serialVersionUID = 1584032971006428309L;

	private int partner = -1;
	private int[] parents = { -1, -1 };
	private List<Integer> childrenList = new LinkedList<Integer>();
	private List<Integer> elderSisterList = new LinkedList<Integer>();
	private List<Integer> sisterList = new LinkedList<Integer>();
	private List<Integer> ancestorList = new LinkedList<Integer>();
	private boolean fatherRaper = false;
	private int parentLinkId = -1;

	public int getPartner() {
		return partner;
	}

	public void setPartner(int partner) {
		this.partner = partner;
	}

	public int[] getParents() {
		return parents;
	}

	public void setParents(int[] parents) {
		this.parents = parents;
	}

	public List<Integer> getChildrenList() {
		return childrenList;
	}

	public void setChildrenList(List<Integer> childrenList) {
		this.childrenList = childrenList;
	}

	public List<Integer> getElderSisterList() {
		return elderSisterList;
	}

	public void setElderSisterList(List<Integer> elderSisterList) {
		this.elderSisterList = elderSisterList;
	}

	public List<Integer> getSisterList() {
		return sisterList;
	}

	public void setSisterList(List<Integer> sisterList) {
		this.sisterList = sisterList;
	}

	public List<Integer> getAncestorList() {
		return ancestorList;
	}

	public void setAncestorList(List<Integer> ancestorList) {
		this.ancestorList = ancestorList;
	}

	public boolean isFatherRaper() {
		return fatherRaper;
	}

	public void setFatherRaper(boolean fatherRaper) {
		this.fatherRaper = fatherRaper;
	}

	public int getParentLinkId() {
		return parentLinkId;
	}

	public void setParentLinkId(int parentLinkId) {
		this.parentLinkId = parentLinkId;
	}
}
