package com.akobets.friendfinder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;


public class MainActivity extends Activity {

    public static final int FRIENDS_DEFAULT = 2;

    public static final int ID_CHOICE = 0;
    public static final int ID_FRIEND_LIST = 1;
    public static final int ID_NON_FRIEND_LIST = 2;
    public static final int ID_DEL_CONTACT = 3;
    public static final int ID_ADD_CONTACT = 4;
    public static final String SP_NAME = "Contact list";
    public static final String CONTACT_SET_KEY = "Contact set key";
    private static final char CONTACT_SEPARATOR = 0x00C0;


    private Button bAddContact;
    private ListView lvMain;
    private ArrayList<Contact> contactList;
    private ContactListAdapter adapter;
    private String[] Name;
    private SharedPreferences sp;
    private TextView tvContactsLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = MainActivity.this;

        Name = getResources().getStringArray(R.array.Name);
        bAddContact = (Button) findViewById(R.id.bAddContact);
        lvMain = (ListView) findViewById(R.id.lvMain);
        tvContactsLabel = (TextView) findViewById(R.id.tvContactsLabel);


        sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);


        if (sp.contains(CONTACT_SET_KEY)) {
            contactList = getArrayListFromSet(sp);
        } else {
            contactList = new ArrayList<>();
            initContactSlotList(contactList);
        }

        adapter = new ContactListAdapter(this, R.layout.item_layout, contactList);
        lvMain.setAdapter(adapter);


        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                Log.d("MyLog", "position " + position + " name " + contactList.get(position).getContact());
                showDialog(ID_CHOICE, bundle);
            }
        });

        lvMain.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                Log.d("MyLog", "position " + position + " name " + contactList.get(position).getContact());
                showDialog(ID_DEL_CONTACT, bundle);
                return false;
            }
        });

        bAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Pressed button Add Contact", Toast.LENGTH_SHORT).show();
                showDialog(ID_ADD_CONTACT);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addName(ArrayList<Contact> contactList, String newName) {
        boolean duplication = false;
        for (int i = 0; i < contactList.size(); i++) {
            if (newName.equals(contactList.get(i).getContact())) {
                duplication = true;
                break;
            }
        }
        if (duplication == false) {
            Contact contact = new Contact();
            contact.setContact(newName);
            contactList.add(contact);
            contactList.trimToSize();
        } else {
            Toast.makeText(getApplicationContext(), "Запись не будет сохранёна, т.к. контакт с таким именем уже существует.", Toast.LENGTH_LONG).show();
        }
    }

    public void delContact(ArrayList<Contact> contactList, int index) {
        Log.d("MyLog", "contactList.size =" + contactList.size() + "position " + index + " name " + contactList.get(index).getContact());
        contactList.remove(index);
        contactList.trimToSize();
    }

    public void initContactSlotList(ArrayList<Contact> contactList) {
        contactList.clear();
        int arrayLength = Name.length;

        Contact[] contact = new Contact[arrayLength];
        for (int i = 0; i < arrayLength; i++) {
// ??????????????? ?????????? ?????????? ?? ??????? Name
            contact[i] = new Contact();
            contact[i].setContact(Name[i]);
//            Log.d("MyLog", "contact[i].setContact(Name[i])" + contact[i].getContact());
//            ???????? ?? ??????? ?????????? ????????????? ???????? ? ??????? ? ??????????????
            for (int j = 0; j < i; j++)
                for (int z = 0; z < contact[j].getFriendsList().size(); z++) {
                    if (contact[i].getContact() == contact[j].getFriendsList().get(z)) {
                        contact[i].addFriendsContact(contact[j].getContact());
                    }
                }
// ??????????????? ?????????? ?????????? ?? ??????? Foto
// ????? ?????? ??? ????? ????????
            contact[i].setIvFotoId(getResources().getIdentifier("foto" + i, "drawable", getPackageName()));
//            Log.d("MyLog", "Id FOTO " + i + " " + getResources().getIdentifier("foto" + i, "drawable", getPackageName()));


//  ??? 1. ????????? ?????? ???? ?? ?????? ????????? ??????, ???? ?? ?????? ???? ??? ? ?? ?????? ????? ???????????? ??????
            ArrayList<String> tempStringList = new ArrayList<>();
            for (int j = 0; j < arrayLength; j++) {
                if (contact[i].getContact() != Name[j]) {
                    if (contact[i].getFriendsList().size() == 0) {
                        tempStringList.add(Name[j]);
                    } else {
                        for (int z = 0; z < contact[i].getFriendsList().size(); z++) {
                            if (Name[j] != contact[i].getFriendsList().get(z)) {
                                tempStringList.add(Name[j]);
                            }
                        }
                    }
                }
            }
//  ??? 2. ???????? ???????? ??? ?? ?????????????? ??????, ??????? ? ??????   ?????? ??????? ??????
            int tempIndex;
//            ArrayList<String> tempFriendsList = new ArrayList<>();
            for (int j = contact[i].getFriendsList().size(); j < FRIENDS_DEFAULT; j++) {
                tempIndex = (int) (Math.random() * (tempStringList.size() - 1));
                contact[i].addFriendsContact(tempStringList.get(tempIndex));
//    ????????? - ???? ? ?????? ?????? ??????? ?????? ?????? ??? ?????????????? ??????, ?? ????????? ? ?????? ?????? ????? ??????, ??? ???????? ????????
                for (int y = 0; y < i; y++) {
                    if (contact[y].getContact() == tempStringList.get(tempIndex)) {
                        contact[y].addFriendsContact(contact[i].getContact());
                    }
                }
                tempStringList.remove(tempIndex);
            }
            contactList.add(i, contact[i]);
        }
    }


    @Override
    protected Dialog onCreateDialog(int id, final Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        boolean checkedItem[] = null;
        DialogInterface.OnMultiChoiceClickListener omccl = null;

        switch (id) {
            case ID_CHOICE:
                LayoutInflater liCD = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View vCD = liCD.inflate(R.layout.choice_dialog, null);

                Button bAddFriends = (Button) vCD.findViewById(R.id.bAddFriends);
                Button bDelFriends = (Button) vCD.findViewById(R.id.bDelFriends);
                builder.setView(vCD);
                builder.setTitle("Изменение списка друзей");

                bAddFriends.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(ID_NON_FRIEND_LIST, bundle);
                        removeDialog(ID_CHOICE);
                    }
                });

                bDelFriends.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(ID_FRIEND_LIST, bundle);
                        removeDialog(ID_CHOICE);
                    }
                });
                break;
            case ID_FRIEND_LIST:
                String fL[] = new String[contactList.get(bundle.getInt("position")).getFriendsList().size()];
                for (int i = 0; i < fL.length; i++) {
                    fL[i] = contactList.get(bundle.getInt("position")).getFriendsList().get(i);
                }
                final String friendList[] = fL;
                checkedItem = new boolean[friendList.length];
                for (int i = 0; i < checkedItem.length; i++) {
                    checkedItem[i] = false;
                }
                omccl = new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        contactList.get(bundle.getInt("position")).removeFriendContact(friendList[which]);
                        checkFriends(contactList, contactList.get(bundle.getInt("position")));
                    }
                };
                builder.setMultiChoiceItems(friendList, checkedItem, omccl);
                builder.setTitle("Удаление из списка друзей");

                builder.setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.notifyDataSetChanged();
                        saveSharedPreferences();
                        removeDialog(ID_FRIEND_LIST);
                    }
                });

                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeDialog(ID_FRIEND_LIST);
                    }
                });

                break;
            case ID_NON_FRIEND_LIST:
                ArrayList<String> fl = contactList.get(bundle.getInt("position")).getFriendsList();
                ArrayList<String> tempCL = new ArrayList<>();
                for (int j = 0; j < contactList.size(); j++) {
                    tempCL.add(contactList.get(j).getContact());
                }

                for (int j = 0; j < fl.size(); j++) {
                    tempCL.remove(fl.get(j));
                }
                tempCL.remove(contactList.get(bundle.getInt("position")).getContact());

                String nonFL[] = new String[tempCL.size()];
                for (int i = 0; i < tempCL.size(); i++) {
                    nonFL[i] = tempCL.get(i);
                }
                final String nonFriendList[] = nonFL;
                checkedItem = new boolean[nonFriendList.length];
                for (int i = 0; i < checkedItem.length; i++) {
                    checkedItem[i] = false;
                }
                omccl = new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        contactList.get(bundle.getInt("position")).addFriendsContact(nonFriendList[which]);
                        checkFriends(contactList, contactList.get(bundle.getInt("position")));

                    }

                };
                builder.setMultiChoiceItems(nonFriendList, checkedItem, omccl);
                builder.setTitle("Добавление в список друзей");

                builder.setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.notifyDataSetChanged();
                        saveSharedPreferences();
                        removeDialog(ID_NON_FRIEND_LIST);
                    }
                });

                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeDialog(ID_NON_FRIEND_LIST);
                    }
                });
                break;
            case ID_DEL_CONTACT:

                builder.setTitle("Удаление контакта");
                builder.setMessage("Вы действительно хотите удалить контакт без возможности восстановления ?");

                builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Selected key Delete", Toast.LENGTH_SHORT).show();
                        checkForDelete(contactList, contactList.get(bundle.getInt("position")));
                        delContact(contactList, bundle.getInt("position"));
                        adapter.notifyDataSetChanged();
                        saveSharedPreferences();
                        removeDialog(ID_DEL_CONTACT);
                    }
                });

                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Selected key CANCEL", Toast.LENGTH_SHORT).show();
                        removeDialog(ID_DEL_CONTACT);

                    }
                });

                break;

            case ID_ADD_CONTACT:

                LayoutInflater liACD = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View vACD = liACD.inflate(R.layout.add_contact_dialog, null);

                EditText etAddContact = (EditText) vACD.findViewById(R.id.etAddContact);

                builder.setView(vACD);
                builder.setTitle("Добавление контакта");

                final EditText etAddContactFinal = etAddContact;

                builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Selected key OK", Toast.LENGTH_SHORT).show();
                        addName(contactList, etAddContactFinal.getText().toString());
                        adapter.notifyDataSetChanged();
                        saveSharedPreferences();
                        removeDialog(ID_ADD_CONTACT);
                    }
                });

                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Selected key CANCEL", Toast.LENGTH_SHORT).show();
                        removeDialog(ID_ADD_CONTACT);
                    }
                });
                break;
        }

        return builder.create();
    }

    public void checkFriends(ArrayList<Contact> contactsList, Contact checkContact) {
        for (Contact c : contactsList) {
            if (c.getContact() != checkContact.getContact()) {
                if (c.getFriendsList().contains(checkContact.getContact()) == true &&
                        checkContact.getFriendsList().contains(c.getContact()) == false) {
                    c.removeFriendContact(checkContact.getContact());
                }
                if (c.getFriendsList().contains(checkContact.getContact()) == false &&
                        checkContact.getFriendsList().contains(c.getContact()) == true) {
                    c.addFriendsContact(checkContact.getContact());
                }
            }
        }
    }

    public void checkForDelete(ArrayList<Contact> contactsList, Contact checkContact) {
        for (Contact c : contactsList) {
            if (c.getFriendsList().contains(checkContact.getContact()) == true) {
                c.removeFriendContact(checkContact.getContact());
            }
        }
    }

    public String getSpString(ArrayList<Contact> contactsList) {
        String spString = "";
        for (Contact c : contactsList) {
            spString += c.getStringFormContact() + CONTACT_SEPARATOR;
        }
        spString = spString.substring(0, spString.length() - 1);
        return spString;
    }

    public ArrayList<Contact> getArrayListFromSet(SharedPreferences sp) {
        ArrayList<Contact> arrayListFromString = new ArrayList<>();
        ArrayList<String> tempStringArray = new ArrayList<>();
        String tempString = sp.getString(CONTACT_SET_KEY, null);
        int startCharCnt = 0;

        for (int i = 0; i < tempString.length(); i++) {
            if (tempString.charAt(i) == CONTACT_SEPARATOR) {
                tempStringArray.add(tempString.substring(startCharCnt, i));
                startCharCnt = i + 1;
            }
            if (i + 1 == tempString.length()) {
                tempStringArray.add(tempString.substring(startCharCnt, i + 1));
                startCharCnt = 0;
            }
        }
        tempStringArray.trimToSize();

        Contact contact[] = new Contact[tempStringArray.size()];

        for (int i = 0; i < tempStringArray.size(); i++) {

            contact[i] = new Contact();
            ArrayList<String> friends = new ArrayList<>();
            tempString = tempStringArray.get(i);
            int typeCnt = 0;
            startCharCnt = 0;

            for (int j = 0; j < tempString.length(); j++) {

                switch (typeCnt) {
                    case 0: //получаем имя контакта
                        if (tempString.charAt(j) == Contact.TYPE_SEPARATOR) {//  - символ разделитель
                            Log.d("MyLog", "Имя " + tempString.substring(startCharCnt, j));
                            contact[i].setContact(tempString.substring(startCharCnt, j));
                            startCharCnt = j + 1;
                            typeCnt++;
                        }
                        break;

                    case 1: //получаем id фото
                        if (tempString.charAt(j) == Contact.TYPE_SEPARATOR) {//  - символ разделитель
                            Log.d("MyLog", "Фото " + Integer.valueOf(tempString.substring(startCharCnt, j)));
                            contact[i].setIvFotoId(Integer.valueOf(tempString.substring(startCharCnt, j)));
                            startCharCnt = j + 1;
                            typeCnt++;
                        }
                        if (j + 1 == tempString.length()) {
                            contact[i].setIvFotoId(Integer.valueOf(tempString.substring(startCharCnt, j + 1)));
                            Log.d("MyLog", "Фото " + Integer.valueOf(tempString.substring(startCharCnt, j + 1)));
                        }
                        break;

                    case 2://наполняем список друзей
                        if (tempString.charAt(j) == Contact.TYPE_SEPARATOR) {//  - символ разделитель
                            Log.d("MyLog", "Текущий Друг " + tempString.substring(startCharCnt, j));
                            friends.add(tempString.substring(startCharCnt, j));
                            startCharCnt = j + 1;
                        }
                        if (j + 1 == tempString.length()) {
                            friends.add(tempString.substring(startCharCnt, j + 1));
                            Log.d("MyLog", "Последний Друг " + tempString.substring(startCharCnt, j + 1));
                            contact[i].setFriendsList(friends);
                        }
                        break;
                }
            }
            arrayListFromString.add(contact[i]);
        }
        return arrayListFromString;
    }


    public void saveSharedPreferences() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CONTACT_SET_KEY, getSpString(contactList));
        editor.commit();
        sp.contains(CONTACT_SET_KEY);
    }


}