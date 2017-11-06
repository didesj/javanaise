/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jvn;

import java.io.Serializable;

/**
 *
 * @author juliendides
 */
public class ObjectEntryConsistency implements JvnObject{

    private ValueOfLock valueOfLock;
    private int id;
    private Serializable o;
    public ObjectEntryConsistency(int id, Serializable o) {
        this.id = id;
        this.o = o;
        this.valueOfLock = ValueOfLock.NL;
    }

    public boolean isLock() {
    	return (valueOfLock  != ValueOfLock.NL);
    }
    
    @Override
    public synchronized void jvnLockRead() throws JvnException {
    	// R, W: err
        if(valueOfLock == ValueOfLock.NL){
            valueOfLock = ValueOfLock.R;
        		o = JvnServerImpl.jvnGetServer().jvnLockRead(id);
        }
        else if (valueOfLock == ValueOfLock.RC || valueOfLock == ValueOfLock.R){
            valueOfLock = ValueOfLock.R;
        }
        else if (valueOfLock == ValueOfLock.WC || valueOfLock == ValueOfLock.W){
            valueOfLock = ValueOfLock.RWC;
        }
    }

    @Override
    public synchronized void jvnLockWrite() throws JvnException {
        if(valueOfLock == ValueOfLock.NL){
           	o = JvnServerImpl.jvnGetServer().jvnLockWrite(id);
            valueOfLock = ValueOfLock.W;
        }
        else if (valueOfLock == ValueOfLock.RC || valueOfLock == ValueOfLock.R){
           	o = JvnServerImpl.jvnGetServer().jvnLockWrite(id);
            valueOfLock = ValueOfLock.W;
        }
        else if (valueOfLock == ValueOfLock.WC || valueOfLock == ValueOfLock.W){
            valueOfLock = ValueOfLock.W;
        }
    }

    @Override
    public synchronized void jvnUnLock() throws JvnException {
        if (valueOfLock == ValueOfLock.R){
            valueOfLock = ValueOfLock.RC;
        }
        else if (valueOfLock == ValueOfLock.W){
            valueOfLock = ValueOfLock.WC;
        }
        else if (valueOfLock == ValueOfLock.RWC){
            valueOfLock = ValueOfLock.WC;
        }
        notifyAll();
    }

    @Override
    public int jvnGetObjectId() throws JvnException {
        return id;
    }

    @Override
    public Serializable jvnGetObjectState() throws JvnException {
        return  o;
    }

    @Override
    public synchronized void jvnInvalidateReader() throws JvnException {
        if (valueOfLock == ValueOfLock.RC){
            valueOfLock = ValueOfLock.NL;
        }
        if(valueOfLock == ValueOfLock.R){
            try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            valueOfLock = ValueOfLock.NL;
        }
    }

    @Override
    public synchronized Serializable jvnInvalidateWriter() throws JvnException {
        if (valueOfLock == ValueOfLock.WC || valueOfLock == ValueOfLock.RC){
            valueOfLock = ValueOfLock.NL;
        }
        if(valueOfLock == ValueOfLock.W || valueOfLock == ValueOfLock.R){
            try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            valueOfLock = ValueOfLock.NL;
        }
        return o;
    }
 
    @Override
    public synchronized Serializable jvnInvalidateWriterForReader() throws JvnException {
        if (valueOfLock == ValueOfLock.RWC){
            valueOfLock = ValueOfLock.R;
        }
        if(valueOfLock == ValueOfLock.W){
        		try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            valueOfLock = ValueOfLock.RC;
        }
        if(valueOfLock == ValueOfLock.WC) {
        		valueOfLock = ValueOfLock.RC;
        }
        return o;
    }
           
}
