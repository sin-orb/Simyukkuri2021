package org.simyukkuri.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ListOperationsTest {
	@Test
	public void testRemoveFirstMatchingValue() {
		List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 2, 4));

		ListOperations.removeFirstMatchingValue(list, 2);

		assertEquals(Arrays.asList(1, 3, 2, 4), list);
	}
}
