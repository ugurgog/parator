package com.paypad.vuk507.contact;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.Contact;
import com.paypad.vuk507.model.pojo.CountryPhoneCode;
import com.paypad.vuk507.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;

public class ContactHelper {

    public static void getContactList(Context context, CompleteCallback completeCallback) {
        List<Contact> contactList = new ArrayList<>();
        String previousPhoneNum = "";

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);

        try{
            String sorting = ContactsContract.Contacts.DISPLAY_NAME + " DESC";

            Cursor phones = context
                    .getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, sorting);

            while (phones.moveToNext()) {

                Contact contact = new Contact();

                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                if(!phoneNumber.equals(previousPhoneNum)) {
                    StringBuilder clearPhoneNum = new StringBuilder();

                    for (int i = 0; i < phoneNumber.length(); i++) {
                        char ch = phoneNumber.charAt(i);
                        if (Character.isDigit(ch)) {
                            clearPhoneNum.append(ch);
                        }
                    }
                    contact.setName(name);
                    contact.setPhoneNumber(clearPhoneNum.toString());
                    contactList.add(contact);
                }
                previousPhoneNum = phoneNumber;
            }
            phones.close();
            Collections.sort(contactList, new CustomComparator());
            baseResponse.setObject(contactList);

        }catch (Exception e){
            baseResponse.setSuccess(false);
            baseResponse.setMessage("There is an error occured while loading contact list");
        }

        completeCallback.onComplete(baseResponse);
    }

    public static class CustomComparator implements Comparator<Contact> {
        @Override
        public int compare(Contact o1, Contact o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }

    public static void reformPhoneList(List<CountryPhoneCode> phoneCodes, List<Contact> contactList, final CompleteCallback completeCallback) {
        String locale = CommonUtils.getDefaultCountryCode();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(false);

        for (CountryPhoneCode country : phoneCodes) {
            if (country.getCountryCode().trim().equals(locale)) {
                baseResponse.setSuccess(true);
                baseResponse.setObject(formatNumbersWithDialCode(country.getDialCode(), contactList));
                break;
            }
        }
        completeCallback.onComplete(baseResponse);
    }

    public static List<Contact>  formatNumbersWithDialCode(String dialCode, final List<Contact> contactList) {
        List<Contact> reformedContactList = new ArrayList<>();

        if (CommonUtils.getDefaultCountryCode().equalsIgnoreCase(LANGUAGE_TR)) {
            for (Contact contact : contactList) {
                if (contact != null && contact.getPhoneNumber() != null && !contact.getPhoneNumber().isEmpty()) {

                    try {
                        String completeNumber;
                        String reverseText = new StringBuilder(contact.getPhoneNumber().trim()).reverse().toString();
                        completeNumber = "+".concat(dialCode.trim()).concat(new StringBuilder(reverseText.substring(0, 10)).reverse().toString());

                        Contact contactTemp = new Contact();
                        contactTemp.setName(contact.getName().trim());
                        contactTemp.setPhoneNumber(completeNumber.trim());

                        reformedContactList.add(contactTemp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if(reformedContactList.size() == 0)
            return contactList;
        else
            return reformedContactList;
    }
}
