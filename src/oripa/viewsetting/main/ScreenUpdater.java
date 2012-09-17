package oripa.viewsetting.main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import oripa.paint.Globals;
import oripa.paint.GraphicMouseAction;
import oripa.paint.copypaste.CopyAndPasteAction;

public class ScreenUpdater implements KeyListener {

	public void updateScreen(){
		MainScreenSettingDB screenDB = MainScreenSettingDB.getInstance();
		screenDB.requestRedraw();						

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.isControlDown()){
			GraphicMouseAction action = Globals.getMouseAction();

			if(action instanceof CopyAndPasteAction){
				CopyAndPasteAction casted = (CopyAndPasteAction) action;
				casted.changeAction(true);

				updateScreen();
			}

		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		GraphicMouseAction action = Globals.getMouseAction();

		if(action instanceof CopyAndPasteAction){
			CopyAndPasteAction casted = (CopyAndPasteAction) action;
			casted.changeAction(false);
			updateScreen();
		}


	}

}
