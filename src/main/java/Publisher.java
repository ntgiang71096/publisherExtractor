/**
 * Created by GiangNT on 9/3/2017.
 */
public class Publisher {


    private String journal;
    private int pageStart;
    private int pageEnd;
    private int year;
    private int vol;
    private int no;

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public Publisher(String journal, int pageStart, int pageEnd, int year, int vol) {
        this.journal = journal;
        this.pageStart = pageStart;
        this.pageEnd = pageEnd;
        this.year = year;
        this.vol = vol;
    }

    public Publisher(){
        this.journal = "None";
    }

    public String getJournal() {

        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public int getPageStart() {
        return pageStart;
    }

    public void setPageStart(int pageStart) {
        this.pageStart = pageStart;
    }

    public int getPageEnd() {
        return pageEnd;
    }

    public void setPageEnd(int pageEnd) {
        this.pageEnd = pageEnd;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getVol() {
        return vol;
    }

    public void setVol(int vol) {
        this.vol = vol;
    }
}
