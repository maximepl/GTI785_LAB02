package maxandalex.peertopeer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;

import maxandalex.peertopeer.Server.Tasker;

public class PairingView extends AppCompatActivity {

    private Button btn_Back;
    private ListView txt_PairedList;

    private ArrayList<String> pairingNames = new ArrayList<>();
    private String pairIpAdress;
    private String isOnline = "\u25CF"; //"\u25CF"=big bullet "\u25A0"=big square
    //TODO FAIRE EN SORTE QUE LE BULLET SOIT COLORÉ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pairingview);

        btn_Back = findViewById(R.id.btn_back_pairingview);
        txt_PairedList = findViewById(R.id.pairedTextView);

        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //TODO TESTING PURPOSE
        //Contact.contactList.add(new Contact("asdasd", "Richard", "160.105.123.23"));
        /*Spannable span = new SpannableString(testing21);
        span.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, testing21.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        */

        if(Contact.contactList.size() != 0){
            for(int i = 0; i < Contact.contactList.size(); i++){
                if(!Contact.contactList.get(i).isOnline()){
                    isOnline = "\u25A0";
                }

                pairingNames.add(Contact.contactList.get(i).getName() + " | "
                        + Contact.contactList.get(i).getIp() + " | "
                        + Contact.contactList.get(i).getDistance() + " | "
                        + isOnline);
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    pairingNames);

            txt_PairedList.setAdapter(arrayAdapter);
            txt_PairedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    if(pairingNames.get(position).contains("\u25CF")){
                        PopupMenu popMenu = new PopupMenu(PairingView.this,view);
                        popMenu.getMenuInflater().inflate(R.menu.popup_menu, popMenu.getMenu());

                        popMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if(item.getTitle().charAt(0) == 'A'){
                                    //TODO FAIRE LE HANDLER POUR LE PARTAGE DE FICHIER

                                    pairIpAdress = Contact.contactList.get(position).getIp();
                                    //TODO SAVOIR LE LIENS DES AUTRES EQUIPES
                                    new Tasker.GetFiles().execute("http://" + pairIpAdress + "/getFileList/");


                                } else {
                                    //TODO FAIRE LE HANDLER DE PARTAGE DE LISTE DE CONTACT AVEEC BLUETOOTH
                                }
                                return true;
                            }
                        });
                        popMenu.show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Le pair n'est pas en ligne", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Votre liste de contact est vide, veuillez en ajouter", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}