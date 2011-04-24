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
import java.util.ArrayList;

public interface CorpaldModelInterface {
	
	void runQuery();
	void openQuery(File file);
	void newQuery();
	void saveQuery();
	void saveQueryAs(File file);
	void openDefinitions(File file);
	boolean isModified();
	void setModified(boolean modified);
	String getFileName();
	String getDefinitionsFileName();
	ArrayList<String> getDefWords();
		
	void setQuery(String query);	
	String getQuery();
	void setResult(String result);	
	String getResult();
	File getResultFile();
	void setRootNode(String rootNode);
	String getRootNode();
	void setNodesOnly(boolean nodesOnly);	
	boolean getNodesOnly();
	void setRemoveNodes(boolean removeNodes);
	boolean getRemoveNodes();
		
	void registerQueryObserver( QueryObserver o );
	void removeQueryObserver( QueryObserver o );
	void registerResultObserver( ResultObserver o );
	void removeResultObserver( ResultObserver o );

}
