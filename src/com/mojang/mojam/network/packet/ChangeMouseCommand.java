package com.mojang.mojam.network.packet;


import java.io.*;

import com.mojang.mojam.network.NetworkCommand;


public class ChangeMouseCommand extends NetworkCommand {

  private int x = 0;
  private int y = 0;
  private boolean[] currentState = new boolean[4];
  private boolean[] nextState = new boolean[4];  

  public ChangeMouseCommand() {
  }


  public ChangeMouseCommand(int x, int y, boolean[] currentState, boolean[] nextState) {
    this.x = x;
    this.y = y;
    this.currentState = currentState;
    this.nextState = nextState;
  }


  @Override
  public void read(DataInputStream dis) throws IOException {

    x = dis.readInt();
    y = dis.readInt();

    for (int i=0; i<4; i++){
      currentState[i] = dis.readBoolean();
      nextState[i] = dis.readBoolean();
    }
  }

  @Override
  public void write(DataOutputStream dos) throws IOException {
    dos.writeInt(x);
    dos.writeInt(y);
    for (int i=0; i<4; i++){
      dos.writeBoolean(currentState[i]);
      dos.writeBoolean(nextState[i]);
    }
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  } 

  public boolean[] getAllCurrentState() {
    return currentState;
  }

  public boolean[] getAllNextState() {
    return nextState;
  }

}