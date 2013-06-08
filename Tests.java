import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;
import org.junit.*;
import org.junit.Test;
import org.junit.BeforeClass;

public class Tests{
    protected static Parsely p;
    protected static RequestOptions defaultOptions;
    protected static String trainLink = "http://arstechnica.com/gadgets/2013/04/tunein-radio-app-update-makes-it-easier-for-users-to-discover-new-music/";
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

    @Test
    public void testReferrers(){
        ArrayList<Referrer> refs = p.referrers(ParselyModel.kRefType.kSearch, null, "copyright", null, defaultOptions);
        assertTrue(refs.get(0).hits > 0);
    }

    @Test
    public void testReferrersMeta(){
        ArrayList<Author> authors = p.referrers_meta(ParselyModel.kRefType.kSearch, ParselyModel.kAspect.kAuthor, null, null, defaultOptions);
        assertTrue(authors.get(0).hits > 0);
    }

    @Test
    public void testReferrersMetaDetail(){
        ArrayList<Post> posts = p.referrers_meta_detail("Ars Staff", ParselyModel.kRefType.kSearch, ParselyModel.kAspect.kAuthor, null, defaultOptions);
        assertEquals(posts.get(3).author, "Ars Staff");
    }

    @Test
    public void testReferrersPostDetail(){
        ArrayList<Referrer> referrers = p.referrers_post_detail("http://arstechnica.com/information-technology/2013/04/memory-that-never-forgets-non-volatile-dimms-hit-the-market/", defaultOptions);
        assertTrue(referrers.get(0).hits > 0);
    }

    @Test
    public void testShares(){
        ArrayList<Author> posts = p.shares(ParselyModel.kAspect.kAuthor, "", defaultOptions);
        assertTrue(posts.get(0).name != "");
    }

    @Test
    public void testSharesDetail(){
        fail("Unimplemented!");
    }

    @Test
    public void testRealtime(){
        ArrayList<Post> posts = p.realtime(ParselyModel.kAspect.kAuthor, defaultOptions);
        assertTrue(posts.get(3).title != "");
        assertTrue(posts.size() == 7);
    }

    @Test
    public void testSearch(){
        ArrayList<Post> posts = p.search("security", defaultOptions);
        assertTrue(posts.get(2).title != "");
    }

    @Test
    public void testRelatedUrl(){
        ArrayList<Post> posts = p.related(trainLink, defaultOptions);
        assertTrue(posts.get(3).title != "");
    }

    @Test
    public void testRelatedUser(){
        fail("Unimplemented!");
    }

    @Test
    public void testTrain(){
        fail("Unimplemented!");
    }
}
