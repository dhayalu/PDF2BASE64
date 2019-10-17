package com.tech.cloudnausor.balapdfbase64;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    Button getfile,downlaod;
    TextView base64code;
    EditText filepathedit;
    private int PICK_PDF_REQUEST = 1;
    private Uri filePath;

String pdfPath;
    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getfile=(Button)findViewById(R.id.getfile);
        downlaod =(Button)findViewById(R.id.downlaod);
        base64code=(TextView)findViewById(R.id.base64code);
        filepathedit =(EditText) findViewById(R.id.filepath);

        getfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
        downlaod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download();
            }
        });

    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_PDF_REQUEST);
    }

    public void download(){

        // Bala -> filename
        //check manifests file write
        try {
            File dwldsPath = new File("/storage/emulated/0/Download/" + "Bala"+".pdf");
            byte[] pdfAsBytes = Base64.decode(base64code.getText().toString(), 0);
            Log.d("data", "onActivityResult: pdfAsBytes size="+pdfAsBytes.length);
            FileOutputStream os;
            os = new FileOutputStream(dwldsPath, false);
            os.write(pdfAsBytes);
            os.flush();
            os.close();
            Toast.makeText(this,"Dowload Succesful",Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("error", "onActivityResult: " + e.toString());
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            File file = new File(filePath.toString());
            filepathedit.setText(filePath.toString());

            Log.d("data", "onActivityResult: uri"+filePath.toString());
            //            myFile = new File(uriString);
            //            ret = myFile.getAbsolutePath();
            //Fpath.setText(ret);

// hi dhaya, here you get file name
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream in = getContentResolver().openInputStream(filePath);

                byte[] bytes=getBytes(in);
                Log.d("data", "onActivityResult: bytes size="+bytes.length);

                Log.d("data", "onActivityResult: Base64string="+ Base64.encodeToString(bytes,Base64.DEFAULT));

                base64code.setText(Base64.encodeToString(bytes,Base64.DEFAULT));

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                Log.d("error", "onActivityResult: " + e.toString());
            }


            if (filePath.toString() != null) {
                Log.d("Path: ", filePath.toString());
                pdfPath = filePath.toString();
                String filename = pdfPath.substring(pdfPath.lastIndexOf("/")+1);
                filename = filename.replace("%20"," ");
            }
        }
    }


    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }


    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
