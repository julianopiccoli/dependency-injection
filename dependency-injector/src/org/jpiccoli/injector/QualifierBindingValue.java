package org.jpiccoli.injector;

/**
 * Implementation of the behaviour corresponding to a given QualifierBindingKey.
 * Consider the following field declaration:
 * 
 * <code>
 *  
 *  {@literal @}Inject
 *  {@literal @}Named("impl")
 * 	private MyInterface myInterface;
 * 
 * </code>
 * 
 * The injection of the field "myImplementation" may be done in two forms:
 * either by creating a new instance of a bound class or by providing
 * a bound instance. Both ways are implemented in classes which implement
 * this interface.
 * 
 * @author Piccoli
 *
 * @param <T>
 */
interface QualifierBindingValue<T> {
	
	public T getValue() throws ReflectiveOperationException;

}
