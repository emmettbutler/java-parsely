import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.net.URLEncoder;
import java.io.IOException;


public class Parsely{
    private String apikey, secret;
    private ParselyAPIConnection conn;

    public Parsely(String apikey, String secret, String root) throws IOException{
        this.conn = new ParselyAPIConnection(apikey, secret, root);
        this.apikey = apikey;
        this.secret = secret;

        if(!this.isAuthenticated()){
            throw new IOException("Authentication failed");
        }
    }

    public Parsely(String apikey, String secret) throws IOException{
        this(apikey, secret, null);
    }

    public Parsely(String apikey) throws IOException{
        this(apikey, null, null);
    }

    public String getApiKey(){ return this.apikey; }
    public String getSecret(){ return this.secret; }
    public ParselyAPIConnection getConnection(){ return this.conn; }

    public boolean isAuthenticated(){
        APIResult result = this.conn.requestEndpoint("/analytics/posts", null);
        ArrayList<Post> arr = ParselyModel.typeEntries(result.getData(),
                                          ParselyModel.kAspect.kPost);
        return arr.size() == 0 ? false : true;
    }

    public <T extends ParselyModel> ArrayList<T>
    analytics(ParselyModel.kAspect aspect, RequestOptions options){
        String _aspect = ParselyModel.aspectStrings.get(aspect);

        APIResult result = this.conn.requestEndpoint(
            String.format("/analytics/%s", _aspect), options);

        return ParselyModel.typeEntries(result.getData(), aspect);
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
        return ParselyModel.typeEntries(result.getData(), ParselyModel.kAspect.kPost);
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
        customOptions.put("section", section == null ? "" : section);
        customOptions.put("tag", tag == null ? "" : tag);
        customOptions.put("domain", domain == null ? "" : domain);

        APIResult result = this.conn.requestEndpoint(
            String.format("/referrers/%s", ParselyModel.refTypeStrings.get(r_type)),
            options, customOptions);
        ArrayList<Referrer> ret = ParselyModel.typeEntries(result.getData(), ParselyModel.kAspect.kReferrer);
        for(Referrer ref : ret){
            ref.setRefType(ParselyModel.refTypeStrings.get(r_type));
        }
        return ret;
    }

    public <T extends ParselyModel> ArrayList<T>
    referrers_meta(ParselyModel.kRefType r_type, ParselyModel.kAspect meta,
                   String section, String domain, RequestOptions options){
        Map<String, Object> customOptions = new HashMap<String, Object>();
        customOptions.put("section", section == null ? "" : section);
        customOptions.put("domain", domain == null ? "" : domain);

        APIResult result = this.conn.requestEndpoint(
            String.format("/referrers/%s/%s",
                ParselyModel.refTypeStrings.get(r_type),
                ParselyModel.aspectStrings.get(meta)),
            options, customOptions);
        return ParselyModel.typeEntries(result.getData(), meta);
    }

    public ArrayList<Post>
    referrers_meta_detail(String meta, ParselyModel.kRefType r_type,
                          ParselyModel.kAspect aspect, String domain,
                          RequestOptions options){
        Map<String, Object> customOptions = new HashMap<String, Object>();
        customOptions.put("domain", domain == null ? "" : domain);

        String aspect_string = ParselyModel.aspectStrings.get(aspect);
        aspect_string = aspect_string.substring(0, aspect_string.length() - 1);

        APIResult result = this.conn.requestEndpoint(
            String.format("/referrers/%s/%s/%s/detail",
                ParselyModel.refTypeStrings.get(r_type),
                aspect_string,
                URLEncoder.encode(meta)),
            options, customOptions);
        return ParselyModel.typeEntries(result.getData(), ParselyModel.kAspect.kPost);
    }

    public ArrayList<Post>
    referrers_meta_detail(ParselyModel meta_obj, ParselyModel.kRefType r_type,
                          ParselyModel.kAspect aspect, String domain,
                          RequestOptions options){
        String aspect_string = ParselyModel.aspectStrings.get(aspect);
        aspect_string = aspect_string.substring(0, aspect_string.length() - 1);
        String value = (String)meta_obj.getField(aspect_string);
        return referrers_meta_detail(value, r_type, aspect, domain, options);
    }

    public ArrayList<Referrer>
    referrers_post_detail(String url, RequestOptions options){
        Map<String, Object> customOptions = new HashMap<String, Object>();
        customOptions.put("url", url);

        APIResult result = this.conn.requestEndpoint(
            "/referrers/post/detail", options, customOptions);
        return ParselyModel.typeEntries(result.getData(), ParselyModel.kAspect.kReferrer);
    }

    public ArrayList<Referrer>
    referrers_post_detail(Post post, RequestOptions options){
        String url = post.getUrl();
        return referrers_post_detail(url, options);
    }

    public <T extends ParselyModel> ArrayList<T>
    shares(ParselyModel.kAspect aspect, RequestOptions options){
        APIResult res = this.conn.requestEndpoint(
            String.format("/shares/%s", ParselyModel.aspectStrings.get(aspect)),
            options);
        return ParselyModel.typeEntries(res.getData(), aspect);
    }

    public Shares
    shares_detail(ParselyModel.kAspect aspect, String url, RequestOptions options){
        Map<String, Object> customOptions = new HashMap<String, Object>();
        customOptions.put("url", url == null ? "" : url);

        APIResult res = this.conn.requestEndpoint(
            "/shares/post/detail", options, customOptions);
        return res.getData().get(0).getAsShares();
    }

    public Shares
    shares_detail(ParselyModel.kAspect aspect, Post post, RequestOptions options){
        return shares_detail(aspect, post.getUrl(), options);
    }

    public ArrayList<Post> realtime(ParselyModel.kAspect aspect,
                                    RequestOptions options){
        APIResult res = this.conn.requestEndpoint(
            String.format("/realtime/%s", ParselyModel.aspectStrings.get(aspect)),
            options);
        return ParselyModel.typeEntries(res.getData(), ParselyModel.kAspect.kPost);
    }

    public ArrayList<Post> related(String url, RequestOptions options){
        Map<String, Object> customOptions = new HashMap<String, Object>();
        customOptions.put("url", url);

        APIResult res = this.conn.requestEndpoint("/related", options, customOptions);
        return ParselyModel.typeEntries(res.getData(), ParselyModel.kAspect.kPost);
    }

    public ArrayList<Post> related(Post post, RequestOptions options){
        return related(post.getUrl(), options);
    }

    public ArrayList<Post> search(String query, RequestOptions options){
        Map<String, Object> customOptions = new HashMap<String, Object>();
        customOptions.put("q", query);

        APIResult res = this.conn.requestEndpoint("/search", options, customOptions);
        return ParselyModel.typeEntries(res.getData(), ParselyModel.kAspect.kPost);
    }
}
