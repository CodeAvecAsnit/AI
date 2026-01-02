package FileHandling;

import java.io.*;

public class FilePersister {
    public void loadQTable(File file, double[][] Q) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        DataInputStream dis = new DataInputStream(fis);
        for(int i = 0; i < Q.length;++i){
            for(int j = 0; j < Q[0].length;++j){
                Q[i][j]=dis.readDouble();
            }
        }
    }

    public void persistQTable(File file,double[][] Q) throws IOException {
        FileOutputStream fis = new FileOutputStream(file);
        DataOutputStream dis = new DataOutputStream(fis);
        for (int i = 0; i < Q.length; ++i) {
            for (int j = 0; j < Q[0].length; ++j) {
                dis.writeDouble(Q[i][j]);
            }
        }
        System.out.println("File saved");
    }

    public void displayQTable(double[][] qTable){
        System.out.println("Q-TABLE");
        for(int i = 0 ; i < qTable.length;++i){
            for(int j = 0 ; j < qTable[0].length;++j){
                System.out.print(qTable[i][j]+", ");
            }
            System.out.println();
        }
    }
}
