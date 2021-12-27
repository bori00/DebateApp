package com.se.DebateApp.Controller;

public class SupportedMappings {

    // ----- general
    // --- request mappings
    // redirecting requests
    public static final String GO_TO_ONGOING_DEBATES_CURRENT_PHASE =
            "/go_to_ongoing_debates_current_phase";

    // ----- home controller
    // --- request mappings
    // redirecting requests
    public static final String GO_TO_STARTING_PAGE = "";
    public static final String GO_TO_AUTHENTICATED_HOME_PAGE = "/go_to_home";
    public static final String GO_TO_REGISTER_PAGE = "/go_to_registration";
    public static final String GO_TO_JOIN_DEBATE_PAGE = "/go_to_join_debate";
    public static final String GO_TO_CONFIGURE_DEBATES_PAGE = "/go_to_configure_debates";
    // --- pages
    public static final String AUTHENTICATED_HOME_PAGE = "home";
    public static final String UNAUTHENTICATED_INDEX_PAGE = "index";
    public static final String REGISTRATION_PAGE = "register";
    public static final String CONFIGURE_DEBATES_PAGE = "configure_debates";
    public static final String JOIN_DEBATE_PAGE = "join_debate";


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
    // responding requests
    public static final String CAST_VOTE = "/cast_vote";
    // --- pages
    public static final String DEPUTY_SELECTION_FOR_JUDGE_PAGE = "deputy_selection_for_judge";
    public static final String DEPUTY_SELECTION_FOR_PLAYER_PAGE = "deputy_selection_for_players";

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


    // ----- ongoing debate controller
    // --- request mappings
    // responding requests
    public static final String GET_USERNAME_OF_CURRENT_USER = "/get_username_of_current_user";
    public static final String GET_DEBATE_SESSION_PLAYER = "/get_debate_session_player";
    public static final String HAS_USER_ONGOING_DEBATE = "/has_user_ongoing_debate";


    // ----- final vote
    // --- request mappings
    // responding requests
    public static final String CAST_FINAL_VOTE = "/cast_final_vote";
    public static final String GET_FINAL_VOTE_STATUS = "/get_final_vote_status";

    // ----- debate templates CRUD controller
    // --- request mappings
    public static final String CREATE_DEBATE_TEMPLATE_REQUEST = "/create_debate_template";
    public static final String PROCESS_DEBATE_TEMPLATE_CREATION_REQUEST =
            "/process_debate_template_creation";
    public static final String PROCESS_DEBATE_TEMPLATE_DELETION_REQUEST =
            "/process_debate_template_deletion";
    public static final String PROCESS_DEBATE_TEMPLATE_VIEWING_REQUEST =
        "/process_debate_template_viewing_request";
    public static final String PROCESS_DEBATE_TEMPLATE_EDITING_REQUEST =
            "/process_debate_template_editing_request";
    public static final String PROCESS_DEBATE_TEMPLATE_EDITING = "/process_debate_template_editing";
    public static final String PROCESS_DEBATE_TEMPLATE_RESOURCE_LINK_DELETION_REQUEST =
            "/process_debate_template_resource_link_deletion";
    public static final String PROCESS_DEBATE_TEMPLATE_RESOURCE_LINK_ADDITION_REQUEST =
            "/process_debate_template_resource_link_addition";
    // --- pages
    public static final String VIEW_DEBATE_TEMPLATE_PAGE = "view_debate_template";
    public static final String CREATE_DEBATE_TEMPLATE_PAGE = "create_debate_template";
    public static final String EDIT_DEBATE_TEMPLATE_PAGE = "edit_debate_template";

    // error
    public static final String ERROR_PAGE = "error";

    // redirect
    public static final String REDIRECT_PREFIX = "redirect:";
}
