package src.util;

import src.entity.core.Entity;
import src.entity.core.attachment.*;
import src.entity.core.attachment.impl.*;
import src.entity.core.effect.*;
import src.entity.core.effect.impl.*;
import src.entity.core.living.yukkuri.Dna;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.living.yukkuri.impl.*;
import src.entity.core.world.bodylinked.*;
import src.entity.core.world.item.*;
import src.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class ListUtilTest {
	@Test
	public void testRemoveContent() {
		List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 2, 4));

		ListUtil.removeContent(list, 2);

		assertEquals(Arrays.asList(1, 3, 2, 4), list);
	}
}
