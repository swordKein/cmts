package com.kthcorp.cmts.mapper;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.model.CcubeContent;
import com.kthcorp.cmts.model.Items;
import com.kthcorp.cmts.model.ItemsContent;
import com.kthcorp.cmts.util.JsonUtil;
import com.kthcorp.cmts.util.MapUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@MybatisTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class ItemsMapperTest {
    @Autowired
    private ItemsMapper itemsMapper;

    @Test
    @Rollback(false)
    public void test_getItems() throws Exception {
        List<Items> result = itemsMapper.getItems();
        System.out.println("#iresult:" + result.toString());
    }

    @Test
    @Rollback(false)
    public void test_getPagedItems() throws Exception {
        Items req = new Items();
        req.setPageNo(1);
        req.setPageSize(10);

        List<Items> result = itemsMapper.getPagedItems(req);
        System.out.println("#iresult:" + result.toString());
    }

    @Test
    @Rollback(false)
    public void test_countItems() throws Exception {
        Items req = new Items();
        req.setTitle("킹콩");
        Integer result = itemsMapper.countItems(req);
        System.out.println("#iresult:" + result.toString());
    }

    @Test
    @Rollback(false)
    public void test_getItemsByIdx() throws Exception {
        Items req = new Items();
        req.setIdx(1);
        Items result = itemsMapper.getItemsByIdx(req);
        System.out.println("#iresult:" + result.toString());
    }


    @Test
    @Rollback(false)
    public void test_insItems() throws Exception {
        Items req = new Items();
        req.setCid("");
        req.setTitle("블라인드 2012");
        req.setRegid("ghkdwo77");
        req.setStat("Y");
        req.setType("movie");
        int result = itemsMapper.insItems(req);
        System.out.println("#iresult:" + result);
    }

    @Test
    public void test_getItemsContent() throws Exception {
        List<ItemsContent> result = itemsMapper.getItemsContent();
        System.out.println("#result:"+result.toString());
    }

    @Test
    public void test_getItemsCine21Second520() throws Exception {
        List<ItemsContent> result = itemsMapper.getItemsCine21Second520();
        List<Map<String, Object>> newArr = new ArrayList();

        for(ItemsContent ic : result) {
            String content = ic.getContent();

            JsonObject resObj = new Gson().fromJson(content, JsonObject.class);
            //System.out.println("#result:"+resObj.toString());

            String whenStr = resObj.get("WHEN").getAsString();
            String whereStr = resObj.get("WHERE").getAsString();
            String whatStr = resObj.get("WHAT").getAsString();
            String whoStr = resObj.get("WHO").getAsString();
            String emotionStr = resObj.get("EMOTION").getAsString();
            JsonObject notmapObj = resObj.get("notKeywordMappingResult").getAsJsonObject();
            List<String> notmapList = JsonUtil.convertJsonObjectToArrayList(notmapObj, 100);

            System.out.println("#notmapList:"+notmapList.toString()+" ::size::"+notmapList.size());


        }
        //System.out.println("#result:"+result.toString());
    }

    @Test
    public void test_getMovieCine21ByTitle() throws Exception {
        ItemsContent result = itemsMapper.getMovieCine21ByTitle("12몽키즈");

        System.out.println("#result:"+result.toString());
    }

    @Test
    public void test_getItemsInfoByIdx() throws Exception {
        Items result = itemsMapper.getItemsInfoByIdx(10405);
        System.out.println("#result:"+result.toString());
    }

    @Test
    public void test_searchItemsPaging() throws Exception {
        Items req = new Items();
        //req.setSearchType("SER");
        req.setSearchStat("ALL");
        //req.setSearchSdate("2017-12-01 00:00:00");
        //req.setSearchEdate("2018-02-01 00:00:00");
        req.setSearchKeyword("결정적");

        List<String> serarchPartsArr = new ArrayList<String>();
        //serarchPartsArr.add("title");
        req.setSearchTitleYn("Y");
        //serarchPartsArr.add("genre");
        //serarchPartsArr.add("METASWHEN");
        //serarchPartsArr.add("METASEMOTION");
        //req.setSearchPartsArr(serarchPartsArr);
        req.setPageSize(50);
        req.setPageNo(1);

        List<Items> result = itemsMapper.searchItemsPaging(req);
        System.out.println("#result:"+result.toString());
        System.out.println("#result.size:"+result.size());

        System.out.println("#allcount:"+ itemsMapper.countItems(req));

        System.out.println("#countItemsPagingByStat:"+itemsMapper.countItemsPagingByStat(req));
    }

    @Test
    public void test_searchTestPaging() throws Exception {
        Items req = new Items();
        req.setSearchKeyword("공포");
        req.setSearchParts("title,genre,METASWHEN,METAEMOTION");
        List<String> serarchPartsArr = new ArrayList<String>();
        serarchPartsArr.add("title");
        serarchPartsArr.add("genre");
        serarchPartsArr.add("METASWHEN");
        serarchPartsArr.add("METASEMOTION");
        req.setSearchTagsArr(serarchPartsArr);

        List<Items> result = itemsMapper.searchTestMetas(req);
        System.out.println("#result:"+result.toString());
        System.out.println("#result.size:"+result.size());
    }
}
