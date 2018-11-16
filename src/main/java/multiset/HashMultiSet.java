package multiset;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

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
    private List<T> HashKey;
    private List<V> Frequency;

	
	/**
	 * Sole constructor of the class.
	 **/
	public HashMultiSet() {
	    HashKey = new ArrayList<>();
	    Frequency = new ArrayList<>();
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
	    int index = HashKey.indexOf(t);
        V valueToInsert;
	    if(index==-1){
	        HashKey.add(t);
	        Number firstValue = 1;
            valueToInsert = (v)firstValue;
	        Frequency.add(valueToInsert);
        }
        else{
            Number newValue = ((Number)Frequency.get(index)).intValue() + 1;
            valueToInsert = (V)newValue;
            Frequency.add(index,valueToInsert);
        }
        return valueToInsert;
		//throw new UnsupportedOperationException();
	}

	/**
	 * Check whether the elements is present in the multiset.
	 * 
	 * @param t T: element
	 * 
	 * @return V: true if the element is present, false otherwise.
	 * */	
	public boolean isPresent(T t) {
		return HashKey.indexOf(t)!=-1;
		//throw new UnsupportedOperationException();
	}
	
	/**
	 * @param t T: element
	 * @return V: frequency count of parameter t ('0' if not present)
	 * */
	public V getElementFrequency(T t) {
		if(HashKey.indexOf(t)!=-1){
		    return Frequency.get(HashKey.indexOf(t));
        }
        return (V)0;
	    //throw new UnsupportedOperationException();
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
	    try{
	        List<T> readFile = Files.lines(source).map(line->line.split(",")).collect(Collectors.toList());
	        for(T i:readFile.size){
	            addElement(i);
            }
        }
        catch (IOException e){
	        System.out.println("Reading error: "+ e.getStackTrace());
        }
		//throw new UnsupportedOperationException();
		
	}

	/**
	 * Same as before with the difference being the source type.
	 * @param source List<T>: source of the multiset
	 * */
	public void buildFromCollection(List<? extends T> source) {
        for(T i:source.size){
            addElement(i);
        }
	    //throw new UnsupportedOperationException();
	}
	
	/**
	 * Produces a linearized, unordered version of the MultiSet data structure.
	 * Example: <{1:2},{2:1}, {3:3}> -> 1 1 2 3 3 3 3
	 * 
	 * @return List<T>: linearized version of the multiset represented by this object.
	 */
	public List<T> linearize() {
	    List<T> result = new ArrayList<>();
        for (int i:HashKey.size()) {
            for(int j:Frequency.get(i)){
                result.add(HashKey.get(i));
            }
        }
        return result;
	    //throw new UnsupportedOperationException();
	}
	
	
}
