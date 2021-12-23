package com.se.DebateApp.Controller;

public class SupportedMappings {

    // ----- general
    // --- request mappings
    // redirecting requests
    public static final String GO_TO_ONGOING_DEBATES_CURRENT_PHASE =
            "/go_to_ongoing_debates_current_phase";
    public static final String STARTING_PAGE = "";


    // ----- registration
    // --- request mappings
    // redirecting requests
    public static final String REGISTER_AND_GO_TO_DESTINATION = "register_and_redirect";
    // --- pages
    public static final String REGISTER_PAGE = "register";
    public static final String REGISTER_SUCCESS_PAGE = "register_success";


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


    // ----- deputy selection
    // --- request mappings
    // redirecting requests
    public static final String GO_TO_DEPUTY_SELECTION = "/go_to_deputy_selection";

    // ----- meetings
    // --- request mappings
    // responding requests
    public static final String CREATE_MEETING = "/create_meeting";
    public static final String GET_ALL_MEETINGS = "/get_all_meetings";


    // ----- state transitions controller
    // --- request mappings
    // responding requests
    public static final String GET_CURRENT_PHASES_TIME_INTERVAL =
            "/get_current_phases_time_interval";
    public static final String GET_CURRENT_PHASE_STARTING_TIME =
            "/get_current_phase_starting_time";
    public static final String IS_DEBATE_SESSION_FINISHED = "/is_debate_finished";
    public static final String PROCESS_END_OF_TIMED_PHASE = "/process_end_of_timed_phase";
    public static final String CLOSE_DEBATE = "/close_debate";

    // error
    public static final String ERROR_PAGE = "error";

    // redirect
    public static final String REDIRECT_PREFIX = "redirect:";
}
