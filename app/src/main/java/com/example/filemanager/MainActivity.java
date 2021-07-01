package com.example.filemanager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButtonToggleGroup;

import java.io.File;

public class MainActivity extends AppCompatActivity implements AddNewFolderDialog.AddNewFolderCallback {

    private ImageView ivAddNewFolder;
    private EditText etSearch;
    private MaterialButtonToggleGroup toggleGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        if (StorageHelper.isExternalStorageReadable()) {
            File externalFilesDir = getExternalFilesDir(null);
            listFiles(externalFilesDir.getPath(), false);
        }

        ivAddNewFolder.setOnClickListener(v -> {
            AddNewFolderDialog dialog = new AddNewFolderDialog();
            dialog.show(getSupportFragmentManager(), null);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_main_fragmentContainer);
                if (fragment instanceof FileListFragment) {
                    ((FileListFragment) fragment).search(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (checkedId == R.id.btn_main_list && isChecked) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_main_fragmentContainer);
                if (fragment instanceof FileListFragment) {
                    ((FileListFragment) fragment).setViewType(ViewType.ROW);
                }
            } else if (checkedId == R.id.btn_main_grid && isChecked) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_main_fragmentContainer);
                if (fragment instanceof FileListFragment) {
                    ((FileListFragment) fragment).setViewType(ViewType.GRID);
                }
            }
        });

    }

    private void init() {
        ivAddNewFolder = findViewById(R.id.iv_main_addNewFolder);
        etSearch = findViewById(R.id.et_main_search);
        toggleGroup = findViewById(R.id.toggleGroup_main);
    }

    public void listFiles(String path, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_main_fragmentContainer, FileListFragment.newInstance(path));
        if (addToBackStack)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    public void listFiles(String path) {
        this.listFiles(path, true);
    }

    @Override
    public void onCreateFolderButtonClick(String folderName) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_main_fragmentContainer);
        if (fragment instanceof FileListFragment) {
            ((FileListFragment) fragment).createNewFolder(folderName);
        }
    }
}