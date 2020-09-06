/**
 * ORIPA - Origami Pattern Editor 
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

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

/**
 * @author  Koji
 */
public class Property {
	/**
	 * 
	 */
	private String dataFilePath;
	/**
	 * 
	 */
	private String title;
	/**
	 * 
	 */
	private String editorName;
	/**
	 * 
	 */
	private String originalAuthorName;
	/**
	 * 
	 */
	private String reference;
	/**
	 * 
	 */
	private String memo;

	/**
	 * Constructor
	 */
	public Property(String dataFilePath) {
		this.dataFilePath = dataFilePath;
	}

	/**
	 * @return dataFilePath
	 */
	public String getDataFilePath() {
		return dataFilePath;
	}

	/**
	 * @param dataFilePath Sets dataFilePath
	 */
	public void setDataFilePath(String dataFilePath) {
		this.dataFilePath = dataFilePath;
	}

	/**
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title Sets title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return editorName
	 */
	public String getEditorName() {
		return editorName;
	}

	/**
	 * @param editorName Sets editorName
	 */
	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}

	/**
	 * @return originalAuthorName
	 */
	public String getOriginalAuthorName() {
		return originalAuthorName;
	}

	/**
	 * @param originalAuthorName Sets originalAuthorName
	 */
	public void setOriginalAuthorName(String originalAuthorName) {
		this.originalAuthorName = originalAuthorName;
	}

	/**
	 * @return reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @param reference Sets reference
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * @return memo
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * @param memo Sets memo
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}
}