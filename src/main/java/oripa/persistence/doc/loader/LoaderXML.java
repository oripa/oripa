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

import org.xml.sax.SAXException;

import oripa.DataSet;
import oripa.persistence.doc.Doc;
import oripa.persistence.filetool.FileVersionError;
import oripa.persistence.filetool.WrongDataFormatException;
import oripa.persistence.xml.ElementLoader;
import oripa.persistence.xml.TypedXPath;
import oripa.resource.Version;

public class LoaderXML implements DocLoader {

    private DataSet loadAsDataSet(final String filePath) throws IOException, WrongDataFormatException {
        var elementLoader = new ElementLoader(new TypedXPath());

        DataSet dataset = new DataSet();
        try {
            var builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            var xmlDocument = builder.parse(new File(filePath));

            // parse opx version
            var datasetNode = elementLoader.findDataSetNode(xmlDocument);
            dataset.setMainVersion(elementLoader.loadVersionFieldValue("mainVersion", datasetNode));
            dataset.setSubVersion(elementLoader.loadVersionFieldValue("subVersion", datasetNode));

            // get object fields
            var fieldNodes = elementLoader.findFieldNodes(xmlDocument);

            // parse property values
            dataset.title = elementLoader.loadPropertyFieldValue("title", fieldNodes);
            dataset.editorName = elementLoader.loadPropertyFieldValue("editorName", fieldNodes);
            dataset.originalAuthorName = elementLoader.loadPropertyFieldValue("originalAuthorName", fieldNodes);
            dataset.reference = elementLoader.loadPropertyFieldValue("reference", fieldNodes);
            dataset.memo = elementLoader.loadPropertyFieldValue("memo", fieldNodes);

            // parse line proxies
            dataset.lines = elementLoader.loadOriLineProxies(xmlDocument);
        } catch (SAXException e) {
            throw new WrongDataFormatException("The file is not in XML format.", e);
        } catch (NumberFormatException e) {
            throw new WrongDataFormatException("Parse error.", e);
        } catch (ParserConfigurationException | XPathExpressionException e) {
            throw new RuntimeException("Bad implementation.", e);
        }
        return dataset;
    }

    @Override
    public Optional<Doc> load(final String filePath)
            throws FileVersionError, WrongDataFormatException, IOException {

        DataSet data;

        data = loadAsDataSet(filePath);

        if (data.getMainVersion() > Version.FILE_MAJOR_VERSION) {
            throw new FileVersionError();
        }

        return Optional.of(data.recover(filePath));

    }
}
