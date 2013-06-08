import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.lang.reflect.Type;

import com.google.gson.*;

public class ParselyModel{
    protected String url, title, section, author, metadata, topic, name, ref_type;
    protected int hits, shares;
    protected ArrayList<String> tags, thumb_urls;
    protected Date pub_date;

    public static enum kAspect{
        kPost, kAuthor, kSection, kTag, kReferrer
    };
    public static enum kRefType{
        kSocial, kSearch, kOther, kInternal
    };
    public static Map<kAspect, String> aspectStrings = new HashMap<kAspect, String>();
    public static Map<kRefType, String> refTypeStrings = new HashMap<kRefType, String>();
    static{
        aspectStrings.put(kAspect.kPost, "posts");
        aspectStrings.put(kAspect.kAuthor, "authors");
        aspectStrings.put(kAspect.kSection, "sections");
        aspectStrings.put(kAspect.kTag, "tags");

        refTypeStrings.put(kRefType.kSocial, "social");
        refTypeStrings.put(kRefType.kSearch, "search");
        refTypeStrings.put(kRefType.kOther, "other");
        refTypeStrings.put(kRefType.kInternal, "internal");
    }

    public ParselyModel(){ }

    public ParselyModel(String url, String title, String section, String author,
                String metadata, String topic, String name, String type,
                Date date, int hits, int shares, ArrayList<String> tags){
        this.url = url;
        this.title = title;
        this.section = section;
        this.author = author;
        this.topic = topic;
        this.hits = hits;
        this.shares = shares;
        this.tags = tags;
        this.metadata = metadata;
        this.name = name;
        this.ref_type = type;
        this.pub_date = date;
    }

    public <T extends ParselyModel> T getAs(kAspect aspect){
        switch(aspect){
            case kPost:
                return (T)this.getAsPost();
            case kAuthor:
                return (T)this.getAsAuthor();
            case kReferrer:
                return (T)this.getAsReferrer();
        }
        return (T)this.getAsPost();
    }

    public Post getAsPost(){
        return new Post(this);
    }

    public Author getAsAuthor(){
        return new Author(this);
    }

    public Section getAsSection(){
        return new Section(this);
    }

    public Topic getAsTopic(){
        return new Topic(this);
    }

    public Referrer getAsReferrer(){
        return new Referrer(this);
    }

    public Object getField(String fieldname){
        String value = null;
        if(fieldname.equals("author")){
            value = this.author;
        } else if(fieldname.equals("section")){
            value = this.section;
        } else if(fieldname.equals("topic")){
            value = this.topic;
        }
        return value;
    }
}

class Post extends ParselyModel{
    public Post(String url, String title, String section, String author,
                String metadata, Date date, int hits, int shares, ArrayList<String> tags){
        this.url = url;
        this.title = title;
        this.section = section;
        this.author = author;
        this.hits = hits;
        this.shares = shares;
        this.tags = tags;
        this.metadata = metadata;
        this.pub_date = date;
    }

    public Post(ParselyModel pm){
        this(pm.url, pm.title, pm.section, pm.author, pm.metadata,
             pm.pub_date, pm.hits, pm.shares, pm.tags
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

    public String getMetadata(){
        return this.metadata;
    }
}

class ParselyMeta extends ParselyModel{
    protected String name;
    protected int hits;

    public ParselyMeta(String name, int hits){
        this.name = name;
        this.hits = hits;
    }

    public String getName(){
        return this.name;
    }

    public int getHits(){
        return this.hits;
    }
}

class Author extends ParselyMeta{
    public Author(ParselyModel pm){
        super(pm.author, pm.hits);
    }
}

class Section extends ParselyMeta{
    public Section(ParselyModel pm){
        super(pm.section, pm.hits);
    }
}

class Topic extends ParselyMeta{
    public Topic(ParselyModel pm){
        super(pm.topic, pm.hits);
    }
}

class Referrer extends ParselyMeta{
    private String ref_type;

    public Referrer (ParselyModel pm){
        super(pm.name, pm.hits);
        this.ref_type = pm.ref_type;
    }

    public void setRefType(String t){
        this.ref_type = t;
    }

    public String getRefType(){
        return this.ref_type;
    }
}

class ModelDeserializer implements JsonDeserializer<ParselyModel> {
    public ParselyModel deserialize(JsonElement json, Type typeOfT,
                                    JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject js = (JsonObject)json;

        String _url = js.get("url") == null ? null : js.get("url").getAsString();
        String _title = js.get("title") == null ?
            null : js.get("title").getAsString();
        String _section = js.get("section") == null ?
            null : js.get("section").getAsString();
        String _author = js.get("author") == null ?
            null : js.get("author").getAsString();
        String _metadata = js.get("metadata") == null ?
            null : js.get("metadata").getAsString();
        String _topic = js.get("topic") == null ?
            null : js.get("topic").getAsString();
        String _name = js.get("name") == null ?
            null : js.get("name").getAsString();
        String _ref_type = js.get("type") == null ?
            null : js.get("type").getAsString();

        String datestring = js.get("pub_date") == null ?
            null : js.get("pub_date").getAsString();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date _date = null;
        try{
            _date = formatter.parse(datestring);
        } catch(Exception ex){}

        int _shares = js.get("shares") == null ? 0 : js.get("shares").getAsInt();
        int _hits = js.get("_hits") == null ? 0 : js.get("_hits").getAsInt();

        JsonElement tags = js.get("tags");
        ArrayList<String> _tags = new ArrayList();
        if(tags != null){
            JsonArray tagArray = tags.getAsJsonArray();
            for(int i = 0; i < tagArray.size(); i++){
                _tags.add(tagArray.get(i).getAsString());
            }
        }

        ParselyModel pm = new ParselyModel(_url, _title, _section, _author,
                           _metadata, _topic, _name, _ref_type, _date, _hits, _shares, _tags);
        return pm;
    }
}

class APIResult{
    private ArrayList<ParselyModel> data;

    public ArrayList<ParselyModel> getData(){
        return this.data;
    }
}
