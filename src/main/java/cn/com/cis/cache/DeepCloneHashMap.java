package cn.com.cis.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import lombok.Cleanup;

public class DeepCloneHashMap<K, V> extends HashMap<K, V> {

	private static final long serialVersionUID = 2227366908962678167L;

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		V v = super.get(key);
		return (V) copy(v);
	}

	public Object copy(Object v) {
		try {
			@Cleanup
			ByteArrayOutputStream bos = null;
			@Cleanup
			ObjectOutputStream oos = null;
			@Cleanup
			ObjectInputStream ois = null;
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(v);
			ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
