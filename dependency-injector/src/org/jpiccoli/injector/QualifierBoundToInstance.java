package org.jpiccoli.injector;

/**
 * Implementation of the QualifierBindingValue which provides the same instance
 * every time the getValue method is called.
 * @author Piccoli
 *
 * @param <T>
 */
class QualifierBoundToInstance<T> implements QualifierBindingValue<T> {
	
	private T instance;
	
	public QualifierBoundToInstance(T instance) {
		this.instance = instance;
	}
	
	@Override
	public T getValue() {
		return instance;
	}

}
