package org.jpiccoli.injector;

/**
 * Implementation of QualifierBindingValue which creates objects of a specific
 * class each time the getValue method is called.
 * @author Piccoli
 *
 * @param <T>
 * @param <S>
 */
class QualifierBoundToImplementation<T, S extends T> implements QualifierBindingValue<T> {

	private Injector injector;
	private Class<S> implementationClass;
	
	public QualifierBoundToImplementation(Injector injector, Class<S> implementationClass) {
		this.injector = injector;
		this.implementationClass = implementationClass;
	}
	
	@Override
	public T getValue() throws ReflectiveOperationException {
		return injector.inject(implementationClass);
	}

}
