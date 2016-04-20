package etsii.cm.amigoinvisible;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.Collections;

import adaptador.ListadoComprados_Adapter;
import comparator.WishByBoughtAndText;
import comparator.WishByText;
import dbms.RunInDB;
import model.ClsEvent;
import model.ClsMyFriend;
import model.ClsWish;
import utils.Comunicador;

public class MyFriend_Activity extends AppCompatActivity implements Serializable {

    private RunInDB db = new RunInDB();
    private ListView    listado;
    private ClsMyFriend myFriend;
    private ClsEvent    eventActual;
    private ClsWish     wishActual;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friend);
        listado = (ListView) findViewById(R.id.listItemView);

        TextView txtVwLisWishestIs = (TextView) findViewById(R.id.txtVwLisWishestIs);
        txtVwLisWishestIs.setText("Su lista de deseos es:");

        listado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView adapterView, View view, int i, long l) {
                Intent nextView = new Intent(getApplicationContext(), WishDetail_Activity.class);
                wishActual = myFriend.getData_wish().get(i);
                Comunicador.setObjeto(wishActual);
                startActivity(nextView);
            }
        });

        eventActual = (ClsEvent) Comunicador.getObjeto();
        wishActual = null;
        getData();

    }

    protected void onResume() {
        super.onResume();
        if(wishActual!=null) {
            Collections.sort(myFriend.getData_wish(), new WishByBoughtAndText());
            showData();
        }
    }

    public void getData (){
        Thread tr = new Thread(new Runnable() {
            @Override
            public void run() {
                myFriend = db.getMyFriend(eventActual.getData_id_event());
                runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                showData();
                            }
                        }
                );
            }
        });
        tr.start();
    }

    public void showData(){
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        TextView  txtVwFriendName  = (TextView) findViewById(R.id.txtVwFriendName);
        ImageView imgVwFriendPhoto = (ImageView)findViewById(R.id.imgVwFriendPhoto);

        txtVwFriendName.setText(myFriend.getData_person().getData_name());
        imgVwFriendPhoto.setImageBitmap(myFriend.getData_person().getData_photo());

        ListadoComprados_Adapter adapter = new ListadoComprados_Adapter(this, myFriend);
        listado.setAdapter(adapter);

    }
}