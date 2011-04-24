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

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

public class JHighlightPane extends JTextPane implements KeyListener {

	private static final long serialVersionUID = 2366076037322997376L;	
	private String nonText = "(){}[] \t\r\n";
	private Hashtable<String, Color> keywords = new Hashtable<String, Color>();

	public JHighlightPane(){
		this("");				
	}
	
	public JHighlightPane( String text ){
		this.setText( text );
		this.addKeyListener( this );
	}
	
	public void setNonText( String nonText ){
		this.nonText = nonText;
	}
	
	public String getNonText(){
		return this.nonText;
	}
			
	public void addKeyWord( String word, Color color ){
		keywords.put( word, color );
		this.updateHighlighting();
	}
	
	public void addKeyWords( String[] words, Color color ){
		for ( String word : words ){
			keywords.put(word, color);			
		}
		this.updateHighlighting();
	}
	
	public Hashtable<String, Color> getKeyWords(){
		return keywords;
	}
	
	public void clearKeyWords(){
		keywords = new Hashtable<String, Color>();
	}
	
	public void setText( String text ){
		String oldText = this.getText().replace("\r", "");
		String newText = text.replace("\r", "");
		if( ! oldText.equals(newText) ){		
	        SimpleAttributeSet styles = new SimpleAttributeSet();
	        DefaultStyledDocument document = new DefaultStyledDocument();
	        this.setDocument(document);        
	        try {
				document.insertString(0, text, styles);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.updateHighlighting();
		}
	}
	
	private void updateHighlighting(){
		//int carpos = this.getCaretPosition();
		String text = null;
		try {
			text = this.getDocument().getText(0, this.getDocument().getLength() + 1);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//this.setText(text);
		//this.setCaretPosition(carpos);
		
		Enumeration<String> keys = keywords.keys();
						
    	DefaultStyledDocument document = (DefaultStyledDocument) this.getDocument();    	
		// document.putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");
    	
    	Style black = this.addStyle("Black", null);
    	StyleConstants.setForeground(black, Color.black);
    	document.setCharacterAttributes(0, text.length()+1, black, true);
    	    	
		while( keys.hasMoreElements() ){
			String word = keys.nextElement();
			Color color = keywords.get(word);
	    	Style style = this.addStyle("Red", null);
	    	StyleConstants.setForeground(style, color);	
			
			// Trick to loop through all occurances of "word" in a neat way
			// See: http://timvalenta.wordpress.com/2009/01/06/java-count-substring/
			int count = 0;			
		    for (int fromIndex = 0; fromIndex > -1; count++){
		        fromIndex = text.indexOf(word, fromIndex + ((count > 0) ? 1 : 0));

		        if( fromIndex !=-1){
		        	// Makes text red
		        	boolean blankStart = false;
		        	boolean blankEnd = false;
		        	if( fromIndex == 0 ){
		        		blankStart = true;
		        	}
		        	else if ( nonText.indexOf( text.charAt( fromIndex-1 ) ) != -1 ){
		        		blankStart = true;
		        	}
		        	if ( fromIndex + word.length() == text.length() ){
		        		blankEnd = true;		        	
		        	} else if ( nonText.indexOf( text.charAt( fromIndex + word.length() ) ) != -1  ){
		        		blankEnd = true;
		        	}
		        	if (blankStart && blankEnd){		        		
		        		document.setCharacterAttributes(fromIndex, word.length(), style, true);
		        	} 
		        }
		    }		   
		}
		
		this.setCaretColor(Color.BLACK);
	}

	public void keyPressed(KeyEvent arg0) {
		
		
	}

	public void keyReleased(KeyEvent arg0) {
		this.updateHighlighting();		
	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
