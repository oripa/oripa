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

package oripa.persistent.doc.loader;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import oripa.DataSet;
import oripa.persistent.doc.Doc;
import oripa.persistent.doc.Loader;
import oripa.persistent.filetool.FileVersionError;
import oripa.resource.Version;

public class LoaderXML implements Loader<Doc> {

	public DataSet loadAsDataSet(String filePath) {
		DataSet dataset;
		try {
			XMLDecoder dec = new XMLDecoder(new BufferedInputStream(
					new FileInputStream(filePath)));
			dataset = (DataSet) dec.readObject();
			dec.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		return dataset;
	}

	@Override
	public Doc load(String filePath) throws FileVersionError {

		DataSet data = loadAsDataSet(filePath);

		if (data.getMainVersion() > Version.FILE_MAJOR_VERSION) {
			throw new FileVersionError();
		}

		return data.recover(filePath);

	}
}
