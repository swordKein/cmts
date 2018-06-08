package com.kthcorp.cmts.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class JsonUtil {

	public static String getNullAsEmptyString(JsonObject jsonObject) {
		return jsonObject.isJsonNull() ? "" : jsonObject.getAsString();
	}

	public static String getNullAsEmptyString2(JsonElement jsonElement) {
		return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
	}

	public static String convertItemsForEs(String result, String id, String keywords, String tag) {
		result = result + "{ \"index\":{ \"_id\" : \""+id+"\" } } " +
				"\n{ \"keywords\":\""+CommonUtil.removeTex(keywords)+"\"}\n";

		return result;
	}

	public static String convertItemsForEs2(String result, String id, String keywords, String tag) {
		result = result + "{ \"index\":{ \"_id\" : \""+id+"\" } } " +
				"\n{ \"keywords\":\""+CommonUtil.removeTex(keywords)+"\", \"tag\":\""+tag+"\" }\n";

		return result;
	}

	public static String convertItemsEs(String result, String id, String keywords, String tag) {
		result = result +
				"{ \"keywords\":\""+CommonUtil.removeTex(keywords)+"\", \"tag\":\""+tag+"\" }";

		return result;
	}


	public static String convertItemsEs2(String result, String id, String keywords, String topic, String tag) {
		result = result +
				"{ " +
				"\"keywords\":\""+keywords+"\"," +
				"\"tag\":\""+tag+"\"," +
				"\"topic\":\""+topic+"\"" +
				" }";

		return result;
	}

	/* List<Map<String, Object>> 을 JsonArray string 으로 convert */
	public static String convertListMapToJsonArrayString(List<Map<String, Object>> list)
	{
		JsonArray json_arr = new JsonArray();
		for (Map<String, Object> map : list) {
			JsonObject json_obj = new JsonObject();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				try {
					json_obj.addProperty(key, value.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			json_arr.add(json_obj);
		}

		return json_arr.toString();
	}

	/* String 을 JsonArray로 convert */
	public static JsonArray convertStringToJsonArray(String reqList) {
		return (JsonArray) new Gson().toJsonTree(reqList,
				new TypeToken<String>() {
				}.getType());
	}

	/* String 을 JsonArray로 convert with delemiter */
	public static JsonArray convertStringToJsonArrayWithDelemeter(String reqList, String delemeter) {
		JsonArray result = null;
		if (!"".equals(reqList.trim())) {
			result = new JsonArray();

			String newArr[] = reqList.split(delemeter);
			for (String ns : newArr) {
				String ns2 = ns.trim();
				if (!"".equals(ns2)) {
					result.add(ns2);
				}
			}
		}
		return result;
	}


	/* String 을 JsonArray로 convert with delemiter */
	public static JsonArray convertStringToJsonArrayForObjWithDelemeter(String reqList, String delemeter) {
		JsonArray result = null;
		if (!"".equals(reqList.trim())) {
			result = new JsonArray();

			String newArr[] = reqList.split(delemeter);
			for (String ns : newArr) {
				String ns2 = ns.trim();
				if (!"".equals(ns2)) {
					JsonObject newObj = new JsonObject();
					newObj.addProperty("type","");
					newObj.addProperty("ratio", 0.0);
					newObj.addProperty("word", ns2);
					result.add(newObj);
				}
			}
		}
		return result;
	}

	/* String 을 JsonObject로 convert */
	public static JsonObject convertStringToJsonObject(String reqStr) {
		com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
		JsonObject jsonObj = (JsonObject) parser.parse(reqStr);
		return jsonObj;
	}

	/* List<String> 을 JsonArray로 convert */
	public static JsonArray convertListToJsonArray(List<String> reqList) {
		return (JsonArray) new Gson().toJsonTree(reqList,
				new TypeToken<List<String>>() {
				}.getType());
	}

	/* List<Integer> 을 JsonArray로 convert */
	public static JsonArray convertIntegerListToJsonArray(List<Integer> reqList) {
		return (JsonArray) new Gson().toJsonTree(reqList,
				new TypeToken<List<Integer>>() {
				}.getType());
	}

	/* JsonArray를 List<String>  으로 convert */
	public static List<String> convertJsonArrayToList(JsonArray reqArray) {
		return new Gson().fromJson(reqArray, new TypeToken<List<String>>(){}.getType());
	}


	public static List<String> convertJsonArrayToListByLabel(JsonArray reqArray, String label) {
		List<String> result = new ArrayList();
		if (reqArray != null && reqArray.size() > 0) {
			for (JsonElement je : reqArray) {
				JsonObject jo = (JsonObject) je;
				String s = jo.get(label).getAsString();
				s = s.trim();
				result.add(s);
			}
		}
		return result;
	}

	/* JsonObject를 Map<String, Object> 으로 convert */
	public static Map<String, Object> convertJsonObjectToMap(JsonObject object) {
		Map<String, Object> map = new HashMap<String, Object>();

		Iterator<String> keysItr = object.keySet().iterator();
		while(keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);

			if(value instanceof JsonArray) {
				value = toList((JsonArray) value);
			}

			else if(value instanceof JsonObject) {
				value = convertJsonObjectToMap((JsonObject) value);
			}
			map.put(key, value);
		}
		return map;
	}

	/* JsonObject를 List<String> 으로 convert */
	// _ 로 시작하면 2개품사 조합 중 첫째 단어가 공백인 경우 - 제외
	public static List<String> convertJsonObjectToArrayList(JsonObject object, int limit) {
		List<String> result = new ArrayList();

		Iterator<String> keysItr = object.keySet().iterator();
		int cnt = 0;
		while(keysItr.hasNext() && cnt <= limit) {
			String key = keysItr.next().trim();
			key = key.trim();
			key = key.replace("_","");

			Object value = object.get(key);

			if (!"".equals(key)
					&& key.length() > 1) {
				result.add(key);
				cnt++;
			}
		}
		return result;
	}

	public static List<Object> toList(JsonArray array) {
		List<Object> list = new ArrayList<Object>();
		for(int i = 0; i < array.size(); i++) {
			Object value = array.get(i);
			if(value instanceof JsonArray) {
				value = toList((JsonArray) value);
			}

			else if(value instanceof JsonObject) {
				value = convertJsonObjectToMap((JsonObject) value);
			}
			list.add(value);
		}
		return list;
	}

	/* 주어진 HashSet<String> 내의 모든 String을  공백으로 분리된 1개의 String으로 리턴
    *
    */
	public static String convertSetMapToString(HashMap<Integer,Object> reqSet, String tagStr, String sharp) {
		String result = "";
		System.out.println("### reqSet :: "+reqSet);

		if (reqSet != null) {
			String tmp = "";
			for (Integer thisValue : reqSet.keySet()) {
				System.out.println("### thisValue :: "+thisValue);
				for (String str : (HashSet<String>) reqSet.get(thisValue)) {
					System.out.println("### (HashSet<String>) reqSet.get(thisValue) :: "+(HashSet<String>) reqSet.get(thisValue));
					if(!"".equals(str)) {
						if ("".equals(tmp)) { result = sharp + str.trim(); }
						else { result = result.trim() + tagStr + sharp + str.trim(); }
						tmp = str;
					}
				}
			}
		}
		result = result.trim();
		return result;
	}


	public String getGgonSchema(String data) throws Exception {
		String result = "";
		Gson gson = new GsonBuilder().create();
		//result = gson.toJson(data.getMerchant().getStakeholder_list());
		result = gson.toJson(data);

		return result;
	}

	public static JsonObject getJsonObject(String data) throws Exception {
		JsonObject result = null;
		Gson gson = new Gson();
		result = gson.fromJson(data, JsonObject.class);

		return result;
	}


	public static JsonArray getJsonArray(String data) throws Exception {
		JsonArray result = null;
		Gson gson = new Gson();
		result = gson.fromJson(data, JsonArray.class);

		//System.out.println("#getJsonArray:result::"+result.toString());
		return result;
	}

	public static JsonNode getJsonSchema (String jsonData) throws Exception {
		ObjectMapper mapper=new ObjectMapper();

		JsonNode rootNode = mapper.readTree(jsonData);
		JsonParser jp=mapper.getJsonFactory().createJsonParser(jsonData);

		rootNode=mapper.readTree(jp);

		ObjectNode onode = (ObjectNode) rootNode;

		//String wv = mapper.writeValue(rootNode);

		//JsonSchema jsc = mapper.generateJsonSchema(SimpleBean.class);

		return rootNode;
	}

	public static ArrayList<String> getListFromJsonArrayOnlyValue(JsonArray jsonArray) {
		ArrayList<String> result = new ArrayList<String>();
		if(jsonArray != null && jsonArray.size() > 0) {
			for (JsonElement je : jsonArray) {
				JsonObject jo = (JsonObject) je;
				Set<Map.Entry<String, JsonElement>> entries = jo.entrySet();
				for (Map.Entry<String, JsonElement> entry : entries) {
					//System.out.println(entry.getValue());
					String s = entry.getValue().toString();
					s = s.replace("\"", "");
					result.add(s);
				}
			}
		}
		return result;
	}


	public static ArrayList<String> getListFromJsonArrayOnlyValueString(JsonArray jsonArray) {
		ArrayList<String> result = new ArrayList<String>();
		if(jsonArray != null && jsonArray.size() > 0) {
			for (int i=0; i<jsonArray.size(); i++) {
				String s = jsonArray.get(i).getAsString();
				result.add(s);
			}
		}
		return result;
	}

	public static List<String> getListFromJsonArrayByTag(JsonArray jsonArray, String tag, int limit) throws Exception {
		List<String> result = new ArrayList<String>();
		if(jsonArray != null && jsonArray.size() > 0) {
			int lineCnt = 0;
			for (JsonElement je : jsonArray) {
				JsonObject jo = (JsonObject) je;
				if (jo != null && jo.get(tag) != null) {
					String word = jo.get(tag).getAsString();
					word = word.trim();
					if (lineCnt < limit) {
						result.add(word);
					} else {
						break;
					}
				}
				lineCnt++;
			}
		}
		return result;
	}

	public static String getStringFromJsonArraysValues(JsonArray jsonArray) {
		String result = "";
		ArrayList<String> resArr = getListFromJsonArrayOnlyValue(jsonArray);
		for(String s : resArr) {
			if(!"".equals(result)) { result += " " + s; }
			else { result = s; }
		}
		return result;
	}

	public static String getStringFromJsonArraysValues2(JsonArray jsonArray) {
		String result = "";
		for(JsonElement je : jsonArray) {
			JsonObject jo = null;
			String jobj_plot = "";
			try {
				jo = (JsonObject) je;
				jobj_plot = (jo != null && jo.get("plot") != null) ? jo.get("plot").getAsString() : "";
			} catch (Exception e) {}

			if (!"".equals(jobj_plot)) {
				result = jobj_plot;
				break;
			} else {
				result = result + " " + je.getAsString().trim();
			}
		}
		return result;
	}

	public static String getStringFromJsonArraysValuesWithDivider(JsonArray jsonArray, String divider) {
		String result = "";
		if(jsonArray != null && jsonArray.size() > 0) {
			ArrayList<String> resArr = getListFromJsonArrayOnlyValueString(jsonArray);
			for(String s : resArr) {
				if(!"".equals(result)) { result += divider + " " + s; }
				else { result = s; }
			}
		}
		return result;
	}

	public static JsonArray getListFromJsonArrayInObjectValuesOld(JsonObject jsonObj) {
		JsonArray result = new JsonArray();

		if(jsonObj != null && jsonObj.get("result") != null) {
			JsonArray jes = (JsonArray) jsonObj.get("result");
			for(JsonElement je : jes) {
				JsonObject jo = (JsonObject) je;
				result.add(jo);
			}
		}
		return result;
	}


	public static JsonArray getListFromJsonArrayInObjectValues(JsonObject jsonObj) {
		JsonArray result = new JsonArray();

		if(jsonObj != null && jsonObj.get("contents") != null) {
			JsonArray jes = (JsonArray) jsonObj.get("contents");
			for(JsonElement je : jes) {
				JsonObject jo = (JsonObject) je;
				result.add(jo);
			}
		}
		return result;
	}


	public static JsonArray getListFromJsonArrayOld(JsonArray jes) {
		JsonArray result = new JsonArray();
		if(jes != null && jes.size() > 0) {

			for(JsonElement je : jes) {
				JsonObject jo = (JsonObject) je;
				JsonArray js = (JsonArray) jo.get("result");
				for(JsonElement e : js) {
					JsonObject o = (JsonObject) e;
					result.add(o);
				}
			}
		}
		return result;
	}

	public static JsonArray getListFromJsonArray(JsonArray jes) {
		JsonArray result = new JsonArray();
		if(jes != null && jes.size() > 0) {

			for(JsonElement je : jes) {
				JsonObject jo = (JsonObject) je;
				JsonArray js = (JsonArray) jo.get("contents");
				for(JsonElement e : js) {
					JsonObject o = (JsonObject) e;
					result.add(o);
				}
			}
		}
		return result;
	}

	public static Map<String, Object> convertJsonToMap(String jsonStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		// convert JSON string to Map
		ObjectMapper mapper = new ObjectMapper();
		try {
			map = mapper.readValue(jsonStr, new TypeReference<Map<String, Object>>(){});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	public static JsonArray convertStringToJsonArrayByComma(String req) {
		JsonArray result = new JsonArray();

		if(!"".equals(req.trim())) {
			if(req.contains(",")) {
				String reqs[] = req.trim().split(",");
				for(String s : reqs) {
					result.add(s.trim());
				}
			} else {
				result.add(req.trim());
			}
		}
		return result;
	}

	public static List<String> getTagValueArrayFromJsonObject(JsonObject reqObj, String tag) {
		List<String> result = new ArrayList();

		if(reqObj != null) {

		}
		return result;
	}

	public static JsonArray getSortedJsonArray(JsonArray origArr, String key_field, String sort_field, String extra_field1, int limit) {
		JsonArray resultArr = null;
		if (origArr != null && origArr.size() > 0) {
			resultArr = new JsonArray();

			Map<String, Double> keyListMap = new HashMap();
			Map<String, String> extraListMap1 = new HashMap();
			for(JsonElement je : origArr) {
				JsonObject jo = (JsonObject) je;
				keyListMap.put(jo.get(key_field).getAsString(), jo.get(sort_field).getAsDouble());
				extraListMap1.put(jo.get(key_field).getAsString(), jo.get(extra_field1).getAsString());
			}
			//Map<String, Double> sortedKeyListMap = MapUtil.getSortedDescMapForDouble(keyListMap);
			List<String> sortedKeyList = MapUtil.getSortedDescStringArrayForDouble(keyListMap, limit);
			//System.out.println("#MLOG.sortedKeyList for LIST_NOT_MAPPED:"+sortedKeyList);
			for(String ks : sortedKeyList) {
				JsonObject newItem = new JsonObject();
				newItem.addProperty(key_field, ks);
				newItem.addProperty(sort_field, keyListMap.get(ks).toString());
				newItem.addProperty(extra_field1, extraListMap1.get(ks).toString());
				resultArr.add(newItem);
			}
			//System.out.println("#MLOG.sortedJsonArray for LIST_NOT_MAPPED:"+resultArr.toString());
		}

		return resultArr;
	}

	public static JsonObject setEmptyMetas(JsonObject reqObj, List<String> origTypes) {
		// 빠진 type은 공백이라도 채워준다
		if(reqObj != null) {
			for(String type : origTypes) {
				if(reqObj.get(type) == null) {
					reqObj.addProperty(type, "");
					//System.out.println("#reqObj add new JsonArray:"+type);
				} else {
					//System.out.println("#reqObj:"+reqObj.get(type).toString());
				}
			}
		}

		return reqObj;
	}

	public static JsonObject setEmptyMetasAndReplaceComma(JsonObject reqObj, List<String> origTypes) {
		// 빠진 type은 공백이라도 채워준다
		if(reqObj != null) {
			for(String type : origTypes) {
				if(reqObj.get(type) == null) {
					reqObj.addProperty(type, "");
					//System.out.println("#reqObj add new JsonArray:"+type);
				} else {
					String orig = reqObj.get(type).getAsString();
					orig = StringUtil.removeAllTags(orig);
					orig = orig.replace(", ","|");
					orig = orig.replace(",","|");
					reqObj.remove(type);
					reqObj.addProperty(type, orig);
					//System.out.println("#reqObj:"+reqObj.get(type).toString());
				}
			}
		}

		return reqObj;
	}

	public static JsonObject getObjFromMatchedGenre(String matchGenre) {
		JsonObject result = null;
		if (!"".equals(matchGenre.trim())) {
			result = new JsonObject();

			result.addProperty("type", "");
			result.addProperty("ratio", 0.0);
			result.addProperty("word", matchGenre);
		}
		return result;
	}
}