package fr.info.orleans.androidbattleship.model.computer_ia;

import java.util.ArrayList;
import java.util.Random;

import fr.info.orleans.androidbattleship.R;
import fr.info.orleans.androidbattleship.model.Coordinate;
import fr.info.orleans.androidbattleship.model.Grid;

/**
 * Created by Wilsigh on 29/01/2016.
 */
public class IADifficile extends IA {

    private ArrayList<Coordinate>  grid2,grid3,grid4,grid5, hunt;
    private ArrayList<Integer> remainingBoat;
    private int state, changeState;
    private boolean initOk;

    public IADifficile(){
        initOk=false;
    }

    public int[] computePlay(Grid playerGrid){
        if(!initOk) {
            initIAHard();
        }
        ArrayList<Coordinate> gridCurrent;
        changeStatus();
        gridCurrent=selectGrid(this.state);
        int x;
        int y;
        Coordinate currentCoord=new Coordinate();
        do {
            switch (this.hunt.size()) {
                case 0:
                    currentCoord = randomCase(gridCurrent);
                    break;
                case 1:
                    currentCoord= selectCase(this.hunt.get(0), playerGrid);
                    break;
                case 2:case 3: case 4:
                    currentCoord=selectSpecificCase(playerGrid);
                    if(changeState!=0){
                        changeState--;
                    }
                    break;
            }
            x = currentCoord.getX();
            y = currentCoord.getY();
        } while (!notYetShot(playerGrid, x, y));
        removeOne(currentCoord);

        Grid.Cell cell = playerGrid.getCells()[x][y];
        final int idRes;

        if (cell == Grid.Cell.EMPTY) {
            idRes = R.drawable.miss;
            playerGrid.setCellAt(x, y, Grid.Cell.MISS);
            if(this.hunt.size()>1){
                changeState=2;
            }
        } else {
            idRes = R.drawable.hit;
            this.hunt.add(currentCoord);
            playerGrid.setCellAt(x, y, Grid.Cell.HIT);
            if (playerGrid.isShipDestroyed(x, y)){
                ArrayList<Integer> delete= minmax(hunt);
                removeMany(delete.get(0),delete.get(1),delete.get(2),delete.get(3));
                changeRemainingBoat(this.hunt);
                changeState=0;
                this.hunt=new ArrayList();
            }
        }
        int[] coords = {x, y, idRes};
        return coords;
    }

    private void initIAHard(){
        grid2=new ArrayList<>();
        grid3=new ArrayList<>();
        grid4=new ArrayList<>();
        grid5=new ArrayList<>();

        for(int i=0; i<Grid.SIZE;i++){
            for(int j=0; j<Grid.SIZE; j++){
                if ((i+j)%2==1){
                    Coordinate c = new Coordinate(i,j);
                    grid2.add(c);
                }
                if ((i+j)%3==1){
                    Coordinate c = new Coordinate(i,j);
                    grid3.add(c);
                }
                if ((i+j)%4==1) {
                    Coordinate c = new Coordinate(i, j);
                    grid4.add(c);
                }
                if ((i+j)%5==1){
                    Coordinate c = new Coordinate(i,j);
                    grid5.add(c);
                }

            }
        }
        this.hunt=new ArrayList();
        this.state=2;
        this.remainingBoat=new ArrayList();
        initRemainingBoat();
        this.changeState = 0;
        initOk=true;

    }

    private Coordinate randomCase(ArrayList<Coordinate> coord){
        int size = coord.size();
        Random r = new Random();
        int i = (r.nextInt(size));
        return coord.get(i);
    }

    private void initRemainingBoat(){
        for(int i=0;i<4;i++){
            if(i==3){
                this.remainingBoat.add(1);
            }
            else this.remainingBoat.add(2);
        }

    }

    private ArrayList<Integer> minmax (ArrayList<Coordinate> List){
        int minx=10;
        int maxx=-1;
        int miny = 10;
        int maxy=-1;
        ArrayList<Integer> sol = new ArrayList();
        for(int i=0; i<List.size();i++){
            Coordinate c = List.get(i);
            if (c.getX()<minx){
                minx=c.getX();
            }
            if (c.getX()>maxx){
                maxx=c.getX();
            }
            if (c.getY()<miny){
                miny=c.getY();
            }
            if (c.getY()<maxy){
                maxy=c.getY();
            }
        }
        sol.add(minx);
        sol.add(maxx);
        sol.add(miny);
        sol.add(maxy);
        return sol;
    }

