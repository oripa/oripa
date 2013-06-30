package oripa.file;

import java.net.URL;

import javax.swing.ImageIcon;



public class ImageResourceLoader {

	public ImageIcon loadAsIcon(String name){
		return this.loadAsIcon(name, getClass());
	}

	public ImageIcon loadAsIcon(String name, Class<?> c){
		ClassLoader classLoader = c.getClassLoader();
		URL url=classLoader.getResource(name);
		
		System.out.println(url.toString());
		
		ImageIcon icon=new ImageIcon(url);
		
		return icon;

	}
}
