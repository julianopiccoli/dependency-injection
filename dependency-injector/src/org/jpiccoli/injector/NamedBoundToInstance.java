package org.jpiccoli.injector;

/**
 * Implementation of the NamedBindingValue which provides the same instance
 * every time the getValue method is called.
 * @author Piccoli
 *
 * @param <T>
 */
class NamedBoundToInstance<T> implements NamedBindingValue<T> {
	
	private T instance;
	
	public NamedBoundToInstance(T instance) {
		this.instance = instance;
	}
	
	@Override
	public T getValue() {
		return instance;
	}

}
