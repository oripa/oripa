package oripa.bind;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JPanel;

import static org.junit.Assert.*;

import org.junit.Test;

import oripa.ORIPA;
import oripa.bind.ButtonFactory;
import oripa.bind.PaintActionButtonFactory;
import oripa.doc.Doc;
import oripa.resource.StringID;

public class ButtonFactoryTest {

	@Test
	public void test() {

		JPanel parent = new JPanel();
		
		ORIPA.doc = new Doc();

		// line input buttons
		assertButtonCreated(parent, StringID.DIRECT_V_ID, false);
		assertButtonCreated(parent, StringID.ON_V_ID, false); 
		assertButtonCreated(parent, StringID.SYMMETRIC_ID, false); 
		assertButtonCreated(parent, StringID.TRIANGLE_ID, false); 
		assertButtonCreated(parent, StringID.BISECTOR_ID, false); 
		assertButtonCreated(parent, StringID.VERTICAL_ID, false); 
		assertButtonCreated(parent, StringID.MIRROR_ID, false); 
		assertButtonCreated(parent, StringID.BY_VALUE_ID, false); 
//		assertButtonCreated(parent, StringID.PICK_LENGTH_ID, false); 
//		assertButtonCreated(parent, StringID.PICK_ANGLE_ID, false); 
		assertButtonCreated(parent, StringID.PERPENDICULAR_BISECTOR_ID, false);
		
		// edit buttons
		assertButtonCreated(parent, StringID.SELECT_ID, true);
		assertButtonCreated(parent, StringID.ADD_VERTEX_ID, true); 
		assertButtonCreated(parent, StringID.CHANGE_LINE_TYPE_ID, true); 
		assertButtonCreated(parent, StringID.DELETE_LINE_ID, true); 
		assertButtonCreated(parent, StringID.DELETE_VERTEX_ID, true); 
		assertButtonCreated(parent, StringID.COPY_PASTE_ID, true);
		assertButtonCreated(parent, StringID.CUT_PASTE_ID, true);
		assertButtonCreated(parent, StringID.EDIT_CONTOUR_ID, true); 
		assertButtonCreated(parent, StringID.SELECT_ALL_LINE_ID, true);
	}

	private void assertButtonCreated(Component parent, String id, boolean hasLabel){
		ButtonFactory paintFactory = new PaintActionButtonFactory();

		JButton button;
		button = (JButton)paintFactory.create(parent, JButton.class, id);

		String text = button.getText();
		System.out.println(id + " text:" + text);
		
		if(hasLabel){
			assertNotNull(text);
			assertTrue(text.length() > 0);
		}

		//button.doClick();
		// test hint text

		// test paint action

	}
}
