package src.util;

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
