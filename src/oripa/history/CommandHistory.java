package oripa.history;


public class CommandHistory extends oripa.undo.UndoManager<HistoryInfo>{

	private static CommandHistory instance = null;
	
	private CommandHistory(){}
	
	public static CommandHistory getInstance(){
		if(instance == null){
			instance = new CommandHistory();
		}
		
		return instance;
	}
	
	
	
}