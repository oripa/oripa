package oripa.resource;

import java.util.ListResourceBundle;

public class WarningStringResource_en extends ListResourceBundle {

	static final Object[][] strings = {
			{ StringID.Warning.SAME_FILE_EXISTS_ID, "Same name file exists. Overwrite?" },
			{ StringID.Warning.FOLD_FAILED_DUPLICATION_ID,
					"Failed to fold. Try again by deleting duplicating segments?" },
			{ StringID.Warning.FOLD_FAILED_WRONG_STRUCTURE_ID,
					"Failed to fold. It seems the pattern has basic problems." },
			{ StringID.Warning.NO_SELECTION_ID,
					"Select target lines." },
			{ StringID.Warning.ARRAY_COPY_TITLE_ID,
					"Array Copy" },
			{ StringID.Warning.CIRCLE_COPY_TITLE_ID,
					"Circle Copy" },
			{ StringID.Warning.SAVE_TITLE_ID,
					"Save" },
	};

	@Override
	protected Object[][] getContents() {
		return strings;
	}

}
