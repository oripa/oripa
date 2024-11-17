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
package oripa.persistence.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.persistence.doc.Doc;
import oripa.persistence.filetool.FileAccessSupport;
import oripa.persistence.filetool.FileAccessSupportFactory;
import oripa.persistence.filetool.FileTypeProperty;
import oripa.persistence.filetool.MultiTypeAcceptableFileLoadingSupport;
import oripa.util.file.FileFactory;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class FileSelectionSupportSelectorTest {

	@InjectMocks
	FileSelectionSupportSelector<Doc> selector;

	@Mock
	SortedMap<FileTypeProperty<Doc>, FileSelectionSupport<Doc>> supports;

	@Mock
	FileSelectionSupportFactory selectionSupportFactory;

	@Mock
	FileAccessSupportFactory accessSupportFactory;

	@Mock
	FileFactory fileFactory;

	@Nested
	class TestGetLoadablesWithMultiType {
		@Mock
		FileSelectionSupport<Doc> support;

		@Mock
		FileTypeProperty<Doc> fileType;

		@Captor
		ArgumentCaptor<FileAccessSupport<Doc>> multi;

		@SuppressWarnings("unchecked")
		@Test
		void resultContainsMultiTypeSupport() {

			FileAccessSupport<Doc> access = mock();

			when(support.getFileAccessSupport()).thenReturn(access);
			when(support.isLoadable()).thenReturn(true);

			when(supports.values()).thenReturn(List.of(support));

			MultiTypeAcceptableFileLoadingSupport<Doc> multiAccess = mock();
			when(accessSupportFactory.createMultiTypeAcceptableLoading(eq(List.of(access)), any()))
					.thenReturn(multiAccess);

			FileSelectionSupport<Doc> forMulti = mock();
			when(forMulti.compareTo(support)).thenReturn(-1);
			when(selectionSupportFactory.create(any(FileAccessSupport.class))).thenReturn(forMulti);

			var loadables = selector.getLoadablesWithMultiType();

			assertEquals(2, loadables.size());

			verify(selectionSupportFactory).create(multi.capture());
			assertTrue(multi.getValue() instanceof MultiTypeAcceptableFileLoadingSupport<Doc>);
		}

		@Test
		void EmptyIfNoLoadableSupport() {

			when(supports.values()).thenReturn(List.of(support));

			var loadables = selector.getLoadablesWithMultiType();

			assertEquals(0, loadables.size());
		}
	}

	@Nested
	class TestGetLoadables {
		@Mock
		FileSelectionSupport<Doc> support;

		@Test
		void returnsGivenSupport() {

			when(support.isLoadable()).thenReturn(true);

			when(supports.values()).thenReturn(List.of(support));

			var loadables = selector.getLoadables();

			assertEquals(1, loadables.size());

			assertSame(support, loadables.get(0));
		}

		@Test
		void emptyIfNoLoadableSupport() {

			when(supports.values()).thenReturn(List.of(support));

			var loadables = selector.getLoadables();

			assertEquals(0, loadables.size());
		}
	}

	@Nested
	class TestGetLoadableOf {
		@Mock
		FileSelectionSupport<Doc> support;

		@Mock
		FileTypeProperty<Doc> fileType;

		@Test
		void returnsGivenSupportWhenPathIsCorrect() throws IOException {

			when(support.isLoadable()).thenReturn(true);
			when(support.extensionsMatch(eq("canonical_path.ext"))).thenReturn(true);

			when(supports.values()).thenReturn(List.of(support));

			File file = mock();
			when(file.isDirectory()).thenReturn(false);
			when(file.getCanonicalPath()).thenReturn("canonical_path.ext");
			when(fileFactory.create(anyString())).thenReturn(file);

			var loadable = selector.getLoadableOf("file.ext");

			assertSame(support, loadable);
		}

		@Test
		void exceptionIsThrownIfPathIsNull() {
			var exception = assertThrows(IllegalArgumentException.class, () -> selector.getLoadableOf(null));
			assertEquals("Wrong path (null)", exception.getMessage());
		}

		@Test
		void exceptionIsThrownIfPathIsDirectory() {

			File file = mock();
			when(file.isDirectory()).thenReturn(true);

			when(fileFactory.create(anyString())).thenReturn(file);

			var exception = assertThrows(IllegalArgumentException.class, () -> selector.getLoadableOf("bad path"));
			assertEquals("The path is for directory.", exception.getMessage());
		}

	}

	@Nested
	class TestGetSavables {
		@Mock
		FileSelectionSupport<Doc> support;

		@Test
		void returnsGivenSupport() {

			when(support.isSavable()).thenReturn(true);

			when(supports.values()).thenReturn(List.of(support));

			var savables = selector.getSavables();

			assertEquals(1, savables.size());

			assertSame(support, savables.get(0));
		}

		@Test
		void emptyIfNoSavableSupport() {

			when(support.isSavable()).thenReturn(false);
			when(supports.values()).thenReturn(List.of(support));

			var savables = selector.getSavables();

			assertEquals(0, savables.size());
		}
	}

	@Nested
	class TestGetSavablesOf {
		@Mock
		FileSelectionSupport<Doc> support;

		@Test
		void returnsGivenSupport() {

			FileType<Doc> type = mock();

			when(support.isSavable()).thenReturn(true);
			when(support.getTargetType()).thenReturn(type);

			Collection<FileType<Doc>> fileTypes = List.of(type);

			when(supports.values()).thenReturn(List.of(support));

			var savables = selector.getSavablesOf(fileTypes);

			assertEquals(1, savables.size());

			assertSame(support, savables.get(0));
		}

		@Test
		void emptyIfNoTypeMatches() {

			FileType<Doc> type = mock();

			when(support.isSavable()).thenReturn(true);
			when(support.getTargetType()).thenReturn(type);

			when(supports.values()).thenReturn(List.of(support));

			// type different from the supported one
			FileType<Doc> givenType = mock();
			Collection<FileType<Doc>> fileTypes = List.of(givenType);

			var savables = selector.getSavablesOf(fileTypes);

			assertEquals(0, savables.size());
		}
	}

	@Nested
	class TestGetSavableOf {
		@Mock
		FileSelectionSupport<Doc> support;

		@Mock
		FileTypeProperty<Doc> fileType;

		@Test
		void returnsGivenSupportWhenPathIsCorrect() throws IOException {

			when(support.isSavable()).thenReturn(true);
			when(support.extensionsMatch(eq("file.ext"))).thenReturn(true);

			when(supports.values()).thenReturn(List.of(support));

			var savable = selector.getSavableOf("file.ext");

			assertSame(support, savable);
		}

		@Test
		void exceptionIsThrownWhenNoExtensionMatches() throws IOException {

			when(support.isSavable()).thenReturn(true);
			when(support.extensionsMatch(eq("file.ext"))).thenReturn(false);

			when(supports.values()).thenReturn(List.of(support));

			var exception = assertThrows(IllegalArgumentException.class, () -> selector.getSavableOf("file.ext"));
			assertEquals("The file type guessed from the extension is not supported.", exception.getMessage());
		}

		@Test
		void exceptionIsThrownWhenPathIsNull() {
			var exception = assertThrows(IllegalArgumentException.class, () -> selector.getSavableOf(null));
			assertEquals("path should not be null.", exception.getMessage());
		}

	}

}
