import java.util.Map;
import java.util.ArrayList;

public class APIResult{
    private ArrayList<Map<String, Object>> data;

    public ArrayList<Map<String, Object>> getData(){
        return this.data;
    }

    public void setData(ArrayList<Map<String, Object>> data){
        this.data = data;
    }
}
