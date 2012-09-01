package oripa.resource;

public class StringID {
	public static class Menu{
		public static final String TITLE_ID = "Title";
		public static final String FILE_ID =  "File"; 
	    public static final String EDIT_ID =  "Edit"; 
	    public static final String HELP_ID =  "Help"; 
	    public static final String NEW_ID =  "New"; 
	    public static final String OPEN_ID =  "Open"; 
	    public static final String SAVE_ID =  "Save"; 
	    public static final String SAVE_AS_ID =  "SaveAs"; 
	    public static final String SAVE_AS_IMAGE_ID =  "SaveAsImage"; 
	    public static final String EXPORT_DXF_ID =  "ExportDXF"; 
	    public static final String EDIT_CONTOUR_ID =  "EditContour"; 
	    public static final String PROPERTY_ID =  "Property"; 
	    public static final String EXIT_ID =  "Exit"; 
	    public static final String UNDO_ID =  "Undo"; 
	    public static final String ABOUT_ID =  "About"; 
	}

	public static class CommandHint{
		public static final String DIRECT_V_ID =  "Direction_DirectV"; 
	    public static final String ON_V_ID =  "Direction_OnV"; 
	    public static final String SYMMETRIC_ID =  "Direction_Symmetric"; 
	    public static final String TRIANGLE_ID =  "Direction_TriangleSplit"; 
	    public static final String BISECTOR_ID =  "Direction_Bisector"; 
	    public static final String VERTICAL_ID =  "Direction_VerticalLine"; 
	    public static final String MIRROR_ID =  "Direction_Mirror"; 
	    public static final String BY_VALUE_ID =  "Direction_ByValue"; 
	    public static final String PICK_LENGTH_ID =  "Direction_PickLength"; 
	    public static final String PICK_ANGLE_ID =  "Direction_PickAngle"; 
	    public static final String CHANGE_LINE_TYPE_ID =  "Direction_ChangeLineType"; 
	    public static final String DELETE_LINE_ID =  "Direction_DeleteLine"; 
	    public static final String ADD_VERTEX_ID =  "Direction_AddVertex"; 
	    public static final String DELETE_VERTEX_ID =  "Direction_DeleteVertex"; 
	    public static final String CONTOUR_ID =  "Direction_EditContour"; 
		public static final String PERPENDICULAR_BISECTOR_ID = "Direction_PBisec";
		public static final String SELECT_ID = "Direction_select";
	}
	
	public static class Error{
	    public static final String SAVE_FAILED_ID =  "Error_FileSaveFailed"; 
	    public static final String LOAD_FAIELD_ID =  "Error_FileLoadFailed"; 
	}
	
	public static class Warning{
		public static final String SAME_FILE_EXISTS_ID =  "Warning_SameNameFileExist"; 
	    public static final String FOLD_FAILED_DUPLICATION_ID =  "Warning_foldFail1";
	    public static final String FOLD_FAILED_WRONG_STRUCTURE_ID =  "Warning_foldFail2";
	}	
	
	public static class Default{
	public static final String FILE_NAME_ID =  "DefaultFileName"; 
	}
	
	public static final String DIALOG_TITLE_SAVE_ID =  "DialogTitle_FileSave"; 
    public static final String ORIPA_FILE_ID =  "ORIPA_File"; 
    public static final String PICTURE_FILE_ID = "Picture_File";
    
    public static class UI{
	    public static final String INPUT_LINE_ID =  "UI_InputLine"; 
	    public static final String SELECT_ID =  "UI_Select"; 
	    public static final String DELETE_LINE_ID =  "UI_DeleteLine"; 
	    public static final String SHOW_GRID_ID =  "UI_ShowGrid"; 
	    public static final String CHANGE_LINE_TYPE_ID =  "UI_ChangeLineType"; 
	    public static final String ADD_VERTEX_ID =  "UI_AddVertex"; 
	    public static final String DELETE_VERTEX_ID =  "UI_DeleteVertex"; 
	    public static final String MEASURE_ID =  "UI_Mesure"; 
	    public static final String FOLD_ID =  "UI_Fold"; 
	    public static final String GRID_SIZE_CHANGE_ID =  "UI_GridSizeChange"; 
	    public static final String SHOW_VERTICES_ID =  "UI_ShowVertices"; 
	    public static final String EDIT_MODE_ID =  "UI_EditMode"; 
	    public static final String LINE_INPUT_MODE_ID =  "UI_LineInputMode";
	    public static final String LENGTH_ID =  "UI_Length";
	    public static final String ANGLE_ID =  "UI_Angle";
	    public static final String GRID_DIVIDE_NUM_ID =  "UI_GridDivideNum";
    }


    public static class ModelMenu{
	    public static final String DISPLAY_ID =  "MENU_Disp";
	    public static final String EXPORT_DXF_ID =  "MENU_ExportModelLine_DXF";
	    public static final String INVERT_ID =  "MENU_Invert";
	    public static final String SLIDE_FACES_ID =  "MENU_SlideFaces";
	    public static final String DIRECTION_BASIC_ID =  "Direction_Basic";
	    public static final String DISPLAY_TYPE_ID =  "MENU_DispType";
	    public static final String FILL_COLOR_ID =  "MENU_FillColor";
	    public static final String FILL_WHITE_ID =  "MENU_FillWhite";
	    public static final String FILL_ALPHA_ID =  "MENU_FillAlpha";
	    public static final String DRAW_LINES_ID =  "MENU_DrawLines";
	    public static final String TITLE_ID =  "ExpectedFoldedOrigami";
    }	
	
	
	

}
