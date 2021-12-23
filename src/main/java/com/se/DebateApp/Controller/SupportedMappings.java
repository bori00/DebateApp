package com.se.DebateApp.Controller;

public class SupportedMappings {

    // ----- general
    // --- request mappings
    // redirecting requests
    public static final String GO_TO_ONGOING_DEBATES_CURRENT_PHASE =
            "/go_to_ongoing_debates_current_phase";

    // ----- starting a debate
    // --- requests mappings
    // redirecting requests
    public static final String PLAYER_GO_TO_CHOOSE_TEAM_REQUEST = "/go_to_choose_team";
    public static final String PLAYER_GO_TO_DEBATE_LOBBY_REQUEST = "/go_to_debate_lobby";
    public static final String JUDGE_GO_TO_STARTING_DEBATE_REQUEST = "/go_to_starting_debate";
    // responding requests
    public static final String PLAYER_JOIN_DEBATE_REQUEST = "/join_debate";
    public static final String PLAYER_JOIN_TEAM_REQUEST = "/join_team";
    public static final String JUDGE_START_DEBATE_REQUEST = "/start_debate";
    // --- pages
    public static final String PLAYER_CHOOSE_TEAM_PAGE = "choose_team";
    public static final String PLAYER_DEBATE_LOBBY_PAGE = "debate_lobby";


    // ----- prep time
    // --- request mappings
    // redirecting requests
    public static final String GO_TO_DEBATE_PREPARATION = "/go_to_debate_preparation";
    // responding requests
    public static final String JUDGE_ACTIVATE_DEBATE = "/activate_debate";
    // --- pages
    public static final String DEBATE_PREPARATION_PAGE = "debate_preparation";


    // ----- meetings
    // --- request mappings
    // responding requests
    public static final String CREATE_MEETING = "/create_meeting";
    public static final String GET_ALL_MEETINGS = "/get_all_meetings";

    // error
    public static final String ERROR_PAGE = "error";

    // redirect
    public static final String REDIRECT_PREFIX = "redirect:";
}
