package tolabuth.CartoonBookRealtime.model;

public class Cartoon {

    private String bookname;
    private String author;
    private String publisher;
    private int issue;
    private String imageUrl;
    private String mkey;

    public Cartoon(String bookname, String author, String publisher, int issue) {
        this.bookname = bookname;
        this.author = author;
        this.publisher = publisher;
        this.issue = issue;
    }

    public Cartoon(String bookname, String author, String publisher, int issue, String imageUrl, String mkey) {
        this.bookname = bookname;
        this.author = author;
        this.publisher = publisher;
        this.issue = issue;
        this.imageUrl = imageUrl;
        this.mkey = mkey;
    }

    public String getkey() {
        return mkey;
    }

    public void setkey(String key) {
        this.mkey = key;
    }

    public Cartoon(String bookname, String author, String publisher, int issue, String imageUrl) {
        this.bookname = bookname;
        this.author = author;
        this.publisher = publisher;
        this.issue = issue;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return this.bookname;
    }

    public Cartoon() {
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getIssue() {
        return issue;
    }

    public void setIssue(int issue) {
        this.issue = issue;
    }
}
