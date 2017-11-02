package jvn;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import irc.Sentence;

public class JvnProxy implements InvocationHandler{
	
	private JvnObject jo;
	private JvnProxy(String name, Object obj) {
		try {
			JvnServerImpl js = JvnServerImpl.jvnGetServer();
			System.out.println("ca marche !!!JvnProxy 1");
			JvnObject jo = js.jvnLookupObject(name);
			System.out.println("ca marche !!!JvnProxy 2");
			
			if (jo == null) {
				jo = js.jvnCreateObject((Serializable) obj);
				// after creation, I have a write lock on the object
				jo.jvnUnLock();
				js.jvnRegisterObject(name, jo);
	
			}
			
			this.jo = jo;
			
		} catch(Exception e) {
			System.out.println("JvnObject problem : " + e.getMessage());
		}
			
	}
	
	public synchronized static Object getOrNewInstance (String name, Object obj) {
		return Proxy.newProxyInstance(
				obj.getClass().getClassLoader(),
				obj.getClass().getInterfaces(),
				new JvnProxy(name, obj));
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = null;
		try {
			if(method.isAnnotationPresent(LockType.class)) {
				LockType lockType = method.getAnnotation(LockType.class);
				//System.out.println("La méthode " + method.getName() + " a comme lock \"" + lockType.lockType() + "\"");
				if(lockType.lockType().equals("read")) {
					jo.jvnLockRead();
				}
				else if(lockType.lockType().equals("write")) {
					jo.jvnLockWrite();
				}
				else {
					// Erreur à gérer
					jo.jvnLockWrite();
				}
			}
			else {
				// Erreur à gérer
				jo.jvnLockWrite();
			}
			result = method.invoke(jo.jvnGetObjectState(), args);
			jo.jvnUnLock();
			
		} catch (Exception e){
			
		}
		return result;
	}

}
