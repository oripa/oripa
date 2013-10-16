package oripa.resource;

import java.util.ListResourceBundle;

public class ExplanationStringResource_en extends ListResourceBundle{
    
	private static final String SELECT_LINE_TEXT = "Select/UnSelect Lines by Left Click or Left Drag";
	private static final String COPY_PASTE_TEXT = "Left Click for Paste. [Ctrl] allows you to change the origin of pasting.";

	static final Object[][] strings = { 
    { StringID.DIRECT_V_ID, "Specify two end points.[Ctrl] allows to pick any point on an edge." }, 

    { StringID.ON_V_ID, "Specify two points that lie on the line" }, 
    
    { StringID.SYMMETRIC_ID, "Input a symmetrical segment. 1st, 2nd are for target. 2nd, 3rd are for base.+[Ctrl] continues automatically." }, 
    
    { StringID.TRIANGLE_ID, "Specify 3 points to input 3 segments to incenter." }, 
    
    { StringID.BISECTOR_ID, "Input a bisector. Specify 3 points and a segment." }, 
    
    { StringID.VERTICAL_ID, "Input a vertical line. Specify a point and a line." }, 
    
    { StringID.MIRROR_ID, "Mirror copy. Pick target segments and [Ctrl]+Click for the base segment." }, 
    
    { StringID.BY_VALUE_ID, "Input length and angle, then specify the start point. Push [Mesure] button to get value from segment." }, 
    
    { StringID.PICK_LENGTH_ID, "Mesure distance between 2 points." }, 
    
    { StringID.PICK_ANGLE_ID, "Mesure angle. Specify 3 points" }, 
    
    { StringID.CHANGE_LINE_TYPE_ID, "Change type of a segment. Pick a segment." }, 
    
    { StringID.DELETE_LINE_ID, "Delete a segment. Pick a segment" }, 
    
    { StringID.ADD_VERTEX_ID, "Add vertex on a segment." }, 
    
    { StringID.DELETE_VERTEX_ID, "Delete vertex that does not change the structure." }, 
    
    { StringID.EDIT_CONTOUR_ID, "Edit contour. The contour must be convex." }, 
    
    { StringID.PERPENDICULAR_BISECTOR_ID, "Input Perpendicular Bisector of two vertices. Select two vertices by left click." }, 
    
    { StringID.SELECT_ID, SELECT_LINE_TEXT},
    
    { StringID.SELECT_ALL_LINE_ID, SELECT_LINE_TEXT},
    
    { StringID.COPY_PASTE_ID, COPY_PASTE_TEXT},
    { StringID.CUT_PASTE_ID, COPY_PASTE_TEXT},
    
    };
    
    @Override
    protected Object[][] getContents() {
        return strings;
    }

}
