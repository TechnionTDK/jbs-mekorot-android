package technion.com.testapplication.data_manage;

public abstract class Cacheable {
    abstract public String getKey();

    abstract public Object getData();

    abstract public void setData(Object data);

    abstract public void FetchDataAsync(Runnable onComplete);
}
