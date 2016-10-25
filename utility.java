import java.io.*;
import java.util.*;

public class utility{
	public static void main (String[] args){

	}



	public static void push_item_set_map(Map<String, Set<String>> map, String key, String val){
		if (map.containsKey(key)) {
			Set<String> valset = map.get(key);
			valset.add(val);
			map.put(key, valset);
		} else {
			Set<String> valset = new HashSet<String>();
			valset.add(val);
			map.put(key, valset);
		}
	}

	public static void push_item_set_map(Map<String, Set<String>> map, String key, Set<String> strSet){
		if (map.containsKey(key)) {
			Set<String> valset = map.get(key);
			valset.addAll(strSet);
			map.put(key, valset);
		} else {
			Set<String> valset = new HashSet<String>();
			valset.addAll(strSet);
			map.put(key, valset);
		}
	}

	

	public static Set<String> read_set (String file) throws IOException {
		Set<String> set = new HashSet<String>();
		BufferedReader r = new BufferedReader(new FileReader(file));
		String line;
		while ((line = r.readLine()) != null) {
			String item = line.trim();
			set.add(item);
		}
		r.close();

		return set;
	}


	public static Set<String> read_string_column (String file, int col) throws IOException {
		Set<String> set = new HashSet<String>();
		BufferedReader r = new BufferedReader(new FileReader(file));
		String line;
		while ((line = r.readLine()) != null) {
			String[] parts = line.split("\\|");
			String item = parts[col];
			set.add(item);
		}
		r.close();
		return set;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(
			Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {

				return (o2.getValue()).compareTo(o1.getValue());

			}
		}

		);

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}