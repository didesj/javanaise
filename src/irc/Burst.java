package irc;

import jvn.JvnObject;
import jvn.JvnServerImpl;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//import jvn.JvnException;

public class Burst {


	public static void main(String argv[]) {
		int nbr_client = 5;
		ExecutorService executor = Executors.newFixedThreadPool(nbr_client);
		//nbr_client = Integer.parseInt(argv[0]);
		try {
		for(int i = 0; i< nbr_client;i ++) {
			System.out.println("!!!");

			ThreadClient t = new ThreadClient();
			executor.execute(t);
		}
		}catch(Exception e){
			
		}
	}
	
}
 class ThreadClient extends Thread{
	 JvnObject obj;
	 JvnServerImpl js ;
		public ThreadClient() {
			try {
			
			 js = JvnServerImpl.jvnGetServer();
				//JvnObject jo ;

			  obj = js.jvnLookupObject("IRC");
			
			if(obj == null) {
				obj = js.jvnCreateObject((Serializable) new Sentence());
				obj.jvnUnLock();
				js.jvnRegisterObject("IRC", obj);
				
			}
			//sentence_obj = obj;
		
		}catch(Exception e){
			}
		}
		@Override 
		public void run() {
			Random rand = new Random();
			int test;
			try {
			while(true) {
				test = rand.nextInt(2);
				if(test == 1) {
					obj.jvnLockWrite();
					((Sentence)obj.jvnGetObjectState()).write(String.valueOf(rand.nextInt(1000)));
					obj.jvnUnLock();	
					
					System.out.println("IRC obj Write : " + ((Sentence) (obj.jvnGetObjectState())).read());
				}
				
				if(test ==0) {
					obj.jvnLockRead();
					String s = ((Sentence)obj.jvnGetObjectState()).read();
					obj.jvnUnLock();
					
					 System.out.println("IRC obj Got : " +  s);
				}

			}
			}catch(Exception e){
				System.out.println(" Erreur: " + e.getMessage() + " / " + e.getClass());
			}
		}
	
} 

