package jvn;

import java.util.Date;

public class CacheObject {
	private Boolean used;
	private Date lastUnLock;
	private JvnObject jo;
	
	public CacheObject(JvnObject jo) {
		used = false;
		lastUnLock = new Date();
		this.jo = jo;
	}

	public Boolean isUsed() {
		return used;
	}

	public void setUsed(Boolean used) {
		this.used = used;
	}

	public Date getLastUnLock() {
		return lastUnLock;
	}

	public void setLastUnLock() {
		this.lastUnLock = new Date();
	}

	public JvnObject getJo() {
		return jo;
	}
	 
}
