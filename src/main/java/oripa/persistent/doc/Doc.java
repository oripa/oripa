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
import java.util.List;

import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.cutmodel.CutModelOutlineFactory;
import oripa.domain.fold.FoldedModelInfo;
import oripa.domain.fold.OrigamiModel;
import oripa.domain.paint.CreasePatternHolder;
import oripa.resource.Constants;
import oripa.value.OriLine;

/**
 * Manages all result of this app's action.
 *
 * @author Koji
 *
 */
public class Doc implements SheetCutOutlinesHolder, CreasePatternHolder, EstimationEntityHolder {

	/**
	 * Crease Pattern
	 */
	private CreasePatternInterface creasePattern = null;

	private List<OriLine> sheetCutLines = new ArrayList<OriLine>();

	/**
	 * Origami Model for Estimation
	 */
	private OrigamiModel origamiModel = null;

	/**
	 * Folded Model Information (Result of Estimation)
	 */
	private FoldedModelInfo foldedModelInfo = null;

	/**
	 * Project data
	 */
	private Property property = new Property("");

	public Doc() {
		initialize(Constants.DEFAULT_PAPER_SIZE);
	}

	public Doc(final double size) {
		initialize(size);
	}

	public void set(final Doc doc) {
		setCreasePattern(doc.getCreasePattern());
		setOrigamiModel(doc.getOrigamiModel());
		setFoldedModelInfo(doc.getFoldedModelInfo());
		setProperty(doc.getProperty());

		sheetCutLines = doc.getSheetCutOutlines();
		// setPaperSize(doc.getPaperSize());

	}

	private void initialize(final double size) {

		creasePattern = (new CreasePatternFactory()).createCreasePattern(size);

		origamiModel = new OrigamiModel(size);
		foldedModelInfo = new FoldedModelInfo();
	}

	public void setDataFilePath(final String path) {
		property.setDataFilePath(path);
	}

	public String getDataFilePath() {
		return property.getDataFilePath();
	}

	public String getDataFileName() {
		File file = new File(property.getDataFilePath());
		String fileName = file.getName();

		return fileName;

	}

	// ===================================================================================================

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.persistent.doc.SheetCutOutlinesHolder#updateSheetCutOutlines(oripa
	 * .value.OriLine)
	 */
	@Override
	public void updateSheetCutOutlines(final OriLine scissorLine) {
		CutModelOutlineFactory factory = new CutModelOutlineFactory();

		sheetCutLines.clear();
		sheetCutLines.addAll(factory.createLines(scissorLine, origamiModel));
	}

	@Override
	public CreasePatternInterface getCreasePattern() {
		return creasePattern;
	}

	@Override
	public void setCreasePattern(final CreasePatternInterface cp) {
		creasePattern = cp;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.persistent.doc.EstimationEntityHolder#getOrigamiModel()
	 */
	@Override
	public OrigamiModel getOrigamiModel() {
		return origamiModel;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.persistent.doc.EstimationEntityHolder#setOrigamiModel(oripa.domain.
	 * fold.OrigamiModel)
	 */
	@Override
	public void setOrigamiModel(final OrigamiModel origamiModel) {
		this.origamiModel = origamiModel;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.persistent.doc.EstimationEntityHolder#getFoldedModelInfo()
	 */
	@Override
	public FoldedModelInfo getFoldedModelInfo() {
		return foldedModelInfo;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.persistent.doc.EstimationEntityHolder#setFoldedModelInfo(oripa.
	 * domain.fold.FoldedModelInfo)
	 */
	@Override
	public void setFoldedModelInfo(final FoldedModelInfo foldedModelInfo) {
		this.foldedModelInfo = foldedModelInfo;
	}

	/**
	 * @return property
	 */
	public Property getProperty() {
		return property;
	}

	/**
	 * @param property
	 *            Sets property
	 */
	public void setProperty(final Property property) {
		this.property = property;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see oripa.persistent.doc.SheetCutOutlinesHolder#getSheetCutOutlines()
	 */
	@Override
	public List<OriLine> getSheetCutOutlines() {
		return sheetCutLines;
	}
}