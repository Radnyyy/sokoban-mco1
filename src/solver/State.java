
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

    

    
    
    private Position playerPos;
    private int cost;
    private int heuristic;
    private ArrayList<Position> boxPos;
    private char prevMove;
    private boolean boxMoved;
    private State parent;
    

    public State(Position playerPos, int cost, int heuristic, ArrayList<Position> boxPos, char prevMove, boolean boxMoved, State parent) {
        this.playerPos = playerPos;
        this.cost = cost;
        this.heuristic = heuristic;
        this.boxPos = boxPos;
        this.prevMove = prevMove;
        this.parent = parent;
    }
    
    /**
     * @return the playerPos
     */
    public Position getPlayerPos() {
        return playerPos;
    }

    
    /**
     * @return the boxPos
     */
    public ArrayList<Position> getBoxPos() {
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
        
        if (s.getPlayerPos().getX() != playerPos.getX() || s.getPlayerPos().getY() != playerPos.getY())
            return false;
        
        for (int i = 0; i < boxPos.size(); i++) {
            if (!s.getBoxPos().get(i).equals(boxPos.get(i))) {
                return false;
            }
        }
       
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.playerPos);
        hash = 59 * hash + Objects.hashCode(this.boxPos);
        return hash;
    }



  
}
