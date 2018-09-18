package design.factory.testmaterial;

public class A1 implements A {

	@Override
	public void doWork() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
