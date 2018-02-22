package com.kthcorp.cmts.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;

@Alias("MovieCine21")
public class MovieCine21 extends Paging {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    private int idx;
    private int movieId;
    private String fimsCd;
    private String movieNm;
    private String movieNmOg;
    private String runTime;
    private String prdtYear;
    private Timestamp openDt;
    private String synop;
    private Timestamp updateDt;
    private String watchGrade;
    private String countries;
    private String genres;
    private String directors;
    private String actors;
    private String staffs;
    private String companies;
    private String awards;
    private String posterList;
    private String stillCutList;
    private String vodList;
    private String cineKeywords;


    public String getRunTime() {
        return runTime;
    }

    public void setRunTime(String runTime) {
        this.runTime = runTime;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getFimsCd() {
        return fimsCd;
    }

    public void setFimsCd(String fimsCd) {
        this.fimsCd = fimsCd;
    }

    public String getMovieNm() {
        return movieNm;
    }

    public void setMovieNm(String movieNm) {
        this.movieNm = movieNm;
    }

    public String getMovieNmOg() {
        return movieNmOg;
    }

    public void setMovieNmOg(String movieNmOg) {
        this.movieNmOg = movieNmOg;
    }


    public String getPrdtYear() {
        return prdtYear;
    }

    public void setPrdtYear(String prdtYear) {
        this.prdtYear = prdtYear;
    }

    public Timestamp getOpenDt() {
        return openDt;
    }

    public void setOpenDt(Timestamp openDt) {
        this.openDt = openDt;
    }

    public String getSynop() {
        return synop;
    }

    public void setSynop(String synop) {
        this.synop = synop;
    }

    public Timestamp getUpdateDt() {
        return updateDt;
    }

    public void setUpdateDt(Timestamp updateDt) {
        this.updateDt = updateDt;
    }

    public String getWatchGrade() {
        return watchGrade;
    }

    public void setWatchGrade(String watchGrade) {
        this.watchGrade = watchGrade;
    }

    public String getCountries() {
        return countries;
    }

    public void setCountries(String countries) {
        this.countries = countries;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getDirectors() {
        return directors;
    }

    public void setDirectors(String directors) {
        this.directors = directors;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getStaffs() {
        return staffs;
    }

    public void setStaffs(String staffs) {
        this.staffs = staffs;
    }

    public String getCompanies() {
        return companies;
    }

    public void setCompanies(String companies) {
        this.companies = companies;
    }

    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public String getPosterList() {
        return posterList;
    }

    public void setPosterList(String posterList) {
        this.posterList = posterList;
    }

    public String getStillCutList() {
        return stillCutList;
    }

    public void setStillCutList(String stillCutList) {
        this.stillCutList = stillCutList;
    }

    public String getVodList() {
        return vodList;
    }

    public void setVodList(String vodList) {
        this.vodList = vodList;
    }

    public String getCineKeywords() {
        return cineKeywords;
    }

    public void setCineKeywords(String cineKeywords) {
        this.cineKeywords = cineKeywords;
    }
}