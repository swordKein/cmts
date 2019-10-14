package com.kthcorp.cmts.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;

@Alias("RelKnowledge")
public class RelKnowledge extends Paging {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    private String items;

	private Integer idx;
	private String regweek;
	private String stat;
	private String program;
	private String title;
	private String part;
	private String assetid;
	private String type;
	
	private String relKnowledgeType;
	
	
	private String cook_title_a;
	private String cook_title_b;
	private String cook_parts;
	private String cook_parts_main;
	private String cook_parts_sub;
	private String cook_type_a;
	private String cook_type_b;
	private String cook_chef_a;
	private String cook_chef_b;
	private String cook_kor_area_a;
	private String cook_kor_area_b;
	private String cook_oth_area_a;
	private String cook_oth_area_b;
	private String cook_place_name;
	private String cook_place_kor_area_a;
	private String cook_place_kor_area_b;
	private String cook_place_oth_area_a;
	private String cook_place_oth_area_b;
	private String cook_good_season;
	private String cook_good_day;
	private String cook_purpose;
	private String cook_flavor;
	private String cook_cooker;
	private String cook_shoot_place;
	private String cook_level_a;
	private String cook_level_b;
	private String cook_level_c;
	private String cook_time;
	private String cook_nutrient;
	private String cook_ea;
	private String cook_etc_a;
	private String cook_etc_b;

	private String curr_subject;
	private String curr_person;
	private String curr_incident;
	private String curr_product;
	private String curr_process;
	private String curr_type;
	private String curr_mc;
	private String curr_actor;
	private String curr_keyword;
	private String cur_etc;

	private String docu_subject;
	private String docu_person;
	private String docu_incident;
	private String docu_area;
	private String docu_mc;
	private String docu_actor;
	private String docu_keyword;
	private String docu_etc;

	private String heal_subject;
	private String heal_part;
	private String heal_symtoms;
	private String heal_cause_a;
	private String heal_cause_b;
	private String heal_cause_c;
	private String heal_diagnose;
	private String heal_treat_type;
	private String heal_prevention;
	private String heal_drug;
	private String heal_treat_name;
	private String heal_hospital;
	private String heal_doctor;
	private String heal_talker;
	private String heal_actor;
	private String heal_expert;
	private String heal_public;
	private String heal_course;
	private String heal_activity;
	private String heal_ingredient;
	private String heal_food_a;
	private String heal_food_b;
	private String heal_activity_part;
	private String heal_activity_name;
	private String heal_season;
	private String heal_target;
	private String heal_keyword;
	private String heal_etc;

	private String hist_subject;
	private String hist_area_a;
	private String hist_area_b;
	private String hist_year_a;
	private String hist_year_b;
	private String hist_person;
	private String hist_feat;
	private String hist_incident;
	private String hist_thing;
	private String hist_area_c;
	private String hist_actor;
	private String hist_etc;
	
	private String tour_theme;
	private String tour_area_a;
	private String tour_area_b;
	private String tour_area_c;
	private String tour_area_d;
	private String tour_area_e;
	private String tour_natural;
	private String tour_nation;
	private String tour_person;
	private String tour_festival;
	private String tour_type_a;
	private String tour_type_b;
	private String tour_type_c;
	private String tour_season;
	private String tour_human;
	private String tour_tourist;
	private String tour_mc_yn;
	private String tour_mc;
	private String tour_food;
	private String tour_type_d;
	private String tour_festival_name;
	private String tour_trans;
	private String tour_peaple;
	private String tour_weather;
	private String tour_music;
	private String tour_period;
	private String tour_companion;
	private String tour_etc_a;
	private String tour_etc_b;
	
	
	
