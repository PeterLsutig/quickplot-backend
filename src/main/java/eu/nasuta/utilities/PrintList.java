package eu.nasuta.utilities;

import eu.nasuta.model.SingleNumericDataFile;

import java.util.List;
import java.util.StringJoiner;

public class PrintList {

    public static void printList( List<?> list){
        StringJoiner stringJoiner = new StringJoiner("/", "[", "]");
        for (Object thing : list) {
            if (thing != null) stringJoiner.add(String.valueOf(thing));
        }
        System.out.println(stringJoiner);
    }

    public static void printSingleDataFile(SingleNumericDataFile sdf){
        System.out.println("--------------SingleNumericDataFile---------------");
        System.out.println("Name: "+sdf.getName());
        System.out.println("---Cols---");
        sdf.getData().keySet().forEach(key->{
            System.out.println(key);
            printList(sdf.getData().get(key));
        });
        System.out.println("-----------------------------");
    }
}
