package org.jpiccoli.injector.tests;

import javax.inject.Inject;
import javax.inject.Named;

public class F {

	@Inject @Named("asingleton")
	private A a1;
	
	private A a2;
	
	private A a3;
	
	private A a4;
	
	@Inject @Named("aimpl")
	private AInterface aInterface1;
	
	private AInterface aInterface2;
	
	private AInterface aInterface3;
	
	@Inject
	public F(@Named("asingleton") A a2, @Named("aimpl") AInterface aInterface2) {
		this.a2 = a2;
		this.aInterface2 = aInterface2;
	}
	
	public A getA1() {
		return a1;
	}
	
	public A getA2() {
		return a2;
	}
	
	public A getA3() {
		return a3;
	}
	
	public A getA4() {
		return a4;
	}
	
	@Inject
	public void setA3(@Named("asingleton") A a3) {
		this.a3 = a3;
	}
	
	@Inject
	public void setAInterface3(@Named("aimpl") AInterface aInterface3) {
		this.aInterface3 = aInterface3;
	}
	
	public AInterface getaInterface1() {
		return aInterface1;
	}
	
	public AInterface getaInterface2() {
		return aInterface2;
	}
	
	public AInterface getaInterface3() {
		return aInterface3;
	}
	
}
