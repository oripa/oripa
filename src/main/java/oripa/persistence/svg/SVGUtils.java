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

package oripa.persistence.svg;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import oripa.vecmath.Vector2d;

/**
 * @author OUCHI Koji / BETTINELLI Jean-Noel
 */
public class SVGUtils {
    private SVGUtils() {
    }

    public static final int SVG_SIZE = 1000;
    public static final double SVG_HALF_SIZE = (double) SVG_SIZE / 2;

    public static final String SVG_XML_HEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>\n"
            + "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20010904//EN\"\n"
            + "\"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\">\n";

    public static final String SVG_START_TAG = "<svg xmlns=\"http://www.w3.org/2000/svg\"\n"
            + " xmlns:xlink=\"http://www.w3.org/1999/xlink\" xml:space=\"preserve\"\n"
            + " width=\"" + SVG_SIZE + "px\" height=\"" + SVG_SIZE + "px\"\n"
            + " viewBox=\"0 0 " + SVG_SIZE + " " + SVG_SIZE + "\" >\n";

    public static final String SVG_START = SVG_XML_HEADER + SVG_START_TAG;

    public static final String SVG_END_TAG = "</svg>";

    /**
     * FoldedModelExporterSVG styles
     */
    public static final String GRADIENT_FRONT = "<linearGradient id=\"Gradient1\"" +
            " x1=\"20%\" y1=\"0%\" x2=\"80%\" y2=\"100%\">\n" +
            "    <stop offset=\"5%\" stop-color=\"#DDEEFF\" />\n" +
            "    <stop offset=\"95%\" stop-color=\"#7788FF\" />\n" +
            "</linearGradient>\n";

    public static final String GRADIENT_BACK = "<linearGradient id=\"Gradient2\"" +
            " x1=\"20%\" y1=\"0%\" x2=\"80%\" y2=\"100%\">\n" +
            "    <stop offset=\"5%\" stop-color=\"#FFFFEE\" />\n" +
            "    <stop offset=\"95%\" stop-color=\"#DDDDDD\" />\n" +
            "</linearGradient>\n";

    public static final String GRADIENTS_DEFINITION = GRADIENT_FRONT + GRADIENT_BACK;

    /**
     * OrigamiModelExporterSVG styles
     */
    public static final String PATH_STYLE_TRANSLUCENT = getFacePathStyle(0.25, "#000000", 0.04).toString();

    public static final String THIN_LINE_STYLE = getPrecreasePathStyle(0.25).toString();

    /**
     *
     * @param strokeWidth
     * @param fillColorCode
     *            in the style of "#000000"
     * @return
     */
    public static StringBuilder getFrontPathStyle(final double strokeWidth, final String fillColorCode) {
        return getFacePathStyle(strokeWidth, fillColorCode, 1.0);
    }

    public static StringBuilder getBackPathStyle(final double strokeWidth, final String fillColorCode) {
        return getFacePathStyle(strokeWidth, fillColorCode, 1.0);
    }

    private static StringBuilder getFacePathStyle(final double strokeWidth, final String fillColorCode,
            final double fillOpacity) {
        return getPathStyle(strokeWidth, "#000000", fillColorCode, fillOpacity);
    }

    /**
     * for X-ray/layer-ordered model
     *
     * @param strokeWidth
     * @return
     */
    public static StringBuilder getPrecreasePathStyle(final double strokeWidth) {
        return getLinePathStyle(strokeWidth, "#000000");
    }

    private static StringBuilder getPathStyle(final double strokeWidth, final String strokeColorCode,
            final String fillColorCode, final double fillOpacity) {
        return new StringBuilder("style=\"")
                .append(Stream.of(
                        getFillToken(fillColorCode),
                        getStrokeToken(strokeColorCode),
                        getStrokeWidthToken(strokeWidth),
                        "stroke-linecap:round",
                        "stroke-linejoin:round",
                        "stroke-opacity:1",
                        getFillOpacityToken(fillOpacity))
                        .collect(Collectors.joining(";")))
                .append("\"\n ");
    }

    public static StringBuilder getLinePathStyle(final double strokeWidth, final String strokeColorCode) {
        return getPathStyle(strokeWidth, strokeColorCode, "none", 1.0);
    }

    private static String getStrokeWidthToken(final double strokeWidth) {
        return String.format("stroke-width:%fpx", strokeWidth);
    }

    private static String getStrokeToken(final String colorCode) {
        return String.format("stroke:%s", colorCode);
    }

    private static String getFillToken(final String colorCode) {
        return String.format("fill:%s", colorCode);
    }

    private static String getFillOpacityToken(final double opacity) {
        return String.format("fill-opacity:%f", opacity);
    }

    public static StringBuilder getLinePathTag(final Vector2d startPoint, final Vector2d endPoint, final String style) {

        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append("<path ");
        pathBuilder.append(style);
        pathBuilder.append(" d=\"M ");

        pathBuilder.append(startPoint.getX()).append(",").append(startPoint.getY()).append(" ");

        pathBuilder.append("L ");
        pathBuilder.append(endPoint.getX()).append(",").append(endPoint.getY());

        pathBuilder.append("\" />\n");

        return pathBuilder;
    }

    public static void putInGroup(final StringBuilder groupContent) {
        groupContent.insert(0, ("<g>"))
                .append("</g>");
    }

    public static StringBuilder getFacePathTag(final List<Vector2d> points, final String style) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append("<path ");
        pathBuilder.append(style);
        pathBuilder.append(" d=\"M ");

        points.forEach(point -> pathBuilder.append(point.getX()).append(",").append(point.getY()).append(" "));

        pathBuilder.append(" z\" />\n");

        return pathBuilder;
    }
}
