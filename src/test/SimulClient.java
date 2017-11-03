package test;

import java.util.Random;

import irc.ISentence;
import irc.Sentence;
import jvn.JvnProxy;

public class SimulClient {
	private static int i;

	public synchronized static void main(String args []) {
		i = Integer.parseInt(args[0]);
		ISentence s1 = (ISentence) JvnProxy.getOrNewInstance("IRC1", new Sentence());
		ISentence s2 = (ISentence) JvnProxy.getOrNewInstance("IRC2", new Sentence());
		Random rand1;
		Random rand2;
		int randReadWrite;
		int randIRC1_2;
		for(int j = 0; j< 1000; j++) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rand1 = new Random();
			rand2 = new Random();
			randReadWrite = rand1.nextInt(2);
			randIRC1_2 = rand2.nextInt(2);
			if(randReadWrite == 1) {
				if(randIRC1_2 == 1) {
					s1.write("IRC1 : test de " + i);
					System.out.println( i + " test de " + i + ". sur IRC1");
				}
				else if(randIRC1_2 == 0) {
					s2.write("IRC2 : test de " + i);
					System.out.println( i + " test de " + i + ". sur IRC2");
				}
			}
			
			if(randReadWrite ==0) {
				if(randIRC1_2 == 1) {
					System.out.println(i + "IRC1 read : " + s1.read());
				}
				else if(randIRC1_2 == 0) {
					System.out.println(i + "IRC2 read : " + s2.read());
				}
			}

		}
	}
}
