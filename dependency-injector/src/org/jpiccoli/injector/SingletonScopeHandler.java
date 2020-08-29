package org.jpiccoli.injector;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of the Singleton scope.
 * @author Piccoli
 *
 */
class SingletonScopeHandler implements ScopeHandler {

	private static Map<Class<?>, Object> singletonInstances;
	private static Lock singletonLock;
	
	static {
		singletonInstances = new HashMap<Class<?>, Object>();
		singletonLock = new ReentrantLock();
	}
	
	@Override
	public <T> T getValue(Class<T> c) {
		// A singleton type should be instantiated only once. The lock
		// mechanism used below prevents two concurrent threads from creating
		// two different instances of a singleton type at the same type.
		singletonLock.lock();
		return (T) singletonInstances.get(c);
	}
	
	public <T> void putValue(T instance) {
		if (instance != null) {
			singletonInstances.put(instance.getClass(), instance);
		}
		singletonLock.unlock();
	}

}
