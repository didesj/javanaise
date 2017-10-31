package irc;

import jvn.LockType;

public interface ISentence {
	
	@LockType(lockType = "write")
	public void write(String text);
	
	@LockType(lockType = "read")
	public String read();
}
