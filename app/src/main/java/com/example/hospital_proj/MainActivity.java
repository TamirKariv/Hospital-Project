package com.example.hospital_proj;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {

    // Load the main activity layout.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Add support to android marshmallow and above.
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 00);
        // Hide the keyboard when starting.
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // Create.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Populate gender select spinner.
        String[] users = {"Male", "Female", "Other"};
        Spinner spin = (Spinner) findViewById(R.id.gender_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, users);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
    }

    // The button press method. When pressed move to the drawing activity.
    public void startDraw(View v) {
        Intent intent = new Intent(this, DrawActivity.class);
        // Get the name and ID of the user.
        EditText name = findViewById(R.id.editText_name);
        EditText id = findViewById(R.id.editText_id);
        String user_name = name.getText().toString();
        String user_id = id.getText().toString();
        // Get the checkbox values to check what hand the user selected.
        CheckBox right = findViewById(R.id.checkBox_right);
        CheckBox left = findViewById(R.id.checkBox_left);
        Boolean right_selected = right.isChecked();
        Boolean left_selected = left.isChecked();
        // Get the age and gender.
        EditText age = findViewById(R.id.editText_age);
        String user_age = age.getText().toString();
        Spinner spinner = findViewById(R.id.gender_spinner);
        String user_gender = spinner.getSelectedItem().toString();
        // Pass the values to the draw activity.
        intent.putExtra("name", user_name);
        intent.putExtra("id", user_id);
        intent.putExtra("right", right_selected);
        intent.putExtra("left", left_selected);
        // Send the age and gender.
        intent.putExtra("age", user_age);
        intent.putExtra("gender", user_gender);
        Context context = this;
        // Show registration error unless all fields are properly filled.
        if (!user_id.equals("") && !user_name.equals("") && !user_age.equals("") &&
                (right_selected || left_selected)) {
            // Get the last 4 digits.
            String last_4_digits = user_id.substring(user_id.length() - 4);
            // Save user ID in a file in the internal storage for quick access login later.
            String mPath = Environment.getExternalStorageDirectory() + "/"
                    + Environment.DIRECTORY_DCIM + "/IDs";
            File file = new File(mPath);
            if (!file.exists()) {
                boolean fol = file.mkdir();
            }
            File sub = new File(mPath + "/" + "user_id.txt");
            try {
                FileWriter fileWriter = new FileWriter(sub, true);
                BufferedWriter bw = new BufferedWriter(fileWriter);
                PrintWriter pw = new PrintWriter(bw);
                pw.println(last_4_digits);
                pw.close();
                bw.close();
                fileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


            // Start the activity if everything worked properly.
            startActivity(intent);
        }
        // If some details are missing show an error alert, forcing the user to try again.
        else {
            // Create the alert.
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("missing details alert");
            alertDialog.setMessage("Please fill in all the required details!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            // Show the alert window.
            alertDialog.show();
        }
    }

    // Starts the drawing activity using the quick logging method.
    public void startQuickDraw(View v) {
        // Get the checkbox values to check what hand the user selected.
        CheckBox right = findViewById(R.id.checkBox_right);
        CheckBox left = findViewById(R.id.checkBox_left);
        Boolean right_selected = right.isChecked();
        Boolean left_selected = left.isChecked();
        // Alert if not selected hand for quick login.
        if (!right_selected && !left_selected) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("no hand selected alert");
            alertDialog.setMessage("Please select a hand for quick login !");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
            return;
        }
        Context context = this;
        boolean check_ifuser_exist = false;
        // Get the digits of the ID and start the next activity.
        // Also get the checkbox values.
        Intent intent = new Intent(this, DrawActivity.class);
        intent.putExtra("right", right_selected);
        intent.putExtra("left", left_selected);
        EditText last_4_digits = findViewById(R.id.editText_quick);
        String digits = last_4_digits.getText().toString();
        intent.putExtra("4_digits", digits);
        // Find the user if already registered.
        try {
            String mPath = Environment.getExternalStorageDirectory() + "/"
                    + Environment.DIRECTORY_DCIM + "/IDs";
            File file = new File(mPath + "/user_id.txt");
            FileReader fr = new FileReader(file);
            BufferedReader bf = new BufferedReader(fr);
            String line = bf.readLine();
            while (line != null) {
                if (line.equals(digits)) {
                    startActivity(intent);
                    check_ifuser_exist = true;
                    break;
                }
                line = bf.readLine();
            }
        }
        // Catch file handling exception.
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        // If the user does not exist show a dialog box prompting registration.
        if (!check_ifuser_exist) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("user not found alert");
            alertDialog.setMessage("Could not find your ID, please register above !");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }

    // If a checkbox is selected while another is selected, disable the other one.
    public void unCheckLeft(View v) {
        CheckBox right = findViewById(R.id.checkBox_right);
        CheckBox left = findViewById(R.id.checkBox_left);
        if (right.isChecked()) {
            left.setChecked(false);
        }
    }

    public void unCheckRight(View v) {
        CheckBox right = findViewById(R.id.checkBox_right);
        CheckBox left = findViewById(R.id.checkBox_left);
        if (left.isChecked()) {
            right.setChecked(false);
        }
    }
}
