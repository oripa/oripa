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

package oripa.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.cutmodel.CutModelOutlinesHolder;
import oripa.domain.paint.CreasePatternHolder;
import oripa.value.OriLine;

/**
 * Manages all result of this app's action.
 *
 * @author Koji
 *
 */
public class Doc implements CutModelOutlinesHolder, CreasePatternHolder {

	/**
	 * Crease Pattern
	 */
	private CreasePattern creasePattern = null;

	private Collection<OriLine> outlines = new ArrayList<OriLine>();

	/**
	 * Project data
	 */
	private Property property = new Property("");

	public Doc(final double size) {
		initialize(size);
	}

	public Doc(final CreasePattern creasePattern) {
		this.creasePattern = creasePattern;
	}

	public void set(final Doc doc) {
		setCreasePattern(doc.getCreasePattern());
		setProperty(doc.getProperty());

		outlines = doc.getOutlines();
	}

	private void initialize(final double size) {

		creasePattern = (new CreasePatternFactory()).createCreasePattern(size);
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
	 * oripa.domain.cutmodel.SheetCutOutlinesHolder#updateSheetCutOutlines(oripa
	 * .value.OriLine)
	 */
	@Override
	public void setOutlines(final Collection<OriLine> outlines) {
		this.outlines = outlines;
	}

	/*
	 * (non Javadoc)
	 *
	 * @see
	 * oripa.domain.cutmodel.doc.SheetCutOutlinesHolder#getSheetCutOutlines()
	 */
	@Override
	public Collection<OriLine> getOutlines() {
		return outlines;
	}

	@Override
	public CreasePattern getCreasePattern() {
		return creasePattern;
	}

	@Override
	public void setCreasePattern(final CreasePattern cp) {
		creasePattern = cp;
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

}