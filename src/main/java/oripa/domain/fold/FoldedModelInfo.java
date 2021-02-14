package oripa.domain.fold;

import java.util.ArrayList;
import java.util.List;

import oripa.util.Matrices;

public class FoldedModelInfo {
	private int overlapRelation[][] = null;
	private List<int[][]> overlapRelations = new ArrayList<int[][]>();
	private int currentORmatIndex = 0;

	private BoundBox boundBox = new BoundBox(null, null);

	public void setNextORMat() {
		if (currentORmatIndex < overlapRelations.size() - 1) {
			currentORmatIndex++;
			Matrices.copy(overlapRelations.get(currentORmatIndex), overlapRelation);
		}
	}

	public void setPrevORMat() {
		if (currentORmatIndex > 0) {
			currentORmatIndex--;
			Matrices.copy(overlapRelations.get(currentORmatIndex), overlapRelation);
		}

	}

	public int[][] getOverlapRelation() {
		return overlapRelation;
	}

	public void setOverlapRelation(final int[][] overlapRelation) {
		this.overlapRelation = overlapRelation;
	}

	public List<int[][]> getFoldableOverlapRelations() {
		return overlapRelations;
	}

	public void setFoldableOverlapRelations(final List<int[][]> foldableOverlapRelations) {
		this.overlapRelations = foldableOverlapRelations;
	}

	public int getCurrentORmatIndex() {
		return currentORmatIndex;
	}

	public void setCurrentORmatIndex(final int currentORmatIndex) {
		this.currentORmatIndex = currentORmatIndex;
	}

	/**
	 * @return boundBox
	 */
	public BoundBox getBoundBox() {
		return boundBox;
	}

	/**
	 * @param boundBox
	 */
	public void setBoundBox(final BoundBox boundBox) {
		this.boundBox = boundBox;
	}

	public int getFoldablePatternCount() {
		return overlapRelations.size();
	}

}
