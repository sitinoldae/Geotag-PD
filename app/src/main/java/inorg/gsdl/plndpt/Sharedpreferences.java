package inorg.gsdl.plndpt;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

public class Sharedpreferences {

    public static final String Tag_user_id = "user_id";
    public static final String Tag_form_name = "form_name";
    public static final String Tag_search_latitude = "search_latitude";
    public static final String Tag_search_Longitude = "search_longitude";
    public static final String Tag_otp_verification = "otp_verification";
    public static final String Tag_user_name_verif = "user_name_verif";
    public static final String Tag_user_email_verif = "user_email_verif";
    public static final String Tag_User_Mobile_verif = "User_Mobile_verif";
    public static final String Division = "Division_Name";
    public static final String images = "images";
    public static final String circle_concerned_officer = "circle_concerned_officer";
    public static final String circle_concerned_officer_mob = "circle_concerned_officer_mob";
    public static final String officeaddress = "officeaddress";
    public static final String emailofficer = "emailofficer";
    public static final List<User> myListData = null;
    private static final String PREF_NAME = "com.waterflood.gsdl_app";
    public static SharedPreferences.Editor editor;
    private static Sharedpreferences userData = null;
    Context context;
    private SharedPreferences pref;
    private int PRIVATE_MODE = 0;


    public Sharedpreferences(Context c) {
        try {
            this.context = c;
            pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            editor = pref.edit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Sharedpreferences getUserDataObj(Context c) {

        if (userData == null) {

            userData = new Sharedpreferences(c);
        }
        return userData;
    }

    public static List<User> getMyListData() {
        return myListData;
    }

    public void clearAll(Context c) {
        this.context = c;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        pref.edit().clear().commit();
    }

    public String get_otp_verification() {
        return pref.getString(Tag_otp_verification, "");
    }

    public void set_otp_verification(String otp_verification) {
        try {
            editor.putString(Tag_otp_verification, otp_verification);
            editor.commit();
        } catch (Exception e) {
        }
    }

    public String get_user_name_verif() {
        return pref.getString(Tag_user_name_verif, "");
    }

    public void set_user_name_verif(String user_name_verif) {
        try {
            editor.putString(Tag_user_name_verif, user_name_verif);
            editor.commit();
        } catch (Exception e) {
        }
    }

    public String get_User_Mobile_verif() {
        return pref.getString(Tag_User_Mobile_verif, "");
    }

    public void set_User_Mobile_verif(String User_Mobile_verif) {
        try {
            editor.putString(Tag_User_Mobile_verif, User_Mobile_verif);
            editor.commit();
        } catch (Exception e) {
        }
    }

    public String get_user_email_verif() {
        return pref.getString(Tag_user_email_verif, "");
    }

    public void set_user_email_verif(String user_email_verif) {
        try {
            editor.putString(Tag_user_email_verif, user_email_verif);
            editor.commit();
        } catch (Exception e) {
        }
    }

    public String get_user_id() {
        return pref.getString(Tag_user_id, "");
    }

    public void set_user_id(String user_id) {
        try {
            editor.putString(Tag_user_id, user_id);
            editor.commit();
        } catch (Exception e) {
        }
    }

    public String get_form_name() {
        return pref.getString(Tag_form_name, "");
    }

    public void set_form_name(String form_name) {
        try {
            editor.putString(Tag_form_name, form_name);
            editor.commit();
        } catch (Exception e) {
        }
    }

    public String get_search_latitude() {
        return pref.getString(Tag_search_latitude, "");
    }

    public void set_search_latitude(String search_latitude) {
        try {
            editor.putString(Tag_search_latitude, search_latitude);
            editor.commit();
        } catch (Exception e) {
        }
    }

    public String get_search_Longitude() {
        return pref.getString(Tag_search_Longitude, "");
    }

    public void set_search_Longitude(String search_Longitude) {
        try {
            editor.putString(Tag_search_Longitude, search_Longitude);
            editor.commit();
        } catch (Exception e) {
        }
    }

    public String getDivision() {
        return pref.getString(Division, "");
    }

    public void setDivision(String division) {
        try {
            editor.putString(Division, division);
            editor.commit();
        } catch (Exception e) {
        }
    }

    public String getCircle_concerned_officer() {
        return pref.getString(circle_concerned_officer, "");
    }

    public void setCircle_concerned_officer(String circle_concerned_offi) {
        try {
            editor.putString(circle_concerned_officer, circle_concerned_offi);
            editor.commit();
        } catch (Exception e) {
        }
    }

    public String getCircle_concerned_officer_mob() {
        return pref.getString(circle_concerned_officer_mob, "");
    }

    public void setCircle_concerned_officer_mob(String circle_concerned_officer_m) {
        try {
            editor.putString(circle_concerned_officer_mob, circle_concerned_officer_m);
            editor.commit();
        } catch (Exception e) {
        }
    }

    public String getOfficeaddress() {
        return pref.getString(officeaddress, "");
    }

    public void setOfficeaddress(String officeaddre) {
        try {
            editor.putString(officeaddress, officeaddre);
            editor.commit();
        } catch (Exception e) {
        }
    }

    public String getEmailofficer() {
        return pref.getString(emailofficer, "");
    }

    public void setEmailofficer(String emailoff) {
        try {
            editor.putString(emailofficer, emailoff);
            editor.commit();
        } catch (Exception e) {
        }
    }

    public String getImages() {
        return pref.getString(images, "");
    }

    public void setImages(String imag) {
        try {
            editor.putString(images, imag);
            editor.commit();
        } catch (Exception e) {
        }
    }


}
