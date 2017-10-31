/***
 * Sentence class : used for representing the text exchanged between users
 * during a chat application
 * Contact: 
 *
 * Authors: 
 */

package irc;

public class Sentence implements java.io.Serializable, ISentence {
	String 		data;
  
	public Sentence() {
		data = new String("");
	}
	
	@Override
	public void write(String text) {
		data = text;
	}
	
	@Override
	public String read() {
		return data;	
	}
	
}