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

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.domain.cutmodel.CutModelOutlineFactory;
import oripa.domain.fold.FoldedModelInfo;
import oripa.domain.fold.OrigamiModel;
import oripa.domain.fold.OrigamiModelFactory;
import oripa.exception.UserCanceledException;
import oripa.persistent.filetool.FileAccessSupportFilter;
import oripa.persistent.filetool.FileChooserCanceledException;
import oripa.resource.Constants;
import oripa.value.OriLine;

/**
 * Manages all result of this app's action.
 * 
 * @author Koji
 * 
 */
public class Doc implements SheetCutOutlinesHolder {

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

	int debugCount = 0;

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

	public String getDataFilePath() {
		return property.getDataFilePath();
	}

	public String getDataFileName() {
		File file = new File(property.getDataFilePath());
		String fileName = file.getName();

		return fileName;

	}

	// ===================================================================================================
	// Persistent
	// ===================================================================================================

	private final DocFilterSelector filterDB = new DocFilterSelector();

	public String saveFileUsingGUI(final String directory,
			final String fileName,
			final Component owner,
			final FileAccessSupportFilter<Doc>[] filters)
			throws UserCanceledException {

		File givenFile = new File(directory, fileName);

		return saveFileUsingGUI(givenFile.getPath(), owner, filters);
	}

	public String saveFileUsingGUI(
			final String homePath,
			final Component owner,
			@SuppressWarnings("unchecked") final FileAccessSupportFilter<Doc>... filters)
			throws UserCanceledException {

		DocDAO dao = new DocDAO();
		try {
			return dao.saveUsingGUI(this, null, owner, filters);
		} catch (FileChooserCanceledException e) {
			throw new UserCanceledException();
		}

	}

	public void saveOpxFile(final String filePath) {
		DocDAO dao = new DocDAO();
		dao.save(this, filePath, FileTypeKey.OPX);

		// updateMenu(filePath);
		// updateTitleText();
		// clearChanged();
	}

	public void saveModelFile(final FileTypeKey type, final Component owner)
			throws UserCanceledException {
		CreasePatternInterface creasePattern = getCreasePattern();
		OrigamiModel origamiModel = getOrigamiModel();

		boolean hasModel = origamiModel.hasModel();

		OrigamiModelFactory modelFactory = new OrigamiModelFactory();
		origamiModel = modelFactory.buildOrigami(creasePattern,
				getPaperSize(), true);
		setOrigamiModel(origamiModel);

		if (type == FileTypeKey.OBJ_MODEL) {

		} else if (!hasModel && !origamiModel.isProbablyFoldable()) {

			JOptionPane.showConfirmDialog(null,
					"Warning: Building a set of polygons from crease pattern "
							+ "was failed.", "Warning", JOptionPane.OK_OPTION,
					JOptionPane.WARNING_MESSAGE);
		}

		DocDAO dao = new DocDAO();

		try {
			dao.saveUsingGUI(this, null,
					owner, filterDB.getFilter(type));
		} catch (FileChooserCanceledException e) {
			throw new UserCanceledException();
		}

	}

//	public void loadFileUsingGUI(final String path, final Component owner) throws FileVersionError {
//		DocDAO dao = new DocDAO();
//		DocFilterSelector selector = new DocFilterSelector();
//
//		set(dao.loadUsingGUI(
//				path, selector.getLoadables(),
//				owner));
//
//	}

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

	public CreasePatternInterface getCreasePattern() {
		return creasePattern;
	}

	public void setCreasePattern(final CreasePatternInterface cp) {
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
	public void setOrigamiModel(final OrigamiModel origamiModel) {
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

	// ======================================================================
	// Getter/Setter eventually unnecessary

	// /**
	// * @param crossLines crossLines is set to this instance.
	// */
	// public void setCrossLines(List<OriLine> sheetCutOutlines) {
	// this.sheetCutLines = sheetCutOutlines;
	// }

//	/**
//	 * @param size
//	 *            size is set to this instance.
//	 */
//	public void setPaperSize(final double size) {
//		// this.paperSize = size;
//		// origamiModel.setPaperSize(size);
//		creasePattern.changePaperSize(size);
//	}

	/**
	 * @return size
	 */
	public double getPaperSize() {
		return creasePattern.getPaperSize();
		// return paperSize;
	}
	//
	// /**
	// * @return title
	// */
	// public String getTitle() {
	// return property.getTitle();
	// }
	//
	// /**
	// * @param title
	// * title is set to this instance.
	// */
	// public void setTitle(String title) {
	// this.property.setTitle(title);
	// }
	//
	// /**
	// * @return editorName
	// */
	// public String getEditorName() {
	// return property.getEditorName();
	// }
	//
	// /**
	// * @param editorName
	// * editorName is set to this instance.
	// */
	// public void setEditorName(String editorName) {
	// this.property.setEditorName(editorName);
	// }
	//
	// /**
	// * @return originalAuthorName
	// */
	// public String getOriginalAuthorName() {
	// return property.getOriginalAuthorName();
	// }
	//
	// /**
	// * @param originalAuthorName
	// * originalAuthorName is set to this instance.
	// */
	// public void setOriginalAuthorName(String originalAuthorName) {
	// this.property.setOriginalAuthorName(originalAuthorName);
	// }
	//
	// /**
	// * @return reference
	// */
	// public String getReference() {
	// return property.getReference();
	// }
	//
	// /**
	// * @param reference
	// * reference is set to this instance.
	// */
	// public void setReference(String reference) {
	// this.property.setReference(reference);
	// }
	//
}
