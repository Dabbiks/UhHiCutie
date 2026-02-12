package dabbiks.uhc.game.teams;

public class TeamCreator {

    public static void initializeTeams() {
        TeamLoader.loadTeams();
        for (TeamData team : TeamLoader.getTeams()) {
            TeamUtils.createTeam(team.name);
            TeamDisplay.createTeamDisplay(team);
        }
    }
}
