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

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class CorpaldView extends JFrame implements ActionListener, QueryObserver, ResultObserver, KeyListener, ItemListener, ClipboardOwner {
	
	CorpaldModelInterface model;
	ControllerInterface controller;
	
	// For gathering input for searches
	JTextField txtRootNode = null;
	JLabel labQuery = new JLabel("Query:");
	JHighlightPane txtQuery = null;	
	JCheckBox chkNodesOnly = null;
    JCheckBox chkRemoveNodes = null;       
    
    // Buttons
    JButton butNewQuery = null;
    JButton butSaveQuery = null;
    JButton butSaveQueryAs = null;
    JButton butOpenQuery = null;
    JButton butOpenDef = null;
    JButton butRunQuery = null;

    JButton butCopyResults = null;
    JButton butOpenFolder = null;
    JButton butTextEditor = null;
    
    // For syntax highlighting
    String[] keywords = new String[]{"CCommands", "cCommands", "ccommands", "column", "Col",
    		"col", "Dominates", "dominates", "Doms", "doms", "domsWords", "domswords",
    		"DomsWords<", ",domsWords<", "domswords<", "DomsWords>", "domsWords>",
    		"domswords>", "Exists", "exists", "HasLabel", "hasLabel", "haslabel",
    		"HasSister", "hasSister", "hassister", "iDominates", "idominates",
    		"iDoms", "idoms", "iDomsFirst", "idomsfirst", "iDomsLast", "idomslast",
    		"iDomsMod", "idomsmod", "iDomsNumber", "idomsnumber", "iDomsNum", "idomsnum",
    		"iDomsOnly", "idomsonly", "iDomsTotal", "idomstotal", "iDomsTotal<", "idomstotal<",
    		"iDomsTotal>", "idomstotal>", "iDomsViaTrace", "idomsviatrace", "InID", "inID",
    		"iPrecedes", "iprecedes", "iPres", "ipres", "IsRoot", "isRoot", "isroot",
    		"Precedes", "precedes", "Pres", "pres", "SameIndex", "sameIndex", "sameindex"};
    String[] operators = new String[]{"AND","OR","NOT"};
    String[] defwords = new String[]{};
    
    // Dialogs
    final JFileChooser queryChooser = new JFileChooser();
    final JFileChooser defChooser = new JFileChooser();    
    
    // For displaying results from searches
	JTextArea txtResult = null;	
		
	private static final long serialVersionUID = 9022781806157292479L;
	
	public CorpaldView( ControllerInterface controller, CorpaldModelInterface model ){
		this.controller = controller;
		this.model = model;
		model.registerQueryObserver( (QueryObserver) this );
		model.registerResultObserver( (ResultObserver) this );
	}

	
    public void createAndShowGUI() {        
    	CorpaldSettings settings = CorpaldSettings.getInstance();
    	
        //Create and set up the window.
        JFrame frame = new JFrame( settings.getProperty("corpus.acronym") + " "+ settings.getProperty("corpus.version") +
        		     " - " + settings.getProperty("corpus.longname"));        
        frame.setIconImage( (new ImageIcon("icons/corpald.png")).getImage() );
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        // frame.setBounds(50, 50, 700, 450);
        frame.setPreferredSize( new Dimension(750,720) );
        
        // Create the panel that has the query and the result        
        JPanel panMainArea = new JPanel(new BorderLayout());        
        JPanel panQuery = new JPanel(new BorderLayout());
        
        txtQuery = new JHighlightPane();        
        txtQuery.addKeyListener(this);
                        
        txtQuery.setFont( new Font("Monospaced",Font.BOLD,16 ) );        
        this.updateHighlighting();        
        txtQuery.setPreferredSize( new Dimension(700,150) );
        
        panQuery.add(labQuery, BorderLayout.NORTH );
        panQuery.add( new JScrollPane( txtQuery, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS ), BorderLayout.CENTER);
        panQuery.setBorder(new EmptyBorder(0, 10, 10, 10));
        
        String welcomeMessage = "";
        try {
			welcomeMessage = FileUtils.readFileToString( new File( settings.getProperty("corpus.welcome") ), "utf-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        JPanel panResult = new JPanel(new BorderLayout());
        txtResult = new JTextArea(welcomeMessage);
        
        txtResult.setEditable(false);
        txtResult.setFont( new Font("Monospaced",Font.BOLD,14 ) );  
        
        JScrollPane scrResult = new JScrollPane( txtResult, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
        scrResult.setPreferredSize( new Dimension(700,400) );
        panResult.add( new JLabel("Result:"), BorderLayout.NORTH );
        panResult.add( scrResult, BorderLayout.CENTER);
        panResult.setBorder(new EmptyBorder(0, 10, 0, 10));
        
        panMainArea.add(panQuery, BorderLayout.NORTH);
        panMainArea.add(panResult, BorderLayout.CENTER);        
        
        // Create panel at top with buttons
        JPanel panToolbar = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
                    
        // New, Open, Save
        // New
        ImageIcon icoNewQuery = new ImageIcon("icons/page_white.png");        
        butNewQuery = new JButton( icoNewQuery );
        butNewQuery.addActionListener(this);
        butNewQuery.setPreferredSize(new Dimension(26,26));
        butNewQuery.setToolTipText("Create a new empty query");
        panToolbar.add( butNewQuery );       
        // Open
        ImageIcon icoOpenQuery = new ImageIcon("icons/folder.png");        
        butOpenQuery = new JButton( icoOpenQuery );
        butOpenQuery.addActionListener(this);
        butOpenQuery.setPreferredSize(new Dimension(26,26));
        butOpenQuery.setToolTipText("Open a query file");
        panToolbar.add( butOpenQuery );               
        // Save
        ImageIcon icoSaveQuery = new ImageIcon("icons/page_save.png");        
        butSaveQuery = new JButton( icoSaveQuery );
        butSaveQuery.addActionListener(this);
        butSaveQuery.setPreferredSize(new Dimension(26,26));
        butSaveQuery.setToolTipText("Save current query");
        panToolbar.add( butSaveQuery );               
        // Save as
        ImageIcon icoSaveQueryAs = new ImageIcon("icons/page_save_as.png");        
        butSaveQueryAs = new JButton( icoSaveQueryAs );
        butSaveQueryAs.addActionListener(this);
        butSaveQueryAs.setPreferredSize(new Dimension(26,26));
        butSaveQueryAs.setToolTipText("Save current query under a new file name");        
        panToolbar.add( butSaveQueryAs );               
                
        // Open definitions file
        ImageIcon icoOpenDef = new ImageIcon("icons/folder_table.png");        
        butOpenDef = new JButton( icoOpenDef );
        butOpenDef.addActionListener(this);
        butOpenDef.setPreferredSize(new Dimension(26,26));        
        butOpenDef.setToolTipText("Select a new definitions file");
        panToolbar.add( butOpenDef );                 
        
        // Run Query button
        ImageIcon icoRunQuery = new ImageIcon("icons/control_play_blue.png");        
        butRunQuery = new JButton( "Run Query", icoRunQuery );
        butRunQuery.setPreferredSize(new Dimension(130,26));
        butRunQuery.addActionListener( this );
        butRunQuery.setToolTipText("Run the current query using CorpusSearch");
        panToolbar.add( butRunQuery );       
                
        // TextField for root node label
        JLabel labRootNode = new JLabel("Root:");
        panToolbar.add( labRootNode );        
        txtRootNode = new JTextField("",12);      
        txtRootNode.setPreferredSize( new Dimension(50,26) );
        txtRootNode.addKeyListener(this);
        txtRootNode.setMargin( new Insets(3,3,3,3) );
        txtRootNode.setToolTipText("<html>Search within instances of a particular type of node,<br/>" +
        		                   "such as IP-*, IP-SUB, NP-*, etc. $ROOT matches<br/>" +
        		                   "the root node of every tree in the corpus.</html>");
        panToolbar.add( txtRootNode );
        
        chkNodesOnly = new JCheckBox("Nodes only");
        chkNodesOnly.addItemListener(this);
        chkNodesOnly.setToolTipText("<html>If checked, CorpusSearch prints out only the nodes that<br/>" +
        		                    "contain the structure described in \"Query\". If not checked,<br/>" +
        		                    "CorpusSearch prints out the entire sentence that contains the<br/>" +
        		                    "structure described in \"Query\".</html>");
        panToolbar.add( chkNodesOnly );

        chkRemoveNodes = new JCheckBox("Remove nodes");
        chkRemoveNodes.addItemListener(this);
        chkRemoveNodes.setToolTipText("<html>Remove subtrees whose root is of the same syntactic category<br/>" +
        		                      "as the node boundary embedded within a instance of that node<br/>" +
        		                      "boundary. \"Remove nodes\" thus removes recursive structure.</html>");
        panToolbar.add( chkRemoveNodes );        
        
        
        // Create panel at top with buttons
        JPanel panBottombar = new JPanel( new FlowLayout( FlowLayout.CENTER ) ); 
        // panBottombar.setBorder(new EmptyBorder(0, 0, 10, 5));
        ImageIcon icoOpenFolder = new ImageIcon("icons/folder.png");
        butOpenFolder = new JButton("Show result in folder", icoOpenFolder );
        butOpenFolder.setEnabled(false);
        butOpenFolder.addActionListener(this);
        panBottombar.add( butOpenFolder );        

        ImageIcon icoTextEditor = new ImageIcon("icons/page_white_go.png");
        butTextEditor = new JButton("Open result in text editor", icoTextEditor);        
        butTextEditor.setEnabled(false);
        butTextEditor.addActionListener(this);
        panBottombar.add( butTextEditor );        
        
        ImageIcon icoCopyResults= new ImageIcon("icons/page_copy.png");
        butCopyResults = new JButton("Copy result to clipboard", icoCopyResults );
        butCopyResults.setEnabled(false);
        butCopyResults.addActionListener(this);
        panBottombar.add( butCopyResults );

        // Add stuff to top level content pane                
        frame.getContentPane().setLayout( new BorderLayout() );
        frame.getContentPane().add( panToolbar, BorderLayout.NORTH );
        frame.getContentPane().add( panMainArea, BorderLayout.CENTER );
        frame.getContentPane().add( panBottombar, BorderLayout.SOUTH );
        
        this.configureFileFilters();
        
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    public void updateHighlighting(){
    	txtQuery.clearKeyWords();
        txtQuery.addKeyWords(keywords, new Color(0xE56717) );
        txtQuery.addKeyWords(operators, new Color(0x0000AA) );
        txtQuery.addKeyWords(defwords, new Color(0x00BB00) );
    }
    
    public void configureFileFilters(){
		queryChooser.setFileFilter( new FileFilter(){

			@Override
			public boolean accept(File file) {
				if (file.isDirectory()) {
		            return true;
		        }
				
				return file.getAbsolutePath().endsWith(".q");
			}

			@Override
			public String getDescription() {
				return "CorpusSearch query files (*.q)";
			}
			
		} );   
		
		defChooser.setFileFilter( new FileFilter(){

			@Override
			public boolean accept(File file) {
				if (file.isDirectory()) {
		            return true;
		        }
				
				return file.getAbsolutePath().endsWith(".def");
			}

			@Override
			public String getDescription() {
				return "CorpusSearch definitions files (*.def)";
			}
			
		} ); 		
    }
    
    /*
     * Event handling section
     */

	public void actionPerformed(ActionEvent event) {
		if( event.getSource() == butRunQuery ){
			controller.runQuery();
		}
		else if ( event.getSource() == butOpenQuery ){
			
	        int returnVal = queryChooser.showOpenDialog(this);	        

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = queryChooser.getSelectedFile();	          
	            controller.openQuery( file );    
	        }	        				        
		}
		else if ( event.getSource() == butNewQuery ){						
			controller.newQuery();
		}
		else if (event.getSource() == butSaveQuery ){
			// save if there is a file already
			this.doSave();
		}
		else if ( event.getSource() == butSaveQueryAs ){
			this.doSaveAs();    			
		}
		else if ( event.getSource() == butOpenDef ){
			
	        int returnVal = defChooser.showOpenDialog(this);	        

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = defChooser.getSelectedFile();	          
	            controller.openDef( file );    
	        }	 	        			
		}
		else if (event.getSource() == butOpenFolder ){
			this.openFolder();
		}
		else if (event.getSource() == butTextEditor ){
			this.openTextEditor();
		}
		else if (event.getSource() == butCopyResults ){
			this.copyToClipboard();
		}
	}
	
	private void openFolder(){
		try {
			String str = FilenameUtils.separatorsToUnix( model.getFileName() );
			str = str.substring(0,str.lastIndexOf("/"));						
			Desktop.getDesktop().open( new File(str) );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}		
	
	private void openTextEditor(){					
		try {
			Desktop.getDesktop().open( model.getResultFile() );
		} catch (IOException e) {
			e.printStackTrace();
		}				
	}
	
	private void copyToClipboard(){
		try {
			File toOpen = model.getResultFile();			
			String resultString = FileUtils.readFileToString(toOpen);
			StringSelection stringSelection = new StringSelection( resultString );
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    clipboard.setContents( stringSelection, this );			
		} catch (Exception e) {
			e.printStackTrace();
		}				
		
	}
	
	private void doSave(){
		if( model.getFileName() != null ){			
			controller.saveQuery();
		}
		else { // otherwise fire up the save as dialog
  			this.doSaveAs();						
		}		
	}
	
	private void doSaveAs(){
        int returnVal = queryChooser.showSaveDialog(this);	        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = queryChooser.getSelectedFile();	          
            controller.saveQueryAs( file );    
        }	  		
	}

	public void queryChanged() {
		txtRootNode.setText( model.getRootNode() );
		txtQuery.setText( model.getQuery() );		
		chkNodesOnly.setSelected( model.getNodesOnly() );
		chkRemoveNodes.setSelected( model.getRemoveNodes() );
		butSaveQuery.setEnabled( model.isModified() );
		if( model.isModified() ){
			butSaveQueryAs.setEnabled(true);
		}
		if( model.getQuery().trim().length() == 0 ){
			butSaveQuery.setEnabled(false);
			butSaveQueryAs.setEnabled(false);
		}
		
		// Construct the message above the query field
		String labString = "Query";		
		if( model.getFileName() != null ){
			labString += " ("+model.getFileName();
			if( model.isModified() ){
				labString += "*";
			}
			labString+=")";
		}		
		else {
			if( model.isModified() ){
				labString += "(*)";
			}					
		}
			
		// Construct the tooltip for the definitions file
		labString += ":";
		labQuery.setText(labString);
		
		String defString = "<html>Select definitions file";
		if( model.getDefinitionsFileName() != null ){
			defString +="<br/>Current: "+model.getDefinitionsFileName();
		}				
		defString+="</html>";
		butOpenDef.setToolTipText(defString);
		
		// load definitions into the highlighting system
		if( model.getDefWords() != null ){
			defwords = model.getDefWords().toArray(new String[model.getDefWords().size()]);
			this.updateHighlighting();
		}
	}

	public void resultChanged() {
		boolean hasResult = (model.getResult().length() != 0 );
		butCopyResults.setEnabled(hasResult);
		butOpenFolder.setEnabled(hasResult);
		butTextEditor.setEnabled(hasResult);
		txtResult.setText( model.getResult() );
	}

	public void keyReleased(KeyEvent event) {
		// it can either be the root node field ...
		if( event.getSource() == txtRootNode ){
			controller.setRootNode( txtRootNode.getText() );
		}
		else { // ... or the query itself
			controller.setQuery( txtQuery.getText() );
		}
	}	
	public void keyPressed(KeyEvent event) {
		if( event.getKeyCode() == java.awt.event.KeyEvent.VK_S ){
			if( event.isControlDown() ){
				this.doSave();
			}
		}
		
	}
	public void keyTyped(KeyEvent event) {}

	public void itemStateChanged(ItemEvent event) {
		if( event.getSource() == chkNodesOnly ){
			controller.setNodesOnly( chkNodesOnly.isSelected() );
		}
		else if( event.getSource() == chkRemoveNodes ){
			controller.setRemoveNodes( chkRemoveNodes.isSelected() );
		}
		
	}

	public void lostOwnership(Clipboard arg0, Transferable arg1) {
		// do nothing
	}		
	
}
