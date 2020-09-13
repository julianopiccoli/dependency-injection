package org.jpiccoli.injector;

import javax.inject.Provider;

/**
 * A provider which creates instances using a QualifierBindingValue.
 * Used for providers declarations annotated with a {@literal @}Qualifier.
 * Example:
 * 
 * {@literal @}Named("myValue") Provider&lt;MyClass&gt; myProvider;
 * 
 * @author Piccoli
 *
 * @param <T>
 */
public class QualifiedProviderImplementation<T> implements Provider<T> {

	private QualifierBindingValue<T> bindingValue;
	
	public QualifiedProviderImplementation(QualifierBindingValue<T> bindingValue) {
		this.bindingValue = bindingValue;
	}
	
	@Override
	public T get() {
		try {
			return bindingValue.getValue();
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

}
