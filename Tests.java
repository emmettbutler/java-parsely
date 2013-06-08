import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;
import org.junit.*;
import org.junit.Test;
import org.junit.BeforeClass;

public class Tests{
    protected static Parsely p;
    protected static RequestOptions defaultOptions;
    private static boolean setUpIsDone = false;

    @BeforeClass
    public static void setUp(){
        if(setUpIsDone) return;
        try{
            p = new Parsely(Secret.apikey, Secret.secret);
        } catch(IOException e){
            e.printStackTrace();
        }
        defaultOptions = RequestOptions.builder()
                                       .withLimit(7)
                                       .withDays(3)
                                       .build();
        setUpIsDone = true;
    }

    @Test
    public void testInit(){
        assertEquals(p.getApiKey(), Secret.apikey);
        assertEquals(p.getSecret(), Secret.secret);
    }

    @Test
    public void testAnalytics(){
        ArrayList<Post> posts = p.analytics(ParselyModel.kAspect.kPost, defaultOptions);
        assertEquals(posts.size(), 7);
        assertTrue(posts.get(0).hits > 0);
    }

    @Test
    public void testPostDetail(){
        Post post = p.postDetail("http://arstechnica.com/science/2013/04/inside-science-selling-and-upsizing-the-meal/", defaultOptions);
        Post post2 = p.postDetail(post, defaultOptions);
        assertTrue(post.hits > 0);
        assertEquals(post.hits, post2.hits);
    }

    @Test
    public void testMetaDetail(){
        ArrayList<Post> posts = p.metaDetail("Technology Lab", ParselyModel.kAspect.kSection, defaultOptions);
        assertEquals(posts.get(0).section, "Technology Lab");
    }
}
