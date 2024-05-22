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

package oripa.persistence.doc.loader;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import oripa.DataSet;
import oripa.OriLineProxy;
import oripa.doc.Doc;
import oripa.persistence.filetool.FileVersionError;
import oripa.persistence.filetool.WrongDataFormatException;
import oripa.resource.Version;

public class LoaderXML implements DocLoader {
	private final XPath xpath = XPathFactory.newInstance().newXPath();

	private static final String INT_NODE_NAME = "int";
	private static final String DOUBLE_NODE_NAME = "double";
	private static final String STRING_NODE_NAME = "string";

	private DataSet loadAsDataSet(final String filePath) throws IOException, WrongDataFormatException {
		DataSet dataset = new DataSet();
		try {
			var builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			var xmlDocument = builder.parse(new File(filePath));

			// parse opx version
			var datasetNode = (Node) xpath.evaluate("/java/object", xmlDocument, XPathConstants.NODE);
			dataset.setMainVersion(loadVersionFieldValue(datasetNode, "mainVersion"));
			dataset.setSubVersion(loadVersionFieldValue(datasetNode, "subVersion"));

			// get object fields
			var fieldNodes = (NodeList) xpath.evaluate("//void[@method='getField']", xmlDocument,
					XPathConstants.NODESET);

			// parse property values
			dataset.title = loadPropertyFieldValue(fieldNodes, "title");
			dataset.editorName = loadPropertyFieldValue(fieldNodes, "editorName");
			dataset.originalAuthorName = loadPropertyFieldValue(fieldNodes, "originalAuthorName");
			dataset.reference = loadPropertyFieldValue(fieldNodes, "reference");
			dataset.memo = loadPropertyFieldValue(fieldNodes, "memo");

			// parse line proxies
			dataset.lines = loadOriLineProxies(xmlDocument);
		} catch (SAXException e) {
			throw new WrongDataFormatException("The file is not in XML format.", e);
		} catch (ParserConfigurationException | XPathExpressionException e) {
			throw new RuntimeException("Bad implementation.", e);
		}
		return dataset;
	}

	private int loadVersionFieldValue(final Node datasetNode, final String fieldName)
			throws XPathExpressionException {

		return parseInt((Node) xpath
				.evaluate(createIntExpression(fieldName), datasetNode, XPathConstants.NODE));
	}

	private String loadPropertyFieldValue(final NodeList fieldNodes, final String fieldName)
			throws XPathExpressionException {

		for (int i = 0; i < fieldNodes.getLength(); i++) {
			var fieldNode = fieldNodes.item(i).cloneNode(true);

			var nodeName = parseString((Node) xpath
					.evaluate(STRING_NODE_NAME, fieldNode, XPathConstants.NODE));

			if (nodeName.equals(fieldName)) {
				return parseString(
						(Node) xpath.evaluate("void[@method='set']/" + STRING_NODE_NAME, fieldNode,
								XPathConstants.NODE));
			}
		}
		return null;
	}

	private OriLineProxy[] loadOriLineProxies(final Node rootNode) throws XPathExpressionException {
		var lineExpression = "//object[@class='oripa.OriLineProxy']";
		var lineProxyNodes = (NodeList) xpath.evaluate(lineExpression, rootNode, XPathConstants.NODESET);

		var proxies = new OriLineProxy[lineProxyNodes.getLength()];

		for (int i = 0; i < lineProxyNodes.getLength(); i++) {
			var lineProxyNode = lineProxyNodes.item(i).cloneNode(true);

			var type = parseInt((Node) xpath
					.evaluate(createIntExpression("type"), lineProxyNode, XPathConstants.NODE));

			var x0 = parseDouble((Node) xpath
					.evaluate(createDoubleExpression("x0"), lineProxyNode, XPathConstants.NODE));
			var y0 = parseDouble((Node) xpath
					.evaluate(createDoubleExpression("y0"), lineProxyNode, XPathConstants.NODE));
			var x1 = parseDouble((Node) xpath
					.evaluate(createDoubleExpression("x1"), lineProxyNode, XPathConstants.NODE));
			var y1 = parseDouble((Node) xpath
					.evaluate(createDoubleExpression("y1"), lineProxyNode, XPathConstants.NODE));

			var proxy = new OriLineProxy();
			proxy.setType(type);
			proxy.setX0(x0);
			proxy.setY0(y0);
			proxy.setX1(x1);
			proxy.setY1(y1);

			proxies[i] = proxy;
		}

		return proxies;
	}

	private String createIntExpression(final String name) {
		return createObjectPropertyExpression(name, INT_NODE_NAME);
	}

	private String createDoubleExpression(final String name) {
		return createObjectPropertyExpression(name, DOUBLE_NODE_NAME);
	}

	private String createObjectPropertyExpression(final String propertyName, final String type) {
		return "void[@property='" + propertyName + "']/" + type;
	}

	private String parseString(final Node node) {
		if (node == null) {
			return "";
		}
		return node.getTextContent();
	}

	private int parseInt(final Node node) {
		if (node == null) {
			return 0;
		}
		return Integer.parseInt(node.getTextContent());
	}

	private double parseDouble(final Node node) {
		if (node == null) {
			return 0;
		}
		return Double.parseDouble(node.getTextContent());
	}

	@Override
	public Optional<Doc> load(final String filePath) throws FileVersionError, WrongDataFormatException, IOException {

		DataSet data;

		data = loadAsDataSet(filePath);

		if (data.getMainVersion() > Version.FILE_MAJOR_VERSION) {
			throw new FileVersionError();
		}

		return Optional.of(data.recover(filePath));

	}
}
