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
        String _aspect = ParselyModel.aspectStrings.get(aspect);

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
        String aspect_string = ParselyModel.aspectStrings.get(aspect);
        aspect_string = aspect_string.substring(0, aspect_string.length() - 1);
        APIResult result = this.conn.requestEndpoint(
            String.format("/analytics/%s/%s/detail", aspect_string,
            URLEncoder.encode(meta)), options);
        return typeEntries(result.getData(), ParselyModel.kAspect.kPost);
    }

    public ArrayList<Post> metaDetail(ParselyModel meta_obj,
                                      ParselyModel.kAspect aspect,
                                      RequestOptions options){
        String aspect_string = ParselyModel.aspectStrings.get(aspect);
        aspect_string = aspect_string.substring(0, aspect_string.length() - 1);
        String value = (String)meta_obj.getField(aspect_string);
        return metaDetail(value, aspect, options);
    }

    public ArrayList<Referrer> referrers(ParselyModel.kRefType r_type,
                                         String section, String tag, String domain,
                                         RequestOptions options){
        Map<String, Object> customOptions = new HashMap<String, Object>();
        customOptions.put("section", section);
        customOptions.put("tag", tag);
        customOptions.put("domain", domain);

        APIResult result = this.conn.requestEndpoint(
            String.format("/referrers/%s", ParselyModel.refTypeStrings.get(r_type)),
            options, customOptions);
        ArrayList<Referrer> ret = typeEntries(result.getData(), ParselyModel.kAspect.kReferrer);
        for(Referrer ref : ret){
            ref.setRefType(ParselyModel.refTypeStrings.get(r_type));
        }
        return ret;
    }


    public static void main(String[] args){
        Parsely p = new Parsely(Secret.apikey, Secret.secret);
        RequestOptions options = RequestOptions.builder()
                                               .withLimit(7)
                                               .withDays(3)
                                               .build();

        ArrayList<Post> posts = p.analytics(ParselyModel.kAspect.kPost, options);

        p.metaDetail(posts.get(0), ParselyModel.kAspect.kSection, options);
        ArrayList refs = p.referrers(ParselyModel.kRefType.kSocial, "", "", "", options);
        System.out.println(refs);
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
