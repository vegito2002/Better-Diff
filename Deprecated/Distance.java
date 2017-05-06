import java.io.*;
import java.util.Map;
import java.util.HashMap;

public class Distance {
    static Map<String,String> table = new HashMap<>();

    static int min(int x,int y,int z){
        if (x<y && x<z) return x;
        if (y<x && y<z) return y;
        else return z;
    }

    static String editDist(String str1 , String str2 , int m ,int n) {
        String thisKey = str1.substring(0,m) + "," + str2.substring(0,n);
        String stored_result = table.get(thisKey);
        if (stored_result != null ) return stored_result;

        String result = "";
        if (m == 0) {
            result = String.format("%d +:%s",n, str2.substring(0,n));
            table.put(thisKey, result);
            return result;
        }
          
        if (n == 0) {
            result = String.format("%d -:%s",m, str1.substring(0,m));
            table.put(thisKey, result);
            return result;
        }
          
        if (str1.charAt(m-1) == str2.charAt(n-1)){
            String result1 = "";
            if (((str1.charAt(m-1) == '>') || (str1.charAt(m-1) == '}')) && m != n ) {
                if ( m > n ) {
                    String previousResult = editDist(str1.substring(0,m-1),str2.substring(0,n),m-1,n);
                    String[] previousResults = previousResult.split(" ");
                    result1 = String.format("%d %s", Integer.parseInt(previousResults[0]) + 1, previousResults[1] + "|-:>");
                } else if (m < n) {
                    String previousResult = editDist(str1.substring(0,m), str2.substring(0,n-1), m, n-1);
                    String[] previousResults = previousResult.split(" ");
                    result1 = String.format("%d %s", Integer.parseInt(previousResults[0]) + 1, previousResults[1] + "|+:>");
                } 
                // else {
                //     String[] previousResults1 = editDist(str1.substring(0,m-1), str2.substring(0,n), m-1, n);
                //     String[] previousResults2 = editDist(str1.substring(0,m), str2.substring(0,n-1), m, n-1);
                //     String[] previousResults3 = editDist(str1.substring(0,m-1), str2.substring(0,n-1), m-1, n-1);
                //     Integer distance1 = Integer.parseInt(previousResults1[0]);
                //     Integer distance2 = Integer.parseInt(previousResults2[0]);
                //     Integer distance3 = Integer.parseInt(previousResults3[0]);
                //     int minDistance = min(distance1, distance2, distance3);

                // }
            }
            String previousResult = editDist(str1.substring(0,m-1), str2.substring(0,n-1), m-1, n-1);
            String[] previousResults = previousResult.split(" ");
            String result2 = String.format("%s %s", previousResults[0], previousResults[1] + "|no_change");
            if (result1 != "") {
                Integer change1 = Integer.parseInt((result1.split(" "))[0]);
                Integer change2 = Integer.parseInt((result2.split(" "))[0]);
                if ( change1 <= change2 ) {
                    result = result1;
                } else {
                    result = result2;
                }
            } else {
                result = result2;
            }
            table.put(thisKey, result);
            return result;
        }

        String[] previousInsertResults = editDist(str1.substring(0,m), str2.substring(0,n-1), m, n-1).split(" ");
        String[] previousRemoveResults = editDist(str1.substring(0,m-1), str2.substring(0,n), m-1, n).split(" ");
        String[] previousReplaceResults = editDist(str1.substring(0,m-1), str2.substring(0,n-1), m-1, n-1).split(" ");
        int nextInsert = Integer.parseInt(previousInsertResults[0]);
        int nextRemove = Integer.parseInt(previousRemoveResults[0]);
        int nextReplace = Integer.parseInt(previousReplaceResults[0]);

        int minNext = min(nextInsert, nextRemove, nextReplace);

        if (minNext == nextInsert ) {
            result = String.format("%d %s", Integer.parseInt(previousInsertResults[0])+1, previousInsertResults[1] + String.format("|+:%s", str2.charAt(n-1)));
        } else if (minNext == nextRemove) {
            result = String.format("%d %s", Integer.parseInt(previousRemoveResults[0])+1, previousRemoveResults[1] + String.format("|-:%s", str1.charAt(m-1)));
        } else if (minNext == nextReplace) {
            result = String.format("%d %s", Integer.parseInt(previousReplaceResults[0])+2, previousReplaceResults[1] + String.format("|-:%s|+:%s", str1.charAt(m-1), str2.charAt(n-1)));
        }
        table.put(thisKey, result);
        return result;
    }
 
    public static void main(String args[]) {
        if (args.length < 2) System.out.println("Not enough arguments supplied.");
        try {
            System.setOut(new PrintStream(new FileOutputStream("distance_output.txt")));
            String[] testStrings = new String[20];
            testStrings[1] = "<a><b>";
            testStrings[2] = "<b><a>";

            testStrings[3] = 
            "for(int i=0; i<10; i++) {" +
            "System.out.println(\"First line\");" +
            "} " +
            "for(int i=0; i<10; i++) {" +
            "System.out.println(\"Second line\");" +
            "}" +
            "for(int i=0; i<10; i++) {" +
            "System.out.println(\"Third line\");" +
            "}";
            testStrings[4] = 
            "for(int i=0; i<10; i++) {" +
            "System.out.println(\"First line\");" +
            "} " +
            "for(int i=0; i<10; i++) {" +
            "System.out.println(\"Third line\");" +
            "}";

            testStrings[5] = "<a><b><c>";
            testStrings[6] = "<c><a><b>";

            String str1 = testStrings[Integer.parseInt(args[0])];
            String str2 = testStrings[Integer.parseInt(args[1])];
            System.out.printf("Results for comparing:%n%s%n%s%n is:%n", str1,str2);
            System.out.println(editDist(str1,str2,str1.length(), str2.length()));
        } catch (Exception e) {e.printStackTrace();}
    }
}