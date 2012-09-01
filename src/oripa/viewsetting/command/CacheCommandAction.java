package oripa.viewsetting.command;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;

public class CacheCommandAction implements ActionListener{
	CommandHistory history = CommandHistory.getInstance();

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e == null){
			return;
		}
		AbstractButton source = (AbstractButton)e.getSource();
		history.setCache(source.getActionListeners());			
		
		
	}
}