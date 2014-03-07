/**
 * ORIPA - Origami Pattern Editor 
 * Copyright (C) 2005-2009 Jun Mitani http://mitani.cs.tsukuba.ac.jp/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package oripa.persistent.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.ORIPA;
import oripa.controller.paint.CreasePatternUndoManager;
import oripa.controller.paint.history.CreasePatternUndoFactory;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.cutmodel.CutModelOutlineFactory;
import oripa.domain.fold.FoldedModelInfo;
import oripa.domain.fold.OrigamiModel;
import oripa.resource.Constants;
import oripa.util.history.AbstractUndoManager;
import oripa.util.history.UndoInfo;
import oripa.value.OriLine;

public class Doc {

	// private double paperSize;

	// Crease Pattern

	private CreasePatternInterface creasePattern = null;
	private List<OriLine> sheetCutLines = new ArrayList<OriLine>();

	// Origami Model for Estimation
	private OrigamiModel origamiModel = null;

	// Folded Model Information (Result of Estimation)

	private FoldedModelInfo foldedModelInfo = null;

	// Project data

	private Property property = new Property("");

	private AbstractUndoManager<Collection<OriLine>> undoManager = new CreasePatternUndoManager(
			30);

	int debugCount = 0;

	public Doc() {
		initialize(Constants.DEFAULT_PAPER_SIZE);
	}

	public Doc(double size) {
		initialize(size);
	}

	public void set(Doc doc) {
		setCreasePattern(doc.getCreasePattern());
		setOrigamiModel(doc.getOrigamiModel());
		setFoldedModelInfo(doc.getFoldedModelInfo());
		setProperty(doc.getProperty());

		sheetCutLines = doc.getSheetCutOutlines();
		setPaperSize(doc.getPaperSize());

		undoManager = doc.undoManager;
	}

	private void initialize(double size) {

		// this.paperSize = size;
		creasePattern = (new CreasePatternFactory()).createCreasePattern(size);

		origamiModel = new OrigamiModel(size);
		foldedModelInfo = new FoldedModelInfo();
	}

	public void setDataFilePath(String path) {
		this.property.setDataFilePath(path);
	}

	public String getDataFilePath() {
		return property.getDataFilePath();
	}

	public String getDataFileName() {
		File file = new File(ORIPA.doc.property.getDataFilePath());
		String fileName = file.getName();

		return fileName;

	}

	// TODO move undo operations to paint.cptool.Painter

	CreasePatternUndoFactory factory = new CreasePatternUndoFactory();

	public UndoInfo<Collection<OriLine>> createUndoInfo() {
		UndoInfo<Collection<OriLine>> undoInfo = factory.create(creasePattern);
		return undoInfo;
	}

	public void cacheUndoInfo() {
		undoManager.setCache(creasePattern);
	}

	public void pushCachedUndoInfo() {
		undoManager.pushCachedInfo();
	}

	public void pushUndoInfo() {
		undoManager.push(creasePattern);
	}

	public void pushUndoInfo(UndoInfo<Collection<OriLine>> uinfo) {
		undoManager.push(uinfo);
	}

	public void loadUndoInfo() {
		UndoInfo<Collection<OriLine>> info = undoManager.pop();

		if (info == null) {
			return;
		}

		creasePattern.clear();
		creasePattern.addAll(info.getInfo());
	}

	public boolean canUndo() {
		return undoManager.canUndo();
	}

	public boolean isChanged() {
		return undoManager.isChanged();
	}

	public void clearChanged() {
		undoManager.clearChanged();
	}

	/**
	 * make lines that composes the outline of a shape obtained by cutting the
	 * folded model.
	 * 
	 * @param scissorLine
	 */
	public void updateSheetCutOutlines(OriLine scissorLine) {
		CutModelOutlineFactory factory = new CutModelOutlineFactory();

		sheetCutLines.clear();
		sheetCutLines.addAll(factory.createLines(scissorLine, origamiModel));
	}

	public Collection<Vector2d> getVerticesAround(Vector2d v) {
		return creasePattern.getVerticesAround(v);
	}

	public Collection<Collection<Vector2d>> getVerticesArea(double x, double y,
			double distance) {

		return creasePattern.getVerticesInArea(x, y, distance);
	}

	public CreasePatternInterface getCreasePattern() {
		return creasePattern;
	}

	public void setCreasePattern(CreasePatternInterface cp) {
		creasePattern = cp;
	}

	/**
	 * @return origamiModel
	 */
	public OrigamiModel getOrigamiModel() {
		return origamiModel;
	}

	/**
	 * @param origamiModel
	 *            origamiModel is set to this instance.
	 */
	public void setOrigamiModel(OrigamiModel origamiModel) {
		this.origamiModel = origamiModel;
	}

	/**
	 * @return foldedModelInfo
	 */
	public FoldedModelInfo getFoldedModelInfo() {
		return foldedModelInfo;
	}

	/**
	 * @param foldedModelInfo
	 *            foldedModelInfo is set to this instance.
	 */
	public void setFoldedModelInfo(FoldedModelInfo foldedModelInfo) {
		this.foldedModelInfo = foldedModelInfo;
	}

	/**
	 * @return property
	 */
	public Property getProperty() {
		return property;
	}

	/**
	 * @return crossLines
	 */
	public List<OriLine> getSheetCutOutlines() {
		return sheetCutLines;
	}

	/**
	 * @param property
	 *            Sets property
	 */
	public void setProperty(Property property) {
		this.property = property;
	}

	// ======================================================================
	// Getter/Setter eventually unnecessary

	// /**
	// * @param crossLines crossLines is set to this instance.
	// */
	// public void setCrossLines(List<OriLine> sheetCutOutlines) {
	// this.sheetCutLines = sheetCutOutlines;
	// }

	/**
	 * @param size
	 *            size is set to this instance.
	 */
	public void setPaperSize(double size) {
		// this.paperSize = size;
		// origamiModel.setPaperSize(size);
		creasePattern.changePaperSize(size);
	}

	/**
	 * @return size
	 */
	public double getPaperSize() {
		return creasePattern.getPaperSize();
		// return paperSize;
	}

	/**
	 * @return title
	 */
	public String getTitle() {
		return property.getTitle();
	}

	/**
	 * @param title
	 *            title is set to this instance.
	 */
	public void setTitle(String title) {
		this.property.setTitle(title);
	}

	/**
	 * @return editorName
	 */
	public String getEditorName() {
		return property.getEditorName();
	}

	/**
	 * @param editorName
	 *            editorName is set to this instance.
	 */
	public void setEditorName(String editorName) {
		this.property.setEditorName(editorName);
	}

	/**
	 * @return originalAuthorName
	 */
	public String getOriginalAuthorName() {
		return property.getOriginalAuthorName();
	}

	/**
	 * @param originalAuthorName
	 *            originalAuthorName is set to this instance.
	 */
	public void setOriginalAuthorName(String originalAuthorName) {
		this.property.setOriginalAuthorName(originalAuthorName);
	}

	/**
	 * @return memo
	 */
	public String getMemo() {
		return property.getMemo();
	}

	/**
	 * @param memo
	 *            memo is set to this instance.
	 */
	public void setMemo(String memo) {
		this.property.setMemo(memo);
	}

	/**
	 * @return reference
	 */
	public String getReference() {
		return property.getReference();
	}

	/**
	 * @param reference
	 *            reference is set to this instance.
	 */
	public void setReference(String reference) {
		this.property.setReference(reference);
	}

}
