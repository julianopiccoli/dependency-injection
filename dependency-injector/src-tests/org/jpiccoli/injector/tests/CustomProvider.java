package org.jpiccoli.injector.tests;

import javax.inject.Provider;

public class CustomProvider implements Provider<C> {
	
	private boolean providerCalled;
	
	@Override
	public C get() {
		providerCalled = true;
		return new C();
	}
	
	public boolean isProviderCalled() {
		return providerCalled;
	}

}
