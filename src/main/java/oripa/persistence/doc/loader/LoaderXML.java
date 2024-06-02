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
import oripa.persistence.xml.TypedXPath;
import oripa.persistence.xml.ValueNodeParser;
import oripa.resource.Version;

public class LoaderXML implements DocLoader {

	private final TypedXPath xpath = new TypedXPath(XPathFactory.newInstance().newXPath());
	final ValueNodeParser parser = new ValueNodeParser(xpath);

	private DataSet loadAsDataSet(final String filePath) throws IOException, WrongDataFormatException {
		DataSet dataset = new DataSet();
		try {
			var builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			var xmlDocument = builder.parse(new File(filePath));

			// parse opx version
			var datasetNode = xpath.evaluateAsNode("/java/object", xmlDocument);
			dataset.setMainVersion(loadVersionFieldValue("mainVersion", datasetNode));
			dataset.setSubVersion(loadVersionFieldValue("subVersion", datasetNode));

			// get object fields
			var fieldNodes = xpath.evaluateAsNodeList("//void[@method='getField']", xmlDocument);

			// parse property values
			dataset.title = loadPropertyFieldValue("title", fieldNodes);
			dataset.editorName = loadPropertyFieldValue("editorName", fieldNodes);
			dataset.originalAuthorName = loadPropertyFieldValue("originalAuthorName", fieldNodes);
			dataset.reference = loadPropertyFieldValue("reference", fieldNodes);
			dataset.memo = loadPropertyFieldValue("memo", fieldNodes);

			// parse line proxies
			dataset.lines = loadOriLineProxies(xmlDocument);
		} catch (SAXException e) {
			throw new WrongDataFormatException("The file is not in XML format.", e);
		} catch (NumberFormatException e) {
			throw new WrongDataFormatException("Parse error.", e);
		} catch (ParserConfigurationException | XPathExpressionException e) {
			throw new RuntimeException("Bad implementation.", e);
		}
		return dataset;
	}

	private int loadVersionFieldValue(final String fieldName, final Node datasetNode)
			throws XPathExpressionException {

		return parser.parseIntProperty(fieldName, datasetNode);
	}

	private String loadPropertyFieldValue(final String fieldName, final NodeList fieldNodes)
			throws XPathExpressionException {

		for (int i = 0; i < fieldNodes.getLength(); i++) {
			var fieldNode = fieldNodes.item(i).cloneNode(true);

			var nodeName = parser.parseObjectName(fieldNode);

			if (nodeName.equals(fieldName)) {
				return parser.parseStringValue(fieldNode);
			}
		}
		return null;
	}

	private OriLineProxy[] loadOriLineProxies(final Node rootNode) throws XPathExpressionException {
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
