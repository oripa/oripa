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
package oripa.application;

import java.util.Collection;
import java.util.List;

import oripa.persistence.dao.FileSelectionSupport;
import oripa.persistence.dao.FileSelectionSupportSelector;
import oripa.persistence.dao.FileType;

/**
 * @author OUCHI Koji
 *
 */
public class FileSelectionService<Data> {
    private final FileSelectionSupportSelector<Data> selector;

    /**
     * The descriptions among the {@link FileSelectionSupport}s in the
     * {@code selector} should be unique.
     *
     * @param selector
     */
    public FileSelectionService(final FileSelectionSupportSelector<Data> selector) {
        this.selector = selector;
    }

    public List<FileSelectionSupport<Data>> getSavableSupports() {
        return selector.getSavables();
    }

    public List<FileSelectionSupport<Data>> getSavableSupportsOf(final Collection<FileType<Data>> types) {
        return selector.getSavablesOf(types);
    }

    public List<FileSelectionSupport<Data>> getLoadableSupportsWithMultiType() {
        return selector.getLoadablesWithMultiType();
    }

    public FileType<Data> getSavableTypeByDescription(final String description) {
        return selector.getSavables().stream()
                .filter(support -> support.getDescription().equals(description))
                .findFirst()
                .get()
                .getTargetType();
    }

}
