package github.yeori.treefy;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import github.yeori.treefy.rule.IPropertyRule;
import github.yeori.treefy.rule.Rules;

public class Treefy {
	private static Set<Class<?>> PRIMITIVES = new HashSet<>(Arrays.asList(
		Boolean.class,
		Number.class,
		CharSequence.class,
		LocalDateTime.class,
		LocalDate.class,
		LocalTime.class
	));
	
	public <T> T treefy(T data) {
	    if (data == null) {
	        return null;
	    }
		return treefy(data, (Class<T>)data.getClass());
	}
	public <T> T treefy(T data, String ... exclusionRules) {
	    if (data == null) {
	        return null;
	    }
		return treefy(data, (Class<T>)data.getClass(), exclusionRules);
	}
	public <T, U> U treefy(T data, Class<U> clazz) {
		return treefy(data, clazz, new String[0]);
	}
	public <T, U> U treefy(T data, Class<U> clazz, String ... exclusionRules) {
		if (data == null) {
			return null;
		}
		IPropertyRule rule = Rules.parseRules(exclusionRules);
		TreefyContext ctx = new TreefyContext();
		ctx.enter("");
		if (isList(data)) {
			return (U) toList(ctx, (List)data, rule);
		} else if (isMap(data)) {
			return (U) toMap(ctx, (Map)data, rule);
		} else if (isSet(data)) {
			return (U) toSet(ctx, (Set)data, rule);
		} else if (isArray(data)) {
			return (U) toArray(ctx, data, rule);
		} else {
			return toDto(ctx, data, clazz, rule);			
		}
	}

	<T, U> U toDto(TreefyContext ctx, T data, Class<U> cls, IPropertyRule rule) {
		if (data == null) {
			return null;
		}
		
		if (isPrimitiveType(data)) {
			return (U)data;
		}
		if (cls.isEnum()) {
			return (U)data;
		}
		
		try {
			Map<Field, Method[]> mmap = ctx.findAccessors(cls);
			U dto = init(cls);
			for(Field f: mmap.keySet()) {
				String fieldName = f.getName();
				ctx.enter(fieldName);
				Method getter = mmap.get(f)[0];
				Method setter = mmap.get(f)[1];
				Object value = null;
				if (rule.isMatched(ctx.getPath(), ctx.getLevel())) {
					setter.invoke(dto, value); // as null
					ctx.exit();
					continue;
				}
				value = getter.invoke(data);
				if (value == null) {
					setter.invoke(dto, value);
				} else if (isMap(value)) {
					Map<?, ?> copied = toMap(ctx, (Map<?, ?>)value, rule);
					setter.invoke(dto, copied);
				} else if (isList(value)){
					List<?> copied = toList(ctx, (List<?>)value, rule);
					setter.invoke(dto, copied);
				} else if (isSet(value)) {
					Set<?> copied = toSet(ctx, (Set<?>)value, rule);
					setter.invoke(dto, copied);
				} else if (isArray(value)) {
					Object copied = toArray(ctx, value, rule);
					setter.invoke(dto, copied);
				}
				else {
					Object cloned = toDto(ctx, value, value.getClass(), rule);
					setter.invoke(dto, cloned);
				}
				ctx.exit();
			}
			return dto;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
		}
	}
	private <T> T init (Class<T> clazz) throws Exception {
		Constructor<T> c = clazz.getDeclaredConstructor();
		return c.newInstance();
	}
	private boolean isMap(Object value) {
		return Map.class.isAssignableFrom(value.getClass());
	}
	private boolean isList(Object value) {
		return List.class.isAssignableFrom(value.getClass());
	}
	private boolean isSet(Object value) {
		return Set.class.isAssignableFrom(value.getClass());
	}
	private boolean isArray(Object value) {
		return value.getClass().isArray();
	}
	<T> List<? super T> toList(TreefyContext ctx, List<? extends T> src, IPropertyRule rule) {
		try {
			List<? super T> dst = new ArrayList<>();
			for(T elem : src) {
				@SuppressWarnings("unchecked")
				T copied = toDto(ctx, elem, (Class<T>)elem.getClass(), rule);
				dst.add(copied);
			}
			return dst;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	@SuppressWarnings("unchecked")
	<K,V> Map<K, ? super V> toMap(TreefyContext ctx, Map<K, ? extends V> src, IPropertyRule rule) {
		try {
			Map<K, ? super V> dst = new HashMap<>();
			for(K k : src.keySet()) {
				K key = toDto(ctx, k, (Class<K>)k.getClass(), rule);
				V value = toDto(ctx, src.get(k), (Class<V>)src.get(k).getClass(), rule);
				dst.put(key, value);
			}
			return dst;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	<T> Set<? super T> toSet(TreefyContext ctx, Set<? extends T> src, IPropertyRule rule){
		try {
			Set<T> dst = new HashSet<>();
			for(T elem : src) {
				@SuppressWarnings("unchecked")
				T copied = (T) toDto(ctx, elem, elem.getClass(), rule);
				dst.add(copied);
			}
			return dst;			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	Object toArray(TreefyContext ctx, Object src, IPropertyRule rule) {
		int len = Array.getLength(src);
		Class<?> elemType = src.getClass().getComponentType();
		Object dst = Array.newInstance(elemType, len);
		for(int i = 0 ; i < len ; i++) {
			Object v = Array.get(src, i);
			Array.set(dst, i, toDto(ctx, v, v.getClass(), rule));
		}
		return dst;
	}
	
	boolean isPrimitiveType(Object v) {
		Class<?> cls = v.getClass();
		for(Class<?> primitive: PRIMITIVES) {
			if (primitive.isAssignableFrom(cls)) {
				return true;
			}
		}
		return false;
	}
}
