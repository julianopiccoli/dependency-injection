package org.jpiccoli.injector;

/**
 * Class used to create keys for Maps or Sets. Each key corresponds to a specific
 * binding of a {@literal @}Qualifier with a given class. 
 * 
 * 	<code>
 *  {@literal @}MyQualifier
 * 	private MyInterface myInterface
 *  </code>
 *  
 *  In the case of this example, one could build an instance of this
 *  class using the following code:
 *  
 *  <code>
 *  
 *  new QualifierBindingKey&lt;MyQualifier, MyInterface&gt;(MyQualifier.class, MyInterface.class);
 *  
 *  </code>
 *  
 *  MyQualifier must be an annotation type which is itself annotated with the {@literal @}Qualifier annotation.
 * 
 * @author Piccoli
 *
 * @param <T>
 */
public class QualifierBindingKey<Q, T> {
	
	private Class<Q> qualifier;
	private Class<T> type;
	private String identificationString;
	private int hashCode;
	
	public QualifierBindingKey(Class<Q> qualifier, Class<T> type) {
		this.qualifier = qualifier;
		this.type = type;
		identificationString = buildIdentificationString();
		hashCode = identificationString.hashCode();
	}
	
	public Class<Q> getQualifier() {
		return qualifier;
	}
	
	public Class<T> getType() {
		return type;
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof QualifierBindingKey) {
			QualifierBindingKey<?, ?> other = (QualifierBindingKey<?, ?>) obj;
			return other.identificationString.equals(identificationString);
		}
		return false;
	}
	
	protected String buildIdentificationString() {
		return qualifier.getName() + "_" + type.getName() + "_";
	}

}
