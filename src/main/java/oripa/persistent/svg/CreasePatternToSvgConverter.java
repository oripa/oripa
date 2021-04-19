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

package oripa.persistent.svg;

import oripa.domain.creasepattern.CreasePatternInterface;
import oripa.value.OriLine;

import java.util.stream.Collectors;

import static oripa.persistent.svg.SVGUtils.getLineTag;

/**
 * @author OUCHI Koji / BETTINELLI Jean-Noel
 */
public class CreasePatternToSvgConverter extends SvgConverter {

    public static final String SPACE = " ";
    CreasePatternInterface creasePatternInterface;

    public CreasePatternToSvgConverter(CreasePatternInterface creasePatternInterface, double scaleToFitDomain) {
        this.creasePatternInterface = creasePatternInterface;
        this.domain = creasePatternInterface.getPaperDomain();
        this.scaleToFitDomain = scaleToFitDomain;
    }

    public String getSvgCreasePattern() {
        return creasePatternInterface.stream()
                .map(oriLine -> getLineTag(mapToDomain(oriLine.p0), mapToDomain(oriLine.p1), getLineStyle(oriLine)))
                .map(stringBuilder -> stringBuilder.insert(0, SPACE))
                .map(StringBuilder::toString)
                .collect(Collectors.joining(""));
    }

    private String getLineStyle(OriLine line) {
        String style = "style=\"";
        switch (line.getType()) {
            case CUT:
                style += "stroke:black;stroke-width:4;";
                break;
            case MOUNTAIN:
                style += "stroke:red;stroke-width:2;";
                break;
            case VALLEY:
                style += "stroke:blue;stroke-width:2;stroke-opacity:1";
                break;
            default:
                style += "stroke:gray;stroke-width:1;";
        }
        style += "\"";
        return style;
    }


}