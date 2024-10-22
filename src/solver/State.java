
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

    

    
    
    private final Position playerPos;
    private final int heuristic;
    private final ArrayList<Position> boxPos;
    private final char prevMove;
    private final State parent;
    

    public State(Position playerPos, int heuristic, ArrayList<Position> boxPos, char prevMove, State parent) {
        this.playerPos = playerPos;
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
     * @return the heuristic
     */
    public int getHeuristic() {
        return heuristic;
    }
    
    /**
     * @return the prevMove
     */
    public char getPrevMove() {
        return prevMove;
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
