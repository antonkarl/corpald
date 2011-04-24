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

public class QueryParser {

	// The query itself
	private String rootNode = null;
	private String query = null;
	private String definitions = null;
	private boolean nodesOnly = false;
	private boolean removeNodes = false;	
	
	public QueryParser( String query ){
				
		this.query = query.substring( query.indexOf("query:") + 6 ).trim();
		String options = query.substring( 0, query.indexOf("query:") );
		String[] lines = options.split("\n");
		for( String line : lines ){
			String[] chunks = line.split(":");
			if( chunks.length == 2 ){
				String key = chunks[0].trim();
				String value = chunks[1].trim();

				if( key.equals("node") ){
					rootNode = value;
				}				

				if( key.equals("define") ){
					definitions = value;
				}						
				
				if( key.equals("nodes_only") && value.equals("t") ){
					nodesOnly = true;
				}
				
				if( key.equals("remove_nodes") && value.equals("t") ){
					removeNodes = true;
				}
				
			}
		}
	}
	
	public String getRootNode(){
		return this.rootNode;
	}
	
	public String getDefinitions(){
		return this.definitions;
	}
	
	public String getQuery(){
		return this.query;
	}
	
	public boolean getNodesOnly(){
		return this.nodesOnly;
	}
	
	public boolean getRemoveNodes(){
		return this.removeNodes;
	}
	
			
}
