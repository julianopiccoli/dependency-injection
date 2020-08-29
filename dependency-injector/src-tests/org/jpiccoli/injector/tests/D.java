package org.jpiccoli.injector.tests;

import javax.inject.Inject;
import javax.inject.Provider;

public class D {

	@Inject
	private Provider<A> providerA1;
	
	private Provider<A> providerA2;
	
	private Provider<A> providerA3;
	
	@Inject
	public D(Provider<A> providerA2) {
		this.providerA2 = providerA2;
	}
	
	public Provider<A> getProviderA1() {
		return providerA1;
	}
	
	public Provider<A> getProviderA2() {
		return providerA2;
	}
	
	public Provider<A> getProviderA3() {
		return providerA3;
	}
	
	@Inject
	public void setProviderA3(Provider<A> providerA3) {
		this.providerA3 = providerA3;
	}
	
}
