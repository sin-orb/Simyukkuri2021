package src.enums;

import src.entity.core.world.bodylinked.Stalk;

/** ゆっくり以外のオブジェクトのタイプ */
public enum WorldEntityKind {
	FOOD(src.entity.core.world.item.Food.class),
	TOILET(src.entity.core.world.item.Toilet.class),
	BED(src.entity.core.world.item.Bed.class),
	TOY(src.entity.core.world.item.Toy.class),
	STONE(src.entity.core.world.item.Stone.class),
	BELTCONVEYOR(src.entity.core.world.item.BeltconveyorObj.class),
	BREEDINGPOOL(src.entity.core.world.item.BreedingPool.class),
	GARBAGECHUTE(src.entity.core.world.item.GarbageChute.class),
	MACHINEPRESS(src.entity.core.world.item.MachinePress.class),
	FOODMAKER(src.entity.core.world.item.FoodMaker.class),
	ORANGEPOOL(src.entity.core.world.item.OrangePool.class),
	PRODUCTCHUTE(src.entity.core.world.item.ProductChute.class),
	STALK(Stalk.class),
	DIFFUSER(src.entity.core.world.item.Diffuser.class),
	YUNBA(src.entity.core.world.item.Yunba.class),
	STICKYPLATE(src.entity.core.world.item.StickyPlate.class),
	HOTPLATE(src.entity.core.world.item.HotPlate.class),
	PROCESSERPLATE(src.entity.core.world.item.ProcesserPlate.class),
	MIXER(src.entity.core.world.item.Mixer.class),
	AUTOFEEDER(src.entity.core.world.item.AutoFeeder.class),
	SUI(src.entity.core.world.item.Sui.class),
	TRASH(src.entity.core.world.item.Trash.class),
	GARBAGESTATION(src.entity.core.world.item.GarbageStation.class),
	HOUSE(src.entity.core.world.item.House.class),
	GENERATOR(src.entity.core.world.item.Generator.class),
	;

	private final Class<?> classPack;

	WorldEntityKind(Class<?> cls) {
		this.classPack = cls;
	}

	public Class<?> getClassPack() {
		return classPack;
	}
}
