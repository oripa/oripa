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
	public AngleValueInputListener(final ValueDB valueSetting) {
		super(valueSetting);
	}

	@Override
	protected void setValue(final double value, final ValueDB valueSetting) {
		valueSetting.setAngle(value);

	}

}
