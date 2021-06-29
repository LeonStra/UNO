//Zum Testen des Programms

public class InfUnoTest {

    /*public static void main(String[] args) {
        System.out.println("Start");

        //"Datenmodell"
        LinkedList<Card> drawPile = new LinkedList<Card>();
        LinkedList<Card> playPile = new LinkedList<Card>();
        LinkedList<Player> players= new LinkedList<Player>();
        Integer drawCount = 0; //Adapterklasse, damit die Zahl global aktualisiert wird

        //Spieler
        Player player1 = new Player(drawPile,playPile,players,drawCount);
        Player player2 = new Player(drawPile,playPile,players,drawCount);


        //Start
        //Typen vorbereiten
        List<TYPE> typeList = Arrays.stream(TYPE.values()).toList();
        typeList.removeAll(TYPE.getExcluded());

        //Farben vorbereiten
        List<COLOR> colorList = Arrays.stream(COLOR.values()).toList();
        colorList.removeAll(COLOR.getExcluded());

        //Deck bilden
        for (int i=0;i<2;i++){
            for(TYPE t : typeList) {
                for (COLOR c : colorList) {
                    if (t == TYPE.ZERO){continue;} //Nur eine Null pro Farbe
                    drawPile.add(new Card(t, c));
                }
            }
        }
        //Wunschkarten
        for (int i=0;i<4;i++){
            drawPile.add(new Card(TYPE.WILD,COLOR.MULTICOLORED));
            drawPile.add(new Card(TYPE.WILDFOUR,COLOR.MULTICOLORED));
        }
        Collections.shuffle(drawPile);
    }*/

}
