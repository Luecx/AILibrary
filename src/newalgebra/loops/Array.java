package newalgebra.loops;

import newalgebra.cells.Cell;
import newalgebra.network.nodes.recurrent.LSTM;
import newalgebra.network.recurrent.RecurrentConnection;

import java.util.ArrayList;

public class Array extends Cell{

    private int parallel;
    private int sequentiel;

    private Cell cell;


    private RecurrentConnection[] recurrentConnections;

    /**
     *
     * creates a 2d-array of the given cells. reuses vaariables
     *
     *
     *
     *
     *
     *         +------------------------------------------------------------------------+
     *         |                                                                        |
     *         |                                                                        |
     *         |                                                                        |
     *         |                                                                        |
     *         |           ..                                                           |
     *         |           |                                                            |
     *         |     +------------+                                                     |
     *         |     |            |  -   ..                                             |
     *         |     |    Cell    |                                                     |
     *         |     |            |  -   ..                                             |
     *         |     +------------+                                                     |
     *         |                                                                        |
     *         +------------------------------------------------------------------------+
     *
     *
     *
     * @param parallel
     * @param sequentiel
     * @param cell
     * @param inOut
     * @param connections
     */
    public Array(int parallel, int sequentiel, Cell cell, RecurrentConnection inOut, RecurrentConnection... connections) {
        super(parallel, parallel);

        this.parallel = parallel;
        this.sequentiel = sequentiel;
        this.cell = cell;
        this.recurrentConnections = connections;

        if(!Cell.isEnclosed(cell)){
            throw new RuntimeException();
        }
        if(connections.length != cell.getUnconnectedInputs().size() - 1){
            throw new RuntimeException();
        }
        ArrayList<Cell> cells = new ArrayList<>();


        Cell[] all = new Cell[parallel * sequentiel];
        Cell[][] cellArray = new Cell[parallel][sequentiel];
        for(int i = 0; i < parallel; i++){
            for(int n = 0; n < sequentiel; n++){
                cellArray[i][n] = cell.copy(true);
                all[i * sequentiel + n] = cellArray[i][n];
            }
        }

        for(int i = 0; i < parallel; i++){
            for(int n = 0; n < sequentiel; n++){

                if(i > 0){
                    Cell.connectCells(cellArray[i-1][n], cellArray[i][n], inOut.getOutputIndex(), inOut.getInputIndex());
                }

                if(n > 0){
                    for(RecurrentConnection con:connections){
                        Cell.connectCells(cellArray[i][n-1], cellArray[i][n], con.getOutputIndex(), con.getInputIndex());
                    }
                }

            }
        }
        this.wrap(all);
    }

    public static void main(String[] args) {
        LSTM lstm = new LSTM();

        Array ar = new Array(2,2, lstm, new RecurrentConnection(1,1), new RecurrentConnection(0,0), new RecurrentConnection(2,2));

        System.out.println(ar);
    }






}
