package dabbiks.uhc.player.data.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionDataManager {

    private static Map<UUID, SessionData> sessionDataMap = new HashMap<>();

    public static SessionData getData(UUID uuid) {
        if (!sessionDataMap.containsKey(uuid)) {
            SessionData sessionData = new SessionData();
            sessionDataMap.put(uuid, sessionData);
        }
        return sessionDataMap.get(uuid);
    }

}
