/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.rmi.server.UnicastRemoteObject;
import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class JvnCoordImpl 	
              extends UnicastRemoteObject 
							implements JvnRemoteCoord{
    
    private int jvnObjectId = 0;
    private Hashtable<Integer, ObjectCoord> listObjectsById;
    private Hashtable<String, ObjectCoord> listObjectsByJon;
    
    public static void main(String args[]){
        try{
            JvnRemoteCoord coordinateur = new JvnCoordImpl();
            
            LocateRegistry.createRegistry(2045);
            Naming.bind("//localhost:2045/Coordinateur", coordinateur);
            
        }
        catch(Exception e){
            System.out.print("erreur :"+e.getMessage());
        }
        System.out.println("coord cree");
    }
	

  /**
  * Default constructor
  * @throws JvnException
  **/
	private JvnCoordImpl() throws Exception {
		this.listObjectsById = new Hashtable<Integer, ObjectCoord>();
		this.listObjectsByJon = new Hashtable<String, ObjectCoord>();
	}

  /**
  *  Allocate a NEW JVN object id (usually allocated to a 
  *  newly created JVN object)
  * @throws java.rmi.RemoteException,JvnException
  **/
  public int jvnGetObjectId()
  throws java.rmi.RemoteException,jvn.JvnException {
    return jvnObjectId++;
  }
  
  /**
  * Associate a symbolic name with a JVN object
  * @param jon : the JVN object name
  * @param jo  : the JVN object 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the JVNServer
  * @throws java.rmi.RemoteException,JvnException
  **/
  public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js)
  throws java.rmi.RemoteException,jvn.JvnException{
    // TODO : vérifier si le nom est unique
	ObjectCoord objCoord = new ObjectCoord(jo);
	listObjectsById.put(jo.jvnGetObjectId(), objCoord);
	listObjectsByJon.put(jon, objCoord);
    //listObjects.add(new ObjectCoord(jon, jo));
	System.out.println("Registrer Object ");

  }
  
  
  /**
  * Get the reference of a JVN object managed by a given JVN server 
  * @param jon : the JVN object name
  * @param js : the remote reference of the JVNServer
  * @throws java.rmi.RemoteException,JvnException
  **/
  public JvnObject jvnLookupObject(String jon, JvnRemoteServer js)
  throws java.rmi.RemoteException,jvn.JvnException{
	System.out.println("Object cherché "+jon);
    if(listObjectsByJon.containsKey(jon)){
    		ObjectCoord obj = listObjectsByJon.get(jon);
        JvnObject jvnObject = obj.getObj();
        int idJvnObject = jvnObject.jvnGetObjectId();
        if(obj.isServerLockWrite()){
            jvnObject = new ObjectEntryConsistency(idJvnObject, obj.getServerGotLockWrite().jvnInvalidateWriterForReader(idJvnObject));
            obj.setObj(jvnObject);
            obj.addServersGotLockRead(obj.getServerGotLockWrite());
            obj.setServerGotLockWriteNull();
        }
        System.out.println("Objet trouvé et envoyé");
        return jvnObject;
    }
    System.out.println("lookup termine dans coord et Objet non trouvé !");
    return null;
  }
  
  /**
  * Get a Read lock on a JVN object managed by a given JVN server 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the server
  * @return the current JVN object state
  * @throws java.rmi.RemoteException, JvnException
  **/
   public Serializable jvnLockRead(int joi, JvnRemoteServer js)
   throws java.rmi.RemoteException, JvnException{
		System.out.println("jvnLockRead du coordianteur");
    ObjectCoord obj = listObjectsById.get(joi);
    Serializable objectMAJ = obj.getObj().jvnGetObjectState();
    if(obj.isServerLockWrite()){
    		System.out.println("Lock Read Coord");
        JvnRemoteServer serverLockWrite = obj.getServerGotLockWrite();
        System.out.println("le server demandeur == server avec le lock ? : " + (js == serverLockWrite));
        objectMAJ = serverLockWrite.jvnInvalidateWriterForReader(joi);
        obj.addServersGotLockRead(serverLockWrite);
        obj.setServerGotLockWriteNull();
        obj.setObj(new ObjectEntryConsistency(joi, objectMAJ));
    }
    obj.addServersGotLockRead(js);
    return objectMAJ;
   }

  /**
  * Get a Write lock on a JVN object managed by a given JVN server 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the server
  * @return the current JVN object state
  * @throws java.rmi.RemoteException, JvnException
  **/
   public Serializable jvnLockWrite(int joi, JvnRemoteServer js)
   throws java.rmi.RemoteException, JvnException{
	   System.out.println("jvnLockWrite du coordinateur");
    ObjectCoord obj = listObjectsById.get(joi);
    Serializable objectMAJ = obj.getObj().jvnGetObjectState();
    if(obj.isServerLockWrite()){
    		System.out.println("Je met à jour la donnée !!!");
        JvnRemoteServer serverLockWrite = obj.getServerGotLockWrite();
        objectMAJ = serverLockWrite.jvnInvalidateWriter(joi);
        obj.setServerGotLockWriteNull();
        obj.setObj(new ObjectEntryConsistency(joi, objectMAJ));
    }
    if(obj.isServerLockRead()){
        for(JvnRemoteServer jrs : obj.getServersGotLockRead()){
        		// System.out.println("le serveur qui veux le lockWrite veux s'invalider e lockRead : "+ !(jrs.equals(js))); 
        		if(!(jrs.equals(js))) {
        			jrs.jvnInvalidateReader(joi);
        		}
        }
        obj.setServersGotLockReadNull();
    }
    obj.setServerGotLockWrite(js);
    return objectMAJ;
   }

	/**
	* A JVN server terminates
	* @param js  : the remote reference of the server
	* @throws java.rmi.RemoteException, JvnException
	**/
    public void jvnTerminate(JvnRemoteServer js)
	 throws java.rmi.RemoteException, JvnException {
	 // TODO : to be completed
    }
}
 
 
