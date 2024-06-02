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

public class ValueNodeParser {
	private final TypedXPath xpath;
	private static final String INT_NODE_NAME = "int";
	private static final String DOUBLE_NODE_NAME = "double";
	private static final String STRING_NODE_NAME = "string";

	public ValueNodeParser(final TypedXPath xpath) {
		this.xpath = xpath;
	}

	private String createIntExpression(final String propertyName) {
		return createObjectPropertyExpression(propertyName, INT_NODE_NAME);
	}

	private String createDoubleExpression(final String propertyName) {
		return createObjectPropertyExpression(propertyName, DOUBLE_NODE_NAME);
	}

	private String createObjectPropertyExpression(final String propertyName, final String type) {
		return "void[@property='" + propertyName + "']/" + type;
	}

	private String parseString(final String expression, final Node ancestorNode) throws XPathExpressionException {
		var node = xpath.evaluateAsNode(expression, ancestorNode);
		if (node == null) {
			return "";
		}
		return node.getTextContent();
	}

	public String parseObjectName(final Node objectNode) throws XPathExpressionException {
		return parseString(STRING_NODE_NAME, objectNode);
	}

	public String parseStringValue(final Node objectNode) throws XPathExpressionException {
		return parseString("void[@method='set']/" + STRING_NODE_NAME, objectNode);
	}

	public int parseIntProperty(final String propertyName, final Node parentNode) throws XPathExpressionException {
		var node = xpath.evaluateAsNode(createIntExpression(propertyName), parentNode);
		if (node == null) {
			return 0;
		}
		return Integer.parseInt(node.getTextContent());
	}

	public double parseDoubleProperty(final String propertyName, final Node parentNode)
			throws XPathExpressionException {
		var node = xpath.evaluateAsNode(createDoubleExpression(propertyName), parentNode);
		if (node == null) {
			return 0;
		}
		return Double.parseDouble(node.getTextContent());
	}

}