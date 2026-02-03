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
package oripa.gui.presenter.main.logic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import oripa.application.main.DocFileAccess;
import oripa.file.FileHistory;
import oripa.gui.presenter.file.FileSelectionResult;
import oripa.gui.presenter.main.DocFileSelectionPresenter;
import oripa.gui.presenter.main.MainDialogPresenterFactory;
import oripa.gui.view.main.MainFrameView;
import oripa.persistence.dao.DataAccessException;
import oripa.persistence.dao.FileType;
import oripa.persistence.doc.Doc;
import oripa.project.Project;
import oripa.util.file.FileFactory;

/**
 * @author OUCHI Koji
 *
 */
@ExtendWith(MockitoExtension.class)
class MainFrameFilePresentationLogicTest {

    @InjectMocks
    MainFrameFilePresentationLogic presentationLogic;

    @Mock
    MainFrameView view;

    @Mock
    FileAccessPresentationLogic fileAccessPresentationLogic;

    @Mock
    DocFileAccess docFileAccess;

    @Mock
    Project project;

    @Mock
    FileHistory fileHistory;

    @Mock
    FileFactory fileFactory;

    @Mock
    MainDialogPresenterFactory dialogPresenterFactory;

    @Nested
    class TestModifySavingActions {
        @Captor
        ArgumentCaptor<Supplier<Object>> foldConfigCaptor;

        @Test
        void saveConfigurationOfFOLDShouldBeDone() {
            // execute
            presentationLogic.modifySavingActions();

            verify(docFileAccess).setupFOLDConfigForSaving();
        }
    }

    @Nested
    class TestSsveFileToCurrentPath {
        @Test
        void succeeds() {
            var path = "path";

            when(project.getDataFilePath()).thenReturn(path);
            FileType<Doc> type = mock();

            // execute
            presentationLogic.saveFileToCurrentPath(type);

            verify(fileAccessPresentationLogic).saveFile(path, type);
        }
    }

    @Nested
    class TestSaveFileUsingGUI {
        @SuppressWarnings("unchecked")
        @Test
        void succeedsWhenFileIsSelected() {

            when(fileHistory.getLastDirectory()).thenReturn("directory");

            when(project.getDataFileName()).thenReturn(Optional.of("file name"));

            File defaultFile = mock();
            when(defaultFile.getPath()).thenReturn("path");
            when(fileFactory.create("directory", "file name")).thenReturn(defaultFile);

            DocFileSelectionPresenter selectionPresenter = mock();
            String selectedPath = "selected path";
            FileSelectionResult<Doc> selectionResult = FileSelectionResult
                    .createSelectedForSave(
                            selectedPath,
                            mock());
            when(selectionPresenter.saveUsingGUI("path")).thenReturn(selectionResult);
            when(dialogPresenterFactory.createDocFileSelectionPresenter(eq(view), any()))
                    .thenReturn(selectionPresenter);

            when(fileAccessPresentationLogic.saveFile(eq(selectedPath), any())).thenReturn(selectedPath);

            // execute

            var returnedPath = presentationLogic.saveFileUsingGUI();

            assertEquals(selectedPath, returnedPath);
        }

        @SuppressWarnings("unchecked")
        @Test
        void newFileOpxWhenProjectIsNotLoaded() {

            when(fileHistory.getLastDirectory()).thenReturn("directory");

            when(project.getDataFileName()).thenReturn(Optional.empty());

            File defaultFile = mock();
            when(defaultFile.getPath()).thenReturn("path");
            when(fileFactory.create("directory", "newFile.opx")).thenReturn(defaultFile);

            DocFileSelectionPresenter selectionPresenter = mock();
            String selectedPath = "selected path";
            FileSelectionResult<Doc> selectionResult = FileSelectionResult
                    .createSelectedForSave(
                            selectedPath,
                            mock());
            when(selectionPresenter.saveUsingGUI("path")).thenReturn(selectionResult);
            when(dialogPresenterFactory.createDocFileSelectionPresenter(eq(view), any()))
                    .thenReturn(selectionPresenter);

            when(fileAccessPresentationLogic.saveFile(eq(selectedPath), any())).thenReturn(selectedPath);

            // execute

            var returnedPath = presentationLogic.saveFileUsingGUI();

            assertEquals(selectedPath, returnedPath);
        }

        @SuppressWarnings("unchecked")
        @Test
        void noChangesWhenCanceled() {

            when(fileHistory.getLastDirectory()).thenReturn("directory");

            when(project.getDataFileName()).thenReturn(Optional.of("file name"));
            when(project.getDataFilePath()).thenReturn("project path");

            File defaultFile = mock();
            when(defaultFile.getPath()).thenReturn("path");
            when(fileFactory.create("directory", "file name")).thenReturn(defaultFile);

            DocFileSelectionPresenter selectionPresenter = mock();
            FileSelectionResult<Doc> selectionResult = FileSelectionResult
                    .createCanceled();
            when(selectionPresenter.saveUsingGUI("path")).thenReturn(selectionResult);
            when(dialogPresenterFactory.createDocFileSelectionPresenter(eq(view), any()))
                    .thenReturn(selectionPresenter);

            // execute

            var selectedPath = presentationLogic.saveFileUsingGUI();

            verify(docFileAccess, never()).saveFile(any(), anyString(), any());

            assertEquals("project path", selectedPath);
        }

