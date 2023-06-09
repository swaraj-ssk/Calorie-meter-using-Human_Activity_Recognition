package dspanah.sensor_based_har;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Objects;

public class ResultActivity extends AppCompatActivity {
    private long[]  arrytimer ;
    private String [] tag = {"Running","Downstrairs","Jogging","Sitting","Standing","Upstairs","Walking"};
    long totaltime =0;
    PieChart pieChart;
    Button getresultbtn;
    EditText inputcaledt;
    double BMR,totalcalburn;
      TextView resulttxt , resulttextview2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        pieChart = findViewById(R.id.peichartview_id);
        getresultbtn = findViewById(R.id.getcalorydeficitbrn_id);
        inputcaledt = findViewById(R.id.inputcaloryedt_id);
        resulttxt = findViewById(R.id.textviewlastvalueid);
         resulttextview2 = findViewById(R.id.textview2lastvalueid);


        arrytimer = Objects.requireNonNull(getIntent().
                getExtras()).getLongArray("arrayoftime");
        for(int i =0;i<7;i++){
            totaltime =totaltime+arrytimer[i];
        }

        setuppiechart();
        ArrayList<PieEntry>entries = new ArrayList<>();
       for(int j =0;j<7;j++){
               entries.add(new PieEntry(arrytimer[j],tag[j]));
       }


            ArrayList<Integer>colors = new ArrayList<>();
            for(int color: ColorTemplate.MATERIAL_COLORS){
                colors.add(color);
            }
        for(int color: ColorTemplate.VORDIPLOM_COLORS){
            colors.add(color);
        }
        PieDataSet dataSet = new PieDataSet(entries,"");
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setDrawValues(true);

        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();

        calcualtebmi();
        calorieburncalculate();
        getresultbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputcals = inputcaledt.getText().toString();

                if (TextUtils.isEmpty(inputcals)){
                    inputcaledt.setError("Enter Calorie");
                }else{

                    double deficit = totalcalburn - Double.parseDouble(inputcals);
                    resulttxt.setText(String.valueOf(round(deficit, 2)));
                    resulttextview2.setText(String.valueOf(round(totalcalburn,2)));
                    try {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    } catch(Exception ignored) {
                    }
                }
            }
        });
    }

    private void calorieburncalculate()
    {
         totalcalburn =((arrytimer[0]*288 + arrytimer[1]*275 + arrytimer[2]*216 +arrytimer[3]*80
                +arrytimer[4]*88+arrytimer[5]*470+arrytimer[6]*133)/3600)+BMR;
       // Toast.makeText(this, "totalcalburn"+totalcalburn, Toast.LENGTH_SHORT).show();
    }

    private void calcualtebmi()
    {
        SharedPreferences prefs = getSharedPreferences("Profile", MODE_PRIVATE);
        int idName = prefs.getInt("Gender", 0);
        int idheght = prefs.getInt("Height",165);
        int idweight = prefs.getInt("Wight",52);
        int idage = prefs.getInt("age",22);
        if(idName==0){
            BMR = 88.362 +(13.397*idweight)+(4.799*idheght)-(5.677*idage);

        }else{
            BMR = 447.593 +(9.247*idweight)+(3.098*idheght)-(4.330*idage);
        }
        Toast.makeText(this, "BMR Value is"+BMR, Toast.LENGTH_SHORT).show();
    }

    private void setuppiechart(){
        pieChart.setDrawSliceText(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setCenterText("Spending Activities");
        pieChart.setCenterTextSize(10);
        pieChart.getDescription().setEnabled(false);
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}