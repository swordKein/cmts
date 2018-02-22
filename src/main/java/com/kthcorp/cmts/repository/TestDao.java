package com.kthcorp.cmts.repository;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class TestDao {

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	public String getCurrentDateTime2() {
		return this.sqlSessionTemplate.selectOne("getCurrentDateTime");
	}

}
