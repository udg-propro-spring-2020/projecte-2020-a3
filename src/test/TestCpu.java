import java.util.ArrayList;
import java.util.*;


public class TestCpu{
    //Descripció general:
    //Programa que prova l'algorisme minMax de la CPU
    int profundity=3;

    public static void main(String argv[]){

        ArrayList<Integer> piecesPlayer1 = new ArrayList<Integer>( Arrays.asList(1) );
        ArrayList<Integer> piecesPlayer2 = new ArrayList<Integer>( Arrays.asList(1,2) );
        Integer piece=-1;
        Integer movement=-1;
        TestCpu t=new TestCpu();
        System.out.println("minmax:"+t.minMax(0,0,piecesPlayer1,piecesPlayer2,piece,movement));
    }

    public int minMax(Integer profunditat,Integer tipusJugador,List<Integer> piecesPlayer1,List<Integer> piecesPlayer2,Integer piece,Integer movement){

        if(profunditat==profundity)return 0;
        if(tipusJugador==0){
            Integer max= Integer.MIN_VALUE;
            Iterator<Integer> it = piecesPlayer1.iterator();
            while(it.hasNext()){
                Integer valor = it.next();
                for(int i=0;i<=1;i++){
                    Integer puntuacio = valor*i /*PUNTUACIO MOVIMENT ACTUAL(de moment retornarem numero de la peça*i*/ + minMax(profunditat+1,1,piecesPlayer1,piecesPlayer2,piece,movement);
                    if(puntuacio>max){
                        max=puntuacio;
                        piece=valor;
                        movement=i;
                    }
                }
            }
            System.out.println("Nivell ("+profunditat+") Jugador1 \n\t peça escollida:"+piece+"\n\t moviment escollit:"+movement);
            return max;
        }
        else{
            Integer min=Integer.MAX_VALUE;
            Iterator<Integer> it = piecesPlayer2.iterator();
            while(it.hasNext()){
                Integer valor = it.next();
                for(int i=0;i<=1;i++){
                    Integer puntuacio = valor*-i /*PUNTUACIO MOVIMENT ACTUAL(de moment retornarem numero de la peça*i*/ + minMax(profunditat+1,0,piecesPlayer1,piecesPlayer2,piece,movement);
                    if(puntuacio<min){
                        min=puntuacio;
                        piece = valor;
                        movement=i;
                    }
                }
            }
            System.out.println("Nivell:("+profunditat+") Jugador2 \n\t peça escollida:"+piece+"\n\t moviment escollit:"+movement);
            return min;
        }
    }

}