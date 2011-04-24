/*
Corpald
Copyright 2011 Anton Karl Ingason
Website: http://linguist.is/corpald
Contact: anton.karl.ingason@gmail.com

Icelandic Parsed Historical Corpus (IcePaHC)
is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

Corpald is a cross-platform graphical user interface to search corpora in labeled bracketing format.
Corpald calls CorpusSearch by Beth Randall on the command line to execute
search queries. (http://corpussearch.sourceforge.net/)

The project is funded in part by the following grant:
From the Icelandic Research Fund (RANNÍS), grant nr. 090662011, Viable Language Technology beyond English – Icelandic as a test case.
*/

package is.iclt.jcorpald;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class CorpaldModel implements CorpaldModelInterface {

	// Observers
	private ArrayList<QueryObserver> queryObservers = new ArrayList<QueryObserver>();
	private ArrayList<ResultObserver> resultObservers = new ArrayList<ResultObserver>();
	
	// The query itself
	private String rootNode = "";
	private String query = "";
	private boolean nodesOnly = false;
	private boolean removeNodes = false;	
	private String result = "";	
	private File definitions = null;
	private ArrayList<String> defWords = null;
	

	// File state 
	private File file = null;
	private boolean modified = false;
	
	private String constructQueryFile(){
		String queryFile = "node: " + rootNode + "\n";
		if( nodesOnly ){
			queryFile += "nodes_only: t\n";
		}
		if( removeNodes ){
			queryFile += "remove_nodes: t\n";
		}
		if( definitions != null ){
			queryFile += "define: " + this.getDefinitionsFileName()+"\n"; 
		}
		queryFile += "\nquery: " + query;
		return queryFile;
	}

	private String getTempQueryPath() {
		String filename = System.getProperty("user.home")+"/temp.q";
		FileUtils.deleteQuietly(new File(filename));
		return filename;
	}
/*
	private String getTempOutputPath() {
		String filename = System.getProperty("user.home")+"/temp.out";
		FileUtils.deleteQuietly(new File(filename));
		return filename;
	}
*/
	public void runQuery() {
		try {
			this.performRunQuery();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void performRunQuery() throws Exception {			
				
		String queryFile = null;		
		String outputFile = null;
		String targetFile = null; 
		
		if( this.file == null ){
		  queryFile = new File( this.getTempQueryPath() ).getPath();
		  // outputFile = this.getTempOutputPath();				 
		}
		else {
		  queryFile = this.file.getPath();
		//  outputFile = queryFile.substring(0,queryFile.length()-2)+".out";		  		 
		}
		outputFile = queryFile.substring(0,queryFile.length()-2)+".out";				
		targetFile = outputFile.substring(0,outputFile.length()-4) + ".txt";
		try {
		// FileUtils.deleteQui(new File(outputFile));
			new File(outputFile).delete();
			System.out.println("Deleted: " + outputFile);
		} catch ( Exception ex ){
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}	
		// Write the query to a temporary file

		this.file = new File(queryFile); 
		FileUtils.writeStringToFile( file, this.constructQueryFile(), "utf-8");
		String cmd = "java -classpath lib/CS_2.002.75.jar csearch.CorpusSearch \""
				+ new File(queryFile).getPath() 
				+ "\" corpora/"+CorpaldSettings.getInstance().getProperty("corpus.directory")+"/*.psd -out \""
				+ new File(outputFile).getPath() + "\"";
		
		if( ! OSValidator.isWindows() ){
			cmd = cmd.replace("\"", "");
		}
		
		result = cmd;
		System.out.println(cmd);
/*		
		Runtime run = Runtime.getRuntime();
		Process pr = null;
		pr = run.exec(cmd);
		pr.waitFor();
	*/	
//hhhhhhhh
		Process application = Runtime.getRuntime().exec(cmd);

		StringBuffer inBuffer = new StringBuffer();
		InputStream inStream = application.getInputStream();
		new InputStreamHandler( inBuffer, inStream );

		StringBuffer errBuffer = new StringBuffer();
		InputStream errStream = application.getErrorStream();
		new InputStreamHandler( errBuffer , errStream );

		application.waitFor();
		
		
		// TODO deal with somehow
		// System.out.println(stdout);
		// System.out.println(stderr);
		
		String result = FileUtils.readFileToString( new File( outputFile ), "utf-8" );
		FileUtils.deleteQuietly( new File(targetFile) );
		FileUtils.moveFile( new File(outputFile), new File(targetFile) );				
		//String line;
		//boolean error = false;
		//String stdout = IOUtils.toString(pr.getInputStream()).trim();
		// String stderr = "x"; // IOUtils.toString(pr.getErrorStream()).trim();
		// if( pr.getErrorStream()		  
		// System.out.println(stderr);
		
		if( errBuffer.toString().indexOf("ERROR!") > -1 ){
			result = "Unfortunately there is a problem with your query.\nCorpusSearch was unable to " +
					"come up with results.\n\n" +
					"Did you misspell a keyword, a logical operator or a reference to the definitions file?\n" +
					"- those are displayed in orange, blue and green respectively if well-formed\n" +														
					"Did you use the correct number of arguments for all search functions?\n" +
					"- for example, 'idoms' requires two arguments while 'idomsmod' requires three \n" +
					"- see CorpusSearch documentation (http://corpussearch.sourceforge.net)\n" +
					"Do you have unmatched parentheses or brackets?\n";
		}
		
		this.setModified(false);
		this.notifyQueryObservsers();
		setResult(result);
		
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		if( ! this.query.equals(query) ){
			this.query = query;
			this.modified = true;
			this.notifyQueryObservsers();
		}
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
		this.notifyResultObservers();
	}

	public boolean getNodesOnly() {
		return nodesOnly;
	}

	public boolean getRemoveNodes() {
		return removeNodes;
	}

	public String getRootNode() {
		return rootNode;
	}

	public void setNodesOnly(boolean nodesOnly) {
		if( this.nodesOnly != nodesOnly){
			this.nodesOnly = nodesOnly;
			this.modified = true;
			this.notifyQueryObservsers();
		}		
	}

	public void setRemoveNodes(boolean removeNodes) {
		if( this.removeNodes != removeNodes ){
			this.removeNodes = removeNodes;
			this.modified = true;
			this.notifyQueryObservsers();
		}
	}

	public void setRootNode(String rootNode) {
		if( ! this.rootNode.equals(rootNode) ){
			this.modified = true;
			this.rootNode = rootNode;
			this.notifyQueryObservsers();
		}
	}
	
	public void saveQuery() {
		if( this.file != null && this.modified ){
			this.saveQueryAs(file);	
			this.modified = false;
			this.notifyQueryObservsers();
		}		
	}
	
	public void newQuery() {
		CorpaldSettings settings = CorpaldSettings.getInstance();
		
		rootNode = "";
		query = "";
		nodesOnly = true;
		removeNodes = false;	
		result = "";
		this.file = null;
		this.modified = false;
		this.rootNode= settings.getProperty("query.root");
		this.notifyQueryObservsers();
		this.notifyResultObservers();
	}	
	
	public void saveQueryAs(File file) {		
		try {
			this.file = file;
			if( ! file.getPath().endsWith(".q") ){
				this.file = new File( file.getPath()+".q" );								
			}
			FileUtils.writeStringToFile(file, this.constructQueryFile(), "utf-8");
			this.modified = false;
			this.notifyQueryObservsers();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void setModified( boolean modified ){
		this.modified = modified;		
		this.notifyQueryObservsers();
	}

	public boolean isModified(){
		return this.modified;
	}
	
	public String getFileName(){
	
			if( this.file != null){				
				return this.file.getPath();
			}
			else{
				return null;
			}
	
	}
	
	public String getDefinitionsFileName(){
		try {
			if( this.definitions != null){				
				return this.definitions.getPath();
			}
			else{
				return null;
			}
		} catch (Exception e) {		
			e.printStackTrace();
			return null;
		}			
	}
	
	public ArrayList<String> getDefWords(){
		return this.defWords;
	}

	public void openQuery(File file) {
		try {
			String queryString = FileUtils.readFileToString(file, "utf-8");
			QueryParser parser = new QueryParser(queryString);
			this.setRootNode( parser.getRootNode() );
			this.setQuery( parser.getQuery() );
			this.setNodesOnly( parser.getNodesOnly() );
			this.setRemoveNodes( parser.getRemoveNodes() );
			this.openDefinitions( new File( parser.getDefinitions() ) );
			this.file = file;
			this.modified = false;
			this.notifyQueryObservsers();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}			
	
	public void openDefinitions(File definitions) {
	
		try {
			this.definitions = definitions;
			String defString = FileUtils.readFileToString(definitions, "utf-8");
			DefinitionsParser parser = new DefinitionsParser(defString);
			this.defWords = parser.getDefWords();
		} catch (IOException e) {
			// could not load definitions
			e.printStackTrace();
		}		
				
		this.notifyQueryObservsers();
	}
	
	/*
	 * Code for handling observers
	 */
	
	public void registerQueryObserver(QueryObserver o) {
		queryObservers.add(o);
	}

	public void removeQueryObserver(QueryObserver o) {
		queryObservers.remove(o);
	}

	public void registerResultObserver(ResultObserver o) {
		resultObservers.add(o);
	}

	public void removeResultObserver(ResultObserver o) {
		resultObservers.remove(o);
	}

	private void notifyQueryObservsers() {
		for (QueryObserver o : queryObservers) {
			o.queryChanged();
		}
	}

	private void notifyResultObservers() {
		for (ResultObserver o : resultObservers) {
			o.resultChanged();
		}
	}

	public File getResultFile() {
		try {
			String str = this.getFileName();			
			str = FilenameUtils.removeExtension(str)+".txt";
			return new File(str);
		} catch (Exception e) {			
			e.printStackTrace();
			return null;
		}		
	}


}
