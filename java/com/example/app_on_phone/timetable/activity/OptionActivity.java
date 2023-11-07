package com.example.app_on_phone.timetable.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app_on_phone.R;
import com.example.app_on_phone.timetable.adapter.CourseAdapter;
import com.example.app_on_phone.timetable.pojo.Course;
import com.example.app_on_phone.timetable.view.SelectTableView;
import com.example.app_on_phone.utils.ApiService;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class OptionActivity extends AppCompatActivity{
    private ListView lvContent;
    private ImageView btnDel;
    private ImageView btnEdit;
    private ImageView btnSave;
    private ImageView btnAdd;
    private TextView btnSelect;
    private List<Course> courseList;
    private SelectTableView select;
    private List<Course> tempCourseList = new ArrayList<>();
    private long date;
    private SharedPreferences sp;
    public List<String> startList = new ArrayList<>();
    public List<String> endList = new ArrayList<>();
    private ApiService apiService = new ApiService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        lvContent = findViewById(R.id.lvCourse);
        btnDel = findViewById(R.id.btnDel);
        btnEdit = findViewById(R.id.btnEdit);
        btnSelect = findViewById(R.id.btnSelect);
        btnSave = findViewById(R.id.btnSave);
        btnAdd = findViewById(R.id.btnAddCourse);
        btnDel.setVisibility(View.INVISIBLE);
        btnSave.setVisibility(View.INVISIBLE);
        lvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Course course = courseList.get(position);
                addLesson(course);
            }
        });
        sp = getSharedPreferences("time", MODE_PRIVATE);
        String startTimeVal = sp.getString("start", "07:20");
        String breakTimeVal = sp.getString("break", "09:00");
        String lunchTimeVal = sp.getString("lunch", "13:00");
        int classLength = sp.getInt("Length", 45);
        classLength += 10;
        int classCnt = sp.getInt("Cnt", 4);
        //将string转为日期
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date lunchTime = null;
        Date breakTime = null;
        Date startTime = null;
        try {
            lunchTime = sdf.parse(startTimeVal);
            breakTime = sdf.parse(breakTimeVal);
            startTime = sdf.parse(lunchTimeVal);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf = new SimpleDateFormat("mm");
        Date ClassTime = null;
        try{
            String classLengthstr = classLength + "";
            ClassTime = sdf.parse(classLengthstr);
        }catch (ParseException e){
            e.printStackTrace();
        }

        long startLong = startTime.getTime();
        long endLong = startLong;
        long breakLong = breakTime.getTime();
        for(int i = 0; i < classCnt; i++){
            if(i == 2){
                startLong = breakLong;
            }
            endLong = startLong + classLength;
            startList.add(new Date(startLong).toString());
            endList.add(new Date(endLong).toString());
            startLong += classLength + 10;
        }
        startLong = lunchTime.getTime();
        for(int i = 0; i < 9 - classCnt; i++){
            endLong = startLong + classLength;
            startList.add(new Date(startLong).toString());
            endList.add(new Date(endLong).toString());
            startLong += classLength + 10;
        }
        Log.e("startList", startList.toString());
        Log.e("endList", endList.toString());
    }

    @Override
    protected void onStart() {
        super.onStart();
        show("1");
    }

    /**
     * 所有课程
     */
    private void show(String accID) {
        apiService.getCoursesList(accID).thenAccept(courses -> {
            courseList = courses;
            tempCourseList.clear();
            for(Course course : courseList){
                tempCourseList.add(course.clone());
            }
            OptionActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int listSize = courseList.size();
                    if (listSize >= 0) {
                        CourseAdapter courseAdapter = new CourseAdapter(OptionActivity.this, courseList,1);
                        lvContent.setAdapter(courseAdapter);
                    } else {
                        Toast.makeText(OptionActivity.this, "暂无数据，请添加课程！", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }).exceptionally(e -> {
            System.out.println("An error occurred: " + e.getCause());
            return null;
        });
    }

    /**
     * 编辑
     *
     * @param view
     */
    public void edit(View view) {
        CourseAdapter courseAdapter = new CourseAdapter(this, courseList,2);
        lvContent.setAdapter(courseAdapter);
        btnAdd.setVisibility(View.INVISIBLE);
        btnEdit.setVisibility(View.INVISIBLE);
        btnSelect.setVisibility(View.INVISIBLE);
        btnDel.setVisibility(View.INVISIBLE);
        btnSave.setVisibility(View.VISIBLE);
    }

    /**
     * 保存
     *
     * @param view
     */
    public void save(View view) {
        int firstVisiblePosition = lvContent.getFirstVisiblePosition();
        int lastVisiblePosition = lvContent.getLastVisiblePosition();
        for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
            View itemView = lvContent.getChildAt(i - firstVisiblePosition);
            // 在这里操作 itemView，比如更改背景色、文本等
            EditText courseName = itemView.findViewById(R.id.courseName);
            String CourseName = courseName.getText().toString();
            courseList.get(i - firstVisiblePosition).setCourseName(CourseName);
        }
        Log.e("Save@@@@@courseList",courseList.toString());
        apiService.updateCoursesName(courseList).thenAccept(message -> {
            if(message.equals("success")){
                OptionActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        show("1");
                        Toast.makeText(OptionActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                OptionActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        show("1");
                        Toast.makeText(OptionActivity.this, "更新失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }).exceptionally(e -> {
            System.out.println("An error occurred: " + e.getCause());
            OptionActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    show("1");
                    Toast.makeText(OptionActivity.this, "更新失败！", Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        });
        btnAdd.setVisibility(View.VISIBLE);
        btnEdit.setVisibility(View.VISIBLE);
        btnSelect.setVisibility(View.VISIBLE);
        btnDel.setVisibility(View.INVISIBLE);
        btnSave.setVisibility(View.INVISIBLE);
        closeKeyboard();
    }

    /**
     * 选择
     *
     * @param view
     */
    public void select(View view){
        CourseAdapter courseAdapter = new CourseAdapter(this, courseList,3);
        lvContent.setAdapter(courseAdapter);
        btnAdd.setVisibility(View.INVISIBLE);
        btnEdit.setVisibility(View.INVISIBLE);
        btnSelect.setVisibility(View.INVISIBLE);
        btnDel.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.INVISIBLE);
    }

    /**
     * 删除
     *
     * @param view
     */
    public void delete(View view) {
        new AlertDialog.Builder(this)
            .setTitle("删除课程")
            .setPositiveButton("确定删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int firstVisiblePosition = lvContent.getFirstVisiblePosition();
                    int lastVisiblePosition = lvContent.getLastVisiblePosition();
                    List<Integer> idList = new ArrayList<>();
                    for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                        View itemView = lvContent.getChildAt(i - firstVisiblePosition);
                        // 在这里操作 itemView，比如更改背景色、文本等
                        CheckBox checkBox = itemView.findViewById(R.id.courseCheck);
                        if(checkBox.isChecked()){
                            idList.add(courseList.get(i - firstVisiblePosition).getId());
                        }
                    }
                    apiService.deleteCourses(idList).thenAccept(message -> {
                        if(message.equals("success")){
                            OptionActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    show("1");
                                    Toast.makeText(OptionActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            OptionActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    show("1");
                                    Toast.makeText(OptionActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).exceptionally(e -> {
                        System.out.println("An error occurred: " + e.getCause());
                        OptionActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                show("1");
                                Toast.makeText(OptionActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return null;
                    });
                    btnAdd.setVisibility(View.VISIBLE);
                    btnEdit.setVisibility(View.VISIBLE);
                    btnSelect.setVisibility(View.VISIBLE);
                    btnDel.setVisibility(View.INVISIBLE);
                    btnSave.setVisibility(View.INVISIBLE);
                }
            }).setNegativeButton("取消", null)
            .create()
            .show();
    }


    /**
     * 返回
     *
     * @param view
     */
    public void back(View view) {
        this.finish();
    }

    /**
     * 添加课程
     *
     * @param view
     */
    public void addCourse(View view) {
        final View inflate = getLayoutInflater().inflate(R.layout.add_course_item, null);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(inflate)
                .setPositiveButton("添加", null) // 注意这里，我们暂时设置监听器为null
                .setNegativeButton("取消", null)
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button addButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String courseName = ((EditText) inflate.findViewById(R.id.etCourseName)).getText().toString();

                        if ("".equals(courseName)) {
                            Toast.makeText(OptionActivity.this, "课程名不可为空！", Toast.LENGTH_SHORT).show();
                        } else {
                            apiService.insertCourse("1",courseName).thenAccept(message -> {
                                if(message.equals("success")){
                                    alertDialog.dismiss();
                                    OptionActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            show("1");
                                            Toast.makeText(OptionActivity.this, "添加课程成功！", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    OptionActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(OptionActivity.this, "添加失败！", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).exceptionally(e -> {
                                System.out.println("An error occurred: " + e.getCause());
                                OptionActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(OptionActivity.this, "添加失败！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return null;
                            });
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }

    /**
     * 添加课次
     *
     */
    public void addLesson(Course course) {
        final View inflate = getLayoutInflater().inflate(R.layout.add_lesson_item, null);
        select  = inflate.findViewById(R.id.selectTable);
        select.loadData(courseList,course.getCourseName());
        new AlertDialog.Builder(this)
                .setTitle("添加课次")
                .setView(inflate)
                .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[][] res = select.response;
                        for(int i = 0; i < 9; i++){
                            for(int j = 0; j < 5; j++){
                                Log.e("res",res[i][j]);
                                if(res[i][j] != "@") {
                                    if(res[i][j] == "$"){
                                        addLesson(j + 1, i + 1, course.getCourseName(), course);
                                    }else if(res[i][j] == "del") {
                                        coverCourse(getTargetCourse(course.getCourseName()),j + 1 ,i + 1);
                                    }else{
                                        coverCourse(getTargetCourse(res[i][j]),j + 1 ,i + 1);
                                        addLesson(j + 1, i + 1,course.getCourseName(), course);
                                    }
                                }
                            }
                        }
                        Log.e("templist@@@@@",tempCourseList.toString());
                        apiService.updateCourseTime(1,tempCourseList).thenAccept(message -> {
                            if(message.equals("success")){
                                OptionActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        show("1");
                                        Toast.makeText(OptionActivity.this, "添加课次成功！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                OptionActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tempCourseList.clear();
                                        for (Course item : courseList) {
                                            tempCourseList.add(item.clone());
                                        }
                                        Toast.makeText(OptionActivity.this, "添加失败！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).exceptionally(e -> {
                            System.out.println("An error occurred: " + e.getCause());
                            OptionActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(OptionActivity.this, "添加失败！", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return null;
                        });
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();

    }

    private void coverCourse(Course course, int day, int section){
        Log.e("coverCourse",course.toString());
        List<Course> courseList = course.toDetail();
        Iterator<Course> iterator = courseList.iterator();
        while (iterator.hasNext()) {
            Course c = iterator.next();
            if (c.getDay() == day &&
                    c.getSection() == section) {
                iterator.remove();
                break;
            }
        }
        Course toCourse = Course.toCourse(courseList, course.getId());
        Log.e("toCourse",toCourse.toString());
        if(toCourse.getCourseTime().equals(null)){
            toCourse.setCourseTime("");
        }
        for(Course c: tempCourseList){
            if (c.getId() == toCourse.getId()){
                c.setCourseTime(toCourse.getCourseTime());
                break;
            }
        }
    }

    private void addLesson(int day, int section, String courseName, Course mCourse) {
        Log.e("addLesson",courseName);
        Course course = new Course();
        course.setDay(day);
        course.setSection(section);
        String courseTime = getTargetCourse(courseName).getCourseTime();
        int id = mCourse.getId();
        if (TextUtils.isEmpty(courseTime)) {
            course.setCourseTime(course.toTime());
        } else {
            course.setCourseTime(courseTime + ";" + course.toTime());
        }
        course.setId(id);
        //修改

        for(Course c: tempCourseList){
            if (c.getId() == course.getId()){
                c.setCourseTime(course.getCourseTime());
                break;
            }
        }
    }

    private Course getTargetCourse(String target) {
        Log.e("getTargetCourse",target);
        int listSize = tempCourseList.size();
        if (listSize > 0) {
            for (int i = 0; i < listSize; i++){
                if(tempCourseList.get(i).getCourseName().equals(target)){
                    return tempCourseList.get(i);
                }
            }
        }
        return new Course();
    }

    /**
     * 修改开学时间
     * @param view
     */
    public void alterDate(View view) {
        final SharedPreferences config = getSharedPreferences("config", MODE_PRIVATE);
        final DatePicker datePicker = new DatePicker(this);
        long date = config.getLong("date", 0);
        if(date != 0){
            Calendar c = Calendar.getInstance();
            c.setTime(new Date(date));
            datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), null);
        }
        new DatePickerDialog.Builder(this)
                .setTitle("选择开学日期")
                .setView(datePicker)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth();
                        int dayOfMonth = datePicker.getDayOfMonth();
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth, 0, 0, 0);
                        Date time = calendar.getTime();
                        config.edit().putLong("date", time.getTime()).apply();
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    private void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    //点击修改时间打开新的页面
    public void alterTime(View view) {
        Intent intent = new Intent(OptionActivity.this, AlterTimeActivity.class);
        startActivity(intent);
    }


}
