package org.jpiccoli.injector.tests;

import javax.inject.Inject;

public class AImplContainer {

	@Inject
	private AInterface test1;
	
	private AInterface test2;
	
	private AInterface test3;
	
	@Inject
	public AImplContainer(AInterface test2) {
		this.test2 = test2;
	}
	
	public AInterface getTest1() {
		return test1;
	}
	
	public AInterface getTest2() {
		return test2;
	}
	
	public AInterface getTest3() {
		return test3;
	}
	
	@Inject
	public void setTest3(AInterface test3) {
		this.test3 = test3;
	}
	
}
