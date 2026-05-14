package org.simyukkuri.enums;

import org.simyukkuri.entity.core.world.bodylinked.Stalk;

/** ゆっくり以外のオブジェクトのタイプ */
public enum WorldEntityKind {
	FOOD(org.simyukkuri.entity.core.world.item.Food.class),
	TOILET(org.simyukkuri.entity.core.world.item.Toilet.class),
	BED(org.simyukkuri.entity.core.world.item.Bed.class),
	TOY(org.simyukkuri.entity.core.world.item.Toy.class),
	STONE(org.simyukkuri.entity.core.world.item.Stone.class),
	BELTCONVEYOR(org.simyukkuri.entity.core.world.item.BeltconveyorObj.class),
	BREEDINGPOOL(org.simyukkuri.entity.core.world.item.BreedingPool.class),
	GARBAGECHUTE(org.simyukkuri.entity.core.world.item.GarbageChute.class),
	MACHINEPRESS(org.simyukkuri.entity.core.world.item.MachinePress.class),
	FOODMAKER(org.simyukkuri.entity.core.world.item.FoodMaker.class),
	ORANGEPOOL(org.simyukkuri.entity.core.world.item.OrangePool.class),
	PRODUCTCHUTE(org.simyukkuri.entity.core.world.item.ProductChute.class),
	STALK(Stalk.class),
	DIFFUSER(org.simyukkuri.entity.core.world.item.Diffuser.class),
	YUNBA(org.simyukkuri.entity.core.world.item.Yunba.class),
	STICKYPLATE(org.simyukkuri.entity.core.world.item.StickyPlate.class),
	HOTPLATE(org.simyukkuri.entity.core.world.item.HotPlate.class),
	PROCESSERPLATE(org.simyukkuri.entity.core.world.item.ProcesserPlate.class),
	MIXER(org.simyukkuri.entity.core.world.item.Mixer.class),
	AUTOFEEDER(org.simyukkuri.entity.core.world.item.AutoFeeder.class),
	SUI(org.simyukkuri.entity.core.world.item.Sui.class),
	TRASH(org.simyukkuri.entity.core.world.item.Trash.class),
	GARBAGESTATION(org.simyukkuri.entity.core.world.item.GarbageStation.class),
	HOUSE(org.simyukkuri.entity.core.world.item.House.class),
	GENERATOR(org.simyukkuri.entity.core.world.item.Generator.class),
	;

	private final Class<?> classPack;

	WorldEntityKind(Class<?> cls) {
		this.classPack = cls;
	}

	public Class<?> getClassPack() {
		return classPack;
	}
}
