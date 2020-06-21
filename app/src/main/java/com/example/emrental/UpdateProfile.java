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
import android.widget.Toast;

import java.util.regex.Pattern;

public class UpdateProfile extends AppCompatDialogFragment {
    private EditText ppedt, fnedt, pedt, emedt;
    private dialogListner listner;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.drawable.dialog_rd);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_update_profile, null);

        builder.setView(view).setTitle("Update profile").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ph = pedt.getText().toString();
                String pp = ppedt.getText().toString();
                String fn = fnedt.getText().toString();
                listner.applyTexts(pp, fn, ph);
                dialog.cancel();
            }
        });
        ppedt = view.findViewById(R.id.paypalEdt);
        pedt = view.findViewById(R.id.phoneEdt);
        fnedt = view.findViewById(R.id.fullnameEdt);
        String fullName = getArguments().getString("FullName");
        String payPal = getArguments().getString("PayPal");
        String phone = getArguments().getString("Phone");
        ppedt.setText(payPal);
        fnedt.setText(fullName);
        pedt.setText(phone);
        return builder.create();
    }

    private boolean validInputs(String ph, String pp, String fn) {
        if (ph.isEmpty() || ph.trim().length() != 10 || ph.contains(" ")) {
            pedt.setError("Invalid Phone Number");
            return false;
        }
        if (!isFullname(fn)) {
            fnedt.setError("Invalid Full Name");
            return false;
        }
        if (isEmail(pp)) {
            ppedt.setError("Invalid PayPal Email");
            return false;
        }
        return true;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listner = (dialogListner) context;

    }

    public interface dialogListner {

        void applyTexts(String paypal, String fullname, String phone);
    }

    public static boolean isFullname(String str) {
        String expression = "^[a-zA-Z\\s]+";
        return str.matches(expression);
    }

    public static boolean isEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}
