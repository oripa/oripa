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

import oripa.domain.fold.halfedge.OriFace;
import oripa.domain.fold.halfedge.OriHalfedge;
import oripa.geom.RectangleDomain;
import oripa.value.OriLine;

import javax.vecmath.Vector2d;
import java.util.List;
import java.util.stream.Collectors;

import static oripa.persistent.svg.SVGUtils.*;

/**
 * @author OUCHI Koji / BETTINELLI Jean-Noel
 */
public class FacesToSvgConverter extends SvgConverter {

    /**
     * Line and polygon styles
     */
    String frontFaceStyle = "";
    String backFaceStyle = "";
    String precreaseLineStyle = "";

    public void initDomain(List<OriFace> faces, double paperSize) {
        domain = new RectangleDomain();
        scaleToFitDomain = SVG_SIZE / paperSize;
        faces.stream()
                .flatMap(OriFace::halfedgeStream)
                .map(OriHalfedge::getPosition)
                .forEach(domain::enlarge);
    }

    public void setFaceStyles(String bothFacesStyle) {
        setFaceStyles(bothFacesStyle, bothFacesStyle);
    }

    public void setFaceStyles(String frontFaceStyle, String backFaceStyle) {
        this.frontFaceStyle = frontFaceStyle;
        this.backFaceStyle = backFaceStyle;
    }

    public void setPrecreaseLineStyle(String precreaseLineStyle) {
        this.precreaseLineStyle = precreaseLineStyle;
    }

    public String getSvgFaces(List<OriFace> faces) {
        StringBuilder svgBuilder = new StringBuilder();

        faces.forEach(oriFace -> svgBuilder.append(getSvgFace(oriFace)));

        putInGroup(svgBuilder);
        return svgBuilder.toString();
    }

    private StringBuilder getSvgFace(OriFace face) {
        StringBuilder faceBuilder = new StringBuilder();
        List<Vector2d> points = mapPointsToDomain(face);

        String polygonStyle = face.isFaceFront() ? frontFaceStyle : backFaceStyle;

        faceBuilder.append(getPathTag(points, polygonStyle));

        face.precreaseStream().forEach(oriLine -> faceBuilder.append(getPrecreaseLineTag(oriLine)));

        if (face.hasPrecreases()) {
            putInGroup(faceBuilder);
        }

        return faceBuilder;
    }

    private List<Vector2d> mapPointsToDomain(OriFace face) {
        return face.halfedgeStream()
                .map(OriHalfedge::getPosition)
                .map(this::mapToDomain)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Calculates the coordinates of the precrease Line and returns a builder containing the "line" tag
     *
     * @param oriLine Precrease line
     * @return builder containing the SVG line Tag
     */
    private StringBuilder getPrecreaseLineTag(OriLine oriLine) {
        Vector2d startPoint = mapToDomain(oriLine.p0);
        Vector2d endPoint = mapToDomain(oriLine.p1);
        return SVGUtils.getLineTag(startPoint, endPoint, precreaseLineStyle);
    }

}