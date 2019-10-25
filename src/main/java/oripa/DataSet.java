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

import oripa.domain.creasepattern.CreasePatternFactory;
import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.persistent.doc.Doc;
import oripa.persistent.doc.Property;
import oripa.resource.Version;
import oripa.value.OriLine;

public class DataSet {

	private int mainVersion;
	private int subVersion;
	public OriLineProxy[] lines;
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

		CreasePatternInterface creasePattern = doc.getCreasePattern();
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

		CreasePatternFactory factory = new CreasePatternFactory();
		CreasePatternInterface creasePattern = factory
				.createEmptyCreasePattern(paperSize);

		for (int i = 0; i < lines.length; i++) {
			creasePattern.add(lines[i].getLine());
		}

		Doc doc = new Doc(paperSize);
		doc.setCreasePattern(creasePattern);
		// doc.setPaperSize(paperSize);

		doc.setProperty(createProperty(filePath));

		return doc;

	}

	private Property createProperty(final String filePath) {
		Property property = new Property(filePath);
		property.setTitle(title);
		property.setEditorName(editorName);
		property.setOriginalAuthorName(originalAuthorName);
		property.setReference(reference);
		property.setMemo(memo);

		return property;
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
