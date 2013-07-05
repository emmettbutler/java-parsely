/**
 *
 *   Copyright (C) 2013 Emmett Butler, Parsely Inc.
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Java-Parsely is a type-safe pure Java binding for the Parsely API
 *
 *   @author Emmett Butler
 *   @version 1.0.0
 *   @url http://github.com/emmett9001/java-parsely
 */
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.net.URLEncoder;
import java.io.IOException;


public class Parsely{
    /*
     *  public and private API keys, sent to the API with every request
     */
    private String apikey, secret;
    private ParselyAPIConnection conn;

    /*
     *  Base Parsely constructor - called by other higher-level constructors
     *
     *  @param  apikey      the public api key (eg "mysite.com")
     *  @param  secret      the secret api key
     *  @param  root        the URL root to use for requests to the API
     *  @throws IOException on authentication failure
     */
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

    /*
     *  Check for authentication status with public and private keys
     *
     *  @return true if auth successful, false otherwise
     */
    public boolean isAuthenticated(){
        APIResult result = this.conn.requestEndpoint("/analytics/posts", null);
        ArrayList<Post> arr = ParselyModel.typeEntries(result.getData(),
                                          ParselyModel.kAspect.kPost);
        return arr.size() == 0 ? false : true;
    }

    /*
     *  Get most popular content by aspect
     *
     *  http://parsely.com/api/api_ref.html#method-analytics
     *
     *  @param  aspect
     *  @param  options
     *  @return the model objects representing the returned data
     */
    public <T extends ParselyModel> ArrayList<T>
    analytics(ParselyModel.kAspect aspect, RequestOptions options){
        String _aspect = ParselyModel.aspectStrings.get(aspect);

        APIResult result = this.conn.requestEndpoint(
            String.format("/analytics/%s", _aspect), options);

        return ParselyModel.typeEntries(result.getData(), aspect);
    }

    /*
     *  Get pageviews and metadata for a post
     *
     *  http://parsely.com/api/api_ref.html#method-analytics-post-detail
     *
     *  @param  url
     *  @param  options
     *  @return the post data as a Post
     */
    public Post postDetail(String url, RequestOptions options){
        Map<String, Object> customOptions = new HashMap<String, Object>();
        customOptions.put("url", url);
        APIResult result = this.conn.requestEndpoint("/analytics/post/detail", options, customOptions);
        return result.getData().get(0).getAsPost();
    }

    /*
     *  Wrapper around postDetail aceepting a Post object instead of a URL
     *  string
     *
     *  @param  url
     *  @param  options
     *  @return the post data as a Post
     */
    public Post postDetail(Post post, RequestOptions options){
        return postDetail(post.getUrl(), options);
    }

    /*
     *  List posts by metadata field
     *
     *  http://parsely.com/api/api_ref.html#method-analytics-detail
     *
     *  @param  meta    the value of the meta. author, section, or topic name
     *  @param  aspect  the meta field to inspect
     *  @param  options
     *  @return group of objects modeling the returned data
     */
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


    /*
     *  Wrapper around metaDetail() accepting a ParselyModel representing the
     *  chosen meta value instead of a string
     *
     *  @param  meta_obj    the value of the meta. author, section, or topic name
     *  @param  aspect      the meta field to inspect
     *  @param  options
     *  @return group of objects modeling the returned data
     */
    public ArrayList<Post> metaDetail(ParselyModel meta_obj,
                                      ParselyModel.kAspect aspect,
                                      RequestOptions options){
        String aspect_string = ParselyModel.aspectStrings.get(aspect);
        aspect_string = aspect_string.substring(0, aspect_string.length() - 1);
        String value = (String)meta_obj.getField(aspect_string);
        return metaDetail(value, aspect, options);
    }

    /*
     *  List top referrers
     *
     *  http://parsely.com/api/api_ref.html#method-referrer
     *
     *  @param  r_type
     *  @param  section
     *  @param  tag
     *  @param  domain
     *  @param  options
     *  @return list of referrers
     */
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

     /*
      *  List top metas by referral
      *
      *  http://parsely.com/api/api_ref.html#method-referrer-meta
      *
      *  @param  r_type
      *  @param  meta
      *  @param  section
      *  @param  domain
      *  @param  options
      *  @return top ParselyModels ranked by referrer
      */
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

    /*
     *  List posts by metadata field
     *
     *  http://parsely.com/api/api_ref.html#method-referrer-meta-value
     *
     *  @param  meta
     *  @param  r_type
     *  @param  aspect
     *  @param  domain
     *  @param  options
     *  @return top posts with the given metadata field
     */
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

