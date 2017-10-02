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

    private ValueOfLock valueOfLock = ValueOfLock.NL;
    private int id;
    private Serializable o;

    public ObjectEntryConsistency(int id, Serializable o) {
        this.id = id;
        this.o = o;
    }

    @Override
    public void jvnLockRead() throws JvnException {
        if(valueOfLock == ValueOfLock.NL){
            // TODO : demander droits de la donnée o au coordinateur : jvnLockRead(id, JvnRemoteServer js)
            valueOfLock = ValueOfLock.R;
        }
        else if (valueOfLock == ValueOfLock.RC || valueOfLock == ValueOfLock.R){
            valueOfLock = ValueOfLock.R;
        }
        else if (valueOfLock == ValueOfLock.WC || valueOfLock == ValueOfLock.W){
            valueOfLock = ValueOfLock.RWC;
        }
        // TODO : demander dernière version de la donnée o au coordinateur
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void jvnLockWrite() throws JvnException {
        if(valueOfLock == ValueOfLock.NL){
            // TODO : demander droits de la donnée o au coordinateur
            valueOfLock = ValueOfLock.W;
        }
        else if (valueOfLock == ValueOfLock.RC || valueOfLock == ValueOfLock.R){
            // TODO : demander droits de la donnée o au coordinateur
            valueOfLock = ValueOfLock.W;
        }
        else if (valueOfLock == ValueOfLock.WC || valueOfLock == ValueOfLock.W){
            valueOfLock = ValueOfLock.W;
        }
        // TODO : demander dernière version de la donnée o au coordinateur
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void jvnUnLock() throws JvnException {
        if (valueOfLock == ValueOfLock.R){
            valueOfLock = ValueOfLock.RC;
        }
        else if (valueOfLock == ValueOfLock.W){
            valueOfLock = ValueOfLock.WC;
        }
        else if (valueOfLock == ValueOfLock.RWC){
            valueOfLock = ValueOfLock.WC;
        }
        //notifyAll()
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
    public void jvnInvalidateReader() throws JvnException {
        if (valueOfLock == ValueOfLock.RC){
            valueOfLock = ValueOfLock.NL;
        }
        if(valueOfLock == ValueOfLock.R){
            //wait();
            valueOfLock = ValueOfLock.NL;
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Serializable jvnInvalidateWriter() throws JvnException {
        if (valueOfLock == ValueOfLock.WC){
            valueOfLock = ValueOfLock.NL;
        }
        if(valueOfLock == ValueOfLock.W){
            //wait();
            valueOfLock = ValueOfLock.NL;
        }
        return o;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Serializable jvnInvalidateWriterForReader() throws JvnException {
        if (valueOfLock == ValueOfLock.RWC){
            valueOfLock = ValueOfLock.R;
        }
        if(valueOfLock == ValueOfLock.W){
            //wait();
            valueOfLock = ValueOfLock.R;
        }
        return o;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public Object clone(){     
        // TODO 
        return null;
    }

           
}
