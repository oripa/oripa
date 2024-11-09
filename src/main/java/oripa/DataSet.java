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

package oripa;

import java.util.ArrayList;

import oripa.doc.Doc;
import oripa.domain.creasepattern.CreasePattern;
import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.docprop.Property;
import oripa.resource.Version;
import oripa.value.OriLine;

// TODO: Move this class to the package of persistence layer in order to remove circular dependency.
// This change disables to read old opx file since serialization target becomes different.
public class DataSet {

	private int mainVersion;
	private int subVersion;
	public OriLineProxy[] lines;

	// meaningless since ORIPA ver. 1.25 but kept for compatibility
	private double paperSize;

	public String title;
	public String editorName;
	public String originalAuthorName;
	public String reference;
	public String memo;

	public DataSet() {
	}

	public DataSet(final Doc doc) {
		mainVersion = Version.FILE_MAJOR_VERSION;
		subVersion = Version.FILE_MINOR_VERSION;

		// reconstruct CP to refresh the paper size
		var factory = new CreasePatternFactory();
		CreasePattern creasePattern = factory
				.createCreasePattern(doc.getCreasePattern());

		Property property = doc.getProperty();

		int lineNum = creasePattern.size();

		lines = new OriLineProxy[lineNum];

		OriLine[] docLines = new OriLine[lineNum];
		creasePattern.toArray(docLines);

		for (int i = 0; i < lineNum; i++) {
			lines[i] = new OriLineProxy(docLines[i]);
		}

		paperSize = creasePattern.getPaperSize();

		title = property.getTitle();
		editorName = property.getEditorName();
		originalAuthorName = property.getOriginalAuthorName();
		reference = property.getReference();
		memo = property.getMemo();
	}

	public Doc recover(final String filePath) {

		var oriLines = new ArrayList<OriLine>();

		for (int i = 0; i < lines.length; i++) {
			oriLines.add(lines[i].getLine());
		}

		CreasePatternFactory factory = new CreasePatternFactory();
		CreasePattern creasePattern = factory
				.createCreasePattern(oriLines);

		Doc doc = new Doc(creasePattern, createProperty(), filePath);

		return doc;
	}

	private Property createProperty() {
		return new Property()
				.setTitle(title)
				.setEditorName(editorName)
				.setOriginalAuthorName(originalAuthorName)
				.setReference(reference)
				.setMemo(memo);
	}

	public void setPaperSize(final double d) {
		paperSize = d;
	}

	public double getPaperSize() {
		return paperSize;
	}

	public void setMainVersion(final int i) {
		mainVersion = i;
	}

	public int getMainVersion() {
		return mainVersion;
	}

	public void setSubVersion(final int i) {
		subVersion = i;
	}

	public int getSubVersion() {
		return subVersion;
	}

	public void setLines(final OriLineProxy[] l) {
		lines = l;
	}

	public OriLineProxy[] getLines() {
		return lines;
	}

	public void setTitle(final String s) {
		title = s;
	}

	public String getTitle() {
		return title;
	}

	public void setEditorName(final String s) {
		editorName = s;
	}

	public String getEditorName() {
		return editorName;
	}

	public void setOriginalAuthorName(final String s) {
		originalAuthorName = s;
	}

	public String getOriginalAuthorName() {
		return originalAuthorName;
	}

	public void setReference(final String s) {
		reference = s;
	}

	public String getReference() {
		return reference;
	}

	public void setMemo(final String s) {
		memo = s;
	}

	public String getMemo() {
		return memo;
	}
}
