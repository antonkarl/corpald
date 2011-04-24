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

public class CorpaldController implements ControllerInterface {

	CorpaldModelInterface model;
	CorpaldView view;
	
	public CorpaldController( CorpaldModelInterface model ){
		this.model = model;
		view = new CorpaldView(this, model);
		view.createAndShowGUI();
	}
	
	public void initialize(){
		CorpaldSettings settings = CorpaldSettings.getInstance();
		this.setQuery("");
		this.setRootNode( settings.getProperty("query.root") );
		this.openDef( new File("definitions/" + settings.getProperty("query.definitions") ) );
		this.setNodesOnly(true);
		this.setModified(false);			
		// this.setQuery( System.getProperty("user.dir") );
	}
	
	public void runQuery() {
		model.runQuery();
	}
	
	public void setQuery(String query){
		model.setQuery(query);
	}

	public void setNodesOnly(boolean nodesOnly) {
		model.setNodesOnly(nodesOnly);
		
	}

	public void setRemoveNodes(boolean removeNodes) {
		model.setRemoveNodes(removeNodes);		
	}

	public void setRootNode(String rootNode) {
		model.setRootNode(rootNode);
	}

	public void newQuery() {
		model.newQuery();
	}
	
	public void setModified( boolean modified ){
		model.setModified(modified);
	}

	public void openDef( File file ) {
		model.openDefinitions(file);		
	}

	public void openQuery( File file ) {
		model.openQuery(file);		
	}

	public void saveQuery() {
		model.saveQuery();		
	}

	public void saveQueryAs( File file ) {
		model.saveQueryAs(file);		
	}
	

}
