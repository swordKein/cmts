package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.RelKnowledge;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface RelKnowledgeMapper {

	//int delRelKnowledgesByType(RelKnowledge relKnowledge);
	int delRelKnowledgesCook(RelKnowledge relKnowledge);
	int delRelKnowledgesCurr(RelKnowledge relKnowledge);
	int delRelKnowledgesDocu(RelKnowledge relKnowledge);
	int delRelKnowledgesHeal(RelKnowledge relKnowledge);
	int delRelKnowledgesHist(RelKnowledge relKnowledge);
	int delRelKnowledgesTour(RelKnowledge relKnowledge);
	void addRelKnowledgesCook(RelKnowledge relKnowledge);
	void addRelKnowledgesCurr(RelKnowledge relKnowledge);
	void addRelKnowledgesDocu(RelKnowledge relKnowledge);
	void addRelKnowledgesHeal(RelKnowledge relKnowledge);
	void addRelKnowledgesHist(RelKnowledge relKnowledge);
	void addRelKnowledgesTour(RelKnowledge relKnowledge);
	List<RelKnowledge> getRelKnowledgeListCook(RelKnowledge relKnowledge);
	List<RelKnowledge> getRelKnowledgeListCurr(RelKnowledge relKnowledge);
	List<RelKnowledge> getRelKnowledgeListDocu(RelKnowledge relKnowledge);
	List<RelKnowledge> getRelKnowledgeListHeal(RelKnowledge relKnowledge);
	List<RelKnowledge> getRelKnowledgeListHist(RelKnowledge relKnowledge);
	List<RelKnowledge> getRelKnowledgeListTour(RelKnowledge relKnowledge);
	
	//int importRelKnowledgesByType(RelKnowledge relKnowledge);
	int importRelKnowledgesCook(RelKnowledge relKnowledge);
	int importRelKnowledgesCurr(RelKnowledge relKnowledge);
	int importRelKnowledgesDocu(RelKnowledge relKnowledge);
	int importRelKnowledgesHeal(RelKnowledge relKnowledge);
	int importRelKnowledgesHist(RelKnowledge relKnowledge);
	int importRelKnowledgesTour(RelKnowledge relKnowledge);

	List<Map<String, Object>> getVodCook(Map<String, Object> reqMap);
	List<Map<String, Object>> getVodCurr(Map<String, Object> reqMap);
	List<Map<String, Object>> getVodDocu(Map<String, Object> reqMap);
	List<Map<String, Object>> getVodHeal(Map<String, Object> reqMap);
	List<Map<String, Object>> getVodHist(Map<String, Object> reqMap);
	List<Map<String, Object>> getVodTour(Map<String, Object> reqMap);

}
