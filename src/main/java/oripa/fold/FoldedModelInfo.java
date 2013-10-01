package oripa.fold;

import java.util.ArrayList;

public class FoldedModelInfo {
	private int overlapRelation[][] = null;
	private ArrayList<int[][]> overlapRelations = new ArrayList<int[][]>();
	private int currentORmatIndex = 0;

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

	public ArrayList<int[][]> getOverlapRelations() {
		return overlapRelations;
	}

	public void setOverlapRelations(ArrayList<int[][]> overlapRelations) {
		this.overlapRelations = overlapRelations;
	}

	public int getCurrentORmatIndex() {
		return currentORmatIndex;
	}

	public void setCurrentORmatIndex(int currentORmatIndex) {
		this.currentORmatIndex = currentORmatIndex;
	}

	
}
