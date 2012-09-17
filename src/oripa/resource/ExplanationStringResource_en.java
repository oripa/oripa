package oripa.resource;

import java.util.ListResourceBundle;

public class ExplanationStringResource_en extends ListResourceBundle{
    static final Object[][] strings = { 
    { StringID.Command.DIRECT_V_ID, "Specify two end poionts.[Ctrl] allows to pick any point on an edge." }, 
    { StringID.Command.ON_V_ID, "Specify two points that lie on the line" }, 
    { StringID.Command.SYMMETRIC_ID, "Input a symmetrical segment. 1st, 2nd are for target. 2nd, 3rd are for base.+[Ctrl] continues automatically." }, 
    { StringID.Command.TRIANGLE_ID, "Specify 3 points to input 3 segments to incenter." }, 
    { StringID.Command.BISECTOR_ID, "Input a bisector. Specify 3 points and a segment." }, 
    { StringID.Command.VERTICAL_ID, "Input a vertical line. Specify a point and a line." }, 
    { StringID.Command.MIRROR_ID, "Mirror copy. Pick target segments and [Ctrl]+Click for the base segment." }, 
    { StringID.Command.BY_VALUE_ID, "Input length and angle, then specify the start point. Push [Mesure] button to get value from segment." }, 
    { StringID.Command.PICK_LENGTH_ID, "Mesure distance between 2 points." }, 
    { StringID.Command.PICK_ANGLE_ID, "Mesure angle. Specify 3 points" }, 
    { StringID.Command.CHANGE_LINE_TYPE_ID, "Change type of a segment. Pick a segment." }, 
    { StringID.Command.DELETE_LINE_ID, "Delete a segment. Pick a segment" }, 
    { StringID.Command.ADD_VERTEX_ID, "Add vertex on a segment." }, 
    { StringID.Command.DELETE_VERTEX_ID, "Delete vertex that does not change the structure." }, 
    { StringID.Command.CONTOUR_ID, "Edit contour. The contour must be convex." }, 
    { StringID.Command.PERPENDICULAR_BISECTOR_ID, "Input Perpendicular Bisector of two vertices. Select two vertices by left click." }, 
    { StringID.Command.SELECT_ID, "Select/UnSelect Lines by Left Click or Left Drag"},
    { StringID.Command.COPY_PASTE_ID, "Left Click for Paste. [Ctrl] allows you to change the origin of pasting."},
    };
    
    @Override
    protected Object[][] getContents() {
        return strings;
    }

}
