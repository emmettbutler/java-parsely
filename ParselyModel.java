import java.util.ArrayList;
import java.lang.reflect.Type;

import com.google.gson.*;

public class ParselyModel{
    protected String url, title, section, author, metadata, topic, name;
    protected int hits, shares;
    protected ArrayList<String> tags, thumb_urls;
    //protected Date pub_date;

    public static enum kAspect{
        kPost, kAuthor, kSection, kTag
    };

    public ParselyModel(){ }

    public ParselyModel(String url, String title, String section, String author,
                String metadata, String topic, String name, int hits, int shares,
                ArrayList<String> tags){
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
    }

    public <T extends ParselyModel> T getAs(kAspect aspect){
        switch(aspect){
            case kPost:
                return (T)this.getAsPost();
            case kAuthor:
                return (T)this.getAsAuthor();
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
        if(fieldname == "author"){
            value = this.author;
        } else if(fieldname == "section"){
            value = this.section;
        } else if(fieldname == "topic"){
            value = this.topic;
        }
        return value;
    }

    public static String aspectToString(kAspect aspect, boolean plural){
        switch(aspect){
            case kPost:
                if(plural) return "posts";
                return "post";
            case kAuthor:
                if(plural) return "authors";
                return "author";
            case kSection:
                if(plural) return "sections";
                return "section";
            case kTag:
                if(plural) return "tags";
                return "tag";
        }
        return null;
    }
}

class Post extends ParselyModel{
    public Post(String url, String title, String section, String author,
                String metadata, int hits, int shares, ArrayList<String> tags){
        this.url = url;
        this.title = title;
        this.section = section;
        this.author = author;
        this.hits = hits;
        this.shares = shares;
        this.tags = tags;
        this.metadata = metadata;
    }

    public Post(ParselyModel pm){
        this(pm.url, pm.title, pm.section, pm.author, pm.metadata,
             pm.hits, pm.shares, pm.tags
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

    private enum kIdType{
        kAuthor, kTopic, kSection, kTag, kReferrer
    };
    private kIdType idType;

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
    public Referrer (ParselyModel pm){
        super(pm.name, pm.hits);
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
                           _metadata, _topic, _name, _hits, _shares, _tags);
        return pm;
    }

}
