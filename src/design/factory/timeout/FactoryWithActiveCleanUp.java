package design.factory.timeout;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;

import design.factory.testmaterial.A;
import design.factory.testmaterial.A1;
import design.factory.testmaterial.B;
import design.factory.testmaterial.B1;

public class FactoryWithActiveCleanUp {

	List<Entry<Object, Long>> register =  new LinkedList<>();
	PriorityQueue<Entry<Object, Long>> inUseQueue = new PriorityQueue<>(new EntryComparator());


	public FactoryWithActiveCleanUp() {
		Entry<Object, Long> entryA1 = new AbstractMap.SimpleEntry<>(new A1(), Long.MIN_VALUE);
		register.add(entryA1);
		Entry<Object, Long> entryB1 = new AbstractMap.SimpleEntry<>(new B1(), Long.MIN_VALUE);
		register.add(entryB1);

		Thread cleanUp = new CleanUpThread();
		cleanUp.start();

	}
	public Object getObject(String interfaceName, int durationInMilliSeconds) {

		//if interface exists, scan a register with object and its expiry-time
		Entry<Object, Long> entry = getAvailableEntry(interfaceName);
		Long newExpiryTime = System.currentTimeMillis() + durationInMilliSeconds;
		entry.setValue(newExpiryTime);
		inUseQueue.add(entry);
		synchronized (inUseQueue) {
			inUseQueue.notify();
		}

		if (interfaceName.equals(A.class.getName())) {
			return new WrapperA((A) entry.getKey(), newExpiryTime);
		} else if (interfaceName.equals(B.class.getName())) {
			return new WrapperB((B) entry.getKey(), newExpiryTime);
		}

		return null;

	}


	private Entry<Object,Long> getAvailableEntry(String interfaceName) {
		//using the interface-name, get the first available object from the register
		for (int i = 0; i < register.size(); i++) {
			Entry<Object, Long> entry = register.get(i);

			try {
				if (Class.forName(interfaceName).isInstance(entry.getKey())) {
					register.remove(i);
					return entry;
					//change the expiry time just before wrapping the object

				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		throw new UnsupportedOperationException("The assumption that inputs are sanitized is incorrect");
	}


	class CleanUpThread extends Thread {

		@Override
		public void run() {

			while (true) {
				try {
					synchronized (inUseQueue) {

						while (inUseQueue.isEmpty()) {
							inUseQueue.wait();
						}
						Long expiryTime = inUseQueue.peek().getValue();
						Long timeToWait = expiryTime - System.currentTimeMillis();
						if (timeToWait > 0) {
							inUseQueue.wait(timeToWait);
						}

						if (inUseQueue.peek().getValue() <= System.currentTimeMillis()) {

							Entry<Object, Long> entry = inUseQueue.poll();
							//entry.getKey().cleanup()
							register.add(entry);
						}

					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

	}


	class EntryComparator implements Comparator<Entry<Object, Long>> {

		@Override
		public int compare(Entry<Object, Long> o1, Entry<Object, Long> o2) {
			return o1.getValue().compareTo(o2.getValue());
		}

	}

	public static void main(String[] args) throws InterruptedException {
		FactoryWithActiveCleanUp factory = new FactoryWithActiveCleanUp();
		A a = (A) factory.getObject("design.factory.testmaterial.A", 100);
		a.doWork();
		//a.doWork(); //exception expected
		Thread.sleep(3000);
		A a2 = (A) factory.getObject("design.factory.testmaterial.A", 100);
		a2.doWork();



	}



}
