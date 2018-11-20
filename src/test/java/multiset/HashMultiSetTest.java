package multiset;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Paths;


public class HashMultiSetTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testBuildFromCollection() {
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
	public void testBuildFromCollectionNotNullLinearize() {
		List<String> list = Arrays.asList("ab","bc","bc");
		HashMultiSet<String,Integer> hmSet = new HashMultiSet<>();
		hmSet.buildFromCollection(list);
		List<String> list2 = hmSet.linearize();
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

	@Test
	public void testBuildFromFileNull() throws IOException{
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Method should be invoked with a non null file path");
		HashMultiSet<String, Integer> hmSet = new HashMultiSet<>();
		hmSet.buildFromFile(null);
	}

	@Test
	public void testBuildFromFileNotExist() throws IOException{
		exception.expect(IOException.class);
		exception.expectMessage("The method should be invoked on an existing file");
		HashMultiSet<String, Integer> hmSet = new HashMultiSet<>();
		hmSet.buildFromFile(Paths.get("prova.txt"));
	}

	@Test
	public void testBuildFromFileExist() throws IOException{
		HashMultiSet<String,Integer> hmSet = new HashMultiSet<>();
		hmSet.buildFromFile(Paths.get("src\\test\\java\\multiset\\test.txt"));
		List<String> list = hmSet.linearize();
		List<String> list2 = Arrays.asList("abc","abc","abc","cde","cde","cde","dhf","lmn","kqr","zzz","zzz");
		assertTrue(list.equals(list2));
	}

}
