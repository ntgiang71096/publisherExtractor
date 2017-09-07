import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sun.text.Normalizer;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GiangNT on 9/3/2017.
 */
public class App {

    static final String PDFFolderPath = "C:\\Users\\GiangNT\\Desktop\\New folder";
    static final String XmlFolderPath = "C:\\Users\\GiangNT\\Desktop\\New folder";
    static final String journalListDictionaryPath = "C:\\Users\\GiangNT\\Desktop\\New folder";
    static final ArrayList<String> journalList = getJournalList();
    public static void main(String[] args) {


//        ArrayList<String> listOfJournal = getListOfJournalFromTxt(journalListTxtPath);
//        for (String journal : listOfJournal){
//            System.out.println(journal);
//        }
        File folder = new File(PDFFolderPath);
        File[] listOfFile = folder.listFiles();
        int count = listOfFile.length;
        for (int i = 0; i < count; i++ ){
            if (listOfFile[i].isFile() && listOfFile[i].getName().toLowerCase().endsWith(".pdf")){
                String filePath = PDFFolderPath + "\\" + listOfFile[i].getName();

                //Process top page
                processByRegion(0,0,1000,200, filePath, listOfFile[i].getName());

//                processHeader(filePath,listOfFile[i].getName());


            }
        }


    }

    private static void processByRegion(int x, int y, int w, int h, String filePath, String fileName) {
        Publisher publisher = new Publisher();
//        filePath = "C:\\Users\\GiangNT\\Desktop\\New folder\\1994-1-3882-1-10-20161107.pdf";
        File file = new File(filePath);
        System.out.println(filePath);
        System.out.println("Processing: " + fileName);

        try {
            PDDocument document = PDDocument.load(file);
            Rectangle2D region = new Rectangle2D.Double(x, y, w, h);
            String regionName = "region";
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.addRegion(regionName, region);
            PDPage pdPage = document.getPage(0);
            stripper.extractRegions(pdPage);

            String text = stripper.getTextForRegion("region");
            System.out.println();
            String lines[] = text.split(System.getProperty("line.separator"));
            for (String line : lines){
                String jounalCatcher = inJournalist(line);

                if (containJournalKeyWord(line) || !jounalCatcher.equals("")){
                    if (!jounalCatcher.equals("")){
                        publisher.setJournal(jounalCatcher);
                    }
                    System.out.println(line);
                    extractPublisher(line, publisher);
                }

            }

//            System.out.println("Journal: " + publisher.getJournal());
//            System.out.println("Year: " + publisher.getYear());
//            System.out.println("Page Start: " + publisher.getPageStart());
//            System.out.println("Page End: " + publisher.getPageEnd());
            System.out.println("Vol: " + publisher.getVol());
            System.out.println("No: " + publisher.getNo());

//            createXmlOutput(publisher, fileName);

            System.out.println("------------------------------------");



        document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static ArrayList<String> getJournalList(){

        ArrayList<String> list = new ArrayList<String>();
        try {
            FileInputStream fstream = new FileInputStream(journalListDictionaryPath + "\\" + "journal list.txt");
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
    private static String inJournalist(String line) {
        for (String journal : journalList){
            if (line.toLowerCase().contains(journal.toLowerCase())){
                return journal;
            }
        }


        return "";
    }


    // Catch pattern with journal keyword

    private static void catchYearFormat(String line, Publisher publisher) {
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

//            StreamResult consoleResult = new StreamResult(System.out);
//            transformer.transform(source, consoleResult);

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

//        extractJournal(text, publisher);
//        extractYear(text,publisher);
//        extractPage(text,publisher);
        extractVol(text,publisher);
        extractNo(text,publisher);
    }

    private static void extractNo(String text, Publisher publisher) {
        String words[] = text.split(" ");

        for(String word : words){
            word = word.replace(".", "" );
            word = word.replace(",", "");

        }

        // example: No. 123
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

        //example: (2)
        String regex = "([(]\\d{1,3}[)])";
        Matcher m = Pattern.compile(regex).matcher(text);
        while (m.find()){
            System.out.println("FOUND");
            String x = m.group().replaceAll("[^\\d.]", "");
            publisher.setNo(Integer.parseInt(x));
        }

        //example: Số 2
        String regex3 = "số \\d{1,3}";
        Matcher m3 = Pattern.compile(regex3).matcher(text.toLowerCase());
        while (m3.find()){
            String x = m3.group().replaceAll("[^\\d.]", "");
            publisher.setNo(Integer.parseInt(x));
        }


        // Also set volume
        //example: 38(3)
        String regex2 = "(\\d{1,3}[(]\\d{1,3}[)])";
        Matcher m2 = Pattern.compile(regex2).matcher(text);
        while (m2.find()){
            System.out.println("FOUND");
            String x = m2.group().replace("("," ").replace(")"," ");
            String x2 = x.split(" ")[0];
            publisher.setVol(Integer.parseInt(x2));
        }


        //example: 38:1
        String regex4 = "(\\d{1,3}:\\d{1,3})";
        Matcher m4 = Pattern.compile(regex4).matcher(text);
        while (m4.find()){
            System.out.println("FOUND");
            String x = m4.group().replaceAll("[^\\d.]"," ").trim();
            String x2 = x.split(" ")[0];
            String x3 = x.split(" ")[1];
            publisher.setVol(Integer.parseInt(x2));
            publisher.setNo(Integer.parseInt(x3));
        }

        //example: No.2
        String regex5 = "no.\\d{1,3}";
        Matcher m5 = Pattern.compile(regex5).matcher(text.toLowerCase());
        while (m5.find()){
            System.out.println("FOUND");
            String x = m5.group().replaceAll("[^\\d]"," ").trim();
            publisher.setNo(Integer.parseInt(x));

        }
        //example: No. 2
        String regex6 = "no. \\d{1,3}";
        Matcher m6 = Pattern.compile(regex6).matcher(text.toLowerCase());
        while (m6.find()){
            System.out.println("FOUND");
            String x = m6.group().replaceAll("[^\\d]"," ").trim();
            publisher.setNo(Integer.parseInt(x));

        }
        //
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

        String regex1 = "vol. \\d{1,3}";
        Matcher m1 = Pattern.compile(regex1).matcher(text.toLowerCase());
        while (m1.find()){
            String x = m1.group().replaceAll("[^\\d]", "");
            publisher.setVol(Integer.parseInt(x));
        }

        String regex2 = "vol.\\d{1,3}";
        Matcher m2 = Pattern.compile(regex2).matcher(text.toLowerCase());
        while (m2.find()){
            String x = m2.group().replaceAll("[^\\d]", "");
            publisher.setVol(Integer.parseInt(x));
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

        // find by regex

        String regex = "((\\d{4}))";
        Matcher m = Pattern.compile(regex).matcher(text);
        while (m.find()){
            int year = Integer.parseInt(m.group());
            if (year > 1980 && year < 2020){
                publisher.setYear(year);
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

        if (!publisher.getJournal().equals("None")){
            return;
        }
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
