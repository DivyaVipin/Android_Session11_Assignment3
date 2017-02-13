package assignment.android.acadgild.extstorageasynchronoustask;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button delete,add;
    TextView txtViewContent;
    EditText editTextData;
    static String FILENAME = "data.txt";
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        delete=(Button)findViewById(R.id.btnDelete);
        add=(Button)findViewById(R.id.btnAdd);
        editTextData=(EditText)findViewById(R.id.editTextData);
        txtViewContent=(TextView)findViewById(R.id.txtViewContent);
         file = new File(Environment.getExternalStorageDirectory().getPath()+"/data.txt");//Path of data.txt in sd card
        if (file.exists()) {//Check data.txt is present in sdcard
            Toast.makeText(MainActivity.this, "File already exists !!!", Toast.LENGTH_LONG).show();
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String value = editTextData.getText().toString();
                    editTextData.setText("");
                    Operations rf = new Operations(file);//Creating an object of inner class asynchronous task   and passing file object
                    rf.execute(value);//Executing data in editText field

                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean deleted = file.delete();//Deleting file retuens a boolean value
                    if(deleted)
                    {
                        Toast.makeText(MainActivity.this, "File deleted successfully", Toast.LENGTH_LONG).show();
                        txtViewContent.setText("");
                    }
                }
            });
        }
        else
        {
            try {
                checkPermission();//If file not present check the permission and finally granting permission to write
                if (!checkPermission()) {

                    requestPermission();


                }

                file.createNewFile();//Creates a file
                Toast.makeText(MainActivity.this, "File created successfully,Now you can enter data", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }//End of onCreate()
    private boolean checkPermission()
    {

        int result = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

       // Toast.makeText(MainActivity.this, "Checking permission", Toast.LENGTH_LONG).show();
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private boolean requestPermission()
    {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},   //request specific permission from user
                10);
        //Toast.makeText(MainActivity.this, "Request granted", Toast.LENGTH_LONG).show();
        return true;
    }
    private class Operations extends AsyncTask<String,Integer,String>
    {
        File f;
        public Operations(File f) {//Passed a file object in MainActivity class for asynchronous class to operate and also string to execute
        super();
        this.f = f;
        // TODO Auto-generated constructor stub
        }

        @Override
        protected String doInBackground(String... str) {
            FileWriter writer=null;
            String enter = "\n";
            try {
                writer = new FileWriter(file,true);
               // String data=str[0].toString();
                writer.append(str[0].toString());
                writer.append(enter);
                //Toast.makeText(MainActivity.this, "Written to file"+data, Toast.LENGTH_LONG).show();
                writer.flush();//Flushing the data to make understand reached end of data
                } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                } finally {
                try {
                    writer.close();
                    } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    }
                }
            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String name = "";
            StringBuilder sb = new StringBuilder();
            FileReader fr = null;
            try {
                fr = new FileReader(f);//Getting the file from sd card
                BufferedReader br = new BufferedReader(fr);//Reading the contents using Buffered Reader
                while ((name = br.readLine()) != null) {//
                    sb.append(name);
                }
                br.close();
                fr.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            txtViewContent.setText(sb.toString());
        }

    }

}
