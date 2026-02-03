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
package oripa.persistence.filetool;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author OUCHI Koji
 *
 */
public class MultiTypeProperty<Data> implements FileTypePropertyWithAccessor<Data> {

    private final Collection<FileTypeProperty<Data>> properties;

    public MultiTypeProperty(final Collection<FileTypeProperty<Data>> properties) {
        this.properties = properties;
    }

    @Override
    public Integer getOrder() {
        return -1;
    }

    @Override
    public String getKeyText() {
        return String.join("+", getExtensions());
    }

    @Override
    public String[] getExtensions() {
        return properties.stream()
                .flatMap(p -> Arrays.asList(p.getExtensions()).stream())
                .toList().toArray(new String[0]);
    }

    @Override
    public Loader<Data> getLoader() {
        return null;
    }

    @Override
    public Exporter<Data> getExporter() {
        return null;
    }
}
