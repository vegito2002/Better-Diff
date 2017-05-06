import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

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
            String indexStr = "";
            for (int i = 0; i < n; i++) indexStr += String.format("@%d", i);
            result = String.format("%d +:%s$%s",n, str2.substring(0,n), indexStr);
            table.put(thisKey, result);
            return result;
        }
          
        if (n == 0) {
            String indexStr = "";
            for (int i = 0; i < m; i++) indexStr += String.format("@%d", i);
            result = String.format("%d -:%s$%s",m, str1.substring(0,m), indexStr);
            table.put(thisKey, result);
            return result;
        }
          
        if (str1.charAt(m-1) == str2.charAt(n-1)){
            String result1 = "";
            if (((str1.charAt(m-1) == '>') || (str1.charAt(m-1) == '}')) && m != n ) {
                if ( m > n ) {
                    String previousResult = editDist(str1.substring(0,m-1),str2.substring(0,n),m-1,n);
                    String[] previousResults = previousResult.split(" ");
                    result1 = String.format("%d %s", Integer.parseInt(previousResults[0]) + 1, String.format("%s@%d", previousResults[1] + "|-:>$", m-1));
                } else if (m < n) {
                    String previousResult = editDist(str1.substring(0,m), str2.substring(0,n-1), m, n-1);
                    String[] previousResults = previousResult.split(" ");
                    result1 = String.format("%d %s", Integer.parseInt(previousResults[0]) + 1, String.format("%s@%d", previousResults[1] + "|+:>$", n-1));
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
            result = String.format("%d %s", Integer.parseInt(previousInsertResults[0])+1, previousInsertResults[1] + String.format("|+:%s$@%d", str2.charAt(n-1), n-1));
        } else if (minNext == nextRemove) {
            result = String.format("%d %s", Integer.parseInt(previousRemoveResults[0])+1, previousRemoveResults[1] + String.format("|-:%s$@%d", str1.charAt(m-1), m-1));
        } else if (minNext == nextReplace) {
            result = String.format("%d %s", Integer.parseInt(previousReplaceResults[0])+2, previousReplaceResults[1] + String.format("|-:%s$@%d|+:%s$@%d", str1.charAt(m-1), m-1, str2.charAt(n-1), n-1));
        }
        table.put(thisKey, result);
        return result;
    }

    public static String calculateEditDistAndVisualize(String str1, String str2) {
        String rawResult = editDist(str1,str2,str1.length(), str2.length());
        String[] fields = rawResult.split(" ");
        int distance = Integer.parseInt(fields[0]);
        String[] edits = fields[1].split("\\|");

        String[] editsOfStr1 = new String[str1.length()];
        Arrays.fill(editsOfStr1, " ");
        String[] editsOfStr2 = new String[str2.length()];
        Arrays.fill(editsOfStr2, " ");

        for (int i = 0; i < edits.length; i++) {
            if (edits[i].equals("no_change")) continue;
            String[] parseResult = parseEdit(edits[i]);

            if (parseResult.length == 1) continue; 
            String sign = parseResult[0];
            String[] indices = Arrays.copyOfRange(parseResult, 1, parseResult.length);

            for (String j : indices) {
                int indexNum = Integer.parseInt(j);
                if (sign.equals("-")) {
                    editsOfStr1[indexNum] = "-";
                } else if (sign.equals("+")) {
                    editsOfStr2[indexNum] = "+";
                }
            }
        }

        String annotation1 = String.join("", editsOfStr1);
        String annotation2 = String.join("", editsOfStr2);

        String result = "Results of comparison:\n"
                    + "Total cost (edit distance) is: " + distance + "\n"
                    + "Details of edits:\n"
                    + "Edits on the first string:\n"
                    + str1 + "\n" + annotation1 + "\n"
                    + "Edits on the second string:\n"
                    + str2 + "\n" + annotation2 + "\n";
        return result;
    }

    private static String[] parseEdit(String edit) {
        List<String> result = new ArrayList<>();
        result.add(Character.toString(edit.charAt(0)));
        String[] split1 = edit.split("\\$");
        if (split1.length > 1) {
            String[] split2 = split1[1].split("\\@");
            for (int i = 1; i < split2.length; i++) {
                result.add(split2[i]);
            }
        }
        String[] resultAr = new String[result.size()];
        return result.toArray(resultAr);
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

            testStrings[7] = "<a><b><c>";
            testStrings[8] = "<a><c>";

            testStrings[9] = "<a><b>";
            testStrings[10] = "<b>";

            String str1 = testStrings[Integer.parseInt(args[0])];
            String str2 = testStrings[Integer.parseInt(args[1])];
            System.out.println(calculateEditDistAndVisualize(str1, str2));
        } catch (Exception e) {e.printStackTrace();}
    }
}