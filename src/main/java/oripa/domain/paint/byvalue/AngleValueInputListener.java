package oripa.domain.paint.byvalue;

/**
 * @see AbstractValueInputListener
 * @author koji
 *
 */
public class AngleValueInputListener extends AbstractValueInputListener {

	/**
	 * Constructor
	 */
	public AngleValueInputListener(final ValueSetting valueSetting) {
		super(valueSetting);
	}

	@Override
	protected void setValue(final double value, final ValueSetting valueSetting) {
		valueSetting.setAngle(value);

	}

}
