package com.kthcorp.cmts.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class JsoupUtil {

    public static String getTaggedValue(String reqStr, String tag) {
        String result = "";
        Document doc = Jsoup.parse(reqStr,"UTF-8");
        //Elements summaryElms = doc.select(tag).removeAttr("Table");
        Elements summaryElms = doc.select(tag);
        //System.out.println(">> summaryElms0 for tag:"+tag+"  ::" + summaryElms.toString());
        //System.out.println(">> summaryElms for tag:"+tag+"  ::" + summaryElms.text().toString());

        if (summaryElms != null && summaryElms.size() > 0) {
            Element summaryElm = summaryElms.get(0);
            if (summaryElm != null && summaryElm.text() != null) {
                //System.out.println(">> items for tag:"+tag+"  ::" + summaryElm.text().toString());
                result = summaryElm.text().toString();
                result = StringUtil.removeAllTags(result);
            }
            if(result.length() < 1) {
                result = summaryElms.text().toString();
                result = StringUtil.removeAllTags(result);
            }
        } else {
            result = "_FAIL";
        }
        return result;
    }

    public static String getTaggedValueAll(String reqStr, String tag) {
        String result = "";
        Document doc = Jsoup.parse(reqStr,"UTF-8");
        //Elements summaryElms = doc.select(tag).removeAttr("Table");
        Elements summaryElms = doc.select(tag);
        //System.out.println(">> summaryElms0 for tag:"+tag+"  ::" + summaryElms.toString());
        //System.out.println(">> summaryElms for tag:"+tag+"  ::" + summaryElms.text().toString());

        if (summaryElms != null && summaryElms.size() > 0) {
            result = summaryElms.text().toString();
            //result = StringUtil.removeAllTags(result);
        } else {
            result = "_FAIL";
        }
        return result;
    }

    public static String getTaggedValueAllHtml(String reqStr, String tag) {
        String result = "";
        Document doc = Jsoup.parse(reqStr,"UTF-8");
        //Elements summaryElms = doc.select(tag).removeAttr("Table");
        Elements summaryElms = doc.select(tag);
        summaryElms.select(".paging_popcorn").remove();
        //System.out.println(">> summaryElms0 for tag:"+tag+"  ::" + summaryElms.toString());
        //System.out.println(">> summaryElms for tag:"+tag+"  ::" + summaryElms.toString());

        if (summaryElms != null && summaryElms.size() > 0) {
            result = summaryElms.toString();
        } else {
            result = "_FAIL";
        }

        return result;
    }

    public static List<String> getTaggedValueArray(String reqStr, String tag) {
        List<String> result = new ArrayList<String>();
        String tmp = "";

        //System.out.println("#JsoupUtil.getTaggedLinkArray process req:"+reqStr.toString());

        Document doc = Jsoup.parse(reqStr,"UTF-8");
        Elements summaryElms = doc.select(tag);
        //System.out.println("#JsoupUtil.getTaggedLinkArray process middle:"
        //        +doc.select(".news .type01 li dt a").attr("href").toString());
        System.out.println("#JsoupUtil.getTaggedValueArray process result:"+summaryElms.toString());

        if (summaryElms != null && summaryElms.size() > 0) {
            for (Element summaryElm : summaryElms) {
                if (summaryElm != null) {
                    System.out.println(">> attr for tag:" + tag + "  ::" + summaryElm.text().toString());
                    tmp = (summaryElm.text() != null ? summaryElm.text().toString() : "");
                    result.add(tmp);
                }
            }
        }
        return result;
    }

    public static String getTaggedAttr(String reqStr, String tag, String attr) {
        String result = "";
        Document doc = Jsoup.parse(reqStr,"UTF-8");
        //Elements summaryElms = doc.select(tag).removeAttr("Table");
        Elements summaryElms = doc.select(tag);
        //System.out.println(">> summaryElms0 for tag:"+tag+"  ::" + summaryElms.toString());
        //System.out.print(">> summaryElms for tag:"+tag+"  ::" + summaryElms.text().toString());

        if (summaryElms != null && summaryElms.size() > 0) {
            Element summaryElm = summaryElms.get(0);
            if (summaryElm != null && summaryElm.attr(attr) != null) {
                //System.out.println(">> items for tag:"+tag+"  ::" + summaryElm.text().toString());
                result = summaryElm.attr(attr).toString();
                result = StringUtil.removeAllTags(result);
            }
        }
        return result;
    }

    public static String getTaggedLink(String reqStr, String tag) {
        String result = "";
        Document doc = Jsoup.parse(reqStr,"UTF-8");
        Elements summaryElms = doc.select(tag);
        if (summaryElms != null && summaryElms.size() > 0) {
            Element summaryElm = summaryElms.get(0);
            if (summaryElm != null && summaryElm.text() != null) {
                System.out.println(">> link for tag:"+tag+"  ::" + summaryElm.attr("href").toString());
                result = summaryElm.attr("href").toString();
            }
        }
        return result;
    }


    public static List<String> getTaggedLinkArray(String reqStr, String tag, String attr) {
        List<String> result = new ArrayList<String>();
        String tmp = "";

        //System.out.println("#JsoupUtil.getTaggedLinkArray process req:"+reqStr.toString());

        Document doc = Jsoup.parse(reqStr,"UTF-8");
        Elements summaryElms = doc.select(tag);
        //System.out.println("#JsoupUtil.getTaggedLinkArray process middle:"
        //        +doc.select(".news .type01 li dt a").attr("href").toString());
        System.out.println("#JsoupUtil.getTaggedLinkArray process result:"+summaryElms.toString());

        if (summaryElms != null && summaryElms.size() > 0) {
            for (Element summaryElm : summaryElms) {
                if (summaryElm != null && summaryElm.attr(attr) != null) {
                    System.out.println(">> attr for tag:" + tag + "  ::" + summaryElm.attr(attr).toString());
                    tmp = summaryElm.attr(attr).toString();
                    result.add(tmp);
                }
            }
        }
        return result;
    }


    public static String getTaggedValueGoogleTrans(String reqStr, String tag) {
        String result = "";
        Document doc = Jsoup.parse(reqStr,"UTF-8");
        System.out.println(">> reqSrtr:"+reqStr);
        Elements summaryElms = doc.select(tag).removeAttr("Table");
        //System.out.print(">> summaryElms for tag:"+tag+"  ::" + summaryElms.text().toString());

        if (summaryElms != null && summaryElms.size() > 0) {
            Element summaryElm = summaryElms.get(0);
            if (summaryElm != null && summaryElm.text() != null) {
                //System.out.println(">> items for tag:"+tag+"  ::" + summaryElm.text().toString());
                result = summaryElm.text().toString();
            }
        }
        return result;
    }
}
