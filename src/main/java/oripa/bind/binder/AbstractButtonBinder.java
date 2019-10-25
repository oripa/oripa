package oripa.bind.binder;

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
	protected AbstractButton createEmptyButton(
			final Class<? extends AbstractButton> buttonClass, final String textID) {

		ResourceHolder resourceHolder = ResourceHolder.getInstance();

		/*
		 * construct button
		 */
		AbstractButton button = null;
		try {
			button = buttonClass.getConstructor().newInstance();
		} catch (Exception e) {
			logger.error("failed to create a button", e);
		}

		/*
		 * set label
		 */
		try {
			button.setText(resourceHolder.getString(ResourceKey.LABEL, textID));
		} catch (Exception e) {
			logger.error("failed to set a label to button", e);
		}

		return button;
	}

}