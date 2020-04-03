/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cacer
 */
public class Position {
    public int row;
    public int col;
    
    public Position(int row, int col) {
	    this.row = row; 
        this.col = col; 
    }

    @Override
    public String toString() {
	    return "(" + row() + "," + col() + ")";
    }

    /**
     * @pre --
     * @post Returns the position's row
     */
    public int row(){
        return this.row;
    }
    
    /**
     * @pre --
     * @post Returns the position's column
     */
    public int col(){
        return this.col;
    }
}