package oripa.bind;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.awt.Component;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

import oripa.domain.paint.MouseActionHolder;
import oripa.domain.paint.PaintContextFactory;
import oripa.resource.StringID;

public class ButtonFactoryTest {
	@Test
	public void test() {

		JPanel parent = new JPanel();

		var actionHolder = mock(MouseActionHolder.class);

		// line input buttons
		assertButtonCreated(parent, StringID.DIRECT_V_ID, actionHolder, false);
		assertButtonCreated(parent, StringID.ON_V_ID, actionHolder, false);
		assertButtonCreated(parent, StringID.SYMMETRIC_ID, actionHolder, false);
		assertButtonCreated(parent, StringID.TRIANGLE_ID, actionHolder, false);
		assertButtonCreated(parent, StringID.BISECTOR_ID, actionHolder, false);
		assertButtonCreated(parent, StringID.VERTICAL_ID, actionHolder, false);
		assertButtonCreated(parent, StringID.MIRROR_ID, actionHolder, false);
		assertButtonCreated(parent, StringID.BY_VALUE_ID, actionHolder, false);
//		assertButtonCreated(parent, StringID.PICK_LENGTH_ID, false);
//		assertButtonCreated(parent, StringID.PICK_ANGLE_ID, false);
		assertButtonCreated(parent, StringID.PERPENDICULAR_BISECTOR_ID, actionHolder, false);

		// edit buttons
		assertButtonCreated(parent, StringID.SELECT_ID, actionHolder, true);
		assertButtonCreated(parent, StringID.ADD_VERTEX_ID, actionHolder, true);
		assertButtonCreated(parent, StringID.CHANGE_LINE_TYPE_ID, actionHolder, true);
		assertButtonCreated(parent, StringID.DELETE_LINE_ID, actionHolder, true);
		assertButtonCreated(parent, StringID.DELETE_VERTEX_ID, actionHolder, true);
		assertButtonCreated(parent, StringID.COPY_PASTE_ID, actionHolder, true);
		assertButtonCreated(parent, StringID.CUT_PASTE_ID, actionHolder, true);
		assertButtonCreated(parent, StringID.EDIT_CONTOUR_ID, actionHolder, true);
		assertButtonCreated(parent, StringID.SELECT_ALL_LINE_ID, actionHolder, true);
	}

	private void assertButtonCreated(final Component parent, final String id,
			final MouseActionHolder actionHolder, final boolean hasLabel) {
		PaintContextFactory contextFactory = new PaintContextFactory();
		ButtonFactory paintFactory = new PaintActionButtonFactory(contextFactory.createContext());

		var keyListener = mock(KeyListener.class);
		JButton button;
		button = (JButton) paintFactory.create(parent, JButton.class, actionHolder, id,
				keyListener);

		String text = button.getText();
		System.out.println(id + " text:" + text);

		if (hasLabel) {
			assertNotNull(text);
			assertTrue(text.length() > 0);
		}

		assertEquals(keyListener, button.getKeyListeners()[0]);
		// button.doClick();
		// test hint text

		// test paint action

	}
}
