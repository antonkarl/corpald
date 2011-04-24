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

import java.io.IOException;
import java.io.InputStream;

class InputStreamHandler
extends Thread
{
/**
 * Stream being read
 */
		
private InputStream m_stream;
		
/**
 * The StringBuffer holding the captured output
 */
		
private StringBuffer m_captureBuffer;
		
/**
 * Constructor. 
 * 
 * @param 
 */
		
InputStreamHandler( StringBuffer captureBuffer, InputStream stream )
{
 m_stream = stream;
 m_captureBuffer = captureBuffer;
 start();
}
		
/**
 * Stream the data.
 */
		
public void run()
{
 try
 {
  int nextChar;
  while( (nextChar = m_stream.read()) != -1 )
  {
   m_captureBuffer.append((char)nextChar);
  }
 }
 catch( IOException ioe )
 {
 }
}
}