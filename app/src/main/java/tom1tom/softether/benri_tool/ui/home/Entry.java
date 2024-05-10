package tom1tom.softether.benri_tool.ui.home;

public class Entry {
    private long id;
    private String date;
    private String content;

    public Entry(long id, String date, String content) {
        this.id = id;
        this.date = date;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
