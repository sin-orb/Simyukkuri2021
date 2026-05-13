package src.draw;

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
