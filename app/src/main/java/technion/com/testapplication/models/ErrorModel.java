package technion.com.testapplication.models;

public class ErrorModel {
    public String makorUri;
    public String makorRange;
    public String issueText;
    public String freeText;
    public ReportType reportType;

    public ErrorModel(String makorUri, String makorRange, String issueText, String freeText, ReportType reportType) {
        this.makorUri = makorUri;
        this.makorRange = makorRange;
        this.issueText = issueText;
        this.freeText = freeText;
        this.reportType = reportType;
    }

    public enum ReportType {
        BAD_IDENTIFICATION,
        PARTIAL_IDENTIFICATION,
        NOT_IDENTIFIED,
        FREE_TEXT,
    }
}
