package technion.com.testapplication.models;

public class ResultModel {
    public String Text;
    public String Title;
    public String URI;

    public ResultModel(String text, String title, String URI) {
        Text = text;
        Title = title;
        this.URI = URI;
    }
}
