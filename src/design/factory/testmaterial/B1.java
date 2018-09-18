package design.factory.testmaterial;

public class B1 implements B {

	@Override
	public void doSomething() {
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
