package com.akobets.friendfinder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by akobets on 28.08.2015.
 */
public class ContactListAdapter extends ArrayAdapter<Contact> {

    private Context context;
    private int resource;
    private ArrayList<Contact> contactList;


    public ContactListAdapter(Context context, int resource, ArrayList<Contact> contactList) {
        super(context, resource, contactList);
        this.context = context;
        this.resource = resource;
        this.contactList = contactList;
    }

    @Override
    public Contact getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_layout, parent, false);

        // Связываем поля и графические элементы
        ImageView ivFoto = (ImageView) view.findViewById(R.id.ivFoto);
        TextView tvContact = (TextView) view.findViewById(R.id.tvContact);
        TextView tvContactList = (TextView) view.findViewById(R.id.tvContactList);

        // получаем объект из списка объектов
        Contact currentContact = contactList.get(position);

        // устанавливаем значения компонентам одного элемента списка
        ivFoto.setImageResource(currentContact.getIvFotoId());
        tvContact.setText(currentContact.getContact());
        tvContactList.setText(currentContact.getFriendsString());

        return view;
    }
}
