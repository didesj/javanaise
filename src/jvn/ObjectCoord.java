/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jvn;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author juliendides
 */
public class ObjectCoord {
    
    private String jon;
    private JvnObject obj;
    private JvnRemoteServer serverGotLockWrite;
    private List<JvnRemoteServer> serversGotLockRead;

    public ObjectCoord(String jon, JvnObject obj) {
        this.jon = jon;
        this.obj = obj;
        this.serversGotLockRead = new ArrayList();
    }
    
    public String getJon(){
        return this.jon;
    }

    public JvnObject getObj() {
        return obj;
    }
    
    public int getId() throws JvnException{
        return obj.jvnGetObjectId();
    }

    public void setObj(JvnObject obj) {
        this.obj = obj;
    }
    
    public boolean isServerLockWrite(){
        if(serverGotLockWrite == null){
            return false;
        }
        return true;
    }
    
    public boolean isServerLockRead(){
        return (!serversGotLockRead.isEmpty());
    }

    public JvnRemoteServer getServerGotLockWrite() {
        return serverGotLockWrite;
    }
    
    public void SetServerGotLockWriteNull() {
        serverGotLockWrite = null;
    }   
    
    public void setServerGotLockWrite(JvnRemoteServer serverGotLockWrite) {
        this.serverGotLockWrite = serverGotLockWrite;
    }

    public List<JvnRemoteServer> getServersGotLockRead() {
        return serversGotLockRead;
    }

    public void addServersGotLockRead(JvnRemoteServer serverGotLockRead) {
        this.serversGotLockRead.add(serverGotLockRead);
    }
    
    public void setServersGotLockReadNull() {
        this.serversGotLockRead.removeAll(serversGotLockRead);
    }
}
