package org.jpiccoli.injector.tests;

import javax.inject.Inject;
import javax.inject.Provider;

public class E {

	@Inject
	public static A a;
	
	public static B b;
	
	@Inject
	public static Provider<A> providerA;
	
	public static Provider<B> providerB;
	
	@Inject
	public static void setB(B _b) {
		b = _b;
	}
	
	@Inject
	public static void setProviderB(Provider<B> _providerB) {
		providerB = _providerB;
	}
	
}
