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

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TypedXPath {
    private final XPath xpath;

    public TypedXPath() {
        this(XPathFactory.newInstance().newXPath());
    }

    public TypedXPath(final XPath xpath) {
        this.xpath = xpath;
    }

    public Node evaluateAsNode(final String expression, final Node node) throws XPathExpressionException {
        return (Node) xpath.evaluate(expression, node, XPathConstants.NODE);
    }

    public NodeList evaluateAsNodeList(final String expression, final Node node) throws XPathExpressionException {
        return (NodeList) xpath.evaluate(expression, node, XPathConstants.NODESET);
    }
}