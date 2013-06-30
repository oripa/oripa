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

package oripa.resource;

import java.util.ListResourceBundle;

public class StringResource extends ListResourceBundle {
    static final Object[][] strings = { 
        { "Title", "ORIPA : Origami Pattern Editor" }, 
        { "File", "File" }, 
        { "Edit", "Edit" }, 
        { "Help", "Help" }, 
        { "New", "New" }, 
        { "Open", "Open" }, 
        { "Save", "Save" }, 
        { "SaveAs", "Save As" }, 
        { "ExportDXF", "Export (DXF)" }, 
        { "EditContour", "Edit Contour" }, 
        { "Property", "Property" }, 
        { "Exit", "Exit" }, 
        { "Undo", "Undo" }, 
        { "About", "About" }, 
        { "Direction_DirectV", "Specify two end poionts.[Ctrl] allows to pick any point on an edge." }, 
        { "Direction_OnV", "Specify two points that lie on the line" }, 
        { "Direction_Symmetric", "Input a symmetrical segment. 1st, 2nd are for target. 2nd, 3rd are for base.+[Ctrl] continues automatically." }, 
        { "Direction_TriangleSplit", "Specify 3 points to input 3 segments to incenter." }, 
        { "Direction_Bisector", "Input a bisector. Specify 3 points and a segment." }, 
        { "Direction_VerticalLine", "Input a vertical line. Specify a point and a line." }, 
        { "Direction_Mirror", "Mirror copy. Pick target segments and [Ctrl]+Click for the base segment." }, 
        { "Direction_ByValue", "Input length and angle, then specify the start point. Push [Mesure] button to get value from segment." }, 
        { "Direction_PickLength", "Mesure distance between 2 points." }, 
        { "Direction_PickAngle", "Mesure angle. Specify 3 points" }, 
        { "Direction_ChangeLineType", "Change type of a segment. Pick a segment." }, 
        { "Direction_DeleteLine", "Delete a segment. Pick a segment" }, 
        { "Direction_AddVertex", "Add vertex on a segment." }, 
        { "Direction_DeleteVertex", "Delete vertex that does not change the structure." }, 
        { "Direction_EditContour", "Edit contour. The contour must be convex." }, 
        { "DefaultFileName", "NoTitle" }, 
        { "DialogTitle_FileSave", "Save" }, 
        { "Error_FileSaveFailed", "Failed to save." }, 
        { "Error_FileLoadFailed", "Failed to load." }, 
        { "Warning_SameNameFileExist", "Same name file exists. Over write?" }, 
        { "ORIPA_File", "ORIPA file" }, 
        { "UI_InputLine", "Input Line" }, 
        { "UI_Select", "Select" }, 
        { "UI_DeleteLine", "Delete Line" }, 
        { "UI_ShowGrid", "Show Grid" }, 
        { "UI_ChangeLineType", "Change Line Type" }, 
        { "UI_AddVertex", "Add Vertex" }, 
        { "UI_DeleteVertex", "Delete Vertex" }, 
        { "UI_Mesure", "Mesure" }, 
        { "UI_Fold", "Fold..." }, 
        { "UI_GridSizeChange", "Set" }, 
        { "UI_ShowVertices", "Show Vertices" }, 
        { "UI_EditMode", "Edit Mode" }, 
        { "UI_LineInputMode", "Line Input Mode"},
        { "UI_Length", "Length"},
        { "UI_Angle", "Angle"},
        { "UI_GridDivideNum", "Div Num"},
        { "Warning_foldFail1", "Failed to fold. Try again by deleting duplicating segments?"},
        { "Warning_foldFail2", "Failed to fold. It seems the pattern has basic problems."},
        { "MENU_Disp", "Display" },
        { "MENU_ExportModelLine_DXF", "Export Model Line(DXF)" },
        { "MENU_Invert", "Invert" },
        { "MENU_SlideFaces", "Slide Faces" },
        { "Direction_Basic", "    L: Rot R:Move Wheel:Zoom " },
        { "MENU_DispType", "Drawing type" },
        { "MENU_FillColor", "Fill Color: may be wrong" },
        { "MENU_FillWhite", "Fill White: may be wrong" },
        { "MENU_FillAlpha", "Fill Transmission" },
        { "MENU_DrawLines", "Draw Lines" },
        { "ExpectedFoldedOrigami", "Expected Folded Origami" }    };
    
    @Override
    protected Object[][] getContents() {
        return strings;
    }
}
