package design.factory.timeout;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import design.factory.testmaterial.A;
import design.factory.testmaterial.A1;
import design.factory.testmaterial.B;
import design.factory.testmaterial.B1;

public class Factory {

	List<Entry<Object, Long>> register =  new ArrayList<>();
	
	public Factory() {
		Entry<Object, Long> entryA1 = new AbstractMap.SimpleEntry<>(new A1(), Long.MIN_VALUE);
		register.add(entryA1);
		Entry<Object, Long> entryB1 = new AbstractMap.SimpleEntry<>(new B1(), Long.MIN_VALUE);
		register.add(entryB1);
		
	}
	public Object getObject(String interfaceName, int durationInMilliSeconds) {
		
		//if interface exists, scan a register with object and its expiry-time
		Entry<Object, Long> entry = getAvailableEntry(interfaceName);
		Long newExpiryTime = System.currentTimeMillis() + durationInMilliSeconds;
		if (interfaceName.equals(A.class.getName())) {
			entry.setValue(newExpiryTime);
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
					//check if it not being used
					if (entry.getValue() < System.currentTimeMillis()) {
						//clean up entry.getKey()
						return entry;
						//change the expiry time just before wrapping the object
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		throw new UnsupportedOperationException("The assumption that inputs are sanitized is incorrect");
	}
	public static void main(String[] args) {
		Factory factory = new Factory();
		A a = (A) factory.getObject("design.factory.testmaterial.A", 100);
		a.doWork();
		a.doWork(); //exception expected
		
		

	}
	
	

}
