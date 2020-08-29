package org.jpiccoli.injector.tests;

import javax.inject.Inject;

public class H {

	@Inject
	private G g1;
	
	private G g2;
	
	private G g3;
	
	@Inject
	public H(G g2) {
		this.g2 = g2;
	}
	
	public G getG1() {
		return g1;
	}
	
	public G getG2() {
		return g2;
	}
	
	public G getG3() {
		return g3;
	}
	
	@Inject
	public void setG3(G g3) {
		this.g3 = g3;
	}
	
}
