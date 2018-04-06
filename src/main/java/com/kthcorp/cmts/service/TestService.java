package com.kthcorp.cmts.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kthcorp.cmts.mapper.*;
import com.kthcorp.cmts.model.*;
import com.kthcorp.cmts.util.*;
import org.apache.avro.generic.GenericData;
import org.apache.directory.shared.ldap.codec.ResponseCarryingException;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.InetAddress;
import java.util.*;

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
    private ItemsService itemsService;
    @Autowired
    private ItemsTagsService itemsTagsService;

    @Value("${spring.static.resource.location}")
    private String UPLOAD_DIR;

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
        String fileName = "C:\\Users\\wodus77\\Documents\\KTH_META\\03.구현\\서브장르__추출_____\\dic_subgenre_genres.txt";
        return loadDicSubgenreGenres(fileName);
    }

    @Override
    public Map<String,Object> loadDicSubgenreKeywords() throws Exception {
        String fileName = "C:\\Users\\wodus77\\Documents\\KTH_META\\03.구현\\서브장르__추출_____\\dic_subgenre_keywords.txt";
        return loadDicSubgenreKeywords(fileName);
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
            newSub.setMtype("mixgenre");
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
    public void putBulkDataToEsIndex(String idxName, Map<String,Object> reqMap) throws Exception {
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("14.63.170.72"), 9300));

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
                if (cntAll > -1) {
                    if (!"".equals(line.trim())) {
                        String lines[] = line.trim().split(seperator);

                        for(int j=0; j < lines.length; j++) {
                            System.out.print("#j:"+j+"::"+lines[j]+" | ");
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


    @Override
    public void processMixedSubgenre() throws Exception {
        List<Map<String, Object>> itemList = testMapper.getItemsForSubgenre();
        System.out.println("#itemsList.size:"+itemList.size());

        for(Map<String, Object> nmap : itemList) {
            //for(int i=0; i<10; i++) {
            //Map<String, Object>   nmap = itemList.get(i);
            System.out.println("#req::"+nmap.toString());

            String reqStr = "";
            if(nmap.get("genre") != null) reqStr = nmap.get("genre").toString();
            if(nmap.get("kt_rating") != null) reqStr = reqStr + " " + nmap.get("kt_rating").toString();

            List<String> result = dicService.getMixedGenreArrayFromGenre(reqStr, "mixgenre");
            String toMeta = result.toString();
            toMeta = CommonUtil.removeNationStr(toMeta);

            if(!"".equals(toMeta)) {
                ItemsMetas newMeta = new ItemsMetas();
                long longIdx = (Long) nmap.get("idx");
                int intIdx = (int) longIdx;
                newMeta.setIdx(intIdx);
                newMeta.setMtype("subgenre1");
                newMeta.setMeta(toMeta);
                System.out.println("#save itemsMetas:" + newMeta.toString());
                int rtItm = itemsService.insItemsMetas(newMeta);
            }
        }
    }

    private String getMetasStringFromJsonObject(JsonObject resultObj, List<String> origTypes) {
        String result = "";

        List<String> resultArr = new ArrayList();

        if (resultObj != null && origTypes != null) {
            for(String type : origTypes) {
                String typeStr = type.replace("METAS","");
                if (resultObj.get(type) != null) {
                    JsonArray metaArr = (JsonArray) resultObj.get(type);
                    //System.out.println("#metaArr:"+metaArr.toString());
                    if(metaArr != null && metaArr.size() > 0) {
                        for(JsonElement je : metaArr) {
                            JsonObject jo = (JsonObject) je;
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
        origTypes.add("METASCHARACTER");

        for(Map<String, Object> nmap : itemList) {
            //for(int i=0; i<10; i++) {
            //Map<String, Object>   nmap = itemList.get(i);
            System.out.println("#req::"+nmap.toString());

            long itemIdx0 = (long) nmap.get("idx");
            int itemIdx = (int) itemIdx0;
            JsonObject resultObj = itemsTagsService.getItemsMetasByIdx(itemIdx, origTypes, "S");

            //System.out.println("#resultObj:"+resultObj.toString());
            //System.out.println("#resultSet:"+getMetasStringFromJsonObject(resultObj, origTypes));
            String reqStr = getMetasStringFromJsonObject(resultObj, origTypes);
            System.out.println("#requestEs for reqStr:"+reqStr);
            JsonObject resultEs = RestUtil.getSearchedEsData("idx_subgenre", "keywords"
                    , reqStr);

            //System.out.println("#resultEs:"+resultEs.toString());
            JsonObject hits = RestUtil.getEsTopWords(resultEs);
            //System.out.println("#resultEs.words top2::"+hits.toString());
            String subGenreWord1 = "";
            String subGenreWord2 = "";
            String subGenreWords = "";

            if (hits != null && hits.get("words") != null) {
                JsonArray words = hits.get("words").getAsJsonArray();
                int cnt = 0;
                for (JsonElement je : words) {
                    JsonObject jo = (JsonObject) je;
                    String word = "";
                    word = (jo.get("word") != null) ? jo.get("word").getAsString() : "";
                    if (cnt == 0) {
                        //subGenreWord1 = word;
                        double score = (jo.get("score") != null) ? jo.get("score").getAsDouble() : 0.0;
                        if (score > 3.0) subGenreWord1 = word;
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
            if(!"".equals(subGenreWord1)) {
                ItemsMetas newMeta = new ItemsMetas();
                long longIdx = (Long) nmap.get("idx");
                int intIdx = (int) longIdx;
                newMeta.setIdx(intIdx);
                newMeta.setMtype("subgenreword1");
                newMeta.setMeta(subGenreWord1);
                System.out.println("#save itemsMetas:" + newMeta.toString());
                int rtItm1 = itemsService.insItemsMetas(newMeta);

                newMeta.setMtype("subgenreword2");
                newMeta.setMeta(subGenreWord2);
                System.out.println("#save itemsMetas:" + newMeta.toString());
                int rtItm2 = itemsService.insItemsMetas(newMeta);

                newMeta.setMtype("subgenrewords");
                newMeta.setMeta(subGenreWords);
                System.out.println("#save itemsMetas:" + newMeta.toString());
                int rtItm3 = itemsService.insItemsMetas(newMeta);

            }

        }
    }
}
