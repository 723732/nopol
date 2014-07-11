package fr.inria.lille.commons.suite;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SampleTestClass {

	class SampleClass {
		public String join(String base, String target) {
			return base + target;
		}
	}
	
	@Test
	public void joinTrue() {
		assertTrue("ab".equals(new SampleClass().join("a", "b")));
	}
	
	@Test
	public void joinFalse() {
		assertFalse("ab".equals(new SampleClass().join("b", "a")));
	}
	
}
