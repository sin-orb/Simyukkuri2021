package src.draw;

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
