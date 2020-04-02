import java.util.ArrayList;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TestCpu{
    //Descripció general:
    //Programa que prova l'algorisme minMax de la CPU
    static int profundity=3;

    public static void main(String argv[]){

        ArrayList<Integer> piecesPlayer1 = new ArrayList<Integer>( Arrays.asList(1) );
        ArrayList<Integer> piecesPlayer2 = new ArrayList<Integer>( Arrays.asList(1,2) );
        Pair<Integer, Integer>moviment=new Pair<Integer, Integer>(-1,-1);
        int[] nodesVisitats = new int[]{0};

        /**EXEMPLE D'EXPLICACIÓ**/
        System.out.println("\n-----SENSE PODA ------\n");
        long començament = System.nanoTime();
        System.out.println("minmax Puntuació escollida:"+minMax(0,0,0,piecesPlayer1,piecesPlayer2,moviment,nodesVisitats));
        long acabament = System.nanoTime();
        long temps = acabament-començament;
        System.out.println("Fitxa:"+moviment.first+"/Moviment:"+moviment.second);
        System.out.println("Nodes visitats:"+nodesVisitats[0]);
        System.out.println("Temps en segons:"+temps/1000000000);

        nodesVisitats[0]=0;
        System.out.println("\n-----AMB PODA------\n");
        començament = System.nanoTime();
        System.out.println("minmax Puntuació escollida:"+minMaxPodant(0,0,0,piecesPlayer1,piecesPlayer2,moviment,Integer.MIN_VALUE,Integer.MAX_VALUE,nodesVisitats));
        acabament = System.nanoTime();
        temps = acabament-començament;
        System.out.println("Fitxa:"+moviment.first+"/Moviment:"+moviment.second);
        System.out.println("Nodes visitats:"+nodesVisitats[0]);
        System.out.println("Temps en segons:"+temps/1000000000);

        piecesPlayer1 = new ArrayList<Integer>( Arrays.asList(21,2,20,4,19,6,14,8) );
        piecesPlayer2 = new ArrayList<Integer>( Arrays.asList(2,20,4,19,8,14,21,6) );

        profundity=4;
        /**SENSE PODA PROFUNITAT = 8**/
        System.out.println("\n-----SENSE PODA ------\n");
        començament = System.nanoTime();
        System.out.println("minmax Puntuació escollida:"+minMax(0,0,0,piecesPlayer1,piecesPlayer2,moviment,nodesVisitats));
        acabament = System.nanoTime();
        temps = acabament-començament;
        System.out.println("Fitxa:"+moviment.first+"/Moviment:"+moviment.second);
        System.out.println("Nodes visitats:"+nodesVisitats[0]);
        System.out.println("Temps en segons:"+temps/1000000000);

        profundity=4;
        /**PODA PROFUNITAT = 8*/
        nodesVisitats[0]=0;
        System.out.println("\n-----AMB PODA------\n");
        començament = System.nanoTime();
        System.out.println("minmax Puntuació escollida:"+minMaxPodant(0,0,0,piecesPlayer1,piecesPlayer2,moviment,Integer.MIN_VALUE,Integer.MAX_VALUE,nodesVisitats));
        acabament = System.nanoTime();
        temps = acabament-començament;
        System.out.println("Fitxa:"+moviment.first+"/Moviment:"+moviment.second);
        System.out.println("Nodes visitats:"+nodesVisitats[0]);
        System.out.println("Temps en segons:"+temps/1000000000);
    }




    public static int minMax(int puntuacio,Integer profunditat,Integer tipusJugador,List<Integer> piecesPlayer1,List<Integer> piecesPlayer2,Pair<Integer, Integer>moviment,int[] nodesVisitats){

        if(profunditat==profundity)return puntuacio;
        else if(tipusJugador==0){
            Integer max= Integer.MIN_VALUE;
            Iterator<Integer> it = piecesPlayer1.iterator();
            while(it.hasNext()){
                Integer valor = it.next();
                for(int i=0;i<=1;i++){
                    nodesVisitats[0]++;
                    Integer score = valor*i + puntuacio;
                    score = minMax(score,profunditat+1,1,piecesPlayer1,piecesPlayer2,moviment,nodesVisitats);
                    if(score>max){
                        max=score;
                        moviment.first=valor;
                        moviment.second=i;
                    }
                }
            }
            return max;
        }
        else{
            Integer min=Integer.MAX_VALUE;
            Iterator<Integer> it = piecesPlayer2.iterator();
            while(it.hasNext()){
                Integer valor = it.next();
                for(int i=0;i<=1;i++){
                    nodesVisitats[0]++;
                    Integer score = valor*-i + puntuacio;
                    score = minMax(score,profunditat+1,0,piecesPlayer1,piecesPlayer2,moviment,nodesVisitats);
                    if(score<min){
                        min=score;
                        moviment.first = valor;
                        moviment.second =i;
                    }
                }
            }
            return min;
        }
    }


    public static int minMaxPodant(int puntuacio,Integer profunditat,Integer tipusJugador,List<Integer> piecesPlayer1,List<Integer> piecesPlayer2,Pair<Integer, Integer>moviment,int mesGranAnterior,int mesPetitAnterior,int[] nodesVisitats){

        if(profunditat==profundity)return puntuacio;
        else if(tipusJugador==0){
            Integer max= Integer.MIN_VALUE;
            Iterator<Integer> it = piecesPlayer1.iterator();
            while(it.hasNext()){
                Integer valor = it.next();
                for(int i=0;i<=1;i++){
                    nodesVisitats[0]++;
                    Integer score = valor*i + puntuacio;
                    score = minMaxPodant(score,profunditat+1,1,piecesPlayer1,piecesPlayer2,moviment,mesGranAnterior,mesPetitAnterior,nodesVisitats);
                    if(score>max){
                        mesGranAnterior=score;
                        max=score;
                        moviment.first=valor;
                        moviment.second=i;
                    }
                    //System.out.println("Nivell ("+profunditat+") Jugador1 \n\t peça actual:"+valor+"\n\t moviment escollit:"+i+"\n\t puntuacio avaluada:"+score);
                    if(mesPetitAnterior<=mesGranAnterior){/*System.out.println("petit:"+mesPetitAnterior+" gran:"+mesGranAnterior);System.out.println("j1Tallo");*/break;}
                }
            }
            return max;
        }
        else{
            Integer min=Integer.MAX_VALUE;
            Iterator<Integer> it = piecesPlayer2.iterator();
            while(it.hasNext()){
                Integer valor = it.next();
                for(int i=0;i<=1;i++){
                    nodesVisitats[0]++;
                    Integer score = valor*-i + puntuacio;
                    score = minMaxPodant(score,profunditat+1,0,piecesPlayer1,piecesPlayer2,moviment,mesGranAnterior,mesPetitAnterior,nodesVisitats);
                    if(score<min){
                        mesPetitAnterior=score;
                        min=score;
                        moviment.first = valor;
                        moviment.second =i;
                    }
                    //System.out.println("Nivell:("+profunditat+") Jugador2 \n\t peça escollida:"+valor+"\n\t moviment escollit:"+i+"\n\t puntuacio avaluada:"+score);
                    if(mesGranAnterior>=mesPetitAnterior){/*System.out.println("petit:"+mesPetitAnterior+" gran:"+mesGranAnterior);System.out.println("j2Tallo");*/break;}
                }
            }
            return min;
        }
    }

}