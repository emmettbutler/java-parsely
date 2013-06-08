import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;
import org.junit.*;
import org.junit.Test;
import org.junit.BeforeClass;

public class Tests{
    protected static Parsely p;
    private static boolean setUpIsDone = false;

    @BeforeClass
    public static void setUp(){
        if(setUpIsDone) return;
        try{
            p = new Parsely(Secret.apikey, Secret.secret);
        } catch(IOException e){
            e.printStackTrace();
        }
        setUpIsDone = true;
    }

    @Test
    public void testInit(){
        assertEquals(p.getApiKey(), Secret.apikey);
        assertEquals(p.getSecret(), Secret.secret);
    }

    @Test
    public void testAnalytics(){
        RequestOptions options = RequestOptions.builder()
                                               .withLimit(7)
                                               .withDays(3)
                                               .build();
        ArrayList<Post> posts = p.analytics(ParselyModel.kAspect.kPost, options);
        assertEquals(posts.size(), 7);
    }
}
