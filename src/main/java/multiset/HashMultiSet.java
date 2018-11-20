package multiset;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import java.nio.file.Files;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>A MultiSet models a data structure containing elements along with their frequency count i.e., </p>
 * <p>the number of times an element is present in the set.</p>
 * <p>HashMultiSet is a Map-based concrete implementation of the MultiSet concept.</p>
 * 
 * <p>MultiSet a = <{1:2}, {2:2}, {3:4}, {10:1}></p>
 * */
public final class HashMultiSet<T, V> {

    /**
     *XXX: data structure backing this MultiSet implementation.
     */
    private HashMap<T,V> multiSet;
	
	/**
	 * Sole constructor of the class.
	 **/
	public HashMultiSet() {
		multiSet = new HashMap<>();
	}
	
	
	/**
	 * If not present, adds the element to the data structure, otherwise 
	 * simply increments its frequency.
	 * 
	 * @param t T: element to include in the multiset
	 * 
	 * @return V: frequency count of the element in the multiset
	 * */	
	public V addElement(T t) {
		if(multiSet.containsKey(t)){
			Number newValue = ((Integer)multiSet.get(t)) + 1;
			multiSet.replace(t,(V)newValue);
			return (V)newValue;
		}
		Number firstValue = 1;
		multiSet.put(t,(V)firstValue);
		return (V)firstValue;
	}

	/**
	 * Check whether the elements is present in the multiset.
	 * 
	 * @param t T: element
	 * 
	 * @return V: true if the element is present, false otherwise.
	 * */	
	public boolean isPresent(T t) {
		return multiSet.containsKey(t);
	}
	
	/**
	 * @param t T: element
	 * @return V: frequency count of parameter t ('0' if not present)
	 * */
	public V getElementFrequency(T t) {
		if(multiSet.containsKey(t)){
		    return multiSet.get(t);
        }
        Number result = 0;
        return (V)result;
	}
	
	
	/**
	 * Builds a multiset from a source data file. The source data file contains
	 * a number comma separated elements. 
	 * Example_1: ab,ab,ba,ba,ac,ac -->  <{ab:2},{ba:2},{ac:2}>
	 * Example 2: 1,2,4,3,1,3,4,7 --> <{1:2},{2:1},{3:2},{4:2},{7:1}>
	 * 
	 * @param source Path: source of the multiset
	 * */
	public void buildFromFile(Path source) throws IOException {
		if(source == null)
			throw new IllegalArgumentException("Method should be invoked with a non null file path");

		if(Files.notExists(source))
			throw new IOException("The method should be invoked on an existing file");

		multiSet.clear();

		String readFileString = new String(Files.readAllBytes(source));
		List<String> listOfString =  Stream.of(readFileString.split(",")).collect(Collectors.toList());

		for(String i:listOfString){
			addElement((T)i);
		}
	}

	/**
	 * Same as before with the difference being the source type.
	 * @param source List<T>: source of the multiset
	 * */
	public void buildFromCollection(List<? extends T> source) throws IllegalArgumentException {
		if(source == null)
			throw new IllegalArgumentException("Method should be invoked with a non null file path");

		for(T i:source){
            addElement(i);
        }
	}
	
	/**
	 * Produces a linearized, unordered version of the MultiSet data structure.
	 * Example: <{1:2},{2:1}, {3:3}> -> 1 1 2 3 3 3 3
	 * 
	 * @return List<T>: linearized version of the multiset represented by this object.
	 */
	public List<T> linearize() {
	    List<T> result = new ArrayList<>();

	    multiSet.forEach((T t, V v)->{
	    	int end = (int)multiSet.get(t);
	    	for(int i=0;i<end;++i){
	    		result.add(t);
			}
		});

        return result;
	}
	
	
}
