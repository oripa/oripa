package oripa.bind.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.swing.JRadioButton;

import org.junit.jupiter.api.Test;

import oripa.bind.binder.BinderInterface;
import oripa.bind.binder.ViewChangeBinder;
import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;
import oripa.resource.StringID;
import oripa.viewsetting.ChangeViewSetting;
import oripa.viewsetting.main.uipanel.ChangeOnPaintInputButtonSelected;
import oripa.viewsetting.main.uipanel.UIPanelSettingDB;

public class ButtonBinderTest {

	@Test
	public void testCreate() {

		BinderInterface<ChangeViewSetting> viewChangeBinder = new ViewChangeBinder();

		// JRadioButton editModeInputLineButton = new JRadioButton("InputLine",
		// true);
		JRadioButton editModeInputLineButton = (JRadioButton) viewChangeBinder.createButton(
				JRadioButton.class, new ChangeOnPaintInputButtonSelected(new UIPanelSettingDB()),
				StringID.UI.INPUT_LINE_ID, null);

		ResourceHolder resources = ResourceHolder.getInstance();

		assertNotNull(editModeInputLineButton);

		String actualText = editModeInputLineButton.getText();
		assertNotNull(actualText);
		System.out.println(actualText);
		assertEquals(resources.getString(ResourceKey.LABEL, StringID.UI.INPUT_LINE_ID),
				editModeInputLineButton.getText());
	}

	@Test
	public void testCreateWithError() {
	}
}
