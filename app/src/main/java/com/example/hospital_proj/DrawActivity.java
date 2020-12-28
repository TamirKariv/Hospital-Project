package com.example.hospital_proj;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;     
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.io.File;




public class DrawActivity extends AppCompatActivity {
    // Global start time param.
    long startTime;
    // Global repeat counter param.
    int repeat_counter;
    // Global queue to store last 10 shapes.
    Queue<String> last_10_shapes = new LinkedList<>();
    // Holds some random tips for users when drawing.
    String[] tips = new String[6];
    // The folder name.
    String folderName;

    // Loads the draw activity layout.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout xml, relative layout will add the canvas for drawing.
        // Canvas located at MyDrawView class.
        setContentView(R.layout.activity_draw);
        // Start the timer.
        startTime = System.currentTimeMillis();
        // Set the repeat counter to 0.
        repeat_counter = 0;
        // Load the tips.
        tips[0] = "How about trying to draw an animal ?";
        tips[1] = "How about trying to draw a vehicle ?";
        tips[2] = "How about trying to draw a person ?";
        tips[3] = "How about trying to draw a character you like ?";
        tips[4] = "How about trying to draw something that's in the room ?";
        tips[5] = "How about trying to draw a house ?";
        // Get the values from the main activity.
        Intent intent = getIntent();
        // Get the last 4 digits of the ID if exist.
        String quickDigits = null;
        if (intent.hasExtra("4_digits")) {
            quickDigits = intent.getStringExtra("4_digits");
        }
        String name = intent.getStringExtra("name");
        String id = intent.getStringExtra("id");
        Boolean left = intent.getBooleanExtra("left", false);
        Boolean right = intent.getBooleanExtra("right", false);
        // Get the age and gender.
        String age = intent.getStringExtra("age");
        String gender = intent.getStringExtra("gender");
        String current_hand;
        // Convert used hand to string.
        String id_folder;
        if (quickDigits!=null){
            id_folder = quickDigits;
        }else {
            id_folder = id.substring(id.length() - 4);
        }
        if (left) {
            current_hand = "Left_" + id_folder;
        } else {
            current_hand = "Right_" + id_folder;
        }
        // Get the context.
        Context context = this;
        // Get the path of the photo folder and save the new folder there.
        String photoDir = Environment.getExternalStorageDirectory() + "/"
                + Environment.DIRECTORY_DCIM + "/";
        // Create the name of the folder for each user.
        // Get folder ID.
        String folder_name = "";
        // Regular log in.
        if (quickDigits == null) {
            folder_name = gender + "_" + age + "_" + id;
        }
        // Quick login, find the correct folder.
        if (quickDigits != null) {
            File folder = new File(photoDir);
            File[] listOfFiles = folder.listFiles();
            for(int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isDirectory()) {
                    String foler = listOfFiles[i].getName();
                    String lastFourDigits="";
                    try {
                        lastFourDigits = foler.substring(foler.length() - 4);
                    }catch (Exception e){
                        continue;
                    }
                    if (lastFourDigits.equals(quickDigits)) {
                        folder_name = foler;
                        break;
                    }
                }
            }
        }
        File folder = new File(photoDir + folder_name);
        // Create the folder, save result to boolean.
        boolean fol = folder.mkdir();
        // Create sub folder.
        File subFolder = new File(photoDir + folder_name + "/" + current_hand);
        boolean subFol = subFolder.mkdir();
        // Set the global folder name for later use.
        folderName = folder_name + "/" + current_hand;
    }

    // When the button is pressed, move to the finish activity.
    public void endDraw(View v) {
        Intent intent = new Intent(this, FinishActivity.class);
        // Get the end time.
        long endTime = System.currentTimeMillis();
        // Convert to minutes and seconds.
        long total_seconds = (endTime - startTime) / 1000;
        long hours = total_seconds / 3600;
        long minutes = (total_seconds % 3600) / 60;
        long seconds = total_seconds % 60;


        String timersc = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        Date now = new Date();
        android.text.format.DateFormat.format("h:mm a dd MMMM yyyy", now);
        // Create the sessions folder.
        String session = "Session" + now;
        String dir_path = Environment.getExternalStorageDirectory() + "/"
                + Environment.DIRECTORY_DCIM + "/" +
                folderName;
        File folder = new File(dir_path);
        File sub = new File(dir_path+"/Sessions");
        if (!sub.exists()){
            boolean fol = sub.mkdir();
        }
        String mPath = Environment.getExternalStorageDirectory() + "/"
                + Environment.DIRECTORY_DCIM + "/" +
                folderName + "/Sessions" + "/"  + session + ".txt";
        mPath = mPath.replace(":",";");
        File time_file = new File(mPath);
        try {
            FileWriter fileWriter = new FileWriter(time_file);
            fileWriter.write(timersc);
            fileWriter.close();
        } catch (Exception e){
            e.printStackTrace();
        }




        intent.putExtra("time1", timersc);

        startActivity(intent);
    }

    // When the button is pressed, show instructions.
    public void showInfo(View v) {
        // Create the alert.
        AlertDialog alertDialog = new AlertDialog.Builder(DrawActivity.this).create();
        alertDialog.setTitle("Tips & Info !");
        // Choose a random tips from the list.
        Random r = new Random();
        int randomTipIndex= r.nextInt(tips.length);
        String randomTip = tips[randomTipIndex];
        alertDialog.setMessage("Random drawing tip !\n"  + randomTip + "\n\n" +
                "How to use:\n1. Use one finger to draw on top of the shape.\n" +
                "2. Use two fingers to change the color randomly.\n" +
                "3. Use three fingers to clear the screen before going to the next shape.\n" +
                "4. You can also press the circle to clear the screen.\n"+
                "5. Press the left square to increase the font size.\n" +
                "6. Press the right square to decrease the font size.");

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Got it !",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // Show the alert window.
        alertDialog.show();
    }

    // Displays the next shape when pressing the button.
    public void nextShape(View v) {
        // Save the screenshot to the correct folder.
        Date now = new Date();
        android.text.format.DateFormat.format("h:mm a dd MMMM yyyy", now);
        try {
            // Image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory() + "/"
                    + Environment.DIRECTORY_DCIM + "/" +
                    folderName + "/"  + now + ".jpg";
            mPath = mPath.replace(":",";");
            // Create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            File imageFile = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            // Give time to save screenshot.
            Thread.sleep(2000);
        }
        catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
        // Get the image from the view.
        ImageView shape_image = findViewById(R.id.shape_image);
        // Choose a random image from the shapes to display.
        Random rand = new Random();
        // *********************************** INCREASE THE RANGE WHEN ADDING MORE PICTURES *******
        int shape_index = rand.nextInt((99 - 1) + 1) + 1;
        // Create the shape file name using the random number appended to the character 'p'.
        String shape_select = "p" + shape_index;
        // If the current shape is the same as the first one in the queue, select again to avoid
        // repeating the same shape more than once.
        while (shape_select.equals(last_10_shapes.peek())) {
            // *********************************** INCREASE THE RANGE WHEN ADDING MORE PICTURES ***
            shape_index = rand.nextInt((99 - 1) + 1) + 1;
            // Create the shape file name using the random number appended to the character 'p'.
            shape_select = "p" + shape_index;
        }
        // If the repeat counter is 10, repeat one of the previous 10 random shapes.
        if (repeat_counter == 10 && last_10_shapes.size() > 0) {
            // Get the first shape in the queue.
            String current_shape = last_10_shapes.remove();
            // Set the image using the shape removed from the queue.
            Context context = shape_image.getContext();
            int id = context.getResources().getIdentifier(current_shape, "drawable",
                    context.getPackageName());
            shape_image.setImageResource(id);
            // Reset the repeat counter.
            repeat_counter = 0;
            return;
        }
        // Display that randomly selected picture on the image view.
        // Get the picture resource based on the string name created.
        Context context = shape_image.getContext();
        int id = context.getResources().getIdentifier(shape_select, "drawable",
                context.getPackageName());
        // Set the image.
        shape_image.setImageResource(id);
        // Increase the repeat counter.
        repeat_counter ++;
        // If the size of the current queue is 10, remove the first one in the queue.
        if (last_10_shapes.size() >= 10) {
            last_10_shapes.remove();
        }
        // Add the currently displayed shape to the queue.
        last_10_shapes.add(shape_select);
    }
}
