package com.reactivespring.movieinfoservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@Document
public class MovieInfo {

    @Id
    private String id;
    @NotBlank(message = "MovieInfo.name must be present")
    private String name;
    @NotNull(message = "MovieInfo.year must be present")
    @Positive(message = "MovieInfo.year must be a positive value")
    private Integer year;
    private List<String> cast;
    private LocalDate relaseDate;

    public MovieInfo() {
    }

    public MovieInfo(String id, String name, Integer year, List<String> cast, LocalDate relaseDate) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.cast = cast;
        this.relaseDate = relaseDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public List<String> getCast() {
        return cast;
    }

    public void setCast(List<String> cast) {
        this.cast = cast;
    }

    public LocalDate getRelaseDate() {
        return relaseDate;
    }

    public void setRelaseDate(LocalDate relaseDate) {
        this.relaseDate = relaseDate;
    }

    @Override
    public String toString() {
        return "MovieInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", year=" + year +
                ", cast=" + cast +
                ", relaseDate=" + relaseDate +
                '}';
    }
}
