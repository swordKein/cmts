package com.kthcorp.cmts.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.mapper.*;
import com.kthcorp.cmts.model.*;
import com.kthcorp.cmts.service.crawl.NaverMovieService;
import com.kthcorp.cmts.util.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.InetAddress;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TestService implements TestServiceImpl {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TestMapper testMapper;
    @Autowired
    private NlpProgsMapper nlpProgsMapper;
    @Autowired
    private MovieCine21Mapper movieCine21Mapper;
    @Autowired
    private ItemsMapper itemsMapper;
    @Autowired
    private DicService dicService;
    @Autowired
    private CcubeMapper ccubeMapper;
    @Autowired
    private DicKeywordsMapper dicKeywordsMapper;
    @Autowired
    private ItemsMetasMapper itemsMetasMapper;
    @Autowired
    private ItemsTagsMapper itemsTagsMapper;
    @Autowired
    private ItemsService itemsService;
    @Autowired
    private ItemsTagsService itemsTagsService;
    @Autowired
    private NaverMovieService naverMovieService;
    @Autowired
    private CcubeService ccubeService;
    @Autowired
    private SftpService sftpService;

    @Value("${spring.static.resource.location}")
    private String UPLOAD_DIR;
    @Value("${spring.static.resource.location}")
    private String WORK_DIR;

    @Value("${elasticsearch.host}")
    private String ES_HOST;
    @Value("${elasticsearch.port}")
    private String ES_PORT;

    final String seperator = "\t";
    final String lineFeed = System.getProperty("line.separator");

    @Override
    public void getTest1() throws Exception {
        TestVO req = new TestVO();
        List<TestVO> result = nlpProgsMapper.getTest1(req);

        File targetFile = new File(UPLOAD_DIR + "CINE21_100_WORDS.csv");
        targetFile.createNewFile();
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile.getPath()), "euc_kr"));

        String lineFeed = System.getProperty("line.separator");

        for(TestVO vo : result) {

            String desc = (vo != null && vo.getDescript() != null) ? vo.getDescript() : "";


            String contall = (vo != null && vo.getContent() != null) ? vo.getContent() : "";
            contall = contall.replace("{", "");
            contall = contall.replace("}", "");
            String[] conts = null;
            if (contall.contains(",")) {
                conts = contall.split(",");
            }
            for (String ct : conts) {
                //System.out.println("# "+desc+" :: "+ct);
                String outLine = "";
                outLine += desc;
                outLine += ",";
                outLine += ct;
                outLine += lineFeed;
                System.out.print("# outline ::"+outLine);

                output.write(outLine);
                //Thread.sleep(200);
            }
            //System.out.println("");

            //output.write(outLine+lineFeed);
        }

        output.close();
        //return result;
    }

    @Override
    public String getTest() {
        String r3 = testMapper.getCurrentDateTime();
        System.out.println("#Test Service result:"+r3);

        return "2";
    }

    @Override
    public List<NlpProgs> getNlpProgs() throws Exception {
        return nlpProgsMapper.getNlpProgs();
    }



    @Override
    public int prcFileUpload() throws Exception {
        MultipartFile file1 = FileUtils.convertFileToMultipart("E:\\upload\\", "test_csv.txt");
        int rt = processFileToNlpsResult(file1);
        return rt;
    }

    @Override
    public int processFileToNlpsResult(MultipartFile uploadfile) throws Exception {
        List<MultipartFile> files1 = new ArrayList<MultipartFile>();
        files1.add(uploadfile);
        FileUtils.saveUploadedFiles(files1, UPLOAD_DIR);

        //FileReader fileReader = new FileReader(UPLOAD_DIR + uploadfile.getOriginalFilename());
        FileInputStream fis1 = new FileInputStream(UPLOAD_DIR + uploadfile.getOriginalFilename());
        long filelines = FileUtils.getLineCountFromFis(fis1);
        System.out.println("#uploaded file linecnt:"+filelines);

        FileInputStream fileStream = new FileInputStream(UPLOAD_DIR + uploadfile.getOriginalFilename());
        String detectCharset = DetectEncoding.guessEncoding(fileStream);
        System.out.println("#uploaded file charset:"+detectCharset);

        String fileName = uploadfile.getOriginalFilename();
        String toFileName = FileUtils.getGenFileName(fileName, "nlp");

        /* 경과 등록 */
        NlpProgs np = new NlpProgs();
        np.setFilename(fileName);
        np.setOutfilename(toFileName);
        np.setFilelines((long) filelines);
        np.setFilecharset(detectCharset);
        np.setStat("Y");

        int rtins = nlpProgsMapper.insNlpProgs(np);
        int nlpIdx = np.getIdx();
        System.out.println("#processFileToNlpsResult inserted NLP_PROGS:: idx:"+nlpIdx);

        FileInputStream fis = new FileInputStream(UPLOAD_DIR + uploadfile.getOriginalFilename());
        InputStreamReader isr = new InputStreamReader(fis,detectCharset);
        BufferedReader bufferedReader = new BufferedReader(isr);

        String line = "";
        long lineCnt = 0;
        List<Map<String, Object>> resultMapArr = new ArrayList<Map<String, Object>>();

        File targetFile = new File(UPLOAD_DIR + toFileName);
        targetFile.createNewFile();
        //BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile.getPath()), detectCharset));
        //WINDOWS를 위해 euc-kr 로 저장
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile.getPath()), "euc-kr"));

        String lineFeed = System.getProperty("line.separator");
        int rtupt = 0;

        while((line = bufferedReader.readLine()) != null && lineCnt < 60000) {
            System.out.println("#uploaded::"+lineCnt+" 'th data:"+line);
            String[] lines = line.split(",");
            String outLine = "";
            outLine += lines[0].trim() + "\t";
            outLine += lines[1] + "\t";
            if (lineCnt == 0) {
                outLine += "PUMSA_WORDS";
            } else {
                ArrayList<ArrayList<String>> nlp_result = SeunjeonUtil.getSimpleWords2(lines[1], null);
                //System.out.println("#nlp_result:"+nlp_result.toString());
                String tmp = "";
                for(ArrayList<String> res : nlp_result) {
                    tmp += " " + CommonUtil.removeTex(res.get(3));
                }
                Map<String, Integer> pumsa_words1 = new HashMap<String, Integer>();
                pumsa_words1 = WordFreqUtil.getWordCountsMapByMap(pumsa_words1, tmp);
                Map<String, Integer> pumsa_words2 = new HashMap<String, Integer>();
                pumsa_words2 = MapUtil.sortByValue(pumsa_words1);
                outLine += pumsa_words2.toString();
                System.out.println("#NLP writing:"+pumsa_words2.toString());
            }

            System.out.println("#writing  ::"+lineCnt+" 'th data:"+outLine);
            //output.write(line+lineFeed);
            output.write(outLine+lineFeed);
            if (lineCnt % 1 == 0 || lineCnt >= filelines) {
                NlpProgs np2 = new NlpProgs();
                np2.setIdx(nlpIdx);
                np2.setLinecnt(lineCnt + 1);
                rtupt = nlpProgsMapper.uptNlpProgs(np2);
                System.out.println("#processFileToNlpsResult updated NLP_PROGS:: lineCnt:"+lineCnt+1 +"    for idx:"+nlpIdx);
            }

            lineCnt++;
        }

        output.close();

        System.out.println("#processFileToNlpsResult updated NLP_PROGS:: END! outfile:"+toFileName);
        return rtupt;

    }



    @Override
    public int processFileToNlpsResult2(String uploadfileName) throws Exception {

        //FileReader fileReader = new FileReader(UPLOAD_DIR + uploadfile.getOriginalFilename());
        FileInputStream fis1 = new FileInputStream(UPLOAD_DIR + uploadfileName);
        long filelines = FileUtils.getLineCountFromFis(fis1);
        System.out.println("#uploaded file linecnt:"+filelines);

        FileInputStream fileStream = new FileInputStream(UPLOAD_DIR + uploadfileName);
        String detectCharset = DetectEncoding.guessEncoding(fileStream);
        System.out.println("#uploaded file charset:"+detectCharset);

        String fileName = uploadfileName;
        String toFileName = FileUtils.getGenFileName(fileName, "nlp");

        /* 경과 등록 */
        NlpProgs np = new NlpProgs();
        np.setFilename(fileName);
        np.setOutfilename(toFileName);
        np.setFilelines((long) filelines);
        np.setFilecharset(detectCharset);
        np.setStat("Y");

        int rtins = nlpProgsMapper.insNlpProgs(np);
        int nlpIdx = np.getIdx();
        System.out.println("#processFileToNlpsResult inserted NLP_PROGS:: idx:"+nlpIdx);

        FileInputStream fis = new FileInputStream(UPLOAD_DIR + uploadfileName);
        InputStreamReader isr = new InputStreamReader(fis,detectCharset);
        BufferedReader bufferedReader = new BufferedReader(isr);

        String line = "";
        long lineCnt = 0;
        List<Map<String, Object>> resultMapArr = new ArrayList<Map<String, Object>>();

        File targetFile = new File(UPLOAD_DIR + toFileName);
        targetFile.createNewFile();
        //BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile.getPath()), detectCharset));
        //WINDOWS를 위해 euc-kr 로 저장
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile.getPath()), "euc-kr"));

        String lineFeed = System.getProperty("line.separator");
        int rtupt = 0;

        while((line = bufferedReader.readLine()) != null && lineCnt < 60000) {
            System.out.println("#uploaded::"+lineCnt+" 'th data:"+line);
            String[] lines = line.split(",");
            String outLine = "";
            outLine += lines[0].trim() + "\t";
            outLine += lines[1] + "\t";
            if (lineCnt == 0) {
                outLine += "PUMSA_WORDS";
            } else {
                ArrayList<ArrayList<String>> nlp_result = SeunjeonUtil.getSimpleWords2(lines[1], null);
                //System.out.println("#nlp_result:"+nlp_result.toString());
                String tmp = "";
                for(ArrayList<String> res : nlp_result) {
                    tmp += " " + CommonUtil.removeTex(res.get(3));
                }
                Map<String, Integer> pumsa_words1 = new HashMap<String, Integer>();
                pumsa_words1 = WordFreqUtil.getWordCountsMapByMap(pumsa_words1, tmp);
                Map<String, Integer> pumsa_words2 = new HashMap<String, Integer>();
                pumsa_words2 = MapUtil.sortByValue(pumsa_words1);
                outLine += pumsa_words2.toString();
                System.out.println("#NLP writing:"+pumsa_words2.toString());
            }

            System.out.println("#writing  ::"+lineCnt+" 'th data:"+outLine);
            //output.write(line+lineFeed);
            output.write(outLine+lineFeed);
            if (lineCnt % 1 == 0 || lineCnt >= filelines) {
                NlpProgs np2 = new NlpProgs();
                np2.setIdx(nlpIdx);
                np2.setLinecnt(lineCnt + 1);
                rtupt = nlpProgsMapper.uptNlpProgs(np2);
                System.out.println("#processFileToNlpsResult updated NLP_PROGS:: lineCnt:"+lineCnt+1 +"    for idx:"+nlpIdx);
            }

            lineCnt++;
        }

        output.close();

        System.out.println("#processFileToNlpsResult updated NLP_PROGS:: END! outfile:"+toFileName);
        return rtupt;

    }


    @Override
    public Map<String, Object> processFileToNlpsSome(MultipartFile uploadfile, int limit) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        List<MultipartFile> files1 = new ArrayList<MultipartFile>();
        files1.add(uploadfile);
        FileUtils.saveUploadedFiles(files1, UPLOAD_DIR);

        //FileReader fileReader = new FileReader(UPLOAD_DIR + uploadfile.getOriginalFilename());
        FileInputStream fis1 = new FileInputStream(UPLOAD_DIR + uploadfile.getOriginalFilename());
        long filelines = FileUtils.getLineCountFromFis(fis1);
        System.out.println("#processFileToNlpsSome file linecnt:"+filelines);

        FileInputStream fileStream = new FileInputStream(UPLOAD_DIR + uploadfile.getOriginalFilename());
        String detectCharset = DetectEncoding.guessEncoding(fileStream);
        System.out.println("#processFileToNlpsSome file charset:"+detectCharset);

        String line = "";
        long lineCnt = 0;
        FileInputStream fis = new FileInputStream(UPLOAD_DIR + uploadfile.getOriginalFilename());
        InputStreamReader isr = new InputStreamReader(fis,detectCharset);
        BufferedReader bufferedReader = new BufferedReader(isr);

        String lineFeed = System.getProperty("line.separator");

        List<String> resultIdArr = new ArrayList<String>();
        List<String> resultOrigArr = new ArrayList<String>();
        List<String> resultParsedArr = new ArrayList<String>();

        while((line = bufferedReader.readLine()) != null && lineCnt < filelines && lineCnt < limit) {
            System.out.println("#uploaded::"+lineCnt+" 'th data:"+line);
            String[] lines = line.split(",");

            resultIdArr.add(lines[0].trim());
            resultOrigArr.add(lines[1]);
            if (lineCnt == 0) {
                resultParsedArr.add("PUMSA_WORDS");
            } else {
                ArrayList<ArrayList<String>> nlp_result = SeunjeonUtil.getSimpleWords2(lines[1], null);
                //System.out.println("#nlp_result:"+nlp_result.toString());
                String tmp = "";
                for(ArrayList<String> res : nlp_result) {
                    tmp += " " + CommonUtil.removeTex(res.get(3));
                }
                Map<String, Integer> pumsa_words1 = new HashMap<String, Integer>();
                pumsa_words1 = WordFreqUtil.getWordCountsMapByMap(pumsa_words1, tmp);
                Map<String, Integer> pumsa_words2 = new HashMap<String, Integer>();
                pumsa_words2 = MapUtil.sortByValue(pumsa_words1);
                resultParsedArr.add(pumsa_words2.toString());
                System.out.println("#NLP writing:"+pumsa_words2.toString());
            }
            lineCnt++;
        }

        resultMap.put("resultIdArr", resultIdArr);
        resultMap.put("resultOrigArr", resultOrigArr);
        resultMap.put("resultParsedArr", resultParsedArr);

        return resultMap;
    }

    @Override
    public int insMovieCine21List() {
        int rts = 0;
        int rt = 0;
        try {
            List<MovieCine21> movieList = FileUtils.getCine21Data();

            for (int i=0; i<movieList.size(); i++) {
                rt = movieCine21Mapper.insMovieCine21(movieList.get(i));
                rts += rt;
            }

            /*
            Map<String, Object> movieMap = new HashMap<String, Object>();
            List<MovieCine21> newList = new ArrayList<MovieCine21>();

            for (int i=0; i<movieList.size(); i++) {
                if(i > 0 && i % 100 == 0) {
                    movieMap.put("list", newList);
                    rt = movieCine21Mapper.insMovieCine21List(movieMap);
                    rts += rt;

                    newList = new ArrayList<MovieCine21>();
                    movieMap = new HashMap<String,Object>();
                } else {
                    newList.add(movieList.get(i));
                }
            }
            */

            System.out.println("#orig:movieList.size:"+movieList.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rts;
    }


    @Override
    public List<ItemsContent> getItemsContent() {
        List<ItemsContent> itemsList = itemsMapper.getItemsContent();

        return itemsList;
    }

    @Override
    public void writeCine21Csv() {
        String outContent = "";
        String lineFeed = System.getProperty("line.separator");
        String tab = "\t";

        String outFileName = "E:\\cine21match_180108.tsv";
        try {

            File targetFile = new File(outFileName);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile.getPath()), "euc-kr"));


            List<ItemsContent> itemList = this.getItemsContentProc2();

            int lineCnt = 1;
            for (ItemsContent ic : itemList) {
                outContent += ic.getTitle().trim()
                        + tab + ""
                        + tab + ""
                        + tab + ""
                        + tab;
                lineCnt++;
            }
            outContent += lineFeed;

            bw.write(outContent);
            outContent = "";

            //System.out.println("#proccess outContent:"+outContent);

            for (int line=0; line<200; line++) {
                for (ItemsContent ic : itemList) {
                    String a1 = " ";
                    String b1 = " ";
                    String c1 = " ";
                    String d1 = " ";
                    if(ic != null && ic.getCine21Keywords() != null) {
                        if (line < ic.getCine21Keywords().size() && ic.getCine21Keywords().get(line) != null) {
                            a1 = ic.getCine21Keywords().get(line).toString();
                        }
                    }
                    if(ic != null && ic.getMetaKeywords() != null) {
                        if (line < ic.getMetaKeywords().size() && ic.getMetaKeywords().get(line) != null) {
                            c1 = ic.getMetaKeywords().get(line).toString();
                        }
                    }

                    outContent += a1.trim()
                            + tab + b1
                            + tab + c1.trim()
                            + tab + d1
                            + tab;
                }
                outContent += lineFeed;
                //lineCnt++;

                //System.out.println("#proccess outContent:"+outContent);
                bw.write(outContent);
                outContent = "";
            }


            //FileWriter fw = new FileWriter(outFileName);
            //PrintWriter bw = new PrintWriter(new FileWriter(outFileName));

            //bw.println(outContent);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void writeCine21CsvOld() {
        String outContent = "";
        String lineFeed = System.getProperty("line.separator");
        String tab = "\t";

        String outFileName = "E:\\cine21match_180108.tsv";
        try {

            File targetFile = new File(outFileName);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile.getPath()), "euc-kr"));


            List<ItemsContent> itemList = this.getItemsContentProc();

            outContent =
                    "제목"
                            + tab + "CINE21 키워드"
                            + tab + "키워드순번"
                            + tab + "키워드"
                            + tab + "빈도수"
                            + tab + "매치 유무"
                            + tab + "매치 키워드"
                            + lineFeed;

            int lineCnt = 1;
            for (ItemsContent ic : itemList) {
                outContent += ic.getTitle().trim()
                        + tab + ic.getTitle2().trim().replace(" ","")
                        + tab + ic.getCid()
                        + tab + ic.getKeyword()
                        + tab + ic.getRate()
                        + tab + (ic.isMatchYn() ? "Y" : "...")
                        + tab + ic.getMatchKeywords()
                        + lineFeed;


                if(lineCnt % 1000 == 0) {
                    System.out.println("#proccess lineCnt:"+lineCnt);
                    bw.write(outContent);
                    outContent = "";
                }
                lineCnt++;
            }
            //FileWriter fw = new FileWriter(outFileName);
            //PrintWriter bw = new PrintWriter(new FileWriter(outFileName));

            //bw.println(outContent);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<ItemsContent> getItemsContentProc() {
        List<ItemsContent> itemsList = itemsMapper.getItemsContent();
        List<ItemsContent> newList = new ArrayList<ItemsContent>();

        for (ItemsContent ic : itemsList) {
            if (ic.getContent() != null && ic.getContent().length() > 2) {

                String cine21keyOrig = ic.getTitle2();
                cine21keyOrig = cine21keyOrig.replace("{","");
                cine21keyOrig = cine21keyOrig.replace("}","");
                cine21keyOrig = cine21keyOrig.replace(" ","");
                String cine21s[] = cine21keyOrig.split(",");

                String contentOrig = ic.getContent().toString();
                contentOrig = contentOrig.replace("{","");
                contentOrig = contentOrig.replace("}","");
                contentOrig = contentOrig.replace(" ","");
                String contents[] = contentOrig.split(",");
                int cid = 1;
                List<String> matchKeywords = new ArrayList<String>();
                List<String> notMatchKeywords = new ArrayList<String>();
                for (String mkey : contents) {
                    if (!"".equals(mkey) && mkey.length() > 2) {
                        String mkeys[] = mkey.split("=");
                        //System.out.println("#mkey:: "+mkeys[0]+"="+mkeys[1]+"   //  "+ic.getTitle2());
                        int rate = 0;
                        if (mkeys[1] != null) rate = Integer.parseInt(mkeys[1].replace(".0","").replace(".5",""));

                        ItemsContent item = new ItemsContent();
                        item.setCid(String.valueOf(cid));
                        item.setTitle(ic.getTitle());
                        item.setTitle2(ic.getTitle2());
                        item.setKeyword(mkeys[0]);
                        item.setRate(rate);

                        item.setCine21keySize(cine21s.length);
                        boolean matchYn = false;
                        for(String cinekey : cine21s) {
                            if (cinekey.equals(mkeys[0])) {
                                matchYn = true;
                                break;
                            }
                        }
                        item.setMatchYn(matchYn);

                        if(matchYn) {
                            matchKeywords.add(mkeys[0]);
                        } else {
                            //notMatchKeywords.add(mkeys[0]);
                        }


                        item.setMatchKeywords(matchKeywords.toString());
                        item.setNotMatchKeywords(notMatchKeywords.toString());
                        //System.out.println("#item:"+item.toString());

                        newList.add(item);
                    }

                    if(cid > 499) break;
                    cid++;
                }
            }
        }
        return newList;
    }



    @Override
    public List<ItemsContent> getItemsContentProc2() {
        List<ItemsContent> itemsList = itemsMapper.getItemsContent();
        List<ItemsContent> newList = new ArrayList<ItemsContent>();

        for (ItemsContent ic : itemsList) {
            if (ic.getContent() != null && ic.getContent().length() > 2) {

                String cine21keyOrig = ic.getTitle2();
                cine21keyOrig = cine21keyOrig.replace("{","");
                cine21keyOrig = cine21keyOrig.replace("}","");
                cine21keyOrig = cine21keyOrig.replace(" ","");
                String cine21s[] = cine21keyOrig.split(",");

                String contentOrig = ic.getContent().toString();
                contentOrig = contentOrig.replace("{","");
                contentOrig = contentOrig.replace("}","");
                contentOrig = contentOrig.replace(" ","");
                String contents[] = contentOrig.split(",");
                int cid = 1;
                //List<String> matchKeywords = new ArrayList<String>();
                //List<String> notMatchKeywords = new ArrayList<String>();
                ItemsContent item = new ItemsContent();
                item.setCid(String.valueOf(cid));
                item.setTitle(ic.getTitle());
                item.setTitle2(ic.getTitle2());
                item.setCine21keySize(cine21s.length);

                List<String> cine21Keywords = new ArrayList<String>();
                for (String s : cine21s) {
                    cine21Keywords.add(s);
                }
                item.setCine21Keywords(cine21Keywords);

                List<String> metaKeywords = new ArrayList<String>();
                for (String ss : contents) {
                    String sss[] = ss.split("=");
                    String ds = "";
                    if (sss != null && sss[0] != null) ds = sss[0];
                    metaKeywords.add(ds);
                }
                item.setMetaKeywords(metaKeywords);

                newList.add(item);
            }
        }
        return newList;
    }


    public List<DicKeywords> loadDataCine21Words(String fileName) throws Exception {
        // The name of the file to open.
        //String fileName = "E:\\dics_cine21_100.txt";
        // This will reference one line at a time
        String line = null;

        int cnts[] = {0,0,0,0,0};

        List<DicKeywords> listDicWords = new ArrayList<DicKeywords>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(fileName), "ms949"))) {
            while ((line = reader.readLine()) != null) {
                String lines[] = line.trim().split("\\t");
                int cols = 0;
                /*
                for (String s : lines) {
                    if (!"".equals(s.trim())) System.out.print(s);
                    if (cols % 2 == 1) System.out.print("\n");
                    cols++;
                }
                */

                String word = "";
                String tag = "";
                cols = 0;

                for (String s : lines) {
                    //if (!"".equals(s.trim())) System.out.print(s);
                    if (cols % 2 == 1) {
                        if (!"".equals(s.trim()) && !"".equals(word)) {
                            DicKeywords newWord = new DicKeywords();
                            String dics = "";
                            s = s.trim();

                            switch (s) {
                                case  "시간적 배경" :
                                    dics = "WHEN";
                                    cnts[0]++;
                                    break;
                                case  "공간적 배경" :
                                    dics = "WHERE";
                                    cnts[1]++;
                                    break;
                                case  "주제/소재" :
                                    dics = "WHAT";
                                    cnts[2]++;
                                    break;
                                case  "인물/캐릭터" :
                                    dics = "WHO";
                                    cnts[3]++;
                                    break;
                                case  "감성/분위기" :
                                    dics = "EMOTION";
                                    cnts[4]++;
                                    break;


                            }
                            //newMap.put(s.trim(), word);
                            //newMap.put(dics, word);
                            newWord.setType(dics);
                            newWord.setKeyword(word);
                            newWord.setRegid("ghkdwo77");

                            listDicWords.add(newWord);

                            //tag = word   + ":" + s.trim() ;
                            //System.out.print(tag + "\n");
                            System.out.println("newMap:"+newWord.toString());
                            word = "";
                        }
                    } else {
                        word = s.trim();
                    }
                    cols++;
                }

                //System.out.println(line);
            }

            int i = 1;
            for(int cnt : cnts) {
                System.out.println("# "+i+" 's CNT:"+cnt);
                i++;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return listDicWords;
    }

    @Override
    public void insert_from_loadDataCine21Words() throws Exception {
        List<DicKeywords> listWords = loadDataCine21Words("E:\\dics_cine21_100.txt");

        for(DicKeywords dk : listWords) {
            System.out.println(dk.toString());
            int rt = dicService.insDicKeywords(dk);
        }
    }

    @Override
    public void insert_from_loadDataCine21_metas() throws Exception {
        List<DicKeywords> listWords = loadDataCine21Words("E:\\dics_100_metas.txt");

        for(DicKeywords dk : listWords) {
            System.out.println(dk.toString());
            int rt = dicService.insDicKeywords(dk);
        }
    }

    @Override
    public int insYjItems() {
        int rt = 0;

        try {
            List<Map<String, Object>> reqMap = this.loadYjDatas("E:\\yj_datas.txt");

            int cnt = 0;
            for(Map<String, Object> req : reqMap) {
                if (cnt > 0) {
                    rt = testMapper.insYjItems(req);
                }

                cnt++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rt;
    }

    @Override
    public List<Map<String, Object>> loadYjDatas(String fileName) throws Exception {
        List<Map<String, Object>> resultMap = new ArrayList();
        int cntAll = 0;
        String line = "";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(fileName), "ms949"))) {
            while ((line = reader.readLine()) != null) {
                String lines[] = line.trim().split("\\t");
                Map<String, Object> newItem = new HashMap<String, Object>();

                System.out.println("# size:"+lines.length+" line_All:"+line);

                String title = "";
                String cntet = "";

                title = "yj_id";
                cntet = lines[0];
                cntet = cntet.replace("\"","");
                if(cntet.length() < 3 && cntet.contains("_")) cntet = cntet.trim().replace("_", "");
                newItem.put(title, cntet);

                title = "purity_title";
                cntet = lines[1];
                cntet = cntet.replace("\"","");
                if(cntet.length() < 3 && cntet.contains("_")) cntet = cntet.trim().replace("_", "");
                newItem.put(title, cntet);

                title = "titleshort";
                cntet = lines[2];
                cntet = cntet.replace("\"","");
                if(cntet.length() < 3 && cntet.contains("_")) cntet = cntet.trim().replace("_", "");
                newItem.put(title, cntet);

                title = "year";
                cntet = lines[3];
                cntet = cntet.replace("\"","");
                if(cntet.length() < 3 && cntet.contains("_")) cntet = cntet.trim().replace("_", "");
                newItem.put(title, cntet);

                title = "openday_kr";
                cntet = lines[4];
                cntet = cntet.replace("\"","");
                if(cntet.length() < 3 && cntet.contains("_")) cntet = cntet.trim().replace("_", "");
                newItem.put(title, cntet);

                title = "country_of_origin";
                cntet = lines[5];
                cntet = cntet.replace("\"","");
                if(cntet.length() < 3 && cntet.contains("_")) cntet = cntet.trim().replace("_", "");
                newItem.put(title, cntet);

                title = "director";
                cntet = lines[6];
                cntet = cntet.replace("\"","");
                if(cntet.length() < 3 && cntet.contains("_")) cntet = cntet.trim().replace("_", "");
                newItem.put(title, cntet);

                title = "director_en";
                cntet = lines[7];
                cntet = cntet.replace("\"","");
                if(cntet.length() < 3 && cntet.contains("_")) cntet = cntet.trim().replace("_", "");
                newItem.put(title, cntet);

                title = "actors_display";
                cntet = lines[8];
                cntet = cntet.replace("\"","");
                if(cntet.length() < 3 && cntet.contains("_")) cntet = cntet.trim().replace("_", "");
                newItem.put(title, cntet);

                title = "actors_display_en";
                cntet = lines[9];
                cntet = cntet.replace("\"","");
                if(cntet.length() < 3 && cntet.contains("_")) cntet = cntet.trim().replace("_", "");
                newItem.put(title, cntet);

                title = "yj_grade";
                cntet = lines[10];
                cntet = cntet.replace("\"","");
                if(cntet.length() < 3 && cntet.contains("_")) cntet = cntet.trim().replace("_", "");
                newItem.put(title, cntet);

                resultMap.add(newItem);
                cntAll++;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return resultMap;
    }



    @Override
    public List<CcubeContent> loadCcubeMoviesDatas() throws Exception {
        String fileName = "E:\\CCUBE_MOVIE.txt";
        List<CcubeContent> result = new ArrayList();
        int cntAll = 0;
        String line = "";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(fileName), "ms949"))) {
            while ((line = reader.readLine()) != null
                    //&& cntAll < 10000
                    ){
                if (cntAll > 0) {
                    String lines[] = line.trim().split("\\t");
                    CcubeContent newItem = new CcubeContent();
                    newItem.setContent_id(lines[0].trim().replace("\"", "").replace("\'", ""));
                    newItem.setMaster_content_id(lines[1].trim().replace("\"", "").replace("\'", ""));
                    newItem.setPurity_title(lines[2].trim().replace("\"", "").replace("\'", ""));
                    newItem.setContent_title(lines[2].trim().replace("\"", "").replace("\'", ""));
                    newItem.setEng_title(lines[3].trim().replace("\"", "").replace("\'", ""));
                    newItem.setActors_display(lines[4].trim().replace("\"", "").replace("\'", ""));
                    newItem.setDirector(lines[5].trim().replace("\"", "").replace("\'", ""));
                    newItem.setYear( ( lines[6] != null && !"".equals(lines[6]) ? lines[6].trim().replace("\"", "").replace("\'", "") : "0"));
                    newItem.setCountry_of_origin(lines[7].trim().replace("\"", "").replace("\'", ""));
                    newItem.setKt_rating(lines[8].trim().replace("\"", "").replace("\'", ""));
                    if (lines.length > 9) newItem.setKmrb_id(lines[9].trim().replace("\"", "").replace("\'", ""));
                    if (lines.length > 10)
                        newItem.setSad_ctgry_id(lines[10].trim().replace("\"", "").replace("\'", ""));
                    if (lines.length > 11)
                        newItem.setSad_ctgry_nm(lines[11].trim().replace("\"", "").replace("\'", ""));
                    if (lines.length > 12) newItem.setPoster_url(lines[12].trim().replace("\"", "").replace("\'", ""));

                    //System.out.println("# size:" + lines.length + " line_All:" + newItem.toString());

                    result.add(newItem);
                }
                cntAll++;
            }

            System.out.println("#allCount:"+cntAll);
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return result;
    }

    @Override
    public void insCcubeMovies(List<CcubeContent> reqList) {
        int rt = 0;

        for(CcubeContent cc : reqList) {
            //System.out.println("#req cc:" + cc.toString());
            if (cc.getKmrb_id().length() < 9) {
                int rt1 = ccubeMapper.insCcubeContent(cc);
            }


            //System.out.println("#req cc:" + cc.toString());
            //System.out.println(" / year:" + Integer.parseInt(cc.getYear()));

            if (cc.getYear().length() > 4
                    || cc.getSad_ctgry_id().length() > 5
                    || ( cc.getDirector().length() < 2 && (cc.getPoster_url() != null && cc.getPoster_url().length() < 10))
                    || cc.getKmrb_id().length() > 8
                    )  {
                System.out.println("#error! req cc:" + cc.toString());
                System.out.println("#error! year size"+cc.getYear().length() + "  /  ctgry_id.size:"+cc.getSad_ctgry_id().length()
                +" / year:" + Integer.parseInt(cc.getYear()));
            }
        }
    }


    public List<String> getAllKeywordMapByType(List<String> resultList, String type, JsonObject reqObj) {
        String whenStr = reqObj.get(type).getAsString();
        List<String> whenList = StringUtil.convertStringToListByComma(whenStr);
        String typeStr = "";
        switch(type) {
            case "WHEN" :
                typeStr = "시간적 배경"; break;
            case "WHERE" :
                typeStr = "공간적 배경"; break;
            case "WHAT" :
                typeStr = "주제/소재"; break;
            case "WHO" :
                typeStr = "인물/캐릭터"; break;
            case "EMOTION" :
                typeStr = "감성/분위기"; break;
        }
        if(whenList != null) {
            for(String s : whenList) {
                resultList.add(s + "\t" + typeStr);
            }
        }
        return resultList;
    }

    public List<String> getAddedNotmapKeywords(List<String> resultList, List<String> notmapList) {
        if (resultList == null) resultList = new ArrayList();
        for (String s : notmapList) {
            if (!"".equals(s.trim())) {
                resultList.add(s + "\t" + "_");
            }
        }
        return resultList;
    }
    @Override
    public List<ItemsContent> getItemsCine21Second520() {
        List<ItemsContent> resultList = new ArrayList();
        List<ItemsContent> itemsList = itemsMapper.getItemsCine21Second520();

        for(ItemsContent ic : itemsList) {
            String content = ic.getContent();
            List<String> allKeywords = new ArrayList<String>();

            JsonObject resObj = new Gson().fromJson(content, JsonObject.class);
            //System.out.println("#result:"+resObj.toString());

            // 시간적 배경
            allKeywords = getAllKeywordMapByType(allKeywords, "WHEN", resObj);
            // 공간적 배경
            allKeywords = getAllKeywordMapByType(allKeywords, "WHERE", resObj);
            // 주제/소재
            allKeywords = getAllKeywordMapByType(allKeywords, "WHAT", resObj);
            // 인물/캐릭터
            allKeywords = getAllKeywordMapByType(allKeywords, "WHO", resObj);
            // 감성/분위기
            allKeywords = getAllKeywordMapByType(allKeywords, "EMOTION", resObj);

            JsonObject notmapObj = resObj.get("notKeywordMappingResult").getAsJsonObject();
            List<String> notmapList = JsonUtil.convertJsonObjectToArrayList(notmapObj, 100);
            allKeywords = getAddedNotmapKeywords(allKeywords, notmapList);
            ic.setAllKeywords(allKeywords);
            //System.out.println("#allKeywordMap:"+allKeywords.toString());
            //System.out.println("#notmapList:"+notmapList.toString()+" ::size::"+notmapList.size());

            resultList.add(ic);
        }
        return resultList;
    }

    @Override
    public List<List<ItemsContent>> getSeperatedContent(List<ItemsContent> reqList, int limit) {
        List<List<ItemsContent>> result = new ArrayList();

        int cnt = 1;
        List<ItemsContent> newList = new ArrayList();
        if (reqList.size() > limit) {


            for(ItemsContent ic : reqList) {
                newList.add(ic);

                if (cnt > 1 && cnt % limit == 0) {
                    result.add(newList);
                    newList = new ArrayList();
                }
                cnt++;
            }
            // 짜투리
            result.add(newList);

        } else {
            result.add(reqList);
        }

        return result;
    }

    @Override
    public void writeCine21Csv2_520() {
        String outContent = "";
        String lineFeed = System.getProperty("line.separator");
        String tab = "\t";

        String outFileNameOrig = "E:\\cine21match2_520_180112";
        try {


            List<ItemsContent> itemList = this.getItemsCine21Second520();
            //List<ItemsContent> itemList = null;
            List<List<ItemsContent>> allItemList = this.getSeperatedContent(itemList, 100);
            System.out.println("#allItemList:"+allItemList.size());

            int lineCnt = 1;
            int itemsNo = 1;
            int xcnt = 0;
            for (List<ItemsContent> itemsListOne : allItemList) {
                String outFileName = outFileNameOrig + "_"+itemsNo+".txt";
                File targetFile = new File(outFileName);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile.getPath()), "euc-kr"));

                xcnt = 0;

                for (ItemsContent item : itemsListOne) {
                    outContent += item.getTitle().trim()
                            + tab + ""
                            + tab + ""
                            + tab + ""
                            + tab;
                    lineCnt++;
                }
                outContent += lineFeed;

                bw.write(outContent);
                System.out.println("#proccess outContent:" + outContent);
                outContent = "";

                for (int line = 0; line < 100; line++) {
                //int line = 0;
                    for (ItemsContent ic : itemsListOne) {
                        String a1 = " ";
                        String b1 = " ";
                        String c1 = " ";
                        //String d1 = " ";
                        String title2 = ic.getTitle2();
                        List<String> cine21Keywords = new ArrayList<String>();
                        if (!"".equals(title2.trim())) {
                            String cine21s[] = title2.split(",");
                            for (String s : cine21s) {
                                cine21Keywords.add(s.trim());
                            }
                            ic.setCine21Keywords(cine21Keywords);
                        }

                        if (ic != null && ic.getCine21Keywords() != null) {
                            if (line < ic.getCine21Keywords().size() && ic.getCine21Keywords().get(line) != null) {
                                a1 = ic.getCine21Keywords().get(line).toString();
                            }
                        }
                        if (ic != null && ic.getAllKeywords() != null) {
                            if (line < ic.getAllKeywords().size() && ic.getAllKeywords().get(line) != null) {
                                c1 = ic.getAllKeywords().get(line).toString();
                            }
                        }

                        outContent += a1.trim()
                                + tab + b1
                                + tab + c1.trim()
                                //+ tab + d1
                                + tab;
                    }
                    outContent += lineFeed;

                    //lineCnt++;

                    //System.out.println("#proccess outContent:"+outContent);
                    bw.write(outContent);
                    outContent = "";
                    xcnt++;
                    //line++;
                }

                System.out.println("#XCNT:"+xcnt);
                itemsNo++;
                bw.close();
            }
            //FileWriter fw = new FileWriter(outFileName);
            //PrintWriter bw = new PrintWriter(new FileWriter(outFileName));

            //bw.println(outContent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<ItemsContent> getItemsCine21SecondFor10AndContentall() {
        List<ItemsContent> resultList = new ArrayList();
        List<ItemsContent> itemsList = itemsMapper.getItemsCine21SecondFor10AndContentall();

        for(ItemsContent ic : itemsList) {
            String content = ic.getContent();
            List<String> allKeywords = new ArrayList<String>();

            JsonObject resObj = new Gson().fromJson(content, JsonObject.class);
            //System.out.println("#result:"+resObj.toString());

            // 시간적 배경
            allKeywords = getAllKeywordMapByType(allKeywords, "WHEN", resObj);
            // 공간적 배경
            allKeywords = getAllKeywordMapByType(allKeywords, "WHERE", resObj);
            // 주제/소재
            allKeywords = getAllKeywordMapByType(allKeywords, "WHAT", resObj);
            // 인물/캐릭터
            allKeywords = getAllKeywordMapByType(allKeywords, "WHO", resObj);
            // 감성/분위기
            allKeywords = getAllKeywordMapByType(allKeywords, "EMOTION", resObj);

            JsonObject notmapObj = resObj.get("notKeywordMappingResult").getAsJsonObject();
            List<String> notmapList = JsonUtil.convertJsonObjectToArrayList(notmapObj, 100);
            allKeywords = getAddedNotmapKeywords(allKeywords, notmapList);
            ic.setAllKeywords(allKeywords);
            //System.out.println("#allKeywordMap:"+allKeywords.toString());
            //System.out.println("#notmapList:"+notmapList.toString()+" ::size::"+notmapList.size());

            int collect_sc_id = 0;
            collect_sc_id = itemsMapper.getCollectSchedId(ic.getSc_id());
            List<ItemsContent> contentList  = itemsMapper.getCollectContentList(collect_sc_id);

            String contentAll = "";
            for(ItemsContent ic2 : contentList) {
                if(ic2.getContent() != null && !"".equals(ic2.getContent())) {
                    contentAll = contentAll + " " + ic2.getContent();
                }
            }

            ic.setContentAll(contentAll);
            System.out.println("#contentAll:"+contentAll);
            resultList.add(ic);
        }
        return resultList;
    }

    @Override
    public void writeCine21Csv2_520SecondFor10AndContentall() {
        String outContent = "";
        String lineFeed = System.getProperty("line.separator");
        String tab = "\t";

        String outFileNameOrig = "E:\\cine21match2_10_180115";

        String outFileContentAll = "E:\\cine21match2_520_180112_contentAll.txt";

        try {

            File targetFileContentAll = new File(outFileContentAll);
            BufferedWriter bwContentAll = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFileContentAll.getPath()), "euc-kr"));


            List<ItemsContent> itemList = this.getItemsCine21SecondFor10AndContentall();
            //List<ItemsContent> itemList = null;
            List<List<ItemsContent>> allItemList = this.getSeperatedContent(itemList, 100);
            System.out.println("#allItemList:"+allItemList.size());

            int lineCnt = 1;
            int itemsNo = 1;
            int xcnt = 0;
            for (List<ItemsContent> itemsListOne : allItemList) {
                String outFileName = outFileNameOrig + "_"+itemsNo+".txt";
                File targetFile = new File(outFileName);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile.getPath()), "euc-kr"));

                xcnt = 0;

                for (ItemsContent item : itemsListOne) {
                    outContent += item.getTitle().trim()
                            + tab + ""
                            + tab + ""
                            + tab + ""
                            + tab;
                    lineCnt++;
                }
                outContent += lineFeed;

                bw.write(outContent);
                System.out.println("#proccess outContent:" + outContent);
                outContent = "";

                String outContentAll = "";
                for (ItemsContent item : itemsListOne) {
                    outContentAll += item.getContentAll().trim();
                    //lineCnt++;

                    outContentAll += lineFeed;
                }

                bwContentAll.write(outContentAll);
                //System.out.println("#proccess outContent:" + outContent);
                outContent = "";

                for (int line = 0; line < 100; line++) {
                    //int line = 0;
                    for (ItemsContent ic : itemsListOne) {
                        String a1 = " ";
                        String b1 = " ";
                        String c1 = " ";
                        //String d1 = " ";
                        String title2 = ic.getTitle2();
                        List<String> cine21Keywords = new ArrayList<String>();
                        if (!"".equals(title2.trim())) {
                            String cine21s[] = title2.split(",");
                            for (String s : cine21s) {
                                cine21Keywords.add(s.trim());
                            }
                            ic.setCine21Keywords(cine21Keywords);
                        }

                        if (ic != null && ic.getCine21Keywords() != null) {
                            if (line < ic.getCine21Keywords().size() && ic.getCine21Keywords().get(line) != null) {
                                a1 = ic.getCine21Keywords().get(line).toString();
                            }
                        }
                        if (ic != null && ic.getAllKeywords() != null) {
                            if (line < ic.getAllKeywords().size() && ic.getAllKeywords().get(line) != null) {
                                c1 = ic.getAllKeywords().get(line).toString();
                            }
                        }

                        outContent += a1.trim()
                                + tab + b1
                                + tab + c1.trim()
                                //+ tab + d1
                                + tab;
                    }
                    outContent += lineFeed;

                    //lineCnt++;

                    //System.out.println("#proccess outContent:"+outContent);
                    bw.write(outContent);
                    outContent = "";
                    xcnt++;
                    //line++;
                }

                System.out.println("#XCNT:"+xcnt);
                itemsNo++;
                bw.close();
            }
            //FileWriter fw = new FileWriter(outFileName);
            //PrintWriter bw = new PrintWriter(new FileWriter(outFileName));

            bwContentAll.close();
            //bw.println(outContent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<ItemsContent> getItemsYj01by01() {
        List<ItemsContent> resultList = new ArrayList();
        List<ItemsContent> itemsList = itemsMapper.getItemsYj01by01();

        for(ItemsContent ic : itemsList) {
            String content = ic.getContent();
            List<String> allKeywords = new ArrayList<String>();

            JsonObject resObj = new Gson().fromJson(content, JsonObject.class);
            //System.out.println("#result:"+resObj.toString());

            /*
            // 시간적 배경
            allKeywords = getAllKeywordMapByType(allKeywords, "WHEN", resObj);
            // 공간적 배경
            allKeywords = getAllKeywordMapByType(allKeywords, "WHERE", resObj);
            // 주제/소재
            allKeywords = getAllKeywordMapByType(allKeywords, "WHAT", resObj);
            // 인물/캐릭터
            allKeywords = getAllKeywordMapByType(allKeywords, "WHO", resObj);
            // 감성/분위기
            allKeywords = getAllKeywordMapByType(allKeywords, "EMOTION", resObj);
*/
            JsonObject notmapObj = resObj.get("notKeywordMappingResult").getAsJsonObject();
            List<String> notmapList = JsonUtil.convertJsonObjectToArrayList(notmapObj, 100);
            allKeywords = getAddedNotmapKeywords(allKeywords, notmapList);
            ic.setAllKeywords(allKeywords);
            //System.out.println("#allKeywordMap:"+allKeywords.toString());
            //System.out.println("#notmapList:"+notmapList.toString()+" ::size::"+notmapList.size());

            resultList.add(ic);
        }
        return resultList;
    }

    @Override
    public void writeYj01by01() {
        String outContent = "";
        String lineFeed = System.getProperty("line.separator");
        String tab = "\t";
        //String tab = "||";

        String outFileNameOrig = "E:\\yj05_4000_180202";
        try {

            List<ItemsContent> itemList = this.getItemsYj01by01();
            int itemCnt = 0;
            if (itemList != null) itemCnt = itemList.size();

            //List<ItemsContent> itemList = null;
            List<List<ItemsContent>> allItemList = this.getSeperatedContent(itemList,50);
            System.out.println("#allItemList:"+allItemList.size());

            int lineCnt = 1;
            int itemsNo = 1;
            int xcnt = 0;
            for (List<ItemsContent> itemsListOne : allItemList) {
                String outFileName = outFileNameOrig + "_"+itemsNo+".txt";
                File targetFile = new File(outFileName);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile.getPath()), "euc-kr"));

                xcnt = 0;

                outContent = "";
                for (ItemsContent item : itemsListOne) {
                    /*
                    outContent += item.getTitle().trim()
                            + tab + ""
                            + tab + ""
                            + tab + ""
                            + tab;
                    */
                    Items item1 = new Items();
                    item1.setIdx(item.getIdx());
                    item1.setType("yj5");
                    item1.setCid(item.getCid());
                    item1.setTitle(item.getTitle());
                    int rtins = itemsMapper.insYjItemsOut(item1);

                    outContent += item.getTitle().trim()
                            + tab + "_"
                            + tab;
                    lineCnt++;
                }
                outContent += lineFeed;

                bw.write(outContent);
                System.out.println("#proccess outContent:" + outContent);
                String outContent2 = "";

                for (int line = 0; line < 100; line++) {
                    //int line = 0;
                    for (ItemsContent ic : itemsListOne) {
                        //System.out.println("#allKeywords:"+ic.getAllKeywords());
                        String a1 = " ";
                        String b1 = " ";
                        String c1 = " ";
                        //String d1 = " ";
                        String title2 = ic.getTitle2();
                        List<String> cine21Keywords = new ArrayList<String>();
                        if (!"".equals(title2.trim())) {
                            String cine21s[] = title2.split(",");
                            for (String s : cine21s) {
                                cine21Keywords.add(s.trim());
                            }
                            ic.setCine21Keywords(cine21Keywords);
                        }


                        a1 = "";
                        if (ic != null && ic.getCine21Keywords() != null) {
                            if (line < ic.getCine21Keywords().size() && ic.getCine21Keywords().get(line) != null) {
                                a1 = ic.getCine21Keywords().get(line).toString();
                            }
                        }
                        c1 = "_" + tab + "_";
                        if (ic != null && ic.getAllKeywords() != null) {
                            if (line < ic.getAllKeywords().size() && ic.getAllKeywords().get(line) != null) {
                                c1 = ic.getAllKeywords().get(line).toString();
                            }
                        }

                        /*
                        outContent2 += a1.trim()
                                + tab + b1
                                + tab + c1.trim()
                                //+ tab + d1
                                + tab;
                                */

                        outContent2 += c1
                                //+ tab + "_"
                                + tab;
                    }
                    outContent2 += lineFeed;
                    //System.out.println("#keyword:"+outContent2);
                    //lineCnt++;

                    //System.out.println("#proccess outContent:"+outContent);
                    bw.write(outContent2);
                    outContent2 = "";
                    xcnt++;
                    //line++;
                }

                System.out.println("#XCNT:"+xcnt);
                itemsNo++;
                bw.close();

                System.out.println("#this itemCnt:"+itemsListOne.size());
            }

            System.out.println("#itemCnt:"+itemCnt);
            //FileWriter fw = new FileWriter(outFileName);
            //PrintWriter bw = new PrintWriter(new FileWriter(outFileName));
            //bw.println(outContent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    @Override
    public void insDicNotUseWords_phase1() {
        try {
            ArrayList<ArrayList<String>> result = FileUtils.getFileSeperatedDataToArray("e:\\dic_not_use.txt", "\\t", 0);
            ArrayList<ArrayList<String>> result2 = new ArrayList();

            for (ArrayList<String> sarr : result) {
                //System.out.println("#result:" + sarr.toString()+" || ");
                if (sarr != null && sarr.size() == 3 && "●".equals(sarr.get(2).trim())) {
                    System.out.println("#xresult:" + sarr.toString());

                    result2.add(sarr);

                    String key = sarr.get(0).trim();
                    DicNotuseWords newWord = new DicNotuseWords();
                    newWord.setWord(key);
                    newWord.setFreq(0.0);
                    newWord.setRegid("ghkdwo77");

                    int rtnu = dicService.insDicNotuseWords(newWord);
                }
            }
            System.out.println("#result.size:"+result2.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<ItemsContent> getItemsCine21() {
        List<ItemsContent> resultList = new ArrayList();
        List<ItemsContent> itemsList = itemsMapper.getItemsCine21();

        for(ItemsContent ic : itemsList) {
            String content = ic.getContent();
            content = content.trim();
            List<String> allKeywords = new ArrayList<String>();

            //JsonObject resObj = new Gson().fromJson(content, JsonObject.class);
            //System.out.println("#result:"+resObj.toString());

            /*
            // 시간적 배경
            allKeywords = getAllKeywordMapByType(allKeywords, "WHEN", resObj);
            // 공간적 배경
            allKeywords = getAllKeywordMapByType(allKeywords, "WHERE", resObj);
            // 주제/소재
            allKeywords = getAllKeywordMapByType(allKeywords, "WHAT", resObj);
            // 인물/캐릭터
            allKeywords = getAllKeywordMapByType(allKeywords, "WHO", resObj);
            // 감성/분위기
            allKeywords = getAllKeywordMapByType(allKeywords, "EMOTION", resObj);
*/
            //JsonObject notmapObj = resObj.get("notKeywordMappingResult").getAsJsonObject();
            //List<String> notmapList = JsonUtil.convertJsonObjectToArrayList(notmapObj, 100);
            //allKeywords = getAddedNotmapKeywords(allKeywords, notmapList);

            if(!"".equals(content)) {
                String words[] = content.split(",");
                if (words != null && words.length > 0) {
                    for(String ws : words) {
                        allKeywords.add(ws.trim() + "\t_");
                    }
                }
            }
            ic.setAllKeywords(allKeywords);
            //System.out.println("#allKeywordMap:"+allKeywords.toString());
            //System.out.println("#notmapList:"+notmapList.toString()+" ::size::"+notmapList.size());

            resultList.add(ic);
        }
        return resultList;
    }



    @Override
    public void writeCine21() {
        String outContent = "";
        String lineFeed = System.getProperty("line.separator");
        String tab = "\t";
        //String tab = "||";

        String outFileNameOrig = "E:\\cine21_1659_180130";
        try {

            List<ItemsContent> itemList = this.getItemsCine21();
            int itemCnt = 0;
            if (itemList != null) itemCnt = itemList.size();

            //List<ItemsContent> itemList = null;
            List<List<ItemsContent>> allItemList = this.getSeperatedContent(itemList,50);
            System.out.println("#allItemList:"+allItemList.size());

            int lineCnt = 1;
            int itemsNo = 1;
            int xcnt = 0;
            for (List<ItemsContent> itemsListOne : allItemList) {
                String outFileName = outFileNameOrig + "_"+itemsNo+".txt";
                File targetFile = new File(outFileName);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile.getPath()), "euc-kr"));

                xcnt = 0;

                outContent = "";
                for (ItemsContent item : itemsListOne) {
                    /*
                    outContent += item.getTitle().trim()
                            + tab + ""
                            + tab + ""
                            + tab + ""
                            + tab;
                    */
                    /*
                    Items item1 = new Items();
                    item1.setIdx(item.getIdx());
                    item1.setType("yj33");
                    item1.setCid(item.getCid());
                    item1.setTitle(item.getTitle());
                    int rtins = itemsMapper.insYjItemsOut(item1);
                    */
                    outContent += item.getTitle().trim()
                            + tab + "_"
                            + tab;
                    lineCnt++;
                }
                outContent += lineFeed;

                bw.write(outContent);
                System.out.println("#proccess outContent:" + outContent);
                String outContent2 = "";

                for (int line = 0; line < 100; line++) {
                    //int line = 0;
                    for (ItemsContent ic : itemsListOne) {
                        //System.out.println("#allKeywords:"+ic.getAllKeywords());
                        String a1 = " ";
                        String b1 = " ";
                        String c1 = " ";
                        //String d1 = " ";
                        String title2 = ic.getTitle2();
                        List<String> cine21Keywords = new ArrayList<String>();
                        /*
                        if (!"".equals(title2.trim())) {
                            String cine21s[] = title2.split(",");
                            for (String s : cine21s) {
                                cine21Keywords.add(s.trim());
                            }
                            ic.setCine21Keywords(cine21Keywords);
                        }
                        */

                        a1 = "";
                        if (ic != null && ic.getCine21Keywords() != null) {
                            if (line < ic.getCine21Keywords().size() && ic.getCine21Keywords().get(line) != null) {
                                a1 = ic.getCine21Keywords().get(line).toString();
                            }
                        }
                        c1 = "_" + tab + "_";
                        if (ic != null && ic.getAllKeywords() != null) {
                            if (line < ic.getAllKeywords().size() && ic.getAllKeywords().get(line) != null) {
                                c1 = ic.getAllKeywords().get(line).toString();
                            }
                        }

                        /*
                        outContent2 += a1.trim()
                                + tab + b1
                                + tab + c1.trim()
                                //+ tab + d1
                                + tab;
                                */

                        outContent2 += c1
                                //+ tab + "_"
                                + tab;
                    }
                    outContent2 += lineFeed;
                    //System.out.println("#keyword:"+outContent2);
                    //lineCnt++;

                    //System.out.println("#proccess outContent:"+outContent);
                    bw.write(outContent2);
                    outContent2 = "";
                    xcnt++;
                    //line++;
                }

                System.out.println("#XCNT:"+xcnt);
                itemsNo++;
                bw.close();

                System.out.println("#this itemCnt:"+itemsListOne.size());
            }

            System.out.println("#itemCnt:"+itemCnt);
            //FileWriter fw = new FileWriter(outFileName);
            //PrintWriter bw = new PrintWriter(new FileWriter(outFileName));
            //bw.println(outContent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public List<String> loadExcelDatas1(String fileName) throws Exception {
        //List<Map<String, Object>> resultMap = new ArrayList();
        List<String> resArr4Map = null;

        //List<String> titlesTabArray = new ArrayList();
        List<List<Map<String,Object>>> arrayResultMap2 = new ArrayList();

        int cntAll = 0;
        String line = "";
        String titles_orig = "";
        List<String> titleArr = new ArrayList();
        String seperator = "\t";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(fileName), "ms949"))) {
            int cnt = 0;
            while ((line = reader.readLine()) != null) {
                List<Map<String, Object>> lineMap = new ArrayList();

                // 타이틀 처리
                if (cnt == 8) {
                    titles_orig = line;
                    //System.out.println("#TITLE:"+titles_orig);
                    String titles[] = titles_orig.trim().split(seperator);
                    int x = 0;
                    for (String tt : titles) {
                        //System.out.println("#title "+x+"'th  ::  "+tt);
                        if (x > 0 && !"".equals(tt.trim())) {
                            //titleArr.add(tt.trim() + seperator + " " + seperator);
                            titleArr.add(tt.trim());
                            //List<Map<String, Object>> newList = new ArrayList();
                            //Map<String,Object> nmap = new HashMap();
                            //nmap.put(tt.trim(), "_");
                            //newList.add(nmap);
                            //arrayResultMap.add(newList);
                        }
                        x++;
                    }

                }

                // 데이터 처리

                if (cnt > 8) {
                    String lines[] = line.trim().split("\\t");
                    int dataCnt = 0;
                    int cnty = 0;
                    // 공백만 있는 라인 제거를 위함
                    for (String ss : lines) {
                        if (cnty > 2 && !ss.trim().equals("")) {
                            dataCnt++;
                        }
                        cnty++;
                    }
                    //Map<String, Object> newItem = new HashMap<String, Object>();

                    // 공백만 있는 라인을 제거
                    if (dataCnt > 0) {
                        int cntz = 1;
                        System.out.println("# size:" + lines.length + " line_All:" + line);

                        // 2칸이 한개의 페어
                        List<Map<String, Object>> origMap = new ArrayList();
                        String s1 = "";
                        for (String ds : lines) {

                            if (cntz > 1) {
//                                System.out.println("#data "+cntz+"'th  ::  "+ds);
                                if (cntz % 2 == 1) {
                                    Map<String, Object> nmap = new HashMap();
                                    nmap.put(ds.trim(), s1);
                                    System.out.println("#data "+cntz+"'th  ::  "+nmap.toString());
                                    origMap.add(nmap);
                                    s1 = "";
                                } else {
                                    s1 = ds.trim();
                                }
                            }
                            cntz++;
                        }

                        arrayResultMap2.add(origMap);

                    }
                }
                cnt++;
            }

            /* title 출력 */
            /*
            int y =1;
            for(String ts : titleArr) {
                System.out.println("#title "+y+"'th  ::  "+ts);
                y++;
            }
            */
            System.out.println("#TITLESarrayResultMap:"+titleArr.toString());
            //System.out.println("#arrayResultMap2:"+arrayResultMap2.toString());

            List<Map<String, Object>> sortedMapArr = processColsListToRows(arrayResultMap2);
            //ystem.out.println("#arrayResultMap2:"+resArrMap.toString());

            resArr4Map = processResultListToRows(sortedMapArr, titleArr);
            System.out.println("#resArrMap::"+resArr4Map.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resArr4Map;
    }


    public List<Map<String, Object>> loadExcelDatas2(String fileName) throws Exception {
        List<Map<String, Object>> resultMap = new ArrayList();

        //List<String> titlesTabArray = new ArrayList();
        List<List<Map<String,Object>>> arrayResultMap2 = new ArrayList();

        int cntAll = 0;
        String line = "";
        String titles_orig = "";
        List<String> titleArr = new ArrayList();
        String seperator = "\t";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(fileName), "ms949"))) {
            int cnt = 0;
            while ((line = reader.readLine()) != null) {
                List<Map<String, Object>> lineMap = new ArrayList();

                // 타이틀 처리
                if (cnt == 8) {
                    titles_orig = line;
                    //System.out.println("#TITLE:"+titles_orig);
                    String titles[] = titles_orig.trim().split(seperator);
                    int x = 0;
                    for (String tt : titles) {
                        //System.out.println("#title "+x+"'th  ::  "+tt);
                        if (x > 0 && !"".equals(tt.trim())) {
                            //titleArr.add(tt.trim() + seperator + " " + seperator);
                            titleArr.add(tt.trim());
                            //List<Map<String, Object>> newList = new ArrayList();
                            //Map<String,Object> nmap = new HashMap();
                            //nmap.put(tt.trim(), "_");
                            //newList.add(nmap);
                            //arrayResultMap.add(newList);
                        }
                        x++;
                    }

                }

                // 데이터 처리

                if (cnt > 8) {
                    String lines[] = line.trim().split("\\t");
                    int dataCnt = 0;
                    int cnty = 0;
                    // 공백만 있는 라인 제거를 위함
                    for (String ss : lines) {
                        if (cnty > 2 && !ss.trim().equals("")) {
                            dataCnt++;
                        }
                        cnty++;
                    }
                    //Map<String, Object> newItem = new HashMap<String, Object>();

                    // 공백만 있는 라인을 제거
                    if (dataCnt > 0) {
                        int cntz = 1;
                        System.out.println("# size:" + lines.length + " line_All:" + line);

                        // 2칸이 한개의 페어
                        List<Map<String, Object>> origMap = new ArrayList();
                        String s1 = "";
                        for (String ds : lines) {

                            if (cntz > 1) {
//                                System.out.println("#data "+cntz+"'th  ::  "+ds);
                                if (cntz % 2 == 1) {
                                    Map<String, Object> nmap = new HashMap();
                                    nmap.put(ds.trim(), s1);
                                    System.out.println("#data "+cntz+"'th  ::  "+nmap.toString());
                                    origMap.add(nmap);
                                    s1 = "";
                                } else {
                                    s1 = ds.trim();
                                }
                            }
                            cntz++;
                        }

                        arrayResultMap2.add(origMap);

                    }
                }
                cnt++;
            }

            /* title 출력 */
            /*
            int y =1;
            for(String ts : titleArr) {
                System.out.println("#title "+y+"'th  ::  "+ts);
                y++;
            }
            */
            System.out.println("#TITLES:arrayResultMap.size:"+titleArr.size()+" datas:"+titleArr.toString());
            //System.out.println("#arrayResultMap2:"+arrayResultMap2.toString());

            List<Map<String, Object>> sortedMapArr = processColsListToRows(arrayResultMap2);
            System.out.println("#arrayResultMap2:size:"+sortedMapArr.size()+" datas:"+sortedMapArr.toString());

            resultMap = processResultListToRowsFiltered2(sortedMapArr, titleArr);
            System.out.println("#resArrMap.size:"+resultMap.size()+ "  datas:"+resultMap.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    private List<String> processResultListToRows(
            List<Map<String, Object>> sortedArrMap
            , List<String> titlesArr) {
        String seperator = "\t";
        String lineFeed = System.getProperty("line.separator");

        List<String> resArr = new ArrayList();

        String line = "";
        for(String ts : titlesArr) {
            line += ts.trim() + seperator + "_" + seperator;
        }
        line +=  lineFeed;
        resArr.add(line);

        List<List<String>> tmpArr = new ArrayList();

        for(Map<String, Object> tmap : sortedArrMap) {
            List<String> tarr = MapUtil.getKeysListFromMapByLimit(tmap, 100);
            tmpArr.add(tarr);
        }

        //System.out.println("#titlesMapArr:"+titlesArr.toString());
        //System.out.println("#tmpArr:"+tmpArr.toString());

        for(int cnt=0; cnt<100; cnt++) {
            String lineStr = "";
            for (int cols = 0; cols < 50; cols++) {
                if (tmpArr.get(cols) != null) {
                    String ts = "";
                    if (tmpArr.get(cols).size() > cnt && tmpArr.get(cols).get(cnt) != null) {
                        ts = tmpArr.get(cols).get(cnt);
                        String tss[] = ts.trim().split("_");
                        String tss2 = tss[0] + seperator + tss[1] + seperator;

                        lineStr += tss2;
                        //System.out.print("#ts:"+ts);

                    } else {
                        ts = "_" + seperator + "_" + seperator;
                        lineStr += ts;
                    }

                }
            }
            //System.out.println(lineFeed);
            lineStr += lineFeed;
            resArr.add(lineStr);
        }
        System.out.println("#resArr:"+resArr.toString());

        return resArr;
    }

    @Override
    public List<String> processResultListToRowsFiltered(
            List<Map<String, Object>> sortedArrMap
            , List<String> titlesArr) {
        String seperator = "\t";
        String lineFeed = System.getProperty("line.separator");

        List<String> resArr = new ArrayList();

        String line = "";
        for(String ts : titlesArr) {
            line += ts.trim() + seperator + "_" + seperator;
        }
        line +=  lineFeed;
        resArr.add(line);

        List<List<String>> tmpArr = new ArrayList();

        for(Map<String, Object> tmap : sortedArrMap) {
            List<String> tarr = MapUtil.getKeysListFromMapByLimit(tmap, 100);
            System.out.println("#tarr:"+tarr.toString());
            tmpArr.add(tarr);
        }

        //System.out.println("#titlesMapArr:"+titlesArr.toString());
        System.out.println("#tmpArr:"+tmpArr.toString());

        for(int cnt=0; cnt<100; cnt++) {
            String lineStr = "";
            for (int cols = 0; cols < 50; cols++) {
                if (tmpArr.get(cols) != null) {
                    String ts = "";
                    if (tmpArr.get(cols).size() > cnt && tmpArr.get(cols).get(cnt) != null) {
                        ts = tmpArr.get(cols).get(cnt);
                        String tss[] = ts.trim().split("_");
                        String tss2 = tss[0] + seperator + tss[1] + seperator;

                        lineStr += tss2;
                        //System.out.print("#ts:"+ts);

                    } else {
                        ts = "_" + seperator + "_" + seperator;
                        lineStr += ts;
                    }

                }
            }
            //System.out.println(lineFeed);
            lineStr += lineFeed;
            resArr.add(lineStr);
        }
        System.out.println("#resArr:"+resArr.toString());

        return resArr;
    }

    private List<Map<String, Object>> processResultListToRowsFiltered2(
            List<Map<String, Object>> sortedArrMap
            , List<String> titlesArr) {

        List<Map<String, Object>> resArr = new ArrayList();
        for(int i =0; i<50; i++) {
            if (titlesArr.get(i) != null && sortedArrMap.get(i) != null) {
                Map<String, Object> newItem = new HashMap();
                newItem.put("title", titlesArr.get(i));
                String tmpWords = String.valueOf(sortedArrMap.get(i));
                //tmpWords = tmpWords.replace("{","").replace("}","");
                newItem.put("words", tmpWords);

                resArr.add(newItem);
            }
        }

        return resArr;
    }


    @Override
    public List<String> processResultListToRowsFiltered3(
            List<Map<String, Object>> sortedArrMap
            , List<String> titlesArr
            , List<String> cidArr) {
        String seperator = "\t";
        String lineFeed = System.getProperty("line.separator");

        List<String> resArr = new ArrayList();

        String line1 = "";
        for(String cs : cidArr) {
            line1 += cs.trim() + seperator + "_" + seperator;
        }
        line1 +=  lineFeed;
        resArr.add(line1);


        String line = "";
        for(String ts : titlesArr) {
            line += ts.trim() + seperator + "_" + seperator;
        }
        line +=  lineFeed;
        resArr.add(line);

        List<List<String>> tmpArr = new ArrayList();

        for(Map<String, Object> tmap : sortedArrMap) {
            List<String> tarr = MapUtil.getKeysListFromMapByLimit(tmap, 100);
            System.out.println("#tarr:"+tarr.toString());
            tmpArr.add(tarr);
        }

        //System.out.println("#titlesMapArr:"+titlesArr.toString());
        System.out.println("#tmpArr:"+tmpArr.toString());

        for(int cnt=0; cnt<100; cnt++) {
            String lineStr = "";
            int colsLimit = 50;
            if (tmpArr.size() < 50) colsLimit = tmpArr.size();

            for (int cols = 0; cols < colsLimit; cols++) {
                if (tmpArr.get(cols) != null) {
                    String ts = "";
                    if (tmpArr.get(cols).size() > cnt && tmpArr.get(cols).get(cnt) != null) {
                        ts = tmpArr.get(cols).get(cnt);
                        String tss[] = ts.trim().split("_");
                        if (tss != null && tss.length > 1) {
                            String tss2 = tss[0] + seperator + tss[1] + seperator;

                            lineStr += tss2;

                            //System.out.print("#ts:"+ts);
                        }

                    } else {
                        ts = "_" + seperator + "_" + seperator;
                        lineStr += ts;
                    }

                }
            }
            //System.out.println(lineFeed);
            lineStr += lineFeed;
            resArr.add(lineStr);
        }
        System.out.println("#resArr:"+resArr.toString());

        return resArr;
    }

    private List<Map<String, Object>> processColsListToRows(List<List<Map<String,Object>>> reqArrmap) {
        List<List<Map<String,Object>>> res2ArrMap = new ArrayList();
        for(int i=0; i<50; i++) {
            List<Map<String,Object>> newArr = new ArrayList();

            res2ArrMap.add(newArr);
        }
        //System.out.println("#processColsListToRows reqMap:"+reqArrmap.toString());

        for(List<Map<String,Object>> lineMap : reqArrmap) {
            //System.out.println("#lines : "+lineMap);
            int i = 0;
            for(Map<String, Object> nmap : lineMap) {
                List<Map<String, Object>> newListCols = null;
                if (res2ArrMap.get(i) == null) {
                    newListCols = new ArrayList();
                } else {
                    newListCols = res2ArrMap.get(i);
                    newListCols.add(nmap);
                    res2ArrMap.set(i, newListCols);
                }
                i++;
            }
        }

        // 각 Array별 Key에 따라 asc 정렬
        List<Map<String, Object>> sortedArrMap = new ArrayList();
        //List<List<Map<String,Object>>> res3ArrMap = new ArrayList();
        // 50개 타입-키워드 리스트 매핑
        for(List<Map<String, Object>> omap : res2ArrMap) {
            // 각 영화별-키워드 리스트 매핑
            Map<String, Object> nmap = new HashMap();
            int idx = 1;
            for (Map<String, Object> map : omap) {
                String[] keys = new String[map.size()];
                Object[] values = new Object[map.size()];
                int index = 0;
                for (Map.Entry<String, Object> mapEntry : map.entrySet()) {
                    keys[index] = mapEntry.getKey();
                    values[index] = mapEntry.getValue();
                    index++;
                }
                if (!"".equals(keys[0].toString().trim()) && !"".equals(values[0].toString().trim())) {
                    nmap.put(keys[0].toString().trim()+"_"+values[0].toString().trim(), idx);
                }

                //System.out.println("#nmap:"+nmap.toString());
                idx++;
            }

            Map<String, Object> sortedMap = MapUtil.getSortedascMapForStringKey(nmap);
            sortedArrMap.add(sortedMap);
        }

        //System.out.println("#sortedArrMap:"+sortedArrMap.toString());

        return sortedArrMap;
    }

    @Override
    public void writeCine21_1600_sorted() {
        try {
            String fileOrig = "E:\\CINE_1600\\메타확장_태깅_씨네21_urg";
            String outFileOrig = "E:\\CINE21_1600_out\\메타확장_태깅_씨네21_put";
            String fileExt = ".txt";

            for(int i = 1;i<35;i++) {
                String suffix = String.format("%02d", i);
                String fileName = fileOrig + suffix + fileExt;

                String outFileName = outFileOrig + suffix + fileExt;
                File targetFile = new File(outFileName);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile.getPath()), "euc-kr"));

                List<String> resultStr = this.loadExcelDatas1(fileName);
                for(String lineStr : resultStr) {
                    bw.write(lineStr);
                }
                bw.close();
                System.out.println("#result.size:"+resultStr.size());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<CcubeContent> getCcubeContents() {
        List<CcubeContent> resultList = new ArrayList();
        List<CcubeContent> yjOutList = ccubeMapper.getCcubeContentByYjid();
        for (CcubeContent cc : yjOutList) {
            resultList.add(cc);
        }

        List<CcubeContent> yjResList = ccubeMapper.getCcubeContentByYjidNot();
        for (CcubeContent cc : yjResList) {
            resultList.add(cc);
        }

        for (CcubeContent cc : yjResList) {
            System.out.println("#cc:" + cc.toString());
        }

        System.out.println("#AllCount:" + resultList.size());

        return resultList;
    }

    private List<String> convertCcubeDatas(List<CcubeContent> req) {
        List<String> result = new ArrayList();
        String tab = "\t";
        String lineFeed = System.getProperty("line.separator");

        result.add("REGDATE" + tab + "CONTENT_ID" + tab + "KMRB_ID" + tab + "PURITY_TITLE" + tab + lineFeed);

        for (CcubeContent cc : req) {
            String lineStr = "";
            lineStr += (cc.getRegdate() != null) ? cc.getRegdate().toString() : "_";
            lineStr += tab;
            lineStr += (cc.getContent_id() != null) ? cc.getContent_id().toString() : "_";
            lineStr += tab;
            lineStr += (cc.getKmrb_id() != null) ? cc.getKmrb_id().toString() : "_";
            lineStr += tab;
            lineStr += (cc.getPurity_title() != null) ? cc.getPurity_title().toString() : "_";
            lineStr += tab;
            lineStr += lineFeed;
            result.add(lineStr);
        }

        return result;
    }


    @Override
    public void writeCcubeContents_phase1_24123() {
        try {
            String outFileOrig = "E:\\ccube_contents_24123";
            String fileExt = ".txt";

                String outFileName = outFileOrig + fileExt;
                File targetFile = new File(outFileName);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile.getPath()), "euc-kr"));

                List<CcubeContent> ccList = getCcubeContents();
                List<String> resultStr = this.convertCcubeDatas(ccList);

                for(String lineStr : resultStr) {
                    bw.write(lineStr);
                }
                bw.close();
                System.out.println("#result.size:"+resultStr.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void write_yjdatas_1st_20180208_sorted() {
        try {

            String fileOrig = "E:\\tagged__1st\\meta_tag_20180208";
            String outFileOrig = "E:\\tagged__1st_out\\meta_tag_20180208";
            String fileExt = ".txt";

            //int i = 1;
            for(int i = 1;i<69;i++) {
                String suffix = String.format("%02d", i);
                String fileName = fileOrig + suffix + fileExt;

                String outFileName = outFileOrig + suffix + fileExt;
                File targetFile = new File(outFileName);
                //BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile.getPath()), "euc-kr"));

                List<Map<String, Object>> resultStr = this.loadExcelDatas2(fileName);
                for(Map<String, Object> lineMap : resultStr) {
                    //bw.write(lineStr);
                    System.out.println("#lineMap:"+lineMap);
                    lineMap.put("type", "yj");
                    lineMap.put("filecnt", suffix);
                    int rtx = testMapper.insYcDatas(lineMap);
                }
                //bw.close();
                System.out.println("#result.size:"+resultStr.size());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void write_yjdatas_2st_20180220_sorted() {
        try {

            String fileOrig = "E:\\tagged__2st\\meta02_";
            String outFileOrig = "E:\\tagged__2st_out\\meta_tag2_20180220";
            String fileExt = ".txt";

            //int i = 1;
            for(int i = 1;i<52;i++) {
                String suffix = String.format("%02d", i);
                String fileName = fileOrig + suffix + fileExt;

                String outFileName = outFileOrig + suffix + fileExt;
                File targetFile = new File(outFileName);
                //BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile.getPath()), "euc-kr"));

                List<Map<String, Object>> resultStr = this.loadExcelDatas2(fileName);
                for(Map<String, Object> lineMap : resultStr) {
                    //bw.write(lineStr);
                    System.out.println("#lineMap:"+lineMap);
                    lineMap.put("type", "yj");
                    lineMap.put("filecnt", suffix);
                    int rtx = testMapper.insYcDatas(lineMap);
                }
                //bw.close();
                System.out.println("#result.size:"+resultStr.size());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, Object>> getYcDatas1st() {
        List<Map<String, Object>> result = testMapper.getYcDatas1st();
        for(Map<String, Object> im : result) {
            System.out.println("#lineMap:" + im.toString());
        }
        System.out.println("#Result.size:"+result.size());
        return result;
    }

    @Override
    public List<List<Map<String, Object>>> getYcDatas1stFromTable() {
        List<Map<String, Object>> result = testMapper.getYcDatas1st();
        //for(Map<String, Object> im : result) {
        //    System.out.println("#lineMap:" + im.toString());
        //}
        System.out.println("#Result.size:"+result.size());

        List<List<Map<String, Object>>> result2 = this.getSeperatedMapList(result, 50);

        return result2;
    }

    public void write_ycDatas_from_table(String path_prefix, String extention) {
        try {

            //String outFileOrig = "E:\\tagged__1st_out\\meta_tag_20180208";
            String outFileOrig = path_prefix;
            //String fileExt = ".txt";
            String fileExt = extention;

            List<List<Map<String, Object>>> resultMapList = this.getYcDatas1stFromTable();
            int lineCnt = 1;
            for(List<Map<String, Object>> ic : resultMapList) {
                //if (lineCnt > 1) break;

                String suffix = String.format("%02d", lineCnt);

                String outFileName = outFileOrig + suffix + fileExt;
                File targetFile = new File(outFileName);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile.getPath()), "euc-kr"));

                List<String> cidArr = new ArrayList();
                List<String> titlesArr = new ArrayList();
                List<Map<String, Object>> sortedMap = new ArrayList();

                System.out.println("#lineMap:"+ic.toString());
                System.out.println("#lineMap.size:"+ic.size());

                for(Map<String, Object> lineMap : ic) {
                    Map<String, Object> newItem = new HashMap();

                    titlesArr.add((String) lineMap.get("title"));
                    cidArr.add((String) lineMap.get("content_id"));

                    String tmpWords = (String) lineMap.get("words");
                    tmpWords = tmpWords.replace("{","").replace("}","");
                    String wordsArr[] = tmpWords.split(",");
                    for (String w : wordsArr) {
                        String ww = w.trim();
                        String www[] = ww.split("=");
                        if (www != null && www.length > 1) {
                            newItem.put(www[0].trim(), www[1].trim());
                        }
                    }

                    System.out.println("#newMap:"+newItem.toString());
                    sortedMap.add(newItem);

                }

                List<String> list2 = this.processResultListToRowsFiltered3(sortedMap, titlesArr, cidArr);

                System.out.println("list2:: result.size:"+list2.size() + "  datas::"+list2.toString());
                for(String ls : list2) {
                    bw.write(ls);
                }

                bw.close();
                lineCnt++;
            }
/*
            List<String> list2 = testService.processResultListToRowsFiltered(sortedMap, titlesArr);

            System.out.println("list2:: result.size:"+list2.size() + "  datas::"+list2.toString());

            System.out.println("#result.size:"+result.size());
            */
            //int i = 1;
            //for(int i = 1;i<69;i++) {

                /*
                for(Map<String, Object> lineMap : resultStr) {

                    //bw.write(lineStr);
                    System.out.println("#lineMap:"+lineMap);
                    lineMap.put("type", "yj");
                    lineMap.put("filecnt", suffix);
                    int rtx = testMapper.insYcDatas(lineMap);
                }
                */
            //bw.close();
            //System.out.println("#result.size:"+resultStr.size());
            //}

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write_ycDatas_1st_20180208_from_table() {
        try {

            String outFileOrig = "E:\\tagged__1st_out\\meta_tag_20180208";
            String fileExt = ".txt";

            this.write_ycDatas_from_table(outFileOrig, fileExt);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write_ycDatas_2st_20180220_from_table() {
        try {

            String outFileOrig = "E:\\tagged__2st_out\\meta_tag2_20180220";
            String fileExt = ".txt";

            this.write_ycDatas_from_table(outFileOrig, fileExt);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<List<Map<String, Object>>> getSeperatedMapList(List<Map<String, Object>> reqList, int limit) {
        List<List<Map<String, Object>>> result = new ArrayList();

        List<Map<String, Object>> newList = new ArrayList();
        int cnt = 1;
        if (reqList.size() > limit) {

            for(Map<String, Object> ic : reqList) {
                newList.add(ic);

                if (cnt > 1 && cnt % limit == 0) {
                    result.add(newList);
                    newList = new ArrayList();
                }
                cnt++;
            }
            // 짜투리
            result.add(newList);

        } else {
            result.add(reqList);
        }

        return result;
    }


    @Override
    public List<ItemsContent> getItemsYj04() {
        List<ItemsContent> resultList = new ArrayList();
        List<ItemsContent> itemsList = itemsMapper.getItemsYj04();

        for(ItemsContent ic : itemsList) {
            String content = ic.getContent();
            List<String> allKeywords = new ArrayList<String>();

            JsonObject resObj = new Gson().fromJson(content, JsonObject.class);
            //System.out.println("#result:"+resObj.toString());

            /*
            // 시간적 배경
            allKeywords = getAllKeywordMapByType(allKeywords, "WHEN", resObj);
            // 공간적 배경
            allKeywords = getAllKeywordMapByType(allKeywords, "WHERE", resObj);
            // 주제/소재
            allKeywords = getAllKeywordMapByType(allKeywords, "WHAT", resObj);
            // 인물/캐릭터
            allKeywords = getAllKeywordMapByType(allKeywords, "WHO", resObj);
            // 감성/분위기
            allKeywords = getAllKeywordMapByType(allKeywords, "EMOTION", resObj);
*/
            JsonObject notmapObj = resObj.get("notKeywordMappingResult").getAsJsonObject();
            List<String> notmapList = JsonUtil.convertJsonObjectToArrayList(notmapObj, 100);
            allKeywords = getAddedNotmapKeywords(allKeywords, notmapList);
            ic.setAllKeywords(allKeywords);
            //System.out.println("#allKeywordMap:"+allKeywords.toString());
            //System.out.println("#notmapList:"+notmapList.toString()+" ::size::"+notmapList.size());

            resultList.add(ic);
        }
        return resultList;
    }

    @Override
    @Transactional
    public void writeYj04() {
        String outContent = "";
        String lineFeed = System.getProperty("line.separator");
        String tab = "\t";
        //String tab = "||";

        String outFileNameOrig = "E:\\yj06_4913_180220";
        try {

            List<ItemsContent> itemList = this.getItemsYj04();
            int itemCnt = 0;
            if (itemList != null) itemCnt = itemList.size();

            //List<ItemsContent> itemList = null;
            List<List<ItemsContent>> allItemList = this.getSeperatedContent(itemList,50);
            System.out.println("#allItemList:"+allItemList.size());

            int lineCnt = 1;
            int itemsNo = 1;
            int xcnt = 0;
            for (List<ItemsContent> itemsListOne : allItemList) {
                String outFileName = outFileNameOrig + "_"+itemsNo+".txt";
                File targetFile = new File(outFileName);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile.getPath()), "euc-kr"));

                xcnt = 0;

                outContent = "";
                for (ItemsContent item : itemsListOne) {
                    /*
                    outContent += item.getTitle().trim()
                            + tab + ""
                            + tab + ""
                            + tab + ""
                            + tab;
                    */
                    Items item1 = new Items();
                    item1.setIdx(item.getIdx());
                    item1.setType("yj6");
                    item1.setCid(item.getCid());
                    item1.setTitle(item.getTitle());
                    int rtins = itemsMapper.insYjItemsOut(item1);

                    outContent += item.getTitle().trim()
                            + tab + "_"
                            + tab;
                    lineCnt++;
                }
                outContent += lineFeed;

                bw.write(outContent);
                System.out.println("#proccess outContent:" + outContent);
                String outContent2 = "";

                for (int line = 0; line < 100; line++) {
                    //int line = 0;
                    for (ItemsContent ic : itemsListOne) {
                        //System.out.println("#allKeywords:"+ic.getAllKeywords());
                        String a1 = " ";
                        String b1 = " ";
                        String c1 = " ";
                        //String d1 = " ";
                        String title2 = ic.getTitle2();
                        List<String> cine21Keywords = new ArrayList<String>();
                        if (!"".equals(title2.trim())) {
                            String cine21s[] = title2.split(",");
                            for (String s : cine21s) {
                                cine21Keywords.add(s.trim());
                            }
                            ic.setCine21Keywords(cine21Keywords);
                        }


                        a1 = "";
                        if (ic != null && ic.getCine21Keywords() != null) {
                            if (line < ic.getCine21Keywords().size() && ic.getCine21Keywords().get(line) != null) {
                                a1 = ic.getCine21Keywords().get(line).toString();
                            }
                        }
                        c1 = "_" + tab + "_";
                        if (ic != null && ic.getAllKeywords() != null) {
                            if (line < ic.getAllKeywords().size() && ic.getAllKeywords().get(line) != null) {
                                c1 = ic.getAllKeywords().get(line).toString();
                            }
                        }

                        /*
                        outContent2 += a1.trim()
                                + tab + b1
                                + tab + c1.trim()
                                //+ tab + d1
                                + tab;
                                */

                        outContent2 += c1
                                //+ tab + "_"
                                + tab;
                    }
                    outContent2 += lineFeed;
                    //System.out.println("#keyword:"+outContent2);
                    //lineCnt++;

                    //System.out.println("#proccess outContent:"+outContent);
                    bw.write(outContent2);
                    outContent2 = "";
                    xcnt++;
                    //line++;
                }

                System.out.println("#XCNT:"+xcnt);
                itemsNo++;
                bw.close();

                System.out.println("#this itemCnt:"+itemsListOne.size());
            }

            System.out.println("#itemCnt:"+itemCnt);
            //FileWriter fw = new FileWriter(outFileName);
            //PrintWriter bw = new PrintWriter(new FileWriter(outFileName));
            //bw.println(outContent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private Map<String, Object> getCombinedMap(Map<String, Object> origMap, String key, String value) {
        List<String> tmpArr = (List<String>) origMap.get(key);
        if (tmpArr == null) tmpArr = new ArrayList();

        tmpArr.add(value);
        origMap.remove(key);
        origMap.put(key, tmpArr);

        return origMap;
    }

    private Map<String, Object> getSeperatedMetas(String words) {
        Map<String, Object> resultMap = new HashMap();
        try {
            words = words.replace("{", "");
            words = words.replace("}", "");
            String[] words2 = words.split(",");
            if (words2 != null && words2.length > 0) {
                for (String w1 : words2) {
                    w1 = w1.trim();

                    String[] w1s = w1.split("=");
                    if (w1s != null && w1s.length > 0) {
                        if (w1s[0] != null) {

                            String w1s0 = w1s[0];

                            String[] w2s = w1s0.split("_");
                            if (w2s != null && w2s.length > 1) {
                                //System.out.println("#tmp::w2:" + w2s[0] + ":" + w2s[1]);
                                resultMap = getCombinedMap(resultMap, w2s[0], w2s[1]);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //System.out.println("#resultMap::"+resultMap.toString());
        return resultMap;
    }

    private JsonArray getJsonFromArray(List<String> reqArr) {
        JsonArray result = new JsonArray();

        for(String rs : reqArr) {
            JsonObject obj = new JsonObject();
            obj.addProperty("word", rs);
            obj.addProperty("ratio", 0.0);
            obj.addProperty("freq", 0.0);
            result.add(obj);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getYcDatas1st2() {
        List<Map<String, Object>> result = testMapper.getYcDatas1st();
        //for(Map<String, Object> im : result) {
        //    System.out.println("#lineMap:" + im.toString());
        //}
        System.out.println("#Result.size:"+result.size());

        int cnt = 0;
        for(Map<String, Object> obj : result) {
            //if (cnt >= 20) break;

            System.out.println("#obj:"+obj.toString());
            String purity_title = "";
            if (obj.get("purity_title") != null) purity_title = obj.get("purity_title").toString();
            if ("".equals(purity_title) && obj.get("title") != null) purity_title = obj.get("title").toString();

            purity_title = purity_title.trim();
            String title2 = purity_title.replace(" ", "");
            //String yj_id = obj.get("kmrb_id").toString();
            Items reqit = new Items();
            reqit.setTitle2(title2);
            Items yjidItem = itemsMapper.getYjidForInsert(reqit);
            String yj_id = "";
            if (yjidItem != null && yjidItem.getCid() != null) {
                yj_id = yjidItem.getCid();
            }
            if (!"".equals(yj_id.trim())) {
                String words = obj.get("words").toString();
                words = words.replace("주제/소재", "METASWHAT");
                words = words.replace("감성/분위기", "METASEMOTION");
                words = words.replace("인물/캐릭터", "METASWHO");
                words = words.replace("공간적배경", "METASWHERE");
                words = words.replace("시간적배경", "METASWHEN");

                System.out.println("#RE::" + purity_title + "__" + title2 + "___" + yj_id + "__" + words);

                Map<String, Object> wordsMap = getSeperatedMetas(words);
                Set entrySet = wordsMap.entrySet();
                Iterator it = entrySet.iterator();
                ItemsTags req1 = null;

                while (it.hasNext()) {
                    Map.Entry me = (Map.Entry) it.next();
                    String atype = me.getKey().toString();
                    List<String> wordsArr = (List<String>) me.getValue();
                    //System.out.println("##type:"+atype+"/ARRAY:"+wordsArr.toString());

                    JsonArray newArr = getJsonFromArray(wordsArr);

                    System.out.println("##jsonArray:" + newArr.toString());

                    req1 = new ItemsTags();
                    req1.setTitle(purity_title);
                    req1.setTitle2(title2);
                    req1.setYj_id(yj_id);
                    req1.setMtype(atype);
                    req1.setMeta(newArr.toString());
                    System.out.println("#insert yc_tags_metas! type:" + atype);
                    int rt1 = itemsMapper.insYjTagsMetas(req1);
                }

                System.out.println("#insert yj_items_out2! title:" + purity_title);
                int rt2 = itemsMapper.insYjItemsOut2(req1);

                cnt++;
            }
        }
        //List<List<Map<String, Object>>> result2 = this.getSeperatedMapList(result, 50);

        return result;
    }

    @Override
    public List<Map<String, Object>> getYcDatas2st2() {
        List<Map<String, Object>> result = testMapper.getYcDatas2st();
        //for(Map<String, Object> im : result) {
        //    System.out.println("#lineMap:" + im.toString());
        //}
        System.out.println("#Result.size:"+result.size());

        //List<List<Map<String, Object>>> result2 = this.getSeperatedMapList(result, 50);

        return result;
    }

    @Override
    public void insDicKeywords__FromYcDatas1st1() throws Exception {
        List<Map<String, Object>> result = testMapper.getYcDatas1st();
        insDicKeywords__FromDatas(result);
        System.out.println("#RESULT.size:"+result.size());
    }

    @Override
    public void insDicKeywords__FromYcDatas1st2() throws Exception {
        List<Map<String, Object>> result = testMapper.getYcDatas2st();
        insDicKeywords__FromDatas(result);
        System.out.println("#RESULT.size:"+result.size());
    }


    public void insDicKeywords__FromDatas(List<Map<String, Object>> reqMapList) throws Exception {
        List<Map<String, Object>> result = reqMapList;
        System.out.println("#RESULT.size:"+result.size());
        for (Map<String, Object> nm : result) {
            //System.out.println("#new:"+nm.toString());
            //System.out.println("#words:"+nm.get("words").toString());
            String tmpWord = nm.get("words").toString();
            Map<String, Object> tmpMap = this.getSeperatedMetas(tmpWord);
            System.out.println("#tmpMap:"+tmpMap.toString());

            Set entrySet = tmpMap.entrySet();
            Iterator it = entrySet.iterator();
            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                String atype = me.getKey().toString();
                atype = chgDicTypes(atype);
                List<String> akeyArr = (List<String>) me.getValue();

                for (String akey : akeyArr) {
                    System.out.println("## to insert type:" + atype + ", keyword:" + akey);
                    DicKeywords reqDic = new DicKeywords();
                    reqDic.setType(atype);
                    reqDic.setKeyword(akey);
                    reqDic.setRatio(0.0);
                    int id1 = dicService.insDicKeywords(reqDic);
                }
            }
        }
    }


    private String chgDicTypes(String req) {
        String result = "";

        switch (req) {
            case "공간적배경" : result = "WHERE"; break;
            case "공간적 배경" : result = "WHERE"; break;
            case "시간적배경" : result = "WHEN"; break;
            case "시간적 배경" : result = "WHEN"; break;
            case "주제/소재" : result = "WHAT"; break;
            case "인물/캐릭터" : result = "WHO"; break;
            case "감성/분위기" : result = "EMOTION"; break;
            case "캐릭터명" : result = "CHARACTER"; break;
            default : result = req; break;
        }

        return result;
    }


    private String removeF(String req) {
        String result = "";
        result = req.trim();
        result = result.replace("\"_\"", "");
        result = result.replace("\"", "");
        result = result.replace("\'", "");
        result = result.replace("_","");
        return result;
    }

    @Override
    public List<CcubeContent> loadCcubeMoviesDatas0226() throws Exception {
        String fileName = "E:\\yj1_0226.txt";
        return loadCcubeMoviesDatas0226_14770_2262(fileName);
    }

    @Override
    public List<CcubeContent> loadCcubeMoviesDatas0226_2() throws Exception {
        String fileName = "E:\\yj2_0226.txt";
        return loadCcubeMoviesDatas0226_14770_2262(fileName);
    }

    @Override
    public List<CcubeContent> loadCcubeMoviesDatas0402_1() throws Exception {
        String fileName = "E:\\ad_yjo_244.txt";
        return loadCcubeMoviesDatas0226_14770_2262(fileName);
    }

    @Override
    public List<CcubeContent> loadCcubeMoviesDatas0402_2() throws Exception {
        String fileName = "E:\\ad_yjx_777.txt";
        return loadCcubeMoviesDatas0226_14770_2262(fileName);
    }
    @Override
    public Map<String,Object> loadDicSubgenreGenres() throws Exception {
        String fileName = "C:\\Users\\wodus77\\Documents\\KTH_META\\03.구현\\서브장르__추출_____\\dic_subgenre_genres4.txt";
        fileName = "E:\\0425_dic_subgenre_genres.txt";
        return loadDicSubgenreGenres(fileName);
    }

    @Override
    public Map<String,Object> loadDicSubgenreKeywords() throws Exception {
        String fileName = "C:\\Users\\wodus77\\Documents\\KTH_META\\03.구현\\서브장르__추출_____\\dic_subgenre_keywords0705.txt";
        //String fileName = "/home/ktipmedia/BAK/dic_subgenre_keywords0705.txt";
        return loadDicSubgenreKeywords(fileName);
    }

    @Override
    public Map<String,Object> loadDicSearchTxtFromFile() throws Exception {
        String fileName = "C:\\Users\\wodus77\\Documents\\KTH_META\\98.산출물\\0.작업중\\asset_full.txt";
        fileName = "E:\\0425_dic_subgenre_genres.txt";
        return this.loadDicSubgenreKeywords();
    }

    @Override
    public Map<String,Object> loadDicResultTagKeywords() throws Exception {
        //String fileName = "E:\\0502_메타_대체_추가어_사전.txt";
        String fileName="C:\\Users\\wodus77\\Documents\\KTH_META\\97.뭐지이건\\0503_WHERE_대체_키워드_최진기팀장.txt";
        fileName="C:\\Users\\wodus77\\Documents\\KTH_META\\97.뭐지이건\\0607_meta_dic.txt";
        fileName="/home/daisy/upload/0607_meta_dic.txt";
        return loadDicResultTagKeywords(fileName);
    }

    @Override
    public void insDicSubgenreKeywords() throws Exception {
        Map<String,Object> reqMap = loadDicSubgenreKeywords();
        putBulkDataToEsIndex("idx_subgenre", reqMap);
    }

    @Override
    public void insDicSubgenreGenres() throws Exception {
        Map<String, Object> result = loadDicSubgenreGenres();
        //System.out.println("#RESULT_MAP::"+result.toString());

        Set entrySet = result.entrySet();
        Iterator it = entrySet.iterator();

        int lineCnt = 0;
        while(it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            System.out.println("# "+lineCnt++ +" st map data:"+(me.getKey()+":"+me.getValue()));
            DicSubgenre newSub = new DicSubgenre();
            //newSub.setMtype("mixgenre");
            newSub.setMtype("subgenre_filter");
            newSub.setGenre(me.getKey().toString());
            newSub.setMeta(me.getValue().toString());
            newSub.setRegid("ghkdwo77");
            int rt1 = dicKeywordsMapper.insDicSubgenreGenres(newSub);
        }
    }

    @Override
    public void insDicResultTagKeywords() throws Exception {
        Map<String, Object> result = loadDicSubgenreGenres();
        //System.out.println("#RESULT_MAP::"+result.toString());

        Set entrySet = result.entrySet();
        Iterator it = entrySet.iterator();

        int lineCnt = 0;
        while(it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            System.out.println("# "+lineCnt++ +" st map data:"+(me.getKey()+":"+me.getValue()));
            DicSubgenre newSub = new DicSubgenre();
            //newSub.setMtype("mixgenre");
            newSub.setMtype("subgenre_filter");
            newSub.setGenre(me.getKey().toString());
            newSub.setMeta(me.getValue().toString());
            newSub.setRegid("ghkdwo77");
            int rt1 = dicKeywordsMapper.insDicSubgenreGenres(newSub);
        }
    }

    private List<CcubeContent> loadCcubeMoviesDatas0226_14770_2262(String fileName) throws Exception {
        //String fileName = "E:\\yj1_0226.txt";
        //String fileName = "E:\\yj1_0226.csv";
        String seperator = "\t";
        List<CcubeContent> result = new ArrayList();
        int cntAll = 0;
        int itemCnt = 0;
        int errCnt = 0;
        String line = "";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(fileName), "ms949"))) {
            while ((line = reader.readLine()) != null
                //&& cntAll < 10000
                    ){
                if (cntAll > 0) {
                    if (!"".equals(line.trim())) {
                    String lines[] = line.trim().split(seperator);


                    for (String ss : lines) {
                         System.out.print("___" + ss);
                    }
                    System.out.println("");


                    CcubeContent newItem = null;
                        newItem = new CcubeContent();
                        newItem.setContent_id(this.removeF(lines[0]));
                        newItem.setMaster_content_id(this.removeF(lines[1]));
                        newItem.setPurity_title(this.removeF(lines[2]));
                        newItem.setContent_title(this.removeF(lines[2]));
                        newItem.setEng_title(this.removeF(lines[3]));
                        newItem.setActors_display(this.removeF(lines[4]));
                        newItem.setDirector(this.removeF(lines[5]));
                        newItem.setYear(this.removeF(lines[6]));
                        newItem.setCountry_of_origin(this.removeF(lines[7]));
                        newItem.setKt_rating(this.removeF(lines[8]));
                        newItem.setKmrb_id(this.removeF(lines[9]));
                        newItem.setSad_ctgry_id(this.removeF(lines[10]));
                        newItem.setSad_ctgry_nm(this.removeF(lines[11]));
                        newItem.setPoster_url(this.removeF(lines[12]));

                        //if(newItem.getPoster_url().length() < 3) {
                        if(newItem.getYear().length() != 0 && newItem.getYear().length() != 4
                                ) {
                            System.out.println("## getYear:"+newItem.getYear() + " // "+ newItem.toString());
                            errCnt++;
                        }
                        //System.out.println("# size:" + lines.length + " line_All:" + newItem.toString());

                        result.add(newItem);
                        itemCnt++;
                    }

                }
                cntAll++;
            }

            System.out.println("#allCount:"+cntAll);
            System.out.println("#itemCnt:"+itemCnt);
            System.out.println("#errCnt:"+errCnt);
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return result;
    }

    @Override
    public void insCcubeMovies0226(List<CcubeContent> reqList) {
        int rt = 0;

        for(CcubeContent cc : reqList) {
            //System.out.println("#req cc:" + cc.toString());
            //if (cc.getKmrb_id().length() < 9) {
            cc.setStat("K");
                int rt1 = ccubeMapper.insCcubeContent(cc);
            //}


            //System.out.println("#req cc:" + cc.toString());
            //System.out.println(" / year:" + Integer.parseInt(cc.getYear()));

            //if (cc.getYear().length() > 4
            //        || cc.getSad_ctgry_id().length() > 5
            //        || ( cc.getDirector().length() < 2 && (cc.getPoster_url() != null && cc.getPoster_url().length() < 10))
            //        || cc.getKmrb_id().length() > 8
            //        )  {
                //System.out.println("#error! req cc:" + cc.toString());
                //System.out.println("#error! year size"+cc.getYear().length() + "  /  ctgry_id.size:"+cc.getSad_ctgry_id().length()+" / year:" + cc.getYear());
            //}
        }
    }

    @Override
    public List<CcubeSeries> loadCcubeSeriesAllDatas_0330() throws Exception {
        String fileName = "E:\\cs_all_0330.txt";
        return loadCcubeSeriesDatas0330(fileName);
    }

    @Override
    public List<CcubeSeries> loadCcubeSeriesDatas_0330() throws Exception {
        String fileName = "E:\\cs_0330.txt";
        return loadCcubeSeriesDatas0330(fileName);
    }

    @Override
    public void insCcubeSeries_0330(List<CcubeSeries> reqList) {
        int rt = 0;

        for (CcubeSeries cc : reqList) {
            //System.out.println("#req cc:" + cc.toString());
            //if (cc.getKmrb_id().length() < 9) {
            try {
                int rt1 = ccubeMapper.insCcubeSeries(cc);
            } catch (Exception e) {
                System.out.println("#err req:"+cc.toString());
                e.printStackTrace();
                try {
                    throw new Exception("ERR!");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            //}
        }
    }

    @Override
    public void insCcubeSeriesAll_0330_run() throws Exception {
        List<CcubeSeries> allItems = this.loadCcubeSeriesAllDatas_0330();
        insCcubeSeries_0330(allItems);
    }

    @Override
    public void insCcubeSeries_0330_run() throws Exception {
        List<CcubeSeries> allItems = this.loadCcubeSeriesDatas_0330();
        insCcubeSeries_0330(allItems);
    }

    private List<CcubeSeries> loadCcubeSeriesDatas0330(String fileName) throws Exception {
        //String fileName = "E:\\yj1_0226.txt";
        //String fileName = "E:\\yj1_0226.csv";
        String seperator = "\t";
        List<CcubeSeries> result = new ArrayList();
        int cntAll = 0;
        int itemCnt = 0;
        int errCnt = 0;
        String line = "";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(fileName), "ms949"))) {
            while ((line = reader.readLine()) != null
                //&& cntAll < 10000
                    ){
                if (cntAll > 0) {
                    if (!"".equals(line.trim())) {
                        String lines[] = line.trim().split(seperator);


                        for (String ss : lines) {
                             System.out.print(lines.length+" ___" + removeF(ss));
                        }
                        System.out.println("");


                        CcubeSeries newItem = new CcubeSeries();
                        newItem.setSeries_id(this.removeF(lines[0]));
                        newItem.setSeries_nm(this.removeF(lines[1]));
                        newItem.setPurity_title(this.removeF(lines[2]));
                        newItem.setSad_ctgry_nm(this.removeF(lines[3]));
                        newItem.setActors_display(this.removeF(lines[4]));
                        if (lines.length > 5 && lines[5] != null) newItem.setDirector(this.removeF(lines[5]));
                        if (lines.length > 6 && lines[6] != null) newItem.setKt_rating(this.removeF(lines[6]));

                        newItem.setStat("S");

                        //if(newItem.getPoster_url().length() < 3) {
                        //if(newItem.getYear().length() != 0 && newItem.getYear().length() != 4
                        //        ) {
                            //System.out.println("## getYear:"+newItem.getYear() + " // "+ newItem.toString());
                            //errCnt++;
                        //}
                        //System.out.println("# size:" + lines.length + " line_All:" + newItem.toString());

                        result.add(newItem);
                        itemCnt++;
                    }

                }
                cntAll++;
            }

            System.out.println("#allCount:"+cntAll);
            System.out.println("#itemCnt:"+itemCnt);
            System.out.println("#errCnt:"+errCnt);
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return result;
    }

    @Override
    public void writeDicEmo0227() throws Exception {
        List<Map<String,Object>> rmap = this.loadDicEmo0227("E:\\dic_emo.txt");
        //System.out.println("#allArr:"+rmap.toString());
        System.out.println("#RESULT.size:"+rmap.size());

        List<String> dicEmoArr = StringUtil.getCombinedWordsArrayFromToWords(rmap);
        //System.out.println("#dicEmoArr:"+dicEmoArr.toString());
        //System.out.println("#dicEmoArr.size:"+dicEmoArr.size());
        for(String emo : dicEmoArr) {
            DicKeywords newItem = new DicKeywords();
            newItem.setType("EMOTION");
            newItem.setKeyword(emo);
            //int rte = dicService.insDicKeywords(newItem);
        }

        List<Map<String,Object>> dicChangeArr = StringUtil.getFilteredWordsArrayFromByWords(rmap);
        System.out.println("#dicChangeArr:"+dicChangeArr.toString());
        System.out.println("#dicChangeArr.size:"+dicChangeArr.size());
        for(Map<String, Object> nmap : dicChangeArr) {
            DicChangeWords nitem = new DicChangeWords();
            nitem.setWord(nmap.get("word").toString());
            nitem.setWordto(nmap.get("toword").toString());
            int rtc = dicService.insDicChangeWords(nitem);
        }

    }

    private List<Map<String,Object>> loadDicEmo0227(String fileName) throws Exception {
        String seperator = "\t";
        List<Map<String, Object>> result = new ArrayList();
        int cntAll = 0;
        int itemCnt = 0;
        int errCnt = 0;
        String line = "";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(fileName), "ms949"))) {
            while ((line = reader.readLine()) != null
                //&& cntAll < 10000
                    ){
                if (cntAll > -1) {
                    if (!"".equals(line.trim())) {
                        String lines[] = line.trim().split(seperator);

/*
                    for (String ss : lines) {
                         System.out.print("___" + ss);
                    }
                    System.out.println("");
                            */

                        Map<String, Object> newItem = new HashMap();
                        newItem.put("word", (this.removeF(lines[0])));
                        if (lines.length > 1) newItem.put("toword", (this.removeF(lines[1])));


                        result.add(newItem);
                        itemCnt++;
                    }

                }
                cntAll++;
            }

            System.out.println("#allCount:"+cntAll);
            System.out.println("#itemCnt:"+itemCnt);
            System.out.println("#errCnt:"+errCnt);
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return result;
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public void processCalFreqFromDicKeywords() {
        List<String> types = dicKeywordsMapper.getKeywordTypes();

        int pageSize = 5000;
        DicKeywords reqDic = new DicKeywords();
        reqDic.setPageSize(pageSize);

        for (String type : types) {
            reqDic.setType(type);
            int countAll = dicKeywordsMapper.cntDicKeywordsByType(reqDic);
            System.out.println("#DIC2 type:"+type+" / countAll:"+countAll);
            if (countAll > 0) {
                int pageAll = 0;
                pageAll = countAll / pageSize + 1;
                System.out.println("#DIC2 type:"+type+" / pageAll:"+pageAll);

                for (int pno = 1; pno <= pageAll; pno++) {
                    reqDic.setPageNo(pno);
                    List<DicKeywords> thisArr = dicKeywordsMapper.getDicKeywordsPaging(reqDic);
                    System.out.println("#DIC3 type:" + type + " / page:" + pno + " / size:" + thisArr.size());


                    for(int cn=0; cn < thisArr.size(); cn++) {
                        DicKeywords kw = thisArr.get(cn);

                            //System.out.println("#DIC4 type:" + type + " / page:" + pno + " / keyword:" + kw.getKeyword());
                            DicKeywords ndic = new DicKeywords();
                            ndic.setType("METAS"+type);
                            ndic.setKeyword(kw.getKeyword());
                            int cntMetas = dicKeywordsMapper.cntTagsMetasByDicKeywords(ndic);
                            System.out.println("#DIC4 type:" + type + " / page:" + pno + " / keyword:" + kw.getKeyword()+ " / cntMetas:"+cntMetas);

                            double cntMetas1 = (double) cntMetas;
                            ndic.setFreq1(cntMetas1);
                            ndic.setIdx(kw.getIdx());
                            int rtU = dicKeywordsMapper.uptDicKeywords2(ndic);
                    }
                }
            }

        }
    }

    @Override
    public void writeRankOfDicKeywordByFreq1() {
        List<String> types = dicKeywordsMapper.getKeywordTypes();

        for(String type : types) {
            DicKeywords req = new DicKeywords();
            req.setType(type);
            List<DicKeywords> wordsList = dicKeywordsMapper.getRankOfDicKeywordsFreq1(req);
            System.out.println("# type:"+type+" / wordsList.size:"+wordsList.size());

            String seperator = "\t";
            String lineFeed = System.getProperty("line.separator");
            String resultStr = "";
            resultStr = "키워드" + seperator + "문서수" + lineFeed;

            for (DicKeywords kw : wordsList) {
                if(!kw.getKeyword().trim().equals(""))
                resultStr += kw.getKeyword() + seperator + kw.getFreq1() + lineFeed;
            }

            String fileNameContent = "RANK_180327_"+type+".tsv";

            int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultStr, UPLOAD_DIR, fileNameContent, "euc-kr");
        }
    }

    public void processRankForDicKeywordsAndGenres_() {
        //List<String> types = dicKeywordsMapper.getKeywordTypes();

        //for(String type : types) {
        Set<String> genreArr = new HashSet();
        ItemsMetas reqIm = new ItemsMetas();
        reqIm.setMtype("genre");
        List<ItemsMetas> resIm = itemsMetasMapper.getItemsMetasByMtype(reqIm);
        for(ItemsMetas im : resIm) {
            String genreStr = "";
            if (im != null && im.getMeta() != null) {
                genreStr = im.getMeta().trim();
                genreStr = CommonUtil.removeNationStr(genreStr);

                if (!"".equals(genreStr) && !genreStr.contains("분")) {
                    if (genreStr.contains(" ")) {
                        String genres[] = genreStr.split(" ");
                        for (String gn : genres) {
                            gn = gn.trim();
                            if (!"".equals(gn)) {
                                genreArr.add(gn);
                            }
                        }
                    } else {
                        genreArr.add(genreStr);
                    }
                }
            }
        }
        System.out.println("#RES.genreArr:"+genreArr.toString());

    }

    @Override
    public void processRankForDicKeywordsAndGenres() {
        String origGenres = "공포, 다큐멘터리, 서사, 뮤지컬, 무협, 미스터리, 액션, 코미디, SF, 에로, 드라마, 컬트, 판타지, 가족, 전쟁, 멜로로맨스, " +
                "범죄, 모험, 애니메이션, 블랙코미디, 실험, 스릴러, 서스펜스, 느와르, TV영화, 서부, 공연실황";
        origGenres = origGenres.replace(" ","");

        Map<String, Integer> calMap = new HashMap();
        String genresp[] = origGenres.split(",");
        //String genresp2[] = {"드라마", "전쟁"};

        List<String> types = dicKeywordsMapper.getKeywordTypes();

        for(String genre : genresp) {
            for (String type : types) {

                int pageSize = 2000;
                DicKeywords reqDic = new DicKeywords();
                reqDic.setPageSize(pageSize);
                reqDic.setPageNo(1);

                    reqDic.setType(type);
                    //int countAll = dicKeywordsMapper.cntDicKeywordsByType(reqDic);
                    //System.out.println("#DIC2 type:"+type+" / countAll:"+countAll);
                    //if (countAll > 0) {
                        //int pageAll = 0;
                        //pageAll = countAll / pageSize + 1;
                        //System.out.println("#DIC2 type:"+type+" / pageAll:"+pageAll);

                        //pageAll = 2;
                        //for (int pno = 1; pno <= pageAll; pno++) {
                            //reqDic.setPageNo(pno);
                            List<DicKeywords> thisArr = dicKeywordsMapper.getDicRankKeywordsPaging(reqDic);
                            System.out.println("#DIC3 type:" + type + " / size:" + thisArr.size());

                            int keywordAllCnt = thisArr.size();
                            //keywordAllCnt = 5;
                            for(int cn=0; cn < keywordAllCnt; cn++) {

                                DicKeywords kw = thisArr.get(cn);

                                System.out.println("#DIC4 type:" + type + " / keyword:" + kw.getKeyword());
                                String keyword = kw.getKeyword();
                                keyword = keyword.trim();

                                if (!"".equals(keyword) && !"\\".equals(keyword)) {
                                    DicKeywords ndic = new DicKeywords();
                                    ndic.setType("METAS" + type);
                                    ndic.setGenre(genre);
                                    ndic.setKeyword(keyword);

                                    List<DicKeywords> cntMetasByType = dicKeywordsMapper.cntTagsMetasByDicKeywordsAndGenre(ndic);
                                    System.out.println("#DIC4 type:" + type + " / keyword:" + keyword
                                            + " / cntMetasByType:" + cntMetasByType.toString());

                                    for (DicKeywords dkw : cntMetasByType) {
                                        String mapKey = genre + "_" + dkw.getType() + "_" + keyword;

                                        int sum1 = 0;
                                        int cntMetas = dkw.getCnt();
                                        if (calMap.get(mapKey) != null) sum1 = calMap.get(mapKey);
                                        sum1 += cntMetas;

                                        calMap.put(mapKey, sum1);
                                    }
                                }
                            }

                            System.out.println("#type:"+type+"  #RES.calMap:"+calMap.size());

                            Set entrySet = calMap.entrySet();
                            Iterator it = entrySet.iterator();

                            while (it.hasNext()) {
                                Map.Entry me = (Map.Entry) it.next();
                                String keys = me.getKey().toString();
                                String keyss[] = keys.split("_");
                                if (keyss != null && keyss.length == 3) {
                                    //String genre = keyss[0];
                                    //String mtype = keyss[1];
                                    String keyword = keyss[2];
                                    int cnt = (int) me.getValue();
                                    DicKeywords reqDk = new DicKeywords();
                                    reqDk.setGenre(genre);
                                    reqDk.setType(type);
                                    reqDk.setKeyword(keyword);
                                    reqDk.setCnt(cnt);
                                    int rti = dicKeywordsMapper.insDicRankWords2(reqDk);
                                }

                            }

                            calMap = null;
                            calMap = new HashMap();
                        //}
                    //}


            }
        }
    }

    @Override
    public void writeNoGenreItems() {
        List<Items> items = testMapper.getNoGenreItems();
        System.out.println("#RES.size:"+items.size());

        String seperator = "\t";
        String lineFeed = System.getProperty("line.separator");
        String resultStr = "";
        resultStr = "CONTENT_ID" + seperator + "ITEM_IDX" + seperator + "TITLE" + lineFeed;

        for (Items item : items) {
                resultStr += item.getCid() + seperator + item.getIdx() + seperator + item.getTitle() + lineFeed;        }

        String fileNameContent = "NO_GENRE_ITEMS_180329.tsv";

        int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultStr, UPLOAD_DIR, fileNameContent, "euc-kr");
    }

    private Map<String, Object> loadDicSubgenreGenres(String fileName) throws Exception {
        String seperator = "\t";
        Map<String, Object> result = new HashMap();
        int cntAll = 0;
        int itemCnt = 0;
        int errCnt = 0;
        String line = "";

        List<String> topGenreArr = new ArrayList();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(fileName), "ms949"))) {
            while ((line = reader.readLine()) != null
                //&& cntAll < 10000
                    ){
                if (cntAll > -1) {
                    if (!"".equals(line.trim())) {
                        String lines[] = line.trim().split(seperator);
                        List<String> newSubgenreTitleArr = null;

                        if (cntAll == 0) {
                            for (String ts : lines) {
                                topGenreArr.add(ts);
                            }
                        }
                        else {
                            System.out.println("# topGenreArr: size:"+topGenreArr.size()+" / datas::"+topGenreArr.toString());
                            System.out.println("# lines.length:"+lines.length);
                            for (int no=1; no < lines.length; no++) {

                                newSubgenreTitleArr = new ArrayList();
                                String topGenre = topGenreArr.get(no);
                                String leftGenre = lines[0];
                                newSubgenreTitleArr.add(topGenre);
                                newSubgenreTitleArr.add(leftGenre);

                                String toGenre = StringUtil.getSortedStringStrsAddSeperator(newSubgenreTitleArr, "___");
                                System.out.println("# "+no+" 'th topGenre:"+topGenre+"/leftGenre:"+leftGenre
                                        + "   dest_genre:" + lines[no] + "   toGenre:"+toGenre);
                                if (!"_".equals(lines[no].trim())) {
                                    result.put(toGenre, lines[no]);
                                }
                            }
                            System.out.println("");

                        }

                        //System.out.println("# size:" + lines.length + " line_All:" + newItem.toString());

                        //result.add(newItem);
                        itemCnt++;
                    }

                }
                cntAll++;
            }

            System.out.println("#allCount:"+cntAll);
            System.out.println("#itemCnt:"+itemCnt);
            System.out.println("#errCnt:"+errCnt);
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return result;
    }


    @Override
    public List<Map<String, Object>> loadDicSearchTxt(String fileName) throws Exception {
        String seperator = "\\|";
        Map<String, Object> result = new HashMap();
        int cntAll = 0;
        int itemCnt = 0;
        int errCnt = 0;
        String line = "";

        List<Map<String,Object>> listMap = new ArrayList();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(fileName), "ms949"))) {
            while ((line = reader.readLine()) != null
                //&& cntAll < 100
                    ){
                if (cntAll > 0) {
                    if (!"".equals(line.trim())) {
                        String lines[] = line.trim().split(seperator);

                        Map<String,Object> newItem = null;

                        //System.out.println("# listMap: size:"+listMap.size()+" / newItem::"+newItem.toString());
                        System.out.println("# line::"+line);
                        System.out.println("# lines.length:"+lines.length);

                        if (lines != null && lines.length > 3) {
                            newItem = new HashMap();
                            if (lines[0] != null) newItem.put("asset_id", lines[0]);
                            if (lines.length > 1 && lines[1] != null) newItem.put("title", lines[1]);
                            if (lines.length > 3 && lines[3] != null) {
                                newItem.put("searchtxt", lines[3]);

                                listMap.add(newItem);
                            }

                        }
                        System.out.println("");

                        }

                        //System.out.println("# size:" + lines.length + " line_All:" + newItem.toString());

                        //result.add(newItem);
                        itemCnt++;
                    }


                cntAll++;
            }

            System.out.println("#allCount:"+cntAll);
            System.out.println("#itemCnt:"+itemCnt);
            System.out.println("#errCnt:"+errCnt);
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return listMap;
    }

    private JsonArray getCombinedSearchKeywords(String origTxt, String appendTxt) throws Exception {
        JsonArray res = null;
        Set<String> newSet = new HashSet();

        if (!"".equals(origTxt.trim()) && !"".equals(appendTxt.trim())) {
            res = JsonUtil.getJsonArray(origTxt);
            if (res == null) res = new JsonArray();

            appendTxt = appendTxt.replace(", ", ",");
            String[] appList = appendTxt.split(",");
            if (appList != null && appList.length > 0) {
                for (String app : appList) {
                    app = app.trim();

                    boolean isExist = false;
                    for (String s : newSet) {
                        if (s.equals(app)) {
                            isExist = true;
                            break;
                        }
                    }
                    if (!isExist) {
                        res.add(app);
                        newSet.add(app);
                    }
                }
            }

        }
        return res;
    }

    @Override
    public void processSearchTxtManualAppendFile() throws Exception {
        String fileName = "/home/daisy/upload/asset_full.txt";
        List<Map<String, Object>> result = this.loadDicSearchTxt(fileName);
        System.out.println("#RESULT_MAP::" + result.toString());
        System.out.println("#RESULT_MAP.size::" + result.size());

        this.processSearchTxtManualAppend(result);
    }

    @Override
    public void processSearchTxtManualAppend(List<Map<String,Object>> reqMapList) {

        Map<String, Object> resultMap = new HashMap();

        if (reqMapList != null && reqMapList.size() > 0) {

            for (Map<String, Object> nmap : reqMapList) {
                if (nmap != null && nmap.get("asset_id") != null && nmap.get("searchtxt") != null) {
                    String asset_id = (String) nmap.get("asset_id");
                    if (asset_id.length() > 17) {
                        asset_id = asset_id.substring(0, 18);
                    }

                    List<Map<String,Object>> idxBySeries = testMapper.getItemIdxBySeriesAssetId(asset_id);

                    if (idxBySeries != null && idxBySeries.size() > 0) {
                        for (Map<String,Object> idxmap : idxBySeries) {
                            long longidx = (long) idxmap.get("idx");
                            int idx = (int) longidx;
                            ItemsTags reqit = new ItemsTags();
                            reqit.setIdx(idx);
                            //reqit.setTagidx((Integer) idxmap.get("tagidx"));
                            reqit.setMtype("LIST_SEARCHKEYWORDS");

                            List<ItemsTags> listTags = itemsTagsMapper.getItemsTagsMetasByManual(reqit);
                            for (ItemsTags it: listTags) {
                                System.out.println("# tags_ser::" + it.toString());

                                String origTxt = (it.getMeta() != null ? it.getMeta() : "");
                                it.setMeta_orig(origTxt);
                                String appendTxt = (String) nmap.get("searchtxt");

                                JsonArray newSearchWordsArray = null;
                                if (!"".equals(origTxt.trim()) && !"".equals(appendTxt.trim())) {
                                    try {
                                        newSearchWordsArray = this.getCombinedSearchKeywords(origTxt, appendTxt);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                if(newSearchWordsArray != null) {
                                    it.setMeta(newSearchWordsArray.toString());

                                    String key = it.getIdx() + "_" + it.getTagidx();
                                    resultMap.put(key, it);
                                    System.out.println("#UPT METAS::"+it.toString());

                                    //int rtu = itemsTagsMapper.uptItemsTagsByManual(it);
                                    //System.out.println("#UPT METAS::"+it.toString() + "  rt:"+rtu);
                                }
                            }
                        }
                    }

                    else {
                        List<Map<String, Object>> idxByContents = testMapper.getItemIdxByContentsAssetId(asset_id);

                        if (idxByContents != null && idxByContents.size() > 0) {
                            for (Map<String, Object> idxmap : idxByContents) {
                                long longidx = (long) idxmap.get("idx");
                                int idx = (int) longidx;
                                ItemsTags reqit = new ItemsTags();
                                reqit.setIdx(idx);
                                //reqit.setTagidx((Integer) idxmap.get("tagidx"));
                                reqit.setMtype("LIST_SEARCHKEYWORDS");

                                List<ItemsTags> listTags = itemsTagsMapper.getItemsTagsMetasByManual(reqit);
                                for (ItemsTags it : listTags) {
                                    System.out.println("# tags_con::" + it.toString());

                                    String origTxt = (it.getMeta() != null ? it.getMeta() : "");
                                    it.setMeta_orig(origTxt);
                                    String appendTxt = (String) nmap.get("searchtxt");

                                    JsonArray newSearchWordsArray = null;
                                    if (!"".equals(origTxt.trim()) && !"".equals(appendTxt.trim())) {

                                        System.out.println("#append SEARCH_KEYWORDS::"+appendTxt);

                                        try {
                                            newSearchWordsArray = this.getCombinedSearchKeywords(origTxt, appendTxt);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    if(newSearchWordsArray != null) {

                                        System.out.println("#newSearchWordsArray METAS::"+newSearchWordsArray.toString());
                                        it.setMeta(newSearchWordsArray.toString());

                                        String key = it.getIdx() + "_" + it.getTagidx();
                                        resultMap.put(key, it);
                                        System.out.println("#UPT METAS::"+it.toString());

                                        //int rtu = itemsTagsMapper.uptItemsTagsByManual(it);
                                        //System.out.println("#UPT METAS::"+it.toString() + "  rt:"+rtu);
                                    }
                                }
                            }
                        }

                    }

                }

            }
        }

        //System.out.println("#RESULT_MAP:"+resultMap.toString());

        Set entrySet = resultMap.entrySet();
        Iterator it = entrySet.iterator();

        while(it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            //System.out.println((me.getKey()+","+me.getValue()));
            ItemsTags reqit = (ItemsTags) me.getValue();
            int rti = itemsTagsMapper.insItemsTagsMetas_0503(reqit);
        }

        System.out.println("#RESULT_MAP.size():"+resultMap.size());
    }

    @Override
    public void checkEsProperty() {
        System.out.println("ES host:"+ES_HOST+" / port:"+ES_PORT);
    }

    @Override
    public void putBulkDataToEsIndex(String idxName, Map<String,Object> reqMap) throws Exception {
        int es_port = 9200;
        try {
            if (!"".equals(ES_PORT)) {
                es_port = Integer.parseInt(ES_PORT);
            }
        } catch (Exception e) {}

        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ES_HOST), es_port));

        IndicesAdminClient indicesAdminClient = client.admin().indices();

        BulkRequestBuilder bulkRequest = client.prepareBulk();

        //String jsonSource = FileUtils.getTestIdxData();
        /*
        List<Map<String,Object>> resMap = null;
        try {
            resMap = FileUtils.getTestIdxDataMap();
            //System.out.println("#resultMap:"+resMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        Set entrySet = reqMap.entrySet();
        Iterator it = entrySet.iterator();

        int lineCnt = 1;
        while(it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            System.out.println("# "+lineCnt++ +" st map data:"+(me.getKey()+":"+me.getValue()));
            JsonObject newObj = new JsonObject();
            newObj.addProperty("id",lineCnt);
            newObj.addProperty("topic", me.getKey().toString());
            newObj.addProperty("keywords", me.getValue().toString());
            bulkRequest.add(client.prepareIndex(idxName, "item", String.valueOf(lineCnt)).setSource(newObj.toString()));
            //bulkRequest.source("id", "1", "adresse", "USA");
        }

        BulkResponse bulkResponse = bulkRequest.get();

        if (bulkResponse.hasFailures()) {
            System.out.println("#fail:"+bulkResponse.getItems().toString());
            //process failures by iterating through each bulk response item
        }

        System.out.println("#bulkResponse:"+bulkResponse.toString());

        client.close();

    }

    private Map<String, Object> loadDicSubgenreKeywords(String fileName) throws Exception {
        String seperator = "\t";
        Map<String, Object> result = new HashMap();
        int cntAll = 0;
        int itemCnt = 0;
        int errCnt = 0;
        String line = "";

        List<String> topGenreArr = new ArrayList();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(fileName), "ms949"))) {
            while ((line = reader.readLine()) != null
                //&& cntAll < 10000
                    ){
                if (cntAll > 0) {
                    if (!"".equals(line.trim())) {
                        String lines[] = line.trim().split(seperator);

                        for(int j=0; j < lines.length; j++) {
                            System.out.print("#orig j:"+j+"::"+lines[j]+" | ");
                        }
                        System.out.println(" ");

                        List<String> newSubgenreTitleArr = null;

                        if (cntAll == 0) {
                        }
                        else {
                            List<String> words = new ArrayList();
                            String subgenre = lines[0];

                            String newKey = "";
                            for(int j=0; j < lines.length; j++) {
                                System.out.print("#j:"+j+"::"+lines[j]+" | ");

                                if(j>0) {
                                    if(!lines[j].equals("_")) {
                                        if (j % 2 == 1) {
                                            newKey = lines[j];
                                        } else {
                                            newKey = newKey + "___" + lines[j];
                                            words.add(newKey);
                                        }
                                    }
                                }
                            }
                            System.out.println(" ");
                            System.out.println("#subgenre:"+subgenre+"  | words::"+words.toString());
                            result.put(subgenre, words);
                            /*
                            for (int no=1; no < lines.length; no++) {

                                newSubgenreTitleArr = new ArrayList();
                                String topGenre = topGenreArr.get(no);
                                String leftGenre = lines[0];
                                newSubgenreTitleArr.add(topGenre);
                                newSubgenreTitleArr.add(leftGenre);

                                String toGenre = StringUtil.getSortedStringStrsAddSeperator(newSubgenreTitleArr, "___");
                                System.out.println("# "+no+" 'th topGenre:"+topGenre+"/leftGenre:"+leftGenre
                                        + "   dest_genre:" + lines[no] + "   toGenre:"+toGenre);
                                if (!"_".equals(lines[no].trim())) {
                                    result.put(toGenre, lines[no]);
                                }
                            }
                            System.out.println("");
                            */

                        }

                        //System.out.println("# size:" + lines.length + " line_All:" + newItem.toString());

                        //result.add(newItem);
                        itemCnt++;
                    }

                }
                cntAll++;
            }

            System.out.println("#allCount:"+cntAll);
            System.out.println("#itemCnt:"+itemCnt);
            System.out.println("#errCnt:"+errCnt);
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return result;
    }

    private Map<String, Object> loadDicResultTagKeywords(String fileName) throws Exception {
        String seperator = "\t";
        Map<String, Object> result = new HashMap();
        int cntAll = 0;
        int itemCnt = 0;
        int errCnt = 0;
        String line = "";

        List<String> topGenreArr = new ArrayList();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(fileName), "utf-8"))) {
            while ((line = reader.readLine()) != null
                //&& cntAll < 100
                    ){
                if (cntAll > -1) {
                    if (!"".equals(line.trim())) {

                        String lines[] = line.trim().split(seperator);

                        for(int j=0; j < lines.length; j++) {
                            System.out.print("#j:"+j+"::"+lines[j]+" | ");
                        }
                        System.out.println(" ");


                        if (cntAll == 0) {
                        }
                        else {
                            if (lines != null && lines.length > 2) {
                                Map<String, Object> newItem = new HashMap();

                                String mtype = lines[0];
                                mtype = mtype.trim();

                                String word = lines[1];
                                word = word.trim();

                                String wordto = (lines.length > 2 && lines[2] != null ? lines[2] : "");
                                wordto = wordto.trim();
                                wordto = wordto.replace("_","");

                                String wordadd = (lines.length > 3 && lines [3] != null ? lines[3] : "");
                                wordadd = wordadd.trim();
                                wordadd = wordadd.replace("_", "");

                                String worddel = (lines.length > 4 && lines [4] != null ? lines[4] : "");
                                worddel = worddel.trim();
                                worddel = worddel.replace("_","");


                                if (!"".equals(word)) {
                                    if (!"".equals(wordto) || !"".equals(wordadd) || !"".equals(worddel)) {
                                        String newKey = mtype+"_"+word;
                                        //System.out.println(" ");

                                        newItem.put("mtype",mtype);
                                        newItem.put("word",word);
                                        newItem.put("wordto",wordto);
                                        newItem.put("wordadd",wordadd);
                                        newItem.put("worddel",worddel);

                                        System.out.println("#newKey:"+newKey+"  | result_tag_item::"+newItem.toString());

                                        int rti = testMapper.insDicResultTagKeywords(newItem);

                                        result.put(newKey, newItem);
                                    }
                                }
                            }
                        }

                        //System.out.println("# size:" + lines.length + " line_All:" + newItem.toString());

                        //result.add(newItem);
                        itemCnt++;
                    }

                }
                cntAll++;
            }

            System.out.println("#allCount:"+cntAll);
            System.out.println("#itemCnt:"+itemCnt);
            System.out.println("#errCnt:"+errCnt);
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return result;
    }


    @Override
    public void processMixedSubgenre() throws Exception {
        List<Map<String, Object>> itemList = testMapper.getItemsForSubgenre();
        System.out.println("#itemsList.size:"+itemList.size());

        for(Map<String, Object> nmap : itemList) {
            //for(int i=0; i<10; i++) {
            //Map<String, Object>   nmap = itemList.get(i);
            System.out.println("#req::"+nmap.toString());

            String reqStr0 = "";
            String reqStr = "";
            if(nmap.get("genre") != null) reqStr0 = nmap.get("genre").toString();
            if(nmap.get("kt_rating") != null) reqStr = reqStr0 + " " + nmap.get("kt_rating").toString();
            System.out.println("#req str: genre/kt_rating::"+reqStr);

            Set result = dicService.getMixedGenreArrayFromGenre(reqStr, "subgenre_filter");
            String toMeta = result.toString();
            toMeta = CommonUtil.removeNationStr(toMeta);

            String origin = "";
            if(nmap.get("origin") != null) origin = nmap.get("origin").toString();

            System.out.println("###REQ_STR2::origin:"+origin);
            Set resultNation = dicService.getMixedNationGenreArrayFromGenre(reqStr0, origin, "origin");
            System.out.println("#RESULT_NATION:"+resultNation.toString());
            String toMetaOrigin = resultNation.toString();
            toMetaOrigin = CommonUtil.removeBrackets(toMetaOrigin);

            if(!"".equals(toMeta)) {
                ItemsMetas newMeta = new ItemsMetas();
                long longIdx = (Long) nmap.get("idx");
                int intIdx = (int) longIdx;
                newMeta.setIdx(intIdx);
                newMeta.setMtype("subgenreMix2");
                newMeta.setMeta(toMeta);
                System.out.println("#save itemsMetas:" + newMeta.toString());
                int rtItm = itemsService.insItemsMetas(newMeta);
            }

            if(!"".equals(toMetaOrigin)) {
                ItemsMetas newMeta = new ItemsMetas();
                long longIdx = (Long) nmap.get("idx");
                int intIdx = (int) longIdx;
                newMeta.setIdx(intIdx);
                newMeta.setMtype("subgenreOrgin2");
                newMeta.setMeta(toMetaOrigin);
                System.out.println("#save itemsMetas2:" + newMeta.toString());
                int rtItm = itemsService.insItemsMetas(newMeta);
            }
        }

        System.out.println("#itemsList.size:"+itemList.size());
    }

    @Override
    public void processMixedSubgenre2() throws Exception {
        List<Map<String, Object>> itemList = testMapper.getItemsForSubgenre2();
        System.out.println("#itemsList.size:"+itemList.size());

        for(Map<String, Object> nmap : itemList) {
            //for(int i=0; i<10; i++) {
            //Map<String, Object>   nmap = itemList.get(i);
            System.out.println("#req::"+nmap.toString());

            String reqStr0 = "";
            String reqStr = "";
            if(nmap.get("genre") != null) reqStr0 = nmap.get("genre").toString();
            if(nmap.get("kt_rating") != null) reqStr = reqStr0 + " " + nmap.get("kt_rating").toString();
            System.out.println("#req str: genre/kt_rating::"+reqStr);

            Set result = dicService.getMixedGenreArrayFromGenre(reqStr, "subgenre_filter");
            String toMeta = result.toString();
            toMeta = CommonUtil.removeNationStr(toMeta);
            toMeta = toMeta.replace("영화", "시리즈");

            String origin = "";
            if(nmap.get("origin") != null) origin = nmap.get("origin").toString();

            System.out.println("###REQ_STR2::origin:"+origin);
            Set resultNation = dicService.getMixedNationGenreArrayFromGenre(reqStr0, origin, "origin");
            System.out.println("#RESULT_NATION:"+resultNation.toString());
            String toMetaOrigin = resultNation.toString();
            toMetaOrigin = CommonUtil.removeBrackets(toMetaOrigin);
            toMetaOrigin = toMetaOrigin.replace("영화", "시리즈");

            if(!"".equals(toMeta)) {
                ItemsMetas newMeta = new ItemsMetas();
                long longIdx = (Long) nmap.get("idx");
                int intIdx = (int) longIdx;
                newMeta.setIdx(intIdx);
                newMeta.setMtype("subgenreMix2");
                newMeta.setMeta(toMeta);
                System.out.println("#save itemsMetas:" + newMeta.toString());
                int rtItm = itemsService.insItemsMetas(newMeta);
            }

            if(!"".equals(toMetaOrigin)) {
                ItemsMetas newMeta = new ItemsMetas();
                long longIdx = (Long) nmap.get("idx");
                int intIdx = (int) longIdx;
                newMeta.setIdx(intIdx);
                newMeta.setMtype("subgenreOrgin2");
                newMeta.setMeta(toMetaOrigin);
                System.out.println("#save itemsMetas2:" + newMeta.toString());
                int rtItm = itemsService.insItemsMetas(newMeta);
            }
        }

        System.out.println("#itemsList.size:"+itemList.size());
    }


    @Override
    public void processSubgenreToTags() throws Exception {
        List<Map<String, Object>> itemList = testMapper.getItemsForSubgenre();
        System.out.println("#itemsList.size:"+itemList.size());

        for(Map<String, Object> nmap : itemList) {
            //for(int i=0; i<10; i++) {
            //Map<String, Object>   nmap = itemList.get(i);
            System.out.println("#req::"+nmap.toString());

            String reqStr0 = "";
            String reqStr = "";
            if(nmap.get("genre") != null) reqStr0 = nmap.get("genre").toString();
            if(nmap.get("kt_rating") != null) reqStr = reqStr0 + " " + nmap.get("kt_rating").toString();
            System.out.println("#req str: genre/kt_rating::"+reqStr);

            Set result = dicService.getMixedGenreArrayFromGenre(reqStr, "subgenre_filter");
            String toMeta = result.toString();
            toMeta = CommonUtil.removeNationStr(toMeta);
            if (nmap.get("type").toString().contains("CcubeSeries")) {
                toMeta = toMeta.replace("영화", "시리즈");
            }

            String origin = "";
            if(nmap.get("origin") != null) origin = nmap.get("origin").toString();

            System.out.println("###REQ_STR2::origin:"+origin);
            Set resultNation = dicService.getMixedNationGenreArrayFromGenre(reqStr0, origin, "origin");
            System.out.println("#RESULT_NATION:"+resultNation.toString());
            String toMetaOrigin = resultNation.toString();
            toMetaOrigin = CommonUtil.removeBrackets(toMetaOrigin);

            if (nmap.get("type").toString().contains("CcubeSeries")) {
                toMetaOrigin = toMetaOrigin.replace("영화", "시리즈");
            }

            if (!"".equals(toMeta)) {
                if (!"".equals(toMetaOrigin)) {
                    toMeta += " ," + toMetaOrigin;
                }
            } else {
                toMeta = toMetaOrigin;
            }

            long longidx = (long) nmap.get("idx");
            int itemid = (int) longidx;
            //max tagidx 를 찾는다.
            ItemsTags reqit = new ItemsTags();
            reqit.setIdx(itemid);
            reqit.setStat("S");
            int maxTagIdx = itemsTagsMapper.getMaxTagsIdxByItemIdx(reqit);

            if(!"".equals(toMeta)) {
                //String newList[] = toMeta.split(",");
                JsonArray newArr = JsonUtil.convertStringToJsonArrayForObjWithDelemeter(toMeta, ",");

                ItemsTags reqMeta = new ItemsTags();
                reqMeta.setIdx(itemid);
                reqMeta.setTagidx(maxTagIdx);
                reqMeta.setMtype("LIST_SUBGENRE");
                reqMeta.setMeta(newArr.toString());

                System.out.println("#MLOG run insItemsTagsMetas data:"+reqMeta.toString());
                int rt = itemsTagsService.insItemsTagsMetas(reqMeta);
            }
        }

        System.out.println("#itemsList.size:"+itemList.size());
    }


    @Override
    public void processSubgenreToTagsSer() throws Exception {
        List<Map<String, Object>> itemList = testMapper.getItemsForSubgenre2();
        System.out.println("#itemsList.size:"+itemList.size());

        for(Map<String, Object> nmap : itemList) {
            //for(int i=0; i<10; i++) {
            //Map<String, Object>   nmap = itemList.get(i);
            System.out.println("#req::"+nmap.toString());

            String reqStr0 = "";
            String reqStr = "";
            if(nmap.get("genre") != null) reqStr0 = nmap.get("genre").toString();
            if(nmap.get("kt_rating") != null) reqStr = reqStr0 + " " + nmap.get("kt_rating").toString();
            System.out.println("#req str: genre/kt_rating::"+reqStr);

            Set result = dicService.getMixedGenreArrayFromGenre(reqStr, "subgenre_filter");
            String toMeta = result.toString();
            toMeta = CommonUtil.removeNationStr(toMeta);
            if (nmap.get("type").toString().contains("CcubeSeries")) {
                toMeta = toMeta.replace("영화", "시리즈");
            }

            String origin = "";
            if(nmap.get("origin") != null) origin = nmap.get("origin").toString();

            System.out.println("###REQ_STR2::origin:"+origin);
            Set resultNation = dicService.getMixedNationGenreArrayFromGenre(reqStr0, origin, "origin");
            System.out.println("#RESULT_NATION:"+resultNation.toString());
            String toMetaOrigin = resultNation.toString();
            toMetaOrigin = CommonUtil.removeBrackets(toMetaOrigin);

            if (nmap.get("type").toString().contains("CcubeSeries")) {
                toMetaOrigin = toMetaOrigin.replace("영화", "시리즈");
            }

            long longidx = (long) nmap.get("idx");
            int itemid = (int) longidx;
            //max tagidx 를 찾는다.
            ItemsTags reqit = new ItemsTags();
            reqit.setIdx(itemid);
            reqit.setStat("S");
            int maxTagIdx = itemsTagsMapper.getMaxTagsIdxByItemIdx(reqit);

            if(!"".equals(toMeta)) {
                //String newList[] = toMeta.split(",");
                JsonArray newArr = JsonUtil.convertStringToJsonArrayForObjWithDelemeter(toMeta, ",");

                ItemsTags reqMeta = new ItemsTags();
                reqMeta.setIdx(itemid);
                reqMeta.setTagidx(maxTagIdx);
                reqMeta.setMtype("LIST_SUBGENRE");
                reqMeta.setMeta(newArr.toString());

                System.out.println("#MLOG run insItemsTagsMetas data:"+reqMeta.toString());
                int rt = itemsTagsService.insItemsTagsMetas(reqMeta);
            }
        }

        System.out.println("#itemsList.size:"+itemList.size());
    }

    private String getMetasStringFromJsonObject(JsonObject resultObj, List<String> origTypes) {
        String result = "";

        List<String> resultArr = new ArrayList();
        JsonArray metaArr = null;
        if (resultObj != null && origTypes != null) {
            for(String type : origTypes) {
                String typeStr = type.replace("METAS","");
                if (resultObj.get(type) != null) {
                    metaArr = (JsonArray) resultObj.get(type);
                    //System.out.println("#metaArr:"+metaArr.toString());
                    JsonObject jo = null;
                    if(metaArr != null && metaArr.size() > 0) {
                        for(JsonElement je : metaArr) {
                            jo = (JsonObject) je;
                            String keyOne = (jo.get("word") != null) ? jo.get("word").toString() : "";
                            if((!"".equals(keyOne.trim()))) {
                                keyOne = keyOne.replace("\"","");
                                keyOne = keyOne.replace("\'","");
                                keyOne = typeStr +"___"+ keyOne.trim();
                                //System.out.println("#word:"+keyOne);
                                resultArr.add(keyOne);
                            }
                        }
                    }
                }
            }
        }

        if(resultArr != null && resultArr.size() > 0) {
            result = resultArr.toString();
            result = result.replace(",","");
            result = CommonUtil.removeBrackets(result);
        }
        return result;
    }

    @Override
    public void processSubgenre2ByKeywords() throws Exception {
        List<Map<String, Object>> itemList = testMapper.getItemsForSubgenre();
        System.out.println("#itemsList.size:"+itemList.size());

        List<String> origTypes = new ArrayList<String>();
        origTypes.add("METASWHEN");
        origTypes.add("METASWHERE");
        origTypes.add("METASWHO");
        origTypes.add("METASWHAT");
        origTypes.add("METASEMOTION");
        //origTypes.add("METASCHARACTER");

        long itemIdx0 = (long) 0;
        JsonObject hits = null;
        int itemIdx = 0;
        JsonObject resultObj = null;
        JsonObject resultEs = null;

        String subGenreWord1 = "";
        String subGenreWord2 = "";
        String subGenreWords = "";
        JsonArray words = null;
        String reqStr = "";

        int cnt = 0;
        JsonObject jo = null;
        String word = "";
        double score = 0.0;

        ItemsMetas newMeta = null;
        int intIdx = 0;
        int rtItm1 = -1;
        long longIdx = 0;
        Map<String, Object> nmap = null;
        for(int i=0; i<itemList.size(); i++) {
            nmap = itemList.get(i);
        //for(nmap : itemList) {
            //for(int i=0; i<10; i++) {
            //Map<String, Object>   nmap = itemList.get(i);
            System.out.println("#req::"+nmap.toString());

            itemIdx0 = (long) nmap.get("idx");
            itemIdx = (int) itemIdx0;
            resultObj = itemsTagsService.getItemsMetasByIdx(itemIdx, origTypes, "S");

            String itemGenre = (nmap.get("genre") != null) ? nmap.get("genre").toString() : "";

            //System.out.println("#resultObj:"+resultObj.toString());
            //System.out.println("#resultSet:"+getMetasStringFromJsonObject(resultObj, origTypes));
            reqStr = "";
            reqStr = getMetasStringFromJsonObject(resultObj, origTypes);
            System.out.println("#requestEs for reqStr:"+reqStr);
            resultEs = getSearchedEsData("idx_subgenre", "keywords"
                    , reqStr);

            //System.out.println("#resultEs:"+resultEs.toString());
            hits = getEsTopWords(resultEs);
            //System.out.println("#resultEs.words top2::"+hits.toString());
            subGenreWord1 = "";
            subGenreWord2 = "";
            subGenreWords = "";
            words = null;
            if (hits != null && hits.get("words") != null) {
                words = hits.get("words").getAsJsonArray();
                cnt = 0;
                jo = null;
                word = "";
                score = 0.0;
                for (JsonElement je : words) {
                    jo = (JsonObject) je;
                    word = "";
                    word = (jo.get("word") != null) ? jo.get("word").getAsString() : "";

                    if (cnt == 0) {
                        score = 0.0;
                        //subGenreWord1 = word;
                        score = (jo.get("score") != null) ? jo.get("score").getAsDouble() : 0.0;
                        if (score > 6.0) subGenreWord1 = word;
                    } else {
                        subGenreWord2 = word;
                    }
                    cnt++;
                }
                subGenreWords = hits.get("words").toString();
            }
            System.out.println("#subGenreWord1:"+subGenreWord1+" / subGenreWord2:"+subGenreWord2
                                +" / subGenreWords:"+subGenreWords);
                //if(nmap.get("genre") != null) reqStr = nmap.get("genre").toString();
            //if(nmap.get("kt_rating") != null) reqStr = reqStr + " " + nmap.get("kt_rating").toString();

            /*
            List<String> result = dicService.getMixedGenreArrayFromGenre(reqStr, "mixgenre");
            String toMeta = result.toString();
            toMeta = CommonUtil.removeNationStr(toMeta);
            */
            //if(!"".equals(subGenreWord1)) {
            newMeta = new ItemsMetas();
            longIdx = (Long) nmap.get("idx");
            intIdx = (int) longIdx;
            newMeta.setIdx(intIdx);
            newMeta.setMtype("subgenremeta1");
            newMeta.setMeta(subGenreWord1);
            System.out.println("#save itemsMetas.1:" + newMeta.toString());
            //rtItm1 = itemsService.insItemsMetas(newMeta);

            newMeta.setMtype("subgenremeta2");
            newMeta.setMeta(subGenreWord2);
            System.out.println("#save itemsMetas.2:" + newMeta.toString());
            //rtItm1 = itemsService.insItemsMetas(newMeta);

            newMeta.setMtype("subgenrewords");
            newMeta.setMeta(subGenreWords);
            System.out.println("#save itemsMetas.S:" + newMeta.toString());
            rtItm1 = itemsService.insItemsMetas(newMeta);

            List<String> genreTopic01 = dicService.getMixedGenreArrayFromFilter(subGenreWord1 + " " + itemGenre, "subgenre_filter");
            String subGenreTopic1 = genreTopic01.toString();
            subGenreTopic1 = CommonUtil.removeBrackets(subGenreTopic1);

            System.out.println("###subGenreTopic1 subgenreWord1:"+subGenreWord1+ "  ::itemGenre::"+itemGenre+" ::"+subGenreTopic1);
            newMeta.setMtype("subgenretopic1");
            newMeta.setMeta(subGenreTopic1);
            rtItm1 = itemsService.insItemsMetas(newMeta);

            List<String> genreTopic02 = dicService.getMixedGenreArrayFromFilter(subGenreWord2 + " " + itemGenre, "subgenre_filter");
            String subGenreTopic2 = genreTopic02.toString();
            subGenreTopic2 = CommonUtil.removeBrackets(subGenreTopic2);
            newMeta.setMtype("subgenretopic2");
            newMeta.setMeta(subGenreTopic2);
            rtItm1 = itemsService.insItemsMetas(newMeta);

            System.out.println("###subGenreTopic2 subgenreWord2:"+subGenreWord2+ "  ::itemGenre::"+itemGenre+" ::"+subGenreTopic2);


            //}

        }
    }



    @Override
    public void processSubgenre2ByMetaKeywords() throws Exception {
        List<Map<String, Object>> itemList = testMapper.getItemsForSubgenre();
        System.out.println("#itemsList.size:"+itemList.size());

        List<String> origTypes = new ArrayList<String>();
        origTypes.add("METASWHEN");
        origTypes.add("METASWHERE");
        origTypes.add("METASWHO");
        origTypes.add("METASWHAT");
        origTypes.add("METASEMOTION");
        //origTypes.add("METASCHARACTER");

        long itemIdx0 = (long) 0;
        int itemIdx = 0;
        JsonObject resultObj = null;

        int cnt = 0;
        JsonObject jo = null;
        String word = "";
        double score = 0.0;

        ItemsMetas newMeta = null;
        int intIdx = 0;
        int rtItm1 = -1;
        long longIdx = 0;
        //Map<String, Object> nmap = null;
        //for(int i=0; i<itemList.size(); i++) {
          //  nmap = itemList.get(i);
            //for(nmap : itemList) {
            for(int i=0; i<5; i++) {
            Map<String, Object>   nmap = itemList.get(i);
            System.out.println("#req::"+nmap.toString());

            JsonObject resultEs = null;

            String esWord1 = ""; String esWord2 = ""; String esWord3 = ""; String esWord4 = "";
            String esWords = "";

            String meta_single1 = ""; String meta_single2 = ""; String meta_single3 = ""; String meta_single4 = "";
            String meta_genre1 = ""; String meta_genre2 = ""; String meta_genre3 = ""; String meta_genre4 = "";

            JsonArray words = null;
            String reqStr = "";

            JsonObject hits = null;

            Set<String> esWarr1 = new HashSet<String>();
            Set<String> esWarr2 = new HashSet<String>();
            Set<String> esWarr3 = new HashSet<String>();
            Set<String> esWarr4 = new HashSet<String>();

            itemIdx0 = (long) nmap.get("idx");
            itemIdx = (int) itemIdx0;
            resultObj = itemsTagsService.getItemsMetasByIdx(itemIdx, origTypes, "S");

            String itemGenre = (nmap.get("genre") != null) ? nmap.get("genre").toString() : "";

            //System.out.println("#resultObj:"+resultObj.toString());
            //System.out.println("#resultSet:"+getMetasStringFromJsonObject(resultObj, origTypes));
            reqStr = "";
            reqStr = getMetasStringFromJsonObject(resultObj, origTypes);
            System.out.println("#requestEs for reqStr:"+reqStr);
            resultEs = getSearchedEsData("idx_subgenre", "keywords"
                    , reqStr);

            System.out.println("#resultEs:"+resultEs.toString());
            hits = getEsTopWords(resultEs);
            System.out.println("#resultEs.words top1::"+hits.toString());

            words = null;
            if (hits != null && hits.get("words") != null) {
                words = hits.get("words").getAsJsonArray();
                cnt = 0;
                jo = null;
                word = "";
                score = 0.0;
                for (JsonElement je : words) {
                    jo = (JsonObject) je;
                    word = "";
                    word = (jo.get("word") != null) ? jo.get("word").getAsString() : "";

                    if (cnt == 0) {
                        score = (jo.get("score") != null) ? jo.get("score").getAsDouble() : 0.0;
                        if (score > 6.0) esWarr1.add(word);
                        if (score > 12.0) esWarr2.add(word);
                        if (score > 18.0) esWarr3.add(word);
                        if (score > 24.0) esWarr3.add(word);
                    } else {
                        //subGenreWord2 = word;
                    }
                    cnt++;
                }
                esWords = hits.get("words").toString();
            }

            if (esWarr1 != null) esWord1 = esWarr1.toString(); esWord1 = esWord1.replace(", "," ");
                if (esWarr2 != null) esWord2 = esWarr2.toString(); esWord2 = esWord2.replace(", "," ");
                if (esWarr3 != null) esWord3 = esWarr3.toString(); esWord3 = esWord3.replace(", "," ");
                if (esWarr4 != null) esWord4 = esWarr4.toString(); esWord4 = esWord4.replace(", "," ");

            System.out.println("#esWord1:(6.0)::"+esWord1+" / esWord2:(12.0):"+esWord2
                    +" / esWords:"+esWords);
            //if(nmap.get("genre") != null) reqStr = nmap.get("genre").toString();
            //if(nmap.get("kt_rating") != null) reqStr = reqStr + " " + nmap.get("kt_rating").toString();

            if(!"".equals(esWord1)) {
                newMeta = new ItemsMetas();
                longIdx = (Long) nmap.get("idx");
                intIdx = (int) longIdx;
                newMeta.setIdx(intIdx);
                newMeta.setMtype("esWord1");
                newMeta.setMeta(esWord1);
                System.out.println("#save itemsMetas.1:" + newMeta.toString());
                //rtItm1 = itemsService.insItemsMetas(newMeta);

                Set<String> metaSingleArr1 = null;
                if (!"".equals(esWord1)) metaSingleArr1 = dicService.getMetaSingleFromGenre(metaSingleArr1, esWord1, "meta_single");
                if (metaSingleArr1 != null) meta_single1 = metaSingleArr1.toString();
                System.out.println("#meta_single1:"+meta_single1);
            }

            if (!"".equals(esWord2)) {
                newMeta.setMtype("esWord2");
                newMeta.setMeta(esWord2);
                System.out.println("#save itemsMetas.2:" + newMeta.toString());
                //rtItm1 = itemsService.insItemsMetas(newMeta);

                Set<String> metaSingleArr2 = null;
                if (!"".equals(esWord2)) metaSingleArr2 = dicService.getMetaSingleFromGenre(metaSingleArr2, esWord2, "meta_single");
                if (metaSingleArr2 != null) meta_single2 = metaSingleArr2.toString();
                System.out.println("#meta_single2:"+meta_single2);
            }

            if (!"".equals(esWord3)) {
                newMeta.setMtype("esWord3");
                newMeta.setMeta(esWord3);
                System.out.println("#save itemsMetas.3:" + newMeta.toString());
                //rtItm1 = itemsService.insItemsMetas(newMeta);

                Set<String> metaSingleArr3 = null;
                if (!"".equals(esWord3)) metaSingleArr3 = dicService.getMetaSingleFromGenre(metaSingleArr3, esWord3, "meta_single");
                if (metaSingleArr3 != null) meta_single3 = metaSingleArr3.toString();
                System.out.println("#meta_single3:"+meta_single3);
            }

            if (!"".equals(esWord4)) {
                newMeta.setMtype("esWord4");
                newMeta.setMeta(esWord4);
                System.out.println("#save itemsMetas.2:" + newMeta.toString());
                //rtItm1 = itemsService.insItemsMetas(newMeta);

                Set<String> metaSingleArr4 = null;
                if (!"".equals(esWord4)) metaSingleArr4 = dicService.getMetaSingleFromGenre(metaSingleArr4, esWord4, "meta_single");
                if (metaSingleArr4 != null) meta_single4 = metaSingleArr4.toString();
                System.out.println("#meta_single4:"+meta_single4);
            }

            if (!"".equals(esWords)) {
                newMeta.setMtype("esWords");
                newMeta.setMeta(esWords);
                System.out.println("#save itemsMetas.esWords:" + newMeta.toString());
                //rtItm1 = itemsService.insItemsMetas(newMeta);
            }

            if(!"".equals(meta_single1)) {
                newMeta.setMtype("meta_single1");
                newMeta.setMeta(meta_single1);
                System.out.println("#save itemsMetas.meta_single1:" + newMeta.toString());
                //rtItm1 = itemsService.insItemsMetas(newMeta);
            }

            if(!"".equals(meta_single2)) {
                newMeta.setMtype("meta_single1");
                newMeta.setMeta(meta_single2);
                System.out.println("#save itemsMetas.meta_single2:" + newMeta.toString());
                //rtItm1 = itemsService.insItemsMetas(newMeta);
            }

            //List<Set> genreTopic01 = dicService.getMixedGenreArrayFromFilter(subGenreWord1 + " " + itemGenre, "subgenre_filter");
            //String subGenreTopic1 = genreTopic01.toString();
            //subGenreTopic1 = CommonUtil.removeBrackets(subGenreTopic1);

            //System.out.println("###subGenreTopic1 subgenreWord1:"+subGenreWord1+ "  ::itemGenre::"+itemGenre+" ::"+subGenreTopic1);
            //newMeta.setMtype("subgenretopic1");
            //newMeta.setMeta(subGenreTopic1);
            //rtItm1 = itemsService.insItemsMetas(newMeta);

            //List<String> genreTopic02 = dicService.getMixedGenreArrayFromFilter(subGenreWord2 + " " + itemGenre, "subgenre_filter");
            //String subGenreTopic2 = genreTopic02.toString();
            //subGenreTopic2 = CommonUtil.removeBrackets(subGenreTopic2);
            //newMeta.setMtype("subgenretopic2");
            //newMeta.setMeta(subGenreTopic2);
            //rtItm1 = itemsService.insItemsMetas(newMeta);

            //System.out.println("###subGenreTopic2 subgenreWord2:"+subGenreWord2+ "  ::itemGenre::"+itemGenre+" ::"+subGenreTopic2);


            //}

        }
    }

    private static EsConfig esConfig = null;
    private static RestClient restClient = null;

    private JsonObject getSearchedEsData(String idxName, String fieldName, String reqStr) throws Exception {
        //String result = "";
        JsonObject result = new JsonObject();

        try {
            if (restClient == null) {
                esConfig = new EsConfig();
                System.out.println("##REST::ElasticSearch server:" + EsConfig.INSTANCE.getEs_host() + ":" + EsConfig.INSTANCE.getEs_port() + "//:request_param:" + reqStr);
                restClient = RestClient.builder(
                        new HttpHost(EsConfig.INSTANCE.getEs_host(), EsConfig.INSTANCE.getEs_port(), "http")).build();
            }
            //HttpEntity entity = new NStringEntity(reqStr, ContentType.APPLICATION_JSON);

            /*
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("keywords", reqStr);
            paramMap.put("pretty", "true");
            */
            HttpEntity entity = new NStringEntity(
                    "{\n" +
                            "    \"query\" : {\n" +
                            "    \"match\": { \""+fieldName+"\":\""+reqStr+"\"} \n" +
                            "} \n"+
                            "}",
                    ContentType.APPLICATION_JSON
            );
            Response response = restClient.performRequest(
                    "GET",
                    "/"+idxName+"/_search",
                    Collections.singletonMap("pretty", "true"),
                    entity
            );


            /*
            Response response = restClient.performRequest(
                    "GET",
                    "/"+idxName+"/_search",
                    paramMap
            );
            */

            //System.out.println(EntityUtils.toString(response.getEntity()));
            //result = response.getEntity().toString();
            String resultStr = EntityUtils.toString(response.getEntity());
            result = JsonUtil.getJsonObject(resultStr);

            //System.out.println("#REST::ElasticSearch Result:"+result.toString());

        } catch (Exception e) { e.printStackTrace(); }


        return result;
    }

    private JsonObject getEsTopWords(JsonObject reqObj) {
        JsonObject result = null;
        JsonArray words = null;
        if(reqObj != null) {
            result = new JsonObject();
            words = new JsonArray();

            JsonObject hitsObj = null;
            if(reqObj.get("hits") != null) hitsObj = (JsonObject) reqObj.get("hits");
            //System.out.println("#hits:"+hitsObj.toString());
            JsonArray hitsArr = null;
            if(hitsObj != null && hitsObj.get("hits") !=null) hitsArr = hitsObj.get("hits").getAsJsonArray();
            //System.out.println("#hitsArr:"+hitsArr.toString());
            int cnt = 0;
            for(JsonElement je : hitsArr) {
                if (cnt < 2) {
                    JsonObject jo = (JsonObject) je;
                    JsonObject jobj = null;
                    String wordOne = "";

                    double score = 0.0;
                    if (jo != null && jo.get("_score") != null) score = jo.get("_score").getAsDouble();
                    if (jo != null && jo.get("_source") != null) jobj = jo.get("_source").getAsJsonObject();
                    if (jobj != null && jobj.get("topic") != null) wordOne = jobj.get("topic").getAsString();
                    System.out.println("# score:"+score+"  /  word:"+wordOne);
                    JsonObject word1 = new JsonObject();
                    word1.addProperty("score", String.valueOf(score));
                    word1.addProperty("word", wordOne);
                    words.add(word1);
                } else {
                    break;
                }
                cnt++;
            }
            result.add("words", words);
        }

        return result;
    }


    private JsonObject getEsTopWordsWithPointCut(JsonObject reqObj, Double limitPoint) {
        JsonObject result = null;
        JsonArray words = null;
        if(reqObj != null) {
            result = new JsonObject();
            words = new JsonArray();

            JsonObject hitsObj = null;
            if(reqObj.get("hits") != null) hitsObj = (JsonObject) reqObj.get("hits");
            //System.out.println("#hits:"+hitsObj.toString());
            JsonArray hitsArr = null;
            if(hitsObj != null && hitsObj.get("hits") !=null) hitsArr = hitsObj.get("hits").getAsJsonArray();
            //System.out.println("#hitsArr:"+hitsArr.toString());
            int cnt = 0;
            for(JsonElement je : hitsArr) {
                if (cnt < 2) {
                    JsonObject jo = (JsonObject) je;
                    JsonObject jobj = null;
                    String wordOne = "";

                    double score = 0.0;
                    if (jo != null && jo.get("_score") != null) score = jo.get("_score").getAsDouble();
                    if (jo != null && jo.get("_source") != null) jobj = jo.get("_source").getAsJsonObject();
                    if (jobj != null && jobj.get("topic") != null) wordOne = jobj.get("topic").getAsString();
                    System.out.println("# score:"+score+"  /  word:"+wordOne);
                    if (score > limitPoint) {
                        JsonObject word1 = new JsonObject();
                        word1.addProperty("score", String.valueOf(score));
                        word1.addProperty("word", wordOne);
                        words.add(word1);
                    }
                } else {
                    break;
                }
                cnt++;
            }
            result.add("words", words);
        }

        return result;
    }

    @Override
    public void writeItemsAndSubgenre() {
        List<Map<String, Object>> items = testMapper.getItemsAndSubgenre();
        System.out.println("#RES.size:" + items.size());

        String seperator = "\t";
        String lineFeed = System.getProperty("line.separator");
        String resultStr = "";
        resultStr = "아이템ID" + seperator + "CONTENT_ID" + seperator + "TITLE"
                + seperator + "장르" + seperator + "KT등급"
                + seperator + "조합장르"
                + seperator + "국가조합장르"
                + seperator + "서브장르1" + seperator + "서브장르2"
                + seperator + "서브장르-매핑장르1" + seperator + "서브장르-매핑장르2"
                + lineFeed;

        String itemStr = "";
        int cnt = 1;
        for (Map<String, Object> item : items) {
            itemStr = item.get("idx").toString();
            itemStr = itemStr + seperator + item.get("cid").toString();
            itemStr = itemStr + seperator + item.get("title").toString();
            itemStr = itemStr + seperator + (item.get("genre") != null ? item.get("genre").toString() : "");
            itemStr = itemStr + seperator + (item.get("kt_rating") != null ? item.get("kt_rating").toString() : "");
            itemStr = itemStr + seperator + (item.get("subgenreMix") != null ? item.get("subgenreMix").toString() : "");
            itemStr = itemStr + seperator + (item.get("subgenreOrgin") != null ? item.get("subgenreOrgin").toString() : "");
            itemStr = itemStr + seperator + (item.get("subgenreword1") != null ? item.get("subgenreword1").toString() : "");
            itemStr = itemStr + seperator + (item.get("subgenreword2") != null ? item.get("subgenreword2").toString() : "");
            itemStr = itemStr + seperator + (item.get("subgenretopic1") != null ? item.get("subgenretopic1").toString() : "");
            itemStr = itemStr + seperator + (item.get("subgenretopic2") != null ? item.get("subgenretopic2").toString() : "");

            resultStr += itemStr + lineFeed;
            System.out.println("#write "+cnt+"'s item::"+itemStr);
            cnt++;
        }

        String fileNameContent = "SUBGENRE_ITEMS_180413.tsv";
        int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultStr, UPLOAD_DIR, fileNameContent, "euc-kr");
    }


    @Override
    public void processSubgenrePointCutting() throws Exception {
        double pointCut = 6.0;
        ItemsMetas reqM = new ItemsMetas();
        reqM.setPageNo(1);
        reqM.setPageSize(15000);
        reqM.setMtype("subgenrewords");
        List<ItemsMetas> itemList = itemsMetasMapper.getItemsMetasByMtypePaging(reqM);
        System.out.println("#itemsList.size:"+itemList.size());

        String metaStr = "";
        int itemIdx = 0;
        JsonArray words = null;
        String subGenreWord1 = "";
        ItemsMetas newMeta = null;
        int rtItm1 = 0;

        for(int i=0; i<itemList.size(); i++) {
            subGenreWord1 = "";
            ItemsMetas item = itemList.get(i);
            itemIdx = item.getIdx();
            metaStr = item.getMeta();

            System.out.println("#req:: idx:"+itemIdx+"  metaStr:"+metaStr.toString());
            if(!"".equals(metaStr.trim())) {
                try {
                    words = JsonUtil.getJsonArray(metaStr);
                    if (words != null && words.size() > 0) {
                        JsonObject jo1 = (JsonObject) words.get(0);
                        if (jo1 != null) {
                            if (jo1.get("score") != null && jo1.get("score").getAsDouble() > pointCut) {
                                subGenreWord1 = jo1.get("word").getAsString();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //if(!"".equals(subGenreWord1)) {
                newMeta = new ItemsMetas();
                newMeta.setIdx(itemIdx);
                newMeta.setMtype("subgenreword1");
                newMeta.setMeta(subGenreWord1);
                System.out.println("#save itemsMetas:" + newMeta.toString());
                rtItm1 = itemsService.insItemsMetas(newMeta);
            //}

        }
    }



    @Override
    public void writeItemsStatRt() {
        List<Map<String, Object>> items = testMapper.getItemsStatRt();
        System.out.println("#RES.size:" + items.size());

        String seperator = "\t";
        String lineFeed = System.getProperty("line.separator");
        String resultStr = "";
        resultStr = "아이템ID" + seperator + "CONTENT_ID" + seperator + "TITLE"
                + seperator + "승인횟수"
                + lineFeed;

        String itemStr = "";
        for (Map<String, Object> item : items) {
            itemStr = item.get("idx").toString();
            itemStr = itemStr + seperator + item.get("cid").toString();
            itemStr = itemStr + seperator + item.get("title").toString();
            itemStr = itemStr + seperator + (item.get("st_cnt") != null ? item.get("st_cnt").toString() : "");

            resultStr += itemStr + lineFeed;

        }

        String fileNameContent = "ITEMS_STAT_RT_180409.tsv";
        int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultStr, UPLOAD_DIR, fileNameContent, "euc-kr");
    }

    @Override
    public void processRetryDaumAward() throws Exception {
        List<Map<String,Object>> itemList = testMapper.getItemsForDaumAward();
        System.out.println("#itemList.size::"+itemList.size());

        ConfTarget reqInfo = null;
        List<ConfPreset> psList = null;
        ConfPreset ps1 = null;
        ItemsMetas newMeta = null;
        int rtItm1 = 0;
        String awardStr = "";

        for(Map<String,Object> item : itemList) {
            reqInfo = new ConfTarget();
            psList = new ArrayList<ConfPreset>();

            ps1 = new ConfPreset();
            ps1.setPs_type("meta");
            ps1.setPs_tag(".main_detail");
            ps1.setDest_field("award");
            ps1.setDescriptp("daummovie_award");
            psList.add(ps1);

            reqInfo.setPresetList(psList);
            reqInfo.setTg_url("DAUM_MOVIE");

            long longIdx = (item != null && item.get("idx") != null) ? (long) item.get("idx"): 0;
            int itemIdx = (int) longIdx;
            String movieTitle = (item != null && item.get("title") != null) ? item.get("title").toString() : "";
            String movieYear = (item != null && item.get("year") != null) ? item.get("year").toString() : "";

            reqInfo.setParam1(movieTitle);
            reqInfo.setMovietitle(movieTitle);
            reqInfo.setMovieyear(movieYear);

            if (itemIdx > 0 && !"".equals(movieTitle)) {

            JsonObject result = naverMovieService.getContents("DAUM_MOVIE", reqInfo);
            awardStr = (result != null && result.get("metas") != null) ? result.get("metas").getAsJsonObject().get("award").toString() : "";
            System.out.println("#Result awardStr:"+ awardStr);

            newMeta = new ItemsMetas();
            newMeta.setIdx(itemIdx);
            newMeta.setMtype("award");
            newMeta.setMeta(awardStr);
            System.out.println("#save itemsMetas:" + newMeta.toString());
            rtItm1 = itemsService.insItemsMetas(newMeta);

            }
        }
    }

    /* dicKeywords 사전 Tag 리스트 */
    private List<String> getDicTypes() {
        List<String> dicTypes = new ArrayList();
        //dicTypes.add("METASEMOTION");
        dicTypes.add("METASWHAT");
        dicTypes.add("METASWHEN");
        dicTypes.add("METASWHERE");
        dicTypes.add("METASWHO");
        return dicTypes;
    }

    private int saveItemsTagsMetas(String metas, int itemIdx, int tagidx, String mtype) {
        ItemsTags itm = new ItemsTags();
        itm.setIdx(itemIdx);
        itm.setTagidx(tagidx);
        itm.setMtype(mtype);
        itm.setMeta(metas);
        int itm1 = itemsTagsService.insItemsTagsMetas(itm);

        return itm1;
    }

    @Override
    public void processItemsSearchKeywordRetry() {
        List<Map<String, Object>> itemList = testMapper.getItemsForSearchKeywords();
        System.out.println("#itemList.size::" + itemList.size());

        ConfTarget reqInfo = null;
        List<ConfPreset> psList = null;
        ConfPreset ps1 = null;
        ItemsMetas newMeta = null;
        int rtItm1 = 0;
        String awardStr = "";
        int maxTagidx = 0;

        for (Map<String, Object> item : itemList) {
            // type 리스트 취득
            List<String> types = this.getDicTypes();

            // 아이템 ID 발췌
            long longIdx = (item != null && item.get("idx") != null) ? (long) item.get("idx"): 0;
            int itemIdx = (int) longIdx;
            String title = item.get("title").toString();

            System.out.println("### process itemIdx:"+itemIdx+"   /  title:"+title);

            // 아이템 태깅 메타 리스트 타입별 조회
            ItemsTags reqm = new ItemsTags();
            reqm.setIdx(itemIdx);
            reqm.setStat("S");

            List<ItemsTags> itemsMetasAll = itemsTagsService.getItemsTagsMetasByItemIdx(reqm);

            // all_keyword_list 조합
            //Map<String, Double> allKeywordList = new HashMap();
            // 각 사전별 top1 키워드와 ratio만 모음
            Map<String, Double> topWordsForTypesList = new HashMap();

            JsonArray searchKeyword = null;
            for(ItemsTags im : itemsMetasAll) {
                maxTagidx = im.getTagidx();

                if (im != null && im.getMtype() != null
                        && im.getMeta() != null && !"".equals(im.getMeta().trim())) {
                    // type 별 word_list 조합
                    Map<String, Double> typesKeywordList = null;
                    searchKeyword = null;

                    for (String mtype : types) {
                        typesKeywordList = new HashMap();

                        String metaStr = "";
                        JsonArray metaWords = null;
                        if (mtype.equals(im.getMtype())) {
                            metaStr = im.getMeta();

                            //System.out.println("###mtype:"+mtype+" / origMetaStr:"+metaStr);
                            try {
                                metaWords = JsonUtil.getJsonArray(metaStr);
                                if(metaWords != null && metaWords.size() > 0) {
                                    JsonObject wordOne = null;
                                    for (JsonElement je : metaWords) {
                                        wordOne = null;
                                        wordOne = (JsonObject) je;
                                        String word = "";
                                        Double ratio = 0.0;
                                        if (wordOne.get("word") != null && wordOne.get("ratio") != null) {
                                            word = wordOne.get("word").getAsString();
                                            word = word.trim();
                                            ratio = wordOne.get("ratio").getAsDouble();

                                            //System.out.println("#putting stand-by word:"+word+" / ratio:"+ratio);

                                            if (!"".equals(word)) {
                                                //allKeywordList.put(word, ratio);
                                                //System.out.println("#putting map word:"+word+" / ratio:"+ratio);
                                                typesKeywordList.put(word, ratio);
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // 각 type 별 최상위 랭킹 키워드만 추출

                            //System.out.println("###mtype:"+mtype+" / typesKeywordList::"+typesKeywordList.toString());
                            Map<String, Double> typeWords3 = MapUtil.getSortedDescMapForDouble(typesKeywordList);
                            //System.out.println("###mtype:"+mtype+"/ sorted typeWords3::"+typeWords3.toString());
                            Map<String, Double> typeWords2 = MapUtil.getCuttedMapFromMapByLimit(typeWords3, 1);
                            String word1 = "";
                            Double ratio1 = 0.0;
                            //System.out.println("#mtype:"+mtype);
                            if(typeWords2 != null) {
                                for ( String key : typeWords2.keySet() ) {

                                    word1 = key;
                                    ratio1 = typeWords2.get(key);
                                    //System.out.println("#top keyword1 for mtype:"+mtype+"  / word:"+word1 + " / ratio1:"+ratio1);
                                    break;
                                }
                            }

                            if (!"".equals(word1)) {
                                topWordsForTypesList.put(word1, ratio1);
                            }
                        }
                    }
                }
            }

            //System.out.println("#topWordsForTypesList::"+topWordsForTypesList.toString());
            //Map<String, Double> searchKeyword3 = MapUtil.getSortedDescMapForDouble(topWordsForTypesList);
            //Map<String, Double> searchKeyword2 = MapUtil.getCuttedMapFromMapByLimit(searchKeyword3, 4);
            Map<String, Double> searchKeyword1 = MapUtil.getSortedDescMapForDouble(topWordsForTypesList);
            //System.out.println("#searchKeyword1:"+searchKeyword1.toString());
            searchKeyword = MapUtil.getListNotMapKeywords(searchKeyword1);
            //System.out.println("#searchKeyword:"+searchKeyword.toString());

            // 검색 키워드 jsonArray mtype:LIST_SEARCHKEYWORDS 저장
            int resitm2 = saveItemsTagsMetas(searchKeyword.toString(), itemIdx, maxTagidx, "LIST_SEARCHKEYWORDS");
            System.out.println("### do saveItemsTagsMetas searchKeyword:"+searchKeyword.toString()+"/itemIdx:"+itemIdx
                    + "/maxTagIdx:"+maxTagidx+"/mtype:LIST_SEARCHKEYWORDS");
        }
    }

    @Override
    public void getCntForSubgenre() throws Exception {
        /*
        String mtype = "subgenreMix";

        List<Map<String, Object>> origCntMap = testMapper.cntItemsMetasForSubgenre(mtype);

        System.out.println("## orig.size:"+origCntMap.size());

        for(Map<String, Object> nmap : origCntMap) {
            int cnt = (int) nmap.get("cnt");

        }
        */
    }

    @Override
    public void getCntForSubgenre(String mtype) throws Exception {
        //String mtype = "subgenreMix";
        List<Map<String, Object>> origCntMap = testMapper.cntItemsMetasForSubgenre(mtype);

        System.out.println("## orig.size:"+origCntMap.size());
        Map<String, Long> resultMap = new HashMap();

        for(Map<String, Object> nmap : origCntMap) {
            long longCnt = (long) nmap.get("cnt");
            //int cnt = (int) longCnt;
            String metaStr = (String) nmap.get("meta");
            Set<String> metas = new HashSet();
            if (metaStr.trim().contains(", ")) {
                String[] metasArr = metaStr.split(", ");
                if (metasArr != null && metasArr.length > 0) {
                    for (String ma : metasArr) {
                        metas.add(ma);
                    }
                }
            } else {
                metas.add(metaStr);
            }

            if(metas.size() > 0) {
                for(String mt : metas) {
                    long oldCnt = 0;
                    if (resultMap.get(mt) != null) {
                        oldCnt = (long) resultMap.get(mt);
                        resultMap.remove(mt);
                    }
                    long newCnt = oldCnt + longCnt;
                    resultMap.put(mt, newCnt);
                }
            }
        }

        //System.out.println("#RES::"+resultMap.toString());
        MapUtil.printMapAll(resultMap);
        System.out.println("#RES.size::"+resultMap.size());
    }

    @Override
    public List<Map<String, Object>> getRtItems0417() throws Exception {
        return testMapper.getRtItems0417();
    }

    @Override
    public void writeItemsRt0417() {
        List<Map<String, Object>> items = testMapper.getRtItems0417();
        System.out.println("#RES.size:" + items.size());

        String seperator = "\t";
        String lineFeed = System.getProperty("line.separator");
        String resultStr = "";
        resultStr = "아이템ID" + seperator + "TYPE" + seperator + "CONTENT_ID" + seperator + "TITLE" + seperator + "REGDATE"
                + seperator + "cnt_cid" + seperator + "cnt_sid"
                + lineFeed;

        String itemStr = "";
        int cnt = 1;
        for (Map<String, Object> item : items) {
            itemStr = item.get("idx").toString();
            itemStr = itemStr + seperator + item.get("type").toString();
            itemStr = itemStr + seperator + item.get("cid").toString();
            itemStr = itemStr + seperator + item.get("title").toString();
            itemStr = itemStr + seperator + item.get("regdate").toString();
            itemStr = itemStr + seperator + item.get("cntcid").toString();
            itemStr = itemStr + seperator + item.get("cntsid").toString();

            resultStr += itemStr + lineFeed;
            System.out.println("#write "+cnt+"'s item::"+itemStr);
            cnt++;
        }

        String fileNameContent = "RT_ITEMS_180417.tsv";
        int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultStr, UPLOAD_DIR, fileNameContent, "euc-kr");
    }

    @Override
    public void insCcubeContentsAssetListUniq() throws Exception {
        int rt = 0;
        List<Map<String,Object>> assetList = testMapper.getContentsAssetList();
        System.out.println("#assetList.size:"+assetList.size());
        Map<String, Object> item = null;

        List<Map<String,Object>> insertList = new ArrayList();

        for(int i=0; i<assetList.size(); i++) {
            //for(int i=0; i<50; i++) {
            item = assetList.get(i);
            String asset = item.get("asset_id").toString();
            System.out.println("#asset_id:"+asset);
            if (asset.contains(",")) {
                String assetArr[] = asset.trim().split(",");
                Map<String, Object> newItem = null;
                for (String ass : assetArr) {
                    newItem = new HashMap();
                    newItem.put("cid", item.get("cid").toString());
                    newItem.put("asset_id", ass);
                    System.out.println("#insert new item:"+newItem.toString());

                    insertList.add(newItem);
                }
            } else {
                System.out.println("#insert item:"+item.toString());
                insertList.add(item);

                if (insertList.size() > 500) {
                    rt = testMapper.insContentsAsset(insertList);
                    insertList = null;
                    insertList = new ArrayList();
                }
            }
            System.out.println("#assetList.i:"+i);
        }
        System.out.println("#assetList.size:"+assetList.size());
        rt = testMapper.insContentsAsset(insertList);
    }



    @Override
    public void insCcubeSeriesAssetListUniq() throws Exception {
        int rt = 0;
        List<Map<String,Object>> assetList = testMapper.getSeriesAssetList();
        System.out.println("#assetList.size:"+assetList.size());
        Map<String, Object> item = null;

        List<Map<String,Object>> insertList = new ArrayList();

        for(int i=0; i<assetList.size(); i++) {
            //for(int i=0; i<50; i++) {
            item = assetList.get(i);
            String asset = item.get("asset_id").toString();
            System.out.println("#asset_id:"+asset);
            if (asset.contains(",")) {
                String assetArr[] = asset.trim().split(",");
                Map<String, Object> newItem = null;
                for (String ass : assetArr) {
                    newItem = new HashMap();
                    newItem.put("sid", item.get("sid").toString());
                    newItem.put("asset_id", ass);
                    System.out.println("#insert new item:"+newItem.toString());

                    insertList.add(newItem);
                }
            } else {
                System.out.println("#insert item:"+item.toString());
                insertList.add(item);

                if (insertList.size() > 500) {
                    rt = testMapper.insSeriesAsset(insertList);
                    insertList = null;
                    insertList = new ArrayList();
                }
            }
            System.out.println("#assetList.i:"+i);
        }
        System.out.println("#assetList.size:"+assetList.size());
        rt = testMapper.insSeriesAsset(insertList);
    }

    @Override
    public void writeCcubeContentsOutputCSV() throws Exception {
        //List<Map<String, Object>> itemList  = testMapper.getCcubeContentsAll();
        List<Map<String, Object>> itemList  = testMapper.getCcubeContentsAll();
        List<Map<String, Object>> resultList = new ArrayList();

        JsonArray contents = null;
        for (Map<String, Object> item : itemList) {
            long longIdx = (long) item.get("idx");
            int idx = (int) longIdx;
            String type = (String) item.get("type");
            String asset_id = (String) item.get("asset_id");
            String cid = (String) item.get("cid");
            String mcid = (String) item.get("mcid");
            String content_title = (String) item.get("content_title");
            String eng_title = (String) item.get("eng_title");
            String director = (String) item.get("director");
            String year = (String) item.get("year");
            //String asset_id = (String) (item.get("asset_id") != null ? item.get("asset_id") : "" );


            String meta_when = "";
            String meta_where = "";
            String meta_what = "";
            String meta_who = "";
            String meta_emotion = "";
            String meta_subgenre = "";
            String meta_search = "";
            String meta_charactor = "";
            String meta_reco_target = "";
            String meta_reco_situation = "";
            String meta_award = "";

            System.out.println("#RES:: idx:"+idx+"/ title:"+content_title);

            contents = ccubeService.getJsonArrayForCcubeOutput(null, type, item);
            if (contents != null && contents.size() > 0) {
                JsonObject jo = (JsonObject) contents.get(0);
                if (jo != null) {
                    meta_when = (jo.get("META_WHEN") != null ? jo.get("META_WHEN").getAsString() : "");
                    item.put("meta_when", meta_when);
                    meta_where = (jo.get("META_WHERE") != null ? jo.get("META_WHERE").getAsString() : "");
                    item.put("meta_where", meta_where);
                    meta_what = (jo.get("META_WHAT") != null ? jo.get("META_WHAT").getAsString() : "");
                    item.put("meta_what", meta_what);
                    meta_who = (jo.get("META_WHO") != null ? jo.get("META_WHO").getAsString() : "");
                    item.put("meta_who", meta_who);
                    meta_emotion = (jo.get("META_EMOTION") != null ? jo.get("META_EMOTION").getAsString() : "");
                    item.put("meta_emotion", meta_emotion);
                    meta_subgenre = (jo.get("META_SUBGENRE") != null ? jo.get("META_SUBGENRE").getAsString() : "");
                    item.put("meta_subgenre", meta_subgenre);
                    meta_search = (jo.get("META_SEARCH") != null ? jo.get("META_SEARCH").getAsString() : "");
                    item.put("meta_search", meta_search);
                    meta_charactor = (jo.get("META_CHARACTER") != null ? jo.get("META_CHARACTER").getAsString() : "");
                    item.put("meta_charactor", meta_charactor);
                    meta_reco_target = (jo.get("META_RECO_TARGET") != null ? jo.get("META_RECO_TARGET").getAsString() : "");
                    item.put("meta_reco_target", meta_reco_target);
                    meta_reco_situation = (jo.get("META_RECO_SITUATION") != null ? jo.get("META_RECO_SITUATION").getAsString() : "");
                    item.put("meta_reco_situation", meta_reco_situation);
                    meta_award = (jo.get("META_AWARD") != null ? jo.get("META_AWARD").getAsString() : "");
                    meta_award = meta_award.replace("현재페이지   1", "").trim(); item.put("meta_award", meta_award);
                    System.out.println("#meta_when:"+meta_when + " / meta_where:"+meta_where + " / meta_award:"+meta_award);
                    resultList.add(item);
                }
            }


        }

        String seperator = "\t";
        String lineFeed = System.getProperty("line.separator");

        String resultStr = "";
        resultStr = "content_id" + seperator + "master_content_id" + seperator + "asset_id"
                + seperator + "content_title" + seperator + "eng_title"
                + seperator + "director"
                + seperator + "year"
                //+ seperator + "asset_id"
                + seperator + "meta_when"
                + seperator + "meta_where"
                + seperator + "meta_what"
                + seperator + "meta_who"
                + seperator + "meta_emotion"
                + seperator + "meta_subgenre"
                + seperator + "meta_search"
                + seperator + "meta_charactor"
                + seperator + "meta_reco_target"
                + seperator + "meta_reco_situation"
                + seperator + "meta_award"
                + lineFeed;

        String itemStr = "";
        int cnt = 1;
        for (Map<String, Object> item : resultList) {
            //itemStr = item.get("idx").toString();
            itemStr = item.get("cid").toString();
            itemStr = itemStr + seperator + item.get("mcid").toString();
            itemStr = itemStr + seperator + item.get("asset_id").toString();
            itemStr = itemStr + seperator + item.get("content_title").toString();
            itemStr = itemStr + seperator + item.get("eng_title").toString();
            itemStr = itemStr + seperator + item.get("director").toString();
            itemStr = itemStr + seperator + item.get("year").toString();
            //itemStr = itemStr + seperator + item.get("asset_id").toString();

            itemStr = itemStr + seperator + item.get("meta_when").toString();
            itemStr = itemStr + seperator + item.get("meta_where").toString();
            itemStr = itemStr + seperator + item.get("meta_what").toString();
            itemStr = itemStr + seperator + item.get("meta_who").toString();
            itemStr = itemStr + seperator + item.get("meta_emotion").toString();
            itemStr = itemStr + seperator + item.get("meta_subgenre").toString();
            itemStr = itemStr + seperator + item.get("meta_search").toString();
            itemStr = itemStr + seperator + item.get("meta_charactor").toString();
            itemStr = itemStr + seperator + item.get("meta_reco_target").toString();
            itemStr = itemStr + seperator + item.get("meta_reco_situation").toString();
            itemStr = itemStr + seperator + item.get("meta_award").toString();

            resultStr += itemStr + lineFeed;
            System.out.println("#write "+cnt+"'s item::"+itemStr);
            cnt++;
        }

        String fileNameContent = "METAS_BY_CONTENTS_WITH_ASSET_180611.tsv";
        int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultStr, UPLOAD_DIR, fileNameContent, "utf-8");

    }



    @Override
    public void writeCcubeContentsOutputCSV_100() throws Exception {
        List<Map<String, Object>> itemList  = testMapper.getContentsOrigItemsAll_100();
        List<Map<String, Object>> resultList = new ArrayList();

        JsonArray contents = null;
        for (Map<String, Object> item : itemList) {
            long longIdx = (long) item.get("idx");
            int idx = (int) longIdx;
            String type = (String) item.get("type");
            String cid = (String) item.get("cid");
            String mcid = (String) item.get("mcid");
            String content_title = (String) item.get("content_title");
            String eng_title = (String) item.get("eng_title");
            String director = (String) item.get("director");
            String year = (String) item.get("year");
            //String asset_id = (String) (item.get("asset_id") != null ? item.get("asset_id") : "" );


            String meta_when = "";
            String meta_where = "";
            String meta_what = "";
            String meta_who = "";
            String meta_emotion = "";
            String meta_subgenre = "";
            String meta_search = "";
            String meta_charactor = "";
            String meta_reco_target = "";
            String meta_reco_situation = "";
            String meta_award = "";

            System.out.println("#RES:: idx:"+idx+"/ title:"+content_title);

            contents = ccubeService.getJsonArrayForCcubeOutput(null, type, item);
            if (contents != null && contents.size() > 0) {
                JsonObject jo = (JsonObject) contents.get(0);
                if (jo != null) {
                    meta_when = (jo.get("META_WHEN") != null ? jo.get("META_WHEN").getAsString() : "");
                    item.put("meta_when", meta_when);
                    meta_where = (jo.get("META_WHERE") != null ? jo.get("META_WHERE").getAsString() : "");
                    item.put("meta_where", meta_where);
                    meta_what = (jo.get("META_WHAT") != null ? jo.get("META_WHAT").getAsString() : "");
                    item.put("meta_what", meta_what);
                    meta_who = (jo.get("META_WHO") != null ? jo.get("META_WHO").getAsString() : "");
                    item.put("meta_who", meta_who);
                    meta_emotion = (jo.get("META_EMOTION") != null ? jo.get("META_EMOTION").getAsString() : "");
                    item.put("meta_emotion", meta_emotion);
                    meta_subgenre = (jo.get("META_SUBGENRE") != null ? jo.get("META_SUBGENRE").getAsString() : "");
                    item.put("meta_subgenre", meta_subgenre);
                    meta_search = (jo.get("META_SEARCH") != null ? jo.get("META_SEARCH").getAsString() : "");
                    item.put("meta_search", meta_search);
                    meta_charactor = (jo.get("META_CHARACTER") != null ? jo.get("META_CHARACTER").getAsString() : "");
                    item.put("meta_charactor", meta_charactor);
                    meta_reco_target = (jo.get("META_RECO_TARGET") != null ? jo.get("META_RECO_TARGET").getAsString() : "");
                    item.put("meta_reco_target", meta_reco_target);
                    meta_reco_situation = (jo.get("META_RECO_SITUATION") != null ? jo.get("META_RECO_SITUATION").getAsString() : "");
                    item.put("meta_reco_situation", meta_reco_situation);
                    meta_award = (jo.get("META_AWARD") != null ? jo.get("META_AWARD").getAsString() : "");
                    meta_award = meta_award.replace("현재페이지   1", "").trim(); item.put("meta_award", meta_award);
                    System.out.println("#meta_when:"+meta_when + " / meta_where:"+meta_where + " / meta_award:"+meta_award);
                    resultList.add(item);
                }
            }


        }

        String seperator = "\t";
        String lineFeed = System.getProperty("line.separator");

        String resultStr = "";
        resultStr = "content_id" + seperator + "master_content_id" + seperator + "content_title" + seperator + "eng_title"
                + seperator + "director"
                + seperator + "year"
                //+ seperator + "asset_id"
                + seperator + "meta_when"
                + seperator + "meta_where"
                + seperator + "meta_what"
                + seperator + "meta_who"
                + seperator + "meta_emotion"
                + seperator + "meta_subgenre"
                + seperator + "meta_search"
                + seperator + "meta_charactor"
                + seperator + "meta_reco_target"
                + seperator + "meta_reco_situation"
                + seperator + "meta_award"
                + lineFeed;

        String itemStr = "";
        int cnt = 1;
        for (Map<String, Object> item : resultList) {
            //itemStr = item.get("idx").toString();
            itemStr = item.get("cid").toString();
            itemStr = itemStr + seperator + item.get("mcid").toString();
            itemStr = itemStr + seperator + item.get("content_title").toString();
            itemStr = itemStr + seperator + item.get("eng_title").toString();
            itemStr = itemStr + seperator + item.get("director").toString();
            itemStr = itemStr + seperator + item.get("year").toString();
            //itemStr = itemStr + seperator + item.get("asset_id").toString();

            itemStr = itemStr + seperator + item.get("meta_when").toString();
            itemStr = itemStr + seperator + item.get("meta_where").toString();
            itemStr = itemStr + seperator + item.get("meta_what").toString();
            itemStr = itemStr + seperator + item.get("meta_who").toString();
            itemStr = itemStr + seperator + item.get("meta_emotion").toString();
            itemStr = itemStr + seperator + item.get("meta_subgenre").toString();
            itemStr = itemStr + seperator + item.get("meta_search").toString();
            itemStr = itemStr + seperator + item.get("meta_charactor").toString();
            itemStr = itemStr + seperator + item.get("meta_reco_target").toString();
            itemStr = itemStr + seperator + item.get("meta_reco_situation").toString();
            itemStr = itemStr + seperator + item.get("meta_award").toString();

            resultStr += itemStr + lineFeed;
            System.out.println("#write "+cnt+"'s item::"+itemStr);
            cnt++;
        }

        String fileNameContent = "METAS_BY_CONTENTS_80_180503.tsv";
        int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultStr, UPLOAD_DIR, fileNameContent, "utf-8");

    }


    @Override
    public void writeCcubeSeriesOutputCSV() throws Exception {
        List<Map<String, Object>> itemList  = testMapper.getCcubeSeriesAll();
        List<Map<String, Object>> resultList = new ArrayList();

        JsonArray Series = null;
        for (Map<String, Object> item : itemList) {
            long longIdx = (long) item.get("idx");
            int idx = (int) longIdx;
            String type = (String) item.get("type");
            String cid = (String) item.get("cid");
            String content_title = (String) item.get("content_title");
            String asset_id = (String) item.get("asset_id");


            String meta_when = "";
            String meta_where = "";
            String meta_what = "";
            String meta_who = "";
            String meta_emotion = "";
            String meta_subgenre = "";
            String meta_search = "";
            String meta_charactor = "";
            String meta_reco_target = "";
            String meta_reco_situation = "";
            String meta_award = "";

            System.out.println("#RES:: idx:"+idx+"/ title:"+content_title);

            Series = ccubeService.getJsonArrayForCcubeOutput(null, type, item);
            if (Series != null && Series.size() > 0) {
                JsonObject jo = (JsonObject) Series.get(0);
                if (jo != null) {
                    meta_when = (jo.get("META_WHEN") != null ? jo.get("META_WHEN").getAsString() : "");
                    item.put("meta_when", meta_when);
                    meta_where = (jo.get("META_WHERE") != null ? jo.get("META_WHERE").getAsString() : "");
                    item.put("meta_where", meta_where);
                    meta_what = (jo.get("META_WHAT") != null ? jo.get("META_WHAT").getAsString() : "");
                    item.put("meta_what", meta_what);
                    meta_who = (jo.get("META_WHO") != null ? jo.get("META_WHO").getAsString() : "");
                    item.put("meta_who", meta_who);
                    meta_emotion = (jo.get("META_EMOTION") != null ? jo.get("META_EMOTION").getAsString() : "");
                    item.put("meta_emotion", meta_emotion);
                    meta_subgenre = (jo.get("META_SUBGENRE") != null ? jo.get("META_SUBGENRE").getAsString() : "");
                    item.put("meta_subgenre", meta_subgenre);
                    meta_search = (jo.get("META_SEARCH") != null ? jo.get("META_SEARCH").getAsString() : "");
                    item.put("meta_search", meta_search);
                    meta_charactor = (jo.get("META_CHARACTER") != null ? jo.get("META_CHARACTER").getAsString() : "");
                    item.put("meta_charactor", meta_charactor);
                    meta_reco_target = (jo.get("META_RECO_TARGET") != null ? jo.get("META_RECO_TARGET").getAsString() : "");
                    item.put("meta_reco_target", meta_reco_target);
                    meta_reco_situation = (jo.get("META_RECO_SITUATION") != null ? jo.get("META_RECO_SITUATION").getAsString() : "");
                    item.put("meta_reco_situation", meta_reco_situation);
                    meta_award = (jo.get("META_AWARD") != null ? jo.get("META_AWARD").getAsString() : "");
                    meta_award = meta_award.replace("현재페이지   1", "").trim(); item.put("meta_award", meta_award);
                    System.out.println("#meta_when:"+meta_when + " / meta_where:"+meta_where + " / meta_award:"+meta_award);
                    resultList.add(item);
                }
            }


        }

        String seperator = "\t";
        String lineFeed = System.getProperty("line.separator");

        String resultStr = "";
        resultStr = "series_id" + seperator + "series_nm"
                + seperator + "otv_series_id"
                + seperator + "meta_when"
                + seperator + "meta_where"
                + seperator + "meta_what"
                + seperator + "meta_who"
                + seperator + "meta_emotion"
                + seperator + "meta_subgenre"
                + seperator + "meta_search"
                + seperator + "meta_charactor"
                + seperator + "meta_reco_target"
                + seperator + "meta_reco_situation"
                + seperator + "meta_award"
                + lineFeed;

        String itemStr = "";
        int cnt = 1;
        for (Map<String, Object> item : resultList) {
            //itemStr = item.get("idx").toString();
            itemStr = item.get("cid").toString();
            itemStr = itemStr + seperator + item.get("content_title").toString();
            itemStr = itemStr + seperator + item.get("asset_id").toString();

            itemStr = itemStr + seperator + item.get("meta_when").toString();
            itemStr = itemStr + seperator + item.get("meta_where").toString();
            itemStr = itemStr + seperator + item.get("meta_what").toString();
            itemStr = itemStr + seperator + item.get("meta_who").toString();
            itemStr = itemStr + seperator + item.get("meta_emotion").toString();
            itemStr = itemStr + seperator + item.get("meta_subgenre").toString();
            itemStr = itemStr + seperator + item.get("meta_search").toString();
            itemStr = itemStr + seperator + item.get("meta_charactor").toString();
            itemStr = itemStr + seperator + item.get("meta_reco_target").toString();
            itemStr = itemStr + seperator + item.get("meta_reco_situation").toString();
            itemStr = itemStr + seperator + item.get("meta_award").toString();

            resultStr += itemStr + lineFeed;
            System.out.println("#write "+cnt+"'s item::"+itemStr);
            cnt++;
        }

        String fileNameContent = "METAS_BY_SERIES_WITH_OTV_180611.tsv";
        int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultStr, UPLOAD_DIR, fileNameContent, "utf-8");

    }


    @Override
    public void writeCcubeSeriesOutputCSV_100() throws Exception {
        List<Map<String, Object>> itemList  = testMapper.getSeriesOrigItemsAll_100();
        List<Map<String, Object>> resultList = new ArrayList();

        JsonArray Series = null;
        for (Map<String, Object> item : itemList) {
            long longIdx = (long) item.get("idx");
            int idx = (int) longIdx;
            String type = (String) item.get("type");
            String cid = (String) item.get("cid");
            String content_title = (String) item.get("content_title");
            //String asset_id = (String) item.get("asset_id");


            String meta_when = "";
            String meta_where = "";
            String meta_what = "";
            String meta_who = "";
            String meta_emotion = "";
            String meta_subgenre = "";
            String meta_search = "";
            String meta_charactor = "";
            String meta_reco_target = "";
            String meta_reco_situation = "";
            String meta_award = "";

            System.out.println("#RES:: idx:"+idx+"/ title:"+content_title);

            Series = ccubeService.getJsonArrayForCcubeOutput(null, type, item);
            if (Series != null && Series.size() > 0) {
                JsonObject jo = (JsonObject) Series.get(0);
                if (jo != null) {
                    meta_when = (jo.get("META_WHEN") != null ? jo.get("META_WHEN").getAsString() : "");
                    item.put("meta_when", meta_when);
                    meta_where = (jo.get("META_WHERE") != null ? jo.get("META_WHERE").getAsString() : "");
                    item.put("meta_where", meta_where);
                    meta_what = (jo.get("META_WHAT") != null ? jo.get("META_WHAT").getAsString() : "");
                    item.put("meta_what", meta_what);
                    meta_who = (jo.get("META_WHO") != null ? jo.get("META_WHO").getAsString() : "");
                    item.put("meta_who", meta_who);
                    meta_emotion = (jo.get("META_EMOTION") != null ? jo.get("META_EMOTION").getAsString() : "");
                    item.put("meta_emotion", meta_emotion);
                    meta_subgenre = (jo.get("META_SUBGENRE") != null ? jo.get("META_SUBGENRE").getAsString() : "");
                    item.put("meta_subgenre", meta_subgenre);
                    meta_search = (jo.get("META_SEARCH") != null ? jo.get("META_SEARCH").getAsString() : "");
                    item.put("meta_search", meta_search);
                    meta_charactor = (jo.get("META_CHARACTER") != null ? jo.get("META_CHARACTER").getAsString() : "");
                    item.put("meta_charactor", meta_charactor);
                    meta_reco_target = (jo.get("META_RECO_TARGET") != null ? jo.get("META_RECO_TARGET").getAsString() : "");
                    item.put("meta_reco_target", meta_reco_target);
                    meta_reco_situation = (jo.get("META_RECO_SITUATION") != null ? jo.get("META_RECO_SITUATION").getAsString() : "");
                    item.put("meta_reco_situation", meta_reco_situation);
                    meta_award = (jo.get("META_AWARD") != null ? jo.get("META_AWARD").getAsString() : "");
                    meta_award = meta_award.replace("현재페이지   1", "").trim(); item.put("meta_award", meta_award);
                    System.out.println("#meta_when:"+meta_when + " / meta_where:"+meta_where + " / meta_award:"+meta_award);
                    resultList.add(item);
                }
            }


        }

        String seperator = "\t";
        String lineFeed = System.getProperty("line.separator");

        String resultStr = "";
        resultStr = "series_id" + seperator + "series_nm"
                + seperator + "otv_series_id"
                + seperator + "meta_when"
                + seperator + "meta_where"
                + seperator + "meta_what"
                + seperator + "meta_who"
                + seperator + "meta_emotion"
                + seperator + "meta_subgenre"
                + seperator + "meta_search"
                + seperator + "meta_charactor"
                + seperator + "meta_reco_target"
                + seperator + "meta_reco_situation"
                + seperator + "meta_award"
                + lineFeed;

        String itemStr = "";
        int cnt = 1;
        for (Map<String, Object> item : resultList) {
            //itemStr = item.get("idx").toString();
            itemStr = item.get("cid").toString();
            itemStr = itemStr + seperator + item.get("content_title").toString();
            //itemStr = itemStr + seperator + item.get("asset_id").toString();

            itemStr = itemStr + seperator + item.get("meta_when").toString();
            itemStr = itemStr + seperator + item.get("meta_where").toString();
            itemStr = itemStr + seperator + item.get("meta_what").toString();
            itemStr = itemStr + seperator + item.get("meta_who").toString();
            itemStr = itemStr + seperator + item.get("meta_emotion").toString();
            itemStr = itemStr + seperator + item.get("meta_subgenre").toString();
            itemStr = itemStr + seperator + item.get("meta_search").toString();
            itemStr = itemStr + seperator + item.get("meta_charactor").toString();
            itemStr = itemStr + seperator + item.get("meta_reco_target").toString();
            itemStr = itemStr + seperator + item.get("meta_reco_situation").toString();
            itemStr = itemStr + seperator + item.get("meta_award").toString();

            resultStr += itemStr + lineFeed;
            System.out.println("#write "+cnt+"'s item::"+itemStr);
            cnt++;
        }

        String fileNameContent = "METAS_BY_SERIES_20_180503.tsv";
        int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultStr, UPLOAD_DIR, fileNameContent, "utf-8");

    }


    @Override
    public void writeCcubeContentsOutputFT() throws Exception {
        List<Map<String, Object>> itemList  = testMapper.getCcubeContentsFT();

        String seperator = "\t";
        String lineFeed = System.getProperty("line.separator");

        String resultStr = "";
        resultStr = "idx"  + seperator + "type" + seperator + "content_id"+ seperator + "master_content_id" + seperator + "content_title"
                + lineFeed;

        String itemStr = "";
        int cnt = 1;
        for (Map<String, Object> item : itemList) {
            itemStr = item.get("idx").toString();
            itemStr = itemStr + seperator + item.get("type").toString();
            itemStr = itemStr + seperator + item.get("cid").toString();
            itemStr = itemStr + seperator + item.get("mcid").toString();
            itemStr = itemStr + seperator + item.get("content_title").toString();

            resultStr += itemStr + lineFeed;
            System.out.println("#write "+cnt+"'s item::"+itemStr);
            cnt++;
        }

        String fileNameContent = "READY_TAGGING_CONTENTS_180514.tsv";
        int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultStr, UPLOAD_DIR, fileNameContent, "euc-kr");

    }


    @Override
    public void writeCcubeSeriesOutputFT() throws Exception {
        List<Map<String, Object>> itemList  = testMapper.getCcubeSeriesFT();

        String seperator = "\t";
        String lineFeed = System.getProperty("line.separator");

        String resultStr = "";
        resultStr = "idx"  + seperator + "type" + seperator + "series_id"+ seperator + "series_nm"
                + lineFeed;

        String itemStr = "";
        int cnt = 1;
        for (Map<String, Object> item : itemList) {
            itemStr = item.get("idx").toString();
            itemStr = itemStr + seperator + item.get("type").toString();
            itemStr = itemStr + seperator + item.get("cid").toString();
            itemStr = itemStr + seperator + item.get("content_title").toString();

            resultStr += itemStr + lineFeed;
            System.out.println("#write "+cnt+"'s item::"+itemStr);
            cnt++;
        }

        String fileNameContent = "FAIL_TAGGING_SERIES_180418.tsv";
        int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultStr, UPLOAD_DIR, fileNameContent, "euc-kr");

    }

    @Override
    public void processRemoveAwardByYear() throws Exception {
        List<Map<String,Object>> itemList = testMapper.getItemsAndAwardAll();
        if (itemList != null && itemList.size() > 0) {
            Map<String,Object> item = null;

            int willRemoveSize = 0;
            for(int i = 0 ; i<itemList.size() ; i++) {
                item = itemList.get(i);

                String syear = "";
                int year = 0;
                String metaStr = "";
                long longIdx = 0;
                int idx = 0;
                if(item != null) {
                    if (item.get("year") != null) syear = item.get("year").toString();
                    if (item.get("award") != null) metaStr = item.get("award").toString();
                    longIdx = (long) item.get("idx");
                    idx = (int) longIdx;

                    if (!"".equals(syear)) {
                        try { year = Integer.parseInt(syear); } catch (Exception e) {}
                        if (year > 0 && !"".equals(metaStr)) {

                            boolean isYearContainsYn = false;
                            if (metaStr.contains(String.valueOf(year))
                                    || metaStr.contains(String.valueOf(year + 1))
                                    || metaStr.contains(String.valueOf(year + 2))) {
                                isYearContainsYn = true;
                            }
                            //System.out.println("## process year compare!  year:"+syear+"  /  metaStr:"+metaStr);

                            ItemsMetas reqIm = null;
                            if (!isYearContainsYn) {
                                //System.out.println("#remove award!");
                                willRemoveSize++;
                                reqIm = new ItemsMetas();
                                reqIm.setIdx(idx);
                                reqIm.setMtype("award");
                                reqIm.setMeta("");
                                reqIm.setRegid("ghkdwo77");
                                int rtim = itemsService.insItemsMetas(reqIm);

                                System.out.println("## remove year compare!  year:"+syear+"  /  metaStr:"+metaStr);

                            }
                        }
                    }
                }
            }

            System.out.println("## AllCnt:"+itemList.size() + "   / willRemoveSize:"+willRemoveSize);
        }
    }

    @Override
    public void writeItemsAndAwardCSV() throws Exception {
        List<Map<String, Object>> itemList  = testMapper.getItemsAndAwardAll2();

        String seperator = "\t";
        String lineFeed = System.getProperty("line.separator");
        String resultStr = "";
        int cnt = 1;

        resultStr = "idx"
                + seperator + "type"
                + seperator + "cid/sid"
                + seperator + "title"
                + seperator + "award"
                + lineFeed;

        for (Map<String, Object> item : itemList) {
            System.out.println("#RES:: idx:"+item.get("idx").toString()+"/ title:"+item.get("title"));

            String itemStr = "";
            itemStr = item.get("idx").toString();
            itemStr = itemStr + seperator + item.get("type");
            itemStr = itemStr + seperator + item.get("cid");
            itemStr = itemStr + seperator + item.get("title");
            itemStr = itemStr + seperator + StringUtil.removeAllTags2(item.get("award").toString());

            resultStr += itemStr + lineFeed;
            System.out.println("#write "+cnt+"'s item::"+itemStr);
            cnt++;
        }

        String fileNameContent = "ITEMS_AND_AWARD_180419_2.tsv";
        int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultStr, UPLOAD_DIR, fileNameContent, "euc-kr");

    }


    @Override
    public void writeDicKeywordsByTypes() throws Exception {
        List<String> types = new ArrayList();
        types.add("CHARACTER");
        types.add("EMOTION");
        types.add("WHAT");
        types.add("WHEN");
        types.add("WHERE");
        types.add("WHO");

        String seperator = "\t";
        String lineFeed = System.getProperty("line.separator");

        for(String type : types) {
            List<Map<String, Object>> itemList = testMapper.getDicKeywordsByType0(type);

            String resultStr = "";
            int cnt = 1;

            resultStr = "keyword"
                    + lineFeed;

            for (Map<String, Object> item : itemList) {
                //System.out.println("#RES:: idx:" + item.get("idx").toString() + "/ title:" + item.get("title"));

                String itemStr = "";
                String tag = item.get("keyword").toString();
                tag = tag.replace(",","");

                if(!"".equals(tag.trim()) && !",".equals(tag) && !".".equals(tag.trim()) && !"'".equals(tag.trim()) && !"\"".equals(tag.trim())
                        ) {
                    itemStr = tag;
                    resultStr += itemStr + lineFeed;
                    System.out.println("#write " + cnt + "'s item::" + itemStr);
                    cnt++;
                }
            }

            String fileNameContent = "DIC_TYPE_"+type+"_180419.tsv";
            int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultStr, UPLOAD_DIR, fileNameContent, "utf-8");
        }
    }

    @Override
    public int writeCcubeOutputToJsonByType(String type) {
        int rt = 0;

        int pageSize = 20;
        Items req = new Items();
        req.setType(type);
        req.setPageSize(pageSize);

        /* get ccube_outupt list , tagcnt < 4 , stat = Y */
        List<Map<String, Object>> reqItems = null;
        int countAll = 0;
        if ("CcubeSeries".equals(type)) {
            countAll = testMapper.cntSeriesOrigItemsAll();
        } else {
            countAll = testMapper.cntContentsOrigItemsAll();
        }

//countAll = 4107;

        JsonObject resultObj = new JsonObject();

        logger.info("#MLLOG:writeCcubeOutput:: type:"+type+" / countAll:"+countAll);
        if(countAll > 0) {
            int pageAll = 0;
            if (countAll == 0) {
                pageAll = 1;
            } else {
                pageAll = countAll / pageSize + 1;
            }
            System.out.println("#pageAll:" + pageAll);

            JsonArray contents = null;

            try {
                for (int pno = 1; pno <= pageAll; pno++) {
                    req.setPageNo(pno);
                    req.setPageSize(pageSize);

                    reqItems = null;
                    if ("CcubeSeries".equals(type)) {
                        reqItems = testMapper.getSeriesOrigItemsAll(req);
                    } else {
                        reqItems = testMapper.getContentsOrigItemsAll(req);
                    }

                    if (reqItems != null) {
                        logger.info("#writeCcubeOutputToJson.getAll: type:" + type + " / pno:" + pno + " / items-size:" + reqItems.size());
                        int oldItemIdx = 0;
                        int cnt = 0;
                        for (Map<String, Object> ins : reqItems) {
                            long longidx = (long) ins.get("itemidx");
                            oldItemIdx = (int) longidx;

                            if (oldItemIdx > 0) {
                                ins.put("idx", oldItemIdx);

                                //System.out.println("#ELOG ins-reqItem:"+ins.toString());

                                contents = ccubeService.getJsonArrayForCcubeOutput(contents, type, ins);

                                cnt++;
                            }
                            //logger.info("#SCHEDULE processCcubeOutputToJson:Copy ccube_output to json ContentsArr:" + contents.toString());
                        }
                    }

                }

                resultObj.addProperty("TOTAL_COUNT", contents.size());

                resultObj.add("CONTENTS", contents);

                logger.info("#SCHEDULE processCcubeOutputToJson:Copy ccube_output to jsonObj:" + resultObj.toString());

                String fileNameContent = (type.startsWith("CcubeSeries") ? "METAS_SERIES_" : "METAS_MOVIE_");
                fileNameContent += DateUtils.getLocalDate("yyyyMMddHH") + ".json";

                int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultObj.toString(), UPLOAD_DIR, fileNameContent, "utf-8");
                logger.info("#SCHEDULE processCcubeOutputToJson file:" + UPLOAD_DIR + fileNameContent + " rt:" + rtFileC);
                //int rtUp = sftpService.uploadToCcube(WORK_DIR, fileNameContent);

                rt = 1;
            } catch (Exception e) {
                rt = -3;
                logger.error("#ERROR:" + e);
                e.printStackTrace();
            }
        }
        return rt;
    }


    @Override
    public int writeCcubeOutputToJsonDevide(String type, int pageLimit) {
        int rt = 0;

        int pageSize = pageLimit;
        Items req = new Items();
        req.setType(type);
        req.setPageSize(pageSize);

        /* get ccube_outupt list , tagcnt < 4 , stat = Y */
        List<Map<String, Object>> reqItems = null;
        int countAll = 0;
        if ("CcubeSeries".equals(type)) {
            countAll = testMapper.cntSeriesOrigItemsAll();
        } else {
            countAll = testMapper.cntContentsOrigItemsAll();
        }

//countAll = 4107;

        JsonObject resultObj = null;

        logger.info("#MLLOG:writeCcubeOutputToJsonDevide:: type:"+type+" / countAll:"+countAll);
        if(countAll > 0) {
            int pageAll = 0;
            if (countAll == 0) {
                pageAll = 1;
            } else {
                pageAll = countAll / pageSize + 1;
            }
            //System.out.println("#pageAll:" + pageAll);

            JsonArray contents = null;

            try {
                for (int pno = 1; pno <= pageAll; pno++) {
                    req.setPageNo(pno);
                    req.setPageSize(pageSize);

                    reqItems = null;
                    if ("CcubeSeries".equals(type)) {
                        reqItems = testMapper.getSeriesOrigItemsAll(req);
                    } else {
                        reqItems = testMapper.getContentsOrigItemsAll(req);
                    }

                    if (reqItems != null) {

                        resultObj = new JsonObject();
                        resultObj.addProperty("TOTAL_COUNT", reqItems.size());
                        contents = new JsonArray();

                        logger.info("#writeCcubeOutputToJsonDevide.getAll: type:" + type + " / pno:" + pno + " / items-size:" + reqItems.size());
                        int oldItemIdx = 0;
                        int cnt = 0;
                        for (Map<String, Object> ins : reqItems) {
                            long longidx = (long) ins.get("itemidx");
                            oldItemIdx = (int) longidx;

                            if (oldItemIdx > 0) {
                                ins.put("idx", oldItemIdx);
                                contents = ccubeService.getJsonArrayForCcubeOutput(contents, type, ins);

                                cnt++;
                            }
                            //logger.info("#SCHEDULE processCcubeOutputToJson:Copy ccube_output to json ContentsArr:" + contents.toString());


                            resultObj.add("CONTENTS", contents);
                            logger.info("#SCHEDULE writeCcubeOutputToJsonDevide:Copy ccube_output to jsonObj:" + resultObj.toString());

                            String fileNameContent = (type.startsWith("CcubeSeries") ? "METAS_SERIES_" : "METAS_MOVIE_");
                            fileNameContent += DateUtils.getLocalDate("yyyyMMddHH") + "_" + pno + ".json";

                            int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultObj.toString(), UPLOAD_DIR, fileNameContent, "utf-8");
                            logger.info("#SCHEDULE writeCcubeOutputToJsonDevide file:" + UPLOAD_DIR + fileNameContent + " rt:" + rtFileC);
                            //int rtUp = sftpService.uploadToCcube(WORK_DIR, fileNameContent);
                        }
                    }

                }

                rt = 1;
            } catch (Exception e) {
                rt = -3;
                logger.error("#ERROR:" + e);
                e.printStackTrace();
            }
        }
        return rt;
    }

    @Override
    public int writeCcubeOutputDayToJsonByType(String type) {
        int rt = 0;

        int pageSize = 20;
        Items req = new Items();
        req.setType(type);
        req.setPageSize(pageSize);

        /* get ccube_outupt list , tagcnt < 4 , stat = Y */
        List<Map<String, Object>> reqItems = null;
        int countAll = 0;
        if ("CcubeSeries".equals(type)) {
            countAll = testMapper.cntSeriesOrigItemsDay();
        } else {
            countAll = testMapper.cntContentsOrigItemsDay();
        }

//countAll = 4107;

        JsonObject resultObj = new JsonObject();
        resultObj.addProperty("TOTAL_COUNT", countAll);

        logger.info("#MLLOG:writeCcubeOutput:: type:"+type+" / countAll:"+countAll);
        if(countAll > 0) {
            int pageAll = 0;
            if (countAll == 0) {
                pageAll = 1;
            } else {
                pageAll = countAll / pageSize + 1;
            }
            System.out.println("#pageAll:" + pageAll);

            JsonArray contents = null;

            try {
                for (int pno = 1; pno <= pageAll; pno++) {
                    req.setPageNo(pno);
                    req.setPageSize(pageSize);

                    reqItems = null;
                    if ("CcubeSeries".equals(type)) {
                        reqItems = testMapper.getSeriesOrigItemsDay(req);
                    } else {
                        reqItems = testMapper.getContentsOrigItemsDay(req);
                    }

                    if (reqItems != null) {
                        logger.info("#writeCcubeOutputToJson.getAll: type:" + type + " / pno:" + pno + " / items-size:" + reqItems.size());
                        int oldItemIdx = 0;
                        int cnt = 0;
                        for (Map<String, Object> ins : reqItems) {

                            /*
                            CcubeKeys reqCk = null;
                            // 중복방지로직, cid , title, director, year 순으로 대조

                            if ("CcubeContent".equals(type)) {
                                reqCk = new CcubeKeys();
                                reqCk.setContent_id(ins.get("content_id").toString());
                                reqCk.setMaster_content_id(ins.get("master_content_id").toString());
                                String title = ins.get("content_title").toString();
                                reqCk.setPurity_title(title);
                                reqCk.setYear((ins.get("year") != null) ? ins.get("year").toString() : "");
                                reqCk.setDirector(ins.get("director") != null ? ins.get("director").toString() : "");
                                reqCk.setKmrb_id(ins.get("kmrb_id") != null ? ins.get("kmrb_id").toString() : "");
                            } else if ("CcubeSeries".equals(type)) {
                                reqCk = new CcubeKeys();
                                reqCk.setSeries_id(ins.get("series_id").toString());
                                String title = ins.get("series_nm").toString();
                                reqCk.setPurity_title(title);
                                reqCk.setYear((ins.get("year") != null) ? ins.get("year").toString() : "");
                                reqCk.setDirector(ins.get("director") != null ? ins.get("director").toString() : "");
                            }

                            oldItemIdx = ccubeService.getCcubeItemIdx(reqCk);
                            */
                            long longidx = (long) ins.get("itemidx");
                            oldItemIdx = (int) longidx;

                            if (oldItemIdx > 0) {
                                // 중복방지 로직에 걸려서 ccube_keys에 등재되지 않은 content_id, series_id 재처리용
                                /*
                                int currItemIdx = ccubeMapper.getCcubeItemIdx(reqCk);
                                if (currItemIdx == 0) {
                                    reqCk.setItemidx(oldItemIdx);

                                    if("CcubeSeries".equals(type)) {
                                        reqCk.setMaster_content_id("0");
                                        reqCk.setContent_id("0");
                                    } else {
                                        reqCk.setSeries_id("0");
                                    }
                                    int rti = ccubeMapper.insCcubeKeys(reqCk);
                                }
                                */
                                ins.put("idx", oldItemIdx);
                                contents = ccubeService.getJsonArrayForCcubeOutput(contents, type, ins);

                                cnt++;
                            }
                            //logger.info("#SCHEDULE processCcubeOutputToJson:Copy ccube_output to json ContentsArr:" + contents.toString());
                        }
                    }

                }

                resultObj.add("CONTENTS", contents);
                logger.info("#SCHEDULE processCcubeOutputToJson:Copy ccube_output to jsonObj:" + resultObj.toString());

                String fileNameContent = (type.startsWith("CcubeSeries") ? "METAS_SERIES_" : "METAS_MOVIE_");
                fileNameContent += DateUtils.getLocalDate("yyyyMMddHH") + ".json";

                int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultObj.toString(), UPLOAD_DIR, fileNameContent, "utf-8");
                logger.info("#SCHEDULE processCcubeOutputToJson file:" + UPLOAD_DIR + fileNameContent + " rt:" + rtFileC);
                if (rtFileC > 0) {
                    int rtUp = sftpService.uploadToCcube(WORK_DIR, fileNameContent);
                }

                rt = 1;
            } catch (Exception e) {
                rt = -3;
                logger.error("#ERROR:" + e);
                e.printStackTrace();
            }
        }
        return rt;
    }

    @Override
    public void processItemsTagsMetasByResultTag() throws Exception {
        Map<String, Object> resultMap = new HashMap();

        List<String> origTypes = new ArrayList<String>();
        origTypes.add("WHEN");
        origTypes.add("WHERE");
        origTypes.add("WHO");
        origTypes.add("WHAT");
        origTypes.add("EMOTION");

        for (String otype : origTypes) {
            ItemsTags req = new ItemsTags();
            req.setMtype("METAS"+otype);
            List<ItemsTags> metaList = itemsTagsMapper.getItemsTagsMetasByMtype(req);

            int metaCount = 0;

            System.out.println("# otype:METAS"+otype +"  size:"+metaList.size());
            //if (metaList != null && metaList.size() > 0)  System.out.println("# otype:META_"+otype +"  get One:"+metaList.get(0).toString());
            if (metaList != null && metaList.size() > 0) {
                for (ItemsTags it : metaList) {
                //for(int i=0; i<10; i++) {
                    //ItemsTags it = metaList.get(0);
                    String meta = (it != null && it.getMeta() != null ? it.getMeta() : "");
                    if (!"".equals(meta)) {
                        JsonArray metaArr = JsonUtil.getJsonArray(meta);
                        JsonArray newArr = new JsonArray();

                        Set<String> newSet = new HashSet();

                        if (metaArr != null && metaArr.size() > 0) {
                            for (JsonElement je : metaArr) {
                                JsonObject jo = (JsonObject) je;
                                System.out.println("#tmp jo:"+jo.toString());

                                if (jo != null && jo.get("word") != null) {
                                    String word1 = jo.get("word").getAsString();
                                    word1 = word1.trim();

                                    if (!"".equals(word1)) {
                                        Set<String> combinedResultTags = dicService.getStringArrayFromWordWithResultTag(word1, otype);

                                        System.out.println("# combinedResultTags :"+combinedResultTags.toString() + "   by word:"+word1);

                                        if (combinedResultTags != null && combinedResultTags.size() > 0) {
                                            for (String ctWord : combinedResultTags) {

                                                boolean isExist = false;
                                                for (String ss : newSet) {
                                                    //System.out.println("# tmp compare ss:"+ss+"  vs  "+ctWord+"    isExist:"+isExist);

                                                    if (ss.equals(ctWord)) {
                                                        isExist = true;
                                                        break;
                                                    }
                                                }
                                                if (!isExist) {
                                                    JsonObject newJo = new JsonObject();
                                                    newJo.addProperty("word", ctWord);
                                                    newJo.addProperty("type", (jo.get("type") != null ? jo.get("type").getAsString() : ""));
                                                    newJo.addProperty("ratio",(jo.get("ratio") != null ? jo.get("ratio").getAsDouble() : 0.0));
                                                    newArr.add(newJo);

                                                    newSet.add(ctWord);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        String newMeta = newArr.toString();

                        System.out.println("# otype:"+otype+"  origMeta:"+meta);
                        System.out.println("# otype:"+otype+"  newMeta:"+newMeta);

                        if(!meta.equals(newMeta)) {
                            it.setMeta_orig(meta);
                            it.setMeta(newMeta);

                            int rti = itemsTagsMapper.insItemsTagsMetas_0503(it);
                        }
                    }

                    metaCount++;
                }
            }
        }

    }


    @Override
    public void writeMetaDicKeywordsByTypes() throws Exception {
        List<String> types = new ArrayList();
        types.add("METASEMOTION");
        types.add("METASWHAT");
        types.add("METASWHEN");
        types.add("METASWHERE");
        types.add("METASWHO");

        String seperator = "\t";
        String lineFeed = System.getProperty("line.separator");

        Map<String, Object> reqMap = null;
        List<Map<String, Object>> itemList = null;
        Set<String> newSet = null;

        Map<String, Object> itemCountByType = new HashMap();

        for(String type : types) {
            reqMap = new HashMap();
            reqMap.put("mtype", type);
            itemList = testMapper.getMetaKeywordsByMtype(reqMap);
            //System.out.println("#orig itemList:"+itemList.toString());
            itemCountByType.put(type, itemList.size());

            if (itemList != null) {
                newSet = new TreeSet();
                for (Map<String,Object> item : itemList) {
                    if(item != null && item.get("meta") != null) {
                        String metaStr = item.get("meta").toString();

                        if (!"".equals(metaStr) && metaStr.contains("[")) {
                            JsonArray metaArr = JsonUtil.getJsonArray(metaStr);

                            if (metaArr != null && metaArr.size() > 0) {

                                for (JsonElement je : metaArr) {
                                    JsonObject jo = (JsonObject) je;
                                    if (jo != null && jo.get("word") != null) {

                                        String word = jo.get("word").getAsString();

                                        if (!"".equals(word)) {
                                            newSet.add(word);
                                        }
                                    }

                                }
                            }
                        }


                    }
                }
            }

            String resultStr = "";
            int cnt = 1;

            resultStr = "keyword"
                    + lineFeed;

            /*
            for (Map<String, Object> item : itemList) {
                System.out.println("#RES:: idx:" + item.get("idx").toString() + "/ title:" + item.get("title"));

                String itemStr = "";
                String tag = item.get("keyword").toString();
                tag = tag.replace(",","");

                if(!"".equals(tag.trim()) && !",".equals(tag) && !".".equals(tag.trim()) && !"'".equals(tag.trim()) && !"\"".equals(tag.trim())
                        ) {
                    itemStr = tag;
                    resultStr += itemStr + lineFeed;
                    System.out.println("#write " + cnt + "'s item::" + itemStr);
                    cnt++;
                }
            }
            */
            for (String word : newSet) {
                resultStr += word + lineFeed;
                System.out.println("#write " + cnt + "'s item::" + word);
                cnt++;
            }

            String fileNameContent = "META_DIC_TYPE_"+type+"_180705.tsv";
            int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultStr, UPLOAD_DIR, fileNameContent, "euc-kr");
        }
        System.out.println("#ALL count by type:"+itemCountByType.toString());
    }


    @Override
    public void checkJsonFileDup() throws Exception {
        //String fileName = "E:\\0608.tar\\0608\\0608\\METAS_MOVIE_2018060814.json";
        String fileName = "D:\\upload\\METAS_MOVIE_2018061414.json";
        //fileName = "E:\\20180614.tar\\20180614\\METAS_MOVIE_2018061415.json";
        fileName = "D:\\upload\\T180618\\METAS_MOVIE_2018061815.json";
        List<CcubeContent> result = new ArrayList();
        int cntAll = 0;
        String line = "";
        String origStr = "";
        List<String> cids = new ArrayList();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(fileName), "utf-8"))) {
            while ((line = reader.readLine()) != null
                //&& cntAll < 10000
                    ){
                origStr += line;
            }

            JsonObject allObj = JsonUtil.getJsonObject(origStr);
            JsonArray allArr = allObj.get("CONTENTS").getAsJsonArray();

            int ccnt = 0;
            for(JsonElement je : allArr) {
                JsonObject jo = (JsonObject) je;

                String cid = jo.get("CONTENT_ID").getAsString();

                cids.add(cid);

                if (cid.equals("10027326330001")) {
                    System.out.println("## "+ccnt+" 'th 10027326330001 data:"+jo.toString());
                }

                ccnt++;
            }

            System.out.println("#allObj:"+ cids.size());

            int lcnt = 0;
            for(String cid : cids) {
                if (cid.equals("10027326330001")) {
                    System.out.println("## 10027326330001 line:" + lcnt);
                }
                lcnt++;
            }

            Map<String, Long> counts =
                    cids.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));




            Set entrySet = counts.entrySet();
            Iterator it = entrySet.iterator();

            while(it.hasNext()){
                Map.Entry me = (Map.Entry)it.next();
                String cid = (String) me.getKey();
                long cnt= (long) me.getValue();
                if (cnt > 1) {
                    System.out.println("#dup cid:"+cid+" / cnt:"+cnt);
                }

            }


            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }



    @Override
    public void checkJsonFileToCsv() throws Exception {
        //String fileName = "E:\\0608.tar\\0608\\0608\\METAS_MOVIE_2018060814.json";
        String fileName = "D:\\upload\\METAS_MOVIE_2018061414.json";
        //fileName = "E:\\20180614.tar\\20180614\\METAS_MOVIE_2018061415.json";
        fileName = "D:\\upload\\T180618\\METAS_MOVIE_2018061815.json";
        List<CcubeContent> result = new ArrayList();
        int cntAll = 0;
        String line = "";
        String origStr = "";
        List<String> cids = new ArrayList();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(fileName), "utf-8"))) {
            while ((line = reader.readLine()) != null
                //&& cntAll < 10000
                    ){
                origStr += line;
            }

            JsonObject allObj = JsonUtil.getJsonObject(origStr);
            JsonArray allArr = allObj.get("CONTENTS").getAsJsonArray();


            String seperator = "\t";
            String lineFeed = System.getProperty("line.separator");

            String resultStr = "";
            resultStr = "content_id" + seperator + "content_title"
                    + lineFeed;

            String itemStr = "";

            int ccnt = 0;
            for(JsonElement je : allArr) {
                JsonObject jo = (JsonObject) je;

                System.out.println("#jo:"+jo.toString());

                String cid = jo.get("CONTENT_ID").getAsString();
                String title = jo.get("CONTENT_TITLE").getAsString();

                if (!"".equals(cid)) {
                    cids.add(cid);
                    itemStr = cid + seperator + title;

                    resultStr += itemStr + lineFeed;
                }

                ccnt++;
            }

            System.out.println("#allObj:"+ cids.size());


            Map<String, Long> counts =
                    cids.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));

            Set entrySet = counts.entrySet();
            Iterator it = entrySet.iterator();

            while(it.hasNext()){
                Map.Entry me = (Map.Entry)it.next();
                String cid = (String) me.getKey();
                long cnt= (long) me.getValue();
                if (cnt > 1) {
                    System.out.println("#dup cid:"+cid+" / cnt:"+cnt);
                }

            }


            String fileNameContent = "LIST_CONTENTS_180618.tsv";
            int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultStr, UPLOAD_DIR, fileNameContent, "utf-8");


            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }



    @Override
    public void writeAllContentsByMcid() throws Exception {

        String seperator = "\t";
        String lineFeed = System.getProperty("line.separator");

        List<Map<String, Object>> itemList = null;

        itemList = testMapper.getAllContentsByMcid();
        System.out.println("#orig itemList.size:"+itemList.size());

        String resultStr = "master_content_id" + seperator + "content_id" + seperator + "title" + seperator + "itemidx" + lineFeed;

        if (itemList != null && itemList.size() > 0) {
            for (Map<String,Object> item : itemList) {
                if(item != null && item.get("master_content_id") != null) {
                    String master_content_id = item.get("master_content_id").toString();
                    String content_id = item.get("content_id").toString();
                    String title = item.get("title").toString();
                    String itemidx = item.get("itemidx").toString();

                    String itemOne = master_content_id + seperator + content_id + seperator + title + seperator + itemidx + lineFeed;
                    if (!"".equals(itemOne)) {
                        resultStr += itemOne;
                    }
                }
            }
        }


        String fileNameContent = "180719__ALL_CONTENTS__by_MCID_CID_ITEMIDX.tsv";
        int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultStr, UPLOAD_DIR, fileNameContent, "utf-8");

    }




    @Override
    public void processGenSubgenre_0725(String type) throws Exception {

        int pageSize = 20;
        Items req = new Items();
        req.setType(type);
        req.setPageSize(pageSize);

        /* get ccube_outupt list , tagcnt < 4 , stat = Y */
        List<Map<String, Object>> reqItems = null;
        int countAll = 0;
        if ("CcubeSeries".equals(type)) {
            countAll = testMapper.cntSeriesOrigItemsAll();
        } else {
            countAll = testMapper.cntContentsOrigItemsAll();
        }

//countAll = 40;

        JsonObject resultObj = new JsonObject();

        logger.info("#MLLOG: type:"+type+" / countAll:"+countAll);
        if(countAll > 0) {
            int pageAll = 0;
            if (countAll == 0) {
                pageAll = 1;
            } else {
                pageAll = countAll / pageSize + 1;
            }
            System.out.println("#pageAll:" + pageAll);

            JsonArray contents = null;

            try {
                for (int pno = 1; pno <= pageAll; pno++) {
                    req.setPageNo(pno);
                    req.setPageSize(pageSize);

                    reqItems = null;
                    if ("CcubeSeries".equals(type)) {
                        reqItems = testMapper.getSeriesOrigItemsAll(req);
                    } else {
                        reqItems = testMapper.getContentsOrigItemsAll(req);
                    }

                    if (reqItems != null) {
                        for (Map<String, Object> nmap : reqItems) {
                            long longidx = (long) nmap.get("itemidx");
                            int idx = (int) longidx;
                            ItemsTags it = new ItemsTags();
                            it.setIdx(idx);
                            it.setStat("S");
                            int maxTagIdx = itemsTagsService.getMaxTagsIdxByItemIdx(it);
                            String tagsArr = itemsTagsService.getItemsTagsMetasStringByItemIdx(it);

                            JsonArray genSubGenreArr = itemsTagsService.getMetaSubgenre(idx, tagsArr);

                            if (genSubGenreArr != null && genSubGenreArr.size() > 0) {
                                it.setTagidx(maxTagIdx);
                                it.setMtype("LIST_SUBGENRE");
                                it.setMeta(genSubGenreArr.toString());
                                it.setRegid("ghkdwo77");

                                int rt0 = itemsTagsMapper.insItemsTagsMetas_0725(it);
                            }
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void writeGenSubgenre_0725(String type) throws Exception {

        int pageSize = 20;
        Items req = new Items();
        req.setType(type);
        req.setPageSize(pageSize);

        /* get ccube_outupt list , tagcnt < 4 , stat = Y */
        List<Map<String, Object>> reqItems = null;
        int countAll = 0;
        if ("CcubeSeries".equals(type)) {
            countAll = testMapper.cntSeriesOrigItemsAll();
        } else {
            countAll = testMapper.cntContentsOrigItemsAll();
        }

//countAll = 40;

        JsonObject resultObj = new JsonObject();

        logger.info("#MLLOG: type:"+type+" / countAll:"+countAll);
        if(countAll > 0) {
            int pageAll = 0;
            if (countAll == 0) {
                pageAll = 1;
            } else {
                pageAll = countAll / pageSize + 1;
            }
            System.out.println("#pageAll:" + pageAll);

            JsonArray contents = null;

            String resultStr = "master_content_id" + seperator + "content_id" + seperator + "title" + seperator + "itemidx"
                    + seperator + "movie_genre" + seperator + "sub_genre" + lineFeed;
            try {
                for (int pno = 1; pno <= pageAll; pno++) {
                    req.setPageNo(pno);
                    req.setPageSize(pageSize);

                    reqItems = null;
                    if ("CcubeSeries".equals(type)) {
                        reqItems = testMapper.getSeriesOrigItemsAll(req);
                    } else {
                        reqItems = testMapper.getContentsOrigItemsAll(req);
                    }

                    if (reqItems != null) {
                        for (Map<String, Object> item : reqItems) {
                            long longidx = (long) item.get("itemidx");
                            int idx = (int) longidx;
                            ItemsTags it = new ItemsTags();
                            it.setIdx(idx);
                            it.setStat("S");
                            it.setMtype("LIST_SUBGENRE");

                            String getSubgenre = itemsTagsService.tmp_getWordArrStringFromItemsTagsMetasIdxAndMtype(it);

                            String master_content_id = item.get("master_content_id").toString();
                            String content_id = item.get("content_id").toString();
                            String title = item.get("content_title").toString();
                            String itemidx = item.get("itemidx").toString();
                            String movieGenre = itemsTagsService.getMovieGenreFromCcubeContents(idx);

                            String itemOne = master_content_id + seperator + content_id + seperator + title + seperator + itemidx
                                    + seperator + movieGenre + seperator + getSubgenre + lineFeed;
                            if (!"".equals(itemOne)) {
                                resultStr += itemOne;
                            }
                        }

                    }
                }


                String fileNameContent = "180725__ALL_CONTENTS__WITH__SUB_GENRE.tsv";
                int rtFileC = FileUtils.writeYyyymmddFileFromStr(resultStr, UPLOAD_DIR, fileNameContent, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
