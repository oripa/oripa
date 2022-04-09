package oripa.gui.bind.binder;

import java.util.MissingResourceException;

import javax.swing.AbstractButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.resource.ResourceHolder;
import oripa.resource.ResourceKey;

public abstract class AbstractButtonBinder<ToBeBound>
		implements BinderInterface<ToBeBound> {

	private static Logger logger = LoggerFactory.getLogger(AbstractButtonBinder.class);

	/**
	 *
	 * @param buttonClass
	 * @param textID
	 *            StringID member for label
	 * @return
	 */
	protected <TButton extends AbstractButton> TButton createEmptyButton(
			final Class<TButton> buttonClass, final String textID) {

		ResourceHolder resourceHolder = ResourceHolder.getInstance();

		/*
		 * construct button
		 */
		TButton button = null;
		try {
			button = buttonClass.getConstructor().newInstance();
		} catch (Exception e) {
			logger.error("failed to create a button", e);
			throw new RuntimeException("maybe arguments are wrong.", e);
		}

		/*
		 * set label
		 */
		try {
			button.setText(resourceHolder.getString(ResourceKey.LABEL, textID));
		} catch (MissingResourceException e) {
			logger.warn("the label for the button is not found");
		}

		return button;
	}

}