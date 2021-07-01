package com.example.filemanager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class AddNewFolderDialog extends DialogFragment {

    private AddNewFolderCallback addNewFolderCallback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        addNewFolderCallback = (AddNewFolderCallback) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_new_folder, null);
        TextInputLayout etlName = view.findViewById(R.id.etl_addNewFolder_name);
        MaterialButton btnCreate = view.findViewById(R.id.btn_addNewFolder_create);
        btnCreate.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(etlName.getEditText().getText().toString().trim())) {
                addNewFolderCallback.onCreateFolderButtonClick(etlName.getEditText().getText().toString());
                dismiss();
            } else etlName.setError("Folder name can't be empty");
        });
        return builder.setView(view).create();
    }

    public interface AddNewFolderCallback {
        void onCreateFolderButtonClick(String folderName);
    }
}
