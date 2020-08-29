package org.jpiccoli.injector;

/**
 * Implementation of NamedBindingValue which creates objects of a specific
 * class each time the getValue method is called.
 * @author Piccoli
 *
 * @param <T>
 * @param <S>
 */
class NamedBoundToImplementation<T, S extends T> implements NamedBindingValue<T> {

	private Injector injector;
	private Class<S> implementationClass;
	
	public NamedBoundToImplementation(Injector injector, Class<S> implementationClass) {
		this.injector = injector;
		this.implementationClass = implementationClass;
	}
	
	@Override
	public T getValue() throws ReflectiveOperationException {
		return injector.inject(implementationClass);
	}

}
