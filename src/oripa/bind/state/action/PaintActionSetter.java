package oripa.bind.state.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oripa.ORIPA;
import oripa.paint.Globals;
import oripa.paint.GraphicMouseAction;
import oripa.paint.PaintContext;
import oripa.viewsetting.main.ScreenUpdater;


/**
 * Add this listener to Button object or something for selecting paint action.
 * 
 * @author koji
 *
 */
public class PaintActionSetter implements ActionListener{
	
	private GraphicMouseAction mouseAction;
	
	public PaintActionSetter(GraphicMouseAction mouseAction) {
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
				
		ScreenUpdater screenUpdater = ScreenUpdater.getInstance();
		screenUpdater.updateScreen();
	}
	

}	

