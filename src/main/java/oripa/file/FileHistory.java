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

import oripa.InitData;

public class FileHistory {
	private LinkedList<String> mostRecentlyUsedHistory = new LinkedList<>();
	private int maxSize;
	
	public FileHistory(int maxSize){
		this.maxSize = maxSize;
	}
	
	/**
	 * 
	 * @param filePath
	 * @return true if the given path is appended to history
	 */
	public boolean useFile(String filePath){

		boolean appended = false;
		int index = mostRecentlyUsedHistory.indexOf(filePath);
		
		if(index < 0){
			if(mostRecentlyUsedHistory.size() >= maxSize){
				mostRecentlyUsedHistory.removeLast();
			}
			
			mostRecentlyUsedHistory.addFirst(filePath);
			appended = true;
		}
		else {
			String item = mostRecentlyUsedHistory.remove(index);
			mostRecentlyUsedHistory.addFirst(item);
		}
		
		return appended;
	}

	public Collection<String> getHistory(){
		return mostRecentlyUsedHistory;
	}
	
	public String getLastPath(){
		if(mostRecentlyUsedHistory.isEmpty()){
			return System.getProperty("user.home");
		}
		
		return mostRecentlyUsedHistory.getFirst();
	}
	
	public String getLastDirectory(){
		if(mostRecentlyUsedHistory.isEmpty()){
			return System.getProperty("user.home");
		}
		
		File file = new File(mostRecentlyUsedHistory.getFirst());
		return file.getParent();
	}
	
	public void saveToFile(String path) {
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
							new FileOutputStream(path)) );
			enc.writeObject(initData);
			enc.close();
			
		} catch (FileNotFoundException e) {
		}
	}

	public void loadFromFile(String path) {
		InitData initData;
		try {
			XMLDecoder dec = new XMLDecoder(
					new BufferedInputStream(
							new FileInputStream(path)) );
			initData = (InitData) dec.readObject();
			dec.close();

			int initMRUlength = initData.MRUFiles.length;
			int length = (maxSize < initMRUlength)? maxSize : initMRUlength;
			
			for (int i = 0; i < length; i++) {
				if (initData.MRUFiles[i] != null && !initData.MRUFiles[i].equals("")) {
					mostRecentlyUsedHistory.add(initData.MRUFiles[i]);
				}
			}

		} catch (Exception e) {
		}

	}

}
