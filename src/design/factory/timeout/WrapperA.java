package design.factory.timeout;

import design.factory.testmaterial.A;

public class WrapperA implements A {

	private Long expiryTime;
	private A a;
	
	public WrapperA(A a, Long expiryTime) {
		this.a = a;
		this.expiryTime = expiryTime;
	}


	@Override
	public void doWork() {
		if (expiryTime > System.currentTimeMillis()) {
		a.doWork();
		} else {
			throw new UnsupportedOperationException("time expired");
		}

	}

}
