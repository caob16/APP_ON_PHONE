package com.example.app_on_phone.utils;

import android.util.Log;

import com.example.app_on_phone.appmaneger.pojo.APP;
import com.example.app_on_phone.appmaneger.pojo.TimeSlot;

import com.example.app_on_phone.timetable.pojo.Course;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ApiService {
    private NetworkUtils networkUtils;
    private Gson gson;

    private String prefix = "http://115.159.79.144:9988/magic/";

    public ApiService() {
        this.networkUtils = new NetworkUtils();
        this.gson = new Gson();
    }

    public CompletableFuture<List<TimeSlot>> getTimeSlotsList(String accID) {
        String url = prefix+"am/time/query";
        Map<String, String> parameters = new HashMap<>();
        parameters.put("account_id", accID);
        String jsonParameters = gson.toJson(parameters);

        return networkUtils.sendRequest(url, jsonParameters).thenApply(response -> {
            JsonElement dataElement = JsonParser.parseString(response).getAsJsonObject().get("data");
            Type type = new TypeToken<List<TimeSlot>>(){}.getType();
            return gson.fromJson(dataElement, type);
        });
    }

    public CompletableFuture<List<APP>> getAPPsList(String accID, String timeSlotID) {
        String url = prefix+"am/app/query";
        Map<String, String> parameters = new HashMap<>();
        parameters.put("account_id", accID);
        parameters.put("timeSlotID", timeSlotID);
        String jsonParameters = gson.toJson(parameters);

        return networkUtils.sendRequest(url, jsonParameters).thenApply(response -> {
            JsonElement dataElement = JsonParser.parseString(response).getAsJsonObject().get("data");
            Type type = new TypeToken<List<APP>>(){}.getType();
            return gson.fromJson(dataElement, type);
        });
    }

    public CompletableFuture<List<Course>> getCoursesList(String accID) {
        String url = prefix+"course/query";
        Map<String, String> parameters = new HashMap<>();
        parameters.put("account_id", accID);
        String jsonParameters = gson.toJson(parameters);

        return networkUtils.sendRequest(url, jsonParameters).thenApply(response -> {
            JsonElement dataElement = JsonParser.parseString(response).getAsJsonObject().get("data");
            Type type = new TypeToken<List<Course>>(){}.getType();
            return gson.fromJson(dataElement, type);
        });
    }

    public CompletableFuture<String> insertTimeSlot(TimeSlot timeSlot) {
        String url = prefix+"am/time/insert";
        String jsonParameters = gson.toJson(timeSlot);
        return networkUtils.sendRequest(url, jsonParameters).thenApply(response -> {
            // 假设服务器返回一个包含一个名为"status"的字段的JSON对象，例如：{"status":200}
            JsonObject responseObject = gson.fromJson(response, JsonObject.class);
            return responseObject.get("msg").getAsString();
        });
    }

    public CompletableFuture<String> insertCourse(String accID, String courseName){
        String url = prefix+"course/insert";
        Map<String, String> parameters = new HashMap<>();
        parameters.put("account_id", accID);
        parameters.put("course_name", courseName);
        String jsonParameters = gson.toJson(parameters);

        return networkUtils.sendRequest(url, jsonParameters).thenApply(response -> {
            // 假设服务器返回一个包含一个名为"status"的字段的JSON对象，例如：{"status":200}
            JsonObject responseObject = gson.fromJson(response, JsonObject.class);
            return responseObject.get("msg").getAsString();
        });
    }

    public CompletableFuture<String> updateTimeSlot(TimeSlot timeSlot) {
        String url = prefix+"am/time/update";
        String jsonParameters = gson.toJson(timeSlot);
        return networkUtils.sendRequest(url, jsonParameters).thenApply(response -> {
            // 假设服务器返回一个包含一个名为"status"的字段的JSON对象，例如：{"status":200}
            JsonObject responseObject = gson.fromJson(response, JsonObject.class);
            return responseObject.get("msg").getAsString();
        });
    }

    public CompletableFuture<String> deleteTimeSlot(int id) {
        String url = prefix+"am/time/delete";
        Map<String, Integer> parameters = new HashMap<>();
        parameters.put("id", id);
        String jsonParameters = gson.toJson(parameters);
        return networkUtils.sendRequest(url, jsonParameters).thenApply(response -> {
            // 假设服务器返回一个包含一个名为"status"的字段的JSON对象，例如：{"status":200}
            JsonObject responseObject = gson.fromJson(response, JsonObject.class);
            return responseObject.get("msg").getAsString();
        });
    }

    public CompletableFuture<String> updateAPPStatus(int appID, int tsID) {
        String url = prefix+"am/app/insertstatus";
        Map<String, Integer> parameters = new HashMap<>();
        parameters.put("appId", appID);
        parameters.put("tsId", tsID);
        String jsonParameters = gson.toJson(parameters);
        Log.e("upDateAPPStatus",jsonParameters);

        return networkUtils.sendRequest(url, jsonParameters).thenApply(response -> {
            // 假设服务器返回一个包含一个名为"status"的字段的JSON对象，例如：{"status":200}
            JsonObject responseObject = gson.fromJson(response, JsonObject.class);
            return responseObject.get("msg").getAsString();
        });
    }

    public CompletableFuture<String> deleteAPPStatus(int appID, int tsID) {
        String url = prefix+"am/app/deletestatus";
        Map<String, Integer> parameters = new HashMap<>();
        parameters.put("appId", appID);
        parameters.put("tsId", tsID);
        String jsonParameters = gson.toJson(parameters);

        Log.e("deleteAPPStatus",jsonParameters);
        return networkUtils.sendRequest(url, jsonParameters).thenApply(response -> {
            // 假设服务器返回一个包含一个名为"status"的字段的JSON对象，例如：{"status":200}
            JsonObject responseObject = gson.fromJson(response, JsonObject.class);
            return responseObject.get("msg").getAsString();
        });
    }

    public CompletableFuture<String> updateCoursesName(List<Course> courseList) {
        String url = prefix+"course/updateName";
        Map<String, List<Course>> parameters = new HashMap<>();
        parameters.put("data", courseList);
        String jsonParameters = gson.toJson(parameters);

        return networkUtils.sendRequest(url, jsonParameters).thenApply(response -> {
            // 假设服务器返回一个包含一个名为"status"的字段的JSON对象，例如：{"status":200}
            JsonObject responseObject = gson.fromJson(response, JsonObject.class);
            return responseObject.get("msg").getAsString();
        });
    }

    public CompletableFuture<String> updateCourseTime(int accID, List<Course> courseList) {
        String url = prefix+"course/updateCourseTime";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("account_id", accID);
        parameters.put("data", courseList);
        String jsonParameters = gson.toJson(parameters);
        Log.e("!!!!!!!!!!!!!!!!!!",jsonParameters);
        return networkUtils.sendRequest(url, jsonParameters).thenApply(response -> {
            // 假设服务器返回一个包含一个名为"status"的字段的JSON对象，例如：{"status":200}
            JsonObject responseObject = gson.fromJson(response, JsonObject.class);
            return responseObject.get("msg").getAsString();
        });
    }


    public CompletableFuture<String> deleteCourses(List<Integer> ids) {
        String url = prefix+"course/delete";
        Map<String, List<Integer>> parameters = new HashMap<>();
        parameters.put("ids", ids);
        String jsonParameters = gson.toJson(parameters);

        return networkUtils.sendRequest(url, jsonParameters).thenApply(response -> {
            // 假设服务器返回一个包含一个名为"status"的字段的JSON对象，例如：{"status":200}
            JsonObject responseObject = gson.fromJson(response, JsonObject.class);
            return responseObject.get("msg").getAsString();
        });
    }
    // 为其他的请求类型添加更多的方法，例如：
    // public CompletableFuture<AnotherObject> getAnotherObject() {...}
    /**
     *####
     * 7.15/zxy
     *#####
     * **/
    public CompletableFuture<String> endMeasure (int measureID) {
        String url = prefix+"measure/end";
        Map<String, Integer> parameters = new HashMap<>();
        parameters.put("id", measureID);
        String jsonParameters = gson.toJson(parameters);

        return networkUtils.sendRequest(url, jsonParameters).thenApply(response -> {
            // 假设服务器返回一个包含一个名为"status"的字段的JSON对象，例如：{"status":200}\
            JsonObject responseObject = gson.fromJson(response, JsonObject.class);
            return responseObject.get("msg").getAsString();
        });
    }

    public CompletableFuture<Integer> startMeasure (String accID) {
        String url = prefix+"measure/start";
        Map<String, String> parameters = new HashMap<>();
        parameters.put("account_id", accID);
        String jsonParameters = gson.toJson(parameters);

        return networkUtils.sendRequest(url, jsonParameters).thenApply(response -> {
            // 假设服务器返回一个包含一个名为"status"的字段的JSON对象，例如：{"status":200}\
            JsonObject responseObject = gson.fromJson(response, JsonObject.class);
            return responseObject.get("data").getAsInt();
        });
    }

    public CompletableFuture<List<Integer>> getMeasureTimeStats (String AccID, String date) {
        String url = prefix+"measure/timeQuery";
        Map<String, String> parameters = new HashMap<>();
        parameters.put("account_id", AccID);
        parameters.put("date", date);
        String jsonParameters = gson.toJson(parameters);

        return networkUtils.sendRequest(url, jsonParameters).thenApply(response -> {
            // 假设服务器返回一个包含一个名为"status"的字段的JSON对象，例如：{"status":200}\
            JsonElement dataElement = JsonParser.parseString(response).getAsJsonObject().get("data");
            Type type = new TypeToken<List<Integer>>(){}.getType();
            return gson.fromJson(dataElement, type);
        });
    }

    public CompletableFuture<List<Integer>> getAlarmSeg (String AccID, String date) {
        String url = prefix+"measure/alarmSeg";
        Map<String, String> parameters = new HashMap<>();
        parameters.put("account_id", AccID);
        parameters.put("date", date);
        String jsonParameters = gson.toJson(parameters);

        return networkUtils.sendRequest(url, jsonParameters).thenApply(response -> {
            // 假设服务器返回一个包含一个名为"status"的字段的JSON对象，例如：{"status":200}\
            JsonElement dataElement = JsonParser.parseString(response).getAsJsonObject().get("data");
            Type type = new TypeToken<List<Integer>>(){}.getType();
            return gson.fromJson(dataElement, type);
        });
    }

    public CompletableFuture<List<Integer>> getAlarmType (String AccID, String date) {
        String url = prefix+"measure/alarmType";
        Map<String, String> parameters = new HashMap<>();
        parameters.put("account_id", AccID);
        parameters.put("date", date);
        String jsonParameters = gson.toJson(parameters);

        return networkUtils.sendRequest(url, jsonParameters).thenApply(response -> {
            // 假设服务器返回一个包含一个名为"status"的字段的JSON对象，例如：{"status":200}\
            JsonElement dataElement = JsonParser.parseString(response).getAsJsonObject().get("data");
            Type type = new TypeToken<List<Integer>>(){}.getType();
            return gson.fromJson(dataElement, type);
        });
    }

    public CompletableFuture<Integer> startAlarm (String accID,String type) {
        String url = prefix+"measure/alarmStart";
        Map<String, String> parameters = new HashMap<>();
        parameters.put("account_id", accID);
        parameters.put("alarm_type", type);
        String jsonParameters = gson.toJson(parameters);

        return networkUtils.sendRequest(url, jsonParameters).thenApply(response -> {
            // 假设服务器返回一个包含一个名为"status"的字段的JSON对象，例如：{"status":200}\
            JsonObject responseObject = gson.fromJson(response, JsonObject.class);
            return responseObject.get("data").getAsInt();
        });
    }

    public CompletableFuture<String> endAlarm (int alarmID) {
        String url = prefix+"measure/alarmEnd";
        Map<String, Integer> parameters = new HashMap<>();
        parameters.put("id", alarmID);
        String jsonParameters = gson.toJson(parameters);

        return networkUtils.sendRequest(url, jsonParameters).thenApply(response -> {
            // 假设服务器返回一个包含一个名为"status"的字段的JSON对象，例如：{"status":200}\
            JsonObject responseObject = gson.fromJson(response, JsonObject.class);
            return responseObject.get("msg").getAsString();
        });
    }
}
