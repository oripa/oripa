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
package oripa.persistent.svg;

/**
 * @author OUCHI Koji
 *
 */
public class SVGConstants {
	public static final int size = 1000;
	public static final String head = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>\n"
			+ "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20010904//EN\"\n"
			+ "\"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\">\n"
			+ "<svg xmlns=\"http://www.w3.org/2000/svg\"\n"
			+ " xmlns:xlink=\"http://www.w3.org/1999/xlink\" xml:space=\"preserve\"\n"
			+ " width=\"" + size + "px\" height=\"" + size + "px\"\n"
			+ " viewBox=\"0 0 " + size + " " + size + "\" >\n";
	public static final String end = "</svg>";
	public static final String gradient = " <linearGradient id=\"Gradient1\" x1=\"20%\" y1=\"0%\" x2=\"80%\" y2=\"100%\">\n"
			+ " <stop offset=\"5%\" stop-color=\"#DDEEFF\" />\n"
			+ " <stop offset=\"95%\" stop-color=\"#7788FF\" />\n"
			+ " </linearGradient>\n"
			+
			" <linearGradient id=\"Gradient2\" x1=\"20%\" y1=\"0%\" x2=\"80%\" y2=\"100%\">\n"
			+ " <stop offset=\"5%\" stop-color=\"#FFFFEE\" />\n"
			+ " <stop offset=\"95%\" stop-color=\"#DDDDDD\" />\n"
			+ " </linearGradient>\n";
	public static final String polygonStart = "<path style=\"fill:url(#Gradient1);"
			+ "stroke:#0000ff;stroke-width:2px;stroke-linecap:butt;stroke-linejoin:miter;"
			+ "stroke-opacity:1;fill-opacity:1.0\" d=\"M ";
	public static final String polygonStart2 = "<path style=\"fill:url(#Gradient2);"
			+ "stroke:#0000ff;stroke-width:2px;stroke-linecap:butt;stroke-linejoin:miter;"
			+ "stroke-opacity:1;fill-opacity:1.0\" d=\"M ";
}
