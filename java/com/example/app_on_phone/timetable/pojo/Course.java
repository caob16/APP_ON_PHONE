package com.example.app_on_phone.timetable.pojo;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Course implements Cloneable, Serializable {
    private int id;
    //@SerializedName("course_name")
    private String courseName;//课程名
    //格式：星期-节次-单双周-房号
    private String courseTime;//上课时间

    //private String weekType;//单双周类型
    private int day;//星期几
    private int section;//节次

    public Course() {
    }
    public Course(String courseName, String courseTime) {
        this.courseName = courseName;
        this.courseTime = courseTime;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public String getCourseTime() {
        return courseTime;
    }

    public void setCourseTime(String courseTime) {
        this.courseTime = courseTime;
    }

    public Course clone() {
        try {
            return (Course) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", courseName='" + courseName + '\'' +
//                ", weekType='" + weekType + '\'' +
                ", day=" + day +
                ", section=" + section +
                ", courseTime='" + courseTime + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(courseName, course.courseName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseName);
    }

    public List<Course> toDetail() {
        List<Course> courseList = new ArrayList<>();
        if (TextUtils.isEmpty(courseTime)) return courseList;
        String[] courseArray = courseTime.split(";");
        for (int i = 0; i < courseArray.length; i++) {
            Course clone = this.clone();
            String[] info = courseArray[i].split(":");

            clone.setDay(Integer.parseInt(info[0]));
            clone.setSection(Integer.parseInt(info[1]));
//            clone.setWeekType(info[2]);

            courseList.add(clone);
        }
        return courseList;
    }

    public String toTime(){
        return String.format("%d:%d", day, section);
    }

    public static Course toCourse(List<Course> courseList, int id){
        if(null == courseList)return null;
        Course course = new Course();
        course.setId(id);
        StringBuffer sb = new StringBuffer();
        for(int i = 0, len = courseList.size(); i < len; i++){
            sb.append(courseList.get(i).toTime());
            if(i != len - 1)sb.append(";");
        }
        course.setCourseTime(String.valueOf(sb));
        return course;
    }
}
