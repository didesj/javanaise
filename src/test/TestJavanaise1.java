package test;


import java.io.Serializable;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import irc.Sentence;
import jvn.JvnCoordImpl;
import jvn.JvnObject;
import jvn.JvnServerImpl;

public class TestJavanaise1 {
	
	Thread coord;

	@Before
	public void creationCoord() {
		coord = new Thread() {
			public void run() {
				JvnCoordImpl.main(null);
			}
		};
		coord.start();
	}
	
	@Test
	public void testTransfertDunObjet() {
		Thread client1 = new Thread() {
			public void run() {
				try { 
					// initialize JVN
					JvnServerImpl js = JvnServerImpl.jvnGetServer();
					
					JvnObject jo = js.jvnCreateObject((Serializable) new Sentence());
					System.out.println("objet crée");
					// after creation, I have a write lock on the object
					jo.jvnUnLock();
					System.out.println(jo);
					js.jvnRegisterObject("ObjetPartage", jo);
					System.out.println("objet enregistré");

					// create the graphical part of the Chat application

					 jo.jvnLockWrite();
					 ((Sentence)jo.jvnGetObjectState()).write("bonjour");
					 jo.jvnUnLock();
					 
			   } catch (Exception e) {
				   System.out.println("ObjetPartage problem : " + e.getMessage());
			   }
			}
		};
		
		Thread client2 = new Thread() {
			public void run() {
				try { 
					// initialize JVN
					JvnServerImpl js = JvnServerImpl.jvnGetServer();
					
					JvnObject jo = null;
					
					while(jo == null) {
						jo = js.jvnLookupObject("ObjetPartage");
					}

					 jo.jvnLockRead();
					 String res = ((Sentence)jo.jvnGetObjectState()).read();
					 jo.jvnUnLock();
					 
					 Assert.assertEquals("bonjour",res);
					 
			   } catch (Exception e) {
				   
			   }
			}
		};
		
		client1.start();
		client2.start();
	}
	


}
