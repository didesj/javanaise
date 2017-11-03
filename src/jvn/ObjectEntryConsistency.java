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
    		//System.out.println("avant jvnLockRead valuOfLock : " + valueOfLock);
        if(valueOfLock == ValueOfLock.NL){
            // TODO : demander droits de la donnée o au coordinateur : jvnLockRead(id, JvnRemoteServer js)
            valueOfLock = ValueOfLock.R;
        		o = JvnServerImpl.jvnGetServer().jvnLockRead(id);
        }
        else if (valueOfLock == ValueOfLock.RC || valueOfLock == ValueOfLock.R){
            valueOfLock = ValueOfLock.R;
        }
        else if (valueOfLock == ValueOfLock.WC || valueOfLock == ValueOfLock.W){
            valueOfLock = ValueOfLock.RWC;
        }
        //System.out.println("après jvnLockRead valuOfLock : " + valueOfLock);
        // TODO : demander dernière version de la donnée o au coordinateur

        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public synchronized void jvnLockWrite() throws JvnException {
    		//System.out.println("avant jvnLockWrite valuOfLock : " + valueOfLock);
        if(valueOfLock == ValueOfLock.NL){
            // TODO : demander droits de la donnée o au coordinateur
           	o = JvnServerImpl.jvnGetServer().jvnLockWrite(id);
            valueOfLock = ValueOfLock.W;
        }
        else if (valueOfLock == ValueOfLock.RC || valueOfLock == ValueOfLock.R){
            // TODO : demander droits de la donnée o au coordinateur
           	o = JvnServerImpl.jvnGetServer().jvnLockWrite(id);
            valueOfLock = ValueOfLock.W;
        }
        else if (valueOfLock == ValueOfLock.WC || valueOfLock == ValueOfLock.W){
            valueOfLock = ValueOfLock.W;
        }
        //System.out.println("après jvnLockWrite valuOfLock : " + valueOfLock);
        // TODO : demander dernière version de la donnée o au coordinateur
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public synchronized void jvnUnLock() throws JvnException {
    		//System.out.println("avant jvnUnLock valuOfLock : " + valueOfLock);
        if (valueOfLock == ValueOfLock.R){
            valueOfLock = ValueOfLock.RC;
        }
        else if (valueOfLock == ValueOfLock.W){
            valueOfLock = ValueOfLock.WC;
        }
        else if (valueOfLock == ValueOfLock.RWC){
            valueOfLock = ValueOfLock.WC;
        }
        //System.out.println("après jvnUnLock valuOfLock : " + valueOfLock);
        notifyAll();
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int jvnGetObjectId() throws JvnException {
        return id;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Serializable jvnGetObjectState() throws JvnException {
        return  o;
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public synchronized void jvnInvalidateReader() throws JvnException {
    		//System.out.println("avant jvnInvalidateReader valuOfLock : " + valueOfLock);
        if (valueOfLock == ValueOfLock.RC){
            valueOfLock = ValueOfLock.NL;
        }
        if(valueOfLock == ValueOfLock.R){
            try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            valueOfLock = ValueOfLock.NL;
        }
        //System.out.println("après jvnInvalidateReader valuOfLock : " + valueOfLock);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public synchronized Serializable jvnInvalidateWriter() throws JvnException {
    		//System.out.println("avant jvnInvalidateWriter valuOfLock : " + valueOfLock);
        if (valueOfLock == ValueOfLock.WC || valueOfLock == ValueOfLock.RC){
            valueOfLock = ValueOfLock.NL;
        }
        if(valueOfLock == ValueOfLock.W || valueOfLock == ValueOfLock.R){
            try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            valueOfLock = ValueOfLock.NL;
        }
        //o = JvnServerImpl.jvnGetServer().jvnLockWrite(id);
        //System.out.println("après jvnInvalidateWriter valuOfLock : " + valueOfLock);
        return o;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
 
    @Override
    public synchronized Serializable jvnInvalidateWriterForReader() throws JvnException {
    		//System.out.println("avant jvnInvalidateWriterForReader valuOfLock : " + valueOfLock);
        if (valueOfLock == ValueOfLock.RWC){
            valueOfLock = ValueOfLock.R;
        }
        if(valueOfLock == ValueOfLock.W){
        		try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            valueOfLock = ValueOfLock.RC;
        }
        if(valueOfLock == ValueOfLock.WC) {
        		valueOfLock = ValueOfLock.RC;
        }
        //o = JvnServerImpl.jvnGetServer().jvnLockRead(id);
        //System.out.println("après jvnInvalidateWriterForReader valuOfLock : " + valueOfLock);
        return o;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
           
}
