package com.example.app_on_phone.ui.notifications;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_on_phone.R;
import com.example.app_on_phone.databinding.ActivityStatsBinding;
import com.example.app_on_phone.utils.ApiService;
import com.shawnlin.numberpicker.NumberPicker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;


public class StatsActivity extends AppCompatActivity{

    private SharedPreferences preferences;
    private Switch switchRad,switchVib,switchNoti,switchDelay,switchDeadalarm;
    private NumberPicker npAngle;
    private NumberPicker npInterval;
    private ApiService apiService = new ApiService();
    private List<Integer> measureTimeStats = new ArrayList<>();
    private List<Integer> alarmSeg = new ArrayList<>();
    private List<Integer> alarmInfoList = new ArrayList<>();
    private ActivityStatsBinding binding;
    private ColumnChartData columnChartData;
    private ColumnChartData previewData;
    private PieChartData pieChartData;
    public final static int MODEL_COUNT = 4;
    private int[] mStartColors = new int[MODEL_COUNT];




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStatsBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);

    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalDate today = LocalDate.now();  // 获取当前日期
        String todayAsString = today.toString();  // 将日期转换为字符串
        binding.textDate.setText(todayAsString);
        binding.textDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取当前日期
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                // 创建一个新的DatePickerDialog并显示它
                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // 这里获取到的月份需要加上1，因为月份是从0开始的
                                String selectedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);

                                // 在TextView上显示用户设置的日期
                                binding.textDate.setText(selectedDate);
                                getData("1", selectedDate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        Log.e("today", todayAsString);
        getData("1", todayAsString);
    }

    private void loadColumnChart() {
        int numColumns = 12;
        // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        for (int i = 0; i < numColumns; ++i) {

            values = new ArrayList<SubcolumnValue>();
            values.add(new SubcolumnValue(alarmSeg.get(i), ChartUtils.pickColor()));

            Column column = new Column(values);
            column.setHasLabels(true);
            column.setHasLabelsOnlyForSelected(true);
            columns.add(column);
        }

        columnChartData = new ColumnChartData(columns);

        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        int maxValue = Collections.max(alarmSeg);

        List<AxisValue> axisXValues = new ArrayList<>();
        for (int i = 0; i < 12; i++) {      //mClockNumberLength
            axisXValues.add(new AxisValue(i).setLabel(2 * i + "-" + (2 * i + 2)));
        }
        List<AxisValue> axisYValues = new ArrayList<>();

        for (int i = 0; i <= maxValue; i++) {      //mClockNumberLength
            Log.e("", "generateDefaultData:"+i);
            axisYValues.add(new AxisValue(i).setLabel(String.valueOf(i)));
        }

        previewData = new ColumnChartData(columnChartData);
        for (Column column : previewData.getColumns()) {
            for (SubcolumnValue value : column.getValues()) {
                value.setColor(ChartUtils.DEFAULT_DARKEN_COLOR);
            }
        }

        axisX.setName("时间段");
        axisY.setName("报警次数");
        axisX.setTextSize(10);//设置字体的大小
        axisY.setTextSize(10);
        axisY.setHasLines(true);//x轴的分割线
        axisX.setValues(axisXValues);
        axisY.setValues(axisYValues);
        columnChartData.setAxisXBottom(axisX);
        columnChartData.setAxisYLeft(axisY);

        binding.barChart.setZoomEnabled(false);
        binding.barChart.setScrollEnabled(false);
        binding.barChart.setColumnChartData(columnChartData);

        binding.previewBarChart.setColumnChartData(previewData);
        binding.previewBarChart.setViewportChangeListener(new ViewportListener());

        Viewport v = new Viewport(-0.75f, binding.barChart.getMaximumViewport().height()*1.25f, numColumns, 0);
        binding.barChart.setMaximumViewport(v);
        binding.barChart.setCurrentViewport(v);

        binding.previewBarChart.setMaximumViewport(v);
        binding.previewBarChart.setCurrentViewport(v);

        previewX(true);
    }

    private void previewX(boolean animate) {
        Viewport tempViewport = new Viewport(binding.barChart.getMaximumViewport());
        float dx = tempViewport.width() / 4;
        tempViewport.inset(dx, 0);
        if (animate) {
            binding.previewBarChart.setCurrentViewportWithAnimation(tempViewport);
        } else {
            binding.previewBarChart.setCurrentViewport(tempViewport);
        }
        binding.previewBarChart.setZoomType(ZoomType.HORIZONTAL);
    }

    private class ViewportListener implements ViewportChangeListener {

        @Override
        public void onViewportChanged(Viewport newViewport) {
            // don't use animation, it is unnecessary when using preview chart because usually viewport changes
            // happens to often.
            binding.barChart.setCurrentViewport(newViewport);
        }

    }

    private void loadPieChart(){
        int numValues = 4;

        List<SliceValue> values = new ArrayList<SliceValue>();
        for (int i = 0; i < numValues; ++i) {
            SliceValue sliceValue = new SliceValue(alarmInfoList.get(i), ChartUtils.pickColor());
            switch(i){
                case 0:
                    sliceValue.setLabel("前倾:"+alarmInfoList.get(i));
                    break;
                case 1:
                    sliceValue.setLabel("后倾:"+alarmInfoList.get(i));
                    break;
                case 2:
                    sliceValue.setLabel("左倾:"+alarmInfoList.get(i));
                    break;
                case 3:
                    sliceValue.setLabel("右倾:"+alarmInfoList.get(i));
                    break;
            }
            values.add(sliceValue);
        }

        pieChartData = new PieChartData(values);
        pieChartData.setHasLabels(true);


//        pieChartData.setHasLabelsOnlyForSelected(true);
// pieChartData.setHasLabelsOutside(true);
        pieChartData.setHasCenterCircle(true);

        int sum = alarmInfoList.stream().mapToInt(Integer::intValue).sum();
        pieChartData.setCenterText1("报警总次数:"+sum);
        // Get roboto-italic font.
//        Typeface tf = Typeface.createFromAsset(this.getAssets(), "Roboto-Italic.ttf");
//        pieChartData.setCenterText1Typeface(tf);

        // Get font size from dimens.xml and convert it to sp(library uses sp values).
        pieChartData.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                (int) getResources().getDimension(R.dimen.pie_chart_text1_size)));

        binding.pieChart.setPieChartData(pieChartData);

    }

    private void getData(String accID, String date){
        apiService.getMeasureTimeStats(accID, date).thenAccept(data -> {
            if(data.size()==3){
                measureTimeStats = data;
                System.out.println("query success");
                Log.e("measureTimeStats", measureTimeStats.toString());
                StatsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.txtMTime.setText("今日总使用时长："+measureTimeStats.get(0).toString());
                        binding.txtATime.setText("今日报警时长："+measureTimeStats.get(1).toString());
                        binding.txtGTime.setText("今日正确坐姿时长："+measureTimeStats.get(2).toString());
                        /**
                         * 加载图表数据
                         * */
                    }
                });
            }else{
                Toast.makeText(StatsActivity.this, "添加时间段失败！", Toast.LENGTH_SHORT).show();
                //保留弹窗？
            }
        }).exceptionally(e -> {
            System.out.println("An error occurred: " + e.getCause());
            Toast.makeText(StatsActivity.this, "添加时间段失败！", Toast.LENGTH_SHORT).show();
            //保留弹窗？
            return null;
        });

        apiService.getAlarmSeg(accID, date).thenAccept(data -> {
            if(data.size()==12){
                alarmSeg = data;
                System.out.println("query success");
                StatsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadColumnChart();
                    }
                });
            }else{
                Toast.makeText(StatsActivity.this, "添加时间段失败！", Toast.LENGTH_SHORT).show();
                //保留弹窗？
            }
        }).exceptionally(e -> {
            System.out.println("An error occurred: " + e.getCause());
            Toast.makeText(StatsActivity.this, "添加时间段失败！", Toast.LENGTH_SHORT).show();
            //保留弹窗？
            return null;
        });

        apiService.getAlarmType(accID, date).thenAccept(data -> {
            if(data.size()==4){
                alarmInfoList = data;
                Log.e("data3", alarmInfoList.toString());
                System.out.println("query success");
                StatsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * 加载图表数据
                         * */
                        loadPieChart();
                    }
                });
            }else{
                //Toast.makeText(StatsActivity.this, "添加时间段失败！", Toast.LENGTH_SHORT).show();
                //保留弹窗？
            }
        }).exceptionally(e -> {
            System.out.println("An error occurred: " + e.getCause());
            //Toast.makeText(StatsActivity.this, "添加时间段失败！", Toast.LENGTH_SHORT).show();
            //保留弹窗？
            return null;
        });
    }

}
