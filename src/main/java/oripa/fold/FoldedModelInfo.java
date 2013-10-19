package oripa.fold;

import java.util.ArrayList;
import java.util.List;

public class FoldedModelInfo {
	private int overlapRelation[][] = null;
	private List<int[][]> overlapRelations = new ArrayList<int[][]>();
	private int currentORmatIndex = 0;

	BoundBox boundBox = new BoundBox(null, null);
	
	
	public void setNextORMat() {
		if (currentORmatIndex < overlapRelations.size() - 1) {
			currentORmatIndex++;
			Folder.matrixCopy(overlapRelations.get(currentORmatIndex), overlapRelation);
		}
	}

	public void setPrevORMat() {
		if (currentORmatIndex > 0) {
			currentORmatIndex--;
			Folder.matrixCopy(overlapRelations.get(currentORmatIndex), overlapRelation);
		}

	}

	public int[][] getOverlapRelation() {
		return overlapRelation;
	}

	public void setOverlapRelation(int[][] overlapRelation) {
		this.overlapRelation = overlapRelation;
	}

	public List<int[][]> getFoldableOverlapRelations() {
		return overlapRelations;
	}

	public void setFoldableOverlapRelations(List<int[][]> foldableOverlapRelations) {
		this.overlapRelations = foldableOverlapRelations;
	}
	

	public int getCurrentORmatIndex() {
		return currentORmatIndex;
	}

	public void setCurrentORmatIndex(int currentORmatIndex) {
		this.currentORmatIndex = currentORmatIndex;
	}

	/**
	 * @return boundBox
	 */
	public BoundBox getBoundBox() {
		return boundBox;
	}

	/**
	 * @param boundBox boundBoxを登録する
	 */
	public void setBoundBox(BoundBox boundBox) {
		this.boundBox = boundBox;
	}

	public int getFoldablePatternCount() {
		return overlapRelations.size();
	}
	
	
}
