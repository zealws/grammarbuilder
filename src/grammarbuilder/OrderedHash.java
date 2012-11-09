package grammarbuilder;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class OrderedHash<K, V> {

	private Hashtable<K, V> hash = new Hashtable<K, V>();
	private List<K> list = new LinkedList<K>();

	public void put(K key, V value) {
		hash.put(key, value);
		list.add(key);
	}

	public void remove(K key) {
		list.remove(hash.get(key));
		hash.remove(key);
	}

	public boolean containsKey(K key) {
		return hash.containsKey(key);
	}

	public V get(K key) {
		return hash.get(key);
	}

	public V get(int pos) {
		return hash.get(list.get(pos));
	}

	public int size() {
		return list.size();
	}

	public List<K> getKeys() {
		return list;
	}

	public List<V> getValues() {
		List<V> values = new LinkedList<V>();
		for (K key : list) {
			values.add(hash.get(key));
		}
		return values;
	}
}
