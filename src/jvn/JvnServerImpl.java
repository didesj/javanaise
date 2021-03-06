/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;



public class JvnServerImpl 	
              extends UnicastRemoteObject 
							implements JvnLocalServer, JvnRemoteServer{
	
  // A JVN server is managed as a singleton 
	private static JvnServerImpl js = null;
    private Hashtable<Integer, CacheObject> jvnObjects;
    private JvnRemoteCoord server_coord ;
    private Hashtable<String, Integer> hachNameId;
    private static int nbr_objects =0;
    private int size_cache = 10;
    		
  /**
  * Default constructor
  * @throws JvnException
  **/
	private JvnServerImpl() throws Exception {
		super();
		while(server_coord == null) {
			server_coord = (JvnRemoteCoord) Naming.lookup("//localhost:2045/Coordinateur");
		}
		System.out.println(server_coord);
		this.hachNameId = new Hashtable<String, Integer>();
		this.jvnObjects = new Hashtable<Integer, CacheObject>();
	}
	
  /**
    * Static method allowing an application to get a reference to 
    * a JVN server instance
    * @throws JvnException
    **/
	public synchronized static JvnServerImpl jvnGetServer() {
		if (js == null){
			try {
				js = new JvnServerImpl();
			} catch (Exception e) {
				return null;
			}
		}
		return js;
	}
	
	/**
	* The JVN service is not used anymore
	* @throws JvnException
	**/
	public  void jvnTerminate()
	throws jvn.JvnException {
            try {
				server_coord.jvnTerminate(js);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	} 
	
	/**
	* creation of a JVN object
	* @param o : the JVN object state
	* @throws JvnException
	**/
	public  JvnObject jvnCreateObject(Serializable o)
	throws jvn.JvnException { 
			int id = 0;
			try {
				id = server_coord.jvnGetObjectId();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            JvnObject jvnObject = new ObjectEntryConsistency(id, o);
            return jvnObject; 
	}
	
	/**
	*  Associate a symbolic name with a JVN object
	* @param jon : the JVN object name
	* @param jo : the JVN object 
	* @throws JvnException
	**/
	public  void jvnRegisterObject(String jon, JvnObject jo)
	throws jvn.JvnException {
		System.out.println("jo ?" + jo);
		addObjectsInCache(jo);
		System.out.println("add object");

		try {
			server_coord.jvnRegisterObject(jon, jo, js);
			System.out.println("objet enregistré du coordinateur");

		} catch (RemoteException e) {
			System.out.println("CATCH : "+e.getMessage());

			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//récpérer ID de l'objet 
		int id_objet = jo.jvnGetObjectId();
		
		hachNameId.put(jon,id_objet);
	}
	
	/**
	* Provide the reference of a JVN object beeing given its symbolic name
	* @param jon : the JVN object name
	* @return the JVN object 
	* @throws JvnException
	**/
	public  JvnObject jvnLookupObject(String jon)
	throws jvn.JvnException {
    // to be completed 
			try {
				if(hachNameId.containsValue(jon)) {
					CacheObject obj = jvnObjects.get(hachNameId.get(jon));
					if( ((ObjectEntryConsistency) obj.getJo()).isLock()) {
						System.out.println("ERREUR");
						return obj.getJo();
					}
				}
				System.out.println("Avant return ");
				JvnObject objLookUp = server_coord.jvnLookupObject(jon, js);
				CacheObject co = new CacheObject(objLookUp);
				if(objLookUp != null) {
					addObjectsInCache(objLookUp);
				}
				return objLookUp;
			} catch (RemoteException e) {
				System.out.println("erreur : " + e.getMessage());
				e.printStackTrace();
			}
	
	
		return null;
	}	
	
	
	/**
	* Get a Read lock on a JVN object 
	* @param joi : the JVN object identification
	* @return the current JVN object state
	* @throws  JvnException
	**/
   public Serializable jvnLockRead(int joi)
	 throws JvnException {
		// to be completed
	   Serializable obj = null ; 
		 try {
			obj = server_coord.jvnLockRead(joi, js);
			if(!jvnObjects.containsKey(joi)) {
				addObjectsInCache(new ObjectEntryConsistency(joi, obj));
			}
			jvnObjects.get(joi).setUsed(true);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			obj = jvnObjects.get(joi).getJo().jvnGetObjectState();
		}
        
	return obj;
	}	
	/**
	* Get a Write lock on a JVN object 
	* @param joi : the JVN object identification
	* @return the current JVN object state
	* @throws  JvnException
	**/
   public Serializable jvnLockWrite(int joi)
	 throws JvnException {
		// to be completed 
	   Serializable obj = null;
	   try {
		   	obj = server_coord.jvnLockWrite(joi, this);
		   	if(!jvnObjects.containsKey(joi)) {
				addObjectsInCache(new ObjectEntryConsistency(joi, obj));
			}
		   	jvnObjects.get(joi).setUsed(true);
	} catch (RemoteException e) {
		// TODO Auto-generated catch block
		obj = jvnObjects.get(joi).getJo().jvnGetObjectState();
	}
		return obj;
	}	

	
  /**
	* Invalidate the Read lock of the JVN object identified by id 
	* called by the JvnCoord
	* @param joi : the JVN object id
	* @return void
	* @throws java.rmi.RemoteException,JvnException
	**/
  public void jvnInvalidateReader(int joi)
	throws java.rmi.RemoteException,jvn.JvnException {
	  CacheObject co = jvnObjects.get(joi);
	  co.getJo().jvnInvalidateReader();
	  co.setLastUnLock();
	  co.setUsed(false);
	  
	};
	    
	/**
	* Invalidate the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
  public Serializable jvnInvalidateWriter(int joi)
	throws java.rmi.RemoteException,jvn.JvnException { 
	  CacheObject co = jvnObjects.get(joi);
	  Serializable res = co.getJo().jvnInvalidateWriter();
	  co.setLastUnLock();
	  co.setUsed(false);
	  return res;
	};
	
	/**
	* Reduce the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
   public Serializable jvnInvalidateWriterForReader(int joi)
	 throws java.rmi.RemoteException,jvn.JvnException { 
	   return jvnObjects.get(joi).getJo().jvnInvalidateWriterForReader();
	 };
	 
	 private void addObjectsInCache(JvnObject jo) throws JvnException {
		 if(nbr_objects >= size_cache) {
			 // delete un object
			 Integer coMin = null;
			 int i = 0;
			 for (Enumeration<CacheObject> e = jvnObjects.elements(); e.hasMoreElements();) {
				 if(i == 0) {
					 coMin = e.nextElement().getJo().jvnGetObjectId();
				 }
				 else if(e.nextElement().getLastUnLock().compareTo(jvnObjects.get(coMin).getLastUnLock()) < 0) {
					 coMin = e.nextElement().getJo().jvnGetObjectId();
				 }
			 }
			  jvnObjects.remove(coMin) ;   	 
		 }
		 CacheObject co = new CacheObject(jo);
		 jvnObjects.put(jo.jvnGetObjectId(), co);
		 nbr_objects ++;
	 }
	 

}

 
