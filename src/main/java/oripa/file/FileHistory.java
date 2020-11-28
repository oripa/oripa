package oripa.file;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.LinkedList;

public class FileHistory {
	private final LinkedList<String> mostRecentlyUsedHistory = new LinkedList<>();
	private final int maxSize;

	public FileHistory(final int maxSize) {
		this.maxSize = maxSize;
	}

	/**
	 *
	 * @param filePath
	 * @return true if the given path is appended to history
	 */
	public boolean useFile(final String filePath) {

		boolean appended = false;
		int index = mostRecentlyUsedHistory.indexOf(filePath);

		if (index < 0) {
			if (mostRecentlyUsedHistory.size() >= maxSize) {
				mostRecentlyUsedHistory.removeLast();
			}

			mostRecentlyUsedHistory.addFirst(filePath);
			appended = true;
		} else {
			String item = mostRecentlyUsedHistory.remove(index);
			mostRecentlyUsedHistory.addFirst(item);
		}

		return appended;
	}

	public Collection<String> getHistory() {
		return mostRecentlyUsedHistory;
	}

	public String getLastPath() {
		if (mostRecentlyUsedHistory.isEmpty()) {
			return System.getProperty("user.home");
		}

		return mostRecentlyUsedHistory.getFirst();
	}

	public String getLastDirectory() {
		if (mostRecentlyUsedHistory.isEmpty()) {
			return System.getProperty("user.home");
		}

		File file = new File(mostRecentlyUsedHistory.getFirst());
		return file.getParent();
	}

	public void saveToFile(final String path) {
		String fileNames[] = new String[maxSize];
		int i = 0;
		for (String history : mostRecentlyUsedHistory) {
			fileNames[i] = history;
			i++;
		}

		InitData initData = new InitData();

		initData.setMRUFiles(fileNames);
		initData.setLastUsedFile(getLastPath());

		try {
			XMLEncoder enc = new XMLEncoder(
					new BufferedOutputStream(
							new FileOutputStream(path)));
			enc.writeObject(initData);
			enc.close();

		} catch (FileNotFoundException e) {
		}
	}

	public void loadFromFile(final String path) {
		InitData initData;
		try {
			XMLDecoder dec = new XMLDecoder(
					new BufferedInputStream(
							new FileInputStream(path)));
			initData = (InitData) dec.readObject();
			dec.close();

			int initMRUlength = initData.MRUFiles.length;
			int length = (maxSize < initMRUlength) ? maxSize : initMRUlength;

			for (int i = 0; i < length; i++) {
				var fileName = initData.MRUFiles[i];
				if (fileName != null && !fileName.isEmpty()) {
					mostRecentlyUsedHistory.add(fileName);
				}
			}

		} catch (Exception e) {
		}

	}

}
