/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package solver;

import java.util.*;


/**
 *
 * @author user
 */
public class State {

    

    
    
    private int PlayerX;
    private int PlayerY;
    private int cost;
    private int heuristic;
    private ArrayList<int[]> boxPos;
    private char prevMove;
    private boolean boxMoved;
    private State parent;
    

    public State(int PlayerX, int PlayerY, int cost, int heuristic, ArrayList<int[]> boxPos, char prevMove, boolean boxMoved, State parent) {
        this.PlayerX = PlayerX;
        this.PlayerY = PlayerY;
        this.cost = cost;
        this.heuristic = heuristic;
        this.boxPos = boxPos;
        this.prevMove = prevMove;
        this.parent = parent;
    }
    
    /**
     * @return the PlayerX
     */
    public int getPlayerX() {
        return PlayerX;
    }

    /**
     * @return the PlayerY
     */
    public int getPlayerY() {
        return PlayerY;
    }
    
    /**
     * @return the boxPos
     */
    public ArrayList<int[]> getBoxPos() {
        return boxPos;
    }
    
    /**
     * @return the cost
     */
    public int getCost() {
        return cost;
    }

    /**
     * @return the heuristic
     */
    public int getHeuristic() {
        return heuristic;
    }
    
    /**
     * @return the total cost
     */
    public int getTotalCost() {
        return cost + heuristic;
    }
    
    /**
     * @return the prevMove
     */
    public char getPrevMove() {
        return prevMove;
    }

    /**
     * @return the boxMoved
     */
    public boolean isBoxMoved() {
        return boxMoved;
    }
    
    /**
     * @return the parent
     */
    public State getParent() {
        return parent;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        
        if (o == null || !(o instanceof State))
            return false;
        
        State s = (State) o;
        
        if (s.getPlayerX() != PlayerX || s.getPlayerY() != PlayerY)
            return false;
        
        for (int i = 0; i < boxPos.size(); i++) {
            if (!Arrays.equals(s.getBoxPos().get(i), boxPos.get(i))) {
                return false;
            }
        }
       
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + this.PlayerX;
        hash = 37 * hash + this.PlayerY;
        
        for (int[] box : boxPos) {
            hash = 37 * hash + Arrays.hashCode(box);
        }
        
        return hash;
    }

  
}
