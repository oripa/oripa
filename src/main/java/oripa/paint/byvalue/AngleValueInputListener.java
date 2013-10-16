package oripa.paint.byvalue;

/**
 * @see AbstractValueInputListener
 * @author koji
 *
 */
public class AngleValueInputListener extends AbstractValueInputListener {

	@Override
	protected void setValue(double value) {
		ValueDB valueDB = ValueDB.getInstance();
		valueDB.setAngle(value);
		
	}

}
