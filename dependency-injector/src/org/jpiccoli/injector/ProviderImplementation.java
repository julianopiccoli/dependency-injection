package org.jpiccoli.injector;

import javax.inject.Provider;

/**
 * Implementation of the Provider interface.
 * @author Piccoli
 *
 * @param <T>
 */
class ProviderImplementation<T> implements Provider<T> {

	private Injector injector;
	private Class<T> type;
	
	ProviderImplementation(Injector injector, Class<T> type) {
		this.injector = injector;
		this.type = type;
	}
	
	@Override
	public T get() {
		try {
			return injector.inject(type);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

}
