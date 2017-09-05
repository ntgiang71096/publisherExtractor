import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by GiangNT on 9/3/2017.
 */
public class App {

    static final String PDFFolderPath = "C:\\Users\\GiangNT\\Desktop\\New folder";
    static final String XmlFolderPath = "C:\\Users\\GiangNT\\Desktop\\New folder";

    public static void main(String[] args) {


//        ArrayList<String> listOfJournal = getListOfJournalFromTxt(journalListTxtPath);
//        for (String journal : listOfJournal){
//            System.out.println(journal);
//        }

        File folder = new File(PDFFolderPath);
        File[] listOfFile = folder.listFiles();
        for (int i = 0; i < listOfFile.length; i++ ){
            if (listOfFile[i].isFile() && listOfFile[i].getName().toLowerCase().endsWith(".pdf")){
                String filePath = PDFFolderPath + "\\" + listOfFile[i].getName();
                processPDF(filePath,listOfFile[i].getName());

            }
        }


    }

    private static void processPDF(String filePath, String fileName) {
        Publisher publisher = new Publisher();
        File file = new File(filePath);
        System.out.println(filePath);
        System.out.println("Processing: " + fileName);
        try {
            PDDocument document = PDDocument.load(file);
            PDFTextStripper pdfTextStripper = new PDFTextStripper(){

            };
            Rectangle2D region = new Rectangle2D.Double(0,0, 1000, 200);
            String regionName = "topPage";
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.addRegion(regionName, region);
            PDPage pdPage = document.getPage(0);
            stripper.extractRegions(pdPage);

            String text = stripper.getTextForRegion("topPage");
            String lines[] = text.split(System.getProperty("line.separator"));
            for (String line : lines){
                if (containJournalKeyWord(line)){
                    extractPublisher(line, publisher);
                }
            }

//            System.out.println(publisher.getJournal());
//            System.out.println("Year: " + publisher.getYear());
//            System.out.println("Page Start: " + publisher.getPageStart());
//            System.out.println("Page End: " + publisher.getPageEnd());
//            System.out.println("Vol: " + publisher.getVol());
//            System.out.println("No: " + publisher.getNo());
//            System.out.println("------------------------------------");

            createXmlOutput(publisher, fileName);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createXmlOutput(Publisher publisher, String fileName) {

        try{
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element rootElement = doc.createElement("xml");
            doc.appendChild(rootElement);

            Element journalName = doc.createElement("journal");
            journalName.appendChild(doc.createTextNode(publisher.getJournal()));
            rootElement.appendChild(journalName);

            Element volume = doc.createElement("volume");
            volume.appendChild(doc.createTextNode(publisher.getVol()+ ""));
            rootElement.appendChild(volume);

            Element number = doc.createElement("number");
            number.appendChild(doc.createTextNode(publisher.getNo() + ""));
            rootElement.appendChild(number);

            Element year = doc.createElement("year");
            year.appendChild(doc.createTextNode(publisher.getYear() + "" ));
            rootElement.appendChild(year);

            Element pageStart = doc.createElement("page-start");
            pageStart.appendChild(doc.createTextNode(publisher.getPageStart() + ""));
            rootElement.appendChild(pageStart);

            Element pageEnd = doc.createElement("page-end");
            pageEnd.appendChild(doc.createTextNode(publisher.getPageEnd() + ""));
            rootElement.appendChild(pageEnd);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            String xmlFileName = fileName.replace(".pdf", ".xml");
            StreamResult result = new StreamResult(new File(XmlFolderPath + "\\" + xmlFileName));
            transformer.transform(source, result);

            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source, consoleResult);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private static void extractPublisher(String text, Publisher publisher) {

        //text = text.toLowerCase();

        extractJournal(text, publisher);
        extractYear(text,publisher);
        extractPage(text,publisher);
        extractVol(text,publisher);
        extractNo(text,publisher);
    }

    private static void extractNo(String text, Publisher publisher) {
        String words[] = text.split(" ");

        for(String word : words){
            word = word.replace(".", "" );
            word = word.replace(",", "");

        }
        String keywords[] = {"no", "số",};
        for (int i = 1; i < words.length; i++){
            for (int j = 0; j < keywords.length; j++){
                if (words[i - 1].toLowerCase().replaceAll("[,.]","").equals(keywords[j])
                        && words[i].replaceAll("[,.]","").matches("^[0-9]+$")){
                    publisher.setNo(Integer.parseInt(words[i].replaceAll("[,.]","")));
                    return;
                }

            }
        }
    }

    private static void extractVol(String text, Publisher publisher) {
        String words[] = text.split(" ");
        for(String word : words){
            word = word.replace(".", "" );
            word = word.replace(",", "");
        }
        String keywords[] = {"vol", "tập"};
        for (int i = 1; i < words.length; i++){
            for (int j = 0; j < keywords.length; j++){
                if (words[i - 1].toLowerCase().replaceAll("[,.]","").equals(keywords[j])
                        && words[i].replaceAll("[,.]","").matches("^[0-9]+$")){
                    publisher.setVol(Integer.parseInt(words[i].replaceAll("[,.]","")));
                    return;
                }
            }
        }
    }

    private static void extractPage(String text, Publisher publisher) {
        String words[] = text.split(" ");



        for (String word : words){
            word = word.replaceAll("[^0-9]+", " ");
            word = word.trim();
            String tmp[] = word.split(" ");
            if (tmp.length == 2){
                publisher.setPageStart(Integer.parseInt(tmp[0]));
                publisher.setPageEnd(Integer.parseInt(tmp[1]));
            }
        }
    }

    private static void extractYear(String text, Publisher publisher) {
        String words[] = text.split(" ");
        boolean haveWord = false;
        for (String word : words){
            if (containYear(word, publisher)){
                haveWord = true;
            }
        }

    }

    private static boolean containYear(String word, Publisher publisher) {
        word = word.replaceAll("[^0-9]+", " ");
        String numbers[] = word.trim().split(" ");
        if (numbers[0].equals("") || numbers[0].equals(" ")){
            return false;
        }
        int num = Integer.parseInt(numbers[0]);
        if (numbers.length == 1 && num > 1980 && num < 2020){
            publisher.setYear(num);
            return true;
        }
        return false;
    }

    private static void extractJournal(String text, Publisher publisher) {

        String journalKeyWords[] = {"tạp chí", "tap chi", "journal", "journals"};
        String seperator[] = {",", "vol", "tập", "số", "vol."};
        String clauses[] = text.split(",");

        boolean haveJournal = false;
        for (String clause : clauses){
            for (String keyWord : journalKeyWords){
                if (!haveJournal && clause.toLowerCase().contains(keyWord)){
                    haveJournal = true;
                    publisher.setJournal(clause);
                }
            }
        }
    }

    private static boolean containJournalKeyWord(String line) {
        String journalKeyWords[] = {"tạp chí", "tap chi", "journal", "journals"};
        for (String keyWord : journalKeyWords)
        if (line.toLowerCase().contains(keyWord)){
            return true;
        }
        return false;
    }

    private static ArrayList<String> getListOfJournalFromTxt(String journalListTxtPath) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            FileInputStream fstream = new FileInputStream(journalListTxtPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;

            while ((strLine = br.readLine()) != null){
                list.add(strLine);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static float getStringSimilarityScore(String a, String b){
        float longerLength = Math.max(a.length(), b.length());
        return ((float)StringUtils.getLevenshteinDistance(a,b))/ longerLength;
    }
}
