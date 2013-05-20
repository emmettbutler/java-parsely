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
        String _aspect = ParselyModel.aspectToString(aspect, true);

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
        String aspect_string = ParselyModel.aspectToString(aspect, false);
        APIResult result = this.conn.requestEndpoint(
            String.format("/analytics/%s/%s/detail", aspect_string,
            URLEncoder.encode(meta)), options);
        return typeEntries(result.getData(), ParselyModel.kAspect.kPost);
    }

    public ArrayList<Post> metaDetail(ParselyModel meta_obj,
                                      ParselyModel.kAspect aspect,
                                      RequestOptions options){
        String value = (String)meta_obj.getField(ParselyModel.aspectToString(aspect, false));
        return metaDetail(value, aspect, options);
    }

    public static void main(String[] args){
        Parsely p = new Parsely(Secret.apikey, Secret.secret);
        RequestOptions options = RequestOptions.builder()
                                               .withDays(10)
                                               .build();

        ArrayList<Post> posts = p.analytics(ParselyModel.kAspect.kPost, options);

        System.out.println(p.metaDetail(posts.get(0), ParselyModel.kAspect.kSection, options));
    }

    private ArrayList typeEntries(ArrayList<ParselyModel> entries,
                                  ParselyModel.kAspect aspect){
        ArrayList ret = new ArrayList();
        for(ParselyModel pm : entries){
            ret.add(pm.getAs(aspect));
        }
        return ret;
    }
}
