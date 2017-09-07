import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GiangNT on 9/5/2017.
 */
public class Example {
    public static void main(String[] args) {
//        String path = "C:\\Users\\GiangNT\\Desktop\\New folder";
//        File folder = new File(path);
//        File[] listOfFiles = folder.listFiles();
//
//        for (int i = 0; i < listOfFiles.length; i++){
//            if(listOfFiles[i].isFile() && listOfFiles[i].getName().toLowerCase().endsWith(".pdf")){
//
//            }
//        }
//        File file = new File("C:\\Users\\GiangNT\\Desktop\\New folder\\390-737-1-SM.pdf");
//        try {
//            PDDocument document = PDDocument.load(file);
//            Rectangle2D region = new Rectangle2D.Double(0,600, 1000,1000);
//            String regionName = "bottomPage";
//            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
//            stripper.addRegion(regionName,region);
//            PDPage pdPage = document.getPage(0);
//            stripper.extractRegions(pdPage);
//            String text = stripper.getTextForRegion("bottomPage");
//            System.out.println(text);
//        } catch (InvalidPasswordException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        String regex4 = "(\\d{1,2}:\\d)";
        String text = "38:1";
        Matcher m4 = Pattern.compile(regex4).matcher(text);
        while (m4.find()){
            System.out.println("FOUND");
            String x = m4.group().replaceAll("[^\\d.]","").trim();
            System.out.println(x);
            String x2 = x.split(" ")[0];
            String x3 = x.split(" ")[1];
            System.out.println(x2);
            System.out.println(x3);
        }

    }
}
