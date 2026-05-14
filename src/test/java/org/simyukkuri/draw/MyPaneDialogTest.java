package org.simyukkuri.draw;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.*;
import org.simyukkuri.entity.core.attachment.impl.*;
import org.simyukkuri.entity.core.effect.*;
import org.simyukkuri.entity.core.effect.impl.*;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.*;
import org.simyukkuri.entity.core.world.bodylinked.*;
import org.simyukkuri.entity.core.world.item.*;
import org.simyukkuri.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.JOptionPane;

import org.junit.jupiter.api.Test;

class MyPaneDialogTest {
	@Test
	void shouldProceedAfterAddDialog_onlyAcceptsOk() {
		assertTrue(MyPane.shouldProceedAfterAddDialog(JOptionPane.OK_OPTION));
		assertFalse(MyPane.shouldProceedAfterAddDialog(JOptionPane.CANCEL_OPTION));
		assertFalse(MyPane.shouldProceedAfterAddDialog(JOptionPane.CLOSED_OPTION));
	}
}
