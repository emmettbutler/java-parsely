import java.util.ArrayList;
import java.lang.reflect.Type;

import com.google.gson.*;

public class ParselyModel{
    protected String url, title, section, author;
    protected int hits, shares;
    protected ArrayList<String> tags, thumb_urls;
    //protected Date pub_date;

    public ParselyModel(){ }

    public ParselyModel(String url, String title, String section, String author,
                int hits, int shares, ArrayList<String> tags){
        this.url = url;
        this.title = title;
        this.section = section;
        this.author = author;
        this.hits = hits;
        this.shares = shares;
        this.tags = tags;
    }

    public Post getAsPost(){
        return new Post(this);
    }
}

class Post extends ParselyModel{
    public Post(String url, String title, String section, String author,
                int hits, int shares, ArrayList<String> tags){
        this.url = url;
        this.title = title;
        this.section = section;
        this.author = author;
        this.hits = hits;
        this.shares = shares;
        this.tags = tags;
    }

    public Post(ParselyModel pm){
        this(pm.url, pm.title, pm.section, pm.author, pm.hits, pm.shares,
             pm.tags
            );
    }

    public String getTitle(){
        return this.title;
    }

    public String getUrl(){
        return this.url;
    }

    public String getSection(){
        return this.section;
    }

    public String getAuthor(){
        return this.author;
    }

    public int getHits(){
        return this.hits;
    }

    public int getShares(){
        return this.shares;
    }

    public ArrayList<String> getTags(){
        return this.tags;
    }
}

class ParselyMeta extends ParselyModel{
    protected String name;
    private int hits;

    private enum kIdType{
        kAuthor, kTopic, kSection, kTag, kReferrer
    };
    private kIdType idType;

    public ParselyMeta(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getHits(){
        return this.hits;
    }
}

class Author extends ParselyMeta{
    public Author(String author){
        super(author);
    }
}

class ModelDeserializer implements JsonDeserializer<ParselyModel> {
    public ParselyModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {

        JsonObject js = (JsonObject)json;

        int _shares = 0;
        JsonElement shares = js.get("shares");
        if(shares != null){
            _shares = shares.getAsInt();
        }

        JsonElement tags = js.get("tags");
        ArrayList<String> _tags = new ArrayList();
        if(tags != null){
            JsonArray tagArray = tags.getAsJsonArray();
            for(int i = 0; i < tagArray.size(); i++){
                _tags.add(tagArray.get(i).getAsString());
            }
        }

        ParselyModel pm = new ParselyModel(js.get("url").getAsString(),
                           js.get("title").getAsString(),
                           js.get("section").getAsString(),
                           js.get("author").getAsString(),
                           js.get("_hits").getAsInt(),
                           _shares, _tags);
        return pm;
    }
}
