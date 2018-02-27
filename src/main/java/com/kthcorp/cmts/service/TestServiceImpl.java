package com.kthcorp.cmts.service;

import com.kthcorp.cmts.model.CcubeContent;
import com.kthcorp.cmts.model.ItemsContent;
import com.kthcorp.cmts.model.NlpProgs;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface TestServiceImpl {
    void getTest1() throws Exception;

    public String getTest();

    List<NlpProgs> getNlpProgs() throws Exception;

    int prcFileUpload() throws Exception;

    int processFileToNlpsResult(MultipartFile uploadfile) throws Exception;

    int processFileToNlpsResult2(String uploadfileName) throws Exception;

    Map<String, Object> processFileToNlpsSome(MultipartFile uploadfile, int limit) throws Exception;

    int insMovieCine21List();

    List<ItemsContent> getItemsContent();

    void writeCine21Csv();

    List<ItemsContent> getItemsContentProc();

    List<ItemsContent> getItemsContentProc2();

    void insert_from_loadDataCine21Words() throws Exception;

    void insert_from_loadDataCine21_metas() throws Exception;

    int insYjItems();

    List<Map<String, Object>> loadYjDatas(String fileName) throws Exception;

    List<CcubeContent> loadCcubeMoviesDatas() throws Exception;

    List<CcubeContent> loadCcubeMoviesDatas0226() throws Exception;

    void insCcubeMovies(List<CcubeContent> reqList);

    List<ItemsContent> getItemsCine21Second520();

    List<List<ItemsContent>> getSeperatedContent(List<ItemsContent> reqList, int limit);

    void writeCine21Csv2_520();

    List<ItemsContent> getItemsCine21SecondFor10AndContentall();

    void writeCine21Csv2_520SecondFor10AndContentall();

    List<ItemsContent> getItemsYj01by01();

    void writeYj01by01();

    void insDicNotUseWords_phase1();

    List<ItemsContent> getItemsCine21();

    void writeCine21();

    List<String> loadExcelDatas1(String fileName) throws Exception;


    List<String> processResultListToRowsFiltered(
            List<Map<String, Object>> sortedArrMap
            , List<String> titlesArr);

    List<String> processResultListToRowsFiltered3(
            List<Map<String, Object>> sortedArrMap
            , List<String> titlesArr
            , List<String> cidArr);

    void writeCine21_1600_sorted();


    List<CcubeContent> getCcubeContents();

    void writeCcubeContents_phase1_24123();

    void write_yjdatas_1st_20180208_sorted();

    void write_yjdatas_2st_20180220_sorted();

    List<List<Map<String, Object>>> getYcDatas1stFromTable();

    void write_ycDatas_1st_20180208_from_table();

    void write_ycDatas_2st_20180220_from_table();

    List<List<Map<String, Object>>> getSeperatedMapList(List<Map<String, Object>> reqList, int limit);

    List<ItemsContent> getItemsYj04();

    void writeYj04();

    List<Map<String, Object>> getYcDatas1st2();

    List<Map<String, Object>> getYcDatas2st2();

    void insDicKeywords__FromYcDatas1st1() throws Exception;

    void insDicKeywords__FromYcDatas1st2() throws Exception;

    List<CcubeContent> loadCcubeMoviesDatas0226_2() throws Exception;

    void insCcubeMovies0226(List<CcubeContent> reqList);

    void writeDicEmo0227() throws Exception;
}
