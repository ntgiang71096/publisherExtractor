import java.io.File;

/**
 * Created by GiangNT on 9/5/2017.
 */
public class Example {
    public static void main(String[] args) {
        String path = "C:\\Users\\GiangNT\\Desktop\\New folder";
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++){
            if(listOfFiles[i].isFile() && listOfFiles[i].getName().toLowerCase().endsWith(".pdf")){

            }
        }
    }
}
