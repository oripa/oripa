package oripa.paint;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JRadioButton;

import oripa.ORIPA;


/**
 * Add this listener to Button object or something for selecting paint action.
 * 
 * @author koji
 *
 */
public class CommandSetter implements ActionListener{
	
	private GraphicMouseAction mouseAction;
	
	public CommandSetter(GraphicMouseAction mouseAction) {
		this.mouseAction = mouseAction;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		PaintContext context = PaintContext.getInstance();

		Globals.mouseAction.destroy(context);
		mouseAction.recover(context);
		
		Globals.mouseAction = mouseAction;

		if(mouseAction.needSelect() == false){
			ORIPA.doc.resetSelectedOriLines();
		}
	}
}	

