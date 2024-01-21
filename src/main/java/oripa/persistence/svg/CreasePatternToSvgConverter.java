/*
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package oripa.persistence.svg;

import static oripa.persistence.svg.SVGUtils.*;

import java.util.stream.Collectors;

import oripa.domain.creasepattern.CreasePattern;
import oripa.value.OriLine;

/**
 * @author OUCHI Koji / BETTINELLI Jean-Noel
 */
public class CreasePatternToSvgConverter extends SvgConverter {

	public static final String SPACE = " ";
	CreasePattern creasePatternInterface;

	public CreasePatternToSvgConverter(final CreasePattern creasePatternInterface, final double scaleToFitDomain) {
		this.creasePatternInterface = creasePatternInterface;
		this.domain = creasePatternInterface.getPaperDomain();
		this.scaleToFitDomain = scaleToFitDomain;
	}

	public String getSvgCreasePattern() {
		return creasePatternInterface.stream()
				.map(oriLine -> getLinePathTag(mapToDomain(oriLine.getOriPoint0()), mapToDomain(oriLine.getOriPoint1()),
						getLineStyle(oriLine)))
				.map(stringBuilder -> stringBuilder.insert(0, SPACE))
				.map(StringBuilder::toString)
				.collect(Collectors.joining(""));
	}

	private String getLineStyle(final OriLine line) {
		switch (line.getType()) {
		case CUT:
			return getLinePathStyle(4, "black").toString();
		case MOUNTAIN:
			return getLinePathStyle(2, "red").toString();
		case VALLEY:
			return getLinePathStyle(2, "blue").toString();
		case UNASSIGNED:
			return getLinePathStyle(2, "orange").toString();
		default:
			return getLinePathStyle(1, "gray").toString();
		}
	}

}