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

import java.util.ArrayList;

import oripa.doc.OptionParser.Keys;
import oripa.util.Pair;

/**
 * @author Koji
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
	public Property(final String dataFilePath) {
		this.dataFilePath = dataFilePath;
	}

	/**
	 * @return dataFilePath
	 */
	public String getDataFilePath() {
		return dataFilePath;
	}

	/**
	 * @param dataFilePath
	 *            Sets dataFilePath
	 */
	public void setDataFilePath(final String dataFilePath) {
		this.dataFilePath = dataFilePath;
	}

	/**
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            Sets title
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * @return editorName
	 */
	public String getEditorName() {
		return editorName;
	}

	/**
	 * @param editorName
	 *            Sets editorName
	 */
	public void setEditorName(final String editorName) {
		this.editorName = editorName;
	}

	/**
	 * @return originalAuthorName
	 */
	public String getOriginalAuthorName() {
		return originalAuthorName;
	}

	/**
	 * @param originalAuthorName
	 *            Sets originalAuthorName
	 */
	public void setOriginalAuthorName(final String originalAuthorName) {
		this.originalAuthorName = originalAuthorName;
	}

	/**
	 * @return reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @param reference
	 *            Sets reference
	 */
	public void setReference(final String reference) {
		this.reference = reference;
	}

	/**
	 * @return memo
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * @param memo
	 *            Sets memo
	 */
	public void setMemo(final String memo) {
		this.memo = memo;
	}

	public String extractFrontColorCode() {
		return extractOption(Keys.FRONT_COLOR);
	}

	public void putFrontColorCode(final String code) {
		putOption(OptionParser.Keys.FRONT_COLOR, code);
	}

	public String extractBackColorCode() {
		return extractOption(Keys.BACK_COLOR);
	}

	public void putBackColorCode(final String code) {
		putOption(OptionParser.Keys.BACK_COLOR, code);
	}

	private String extractOption(final String key) {

		if (memo == null) {
			return null;
		}

		var parser = new OptionParser();

		var optionLines = memo.lines()
				.filter(line -> !parser.matchHeadCommentStart(line))
				.filter(line -> parser.matchOptionStart(line))
				.toList();

		var keyValueOpt = parser.parse(optionLines).stream()
				.filter(option -> option.getV1().equals(key))
				.findFirst();

		if (keyValueOpt.isEmpty()) {
			return null;
		}

		return keyValueOpt.get().getV2();
	}

	private void putOption(final String key, final String value) {
		var optionLines = new ArrayList<String>();
		var textLines = new ArrayList<String>();

		var parser = new OptionParser();

		if (memo == null) {
			memo = "";
		}

		memo.lines().forEach(line -> {
			if (parser.matchHeadCommentStart(line)) {
				return;
			}

			if (parser.matchOptionStart(line)) {
				optionLines.add(line);
			} else {
				textLines.add(line);
			}
		});

		// update
		var options = new ArrayList<>(parser.parse(optionLines));
		boolean updated = false;
		for (int i = 0; i < options.size(); i++) {
			var option = options.get(i);
			if (option.getV1().equals(key)) {
				options.set(i, new Pair<>(key, value));
				updated = true;
				break;
			}
		}

		// insert
		if (!updated) {
			options.add(new Pair<>(key, value));
		}

		String lineSep = System.lineSeparator();

		memo = OptionParser.HEAD_COMMENT + lineSep
				+ String.join(lineSep, parser.createLines(options)) + lineSep
				+ String.join(lineSep, textLines);
	}
}