	public String getItems() {
		return items;
	}
	public void setItems(String items) {
		this.items = items;
	}
	public Integer getIdx() {
		return idx;
	}
	public void setIdx(Integer idx) {
		this.idx = idx;
	}
	public String getRegweek() {
		return regweek;
	}
	public void setRegweek(String regweek) {
		this.regweek = regweek;
	}
	public String getStat() {
		return stat;
	}
	public void setStat(String stat) {
		this.stat = stat;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPart() {
		return part;
	}
	public void setPart(String part) {
		this.part = part;
	}
	public String getAssetid() {
		return assetid;
	}
	public void setAssetid(String assetid) {
		this.assetid = assetid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRelKnowledgeType() {
		return relKnowledgeType;
	}
	public void setRelKnowledgeType(String relKnowledgeType) {
		this.relKnowledgeType = relKnowledgeType;
	}
	public String getCook_title_a() {
		return cook_title_a;
	}
	public void setCook_title_a(String cook_title_a) {
		this.cook_title_a = cook_title_a;
	}
	public String getCook_title_b() {
		return cook_title_b;
	}
	public void setCook_title_b(String cook_title_b) {
		this.cook_title_b = cook_title_b;
	}
	public String getCook_parts() {
		return cook_parts;
	}
	public void setCook_parts(String cook_parts) {
		this.cook_parts = cook_parts;
	}
	public String getCook_parts_main() {
		return cook_parts_main;
	}
	public void setCook_parts_main(String cook_parts_main) {
		this.cook_parts_main = cook_parts_main;
	}
	public String getCook_parts_sub() {
		return cook_parts_sub;
	}
	public void setCook_parts_sub(String cook_parts_sub) {
		this.cook_parts_sub = cook_parts_sub;
	}
	public String getCook_type_a() {
		return cook_type_a;
	}
	public void setCook_type_a(String cook_type_a) {
		this.cook_type_a = cook_type_a;
	}
	public String getCook_type_b() {
		return cook_type_b;
	}
	public void setCook_type_b(String cook_type_b) {
		this.cook_type_b = cook_type_b;
	}
	public String getCook_chef_a() {
		return cook_chef_a;
	}
	public void setCook_chef_a(String cook_chef_a) {
		this.cook_chef_a = cook_chef_a;
	}
	public String getCook_chef_b() {
		return cook_chef_b;
	}
	public void setCook_chef_b(String cook_chef_b) {
		this.cook_chef_b = cook_chef_b;
	}
	public String getCook_kor_area_a() {
		return cook_kor_area_a;
	}
	public void setCook_kor_area_a(String cook_kor_area_a) {
		this.cook_kor_area_a = cook_kor_area_a;
	}
	public String getCook_kor_area_b() {
		return cook_kor_area_b;
	}
	public void setCook_kor_area_b(String cook_kor_area_b) {
		this.cook_kor_area_b = cook_kor_area_b;
	}
	public String getCook_oth_area_a() {
		return cook_oth_area_a;
	}
	public void setCook_oth_area_a(String cook_oth_area_a) {
		this.cook_oth_area_a = cook_oth_area_a;
	}
	public String getCook_oth_area_b() {
		return cook_oth_area_b;
	}
	public void setCook_oth_area_b(String cook_oth_area_b) {
		this.cook_oth_area_b = cook_oth_area_b;
	}
	public String getCook_place_name() {
		return cook_place_name;
	}
	public void setCook_place_name(String cook_place_name) {
		this.cook_place_name = cook_place_name;
	}
	public String getCook_place_kor_area_a() {
		return cook_place_kor_area_a;
	}
	public void setCook_place_kor_area_a(String cook_place_kor_area_a) {
		this.cook_place_kor_area_a = cook_place_kor_area_a;
	}
	public String getCook_place_kor_area_b() {
		return cook_place_kor_area_b;
	}
	public void setCook_place_kor_area_b(String cook_place_kor_area_b) {
		this.cook_place_kor_area_b = cook_place_kor_area_b;
	}
	public String getCook_place_oth_area_a() {
		return cook_place_oth_area_a;
	}
	public void setCook_place_oth_area_a(String cook_place_oth_area_a) {
		this.cook_place_oth_area_a = cook_place_oth_area_a;
	}
	public String getCook_place_oth_area_b() {
		return cook_place_oth_area_b;
	}
	public void setCook_place_oth_area_b(String cook_place_oth_area_b) {
		this.cook_place_oth_area_b = cook_place_oth_area_b;
	}
	public String getCook_good_season() {
		return cook_good_season;
	}
	public void setCook_good_season(String cook_good_season) {
		this.cook_good_season = cook_good_season;
	}
	public String getCook_good_day() {
		return cook_good_day;
	}
	public void setCook_good_day(String cook_good_day) {
		this.cook_good_day = cook_good_day;
	}
	public String getCook_purpose() {
		return cook_purpose;
	}
	public void setCook_purpose(String cook_purpose) {
		this.cook_purpose = cook_purpose;
	}
	public String getCook_flavor() {
		return cook_flavor;
	}
	public void setCook_flavor(String cook_flavor) {
		this.cook_flavor = cook_flavor;
	}
	public String getCook_cooker() {
		return cook_cooker;
	}
	public void setCook_cooker(String cook_cooker) {
		this.cook_cooker = cook_cooker;
	}
	public String getCook_shoot_place() {
		return cook_shoot_place;
	}
	public void setCook_shoot_place(String cook_shoot_place) {
		this.cook_shoot_place = cook_shoot_place;
	}
	public String getCook_level_a() {
		return cook_level_a;
	}
	public void setCook_level_a(String cook_level_a) {
		this.cook_level_a = cook_level_a;
	}
	public String getCook_level_b() {
		return cook_level_b;
	}
	public void setCook_level_b(String cook_level_b) {
		this.cook_level_b = cook_level_b;
	}
	public String getCook_level_c() {
		return cook_level_c;
	}
	public void setCook_level_c(String cook_level_c) {
		this.cook_level_c = cook_level_c;
	}
	public String getCook_time() {
		return cook_time;
	}
	public void setCook_time(String cook_time) {
		this.cook_time = cook_time;
	}
	public String getCook_nutrient() {
		return cook_nutrient;
	}
	public void setCook_nutrient(String cook_nutrient) {
		this.cook_nutrient = cook_nutrient;
	}
	public String getCook_ea() {
		return cook_ea;
	}
	public void setCook_ea(String cook_ea) {
		this.cook_ea = cook_ea;
	}
	public String getCook_etc_a() {
		return cook_etc_a;
	}
	public void setCook_etc_a(String cook_etc_a) {
		this.cook_etc_a = cook_etc_a;
	}
	public String getCook_etc_b() {
		return cook_etc_b;
	}
	public void setCook_etc_b(String cook_etc_b) {
		this.cook_etc_b = cook_etc_b;
	}
	public String getCurr_subject() {
		return curr_subject;
	}
	public void setCurr_subject(String curr_subject) {
		this.curr_subject = curr_subject;
	}
	public String getCurr_person() {
		return curr_person;
	}
	public void setCurr_person(String curr_person) {
		this.curr_person = curr_person;
	}
	public String getCurr_incident() {
		return curr_incident;
	}
	public void setCurr_incident(String curr_incident) {
		this.curr_incident = curr_incident;
	}
	public String getCurr_product() {
		return curr_product;
	}
	public void setCurr_product(String curr_product) {
		this.curr_product = curr_product;
	}
	public String getCurr_process() {
		return curr_process;
	}
	public void setCurr_process(String curr_process) {
		this.curr_process = curr_process;
	}
	public String getCurr_type() {
		return curr_type;
	}
	public void setCurr_type(String curr_type) {
		this.curr_type = curr_type;
	}
	public String getCurr_mc() {
		return curr_mc;
	}
	public void setCurr_mc(String curr_mc) {
		this.curr_mc = curr_mc;
	}
	public String getCurr_actor() {
		return curr_actor;
	}
	public void setCurr_actor(String curr_actor) {
		this.curr_actor = curr_actor;
	}
	public String getCurr_keyword() {
		return curr_keyword;
	}
	public void setCurr_keyword(String curr_keyword) {
		this.curr_keyword = curr_keyword;
	}
	public String getCur_etc() {
		return cur_etc;
	}
	public void setCur_etc(String cur_etc) {
		this.cur_etc = cur_etc;
	}
	public String getDocu_subject() {
		return docu_subject;
	}
	public void setDocu_subject(String docu_subject) {
		this.docu_subject = docu_subject;
	}
	public String getDocu_person() {
		return docu_person;
	}
	public void setDocu_person(String docu_person) {
		this.docu_person = docu_person;
	}
	public String getDocu_incident() {
		return docu_incident;
	}
	public void setDocu_incident(String docu_incident) {
		this.docu_incident = docu_incident;
	}
	public String getDocu_area() {
		return docu_area;
	}
	public void setDocu_area(String docu_area) {
		this.docu_area = docu_area;
	}
	public String getDocu_mc() {
		return docu_mc;
	}
	public void setDocu_mc(String docu_mc) {
		this.docu_mc = docu_mc;
	}
	public String getDocu_actor() {
		return docu_actor;
	}
	public void setDocu_actor(String docu_actor) {
		this.docu_actor = docu_actor;
	}
	public String getDocu_keyword() {
		return docu_keyword;
	}
	public void setDocu_keyword(String docu_keyword) {
		this.docu_keyword = docu_keyword;
	}
	public String getDocu_etc() {
		return docu_etc;
	}
	public void setDocu_etc(String docu_etc) {
		this.docu_etc = docu_etc;
	}
	public String getHeal_subject() {
		return heal_subject;
	}
	public void setHeal_subject(String heal_subject) {
		this.heal_subject = heal_subject;
	}
	public String getHeal_part() {
		return heal_part;
	}
	public void setHeal_part(String heal_part) {
		this.heal_part = heal_part;
	}
	public String getHeal_symtoms() {
		return heal_symtoms;
	}
	public void setHeal_symtoms(String heal_symtoms) {
		this.heal_symtoms = heal_symtoms;
	}
	public String getHeal_cause_a() {
		return heal_cause_a;
	}
	public void setHeal_cause_a(String heal_cause_a) {
		this.heal_cause_a = heal_cause_a;
	}
	public String getHeal_cause_b() {
		return heal_cause_b;
	}
	public void setHeal_cause_b(String heal_cause_b) {
		this.heal_cause_b = heal_cause_b;
	}
	public String getHeal_cause_c() {
		return heal_cause_c;
	}
	public void setHeal_cause_c(String heal_cause_c) {
		this.heal_cause_c = heal_cause_c;
	}
	public String getHeal_diagnose() {
		return heal_diagnose;
	}
	public void setHeal_diagnose(String heal_diagnose) {
		this.heal_diagnose = heal_diagnose;
	}
	public String getHeal_treat_type() {
		return heal_treat_type;
	}
	public void setHeal_treat_type(String heal_treat_type) {
		this.heal_treat_type = heal_treat_type;
	}
	public String getHeal_prevention() {
		return heal_prevention;
	}
	public void setHeal_prevention(String heal_prevention) {
		this.heal_prevention = heal_prevention;
	}
	public String getHeal_drug() {
		return heal_drug;
	}
	public void setHeal_drug(String heal_drug) {
		this.heal_drug = heal_drug;
	}
	public String getHeal_treat_name() {
		return heal_treat_name;
	}
	public void setHeal_treat_name(String heal_treat_name) {
		this.heal_treat_name = heal_treat_name;
	}
	public String getHeal_hospital() {
		return heal_hospital;
	}
	public void setHeal_hospital(String heal_hospital) {
		this.heal_hospital = heal_hospital;
	}
	public String getHeal_doctor() {
		return heal_doctor;
	}
	public void setHeal_doctor(String heal_doctor) {
		this.heal_doctor = heal_doctor;
	}
	public String getHeal_talker() {
		return heal_talker;
	}
	public void setHeal_talker(String heal_talker) {
		this.heal_talker = heal_talker;
	}
	public String getHeal_actor() {
		return heal_actor;
	}
	public void setHeal_actor(String heal_actor) {
		this.heal_actor = heal_actor;
	}
	public String getHeal_expert() {
		return heal_expert;
	}
	public void setHeal_expert(String heal_expert) {
		this.heal_expert = heal_expert;
	}
	public String getHeal_public() {
		return heal_public;
	}
	public void setHeal_public(String heal_public) {
		this.heal_public = heal_public;
	}
	public String getHeal_course() {
		return heal_course;
	}
	public void setHeal_course(String heal_course) {
		this.heal_course = heal_course;
	}
	public String getHeal_activity() {
		return heal_activity;
	}
	public void setHeal_activity(String heal_activity) {
		this.heal_activity = heal_activity;
	}
	public String getHeal_ingredient() {
		return heal_ingredient;
	}
	public void setHeal_ingredient(String heal_ingredient) {
		this.heal_ingredient = heal_ingredient;
	}
	public String getHeal_food_a() {
		return heal_food_a;
	}
	public void setHeal_food_a(String heal_food_a) {
		this.heal_food_a = heal_food_a;
	}
	public String getHeal_food_b() {
		return heal_food_b;
	}
	public void setHeal_food_b(String heal_food_b) {
		this.heal_food_b = heal_food_b;
	}
	public String getHeal_activity_part() {
		return heal_activity_part;
	}
	public void setHeal_activity_part(String heal_activity_part) {
		this.heal_activity_part = heal_activity_part;
	}
	public String getHeal_activity_name() {
		return heal_activity_name;
	}
	public void setHeal_activity_name(String heal_activity_name) {
		this.heal_activity_name = heal_activity_name;
	}
	public String getHeal_season() {
		return heal_season;
	}
	public void setHeal_season(String heal_season) {
		this.heal_season = heal_season;
	}
	public String getHeal_target() {
		return heal_target;
	}
	public void setHeal_target(String heal_target) {
		this.heal_target = heal_target;
	}
	public String getHeal_keyword() {
		return heal_keyword;
	}
	public void setHeal_keyword(String heal_keyword) {
		this.heal_keyword = heal_keyword;
	}
	public String getHeal_etc() {
		return heal_etc;
	}
	public void setHeal_etc(String heal_etc) {
		this.heal_etc = heal_etc;
	}
	public String getHist_subject() {
		return hist_subject;
	}
	public void setHist_subject(String hist_subject) {
		this.hist_subject = hist_subject;
	}
	public String getHist_area_a() {
		return hist_area_a;
	}
	public void setHist_area_a(String hist_area_a) {
		this.hist_area_a = hist_area_a;
	}
	public String getHist_area_b() {
		return hist_area_b;
	}
	public void setHist_area_b(String hist_area_b) {
		this.hist_area_b = hist_area_b;
	}
	public String getHist_year_a() {
		return hist_year_a;
	}
	public void setHist_year_a(String hist_year_a) {
		this.hist_year_a = hist_year_a;
	}
	public String getHist_year_b() {
		return hist_year_b;
	}
	public void setHist_year_b(String hist_year_b) {
		this.hist_year_b = hist_year_b;
	}
	public String getHist_person() {
		return hist_person;
	}
	public void setHist_person(String hist_person) {
		this.hist_person = hist_person;
	}
	public String getHist_feat() {
		return hist_feat;
	}
	public void setHist_feat(String hist_feat) {
		this.hist_feat = hist_feat;
	}
	public String getHist_incident() {
		return hist_incident;
	}
	public void setHist_incident(String hist_incident) {
		this.hist_incident = hist_incident;
	}
	public String getHist_thing() {
		return hist_thing;
	}
	public void setHist_thing(String hist_thing) {
		this.hist_thing = hist_thing;
	}
	public String getHist_area_c() {
		return hist_area_c;
	}
	public void setHist_area_c(String hist_area_c) {
		this.hist_area_c = hist_area_c;
	}
	public String getHist_actor() {
		return hist_actor;
	}
	public void setHist_actor(String hist_actor) {
		this.hist_actor = hist_actor;
	}
	public String getHist_etc() {
		return hist_etc;
	}
	public void setHist_etc(String hist_etc) {
		this.hist_etc = hist_etc;
	}
	public String getTour_theme() {
		return tour_theme;
	}
	public void setTour_theme(String tour_theme) {
		this.tour_theme = tour_theme;
	}
	public String getTour_area_a() {
		return tour_area_a;
	}
	public void setTour_area_a(String tour_area_a) {
		this.tour_area_a = tour_area_a;
	}
	public String getTour_area_b() {
		return tour_area_b;
	}
	public void setTour_area_b(String tour_area_b) {
		this.tour_area_b = tour_area_b;
	}
	public String getTour_area_c() {
		return tour_area_c;
	}
	public void setTour_area_c(String tour_area_c) {
		this.tour_area_c = tour_area_c;
	}
	public String getTour_area_d() {
		return tour_area_d;
	}
	public void setTour_area_d(String tour_area_d) {
		this.tour_area_d = tour_area_d;
	}
	public String getTour_area_e() {
		return tour_area_e;
	}
	public void setTour_area_e(String tour_area_e) {
		this.tour_area_e = tour_area_e;
	}
	public String getTour_natural() {
		return tour_natural;
	}
	public void setTour_natural(String tour_natural) {
		this.tour_natural = tour_natural;
	}
	public String getTour_nation() {
		return tour_nation;
	}
	public void setTour_nation(String tour_nation) {
		this.tour_nation = tour_nation;
	}
	public String getTour_person() {
		return tour_person;
	}
	public void setTour_person(String tour_person) {
		this.tour_person = tour_person;
	}
	public String getTour_festival() {
		return tour_festival;
	}
	public void setTour_festival(String tour_festival) {
		this.tour_festival = tour_festival;
	}
	public String getTour_type_a() {
		return tour_type_a;
	}
	public void setTour_type_a(String tour_type_a) {
		this.tour_type_a = tour_type_a;
	}
	public String getTour_type_b() {
		return tour_type_b;
	}
	public void setTour_type_b(String tour_type_b) {
		this.tour_type_b = tour_type_b;
	}
	public String getTour_type_c() {
		return tour_type_c;
	}
	public void setTour_type_c(String tour_type_c) {
		this.tour_type_c = tour_type_c;
	}
	public String getTour_season() {
		return tour_season;
	}
	public void setTour_season(String tour_season) {
		this.tour_season = tour_season;
	}
	public String getTour_human() {
		return tour_human;
	}
	public void setTour_human(String tour_human) {
		this.tour_human = tour_human;
	}
	public String getTour_tourist() {
		return tour_tourist;
	}
	public void setTour_tourist(String tour_tourist) {
		this.tour_tourist = tour_tourist;
	}
	public String getTour_mc_yn() {
		return tour_mc_yn;
	}
	public void setTour_mc_yn(String tour_mc_yn) {
		this.tour_mc_yn = tour_mc_yn;
	}
	public String getTour_mc() {
		return tour_mc;
	}
	public void setTour_mc(String tour_mc) {
		this.tour_mc = tour_mc;
	}
	public String getTour_food() {
		return tour_food;
	}
	public void setTour_food(String tour_food) {
		this.tour_food = tour_food;
	}
	public String getTour_type_d() {
		return tour_type_d;
	}
	public void setTour_type_d(String tour_type_d) {
		this.tour_type_d = tour_type_d;
	}
	public String getTour_festival_name() {
		return tour_festival_name;
	}
	public void setTour_festival_name(String tour_festival_name) {
		this.tour_festival_name = tour_festival_name;
	}
	public String getTour_trans() {
		return tour_trans;
	}
	public void setTour_trans(String tour_trans) {
		this.tour_trans = tour_trans;
	}
	public String getTour_peaple() {
		return tour_peaple;
	}
	public void setTour_peaple(String tour_peaple) {
		this.tour_peaple = tour_peaple;
	}
	public String getTour_weather() {
		return tour_weather;
	}
	public void setTour_weather(String tour_weather) {
		this.tour_weather = tour_weather;
	}
	public String getTour_music() {
		return tour_music;
	}
	public void setTour_music(String tour_music) {
		this.tour_music = tour_music;
	}
	public String getTour_period() {
		return tour_period;
	}
	public void setTour_period(String tour_period) {
		this.tour_period = tour_period;
	}
	public String getTour_companion() {
		return tour_companion;
	}
	public void setTour_companion(String tour_companion) {
		this.tour_companion = tour_companion;
	}
	public String getTour_etc_a() {
		return tour_etc_a;
	}
	public void setTour_etc_a(String tour_etc_a) {
		this.tour_etc_a = tour_etc_a;
	}
	public String getTour_etc_b() {
		return tour_etc_b;
	}
	public void setTour_etc_b(String tour_etc_b) {
		this.tour_etc_b = tour_etc_b;
	}

}