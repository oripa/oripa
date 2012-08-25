package oripa;

import java.util.Stack;

public class UndoManager<Backup> {

	private Stack<Backup> undoStack = new Stack<Backup>();
	private Backup cache;
	
	private boolean changed = false;
	

	public void push(Backup uinfo){
		undoStack.push(uinfo);
		changed = true;
	}

	public Backup pop() {
		if (undoStack.isEmpty()) {
			return null;
		}
		else {
			changed = true;
		}

		return undoStack.pop();
	}

	public boolean isChanged(){
		return changed;
	}
	
	public void clearChanged(){
		changed = false;
	}
	
	public boolean canUndo(){
		return ! undoStack.isEmpty();
	}
	
	public void setCache(Backup info){
		cache = info;
	}
	
	public Backup getCache(){
		return cache;
	}
	
	public void pushCachedInfo(){
		this.push(cache);
	}
}
