package com.kthcorp.cmts.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;

@Alias("CcubeSeries")
public class CcubeSeries extends Paging {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    private String series_id;
    private String series_nm;
    private String purity_title;
    private String eng_title;
    private String director;
    private String year;
    private String actors_display;
    private String poster_url;
    private String country_of_origin;
    private String sad_ctgry_id;
    private String sad_ctgry_nm;
    private String kt_rating;
    private String detail_genre_display_cd;
    private String detail_genre_display_nm;
    private Timestamp regdate;
    private String stat;

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getSeries_id() {
        return series_id;
    }

    public void setSeries_id(String series_id) {
        this.series_id = series_id;
    }

    public String getSeries_nm() {
        return series_nm;
    }

    public void setSeries_nm(String series_nm) {
        this.series_nm = series_nm;
    }

    public String getPurity_title() {
        return purity_title;
    }

    public void setPurity_title(String purity_title) {
        this.purity_title = purity_title;
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

    public String getKt_rating() {
        return kt_rating;
    }

    public void setKt_rating(String kt_rating) {
        this.kt_rating = kt_rating;
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

    public Timestamp getRegdate() {
        return regdate;
    }

    public void setRegdate(Timestamp regdate) {
        this.regdate = regdate;
    }
}