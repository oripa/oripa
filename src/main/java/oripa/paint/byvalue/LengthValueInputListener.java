package oripa.paint.byvalue;


/**
 * @see AbstractValueInputListener
 * @author koji
 *
 */
public class LengthValueInputListener extends AbstractValueInputListener {

	@Override
	protected void setValue(double value) {
		ValueDB valueDB = ValueDB.getInstance();
		valueDB.setLength(value);
		
	}
	
}
