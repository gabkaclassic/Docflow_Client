package test;

import java.io.*;
import java.util.ArrayList;

public class Test {
    
    public static void main(String[] args) throws IOException {
        
        var fis = new BufferedReader(new FileReader("./documents/m.java"));
        var lines1 = new ArrayList<String>();
        var lines2 = new ArrayList<String>();
        var buf = "";
        while((buf = fis.readLine()) != null)
            lines1.add(buf);
        fis = new BufferedReader(new FileReader("./documents/m2.java"));
        while((buf = fis.readLine()) != null)
            lines2.add(buf);
    
    
    
        lines1.forEach(System.out::println);
        System.out.println("------------------");
        lines2.forEach(System.out::println);
        System.out.println("------------------");
        lines2.removeAll(lines1);
        lines2.forEach(System.out::println);
        
        var fos = new BufferedWriter(new FileWriter("./documents/m.java"));
        for (String s : lines2) {
            fos.append(s);
            fos.newLine();
            fos.flush();
        }
    
    }
}
