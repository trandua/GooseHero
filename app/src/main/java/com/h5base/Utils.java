package com.h5base;


import android.util.Log;

public class Utils {
    /*
    Hero-Goose-Defense
    tank_battle
    * */
    private static String[] allGames = new String[]{
            "Hero-Goose-Defense"
    };
    public static final String game_name = allGames[0];//"Hero-Goose-Defense";
    private static String TAG = "TAGG-chromium";
    public static final String ID_HBANNER = "/22751588535,22946176021/Mobileapp_Stargames/Eerskraft/android_banner";
    public static final String IRON_KEY = "85460dcd";//"85460dcd"; test key, realKey: 1c546f54d
    public static final String AD_APP_OPEN_UNIT_ID = "/22751588535,22946176021/Mobileapp_Stargames/Mobileapp_Eerskraft_Android_Appopenads_1";
    public static final String ID_INTERSTITIAL = "/22751588535,22946176021/Mobileapp_Stargames/mobileapp_Lokiiworldgame_android_interstitial_1";
    public static final String ID_IMG_INTERSTITIAL = "/22751588535,22946176021/Mobileapp_Stargames/mobileapp_Lokiiworldgame_android_interstitial_1";
    public static final String ID_VIDEO = "ca-app-pub-3940256099942544/5224354917";//"/22751588535,22946176021/Mobileapp_Stargames/mobileapp_Lokiiworldgame_android_rwdd_1";
    public static final String ID_COLLAPSE_BANNER = "/22751588535,22946176021/Mobileapp_Stargames/mobileapp_Lokiiworldgame_android_banner_1";//"ca-app-pub-3940256099942544/9214589741";//
    public static final String ID_COLLAPSE_BANNER_INGAME = "/22751588535,22946176021/Mobileapp_Stargames/mobileapp_Lokiiworldgame_android_banner_1";
    public static final String ID_MAX_INTERS = "e77106171a422cf1";
    public static final String ID_MAX_IMG_INTERS ="e77106171a422cf1";
    public static final String ID_MAX_VIDEO = "159170d45aac01ea";

    public static final String ID_MAX_BANNER = "d8c634df09690250";
    public static final String ID_MAX_APPOPEN = "b87ff67e17fd6e4b";
    public static final String ID_APP_OPEN_PLACEMENT = "b87ff67e17fd6e4b";
    public static final String ID_MAX_NATIVE = "0815411a2d128ad7";
    public static boolean checkAppOpen = false;
    public static void showLogMessage(String _mess){
//        Log.i(TAG,_mess);
    }

    public static final String[] mGameFeatured = new String[]{
            "1_among_us",
            "1_head_soccer",
            "1_AABlockBlast",
            "1_happy_filled_glass",
            "1_BeatsWiper",
            "1_tic_tac_toe",
            "1_geometry_rash",
            "1_crazy_fishing",
            "1_chess",
            "1_mr_bullet"
    };

    public static final String[] mGameHistory = new String[]{ // Không còn sử dụng trực tiếp mGameHistory, chỉ dùng để tham chiếu
            "1_find_pair",
            "1_happy_filled_glass",
            "1_move_the_pin_2"};

    public static final String[] mGamePuzzle = new String[]{
            "1_AABlockBlast",
            "1_Color-Fill",
            "1_drop_it",
            "1_drop_n_merge",
            "1_find_pair",
            "1_happy_filled_glass",
            "1_master_fall_down",
            "1_merge_cards",
            "1_merge_dice",
            "1_move_the_pin_2",
            "1_mr_bullet",
            "1_nonogram",
            "1_number_tiles",
            "1_pao_pao",
            "1_sudoku",
            "1_tower_boom",
            "1_water_sort",
            "1_word_connect",
            "1_wordly"};
    public static final String[] mGameCasual = new String[]{
            "1_among_us",
            "1_backgammon",
            "1_BeatsWiper",
            "1_checkers",
            "1_chess",
            "1_city_builder",
            "1_color_cannon",
            "1_crazy_fishing",
            "1_dominoes",
            "1_drop_n_merge",
            "1_find_pair",
            "1_geometry_rash",
            "1_happy_filled_glass",
            "1_ludo",
            "1_mad_fish",
            "1_merge_cards",
            "1_merge_dice",
            "1_move_the_pin_2",
            "1_number_tiles",
            "1_pinball_rush",
            "1_pool_8_pro",
            "1_tic_tac_toe",
            "1_twenty48",
            "1_uno",
            "1_uno_vertical"};
    public static final String[] mGameWord = new String[]{
            "1_word_connect",
            "1_wordly"};
    public static final String[] mGameAction = new String[]{
            "1_among_us",
            "1_Conquer-The-City",
            "1_crazy_fishing",
            "1_geometry_rash",
            "1_head_soccer",
            "1_indiara",
            "1_mad_fish",
            "1_master_fall_down",
            "1_mr_bullet",
            "1_pao_pao",
            "1_pinball_rush",
            "1_tower_boom"};
    public static final String[] mGameStrategy = new String[]{
            "1_backgammon",
            "1_checkers",
            "1_chess",
            "1_city_builder",
            "1_Conquer-The-City",
            "1_dominoes",
            "1_ludo",
            "1_tic_tac_toe",
            "1_uno",
            "1_uno_vertical"};
    public static final String[] mGameSport = new String[]{
            "1_head_soccer",
            "1_pool_8_pro"};
    public static final String[] mGameAdventure = new String[]{
            "1_indiara"};
}

