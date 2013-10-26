package oripa.util.history;

import java.util.Deque;
import java.util.LinkedList;

public class UndoManager<Backup> {

	private Deque<UndoInfo<Backup>> undoStack = new LinkedList<>();
	private UndoInfo<Backup> cache;
	
	private boolean changed = false;

	private int max = 1000;

	private UndoInfoFactory<Backup> factory;

	public UndoManager(UndoInfoFactory<Backup> factory) {
		this.factory = factory;
	}
	
	public UndoManager(UndoInfoFactory<Backup> factory, int max){
		this.factory = factory;
		this.max = max;
	}

	public void push(Backup info){
		
		push(factory.create(info));
		
	}

	public void push(UndoInfo<Backup> info){
		
		undoStack.push(info);
		
		if(undoStack.size() > max){
			undoStack.removeFirst();
		}
		
		changed = true;
	}

	public UndoInfo<Backup> pop() {
		if (undoStack.isEmpty()) {
			return null;
		}
		else {
			changed = true;
		}

		return undoStack.pop();
	}

	public UndoInfo<Backup> peek(){
		if (undoStack.isEmpty()) {
			return null;
		}

		return undoStack.peek();
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
		cache = factory.create(info);
	}
	
	public UndoInfo<Backup> getCache(){
		return cache;
	}
	
	public void pushCachedInfo(){
		this.push(cache);
	}
}
