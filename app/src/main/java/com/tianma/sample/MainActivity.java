package com.tianma.sample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.tianma.zip.R;
import com.tianma.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private Executor threadPool;

    private String srcPath;
    private String destPath;

    private EditText srcEditText;
    private EditText destEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    private void init() {
        findViewById(R.id.source_btn).setOnClickListener(mListener);
        findViewById(R.id.dest_btn).setOnClickListener(mListener);
        findViewById(R.id.zip_btn).setOnClickListener(mListener);
        findViewById(R.id.unzip_btn).setOnClickListener(mListener);

        srcEditText = (EditText) findViewById(R.id.source_edit_text);
        destEditText = (EditText) findViewById(R.id.dest_edit_text);

        threadPool = Executors.newSingleThreadExecutor();

    }


    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.source_btn:
                    chooseSource();
                    break;
                case R.id.dest_btn:
                    chooseDestination();
                    break;
                case R.id.zip_btn:
                    doZip();
                    break;
                case R.id.unzip_btn:
                    doUnZip();
                    break;
            }
        }
    };

    private void chooseSource() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_AND_DIR_SELECT;

        FilePickerDialog dialog = new FilePickerDialog(this, properties);
        dialog.show();
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                srcPath = files[0];
                srcEditText.setText(srcPath);
            }
        });

    }

    private void chooseDestination() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_AND_DIR_SELECT;

        FilePickerDialog dialog = new FilePickerDialog(this, properties);
        dialog.show();
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                destPath = files[0];
                destEditText.setText(destPath);
            }
        });
    }

    private void doZip() {
        new ZipTask().executeOnExecutor(threadPool);
    }

    private void doUnZip() {
        new UnzipTask().executeOnExecutor(threadPool);
    }

    private class ZipTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                ZipUtil.zip(new File(destPath), new File(srcPath));
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            Toast.makeText(MainActivity.this, bool ? "Compress Success ^_^" : "Compress Failed T.T", Toast.LENGTH_SHORT).show();
            super.onPostExecute(bool);
        }
    }

    private class UnzipTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (TextUtils.isEmpty(srcPath)) {
                Toast.makeText(MainActivity.this, "Plz choose a zip file", Toast.LENGTH_SHORT).show();
                return false;
            }

            File srcFile = new File(srcPath);
            if (!srcFile.isFile()) {
                Toast.makeText(MainActivity.this, "Plz choose a file", Toast.LENGTH_SHORT).show();
                return false;
            }

            try {
                if (TextUtils.isEmpty(destPath)) {
                    ZipUtil.unzip(srcFile);
                } else {
                    ZipUtil.unzip(srcFile, new File(destPath));
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            Toast.makeText(MainActivity.this, bool ? "Decompress Success ^_^" : "Decompress Failed T.T", Toast.LENGTH_SHORT).show();
            super.onPostExecute(bool);
        }
    }


}
