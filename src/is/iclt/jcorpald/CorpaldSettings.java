package is.iclt.jcorpald;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class CorpaldSettings extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8882624160570336341L;
	
	private static final CorpaldSettings INSTANCE = new CorpaldSettings();

	private CorpaldSettings(){
		try {
			this.load( new FileInputStream("corpald.properties") );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static CorpaldSettings getInstance(){
		return INSTANCE;
	}
	
}
