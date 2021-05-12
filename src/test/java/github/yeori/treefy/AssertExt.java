package github.yeori.treefy;

import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static org.junit.Assert.*;

public class AssertExt {

    public static void assertNotNulls(Object ... values) {
        for (int i = 0; i < values.length; i++) {
            assertNotNull(fmt("null value at %d", i), values[i]);
        }
    }
    public static void assertNulls(Object ... values) {
        for (int i = 0; i < values.length; i++) {
            assertNull(fmt("not null value [%s] at %d",values[i], i), values[i]);
        }
    }
    public static <T> void assertNulls(List<T> elems, Function<T, Object> conv) {
        for (int i = 0; i < elems.size(); i++) {
            T e = elems.get(i);
            Object v = conv.apply(e);
            if (v != null) {
                assertNull(fmt("not null value [%s] at %d", v, i), v);
            }
        }
    }

    private static String fmt(String format, Object ... args) {
        return String.format(format, args);
    }
}
