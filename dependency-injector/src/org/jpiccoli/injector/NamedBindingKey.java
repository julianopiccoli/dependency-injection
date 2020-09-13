package org.jpiccoli.injector;

import javax.inject.Named;

/**
 * A type of QualifierBindingKey which represents bindings of the {@literal @}Named annotation.
 * For example, the following field declaration corresponds to one instance of this class:
 * 
 * 	<code>
 *  {@literal @}Named("impl")
 * 	private MyImplementation myImplementation;
 *  </code>
 *  
 *  In the case of this example, one could build an instance of this
 *  class using the following code:
 *  
 *  <code>
 *  
 *  new NamedBindingKey&lt;MyImplementation&gt;(MyImplementation.class, "impl");
 *  
 *  </code>
 * 
 * @author Piccoli
 *
 * @param <T>
 */
class NamedBindingKey<T> extends QualifierBindingKey<Named, T> {
	
	private String name;
	
	public NamedBindingKey(Class<T> type, String name) {
		super(Named.class, type);
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	protected String buildIdentificationString() {
		return super.buildIdentificationString() + "_name param: " + name;
	}
	
}
