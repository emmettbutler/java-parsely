import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.net.URLEncoder;


public class Parsely{
    private String apikey, secret;
    private ParselyAPIConnection conn;

    public Parsely(String apikey, String secret, String root){
        this.conn = new ParselyAPIConnection(apikey, secret, root);
        this.apikey = apikey;
        this.secret = secret;
    }

    public Parsely(String apikey, String secret){
        this(apikey, secret, null);
    }

    public Parsely(String apikey){
        this(apikey, null, null);
    }

    public <T extends ParselyModel> ArrayList<T>
    analytics(ParselyModel.kAspect aspect, RequestOptions options){
        String _aspect = aspectToString(aspect, true);

        APIResult result = this.conn.requestEndpoint(
            String.format("/analytics/%s", _aspect), options);

        return typeEntries(result.getData(), aspect);
    }

    public Post postDetail(String url, RequestOptions options){
        Map<String, Object> customOptions = new HashMap<String, Object>();
        customOptions.put("url", url);
        APIResult result = this.conn.requestEndpoint("/analytics/post/detail", options, customOptions);
        return result.getData().get(0).getAsPost();
    }

    public Post postDetail(Post post, RequestOptions options){
        return postDetail(post.getUrl(), options);
    }

    public ArrayList<Post> metaDetail(String meta,
                                      ParselyModel.kAspect aspect,
                                      RequestOptions options){
        String aspect_string = aspectToString(aspect, false);
        APIResult result = this.conn.requestEndpoint(
            String.format("/analytics/%s/%s/detail", aspect_string,
            URLEncoder.encode(meta)), options);
        return typeEntries(result.getData(), ParselyModel.kAspect.kPost);
    }

    public ArrayList<Post> metaDetail(ParselyModel meta_obj,
                                      ParselyModel.kAspect aspect,
                                      RequestOptions options){
        // TODO - getattr(meta_obj, aspect_as_string)
        return metaDetail("", aspect, options);
    }

    public static void main(String[] args){
        Parsely p = new Parsely(Secret.apikey, Secret.secret);
        RequestOptions options = RequestOptions.builder()
                                               .withDays(10)
                                               .build();

        System.out.println(p.metaDetail("Technology Lab", ParselyModel.kAspect.kSection, options));
    }

    private ArrayList typeEntries(ArrayList<ParselyModel> entries,
                                  ParselyModel.kAspect aspect){
        ArrayList ret = new ArrayList();
        for(ParselyModel pm : entries){
            ret.add(pm.getAs(aspect));
        }
        return ret;
    }

    private String aspectToString(ParselyModel.kAspect aspect, boolean plural){
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
