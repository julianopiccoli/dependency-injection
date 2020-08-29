package org.jpiccoli.injector.tests;

import javax.inject.Inject;

public class B {

	@Inject
	private C c;
	
	public B() {
		
	}
	
	public B(C c) {
		this.c = c;
	}
	
	public C getC() {
		return c;
	}
	
}
