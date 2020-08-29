package org.jpiccoli.injector.tests;

import javax.inject.Inject;
import javax.inject.Provider;

public class AImplProviderContainer {

	@Inject
	private Provider<AInterface> test1;
	
	private Provider<AInterface> test2;
	
	private Provider<AInterface> test3;
	
	@Inject
	public AImplProviderContainer(Provider<AInterface> test2) {
		this.test2 = test2;
	}
	
	public Provider<AInterface> getTest1() {
		return test1;
	}
	
	public Provider<AInterface> getTest2() {
		return test2;
	}
	
	public Provider<AInterface> getTest3() {
		return test3;
	}
	
	@Inject
	public void setTest3(Provider<AInterface> test3) {
		this.test3 = test3;
	}
	
}
