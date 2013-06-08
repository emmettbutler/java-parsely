import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;


public class ParselyUser{
    private ParselyAPIConnection conn;
    private String uuid;

    public ParselyUser(Parsely p, String uuid){
        this.conn = p.getConnection();
        this.uuid = uuid;
    }

    public boolean train(String url){
        Map<String, Object> customOptions = new HashMap<String, Object>();
        customOptions.put("url", url);
        customOptions.put("uuid", uuid);

        APIResult result = this.conn._requestEndpoint("/profile", null, customOptions, true);
        return result.getSuccess();
    }

    public boolean train(Post p){
        return train(p.getUrl());
    }

    public ArrayList<Post> related(String section, RequestOptions options){
        Map<String, Object> customOptions = new HashMap<String, Object>();
        customOptions.put("uuid", uuid);
        customOptions.put("section", section == null ? "" : section);

        APIResult result = this.conn.requestEndpoint("/related", options, customOptions);
        return ParselyModel.typeEntries(result.getData(), ParselyModel.kAspect.kPost);
    }

    // TODO - this is the only call to return an object of the form
    // {"data": {...}} instead of {"data": [...]}
    // soooo... it is very annoying. It requires a new type of deserializer
    // that sucks
    /*public ArrayList<ParselyModel> history(){
        Map<String, Object> customOptions = new HashMap<String, Object>();
        customOptions.put("uuid", uuid);

        APIResult result = this.conn.requestEndpoint("/history", null, customOptions);
        return result.getData();
    }*/
}
