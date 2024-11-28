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
package oripa.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import oripa.application.FileAccessService;
import oripa.domain.fold.halfedge.OrigamiModel;
import oripa.persistence.dao.FileDAO;
import oripa.persistence.dao.FileSelectionSupportSelector;
import oripa.persistence.doc.Doc;
import oripa.persistence.doc.DocFileSelectionSupportSelectorFactory;
import oripa.persistence.entity.OrigamiModelFileSelectionSupportSelectorFactory;
import oripa.util.file.FileFactory;

/**
 * @author OUCHI Koji
 *
 */
public class FileAccessServiceModule extends AbstractModule {

	@Override
	protected void configure() {

	}

	@Provides
	FileSelectionSupportSelector<OrigamiModel> createOrigamiModelSelector(
			final OrigamiModelFileSelectionSupportSelectorFactory selectorFactory,
			final FileFactory fileFactory) {
		return selectorFactory.create(fileFactory);
	}

	@Provides
	FileDAO<OrigamiModel> createOrigamiModelFileDAO(
			final FileSelectionSupportSelector<OrigamiModel> selector,
			final FileFactory fileFactory) {
		return new FileDAO<OrigamiModel>(selector, fileFactory);
	}

	@Provides
	FileAccessService<OrigamiModel> createOrigamiModelService(final FileDAO<OrigamiModel> dao) {
		return new FileAccessService<OrigamiModel>(dao);
	}

	@Provides
	FileSelectionSupportSelector<Doc> createDocSelector(
			final DocFileSelectionSupportSelectorFactory selectorFactory,
			final FileFactory fileFactory) {
		return selectorFactory.create(fileFactory);
	}

	@Provides
	FileDAO<Doc> createDocFileDAO(
			final FileSelectionSupportSelector<Doc> selector,
			final FileFactory fileFactory) {
		return new FileDAO<Doc>(selector, fileFactory);
	}

	@Provides
	FileAccessService<Doc> createDocService(final FileDAO<Doc> dao) {
		return new FileAccessService<Doc>(dao);
	}

}
