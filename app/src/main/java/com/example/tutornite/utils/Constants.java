package com.example.tutornite.utils;

import com.example.tutornite.models.SessionCategoryModel;
import com.example.tutornite.models.SessionDetailsModel;
import com.example.tutornite.models.UserModel;

import java.util.ArrayList;

public class Constants {
    public static String privacy_policy_url = "https://conestogac.apparmor.com/Privacy/";
    public static String rating_app_url = "https://play.google.com/store/apps/details?id=com.cutcom.apparmor.conestogac";
    public static String share_app_url = "https://play.google.com/store/apps/details?id=com.cutcom.apparmor.conestogac";
    public static ArrayList<SessionCategoryModel> remoteSessionsCategoryList = new ArrayList<>();
    public static ArrayList<String> remoteUpcomingSessions = new ArrayList<>();
    public static ArrayList<String> remoteSkills = new ArrayList<>();
    public static ArrayList<String> remoteColleges = new ArrayList<>();
    public static UserModel currentUserModel = new UserModel();
    public static String app_date_format = "dd MMMM yyyy";

    /*Session*/
    public static SessionDetailsModel sessionDetails;

    public static String FROM_ORGANISED_SESSION = "from_organised_sessions";
    public static String FROM_HOME_SCREEN = "from_home_sessions";
    public static String HOME_SCREEN = "home_sessions";
    public static String USER_ID = "user_id";
}
