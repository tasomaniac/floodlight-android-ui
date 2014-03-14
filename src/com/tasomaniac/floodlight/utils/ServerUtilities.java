/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tasomaniac.floodlight.utils;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.tasomaniac.floodlight.model.Action;
import com.tasomaniac.floodlight.model.DeviceSummary;
import com.tasomaniac.floodlight.model.Flow;
import com.tasomaniac.floodlight.model.Match;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {

	public static List<String> getControllerInfo(String IP) {

		List<String> info = new ArrayList<String>();

		// Add the ip address of the controller
		info.add(0, IP);

		JSONObject obj;
		// Get whether the controller is healthy or not
		try {
			obj = Deserializer.readJsonObjectFromURL((IP.contains("http") ? IP : "http://" + IP
					+ ":8080") + "/wm/core/health/json");
			if (obj.getBoolean("healthy")) {
				info.add(1, "Yes");
			} else {
				info.add(1, "No");
			}
		} catch (IOException e) {
			System.out
					.println("Failed to read JSON from URL, controller may not be running.");
		} catch (JSONException e1) {
		}

		// Get the JVM memory bloat
		try {
			obj = Deserializer.readJsonObjectFromURL((IP.contains("http") ? IP : "http://" + IP
					+ ":8080") + "/wm/core/memory/json");
			long free = obj.getLong("free");
			long total = obj.getLong("total");
			info.add(2, FormatLong.formatBytes(free, true, false) + " free of "
					+ FormatLong.formatBytes(total, true, false));
		} catch (IOException e) {
			System.out
					.println("Failed to read JSON from URL, controller may not be running.");
		} catch (JSONException e1) {
		}

		// Get the modules loaded for the controller
		try {
			obj = Deserializer.readJsonObjectFromURL((IP.contains("http") ? IP : "http://" + IP
					+ ":8080") + "/wm/core/module/loaded/json");
			Iterator<?> myIter = obj.keys();
			String modules = "";
			while (myIter.hasNext()) {
				try {
					String key = (String) myIter.next();
					if (obj.get(key) instanceof JSONObject) {
						modules = modules.concat(key + " ");
					}
				} catch (Exception e) {
					// Fail silently
				}
			}
			info.add(modules);
		} catch (IOException e) {
			System.out
					.println("Failed to read JSON from URL, controller may not be running.");
		} catch (JSONException e1) {
		}

		return info;
	}

	public static List<DeviceSummary> getDeviceSummaries(String IP)
			{

		List<DeviceSummary> deviceSummaries = new ArrayList<DeviceSummary>();

		JSONObject obj;
		// Get the string IDs of all the switches and create switch summary
		// objects for each one
		try {
			JSONArray json = Deserializer.readJsonArrayFromURL((IP.contains("http") ? IP : "http://" + IP
					+ ":8080") + "/wm/device/");
			for (int i = 0; i < json.length(); i++) {
				obj = json.getJSONObject(i);
				DeviceSummary temp = new DeviceSummary(obj.getJSONArray("mac")
						.getString(0));
				if (!obj.getJSONArray("ipv4").isNull(0))
					temp.setIpv4(obj.getJSONArray("ipv4").getString(0));
				if (!obj.getJSONArray("attachmentPoint").isNull(0)) {
					temp.setAttachedSwitch(obj.getJSONArray("attachmentPoint")
							.getJSONObject(0).getString("switchDPID"));
					temp.setSwitchPort(obj.getJSONArray("attachmentPoint")
							.getJSONObject(0).getInt("port"));
				}
				Date d = new Date(obj.getLong("lastSeen"));
				temp.setLastSeen(d);
				deviceSummaries.add(temp);
			}
		} catch (IOException e) {
			System.out.println("Fail sauce!");
		} catch (JSONException e) {
		}
		return deviceSummaries;
	}

	// This parses JSON from the restAPI to get all the flows from a switch
	public static List<Flow> getStaticFlows(String IP, String sw) throws IOException,
			JSONException {

		JSONObject jsonobj, obj;
		List<Flow> flows = new ArrayList<Flow>();

		// Get the string names of all the specified switch's flows
		jsonobj = Deserializer.readJsonObjectFromURL((IP.contains("http") ? IP : "http://" + IP
				+ ":8080") + "/wm/staticflowentrypusher/list/" + sw + "/json");

		if (!jsonobj.isNull(sw)) {
			jsonobj = jsonobj.getJSONObject(sw);
			// Get the keys for the JSON Object
			Iterator<?> myIter = jsonobj.keys();
			// If a key exists, get the JSON Object for that key and create a
			// flow using that object
			while (myIter.hasNext()) {
				try {
					String key = (String) myIter.next();
					if (jsonobj.has(key)) {
						if (jsonobj.get(key) instanceof JSONObject) {
							obj = (JSONObject) jsonobj.get(key);
							Flow flow = new Flow();
							flow.setSwitch(sw);
							flow.setName(key);
							// Get the actions
							flow.setActions(getActions(IP, sw, key));
							// Get the match
							flow.setMatch(getMatch(IP, sw, key));
							if (obj.getInt("priority") != 32767)
								flow.setPriority(String.valueOf(obj
										.getInt("priority")));
							flow.setCookie(String.valueOf(obj.getLong("cookie")));
							if (obj.getInt("idleTimeout") != 0)
								flow.setIdleTimeOut(String.valueOf(obj
										.getInt("idleTimeout")));
							if (obj.getInt("hardTimeout") != 0)
								flow.setHardTimeOut(String.valueOf(obj
										.getInt("hardTimeout")));
							if (obj.getInt("outPort") != -1)
								flow.setOutPort(String.valueOf(obj
										.getInt("outPort")));
							flows.add(flow);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return flows;
	}

	// This parses JSON from the restAPI to get all the actions and values for
	// that action by it's flow name
	public static List<Action> getActions(String IP, String dpid,
			String flowName) throws JSONException, IOException {

		List<Action> actions = new ArrayList<Action>();
		// Get the array of actions
		JSONObject obj = Deserializer.readJsonObjectFromURL((IP.contains("http") ? IP : "http://" + IP
				+ ":8080") + "/wm/staticflowentrypusher/list/" + dpid + "/json");
		if (!obj.getJSONObject(dpid).getJSONObject(flowName).isNull("actions")) {
			JSONArray json = obj.getJSONObject(dpid).getJSONObject(flowName)
					.getJSONArray("actions");

			for (int i = 0; i < json.length(); i++) {
				obj = json.getJSONObject(i);
				String objActionType = obj.getString("type");
				if (objActionType.equals("OUTPUT")) {
					actions.add(new Action("output", String.valueOf(obj
							.getInt("port")), "Port"));
				} else if (objActionType.equals("OPAQUE_ENQUEUE")) {
					actions.add(new Action("enqueue", String.valueOf(obj
							.getInt("port") + ":" + obj.getInt("queueId")),
							"Port:Queue ID"));
				} else if (objActionType.equals("STRIP_VLAN")) {
					actions.add(new Action("strip-vlan", ""));
				} else if (objActionType.equals("SET_VLAN_ID")) {
					actions.add(new Action("set-vlan-id", String.valueOf(obj
							.getInt("virtualLanIdentifier")), "VLAN ID"));
				} else if (objActionType.equals("SET_VLAN_PCP")) {
					actions.add(new Action("set-vlan-priority",
							String.valueOf(obj
									.getInt("virtualLanPriorityCodePoint")),
							"VLAN PCP"));
				} else if (objActionType.equals("SET_DL_SRC")) {
					String dl = obj.getString("dataLayerAddress");
					actions.add(new Action("set-src-mac", String
							.valueOf(HexString.toHexString(dl.getBytes())),
							"Data Layer Address"));
				} else if (objActionType.equals("SET_DL_DST")) {
					String dl = obj.getString("dataLayerAddress");
					actions.add(new Action("set-dst-mac", String
							.valueOf(HexString.toHexString(dl.getBytes())),
							"Data Layer Address"));
				} else if (objActionType.equals("SET_NW_TOS")) {
					actions.add(new Action("set-tos-bits", String.valueOf(obj
							.getInt("networkTypeOfService")),
							"Network Type Of Service"));
				} else if (objActionType.equals("SET_NW_SRC")) {
					long ip = obj.getLong("networkAddress");
					byte[] bytes = BigInteger.valueOf(ip).toByteArray();
					InetAddress address = null;

					try {
						address = InetAddress.getByAddress(bytes);
					} catch (UnknownHostException e) {
						System.out.println("Getting address failed.");
						e.printStackTrace();
					}

					actions.add(new Action("set-src-ip", address.toString()
							.replaceAll("/", ""), "Network Address"));
				} else if (objActionType.equals("SET_NW_DST")) {
					long ip = obj.getLong("networkAddress");
					byte[] bytes = BigInteger.valueOf(ip).toByteArray();
					InetAddress address = null;

					try {
						address = InetAddress.getByAddress(bytes);
					} catch (UnknownHostException e) {
						System.out.println("Getting address failed.");
						e.printStackTrace();
					}

					actions.add(new Action("set-dst-ip", address.toString()
							.replaceAll("/", ""), "Network Address"));
				} else if (objActionType.equals("SET_TP_SRC")) {
					actions.add(new Action("set-src-port", String.valueOf(obj
							.getInt("transportPort")), "Transport Port"));
				} else if (objActionType.equals("SET_TP_DST")) {
					actions.add(new Action("set-dst-port", String.valueOf(obj
							.getInt("transportPort")), "Transport Port"));
				}
			}
		}
		return actions;
	}

	// This parses JSON from the restAPI to get the match of a flow and all it's
	// values
	public static Match getMatch(String IP, String dpid, String flowName)
			throws JSONException, IOException {

		// String dataLayerDestination, dataLayerSource, dataLayerType,
		// dataLayerVLAN,
		// dataLayerPCP, inputPort, networkDestination,
		// networkDestinationMaskLength, networkProtocol, networkSource,
		// networkSourceMaskLength, networkTypeOfService,
		// transportDestination, transportSource, wildcards;
		JSONObject obj;

		Match match = new Match();
		// Get the match object
		obj = Deserializer.readJsonObjectFromURL((IP.contains("http") ? IP : "http://" + IP
				+ ":8080") + "/wm/staticflowentrypusher/list/" + dpid + "/json");
		obj = obj.getJSONObject(dpid).getJSONObject(flowName)
				.getJSONObject("match");

		// Here we check the values, if they are default we set them to emptry
		// strings.
		// This way they don't confuse the user into thinking they set something
		// they didn't
		if (!obj.getString("dataLayerDestination").equals("00:00:00:00:00:00"))
			match.setDataLayerDestination(obj.getString("dataLayerDestination"));
		if (!obj.getString("dataLayerSource").equals("00:00:00:00:00:00"))
			match.setDataLayerSource(obj.getString("dataLayerSource"));
		if (!obj.getString("dataLayerType").equals("0x0000"))
			match.setDataLayerType(obj.getString("dataLayerType"));
		if (obj.getInt("dataLayerVirtualLan") != -1)
			match.setDataLayerVLAN(String.valueOf(obj
					.getInt("dataLayerVirtualLan")));
		if (obj.getInt("dataLayerVirtualLanPriorityCodePoint") != 0)
			match.setDataLayerPCP(String.valueOf(obj
					.getInt("dataLayerVirtualLanPriorityCodePoint")));
		if (obj.getInt("inputPort") != 0 && obj.getInt("inputPort") != 1)
			match.setInputPort(String.valueOf(obj.getInt("inputPort")));
		if (!obj.getString("networkDestination").equals("0.0.0.0"))
			match.setNetworkDestination(obj.getString("networkDestination"));
		// match.setNetworkDestinationMaskLength(String.valueOf(obj.getInt("networkDestinationMaskLen")));
		if (obj.getInt("networkProtocol") != 0)
			match.setNetworkProtocol(String.valueOf(obj
					.getInt("networkProtocol")));
		if (!obj.getString("networkSource").equals("0.0.0.0"))
			match.setNetworkSource(obj.getString("networkSource"));
		// match.setNetworkSourceMaskLength(String.valueOf(obj.getInt("networkSourceMaskLen")));
		if (obj.getInt("networkTypeOfService") != 0)
			match.setNetworkTypeOfService(String.valueOf(obj
					.getInt("networkTypeOfService")));
		if (obj.getInt("transportDestination") != 0)
			match.setTransportDestination(String.valueOf(obj
					.getInt("transportDestination")));
		if (obj.getInt("transportSource") != 0)
			match.setTransportSource(String.valueOf(obj
					.getInt("transportSource")));
		if (obj.getLong("wildcards") != 4194303)
			match.setWildcards(String.valueOf(obj.getLong("wildcards")));

		return match;
	}

}
