package com.tasomaniac.floodlight.utils.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tasomaniac.floodlight.model.Flow;
import com.tasomaniac.floodlight.utils.Deserializer;
import com.tasomaniac.floodlight.utils.FormatLong;
import com.tasomaniac.floodlight.utils.JSONArray;
import com.tasomaniac.floodlight.utils.JSONException;
import com.tasomaniac.floodlight.utils.JSONObject;

public class FlowJSON {

	static JSONObject obj;
	static JSONArray json;

	// This parses JSON from the restAPI to get all the flows from a specified switch, meant for the controller overview
	public static List<Flow> getFlows(String IP, String sw) throws IOException,
			JSONException {

		List<Flow> flows = new ArrayList<Flow>();

		// Get the string names of all the specified switch's flows
		obj = Deserializer.readJsonObjectFromURL((IP.contains("http") ? IP : "http://" + IP
				+ ":8080") + "/wm/core/switch/" + sw + "/flow/json");

		if (!obj.isNull(sw)) {
			json = obj.getJSONArray(sw);
			for (int i = 0; i < json.length(); i++) {
				obj = (JSONObject) json.get(i);
				Flow flow = new Flow(sw);
				flow.setActions(ActionJSON.getActions(IP, obj
						.getJSONArray("actions")));
				flow.setMatch(MatchJSON.getMatch(obj.getJSONObject("match")));
				flow.setPriority(String.valueOf(obj.getInt("priority")));
				flow.setCookie(String.valueOf(obj.getLong("cookie")));
				if (obj.getInt("idleTimeout") != 0)
					flow.setIdleTimeOut(String.valueOf(obj
							.getInt("idleTimeout")));
				if (obj.getInt("hardTimeout") != 0)
					flow.setHardTimeOut(String.valueOf(obj
							.getInt("hardTimeout")));
				flow.setDurationSeconds(String.valueOf(obj
						.getInt("durationSeconds")));
				flow.setPacketCount(String.valueOf(obj.getInt("packetCount")));
				flow.setByteCount(FormatLong.formatBytes(obj.getLong("byteCount"),false,false));
				flows.add(flow);
			}
		}
		return flows;
	}
}
