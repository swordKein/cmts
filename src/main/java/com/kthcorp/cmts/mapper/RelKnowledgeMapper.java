package com.kthcorp.cmts.mapper;

import com.kthcorp.cmts.model.RelKnowledge;

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
}
