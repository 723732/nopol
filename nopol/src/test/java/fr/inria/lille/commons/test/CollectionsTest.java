package fr.inria.lille.commons.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import fr.inria.lille.commons.collections.ArrayLibrary;
import fr.inria.lille.commons.collections.CollectionLibrary;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.collections.Multimap;
import fr.inria.lille.commons.collections.SetLibrary;

public class CollectionsTest {

	@Test
	public void subarray() {
		String[] array = new String[] {"a", "s", "r"};
		String[] subarray;
		
		subarray = ArrayLibrary.subarray(array, 0, 0);
		Assert.assertEquals(0, subarray.length);
		
		subarray = ArrayLibrary.subarray(array, 0, 1);
		Assert.assertEquals(1, subarray.length);
		Assert.assertEquals("a", subarray[0]);
		
		subarray = ArrayLibrary.subarray(array, 0, 2);
		Assert.assertEquals(2, subarray.length);
		Assert.assertEquals("a", subarray[0]);
		Assert.assertEquals("s", subarray[1]);
		
		subarray = ArrayLibrary.subarray(array, 1, 2);
		Assert.assertEquals(1, subarray.length);
		Assert.assertEquals("s", subarray[0]);
		
		subarray = ArrayLibrary.subarray(array, 0, 3);
		Assert.assertEquals(3, subarray.length);
		Assert.assertTrue(Arrays.equals(array, subarray));
		
		subarray = ArrayLibrary.subarray(array, 0, 4);
		Assert.assertEquals(3, subarray.length);
		Assert.assertTrue(Arrays.equals(array, subarray));
	}
	
	@Test
	public void copyOf() {
		List<String> listSymbols = ListLibrary.newArrayList(",", ".", "<", ":", "<", ":");
		Collection<String> copy = CollectionLibrary.copyOf(listSymbols);
		Assert.assertEquals(6, listSymbols.size());
		Assert.assertEquals(6, copy.size());
		Assert.assertEquals(listSymbols.getClass(), copy.getClass());
		List<String> copiedList = (List) copy;
		for (int i = 0; i < listSymbols.size(); i += 1) {
			Assert.assertEquals(listSymbols.get(i), copiedList.get(i));
		}
		
		Set<String> setSymbols = SetLibrary.newHashSet(",", ".", "<", ":", "<", ":");
		copy = CollectionLibrary.copyOf(setSymbols);
		Assert.assertEquals(4, setSymbols.size());
		Assert.assertEquals(4, copy.size());
		Assert.assertEquals(setSymbols.getClass(), copy.getClass());
		Set<String> copiedSet = (Set) copy;
		Assert.assertTrue(copiedSet.containsAll(setSymbols));
	}
	
	@Test
	public void any() {
		List<String> list = ListLibrary.newArrayList(".", "..", "...");
		Assert.assertTrue(list.contains(CollectionLibrary.any(list)));
		Set<String> set = SetLibrary.newHashSet("-", "--", "---");
		Assert.assertTrue(set.contains(CollectionLibrary.any(set)));
	}
	
	@Test
	public void headAndLast() {
		List<String> list = ListLibrary.newArrayList(".", "..", "...");
		String head = ListLibrary.head(list);
		String last = ListLibrary.last(list);
		Assert.assertTrue(list.contains(head));
		Assert.assertEquals(".", head);
		Assert.assertTrue(list.contains(last));
		Assert.assertEquals("...", last);
	}
	
	@Test
	public void toStringMap() {
		Map<Integer, Boolean> map = MapLibrary.newHashMap();
		map.put(0, false);
		map.put(1, true);
		Map<String, Boolean> stringMap = MapLibrary.toStringMap(map);
		Assert.assertEquals(2, stringMap.keySet().size());
		Assert.assertTrue(stringMap.containsKey("0"));
		Assert.assertEquals(false, stringMap.get("0"));
		Assert.assertTrue(stringMap.containsKey("1"));
		Assert.assertEquals(true, stringMap.get("1"));
	}
	
	@Test
	public void multimap() {
		Multimap<Integer, String> listMultimap = Multimap.newListMultimap();
		listMultimap.addAll(1, "a", "b", "c", "a", "b", "c");
		Assert.assertEquals(1, listMultimap.size());
		Assert.assertTrue(listMultimap.containsKey(1));
		Collection<String> values = listMultimap.get(1);
		Assert.assertEquals(6, values.size());
		Assert.assertEquals(Arrays.asList("a", "b", "c", "a", "b", "c"),  values);
		
		Multimap<Integer, String> setMultimap = Multimap.newSetMultimap();
		setMultimap.addAll(1, "a", "b", "c", "a", "b", "c");
		Assert.assertEquals(1, setMultimap.size());
		Assert.assertTrue(setMultimap.containsKey(1));
		values = setMultimap.get(1);
		Assert.assertEquals(3, values.size());
		Assert.assertTrue(values.contains("a"));
		Assert.assertTrue(values.contains("b"));
		Assert.assertTrue(values.contains("c"));
	}
}
