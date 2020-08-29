package org.jpiccoli.injector.tests;

import javax.inject.Inject;

public class A {
	
	private B b1;
	
	@Inject
	private B b2;
	
	private B b3;
	
	@Inject
	public A(B b1) {
		this.b1 = b1;
	}
	
	public B getB1() {
		return b1;
	}
	
	public B getB2() {
		return b2;
	}
	
	public B getB3() {
		return b3;
	}
	
	@Inject
	public void setB3(B b3) {
		this.b3 = b3;
	}

}
