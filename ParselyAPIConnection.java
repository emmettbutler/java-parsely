import java.util.Map;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    public APIResult requestEndpoint(String endpoint, RequestOptions options,
                                     Map<String, Object> customOptions){
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

        GsonBuilder gs = new GsonBuilder();
        gs.registerTypeAdapter(ParselyModel.class, new ModelDeserializer());
        Gson gson = gs.create();
        APIResult d = gson.fromJson(res, APIResult.class);

        return d;
    }

    public APIResult requestEndpoint(String endpoint, RequestOptions options){
        return this.requestEndpoint(endpoint, options, null);
    }

    public static void main(String[] args){
        ParselyAPIConnection p = new ParselyAPIConnection(Secret.apikey,
                                                          Secret.secret);
        APIResult res = p.requestEndpoint("/analytics/topics", null);
        Topic po = res.getData().get(0).getAsTopic();
        System.out.println(po.getName());
    }

    private String getJSON(String url, int timeout){
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

