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
package oripa.domain.projectprop;

import java.util.ArrayList;

import oripa.domain.projectprop.OptionParser.Keys;

/**
 * @author Koji
 */
public class Property {
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
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            Sets title
	 */
	public Property setTitle(final String title) {
		this.title = title;
		return this;
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
	public Property setEditorName(final String editorName) {
		this.editorName = editorName;
		return this;
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
	public Property setOriginalAuthorName(final String originalAuthorName) {
		this.originalAuthorName = originalAuthorName;
		return this;
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
	public Property setReference(final String reference) {
		this.reference = reference;
		return this;
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
	public Property setMemo(final String memo) {
		this.memo = memo;
		return this;
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
				.filter(option -> option.key().equals(key))
				.findFirst();

		if (keyValueOpt.isEmpty()) {
			return null;
		}

		return keyValueOpt.get().value();
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
			if (option.key().equals(key)) {
				options.set(i, new Option(key, value));
				updated = true;
				break;
			}
		}

		// insert
		if (!updated) {
			options.add(new Option(key, value));
		}

		String lineSep = System.lineSeparator();

		memo = OptionParser.HEAD_COMMENT + lineSep
				+ String.join(lineSep, parser.createLines(options)) + lineSep
				+ String.join(lineSep, textLines);
	}
}