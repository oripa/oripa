package oripa.gui.presenter.creasepattern.byvalue;

import oripa.domain.paint.byvalue.ValueSetting;

/**
 * @see AbstractValueInputListener
 * @author koji
 *
 */
public class LengthValueInputListener extends AbstractValueInputListener {

	/**
	 * Constructor
	 */
	public LengthValueInputListener(final ValueSetting valueSetting) {
		super(valueSetting);
	}

	@Override
	protected void setValue(final double value, final ValueSetting valueSetting) {
		valueSetting.setLength(value);

	}

}
