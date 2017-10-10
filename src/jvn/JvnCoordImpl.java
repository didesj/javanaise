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
import java.util.List;


public class JvnCoordImpl 	
              extends UnicastRemoteObject 
							implements JvnRemoteCoord{
    
    private int jvnObjectId = 0;
    private List<ObjectCoord> listObjects;
    private List<JvnRemoteServer> listRemoteServer;
    
    public static void main(String args[]){
        try{
            JvnRemoteCoord coordinateur = new JvnCoordImpl();
            
            LocateRegistry.createRegistry(2045);
            Naming.bind("//localhost:2045/Coordinateur", coordinateur);
            
        }
        catch(Exception e){
            System.out.print(e.getMessage());
        }
    }
	

  /**
  * Default constructor
  * @throws JvnException
  **/
	private JvnCoordImpl() throws Exception {
		this.listObjects = new ArrayList<ObjectCoord>();
                this.listRemoteServer = new ArrayList<JvnRemoteServer>();
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
	
    listObjects.add(new ObjectCoord(jon, jo));
	System.out.println("REgistrer Object ");

    listRemoteServer.add(js);
	System.out.println("REgistrer Object add ");

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
    for(ObjectCoord obj : listObjects){
        if(jon.equals(obj.getJon())){
            JvnObject jvnObject = obj.getObj();
            int idJvnObject = jvnObject.jvnGetObjectId();
            if(obj.isServerLockWrite()){
                jvnObject = new ObjectEntryConsistency(idJvnObject, js.jvnInvalidateWriterForReader(idJvnObject));
            }
            else{
                jvnObject = new ObjectEntryConsistency(idJvnObject, jvnObject.jvnGetObjectState());
            }
            System.out.println("Objet trouvé et envoyé");
            return jvnObject;
        }
    }
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
    ObjectCoord obj = findObjectCoordById(joi);
    Serializable objectMAJ = obj.getObj().jvnGetObjectState();
    if(obj.isServerLockWrite()){
    	System.out.println("Lock Read Coord");
        JvnRemoteServer serverLockWrite = obj.getServerGotLockWrite();
        objectMAJ = serverLockWrite.jvnInvalidateWriterForReader(joi);
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
    ObjectCoord obj = findObjectCoordById(joi);
    Serializable objectMAJ = obj.getObj().jvnGetObjectState();
    if(obj.isServerLockWrite()){
    		System.out.println("Je met à jour la donnée !!!");
        JvnRemoteServer serverLockWrite = obj.getServerGotLockWrite();
        objectMAJ = serverLockWrite.jvnInvalidateWriter(joi);
    }
    if(obj.isServerLockRead()){
        for(JvnRemoteServer jrs : obj.getServersGotLockRead()){
            jrs.jvnInvalidateReader(joi);
        }
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
    
    private ObjectCoord findObjectCoordById(int joi) throws JvnException{
    	System.out.println("findObjectCoordById");
        for(ObjectCoord obj : listObjects){
            if(obj.getId() == joi){
            	System.out.println("jvnObject trouvé avec joi : "+joi);
                return obj;
            }
        }
        throw new JvnException("Objet non trouvé !!!");
    }
}
 
 
