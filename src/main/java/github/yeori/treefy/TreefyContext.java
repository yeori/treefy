package github.yeori.treefy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TreefyContext {

	private List<String> props;
	private int level;
	
	private static Map<Class<?>, Map<Field, Method[]>> accessorMap = new HashMap<>();
	private static Lock lock = new ReentrantLock();
	
	TreefyContext() {
		this.props = new ArrayList<>();
	}
	
	void enter(String prop) {
		props.add(prop);
		level++;
	}
	void exit() {
		props.remove(props.size()-1);
		level--;
	}
	public int getLevel() {
		return level;
	}
	public String getPath() {
		if (props.size() == 0) {
			return "";
		}
		return props
				.stream()
				.filter(prop -> prop.length() > 0)
				.reduce((path, prop) -> path + '.' + prop)
				.get();
	}
	
	public Map<Field, Method[]> findAccessors(Class<?> clazz) {
		lock.lock();
		try {
			if (accessorMap.containsKey(clazz)) {
				return accessorMap.get(clazz);
			}
		} finally {
			lock.unlock();
		}
		long s = System.nanoTime();
		List<Field> fields = asList(clazz.getDeclaredFields());
		Map<String, Method> methods = asMap(clazz.getDeclaredMethods());
		Map<Field, Method[]> accessors = new HashMap<>();
		for (Field f : fields) {
			String nm = f.getName();
			String Fname = nm.substring(0, 1).toUpperCase() + nm.substring(1); // name -> Name
			String getter0 = "get" + Fname;
			String getter1 = "is" + Fname;
			String setter0 = "set" + Fname;
			Method getter = methods.get(getter0);
			if (getter == null) {
				getter = methods.get(getter1);
			}
			Method setter = methods.get(setter0);
			if (getter != null && setter != null) {
				accessors.put(f, new Method[] {getter, setter});
			}
		}
		s = System.nanoTime() - s;
		System.out.printf(">>> %.6f\n", s/1_000_000_000.0);
		lock.lock();
		try {
			accessorMap.putIfAbsent(clazz, accessors);
			return accessors;
		} finally {
			lock.unlock();
		}
	}
	
	private Map<String, Method> asMap(Method[] methods) {
		if (methods == null) {
			return Collections.emptyMap();
		}
		Map<String, Method> map = new HashMap<>();
		for (Method m : methods) {
			String mname = m.getName();
			map.put(mname, m);
		}
		return map;
	}
	

	private <T> List<T> asList(T[] fields) {
		return fields == null ? Collections.emptyList() : Arrays.asList(fields);
	}
	
}
