package org.jpiccoli.injector;

/**
 * Interface of the handler for the Scope annotations
 * @author Piccoli
 *
 */
interface ScopeHandler {
	
	/**
	 * Get an instance which corresponds to the specified class
	 * from this scope. May return null if no instance of the
	 * corresponding class was set yet in this scope.
	 * @param c Class of instance to be injected.
	 * @return The corresponding instance or null in case no
	 * object was set in this scope before using the putValue
	 * method.
	 */
	public <T> T getValue(Class<T> c);
	
	/**
	 * Stores the specified instance in this scope.
	 * It can be retrieved later using the getValue
	 * method.
	 * @param instance Instance to be stored.
	 */
	public <T> void putValue(T instance);

}
