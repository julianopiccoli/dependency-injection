package org.jpiccoli.injector;

/**
 * Class used to create keys for Maps or Sets. Each key corresponds to a specific
 * pair of Class annotated with the Named annotation. For example, the following
 * field declaration corresponds to one instance of this class:
 * 
 * 	<code>
 *  @Named("impl")
 * 	private MyImplementation myImplementation
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
class NamedBindingKey<T> {
	
	private Class<T> type;
	private String name;
	private int hashCode;
	
	public NamedBindingKey(Class<T> type, String name) {
		this.type = type;
		this.name = name;
		String hashCodeString = type.getName() + "_" + name;
		hashCode = hashCodeString.hashCode();
	}
	
	public Class<T> getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NamedBindingKey) {
			NamedBindingKey<?> other = (NamedBindingKey<?>) obj;
			return other.getType().equals(type) && other.getName().equals(name);
		}
		return false;
	}
	
}
