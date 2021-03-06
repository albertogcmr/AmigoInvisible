package etsii.cm.amigoinvisible;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

import dbms.RunInDB;
import model.ClsWish;
import utils.Comunicador;
import utils.Iam;
import utils.Photo;

public class WishEdit_Activity extends AppCompatActivity implements Serializable{
    private RunInDB db = new RunInDB();
    private ClsWish   actualWish;
    private ImageView imgPhoto;
    private TextView  txtText;
    private TextView  txtDescription;
    private String    selectedImagePath;

    private static final int SELECT_PICTURE = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.wish_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.opcWishSave) {
            hideSoftKeyboard();
            saveWish();
            Toast.makeText(WishEdit_Activity.this, "Deseo guardado", Toast.LENGTH_SHORT).show();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actualWish = (ClsWish) Comunicador.getObjeto();

        if (actualWish.getData_text().equals("")){
            setTitle("Deseo nuevo");
        } else {
            setTitle(actualWish.getData_text());
        }

        setContentView(R.layout.activity_wish_edit);

        imgPhoto       = (ImageView) findViewById(R.id.imgVwWishEditPhoto);
        txtText        = (TextView)  findViewById(R.id.txtVwWishEditText);
        txtDescription = (TextView)  findViewById(R.id.txtVwWishEditDescription);

        imgPhoto.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                    }
                });

        mostrarDatos();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                imgPhoto.setImageURI(selectedImageUri);
                actualWish.setData_file_path(Photo.getPath(selectedImageUri, this));
            }
        }
    }

    private void saveWish(){
        if (txtText.getText().length() > 0) {
            actualWish.setData_text(txtText.getText().toString());
            actualWish.setData_description(txtDescription.getText().toString());
            actualWish.setData_photo(((BitmapDrawable) imgPhoto.getDrawable()).getBitmap());
            Comunicador.setObjeto(actualWish);
            Thread tr = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (actualWish.getData_id_wish() == 0)
                    db.insWish(actualWish, Iam.getId());
                else
                    db.setWish(actualWish);
                }
            });
            tr.start();
        }
    }

    private void mostrarDatos(){

        if (actualWish.getData_photo() != null) {
            imgPhoto.setImageBitmap(actualWish.getData_photo());
        }
        if (actualWish.getData_text().length() > 0) {
            txtText.setText(actualWish.getData_text());
        }
        if (actualWish.getData_description().length() > 0) {
            txtDescription.setText(actualWish.getData_description());
        }
    }

    private void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

}
