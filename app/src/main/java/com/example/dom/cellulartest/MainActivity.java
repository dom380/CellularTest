package com.example.dom.cellulartest;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Resources res = getResources();
        final Grid testGrid = new Grid(res.getInteger(R.integer.gridHeight),res.getInteger(R.integer.gridWith),res.getFraction(R.fraction.seedPercentage,1,1));
        testGrid.seedGrid();
        testGrid.runCA(4,5,8);
        testGrid.addStartEndGoals();
        //List<Cell> path = testGrid.checkPath();
        DrawingView drawingView = new DrawingView(getApplicationContext(), testGrid);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_main);
        layout.addView(drawingView);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testGrid.runCA(2,5,8);
            }
        });
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testGrid.reSeedGrid();
            }
        });

        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testGrid.resetPath();
                testGrid.checkPath();
            }
        });
        Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testGrid.resetPath();
                testGrid.checkPathJPS(false);
            }
        });
        Button button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testGrid.resetPath();
                testGrid.checkPathJPS(true);
            }
        });
    }
}
