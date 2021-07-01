package com.example.filemanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class FileListFragment extends Fragment implements FileAdapter.FileItemEventListener {

    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private GridLayoutManager gridLayoutManager;

    private TextView tvPath;
    private ImageView ivBack;

    private static final String ARG_PATH = "path";
    private String path;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            path = getArguments().getString(ARG_PATH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_list, container, false);
        init(view);
        gridLayoutManager = new GridLayoutManager(getContext(), 1, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        File currentFolder = new File(path);
        if (StorageHelper.isExternalStorageReadable()) {
            File[] files = currentFolder.listFiles();
            fileAdapter = new FileAdapter(Arrays.asList(files), this);
            recyclerView.setAdapter(fileAdapter);
        }
        tvPath.setText(currentFolder.getName().equalsIgnoreCase("files") ? "External Storage" : currentFolder.getName());
        ivBack.setOnClickListener(v -> getActivity().onBackPressed());
        return view;
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.rv_files);
        tvPath = view.findViewById(R.id.tv_files_path);
        ivBack = view.findViewById(R.id.iv_files_back);
    }

    private File getDestinationFile(String fileNAme) {
        return new File(getContext().getExternalFilesDir(null).getPath() + File.separator + "Destination" + File.separator + fileNAme);
    }

    public void createNewFolder(String folderName) {
        if (StorageHelper.isExternalStorageWritable()) {
            File newFolder = new File(path + File.separator + folderName);
            if (!newFolder.exists()) {
                if (newFolder.mkdir()) {
                    fileAdapter.addFile(newFolder);
                    recyclerView.smoothScrollToPosition(0);
                }
            }
        }
    }

    public void search(String query) {
        if (fileAdapter != null) {
            fileAdapter.search(query);
        }
    }

    private void copy(File source, File destination) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(source);
        FileOutputStream fileOutputStream = new FileOutputStream(destination);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fileInputStream.read(buffer)) > 0) {
            fileOutputStream.write(buffer, 0, length);
        }
        fileInputStream.close();
        fileOutputStream.close();
    }

    public void setViewType(ViewType viewType) {
        if (fileAdapter != null) {
            fileAdapter.setViewType(viewType);
            if (viewType == ViewType.ROW)
                gridLayoutManager.setSpanCount(1);
            else
                gridLayoutManager.setSpanCount(2);
        }
    }

    @Override
    public void onFileItemClick(File file) {
        if (file.isDirectory())
            ((MainActivity) getActivity()).listFiles(file.getPath());
    }

    @Override
    public void onDeleteItemClick(File file) {
        if (file.delete())
            fileAdapter.deleteFile(file);
    }

    @Override
    public void onCopyItemClick(File file) {
        if (StorageHelper.isExternalStorageWritable()) {
            try {
                copy(file, getDestinationFile(file.getName()));
                Toast.makeText(getContext(), "File is copied", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMoveItemClick(File file) {
        if (StorageHelper.isExternalStorageWritable()) {
            try {
                copy(file, getDestinationFile(file.getName()));
                onDeleteItemClick(file);
                Toast.makeText(getContext(), "File is moved", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static FileListFragment newInstance(String path) {
        FileListFragment fragment = new FileListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATH, path);
        fragment.setArguments(args);
        return fragment;
    }
}