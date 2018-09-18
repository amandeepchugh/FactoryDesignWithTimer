package design.factory.timeout;

import design.factory.testmaterial.B;

public class WrapperB implements B {

	private Long expiryTime;
	private B b;
	
	public WrapperB(B b, Long expiryTime) {
		this.b = b;
		this.expiryTime = expiryTime;
	}


	@Override
	public void doSomething() {
		if (expiryTime > System.currentTimeMillis()) {
		b.doSomething();
		} else {
			throw new UnsupportedOperationException("time expired");
		}

	}

}
