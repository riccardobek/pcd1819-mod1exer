package multiset;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.ArrayList;

public class HashMultiSetTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testbuildFromCollection() {
	    exception.expect(IllegalArgumentException.class);
	    exception.expectMessage("Method should be invoked with a non null file path");
	    HashMultiSet<String, Integer> hmSet = new HashMultiSet<>();
	    hmSet.buildFromCollection(null);
	}
	
	@Test
	public void testElementFrequency() {
	    HashMultiSet<Integer, Integer> hmSet = new HashMultiSet<>();
	    hmSet.addElement(1);
	    hmSet.addElement(1);	    
	    assertEquals("Equal", true, hmSet.getElementFrequency(1) == 2);
	}

	@Test
	public void testbuildFromCollectionNotNullLinearize() {
		List<String> list = new ArrayList<>();
		list.add("ab");
		list.add("bc");
		list.add("bc");
		HashMultiSet<String,Integer> hmSet = new HashMultiSet<>();
		hmSet.buildFromCollection(list);
		List<String> list2 = new ArrayList<>();
		list2=hmSet.linearize();
		assertTrue(list.equals(list2));
	}

	@Test
	public void testIsPresent() {
		HashMultiSet<Integer, Integer> hmSet = new HashMultiSet<>();
		hmSet.addElement(1);
		hmSet.addElement(2);
		hmSet.addElement(1);
		assertTrue(hmSet.isPresent(2));
	}
}