    private void removeMany(int mini, int maxi, int minj, int maxj){
        if(mini==maxi){
            for(int i=mini-1 ;i<maxi+2;i++){
                for(int j= minj-1; j<maxj+2; j++){
                    Coordinate c = new Coordinate(i,j);
                    removeOne(c);
                }
            }
        }
    }

    public void changeStatus(){
        boolean stop=false;
        int i=0;
        while(!stop){
            if(remainingBoat.get(i).equals(0)){
                i++;
            }else stop=true;
        }
        this.state=i+2;
    }

    public void changeRemainingBoat(ArrayList<Coordinate> array){
        int x = array.size();
        this.remainingBoat.set(x-2,this.remainingBoat.get(x-2)-1);
        this.hunt=new ArrayList();
    }

    private ArrayList<Coordinate> selectGrid(int state){
        switch (state) {
            case 2:
                return grid2;
            case 3:
                return grid3;
            case 4:
                return grid4;
            case 5:
                return grid5;
            default:
                return grid2;
        }
    }

    private Coordinate selectCase(Coordinate c, Grid playerGrid) {
        Coordinate newCoord = new Coordinate();
        ArrayList<Integer> possibility = new ArrayList();
        if (c.getY()+1<Grid.SIZE && notYetShot(playerGrid, c.getX(), c.getY()+1)) {
            if(c.getY()<10){
                possibility.add(0);
            }
        }
        if (c.getX()+1<Grid.SIZE && notYetShot(playerGrid, c.getX()+1, c.getY())) {
            if(c.getX()<10) {
                possibility.add(1);
            }
        }
        if (c.getY()-1>=0 && notYetShot(playerGrid, c.getX(), c.getY()-1)) {
            if(c.getY()>=0) {
                possibility.add(2);
            }
        }
        if (c.getX()-1>=0 && notYetShot(playerGrid, c.getX()-1, c.getY())) {
            if(c.getX()>=0) {
                possibility.add(3);
            }
        }
        Random r = new Random();
        int i = (r.nextInt(possibility.size()));
        int x = possibility.get(i);
        switch(x) {
            case 0:
                newCoord = new Coordinate(c.getX(), c.getY()+1);
                break;
            case 1:
                newCoord = new Coordinate(c.getX()+1, c.getY());
                break;
            case 2:
                newCoord = new Coordinate(c.getX(), c.getY()-1);
                break;
            case 3:
                newCoord = new Coordinate(c.getX()-1, c.getY());
                break;
        }
        return newCoord;
    }

    private Coordinate selectSpecificCase(Grid playerGrid){
        Coordinate c = new Coordinate();
        if(changeState==0){
            int x=difference(this.hunt.get(hunt.size() - 2).getX(), this.hunt.get(hunt.size() - 1).getX());
            int y=difference(this.hunt.get(hunt.size()-2).getY(),this.hunt.get(hunt.size()-1).getY());
            c= new Coordinate(this.hunt.get(hunt.size() - 1).getX()+x,this.hunt.get(hunt.size()-1).getY()+y);
            if(c.getX()<0 || c.getX()>9){
                changeState=2;
                return selectSpecificCase(playerGrid);
            }
            else if(c.getY()<0 || c.getY()>9){
                changeState=2;
                return selectSpecificCase(playerGrid);
            }
            else if(!notYetShot(playerGrid, c.getX(), c.getY())){
                changeState=2;
                return selectSpecificCase(playerGrid);
            }
        }
        else if(changeState==1){
            int x=difference(this.hunt.get(hunt.size()-1).getX(), this.hunt.get(0).getX());
            int y = difference(this.hunt.get(hunt.size() - 1).getY(), this.hunt.get(0).getY());
            c= new Coordinate(this.hunt.get(hunt.size()-1).getX()-x,this.hunt.get(hunt.size()-1).getY()-y);
            if(!notYetShot(playerGrid, c.getX(), c.getY())){
                changeState=2;
            }
        }
        else if(changeState==2){
            int x=difference(this.hunt.get(0).getX(), this.hunt.get(1).getX());
            int y=difference(this.hunt.get(0).getY(),this.hunt.get(1).getY());
            c= new Coordinate(this.hunt.get(0).getX()-x,this.hunt.get(0).getY()-y);

        }
        return c;
    }

    private void removeOne(Coordinate c){
        if(this.grid2.contains(c)){
            grid2.remove(c);
        }
        if(this.grid3.contains(c)){
            grid3.remove(c);
        }
        if(this.grid4.contains(c)){
            grid4.remove(c);
        }
        if(this.grid5.contains(c)){
            grid5.remove(c);
        }
    }

    private int difference(int x1,int x2){
        return x2-x1;
    }
}
