package design.factory.testmaterial;

public class A1 implements A {

	@Override
	public void doWork() {
		try {
			Thread.sleep(100);
			System.out.println("work done");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
