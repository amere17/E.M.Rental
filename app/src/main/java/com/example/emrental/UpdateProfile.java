package com.example.emrental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class UpdateProfile extends AppCompatDialogFragment {
    private EditText ppedt, fnedt, pedt, emedt;
    private dialogListner listner;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_update_profile, null);
        builder.setView(view).setTitle("Update Profile").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String em = emedt.getText().toString();
                String ph = pedt.getText().toString();
                String pp = ppedt.getText().toString();
                String fn = fnedt.getText().toString();
                listner.applyTexts(pp, fn, ph, em);
            }
        });
        ppedt = view.findViewById(R.id.paypalEdt);
        emedt = view.findViewById(R.id.emailEdt);
        pedt = view.findViewById(R.id.phoneEdt);
        fnedt = view.findViewById(R.id.fullnameEdt);
        String fullName = getArguments().getString("FullName");
        String payPal = getArguments().getString("PayPal");
        String email = getArguments().getString("Email");
        String phone = getArguments().getString("Phone");
        ppedt.setText(payPal);
        fnedt.setText(fullName);
        pedt.setText(phone);
        emedt.setText(email);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listner = (dialogListner) context;

    }

    public interface dialogListner {
        void applyTexts(String paypal, String fullname, String phone, String email);
    }
}
