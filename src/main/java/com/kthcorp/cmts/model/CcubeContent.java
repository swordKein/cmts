package com.kthcorp.cmts.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;

@Alias("CcubeContent")
public class CcubeContent extends Paging {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    private String master_content_id;
    private String content_id;
    private String purity_title;
    private String content_title;
    private String eng_title;
    private String director;
    private String year;
    private String actors_display;
    private String poster_url;
    private String country_of_origin;
    private String sad_ctgry_id;
    private String sad_ctgry_nm;
    private String title_brief;
    private String domestic_release_date;
    private String kt_rating;
    private String running_time;
    private String detail_genre_display_cd;
    private String detail_genre_display_nm;
    private String kmrb_id;
    private String kmrb_title_nm;
    private String kmrb_year;
    private String kmrb_domestic_release_date;
    private String kmrb_country_of_origin;
    private String kmrb_director;
    private String kmrb_director_eng;
    private String kmrb_actors_display;
    private String kmrb_actors_display_eng;
    private Timestamp regdate;
    private String stat;

    private String summary_long;
    private String summary_medium;

    public String getSummary_long() {
        return summary_long;
    }

    public void setSummary_long(String summary_long) {
        this.summary_long = summary_long;
    }

    public String getSummary_medium() {
        return summary_medium;
    }

    public void setSummary_medium(String summary_medium) {
        this.summary_medium = summary_medium;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getMaster_content_id() {
        return master_content_id;
    }

    public void setMaster_content_id(String master_content_id) {
        this.master_content_id = master_content_id;
    }

    public String getContent_id() {
        return content_id;
    }

    public void setContent_id(String content_id) {
        this.content_id = content_id;
    }

    public String getPurity_title() {
        return purity_title;
    }

    public void setPurity_title(String purity_title) {
        this.purity_title = purity_title;
    }

    public String getContent_title() {
        return content_title;
    }

    public void setContent_title(String content_title) {
        this.content_title = content_title;
    }

    public String getEng_title() {
        return eng_title;
    }

    public void setEng_title(String eng_title) {
        this.eng_title = eng_title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getActors_display() {
        return actors_display;
    }

    public void setActors_display(String actors_display) {
        this.actors_display = actors_display;
    }

    public String getPoster_url() {
        return poster_url;
    }

    public void setPoster_url(String poster_url) {
        this.poster_url = poster_url;
    }

    public String getCountry_of_origin() {
        return country_of_origin;
    }

    public void setCountry_of_origin(String country_of_origin) {
        this.country_of_origin = country_of_origin;
    }

    public String getSad_ctgry_id() {
        return sad_ctgry_id;
    }

    public void setSad_ctgry_id(String sad_ctgry_id) {
        this.sad_ctgry_id = sad_ctgry_id;
    }

    public String getSad_ctgry_nm() {
        return sad_ctgry_nm;
    }

    public void setSad_ctgry_nm(String sad_ctgry_nm) {
        this.sad_ctgry_nm = sad_ctgry_nm;
    }

    public String getTitle_brief() {
        return title_brief;
    }

    public void setTitle_brief(String title_brief) {
        this.title_brief = title_brief;
    }

    public String getDomestic_release_date() {
        return domestic_release_date;
    }

    public void setDomestic_release_date(String domestic_release_date) {
        this.domestic_release_date = domestic_release_date;
    }

    public String getKt_rating() {
        return kt_rating;
    }

    public void setKt_rating(String kt_rating) {
        this.kt_rating = kt_rating;
    }

    public String getRunning_time() {
        return running_time;
    }

    public void setRunning_time(String running_time) {
        this.running_time = running_time;
    }

    public String getDetail_genre_display_cd() {
        return detail_genre_display_cd;
    }

    public void setDetail_genre_display_cd(String detail_genre_display_cd) {
        this.detail_genre_display_cd = detail_genre_display_cd;
    }

    public String getDetail_genre_display_nm() {
        return detail_genre_display_nm;
    }

    public void setDetail_genre_display_nm(String detail_genre_display_nm) {
        this.detail_genre_display_nm = detail_genre_display_nm;
    }

    public String getKmrb_id() {
        return kmrb_id;
    }

    public void setKmrb_id(String kmrb_id) {
        this.kmrb_id = kmrb_id;
    }

    public String getKmrb_title_nm() {
        return kmrb_title_nm;
    }

    public void setKmrb_title_nm(String kmrb_title_nm) {
        this.kmrb_title_nm = kmrb_title_nm;
    }

    public String getKmrb_year() {
        return kmrb_year;
    }

    public void setKmrb_year(String kmrb_year) {
        this.kmrb_year = kmrb_year;
    }

    public String getKmrb_domestic_release_date() {
        return kmrb_domestic_release_date;
    }

    public void setKmrb_domestic_release_date(String kmrb_domestic_release_date) {
        this.kmrb_domestic_release_date = kmrb_domestic_release_date;
    }

    public String getKmrb_country_of_origin() {
        return kmrb_country_of_origin;
    }

    public void setKmrb_country_of_origin(String kmrb_country_of_origin) {
        this.kmrb_country_of_origin = kmrb_country_of_origin;
    }

    public String getKmrb_director() {
        return kmrb_director;
    }

    public void setKmrb_director(String kmrb_director) {
        this.kmrb_director = kmrb_director;
    }

    public String getKmrb_director_eng() {
        return kmrb_director_eng;
    }

    public void setKmrb_director_eng(String kmrb_director_eng) {
        this.kmrb_director_eng = kmrb_director_eng;
    }

    public String getKmrb_actors_display() {
        return kmrb_actors_display;
    }

    public void setKmrb_actors_display(String kmrb_actors_display) {
        this.kmrb_actors_display = kmrb_actors_display;
    }

    public String getKmrb_actors_display_eng() {
        return kmrb_actors_display_eng;
    }

    public void setKmrb_actors_display_eng(String kmrb_actors_display_eng) {
        this.kmrb_actors_display_eng = kmrb_actors_display_eng;
    }

    public Timestamp getRegdate() {
        return regdate;
    }

    public void setRegdate(Timestamp regdate) {
        this.regdate = regdate;
    }
}