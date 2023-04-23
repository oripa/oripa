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

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oripa.DataSet;
import oripa.doc.Doc;
import oripa.persistence.filetool.FileVersionError;
import oripa.persistence.filetool.WrongDataFormatException;
import oripa.resource.Version;

public class LoaderXML implements DocLoader {
	private static final Logger logger = LoggerFactory.getLogger(LoaderXML.class);

	public DataSet loadAsDataSet(final String filePath) throws IOException {
		DataSet dataset;
		try (var fis = new FileInputStream(filePath);
				var bis = new BufferedInputStream(fis);
				var dec = new XMLDecoder(bis)) {
			dataset = (DataSet) dec.readObject();
		}

		return dataset;
	}

	@Override
	public Doc load(final String filePath) throws FileVersionError, WrongDataFormatException, IOException {

		DataSet data;

		try {
			data = loadAsDataSet(filePath);
		} catch (RuntimeException e) {
			throw new WrongDataFormatException("failed to load " + filePath);
		}

		if (data.getMainVersion() > Version.FILE_MAJOR_VERSION) {
			throw new FileVersionError();
		}

		return data.recover(filePath);

	}
}
