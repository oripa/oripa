package oripa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import oripa.geom.OriLine;

public class UndoManager {

	private Stack<UndoInfo> undoStack = new Stack<UndoInfo>();
	private UndoInfo cache;
	
	public UndoInfo createUndoInfo(Collection<OriLine> lines){
		UndoInfo undoInfo = new UndoInfo(lines);
		return undoInfo;
	}

	public void push(UndoInfo uinfo){
		undoStack.push(uinfo);
	}

	public UndoInfo pop() {
		if (undoStack.isEmpty()) {
			return null;
		}

		return undoStack.pop();
	}

	public void setCache(UndoInfo info){
		cache = info;
	}
	
	public UndoInfo getCache(){
		return cache;
	}
	
	public void pushCachedInfo(){
		this.push(cache);
	}
}
