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
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.persistence.doc.Doc;
import oripa.persistence.filetool.FileVersionError;
import oripa.persistence.filetool.LoadingAction;
import oripa.persistence.filetool.SavingAction;
import oripa.persistence.filetool.WrongDataFormatException;
import oripa.util.file.FileFactory;

/**
 *
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class FileDAOTest {
    @InjectMocks
    FileDAO<Doc> dao;

    @Mock
    FileFactory fileFactory;

    @Mock
    FileSelectionSupportSelector<Doc> selector;

    @Nested
    class TestLoad {
        @Test
        void loadSucceedsWhenFileExists()
                throws IOException, IllegalArgumentException, FileVersionError, WrongDataFormatException {

            FileSelectionSupport<Doc> support = mock();
            LoadingAction<Doc> loadingAction = mock();

            when(selector.getLoadableOf(anyString())).thenReturn(support);

            when(support.getLoadingAction()).thenReturn(loadingAction);

            when(loadingAction.load(anyString())).thenReturn(Optional.of(mock()));

            File file = mock();
            when(file.exists()).thenReturn(true);
            when(file.getCanonicalPath()).thenReturn("canonical path");

            when(fileFactory.create(anyString())).thenReturn(file);

            var docOpt = dao.load("path");

            verify(loadingAction).load("canonical path");

            assertTrue(docOpt.isPresent());
        }

        @Test
        void exceptionIsThrownWhenFileDoesNotExist()
                throws IOException {

            File file = mock();
            String canonicalPath = "canonical path";
            when(file.exists()).thenReturn(false);
            when(file.getCanonicalPath()).thenReturn(canonicalPath);

            when(fileFactory.create(anyString())).thenReturn(file);

            var exception = assertThrows(DataAccessException.class, () -> dao.load("path"));
            assertEquals(canonicalPath + " doesn't exist.", exception.getMessage());
        }

        @MethodSource("createExceptions")
        @ParameterizedTest
        void exceptionIsThrownWhenLoadingThrowsException(final Throwable expectedException)
                throws IOException, IllegalArgumentException, FileVersionError, WrongDataFormatException {

            FileSelectionSupport<Doc> support = mock();
            LoadingAction<Doc> loadingAction = mock();

            when(selector.getLoadableOf(anyString())).thenReturn(support);

            when(support.getLoadingAction()).thenReturn(loadingAction);

            when(loadingAction.load(anyString())).thenThrow(expectedException);

            File file = mock();
            when(file.exists()).thenReturn(true);
            when(file.getCanonicalPath()).thenReturn("canonical path");

            when(fileFactory.create(anyString())).thenReturn(file);

            var actual = assertThrows(DataAccessException.class, () -> dao.load("path"));
            assertEquals(expectedException, actual.getCause());
        }

        static List<Throwable> createExceptions() {
            return List.of(
                    new WrongDataFormatException("arg"),
                    new FileVersionError(),
                    new IOException());
        }

    }

    @Nested
    class TestSave_NoType {
        @Test
        void saveSucceedsWhenFileTypeIsNotGiven() throws IllegalArgumentException, IOException {

            Doc doc = mock();

            FileSelectionSupport<Doc> support = mock();
            SavingAction<Doc> savingAction = mock();

            when(selector.getSavableOf(anyString())).thenReturn(support);

            when(support.getSavingAction()).thenReturn(savingAction);

            when(savingAction.save(eq(doc), anyString())).thenReturn(true);

            File file = mock();
            when(file.getCanonicalPath()).thenReturn("canonical path");

            when(fileFactory.create(anyString())).thenReturn(file);

            dao.save(doc, "canonical path");

            verify(savingAction).save(doc, "canonical path");
        }

        @MethodSource("createExceptions")
        @ParameterizedTest
        void exceptionIsThrownWhenSavingThrowsException(final Throwable expectedException)
                throws IllegalArgumentException, IOException {

            Doc doc = mock();

            FileSelectionSupport<Doc> support = mock();
            SavingAction<Doc> savingAction = mock();

            when(selector.getSavableOf(anyString())).thenReturn(support);

            when(support.getSavingAction()).thenReturn(savingAction);

            when(savingAction.save(eq(doc), anyString())).thenThrow(expectedException);

            File file = mock();
            when(file.getCanonicalPath()).thenReturn("canonical path");

            when(fileFactory.create(anyString())).thenReturn(file);

            var actual = assertThrows(DataAccessException.class, () -> dao.save(doc, "canonical path"));
            assertEquals(expectedException, actual.getCause());
        }

        static List<Throwable> createExceptions() {
            return List.of(
                    new IOException());
        }

    }

    @Nested
    class TestSave_WithType {
        @Test
        void saveSucceedsWhenFileTypeIsGiven() throws IllegalArgumentException, IOException {

            Doc doc = mock();
            FileType<Doc> fileType = mock();

            FileSelectionSupport<Doc> support = mock();
            SavingAction<Doc> savingAction = mock();

            when(selector.getSavablesOf(anyList())).thenReturn(List.of(support));

            when(support.getSavingAction()).thenReturn(savingAction);

            when(savingAction.save(eq(doc), anyString())).thenReturn(true);

            File file = mock();
            when(file.getCanonicalPath()).thenReturn("canonical path");

            when(fileFactory.create(anyString())).thenReturn(file);

            dao.save(doc, "canonical path", fileType);

            verify(savingAction).save(doc, "canonical path");
        }

        @MethodSource("createExceptions")
        @ParameterizedTest
        void exceptionIsThrownWhenSavingThrowsException(final Throwable expectedException)
                throws IllegalArgumentException, IOException {

            Doc doc = mock();
            FileType<Doc> fileType = mock();

            FileSelectionSupport<Doc> support = mock();
            SavingAction<Doc> savingAction = mock();

            when(selector.getSavablesOf(anyList())).thenReturn(List.of(support));

            when(support.getSavingAction()).thenReturn(savingAction);

            when(savingAction.save(eq(doc), anyString())).thenThrow(expectedException);

            File file = mock();
            when(file.getCanonicalPath()).thenReturn("canonical path");

            when(fileFactory.create(anyString())).thenReturn(file);

            var actual = assertThrows(DataAccessException.class, () -> dao.save(doc, "canonical path", fileType));
            assertEquals(expectedException, actual.getCause());

        }

        static List<Throwable> createExceptions() {
            return List.of(
                    new IOException());
        }

    }

    @Nested
    class TestSetConfigToSavingAction {
        @Test
        void supportMethodIsCalledWhenSupportExists() {

            FileType<Doc> fileType = mock();
            FileSelectionSupport<Doc> support = mock();

            when(selector.getFileSelectionSupport(fileType)).thenReturn(Optional.of(support));

            dao.setConfigToSavingAction(fileType, mock());

            verify(support).setConfigToSavingAction(any());
        }

        @Test
        void supportMethodIsNotCalledWhenSupportDoesNotExist() {
            FileType<Doc> fileType = mock();
            FileSelectionSupport<Doc> support = mock();

            when(selector.getFileSelectionSupport(fileType)).thenReturn(Optional.empty());

            dao.setConfigToSavingAction(fileType, mock());

            verify(support, never()).setConfigToSavingAction(any());
        }

    }

}
