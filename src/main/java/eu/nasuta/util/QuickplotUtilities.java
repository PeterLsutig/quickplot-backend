package eu.nasuta.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class QuickplotUtilities {

    public static <T> List<List<T>> transpose(List<List<T>> table) {
        List<List<T>> ret = new ArrayList<List<T>>();
        final int N = table.get(0).size();
        for (int i = 0; i < N; i++) {
            List<T> col = new ArrayList<T>();
            for (List<T> row : table) {
                col.add(row.get(i));
            }
            ret.add(col);
        }
        return ret;
    }

    public static void printList(List<?> list) {
        StringJoiner stringJoiner = new StringJoiner("/\t", "[", "]");
        for (Object thing : list) {
            stringJoiner.add(String.valueOf(thing));
        }
        System.out.println(stringJoiner);
    }
}