        @SuppressWarnings("unchecked")
        @Test
        void noChangesWhenDataAccessErrors() {

            when(fileHistory.getLastDirectory()).thenReturn("directory");

            String projectPath = "project path";
            when(project.getDataFileName()).thenReturn(Optional.of("file name"));
            when(project.getDataFilePath()).thenReturn(projectPath);

            File defaultFile = mock();
            when(defaultFile.getPath()).thenReturn("path");
            when(fileFactory.create("directory", "file name")).thenReturn(defaultFile);

            DocFileSelectionPresenter selectionPresenter = mock();
            FileSelectionResult<Doc> selectionResult = FileSelectionResult
                    .createSelectedForSave(
                            "selected path",
                            mock());
            when(selectionPresenter.saveUsingGUI("path")).thenReturn(selectionResult);
            when(dialogPresenterFactory.createDocFileSelectionPresenter(eq(view), any()))
                    .thenReturn(selectionPresenter);

            doThrow(DataAccessException.class).when(fileAccessPresentationLogic).saveFile(anyString(), any());

            // execute

            var returnedPath = presentationLogic.saveFileUsingGUI();

            assertEquals(projectPath, returnedPath);
        }

    }

    @Nested
    class TestExportFileUsingGUIWithModelCheck {
        @Test
        void succeedsWhenCheckIsPassed() throws IOException {
            when(fileHistory.getLastDirectory()).thenReturn("directory");

            DocFileSelectionPresenter selectionPresenter = mock();
            String selectedPath = "selected path";
            FileSelectionResult<Doc> selectionResult = FileSelectionResult
                    .createSelectedForSave(
                            selectedPath,
                            mock());

            when(selectionPresenter.saveFileWithModelCheck(
                    anyString(), any(), any()))
                            .thenReturn(selectionResult);
            when(dialogPresenterFactory.createDocFileSelectionPresenter(eq(view), any()))
                    .thenReturn(selectionPresenter);

            FileType<Doc> type = mock();

            // execute

            presentationLogic.exportFileUsingGUIWithModelCheck(type);

            verify(fileAccessPresentationLogic).saveFile(anyString(), eq(type));
        }

        @Test
        void noCHangessWhenCanceledByUserOrCheck() throws IOException {
            when(fileHistory.getLastDirectory()).thenReturn("directory");

            DocFileSelectionPresenter selectionPresenter = mock();
            FileSelectionResult<Doc> selectionResult = FileSelectionResult
                    .createCanceled();

            when(selectionPresenter.saveFileWithModelCheck(
                    anyString(), any(), any()))
                            .thenReturn(selectionResult);
            when(dialogPresenterFactory.createDocFileSelectionPresenter(eq(view), any()))
                    .thenReturn(selectionPresenter);

            FileType<Doc> type = mock();

            // execute

            presentationLogic.exportFileUsingGUIWithModelCheck(type);

            verify(fileAccessPresentationLogic, never()).saveFile(anyString(), eq(type));
        }

    }

    @Nested
    class TestLoadFile {

        @Test
        void succeedsWhenFileIsLoaded() {

            String path = "path";

            when(fileAccessPresentationLogic.loadFile(eq(path))).thenReturn(path);

            // execute

            var loadedPath = presentationLogic.loadFile(path);

            assertEquals(path, loadedPath);
        }

        @Test
        void noChangesWhenFileIsNotLoaded() {

            String path = "path";
            // couldn't load
            when(fileAccessPresentationLogic.loadFile(eq(path))).thenReturn(null);

            // execute

            var loadedPath = presentationLogic.loadFile(path);

            assertNull(loadedPath);

        }

    }

    @Nested
    class TestLoadFileUsingGUI {

        @Test
        void succeedsWhenFileIsLoaded() {

            var lastPath = "last path";
            when(fileHistory.getLastPath()).thenReturn(lastPath);

            var selectedPath = "path";

            FileSelectionResult<Doc> selection = FileSelectionResult.createSelectedForLoad(selectedPath);
            DocFileSelectionPresenter selectionPresenter = mock();
            when(selectionPresenter.loadUsingGUI(lastPath)).thenReturn(selection);

            when(dialogPresenterFactory.createDocFileSelectionPresenter(eq(view), any()))
                    .thenReturn(selectionPresenter);

            // execute

            presentationLogic.loadFileUsingGUI();

            verify(fileAccessPresentationLogic).loadFile(eq(selectedPath));

        }
    }

    @Nested
    class TestImport {

        @Test
        void succeedsWhenFileIsLoaded() {

            var lastPath = "last path";
            when(fileHistory.getLastPath()).thenReturn(lastPath);

            var selectedPath = "path";

            FileSelectionResult<Doc> selection = FileSelectionResult.createSelectedForLoad(selectedPath);
            DocFileSelectionPresenter selectionPresenter = mock();
            when(selectionPresenter.loadUsingGUI(lastPath)).thenReturn(selection);

            when(dialogPresenterFactory.createDocFileSelectionPresenter(eq(view), any()))
                    .thenReturn(selectionPresenter);

            // execute
            presentationLogic.importFileUsingGUI();

            verify(fileAccessPresentationLogic).importFile(eq(selectedPath));
        }
    }

}
