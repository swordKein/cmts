package com.kthcorp.cmts.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.kthcorp.cmts.model.MovieCine21;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONObject;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtils {

    public static void checkDirAndCreate(String dir) {
        File theDir = new File(dir);
        if (!theDir.isDirectory()) {
            try{
                theDir.mkdir();
                System.out.println("#FileUtils.checkDirAndCreate :: mkdir:"+dir);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    //save file
    public static void saveUploadedFiles(List<MultipartFile> files, String path) throws IOException {

        for (MultipartFile file : files) {

            if (file.isEmpty()) {
                continue; //next pls
            }

            byte[] bytes = file.getBytes();
            Path path2 = Paths.get(path + file.getOriginalFilename());
            Files.write(path2, bytes);

        }

    }

    public static byte[] readUploadedFiles(List<MultipartFile> files) throws IOException {
        byte[] bytes = null;

        if (files != null && files.size() > 0) {
            //for (MultipartFile file : files) {
            //if (file.isEmpty()) {
            //	continue; //next pls
            //}

            bytes = files.get(0).getBytes();
            //while ((count = fis.read(b)) != -1) {
            //	for (int i = 0; i < count; i++) {
            //		System.out.print((char) b[i]);
            //	}
            //}
        }
        //}
        return bytes;
    }

    public static String convertByteToString(byte[] req, String charset, int size) {
        String result = "";
        try {
            if (req != null) {
                result = new String(req, charset);
                if (result.length() > size) result = result.substring(0,size);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static MultipartFile convertFileToMultipart(String pathStr, String filename) {
        //Path path = Paths.get("/path/to/the/file.txt");
        //String name = "file.txt";
        //String originalFileName = "file.txt";
        Path path = Paths.get(pathStr + filename);
        String originalFileName = filename;
        String contentType = "text/plain";

        byte[] content = null;
        MultipartFile result = null;

        try {
            content = Files.readAllBytes(path);
            result = new MockMultipartFile(filename,
                    originalFileName, contentType, content);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static File multipartToFile(MultipartFile multipart) throws IllegalStateException, IOException {
        File convFile = new File( multipart.getOriginalFilename());
        multipart.transferTo(convFile);
        return convFile;
    }

    public static String getTestIdxData() throws Exception {

        // The name of the file to open.
        String fileName = "D:\\TADATA";

        // This will reference one line at a time
        String line = null;

        String jsonSource = "";
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            int lineCnt = 0;
            List<Map<String, Object>> resultMapArr = new ArrayList<Map<String, Object>>();

            while((line = bufferedReader.readLine()) != null && lineCnt < 6) {
                //System.out.println(line);
                String[] lineOrig = line.split("\\|");
                String id = lineOrig[0];
                String keywords_orig = lineOrig[1];
                keywords_orig = CommonUtil.removeTex(keywords_orig);
                String keywords = keywords_orig.trim();

                String tag = SeunjeonUtil.getSimpleWords2Str(keywords_orig, null);
                tag = tag.trim();

                String topic = SeunjeonUtil.getSimpleWords2StrForMatchClass2(keywords_orig, null);
                topic = topic.trim();

                //System.out.println("#id:"+id);
                //System.out.println("#keywords:"+keywords.substring(0,40));
                //System.out.println("#date:"+date);
                //Map<String, Object> newMap = new HashMap<String, Object>();
                //newMap.put("id", id);
                //newMap.put("keywords", CommonUtil.removeTex(keywords));

                //resultMapArr.add(newMap);

                lineCnt++;
                jsonSource = JsonUtil.convertItemsEs2(jsonSource, id, keywords, topic, tag);
            }

            // Always close files.
            bufferedReader.close();

            //jsonSource = JsonUtil.convertListMapToJsonArrayString(resultMapArr);
            System.out.println("#jsonSource:"+jsonSource);

        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }

        return jsonSource;
    }


    public static List<MovieCine21> getCine21Data() throws Exception {

        // The name of the file to open.
        String filename = "D:\\movie_cine21.json";

        // This will reference one line at a time
        String line = null;
        String jsonSource = "";
        int cnt  = 0;

        List<MovieCine21> movieList = new ArrayList<MovieCine21>();
        try {
            // FileReader reads text files in the default encoding.
            //FileReader fileReader =                    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            //BufferedReader bufferedReader =                    new BufferedReader(fileReader);
            Type REVIEW_TYPE = new TypeToken<List<JsonObject>>() {
            }.getType();
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(filename));
            List<JsonObject> data = gson.fromJson(reader, REVIEW_TYPE); // contains the whole reviews list

            for (JsonElement je : data) {
                JsonObject jo = (JsonObject) je;
                //System.out.println(cnt + " 'th data:"+jo.toString());

                MovieCine21 newMovie = new MovieCine21();
                if (jo != null && jo.get("movieId") != null) {
                    try {
                        newMovie.setMovieId(jo.get("movieId").getAsInt());
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
                if (jo != null && jo.get("fimsCd") != null) {
                    try {
                        newMovie.setFimsCd(JsonUtil.getNullAsEmptyString2(jo.get("fimsCd")));
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
                if (jo != null && jo.get("movieNm") != null) newMovie.setMovieNm(JsonUtil.getNullAsEmptyString2(jo.get("movieNm")));
                if (jo != null && jo.get("movieNmOg") != null) newMovie.setMovieNmOg(JsonUtil.getNullAsEmptyString2(jo.get("movieNmOg")));
                if (jo != null && jo.get("runTime") != null) newMovie.setRunTime(JsonUtil.getNullAsEmptyString2(jo.get("runTime")));
                if (jo != null && jo.get("prdtYear") != null) newMovie.setPrdtYear(JsonUtil.getNullAsEmptyString2(jo.get("prdtYear")));
                if (jo != null && jo.get("openDt") != null) {
                    //System.out.println("#openDt:"+jo.get("openDt").getAsString());
                    String openDt = JsonUtil.getNullAsEmptyString2(jo.get("openDt"));
                    if ((openDt.startsWith("19") || openDt.startsWith("20") && openDt.length() > 5)) {
                        openDt += " 00:00:00.000";
                        try {
                            newMovie.setOpenDt(Timestamp.valueOf(openDt));
                        } catch (Exception e) {
                            System.out.println("#error openDt:"+openDt);
                            e.printStackTrace();
                        }
                    }

                }
                if (jo != null && jo.get("synop") != null) newMovie.setSynop(jo.get("synop").getAsString());
                if (jo != null && jo.get("updateDt") != null) newMovie.setUpdateDt(Timestamp.valueOf(jo.get("updateDt").getAsString()));

                if (jo != null && jo.get("watchGrade") != null) {
                    JsonObject watArr = (JsonObject) jo.get("watchGrade");
                    String resWat = "";

                    //System.out.println("#watArr:"+watArr.toString());

                    if (watArr != null && watArr.get("watchGradeNm") != null) {
                        try {
                            resWat = JsonUtil.getNullAsEmptyString2(watArr.get("watchGradeNm"));
                        } catch (Exception e) {
                            //System.out.println("#error watchGradeNm:"+watArr.get("watchGradeNm").getAsString());
                            e.printStackTrace();
                        }
                    }
                    newMovie.setWatchGrade(resWat);
                }
                if (jo != null && jo.get("countries") != null) {
                    JsonArray conArr = (JsonArray) jo.get("countries");
                    String resCon = "";
                    for(JsonElement jje : conArr) {
                        JsonObject con = (JsonObject) jje;
                        if(!"".equals(resCon)) {
                            resCon += ", " + JsonUtil.getNullAsEmptyString2(con.get("countryNm"));
                        } else {
                            resCon = JsonUtil.getNullAsEmptyString2(con.get("countryNm"));
                        }
                    }
                    newMovie.setCountries(resCon);
                }
                if (jo != null && jo.get("genres") != null) {
                    JsonArray genArr = (JsonArray) jo.get("genres");
                    String resGen = "";
                    for(JsonElement jge : genArr) {
                        JsonObject gen = (JsonObject) jge;
                        if(!"".equals(resGen)) {
                            resGen += ", " + JsonUtil.getNullAsEmptyString2(gen.get("genreNm"));
                        } else {
                            resGen = JsonUtil.getNullAsEmptyString2(gen.get("genreNm"));
                        }
                    }
                    newMovie.setGenres(resGen);
                }
                if (jo != null && jo.get("directors") != null) {
                    JsonArray dirArr = (JsonArray) jo.get("directors");
                    //System.out.println("#dirArr:"+dirArr.toString());
                    //String resDir = JsonUtil.getStringFromJsonArraysValues(dirArr);
                    String resDir = "";
                    if (dirArr != null) {
                        resDir = JsonUtil.getStringFromJsonArraysValuesWithDivider(dirArr, ",");
                    }
                    newMovie.setDirectors(resDir);
                }
                if (jo != null && jo.get("actors") != null) {
                    JsonArray actArr = (JsonArray) jo.get("actors");
                    String resAct = "";
                    for(JsonElement jae : actArr) {
                        JsonObject act = (JsonObject) jae;
                        if (!"".equals(resAct)) {
                            resAct += ", " + JsonUtil.getNullAsEmptyString2(act.get("actorNm"))
                                    + " (" + JsonUtil.getNullAsEmptyString2(act.get("actorRole")) + ")";
                        } else {
                            resAct = JsonUtil.getNullAsEmptyString2(act.get("actorNm"))
                                    + " (" + JsonUtil.getNullAsEmptyString2(act.get("actorRole")) + ")";
                        }
                    }
                    newMovie.setActors(resAct);
                }
                if (jo != null && jo.get("companies") != null) {
                    JsonArray compArr = (JsonArray) jo.get("companies");
                    //System.out.println("#companies:"+compArr);
                    String resComp = "";
                    for(JsonElement jce : compArr) {
                        JsonObject comp = (JsonObject) jce;
                        if (!"".equals(resComp)) {
                            resComp += ", " + JsonUtil.getNullAsEmptyString2(comp.get("companyNm"))
                                    + " (" + JsonUtil.getNullAsEmptyString2(comp.get("companyRole")) + ")";
                        } else {
                            resComp = JsonUtil.getNullAsEmptyString2(comp.get("companyNm"))
                                    + " (" + JsonUtil.getNullAsEmptyString2(comp.get("companyRole")) + ")";
                        }
                    }
                    newMovie.setCompanies(resComp);
                }
                if (jo != null && jo.get("awards") != null) {
                    JsonArray dirAwa = (JsonArray) jo.get("awards");
                    String resAwa = JsonUtil.getStringFromJsonArraysValuesWithDivider(dirAwa, ",");
                    newMovie.setAwards(resAwa);
                }
                if (jo != null && jo.get("staffs") != null) {
                    JsonArray sttArr = (JsonArray) jo.get("staffs");
                    //System.out.println("#staffs:"+sttArr);
                    String resStt = "";
                    for(JsonElement jse : sttArr) {
                        JsonObject stt = (JsonObject) jse;
                        if (!"".equals(resStt)) {
                            resStt += ", " + JsonUtil.getNullAsEmptyString2(stt.get("staffNm"))
                                    + " (" + JsonUtil.getNullAsEmptyString2(stt.get("staffRole")) + ")";
                        } else {
                            resStt = JsonUtil.getNullAsEmptyString2(stt.get("staffNm"))
                                    + " (" + JsonUtil.getNullAsEmptyString2(stt.get("staffRole")) + ")";
                        }
                    }
                    newMovie.setStaffs(resStt);
                }
                if (jo != null && jo.get("posterList") != null) {
                    JsonArray dirPos = (JsonArray) jo.get("posterList");
                    String resPos = JsonUtil.getStringFromJsonArraysValuesWithDivider(dirPos,",");
                    newMovie.setPosterList(resPos);
                }
                if (jo != null && jo.get("stillCutList") != null) {
                    JsonArray dirSc = (JsonArray) jo.get("stillCutList");
                    String resSc = JsonUtil.getStringFromJsonArraysValuesWithDivider(dirSc,",");
                    newMovie.setStillCutList(resSc);
                }
                if (jo != null && jo.get("vodList") != null) {
                    JsonArray dirVod = (JsonArray) jo.get("vodList");
                    //System.out.println("#vodList:"+dirVod.toString());

                    String resVod = "";
                    for(JsonElement jve : dirVod) {
                        JsonObject vod  = (JsonObject) jve;
                        resVod += " " + JsonUtil.getNullAsEmptyString2(vod.get("vodKind"))
                                + " ("+JsonUtil.getNullAsEmptyString2(vod.get("vodUrl"))+")";
                    }

                    newMovie.setVodList(resVod);
                }
                if (jo != null && jo.get("cineKeywords") != null) {
                    newMovie.setCineKeywords(JsonUtil.getNullAsEmptyString2(jo.get("cineKeywords")).replace("^",", "));
                }


                //System.out.println(cnt+" 'th data:"+je.toString());

                //System.out.println(cnt+" 'th data:"+newMovie.toString());

                //if (cnt > 5) break;
                movieList.add(newMovie);
                cnt++;
            }
            System.out.println("#cnt:"+cnt);
            //int lineCnt = 0;
            //List<Map<String, Object>> resultMapArr = new ArrayList<Map<String, Object>>();

            //while((line = bufferedReader.readLine()) != null && lineCnt < 6) {
            //System.out.println(line);
            //}

            // Always close files.
            //bufferedReader.close();

            //jsonSource = JsonUtil.convertListMapToJsonArrayString(resultMapArr);
            //System.out.println("#jsonSource:"+jsonSource);

        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            filename + "'");
        }

        return movieList;
    }


    public static List<Map<String,Object>> getTestIdxDataMap() throws Exception {

        // The name of the file to open.
        String fileName = "D:\\TADATA";

        // This will reference one line at a time
        String line = null;

        String jsonSource = "";

        List<Map<String, Object>> resMap = new ArrayList<Map<String, Object>>();

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            int lineCnt = 0;
            List<Map<String, Object>> resultMapArr = new ArrayList<Map<String, Object>>();

            while((line = bufferedReader.readLine()) != null && lineCnt < 60000) {
                //System.out.println(line);
                String[] lineOrig = line.split("\\|");
                String id = lineOrig[0];
                String keywords_orig = lineOrig[1];
                keywords_orig = CommonUtil.removeTex(keywords_orig);
                String keywords = keywords_orig.trim();

                String tag = SeunjeonUtil.getSimpleWords2Str(keywords_orig, null);
                tag = tag.trim();

                String topic = SeunjeonUtil.getSimpleWords2StrForMatchClass2(keywords_orig, null);
                topic = topic.trim();

                //System.out.println("#id:"+id);
                //System.out.println("#keywords:"+keywords.substring(0,40));
                //System.out.println("#date:"+date);
                //Map<String, Object> newMap = new HashMap<String, Object>();
                //newMap.put("id", id);
                //newMap.put("keywords", CommonUtil.removeTex(keywords));

                //resultMapArr.add(newMap);

                lineCnt++;
                //jsonSource = JsonUtil.convertItemsForEs2(jsonSource, id, keywords, tag);
                jsonSource = JsonUtil.convertItemsEs2("", id, keywords, topic, tag);
                Map<String, Object> newItem = new HashMap<String, Object>();
                newItem.put("id", id);
                newItem.put("jsonSource", jsonSource);
                resMap.add(newItem);


                if (lineCnt % 3000 == 0) System.out.println("#file writing process:"+lineCnt);
            }

            // Always close files.
            bufferedReader.close();

            //jsonSource = JsonUtil.convertListMapToJsonArrayString(resultMapArr);
            //System.out.println("#jsonSource:"+jsonSource);

        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }

        return resMap;
    }




    public static void transTestIdxData() throws Exception {

        // The name of the file to open.
        String fileName = "D:\\TADATA";
        String outFileName = "E:\\testdata.json";

        // This will reference one line at a time
        String line = null;

        String jsonSource = "";
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            int lineCnt = 0;
            List<Map<String, Object>> resultMapArr = new ArrayList<Map<String, Object>>();

            while((line = bufferedReader.readLine()) != null && lineCnt < 60000) {
                //System.out.println(line);
                String[] lineOrig = line.split("\\|");
                String id = lineOrig[0];
                String keywords_orig = lineOrig[1];
                keywords_orig = CommonUtil.removeTex(keywords_orig);
                String keywords = keywords_orig.trim();

                String tag = SeunjeonUtil.getSimpleWords2Str(keywords_orig, null);
                tag = tag.trim();

                String topic = SeunjeonUtil.getSimpleWords2StrForMatchClass2(keywords_orig, null);
                topic = topic.trim();

                //System.out.println("#id:"+id);
                //System.out.println("#keywords:"+keywords.substring(0,40));
                //System.out.println("#date:"+date);
                //Map<String, Object> newMap = new HashMap<String, Object>();
                //newMap.put("id", id);
                //newMap.put("keywords", CommonUtil.removeTex(keywords));

                //resultMapArr.add(newMap);

                lineCnt++;

                if (lineCnt % 3000 == 0) System.out.println("#file writing process:"+lineCnt);

                jsonSource = JsonUtil.convertItemsForEs2(jsonSource, id, keywords, tag);
            }

            System.out.println("#file writing end:"+lineCnt);


            //FileWriter fw = new FileWriter(outFileName);
            PrintWriter bw = new PrintWriter(new FileWriter( outFileName ));

            bw.println(jsonSource);
            bw.close();

            // Always close files.
            bufferedReader.close();

            //jsonSource = JsonUtil.convertListMapToJsonArrayString(resultMapArr);
            //System.out.println("#jsonSource:"+jsonSource);

        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }

        //return jsonSource;
    }

    public static String getGenFileName(String fileName, String addStr) {
        //String fileName = "test_csv.txt";
        String[] fileNames = fileName.split("\\.");
        String fileNm = fileNames[0];
        String addNm = DateUtils.getLocalDateTime();
        String toFileNm = fileNm+"_" + addStr + "_" + addNm;
        String extNm = "." + fileNames[fileNames.length-1];
        toFileNm += extNm;

        return toFileNm;
    }

    public static long getLineCount(File file) throws IOException {
        try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(file), 1024)) {

            byte[] c = new byte[1024];
            boolean empty = true,
                    lastEmpty = false;
            long count = 0;
            int read;
            while ((read = is.read(c)) != -1) {
                for (int i = 0; i < read; i++) {
                    if (c[i] == '\n') {
                        count++;
                        lastEmpty = true;
                    } else if (lastEmpty) {
                        lastEmpty = false;
                    }
                }
                empty = false;
            }

            if (!empty) {
                if (count == 0) {
                    count = 1;
                } else if (!lastEmpty) {
                    count++;
                }
            }

            return count;
        }
    }

    public static long getLineCountFromFis(FileInputStream fis) throws IOException {
        try (BufferedInputStream is = new BufferedInputStream(fis, 1024)) {

            byte[] c = new byte[1024];
            boolean empty = true,
                    lastEmpty = false;
            long count = 0;
            int read;
            while ((read = is.read(c)) != -1) {
                for (int i = 0; i < read; i++) {
                    if (c[i] == '\n') {
                        count++;
                        lastEmpty = true;
                    } else if (lastEmpty) {
                        lastEmpty = false;
                    }
                }
                empty = false;
            }

            if (!empty) {
                if (count == 0) {
                    count = 1;
                } else if (!lastEmpty) {
                    count++;
                }
            }

            return count;
        }
    }



    public static ArrayList<ArrayList<String>> getFileSeperatedDataToArray(String fileName, String seperator, int limit) throws Exception {

        if (limit == 0) limit = 60000;
        // The name of the file to open.
        //String fileName = "D:\\TADATA";

        // This will reference one line at a time
        String line = null;

        ArrayList<ArrayList<String>> result = new ArrayList();

        try {
            // FileReader reads text files in the default encoding.
            FileInputStream fis = new FileInputStream(fileName);
            InputStreamReader isr = new InputStreamReader(fis,"euc_kr");
            BufferedReader bufferedReader = new BufferedReader(isr);

            int lineCnt = 0;
            while((line = bufferedReader.readLine()) != null && lineCnt < limit) {
                //System.out.println(line);
                String[] lineOrig = line.split(seperator);
                ArrayList<String> newItem = new ArrayList();
                for (String s : lineOrig) {
                    newItem.add(s);
                }
                result.add(newItem);

                if (lineCnt % 3000 == 0) System.out.println("#file writing process:"+lineCnt);
                lineCnt++;
            }

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }

        return result;
    }
}
