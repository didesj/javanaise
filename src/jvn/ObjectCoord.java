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
    
    private JvnObject obj;
    private JvnRemoteServer serverGotLockWrite;
    private List<JvnRemoteServer> serversGotLockRead;

    public ObjectCoord(JvnObject obj) {
        this.obj = obj;
        this.serversGotLockRead = new ArrayList();
    }

    public JvnObject getObj() {
        return obj;
    }

    public void setObj(JvnObject obj) {
        this.obj = obj;
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
