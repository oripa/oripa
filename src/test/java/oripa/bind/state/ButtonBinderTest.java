package oripa.bind.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.swing.JRadioButton;

import org.junit.Test;

import oripa.appstate.ApplicationState;
import oripa.bind.binder.ApplicationStateButtonBinder;
import oripa.bind.binder.BinderInterface;
import oripa.bind.binder.ViewChangeBinder;
import oripa.paint.EditMode;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;
import oripa.viewsetting.ChangeViewSetting;
import oripa.viewsetting.uipanel.ChangeOnPaintInputButtonSelected;

public class ButtonBinderTest {

	@Test
	public void testCreate() {

		BinderInterface<ChangeViewSetting> viewChangeBinder = new ViewChangeBinder();
		BinderInterface<ApplicationState<EditMode>> paintBinder = new ApplicationStateButtonBinder();

		//	JRadioButton editModeInputLineButton = new JRadioButton("InputLine", true);
		JRadioButton editModeInputLineButton = (JRadioButton) viewChangeBinder.createButton(
				JRadioButton.class, new ChangeOnPaintInputButtonSelected(), StringID.UI.INPUT_LINE_ID);

		ResourceHolder resources = ResourceHolder.getInstance();
		
		assertNotNull(editModeInputLineButton);
		
		String actualText = editModeInputLineButton.getText();
		assertNotNull(actualText);
		System.out.println(actualText);
		assertEquals( resources.getString(ResourceKey.LABEL, StringID.UI.INPUT_LINE_ID), editModeInputLineButton.getText());
	}

	@Test
	public void testCreateWithError() {
	}
}
