package oripa.viewsetting.main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import oripa.paint.Globals;
import oripa.paint.GraphicMouseAction;
import oripa.paint.copypaste.CopyAndPasteAction;
import oripa.viewsetting.ViewSettingDataBase;

public class ScreenUpdater extends ViewSettingDataBase {

	public static final String REDRAW_REQUESTED = "redraw requested";



	//-------------------------
	// singleton
	//-------------------------
	private static ScreenUpdater instance = null;

	private ScreenUpdater() {
	}


	public static ScreenUpdater getInstance(){
		if(instance == null){
			instance = new ScreenUpdater();
		}

		return instance;
	}
	//-------------------------


	public void updateScreen(){
		setChanged();
		notifyObservers(REDRAW_REQUESTED);

	}

	
	private void updateIfCopyAndPaste(boolean changeToOrigin){
		GraphicMouseAction action = Globals.getMouseAction();

		if(action instanceof CopyAndPasteAction){
			CopyAndPasteAction casted = (CopyAndPasteAction) action;
			casted.changeAction(changeToOrigin);

			updateScreen();
		}
		
	}

	public class KeyListener implements java.awt.event.KeyListener{
		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
			
			if(e.isControlDown()){
				updateIfCopyAndPaste(true);
			}
			else if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
				updateScreen();
				
			}

		}

		@Override
		public void keyReleased(KeyEvent e) {
			updateIfCopyAndPaste(false);
		}

	}
}
