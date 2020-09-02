package oripa.domain.paint.byvalue;

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
