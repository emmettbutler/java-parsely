import java.util.Map;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/*
 *  Class modeling a non-persistent connection to the Parsely API
 */
public class ParselyAPIConnection{
    private String apikey, secret, rooturl;

    public ParselyAPIConnection(String apikey){
        this.apikey = apikey;
        this.rooturl = "http://api.parsely.com/v2";
    }

    public ParselyAPIConnection(String apikey, String secret){
        this(apikey);
        this.secret = secret;
    }

    public ParselyAPIConnection(String apikey, String secret, String root){
        this(apikey, secret);
        this.rooturl = root == null ? "http://api.parsely.com/v2" : root;
    }

    /*
     *  Make a synchronous GET request to the given endpoint
     *
     *  Uses options and customOptions as a template for the construction of
     *  the query string
     *
     *  @param  endpoint        the API endpoint to request
     *  @param  options         the basic request options to be serialized and sent
     *  @param  customOptions   special options
     *      serialized into a query string such that key=value&
     *  @param  def             use the default JSON deserializer
     *  @return the deserialized request result
     */
    public APIResult _requestEndpoint(String endpoint, RequestOptions options,
                                      Map<String, Object> customOptions, boolean def){

        String url = this.rooturl + endpoint + String.format("?apikey=%s&", this.apikey);
        url += (this.secret != "") ? String.format("secret=%s&", this.secret) : "";

        if(customOptions != null){
            for(Map.Entry<String, Object> entry : customOptions.entrySet()){
                url += String.format("%s=%s&", entry.getKey(), entry.getValue());
            }
        }

        url += options == null ? "" : options.getAsQueryString();
        //System.out.println(url);

        String res = getJSON(url, 20000);
        //System.out.println(res);

        GsonBuilder gs = new GsonBuilder();
        if(!def){
            gs.registerTypeAdapter(ParselyModel.class, new ModelDeserializer());
        }
        Gson gson = gs.create();
        APIResult d = gson.fromJson(res, APIResult.class);

        return d;
    }

    public APIResult requestEndpoint(String endpoint, RequestOptions options,
                                     Map<String, Object> customOptions){
        return _requestEndpoint(endpoint, options, customOptions, false);
    }

    public APIResult requestEndpoint(String endpoint, RequestOptions options){
        return this.requestEndpoint(endpoint, options, null);
    }

    /*
     *  Get a JSON string from the given URL
     *
     *  @param  url
     *  @param  timeout how many milliseconds to wait before failing
     *  @return the json string
     */
    public String getJSON(String url, int timeout){
        try{
            URL u = new URL(url);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
            }
        } catch(MalformedURLException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
