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

package oripa.persistence.doc.exporter;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import oripa.DataSet;
import oripa.doc.Doc;

public class ExporterXML implements DocExporter {

	@Override
	public boolean export(final Doc doc, final String filePath) throws IOException {
		DataSet dataset = new DataSet(doc);

		try (var fos = new FileOutputStream(filePath);
				var bos = new BufferedOutputStream(fos);
				var enc = new XMLEncoder(bos);) {
			enc.writeObject(dataset);
		}

		return true;
	}
}
