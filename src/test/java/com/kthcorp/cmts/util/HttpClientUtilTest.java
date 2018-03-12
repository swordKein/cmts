package com.kthcorp.cmts.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.service.crawl.NaverblogService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class HttpClientUtilTest {

    @Autowired
    private NaverblogService naverblogService;

    @Test
    public void test_reqGet() throws Exception {

//    	HttpAsyncClientUtil.reqGet("https://section.blog.naver.com/Search/Post.nhn?keyword=%EC%88%98%EC%A7%80");
        //HttpAsyncClientUtil.reqGet("https://www.google.com");
        //String result = HttpClientUtil.reqGet("https://www.google.co.kr/search?q=movie%20blind&sourceid=chrome&ie=UTF-8", "", null);
        String result = HttpClientUtil.reqGet(
                "https://www.google.co.kr/search?q=site%3Aimdb.com%2Ftitle%2F+blind+2017+directed+by+shin+gyu+hyun"
                , "", null, null, "");
        System.out.println("# returned string:"+result);
    }

    @Test
    public void test_reqGetNaverBlog() throws Exception {

//    	HttpAsyncClientUtil.reqGet("https://section.blog.naver.com/Search/Post.nhn?keyword=%EC%88%98%EC%A7%80");
        //HttpAsyncClientUtil.reqGet("https://www.google.com");
        //String result = HttpClientUtil.reqGet("https://www.google.co.kr/search?q=movie%20blind&sourceid=chrome&ie=UTF-8", "", null);
        String result = HttpClientUtil.reqGet(
                "https://search.naver.com/search.naver?where=post&start=251&query=%EC%98%81%ED%99%94+%EB%B8%94%EB%9D%BC%EC%9D%B8%EB%93%9C+2011"
                , "", null, null, "");
        System.out.println("#HttpClient:regGetNaverBlog returned string:"+result);
        List<String> searchedBlogList = JsoupUtil.getTaggedLinkArray(result, ".blog .type01 .sh_blog_top .sh_blog_title", "href");
        System.out.println("#getTaggedLinkArray:"+searchedBlogList.toString());


        //List<String> subUrlArray = naverblogService.getSubPagesUrlArray(searchedBlogList);

        //System.out.println("#getTaggedLinkArray:"
        //        +JsoupUtil.getTaggedLinkArray(result, ".blog .type01 .sh_blog ", "value"));
    }



    @Test
    public void test_reqGetTstory() throws Exception {
        String result = HttpClientUtil.reqGet("http://farand.tistory.com/81?1&d=1"
                ,"",null, null, "");
        System.out.println("#HttpClient:test_reqGetTstory returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, ".article_body");
        System.out.println("#getTaggedValue:"+result2.toString());

        //List<String> subUrlArray = naverblogService.getSubPagesUrlArray(searchedBlogList);
        //System.out.println("#getTaggedLinkArray:"
        //        +JsoupUtil.getTaggedLinkArray(result, ".blog .type01 .sh_blog ", "value"));
    }


    @Test
    public void test_reqGet2() throws Exception {

        String reqUrl = "http://14.63.170.72:9200/_analyze?analyzer=korean&pretty";
        Map<String, Object> req = new HashMap<String, Object>();
        req.put("body","아버지가방에들어가신다");
        String result = HttpClientUtil.reqPost(reqUrl, req);
        System.out.println("# returned string:"+result);
    }


    @Test
    public void test_reqGet3() throws Exception {

        String reqUrl = "http://translate.google.co.kr/";
        reqUrl += "#en/ko/";
        reqUrl += URLEncoder.encode("Shutting down Quartz Scheduler \n" +
                "DEV::2017-09-21 13:40:13.198 [Thread-3] INFO  o.q.c.QuartzScheduler - " +
                "Scheduler schedulerFactoryBean_$_NON_CLUSTERED shutting down. ", "UTF-8");
        //reqUrl = URLEncoder.encode(reqUrl, "UTF-8");

        Map<String, Object> req = new HashMap<String, Object>();
        //req.put("body","아버지가방에들어가신다");
        String result = HttpClientUtil.reqGet(reqUrl, "", null, null, "");
        System.out.println("# returned string:"+result);
    }



    @Test
    public void test_reqGet4() throws Exception {

        String reqUrl = "http://translate.google.co.kr/";
        reqUrl += "#en/ko/";
        reqUrl += URLEncoder.encode("Shutting down Quartz Scheduler \n" +
                "DEV::2017-09-21 13:40:13.198 [Thread-3] INFO  o.q.c.QuartzScheduler - " +
                "Scheduler schedulerFactoryBean_$_NON_CLUSTERED shutting down. ", "UTF-8");
        //reqUrl = URLEncoder.encode(reqUrl, "UTF-8");

        Map<String, Object> req = new HashMap<String, Object>();
        //req.put("body","아버지가방에들어가신다");
        String result = HttpClientUtil.reqGet(reqUrl, "",  null, null, "");
        System.out.println("# returned string:"+result);
    }

    @Test
    public void test_reqPost() throws Exception {

        String reqUrl = "http://14.63.170.72:9200/_analyze?analyzer=korean&pretty";
        Map<String, Object> req = new HashMap<String, Object>();
        req.put("body","아버지가방에들어가신다");
        String result = HttpClientUtil.reqPost(reqUrl, req);
        System.out.println("# returned string:"+result);
    }


    @Test
    public void test_reqGetMaxmovie() throws Exception {
        String result = HttpClientUtil.reqGet("http://news.maxmovie.com/335536"
                ,"",null, null, "bypass");
        System.out.println("#HttpClient:test_reqGetMaxmovie returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, ".type-post .inside-article");
        System.out.println("#getTaggedValue:"+result2.toString());

        //List<String> subUrlArray = naverblogService.getSubPagesUrlArray(searchedBlogList);
        //System.out.println("#getTaggedLinkArray:"
        //        +JsoupUtil.getTaggedLinkArray(result, ".blog .type01 .sh_blog ", "value"));
    }


    @Test
    public void test_reqGet_www_Honam_co_kr() throws Exception {
        String result = HttpClientUtil.reqGet("http://www.honam.co.kr/read.php3?aid=1508684400537885065"
                ,"",null, null, "bypass");
        System.out.println("#HttpClient:test_reqGetMaxmovie returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, "body table table table table font table table");
        System.out.println("#getTaggedValue:"+result2.toString());

        //List<String> subUrlArray = naverblogService.getSubPagesUrlArray(searchedBlogList);
        //System.out.println("#getTaggedLinkArray:"
        //        +JsoupUtil.getTaggedLinkArray(result, ".blog .type01 .sh_blog ", "value"));
    }

    @Test
    public void test_reqGet_fnnews() throws Exception {
        String result = HttpClientUtil.reqGet("http://www.fnnews.com/news/201602231042540754"
                ,"",null, null, "");
        System.out.println("#HttpClient:test_reqGetMaxmovie returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, "#container #article_content");
        System.out.println("#getTaggedValue:"+result2.toString());

        //List<String> subUrlArray = naverblogService.getSubPagesUrlArray(searchedBlogList);
        //System.out.println("#getTaggedLinkArray:"
        //        +JsoupUtil.getTaggedLinkArray(result, ".blog .type01 .sh_blog ", "value"));
    }


    @Test
    public void test_reqGet_nocutnews() throws Exception {
        String result = HttpClientUtil.reqGet("http://www.nocutnews.co.kr/news/4871928"
                ,"",null, null, "");
        System.out.println("#HttpClient:test_reqGetMaxmovie returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, ".wrap .viewbox");
        System.out.println("#getTaggedValue:"+result2.toString());
    }

    @Test
    public void test_reqGet_nongaek() throws Exception {
        String result = HttpClientUtil.reqGet("http://www.nongaek.com/news/articleView.html?idxno=30456"
                ,"",null, null, "");
        System.out.println("#HttpClient:test_reqGetMaxmovie returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, ".content #articleBody");
        System.out.println("#getTaggedValue:"+result2.toString());
    }

    @Test
    public void test_reqGet_sbscnbc_sbs() throws Exception {
        String result = HttpClientUtil.reqGet("http://sbscnbc.sbs.co.kr/read.jsp?pmArticleId=10000880556"
                ,"",null, null, "");
        System.out.println("#HttpClient:test_reqGetMaxmovie returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, "#content .atend_center");
        System.out.println("#getTaggedValue:"+result2.toString());
    }

    @Test
    public void test_reqGet_imnews_imbc() throws Exception {
        String result = HttpClientUtil.reqGet("http://imnews.imbc.com/replay/2017/nwtoday/article/4452268_21414.html"
                ,"",null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedAttr(result, "meta[property=og:description]", "content");
        System.out.println("#getTaggedValue:"+result2.toString());
    }

    @Test
    public void test_contains() {
        String req = "meta[property=og:description]|content";
        boolean isOk = false;
        if (req.contains("|")) {
            isOk = true;
            String[] pstags = req.split("\\|");
            System.out.println("#pstags 0:"+pstags[0]+"  1:"+pstags[1]);
        }
        System.out.println("#result:"+isOk);
    }
    @Test
    public void test_reqGet_imnews_imbc2() throws Exception {
        String result = HttpClientUtil.reqGet(" http://imnews.imbc.com/replay/2017/nwdesk/article/4451561_21408.html"
                ,"",null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedAttr(result, "meta[property=og:description]", "content");
        System.out.println("#getTaggedValue:"+result2.toString());
    }


    @Test
    public void test_reqGet_ccdn() throws Exception {
        String result = HttpClientUtil.reqGet("http://www.ccdn.co.kr/news/articleView.html?idxno=484756#07RY"
                ,"",null, null, "");
        System.out.println("#HttpClient:test_reqGetMaxmovie returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, "#ND_Warp #articleBody");
        System.out.println("#getTaggedValue:"+result2.toString());
    }

    @Test
    public void test_reqGet_viva100() throws Exception {
        String result = HttpClientUtil.reqGet("http://www.viva100.com/main/view.php?key=20171106010001929"
                ,"",null, null, "");
        System.out.println("#HttpClient:test_reqGetMaxmovie returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, "#container .view_left_warp");
        System.out.println("#getTaggedValue:"+result2.toString());
    }

    @Test
    public void test_reqGet_news_jtbc() throws Exception {
        String result = HttpClientUtil.reqGet("http://news.jtbc.joins.com/html/634/NB11545634.html"
                ,"",Charset.forName("UTF-8"), null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, "#article .article_content");
        System.out.println("#getTaggedValue:"+result2.toString());
    }

    @Test
    public void test_reqGet_news_jtbc2() throws Exception {
        String result = HttpClientUtil.reqGet("http://news.jtbc.co.kr/html/374/NB11545374.html"
                ,"", null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, "#article .article_content");
        System.out.println("#getTaggedValue:"+result2.toString());
    }
    /*
    #failResult:"not_defined___http://news.jtbc.co.kr/html/634/NB11545634.html"
#failResult:"filtered___http://news.kbs.co.kr/news/view.do?ncd=3567435&ref=A"
#failResult:"filtered___http://www.mt.co.kr/view/mtview.php?type=1&no=2017110513497639241&outlink=1"
#failResult:"filtered___http://imnews.imbc.com/replay/2017/nwdesk/article/4451561_21408.html"
#failResult:"not_defined___http://news.jtbc.co.kr/html/374/NB11545374.html"
#failResult:"not_defined___http://mbn.mk.co.kr/pages/news/newsView.php?category=mbn00008&news_seq_no=3379243"
#failResult:"not_defined___http://news.jtbc.co.kr/html/252/NB11545252.html"
#failResult:"filtered___http://news.kbs.co.kr/news/view.do?ncd=3567281&ref=A"
#failResult:"filtered___http://www.redian.org/archive/116109"
     */

    @Test
    public void test_reqGet_news_kbs() throws Exception {
        String result = HttpClientUtil.reqGet("http://news.kbs.co.kr/news/view.do?ncd=3567281&ref=A"
                ,"",null, null, "");
        System.out.println("#HttpClient:test_reqGetMaxmovie returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, "#container #cont_newstext");
        System.out.println("#getTaggedValue:"+result2.toString());
    }

    @Test
    public void test_reqGet_news_daily_hankooki() throws Exception {
        String result = HttpClientUtil.reqGet("http://daily.hankooki.com/lpage/politics/201711/dh20171106120708137590.htm"
                ,"",null, null, "");
        System.out.println("#HttpClient:test_reqGetMaxmovie returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, ".wrap1 #GS_Content");
        System.out.println("#getTaggedValue:"+result2.toString());
    }


    @Test
    public void test_reqGet_news_hankooki() throws Exception {
        String result = HttpClientUtil.reqGet("http://www.hankookilbo.com/v/efae2993dd9a40de9f7768200bae1636"
                ,"",null, null, "");
        System.out.println("#HttpClient:test_reqGetMaxmovie returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, "#wrap .DetailCon");
        System.out.println("#getTaggedValue:"+result2.toString());
    }

    @Test
    public void test_reqGet_news_mt() throws Exception {
        String result = HttpClientUtil.reqGet("http://www.mt.co.kr/view/mtview.php?type=1&no=2017110513497639241&outlink=1"
                ,"",null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);

        if (result.startsWith("<script>") && result.endsWith("</script>")
                && result.contains("location.href=")) {
            String toUrl = result.replace("<script>", "");
            toUrl = toUrl.replace("</script>", "");
            toUrl = toUrl.replace("\"", "");
            toUrl = toUrl.replace("location.href=", "");
            result = HttpClientUtil.reqGet(toUrl,"",null, null, "");
            System.out.println("#toUrl:"+toUrl);
            System.out.println("#HttpClient:test_reqGet returned string:"+result);
        }

        String result2 = JsoupUtil.getTaggedValue(result, ".view_text #textBody");
        System.out.println("#getTaggedValue:"+result2.toString());
    }

    @Test
    public void test_reqGet_news_imnews_imbc_com() throws Exception {
        String result = HttpClientUtil.reqGet("http://imnews.imbc.com/replay/2017/nwdesk/article/4451561_21408.html"
                ,"",null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedAttr(result, "meta[property=og:description]", "content");
        System.out.println("#getTaggedValue:"+result2.toString());
    }

    @Test
    public void test_reqGet_news_kns() throws Exception {
        String result = HttpClientUtil.reqGet("http://www.kns.tv/news/articleView.html?idxno=371473"
                ,"",null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedAttr(result, "meta[property=og:description]", "content");
        System.out.println("#getTaggedValue:"+result2.toString());
    }

    @Test
    public void test_reqGet_news_jtbc_co_kr() throws Exception {
        String result = HttpClientUtil.reqGet("http://news.jtbc.joins.com/html/634/NB11545634.html"
                ,"", Charset.forName("UTF-8"), null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, "#article .article_content");
        System.out.println("#getTaggedValue:"+result2.toString());
    }

    @Test
    public void test_reqGet_yonhapnewstv() throws Exception {
        String result = HttpClientUtil.reqGet("http://www.yonhapnewstv.co.kr/MYH20171105004600038/?did=1825m"
                ,"", null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, "#content .entry-content");
        System.out.println("#getTaggedValue:"+result2.toString());
    }

    @Test
    public void test_reqGet_redian() throws Exception {
        String result = HttpClientUtil.reqGet("http://www.redian.org/archive/116109"
                ,"", null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, "#wrap .redian-view-text");
        System.out.println("#getTaggedValue:"+result2.toString());
    }

    @Test
    public void test_reqGet_segye() throws Exception {
        String subLinkUrl  = "http://www.segye.com/content/html/2017/11/06/20171106002634.html?OutUrl=naver";
        String subContent = HttpClientUtil.reqGet(subLinkUrl
                ,"", null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+subContent);
        String result2 = JsoupUtil.getTaggedValue(subContent, ".news_article #article_txt");

        if (subContent.trim().length() < 200
                && subContent.trim().startsWith("<script") && subContent.trim().endsWith("script>")
                && subContent.contains("location.href=")) {
            String toUrl = subContent.trim().replace("<script>", "");
            toUrl = toUrl.replace("<script type='text/javascript'>", "");
            toUrl = toUrl.replace("</script>", "");
            toUrl = toUrl.replace("\"", "");
            toUrl = toUrl.replace("\'", "");
            toUrl = toUrl.replace(";", "");
            toUrl = toUrl.replace("location.href=", "");

            toUrl = toUrl.trim();
            if(toUrl.startsWith("/")) {
                String dns[] = subLinkUrl.split("/");
                if (dns != null && dns.length > 2) {
                    toUrl = dns[0] + "/" + dns[1] + "/"+ dns[2] + toUrl;
                }
            }
            subContent = HttpClientUtil.reqGet(toUrl,"",null, null, "");
            System.out.println("#NaverNews.Redirection toURL:"+toUrl);
            System.out.println("#NaverNews.Redirection URL result:"+subContent);
        }
        result2 = JsoupUtil.getTaggedValue(subContent, ".news_article #article_txt");

        System.out.println("#getTaggedValue:"+result2.toString());
    }

    @Test
    public void test_reqGet_ohmynews() throws Exception {
        String result = HttpClientUtil.reqGet("http://www.ohmynews.com/NWS_Web/View/at_pg.aspx?CNTN_CD=A0002371393&CMPT_CD=P0010&utm_source=naver&utm_medium=newsearch&utm_campaign=naver_news"
                ,"", null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, ".atc-text");
        System.out.println("#getTaggedValue:"+result2.toString());
    }

    @Test
    public void test_reqGet_biz_heraldcorp() throws Exception {
        String result = HttpClientUtil.reqGet("http://biz.heraldcorp.com/view.php?ud=201608191630210026391_1"
                ,"", null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, ".wrap #articleText");
        System.out.println("#getTaggedValue:"+result2.toString());
    }

    @Test
    public void test_reqGet_hankyung() throws Exception {
        String result = HttpClientUtil.reqGet("http://bntnews.hankyung.com/apps/news?popup=0&nid=04&c1=04&c2=04&c3=00&nkey=201710201039593&mode=sub_view"
                ,"", null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, "#__newsBody__");
        System.out.println("#getTaggedValue:"+result2.toString());

        System.out.println("contains special charactor:"+result2.contains("�"));
    }

    @Test
    public void test_reqGet_news_hankyung() throws Exception {
        String result = HttpClientUtil.reqGet("http://news.hankyung.com/article/2015072290017"
                ,"", Charset.forName("UTF-8"), null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, "#container .hk-news-view");
        System.out.println("#getTaggedValue:"+result2.toString());

        System.out.println("contains special charactor:"+result2.contains("�"));
    }

    @Test
    public void test_reqGet_star_mk() throws Exception {
        String result = HttpClientUtil.reqGet("http://star.mk.co.kr/star_forward.php?no=213387&year=2010"
                ,"", null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, "#artText .read_txt");
        System.out.println("#getTaggedValue size:"+result2.length());
        System.out.println("#getTaggedValue:"+result2.toString());

        System.out.println("contains special charactor:"+result2.contains("�"));
    }

    @Test
    public void test_reqGet_news_naver() throws Exception {
        String result = HttpClientUtil.reqGet("http://news.naver.com/main/read.nhn?mode=LSD&mid=sec&sid1=106&oid=011&aid=0002399545"
                ,"", null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, ".end_ct #articeBody");
        System.out.println("#getTaggedValue size:"+result2.length());
        System.out.println("#getTaggedValue:"+result2.toString());

        System.out.println("contains special charactor:"+result2.contains("�"));
    }

    @Test
    public void test_reqGet_star_hankookilbo() throws Exception {
        String result = HttpClientUtil.reqGet("http://star.hankookilbo.com/#!/home/SVCPost_351198543380480/detail"
                ,"", null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, ".news-container .news-detail-content");
        System.out.println("#getTaggedValue size:"+result2.length());
        System.out.println("#getTaggedValue:"+result2.toString());

        System.out.println("contains special charactor:"+result2.contains("�"));
    }

    @Test
    public void test_reqGet_sports_khan() throws Exception {
        String result = HttpClientUtil.reqGet("http://sports.khan.co.kr/news/sk_index.html?art_id=201709210901003&sec_id=540401&pt=nv"
                ,"", null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, ".art_body");
        System.out.println("#getTaggedValue size:"+result2.length());
        System.out.println("#getTaggedValue:"+result2.toString());

        System.out.println("contains special charactor:"+result2.contains("�"));
    }

    @Test
    public void test_reqGet_www_asiae() throws Exception {
        String result = HttpClientUtil.reqGet("http://www.asiae.co.kr/news/view.htm?idxno=2017101717410512626"
                ,"", null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, ".article");
        System.out.println("#getTaggedValue size:"+result2.length());
        System.out.println("#getTaggedValue:"+result2.toString());

        System.out.println("contains special charactor:"+result2.contains("�"));
    }

    @Test
    public void test_reqGet_www_ohmynews2() throws Exception {
        String result = HttpClientUtil.reqGet("http://www.ohmynews.com/NWS_Web/View/at_pg.aspx?CNTN_CD=A0002358818&CMPT_CD=P0010&utm_source=naver&utm_medium=newsearch&utm_campaign=naver_news"
                ,"", null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, ".atc_text");
        System.out.println("#getTaggedValue size:"+result2.length());
        System.out.println("#getTaggedValue:"+result2.toString());

        System.out.println("contains special charactor:"+result2.contains("�"));
    }

    @Test
    public void test_reqGet_www_maxmovie() throws Exception {
        String result = HttpClientUtil.reqGet("http://www.maxmovie.com/movie_info/news_read.asp?idx=MI0100914732"
                ,"", null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, ".type-post .inside-article");
        System.out.println("#getTaggedValue size:"+result2.length());
        System.out.println("#getTaggedValue:"+result2.toString());

        System.out.println("contains special charactor:"+result2.contains("�"));
    }

    @Test
    public void test_reqGet_sports_chosun() throws Exception {
        String result = HttpClientUtil.reqGet("http://sports.chosun.com/news/ntype.htm?ut=1&name=/news/entertainment/200812/20081202/8cb30013.htm"
                ,"", null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, ".article .news_content");
        System.out.println("#getTaggedValue size:"+result2.length());
        System.out.println("#getTaggedValue:"+result2.toString());

        System.out.println("contains special charactor:"+result2.contains("�"));
    }

    @Test
    public void test_reqGet_mediapen() throws Exception {
        String result = HttpClientUtil.reqGet("http://www.mediapen.com/news/view/288458"
                ,"", null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, ".view_r");
        System.out.println("#getTaggedValue size:"+result2.length());
        System.out.println("#getTaggedValue:"+result2.toString());

        System.out.println("contains special charactor:"+result2.contains("�"));
    }

    @Test
    public void test_reqGet_edaily() throws Exception {
        String result = HttpClientUtil.reqGet("http://www.edaily.co.kr/news/newspath.asp?newsid=03168486616058152"
                ,"", null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, ".container_box");
        System.out.println("#getTaggedValue size:"+result2.length());
        System.out.println("#getTaggedValue:"+result2.toString());

        System.out.println("contains special charactor:"+result2.contains("�"));
    }

    @Test
    public void test_reqGet_naver_news() throws Exception {
        String result = HttpClientUtil.reqGet("http://news.naver.com/main/read.nhn?mode=LSD&mid=sec&sid1=103&oid=001&aid=0004070880"
                ,"", null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, "#articleBodyContents");
        System.out.println("#getTaggedValue size:"+result2.length());
        System.out.println("#getTaggedValue:"+result2.toString());

        System.out.println("contains special charactor:"+result2.contains("�"));
    }

    @Test
    public void test_reqGet_wwww_hankyung() throws Exception {
        String result = HttpClientUtil.reqGet("http://www.hankyung.com/news/app/newsview.php?aid=2015072290017"
                ,"", null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);
        String result2 = JsoupUtil.getTaggedValue(result, "#articleBodyContents");
        System.out.println("#getTaggedValue size:"+result2.length());
        System.out.println("#getTaggedValue:"+result2.toString());

        System.out.println("contains special charactor:"+result2.contains("�"));
    }

    @Test
    public void test_reqGet_news_daum() throws Exception {
        String result = HttpClientUtil.reqGet("http://search.daum.net/search?w=news&DA=PGD&cluster=y&q=%EC%98%81%ED%99%94+%EC%B1%84%EB%B9%84+%EB%A6%AC%EB%B7%B0&p=2"
                ,"", null, null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result);

        List<String> result2 = JsoupUtil.getTaggedLinkArray(result, "#clusterResultUL li .wrap_tit a", "href");

        System.out.println("#getTaggedValue size:"+result2.size());
        System.out.println("#getTaggedValue:"+result2.toString());
    }

    @Test
    public void test_reqGet_v_media_daum() throws Exception {
        Map<String,Object> result = HttpClientUtil.reqGetHtml("http://v.media.daum.net/v/19961003103400212?f=o"
                ,"", Charset.forName("UTF-8"), null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result.toString());

        String result2 = JsoupUtil.getTaggedValue(result.get("resultStr").toString(), ".article_view");

        //System.out.println("#getTaggedValue size:"+result2.length());
        System.out.println("#getTaggedValue:"+result2.toString());

        System.out.println("contains special charactor:"+result2.contains("�"));
    }

    @Test
    public void jsoup_connect() {
        try {
            Document doc = Jsoup.connect("http://v.media.daum.net/v/20171107110004403?f=o").get();
            System.out.println("#result:"+doc.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test_reqGet_www_yonhapnews() throws Exception {
        Map<String,Object> result = HttpClientUtil.reqGetHtml("http://www.yonhapnews.co.kr/bulletin/2017/11/01/0200000000AKR20171101098700704.HTML?input=1179m"
                ,"", Charset.forName("UTF-8"), null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result.toString());

        //String result2 = JsoupUtil.getTaggedValue(result, ".article");

        //System.out.println("#getTaggedValue size:"+result2.length());
        //System.out.println("#getTaggedValue:"+result2.toString());
    }

    @Test
    public void test_reqGet_tenasia_hankyung() throws Exception {
        Map<String,Object> result = HttpClientUtil.reqGetHtml("http://tenasia.hankyung.com/archives/893739"
                ,"", Charset.forName("UTF-8"), null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result.toString());

        String result2 = JsoupUtil.getTaggedValue(result.get("resultStr").toString(), "#content .entry-content");

        //System.out.println("#getTaggedValue size:"+result2.length());
        System.out.println("#getTaggedValue:"+result2.toString());

        System.out.println("contains special charactor:"+result2.contains("�"));
    }

    @Test
    public void test_reqGet_businesspost() throws Exception {
        Map<String,Object> result = HttpClientUtil.reqGetHtml("http://www.businesspost.co.kr/news/articleView.html?idxno=49545"
                ,"", Charset.forName("UTF-8"), null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result.toString());

        String result2 = JsoupUtil.getTaggedValue(result.get("resultStr").toString(), "#rn_wrap .rns_text");

        //System.out.println("#getTaggedValue size:"+result2.length());
        System.out.println("#getTaggedValue:"+result2.toString());

        System.out.println("contains special charactor:"+result2.contains("�"));
    }

    @Test
    public void test_reqGet_chicnews() throws Exception {
        Map<String,Object> result = HttpClientUtil.reqGetHtml("http://chicnews.co.kr/article.php?aid=1508901801157920011"
                ,"", Charset.forName("UTF-8"), null, "");
        System.out.println("#HttpClient:test_reqGet returned string:"+result.toString());

        String result2 = JsoupUtil.getTaggedValue(result.get("resultStr").toString(), "#container #CmAdContent");

        //System.out.println("#getTaggedValue size:"+result2.length());
        System.out.println("#getTaggedValue:"+result2.toString());

        System.out.println("contains special charactor:"+result2.contains("�"));
    }


    @Test
    public void test_reqGet_moviedaum() throws Exception {
        Map<String,Object> result = HttpClientUtil.reqGetHtml("http://movie.daum.net/moviedb/award?movieId=1172"
                ,"", Charset.forName("UTF-8"), null, "");
        System.out.println("#HttpClient:test_reqGet_moviedaum returned string:"+result.toString());

        String result2 = JsoupUtil.getTaggedValueAllHtml(result.get("resultStr").toString(), ".list_produce");

        //System.out.println("#getTaggedValue size:"+result2.length());
        System.out.println("#getTaggedValue:"+result2);

    }

    @Test
    public void test_reqPost_elasticsearch_testidx() throws Exception {
        Map<String,Object> result = HttpClientUtil.reqGetHtml("http://14.63.170.72:9200/testidx"
                ,"", Charset.forName("UTF-8"), null, "");
        System.out.println("#HttpClient:test_reqGet_moviedaum returned string:"+result.toString());

        String result2 = JsoupUtil.getTaggedValueAllHtml(result.get("resultStr").toString(), ".list_produce");

        //System.out.println("#getTaggedValue size:"+result2.length());
        System.out.println("#getTaggedValue:"+result2);

    }


    @Test
    public void test_reqPost_naverblogss() throws Exception {
        String requrl = "http://blog.naver.com/PostView.nhn?blogId=hikgayon&logNo=221157407783";
        requrl = "http://krdic.naver.com/search.nhn?query=%EC%95%84%EB%A6%84%EB%8B%A4%EC%9A%B4&kind=all";
        Map<String,Object> result = HttpClientUtil.reqGetHtml(requrl
                ,"", Charset.forName("utf-8"), null, "");
        System.out.println("#HttpClient:test_reqGet_moviedaum returned string:"+result.toString());

        String result2 = JsoupUtil.getTaggedValueAll(result.get("resultStr").toString(), ".syn .syno");
        result2 = CommonUtil.removeNumber(result2);

        //System.out.println("#getTaggedValue size:"+result2.length());
        System.out.println("#getTaggedValue:"+result2);

    }

    @Test
    public void test_reqPost_daumblogss() throws Exception {
        String toUrl = "http://blog.daum.net/_blog/BlogTypeMain.do?blogid=0fPuQ&admin=";
        toUrl.replace("/_blog/BlogTypeMain.do", "/_blog/hdn/ArticleContentsView.do");
        toUrl = "http://blog.daum.net/_blog/hdn/ArticleContentsView.do?blogid=0BvMP&articleno=16109338&looping=0&longOpen=";
        toUrl = "http://blog.daum.net/_blog/hdn/ArticleContentsView.do?blogid=0QIzO&articleno=898&looping=0&longOpen=";

        Map<String,Object> result = HttpClientUtil.reqGetHtml(toUrl
                ,"", Charset.forName("utf-8"), null, "");
        System.out.println("#HttpClient:test_reqGet_daumblog returned string:"+result.toString());

        String result2 = JsoupUtil.getTaggedValueAllHtml(result.get("resultStr").toString(), "#cContent #contentDiv");
        System.out.println("#getTaggedValue:"+result2);

        String result3 = JsoupUtil.getTaggedValue(result.get("resultStr").toString(), "#cContent #contentDiv");
        System.out.println("#getTaggedValue:"+result3);

        //System.out.println("#getTaggedValue size:"+result2.length());
        //System.out.println("#getTaggedValue:"+result2);

    }


    @Test
    public void test_reqPost_searchnaverblog() throws Exception {
        String toUrl = "http://search.naver.com/search.naver?where=post&dup_remove=1&start=1&query=%EC%98%81%ED%99%94+%EA%B5%BF%EB%B0%94%EC%9D%B4++%EC%A4%84%EA%B1%B0%EB%A6%AC";

        Map<String,Object> result = HttpClientUtil.reqGetHtml(toUrl
                ,"", Charset.forName("utf-8"), null, "");
        System.out.println("#HttpClient:test_reqGet_daumblog returned string:"+result.toString());

        //String result2 = JsoupUtil.getTaggedValueAllHtml(result.get("resultStr").toString(), "#cContent #contentDiv");
        //System.out.println("#getTaggedValue:"+result2);

        //String result3 = JsoupUtil.getTaggedValue(result.get("resultStr").toString(), "#cContent #contentDiv");
        //System.out.println("#getTaggedValue:"+result3);

        //System.out.println("#getTaggedValue size:"+result2.length());
        //System.out.println("#getTaggedValue:"+result2);

    }

    @Test
    public void test_getNaverMovieSearch() throws Exception {
        String toUrl = "http://movie.naver.com/movie/search/result.nhn?section=movie&query=%C5%E4%B8%A3+%B6%F3%B1%D7%B3%AA%B7%CE%C5%A9";

        Map<String,Object> result1 = HttpClientUtil.reqGetHtml(toUrl
                ,"", Charset.forName("euc_kr"), null, "");
        System.out.println("#HttpClient:test_getNaverMovieSearch returned string:"+result1.toString());

        JsonArray result = new JsonArray();
        //List<String> result2 = JsoupUtil.getTaggedValueArray(result1.get("resultStr").toString(), "#container #content #cbody .search_list_1 li p a");
        //System.out.println("#getTaggedValue:"+result2);

        Document doc = Jsoup.parse(result1.get("resultStr").toString(),"UTF-8");
        Elements summaryElms = doc.select("#container #content #cbody .search_list_1 li");
        //System.out.println("## result Array:: "+summaryElms.toString());
        if (summaryElms != null && summaryElms.size() > 0) {
            JsonObject obj = null;
            for (Element elm : summaryElms) {
                obj = new JsonObject();
                Elements titles = elm.select("dl dt");
                Elements items = elm.select("a");

                //System.out.println("## array All-ITEM:: "+elm.toString());
                //System.out.println("## array ITEM:title:: "+titles.text().toString());
                //System.out.println("## array ITEM:link:: "+items.attr("href").toString());

                String title = titles.text().toString();
                String link = "";
                link = items.attr("href").toString();
                if (!"".equals(link)) link = "movie.naver.com"+link;

                obj.addProperty("title", title);
                obj.addProperty("link", link);

                result.add(obj);
            }
        }

        System.out.println("#resultArr:"+result.toString());
    }


    @Test
    public void test_getDaumMovieSearch() throws Exception {
        String toUrl = "http://movie.daum.net/search/main?returnUrl=http%3A%2F%2Fmovie.daum.net%2Fmain%2Fnew&searchText=%EC%84%9C%ED%8E%B8%EC%A0%9C#searchType=movie&page=1&sortType=acc";

        Map<String,Object> result1 = HttpClientUtil.reqGetHtml(toUrl
                ,"", Charset.forName("utf-8"), null, "");
        System.out.println("#HttpClient:test_getNaverMovieSearch returned string:"+result1.toString());

        JsonArray result = new JsonArray();
        //List<String> result2 = JsoupUtil.getTaggedValueArray(result1.get("resultStr").toString(), "#container #content #cbody .search_list_1 li p a");
        //System.out.println("#getTaggedValue:"+result2);

        Document doc = Jsoup.parse(result1.get("resultStr").toString(),"UTF-8");
        Elements summaryElms = doc.select("#movie_result .movie_join #contents_result li");
        //System.out.println("## result Array:: "+summaryElms.toString());
        if (summaryElms != null && summaryElms.size() > 0) {
            JsonObject obj = null;
            for (Element elm : summaryElms) {
                obj = new JsonObject();
                Elements titles = elm.select("dl dt");
                Elements items = elm.select("a");

                //System.out.println("## array All-ITEM:: "+elm.toString());
                //System.out.println("## array ITEM:title:: "+titles.text().toString());
                //System.out.println("## array ITEM:link:: "+items.attr("href").toString());

                String title = titles.text().toString();
                String link = "";
                link = items.attr("href").toString();
                if (!"".equals(link)) link = "movie.naver.com"+link;

                obj.addProperty("title", title);
                obj.addProperty("link", link);

                result.add(obj);
            }
        }

        System.out.println("#resultArr:"+result.toString());
    }


    @Test
    public void test_getDaumBlogSearch() throws Exception {
        String toUrl = "http://search.daum.net/search?nil_suggest=btn&nil_ch=&rtupcoll=&w=blog&m=board&f=&lpp=10&DA=SBC&sug=&sq=&o=&sugo=&page=91&q=%EC%98%81%ED%99%94+%ED%86%A0%EB%81%BC%EC%99%80+%EA%B1%B0%EB%B6%81%EC%9D%B4+1924+%EC%A4%84%EA%B1%B0%EB%A6%AC";

        Map<String,Object> result1 = HttpClientUtil.reqGetHtml(toUrl
                ,"", Charset.forName("utf-8"), null, "");
        System.out.println("#HttpClient:test_getDaumBlogSearch returned string:"+result1.toString());

        JsonArray result = new JsonArray();
        //List<String> result2 = JsoupUtil.getTaggedValueArray(result1.get("resultStr").toString(), "#container #content #cbody .search_list_1 li p a");
        //System.out.println("#getTaggedValue:"+result2);

        Document doc = Jsoup.parse(result1.get("resultStr").toString(),"UTF-8");
        Elements summaryElms = doc.select(".wrap_cont .cont_inner .wrap_tit");
        //System.out.println("## result Array:: "+summaryElms.toString());
        if (summaryElms != null && summaryElms.size() > 0) {
            JsonObject obj = null;
            for (Element elm : summaryElms) {
                obj = new JsonObject();
                Elements titles = elm.select("dl dt");
                Elements items = elm.select("a");

                //System.out.println("## array All-ITEM:: "+elm.toString());
                //System.out.println("## array ITEM:title:: "+titles.text().toString());
                //System.out.println("## array ITEM:link:: "+items.attr("href").toString());

                String title = titles.text().toString();
                String link = "";
                link = items.attr("href").toString();
                if (!"".equals(link)) link = "movie.naver.com"+link;

                obj.addProperty("title", title);
                obj.addProperty("link", link);

                result.add(obj);
            }
        }

        System.out.println("#resultArr:"+result.toString());
    }

    @Test
    public void test_post_large() throws Exception {
        String txt = "[{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"},{\"meta\":\"엄니\",\"type\":\"who\",\"target_meta\":\"엄니\",\"action\":\"del\"}]";

        String reqUrl = "http://14.63.170.72:8080/pop/meta/upt/array";
        Map<String, Object> req = new HashMap<String, Object>();
        req.put("body",txt);
        req.put("itemid", "16961");
        String result = HttpClientUtil.reqPost(reqUrl, req);
        System.out.println("# returned string:"+result);
    }
}


