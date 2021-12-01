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

import java.util.List;
import java.util.stream.Collectors;

import oripa.util.Pair;

/**
 * Key-value style option.
 *
 * Each option should be described as "//key:value".
 *
 * @author OUCHI Koji
 *
 */
public class OptionParser {
	public static class Keys {
		public static final String FRONT_COLOR = "frontColor";
		public static final String BACK_COLOR = "backColor";
	}

	private static final String OPTION_START = "//";

	public boolean matchOptionStart(final String line) {
		return line.startsWith(OPTION_START);
	}

	public Pair<String, String> parse(final String line) {
		var trimmed = line.split(OPTION_START)[1].trim();
		var keyValue = trimmed.split("\\s*:\\s*");

		return new Pair<String, String>(keyValue[0], keyValue[1]);
	}

	public List<Pair<String, String>> parse(final List<String> lines) {
		return lines.stream()
				.map(this::parse)
				.collect(Collectors.toList());
	}

	public String createLine(final Pair<String, String> option) {
		return OPTION_START + option.getV1() + ":" + option.getV2();
	}

	public List<String> createLines(final List<Pair<String, String>> options) {
		return options.stream()
				.map(this::createLine)
				.collect(Collectors.toList());
	}
}
