package test;

import java.util.Random;

import irc.ISentence;
import irc.Sentence;
import jvn.JvnProxy;

public class SimulClient {
	private static int idClient;
	private static int nbObjects;

	public synchronized static void main(String args []) {
		idClient = Integer.parseInt(args[0]);
		nbObjects = Integer.parseInt(args[1]);
		ISentence[] tabObjects = new ISentence[nbObjects];
		for(int i = 0; i < nbObjects; i++) {
			tabObjects[i] = (ISentence) JvnProxy.getOrNewInstance("IRC"+i, new Sentence());
		}
		Random rand1;
		Random rand2;
		int randReadWrite;
		int randIRC;
		int randSleep;
		for(int j = 0; j< 1000; j++) {
			try {
				rand1 = new Random();
				randSleep = rand1.nextInt(900) + 100;
				Thread.sleep(randSleep);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rand1 = new Random();
			rand2 = new Random();
			randReadWrite = rand1.nextInt(2);
			randIRC = rand2.nextInt(nbObjects);
			if(randReadWrite == 1) {
				tabObjects[randIRC].write("IRC"+randIRC+" : test de " + idClient);
				System.out.println("write : \"" + idClient + " test de " + idClient + ". sur IRC"+randIRC + "\"");
			}
			
			if(randReadWrite ==0) {
				System.out.println(idClient + "IRC"+randIRC+" read : \"" + tabObjects[randIRC].read() +"\"");
			}

		}
	}
}
