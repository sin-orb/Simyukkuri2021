package src.enums;

import src.game.Stalk;
/** ゆっくり以外のオブジェクトのタイプ */
public enum ObjEXType {
	FOOD(src.item.Food.class),
	TOILET(src.item.Toilet.class),
	BED(src.item.Bed.class),
	TOY(src.item.Toy.class),
	STONE(src.item.Stone.class),
	BELTCONVEYOR(src.item.BeltconveyorObj.class),
	BREEDINGPOOL(src.item.BreedingPool.class),
	GARBAGECHUTE(src.item.GarbageChute.class),
	MACHINEPRESS(src.item.MachinePress.class),
	FOODMAKER(src.item.FoodMaker.class),
	ORANGEPOOL(src.item.OrangePool.class),
	PRODUCTCHUTE(src.item.ProductChute.class),
	STALK(Stalk.class),
	DIFFUSER(src.item.Diffuser.class),
	YUNBA(src.item.Yunba.class),
	STICKYPLATE(src.item.StickyPlate.class),
	HOTPLATE(src.item.HotPlate.class),
	PROCESSERPLATE(src.item.ProcesserPlate.class),
	MIXER(src.item.Mixer.class),
	AUTOFEEDER(src.item.AutoFeeder.class),
	SUI(src.item.Sui.class),
	TRASH(src.item.Trash.class),
	GARBAGESTATION(src.item.GarbageStation.class),
	HOUSE(src.item.House.class),
	GENERATOR(src.item.Generator.class),
	;
	private final Class<?> classPack;
	ObjEXType(Class<?> cls) { this.classPack = cls; }

	public Class<?> getClassPack() {
		return classPack;
	}
}
