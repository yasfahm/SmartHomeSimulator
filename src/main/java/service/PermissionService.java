package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import controller.UserPermissionsController;
import entity.CommandType;
import entity.PermissionType;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Service layer responsible for user permission management
 */
public class PermissionService {

    /**
     * Imports the permissions from a JSON object
     *
     * @param object The JSONObject obtained from the txt file
     * @throws JsonProcessingException Thrown if the obtained object is not in specified permissions format
     */
    public static void importPermissions(final JSONObject object) throws JsonProcessingException {
        JSONObject jsonMap = object.getJSONObject("permissions");
        HashMap<String, Object> map = new ObjectMapper().readValue(jsonMap.toString(), HashMap.class);
        Map<String, Map<CommandType, PermissionType>> result = new HashMap<>();
        map.keySet().forEach(key -> {
            Map commandPermissions = new HashMap();
            Map userPermissions = (Map) map.get(key);
            userPermissions.keySet().forEach(command -> {
                commandPermissions.put(CommandType.valueOf(command.toString()), PermissionType.valueOf(userPermissions.get(command).toString()));
            });
            result.put(key, commandPermissions);
        });

        Map<String, Map<CommandType, PermissionType>> existingPermissions = UserPermissionsController.getUserPermissions();
        existingPermissions.putAll(result);
        UserPermissionsController.setUserPermissions(existingPermissions);
    }
}