    /*
     *  Wrapper around referrers_meta_detail accepting a ParselyModel
     *  representing the chosen meta value instead of a string
     *
     *  @param  meta_obj
     *  @param  r_type
     *  @param  aspect
     *  @param  domain
     *  @param  options
     *  @return top posts with the given metadata field
     */
    public ArrayList<Post>
    referrers_meta_detail(ParselyModel meta_obj, ParselyModel.kRefType r_type,
                          ParselyModel.kAspect aspect, String domain,
                          RequestOptions options){
        String aspect_string = ParselyModel.aspectStrings.get(aspect);
        aspect_string = aspect_string.substring(0, aspect_string.length() - 1);
        String value = (String)meta_obj.getField(aspect_string);
        return referrers_meta_detail(value, r_type, aspect, domain, options);
    }

    /*
     *  List top referrers for a post
     *
     *  http://parsely.com/api/api_ref.html#method-referrer-url
     *
     *  @param  url
     *  @param  options
     *  @return top referrers
     */
    public ArrayList<Referrer>
    referrers_post_detail(String url, RequestOptions options){
        Map<String, Object> customOptions = new HashMap<String, Object>();
        customOptions.put("url", url);

        APIResult result = this.conn.requestEndpoint(
            "/referrers/post/detail", options, customOptions);
        return ParselyModel.typeEntries(result.getData(), ParselyModel.kAspect.kReferrer);
    }

    /*
     *  Wrapper around referrers_post_detail accepting a Post object instead
     *  of a string representing the chosen url
     *
     *  @param  post
     *  @param  options
     *  @return top referrers
     */
    public ArrayList<Referrer>
    referrers_post_detail(Post post, RequestOptions options){
        String url = post.getUrl();
        return referrers_post_detail(url, options);
    }

    /*
     *  List posts or authors by most social shares
     *
     *  http://parsely.com/api/api_ref.html#method-share-post-detail
     *
     *  @param  aspect
     *  @param options
     *  @return top posts or authors by shares
     */
    public <T extends ParselyModel> ArrayList<T>
    shares(ParselyModel.kAspect aspect, RequestOptions options){
        APIResult res = this.conn.requestEndpoint(
            String.format("/shares/%s", ParselyModel.aspectStrings.get(aspect)),
            options);
        return ParselyModel.typeEntries(res.getData(), aspect);
    }

    /*
     *  Get share details for a post
     *
     *  http://parsely.com/api/api_ref.html#method-shares (called with post)
     *
     *  @param  url
     *  @param  options
     *  @return share details for the given post
     */
    public Shares
    shares_detail(String url, RequestOptions options){
        Map<String, Object> customOptions = new HashMap<String, Object>();
        customOptions.put("url", url == null ? "" : url);

        APIResult res = this.conn.requestEndpoint(
            "/shares/post/detail", options, customOptions);
        return res.getData().get(0).getAsShares();
    }

    /*
     *  Wrapper around shares_detail accepting a Post instead of a string
     *  representing the post for which details are requested
     *
     *  @param  post
     *  @param  options
     *  @return share details for the given post
     */
    public Shares
    shares_detail(Post post, RequestOptions options){
        return shares_detail(post.getUrl(), options);
    }

    /*
     *  List top posts with small granularity
     *
     *  http://parsely.com/api/api_ref.html#method-realtime
     *
     *  @param  aspect
     *  @param  options
     *  @return top posts
     */
    // TODO - this is missing the 'time' querystring arg
    public ArrayList<Post> realtime(ParselyModel.kAspect aspect,
                                    RequestOptions options){
        APIResult res = this.conn.requestEndpoint(
            String.format("/realtime/%s", ParselyModel.aspectStrings.get(aspect)),
            options);
        return ParselyModel.typeEntries(res.getData(), ParselyModel.kAspect.kPost);
    }

    /*
     *  Post recommendations by URL
     *
     *  http://parsely.com/api/api_ref.html#method-related
     *
     *  @param  url
     *  @param  options
     *  @return recommended posts
     */
    public ArrayList<Post> related(String url, RequestOptions options){
        Map<String, Object> customOptions = new HashMap<String, Object>();
        customOptions.put("url", url);

        APIResult res = this.conn.requestEndpoint("/related", options, customOptions);
        return ParselyModel.typeEntries(res.getData(), ParselyModel.kAspect.kPost);
    }

    /*
     *  Wrapper around related() accepting a Post instead of a url representing
     *  the chosen post
     *
     *  @param  url
     *  @param  options
     *  @return recommended posts
     */
    public ArrayList<Post> related(Post post, RequestOptions options){
        return related(post.getUrl(), options);
    }

    /*
     *  Search for posts by keyword
     *
     *  http://parsely.com/api/api_ref.html#method-search
     *
     *  @param  query
     *  @param  options
     */
    public ArrayList<Post> search(String query, RequestOptions options){
        Map<String, Object> customOptions = new HashMap<String, Object>();
        customOptions.put("q", query);

        APIResult res = this.conn.requestEndpoint("/search", options, customOptions);
        return ParselyModel.typeEntries(res.getData(), ParselyModel.kAspect.kPost);
    }
}
