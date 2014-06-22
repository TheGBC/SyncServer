package org.gbc.kinect;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.TreeSet;
import java.util.SortedSet;
import java.util.Comparator;

import java.net.ServerSocket;
import java.net.Socket;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;


public class SyncServer {
  private JsonParser parser = new JsonParser();
  private TreeSet<Action> storedActions = new TreeSet<Action>(new Comparator<Action>() {
      @Override
      public int compare(Action action1, Action action2) {
        return action1.millis > action2.millis ? 1 : (action1.millis == action2.millis ? 0 : -1);
      }
  });
  private ServerSocket server;
  private final Object DATA_LOCK = new Object();
  
  public SyncServer(int port) throws Exception {
    server = new ServerSocket(port);
  }
  
  public void receive(JsonObject action) {
    Action actionObj = new Action(action);
    actionObj.millis += 1500;
    synchronized (DATA_LOCK) {
      storedActions.add(actionObj);
    }
  }
  
  public String send(long millisStart, long millisEnd) {
    synchronized (DATA_LOCK) {
      SortedSet<Action> sendableActions = storedActions.subSet(new Action(millisStart), new Action(millisEnd));
      JsonArray arr = new JsonArray();
      for (Action action : sendableActions) {
        arr.add(action.toJson());
      }
      return arr.toString();
    }
  }
  
  public void acceptRequest() throws Exception {
    Socket requestSocket = server.accept();
    PrintWriter out = new PrintWriter(requestSocket.getOutputStream(), true);
    BufferedReader in = new BufferedReader(new InputStreamReader(requestSocket.getInputStream()));  
    JsonObject request = parser.parse(in.readLine()).getAsJsonObject();
    String type = request.getAsJsonPrimitive("type").getAsString();
    if (type.equals("add")) {
      receive(request.getAsJsonObject("action"));
      out.println("ok");
      out.flush();
    } else if (type.equals("poll")) {
      out.println(send(request.getAsJsonPrimitive("start").getAsLong(),
          request.getAsJsonPrimitive("end").getAsLong()));
      out.flush();
    }
  }
  
  public static void main(String[] args) throws Exception {
    SyncServer server = new SyncServer(Integer.parseInt(args[0]));
    while (true) {
      server.acceptRequest();
    }
  }
  
  public static class Action {
    public Vector3 position;
    public Vector3 velocity;
    public long millis;
    
    public Action(long millis) {
      this.millis = millis;
    }
    
    public Action(JsonObject object) {
      this.position = new Vector3(object.getAsJsonObject("position"));
      this.velocity = new Vector3(object.getAsJsonObject("velocity"));
      this.millis = object.getAsJsonPrimitive("timestamp").getAsLong();
    }
    
    public JsonElement toJson() {
      JsonObject object = new JsonObject();
      object.add("position", position.toJson());
      object.add("velocity", position.toJson());
      object.addProperty("timestamp", millis);
      return object;
    }
  }
  
  public static class Vector3 {
    public float x;
    public float y;
    public float z;
    
    public Vector3(JsonObject object) {
      this.x = object.getAsJsonPrimitive("x").getAsLong();
      this.y = object.getAsJsonPrimitive("x").getAsLong();
      this.z = object.getAsJsonPrimitive("x").getAsLong();
    }
    
    public JsonElement toJson() {
      JsonObject object = new JsonObject();
      object.addProperty("x", x);
      object.addProperty("y", y);
      object.addProperty("z", z);
      return object;
    }
  }
}