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
package oripa.persistence.xml;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import oripa.OriLineProxy;

/**
 * @author OUCHI Koji
 *
 */
public class ElementLoader {
    private final TypedXPath xpath;
    private final ValueNodeParser parser;

    public ElementLoader(final TypedXPath xpath) {
        this.xpath = xpath;
        parser = new ValueNodeParser(xpath);
    }

    public Node findDataSetNode(final Node rootNode) throws XPathExpressionException {
        return xpath.evaluateAsNode("/java/object", rootNode);
    }

    public NodeList findFieldNodes(final Node rootNode) throws XPathExpressionException {
        return xpath.evaluateAsNodeList("//void[@method='getField']", rootNode);
    }

    public int loadVersionFieldValue(final String fieldName, final Node datasetNode)
            throws XPathExpressionException {

        return parser.parseIntProperty(fieldName, datasetNode);
    }

    public String loadPropertyFieldValue(final String fieldName, final NodeList fieldNodes)
            throws XPathExpressionException {

        for (int i = 0; i < fieldNodes.getLength(); i++) {
            var fieldNode = fieldNodes.item(i).cloneNode(true);

            var nodeName = parser.parseObjectName(fieldNode);

            if (nodeName.equals(fieldName)) {
                return parser.parseStringValue(fieldNode);
            }
        }
        // each field can be null.
        return null;
    }

    public OriLineProxy[] loadOriLineProxies(final Node rootNode) throws XPathExpressionException {
        var lineExpression = "//object[@class='oripa.OriLineProxy']";
        var lineProxyNodes = xpath.evaluateAsNodeList(lineExpression, rootNode);

        var proxies = new OriLineProxy[lineProxyNodes.getLength()];

        for (int i = 0; i < lineProxyNodes.getLength(); i++) {
            var lineProxyNode = lineProxyNodes.item(i).cloneNode(true);

            var proxy = new OriLineProxy();
            proxy.setType(parser.parseIntProperty("type", lineProxyNode));
            proxy.setX0(parser.parseDoubleProperty("x0", lineProxyNode));
            proxy.setY0(parser.parseDoubleProperty("y0", lineProxyNode));
            proxy.setX1(parser.parseDoubleProperty("x1", lineProxyNode));
            proxy.setY1(parser.parseDoubleProperty("y1", lineProxyNode));

            proxies[i] = proxy;
        }

        return proxies;
    }

}
