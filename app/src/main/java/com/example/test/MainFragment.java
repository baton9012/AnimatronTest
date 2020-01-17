package com.example.test;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MainFragment extends Fragment {

    private DrawFragment.Pt[][] mHexes;
    private List<DrawFragment.Pt> mUsedHex;

    private JSONArray mJSONHexesX;
    private JSONArray mJSONHexesY;
    private JSONArray mJSONUsedHexX;
    private JSONArray mJSONUsedHexY;

    private File file;

    private DrawFragment f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main_fragment, container, false);

        Button mGen = view.findViewById(R.id.generate);
        Button mExp = view.findViewById(R.id.save);
        Button mImp = view.findViewById(R.id.import_json);

        mGen.setText("Gen");
        mGen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f = new DrawFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.draw_fragment, f).commit();
            }
        });
        mExp.setText("Exp");
        mExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHexes = f.getHexs();
                mUsedHex = f.getUsedHex();
                file = new File(android.os.Environment.getExternalStorageDirectory() + "/JSON.json");
                if (mHexes != null) {
                    createJSONFile();
                }
            }
        });
        mImp.setText("Imp");
        mImp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (file != null) {
                    String json = readeJSONFile().toString();
                    setHexes(json);
                    f.invalidate();
                }
            }
        });
        return view;
    }

    private void createJSONFile() {
        JSONObject jsonObject = new JSONObject();
        mJSONHexesX = new JSONArray();
        mJSONHexesY = new JSONArray();
        mJSONUsedHexX = new JSONArray();
        mJSONUsedHexY = new JSONArray();
        try {
            for (int i = 0; i < mHexes.length; i++) {
                for (int j = 0; j < 7; j++) {
                    mJSONHexesX.put(mHexes[i][j]._mX);
                    mJSONHexesY.put(mHexes[i][j]._mY);
                }
            }
            jsonObject.put("hexX", mJSONHexesX);
            jsonObject.put("hexY", mJSONHexesY);
            if (mUsedHex.size() > 0) {
                for (int i = 0; i < mUsedHex.size(); i++) {
                    mJSONUsedHexX.put(mUsedHex.get(i)._mX);
                    mJSONUsedHexY.put(mUsedHex.get(i)._mY);
                }
                jsonObject.put("usedHexX", mJSONUsedHexX);
                jsonObject.put("usedHexY", mJSONUsedHexY);
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonObject.toString());
            fileWriter.flush();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private StringBuilder readeJSONFile() {
        BufferedReader reader;
        StringBuilder content = new StringBuilder();
        try {
            FileReader fileReader = new FileReader(file);
            reader = new BufferedReader(fileReader);
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    private void setHexes(String json) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            mJSONHexesX = jsonObject.getJSONArray("hexX");
            mJSONHexesY = jsonObject.getJSONArray("hexY");
            if (json.contains("usedHexX") && json.contains("usedHexY")) {
                mJSONUsedHexX = jsonObject.getJSONArray("usedHexX");
                mJSONUsedHexY = jsonObject.getJSONArray("usedHexY");
            }
            int j = 0;
            for (int i = 0; i < mJSONHexesX.length(); i = i+7) {
                float x = mJSONHexesX.getInt(i);
                float y = mJSONHexesY.getInt(i);
                f.loadHex(x, y, j, mJSONHexesX.length()/7);
                j++;
            }
            if (mJSONUsedHexX.length() != 0) {
                for (int i = 0; i < mJSONUsedHexX.length(); i++) {
                    float usedHexX = mJSONUsedHexX.getInt(i);
                    float usedHexY = mJSONUsedHexY.getInt(i);
                    f.loadUsedHex(i, usedHexX, usedHexY, mJSONUsedHexX.length());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
