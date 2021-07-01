package com.example.filemanager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {

    private List<File> files;
    private List<File> filteredFiles;
    private FileItemEventListener fileItemEventListener;
    private ViewType viewType = ViewType.ROW;

    public FileAdapter(List<File> files, FileItemEventListener fileItemEventListener) {
        this.files = new ArrayList<>(files);
        this.filteredFiles = this.files;
        this.fileItemEventListener = fileItemEventListener;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FileViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                viewType == ViewType.ROW.getValue() ? R.layout.item_file : R.layout.item_file_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FileAdapter.FileViewHolder holder, int position) {
        holder.bindFile(filteredFiles.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredFiles.size();
    }

    public class FileViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private ImageView ivIcon, ivMore;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_file_name);
            ivIcon = itemView.findViewById(R.id.iv_file_icon);
            ivMore = itemView.findViewById(R.id.iv_file_more);

        }

        public void bindFile(File file) {
            if (file.isDirectory())
                ivIcon.setImageResource(R.drawable.ic_folder_black_32dp);
            else
                ivIcon.setImageResource(R.drawable.ic_file_black_32dp);

            tvName.setText(file.getName());
            itemView.setOnClickListener(v -> fileItemEventListener.onFileItemClick(file));
            ivMore.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.menu_file_item, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.menuItem_delete:
                            fileItemEventListener.onDeleteItemClick(file);
                            break;
                        case R.id.menuItem_copy:
                            fileItemEventListener.onCopyItemClick(file);
                            break;
                        case R.id.menuItem_move:
                            fileItemEventListener.onMoveItemClick(file);
                            break;
                    }
                    return true;
                });
            });
        }
    }

    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
        notifyDataSetChanged();
    }

    public void addFile(File file) {
        files.add(0, file);
        notifyItemInserted(0);
    }

    public void deleteFile(File file) {
        int index = files.indexOf(file);
        if (index > -1) {
            files.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void search(String query) {
        if (!TextUtils.isEmpty(query)) {
            List<File> result = new ArrayList<>();
            for (File file : files) {
                if (file.getName().toLowerCase().contains(query.toLowerCase())) {
                    result.add(file);
                }
            }
            filteredFiles = result;
        } else {
            this.filteredFiles = this.files;
        }
        notifyDataSetChanged();
    }

    public interface FileItemEventListener {
        void onFileItemClick(File file);

        void onDeleteItemClick(File file);

        void onCopyItemClick(File file);

        void onMoveItemClick(File file);
    }

    @Override
    public int getItemViewType(int position) {
        return viewType.getValue();
    }
}
