import java.util.EnumSet;
import java.util.Iterator;
import java.util.ArrayList;

public class Parking implements Iterable<Car> {
    private enum _Direction {
        FORWARD,
        BACKWARD
    }

    private int _xSize;
    private int _ySize;
    private int[] _exit;
    private boolean _isWin = false;
    private Car _goal_car;
    private ArrayList<Car> _carList = new ArrayList<Car>(0);
    private int[][] parkingMatrix;

    public boolean is_won () {
        return this._isWin; 
    }

    /* http://stackoverflow.com/questions/5849154/can-we-write-our-own-iterator-in-java
     * @desc Iterateur sur les véhicules de la classe pour acceder
     *      au différents véhicules en dehors de la classe. 
     */
    @Override
    public Iterator<Car> iterator () {
        Iterator<Car> it = new Iterator<Car>() {

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < _carList.size();
            }

            @Override
            public Car next() {
                ++currentIndex;
                return _carList.get(currentIndex);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return it;
    }

    /* @desc Renvoie la partie la plus avancée lorsqu'il s'agit
     *      d'avancer la voiture.
     *
     * @param {catToGet} : La voiture sur laquel il faut trouver les coord.
     *
     * @return {int[]} Renvoie les coordonnées.
     */
    private int[] _get_head_f(Car carToGet) {
        int[] head = {0, 0};
        for ( int[] pos : carToGet ) {
            if ( (pos[0] > head[0]) || (pos[1] > head[1]) ) {
                head = pos;
            }
        }
        return head;
    }

    /* @desc Renvoie la partie la plus avancée lorsqu'il s'agit de 
     *      reculer la voiture.
     *
     * @param {catToGet} : La voiture sur laquel il faut trouver les coord.
     *
     * @return {int[]} Renvoie les coordonnés.
     */
    private int[] _get_head_b(Car carToGet) {
        int[] head = {this.parkingMatrix.length, this.parkingMatrix.length};
        for ( int[] pos : carToGet ) {
            if ( (pos[0] < head[0]) || (pos[1] < head[1]) ) {
                head = pos;
            }
        }
        return head;
    }

    /* @desc Cette fonction permet de vérifier si suite à un déplacement,
     *      l'on a fait l'on se trouve toujours dans une configuration valide
     *      du parking, càd dans les limites du parking et en ne touchant pas
     *      d'autre véhicule.
     *
     * @return {Boolean} Vrai si configuration correct, faux si configuration
     *      incorrect.
     */
    private boolean _check_movement( Car movedCar, _Direction dir ) {
        // On commence par recherche la "tête" du véhicule.
        int[] head = new int[2];
        if (dir == _Direction.FORWARD) {
            // Si FORWARD il faut trouver la plus grande coord.
            head = this._get_head_f(movedCar);
        } else {
            // Si BACKWARD il faut trouver la plus petite coord.
            head = this._get_head_b(movedCar);
        }
        return ( (this.parkingMatrix[head[0]][head[1]] == 0)
            && (0 <= head[0] && head[0] < this._xSize)
            && (0 <= head[1] && head[1] < this._ySize) );
    }

    /* @desc Renvoie un la manière dont le parking serait si
     *      la voiture {toMoveCar} était bougée en avant.
     *
     * @param {toMoveCar} : Voiture que l'on va devoir bouger.
     *
     * @return {Parking} : La nouvelle forme du parking.
     */
    public Parking move_forward( Car toMoveCar ) {
        // Vérification que la voiture sait avancer.
        Car newCarPos = toMoveCar.forward();

        if (!this._check_movement(toMoveCar, _Direction.FORWARD)) {
            System.out.println("null");
            return null;
        }

        // COPY de {_carList} mais en remplaçant {toMoveCar}.
        ArrayList<Car> result = new ArrayList<Car>( this._carList.size() );

        for ( int i = 0; i < this._carList.size(); ++i ) {
            if ( this._carList.get(i) == toMoveCar ) {
                result.set(i, newCarPos);
            } else {
                result.set(i, this._carList.get(i)); // TODO .clone());
            }
        }

        return new Parking(this._xSize, this._ySize, this._exit, result);
    }

    /* @desc Renvoie un la manière dont le parking serait si
     *      la voiture {toMoveCar} était bougée en arrière.
     *
     * @param {toMoveCar} : Voiture que l'on va devoir bouger.
     *
     * @return {Parking} : La nouvelle forme du parking.
     */
    public Parking move_backward( Car toMoveCar ) {
        // Vérification que la voiture sait avancer.
        Car newCarPos = toMoveCar.backward();

        if (!this._check_movement(toMoveCar, _Direction.BACKWARD)) {
            return null;
        }

        // COPY de {_carList} mais en remplaçant {toMoveCar}.
        ArrayList<Car> result = new ArrayList<Car>( this._carList.size() );

        for ( int i = 0; i < this._carList.size(); ++i ) {
            if ( this._carList.get(i) == toMoveCar ) {
                result.set(i, newCarPos);
            } else {
                result.set(i, this._carList.get(i)); // TODO .clone());
            }
        }

        return new Parking(this._xSize, this._ySize, this._exit, result);
    }

    public Car set_goal_car (ArrayList<Integer> xPos, ArrayList<Integer> yPos) {
        this._goal_car = this.add_car (xPos, yPos); 

        return this._goal_car;
    }

    /* @desc Ajoute une voiture au parking, ainsi qu'à la matrice du parking.
     *
     * NB : Une voiture prend toujours au moins 2 positions dans le parking,
     *      une voiture de taille 1 n'existe pas car il serait alors impossible
     *      de déterminer dans quel sens elle est orienté.
     *
     * @param {xPos} : Liste des positions en abscisse de la voiture.
     * @param {yPos} : Liste des positions en ordonnée de la voiture.
     *
     * @return {Car} : Renvoie cette nouvelle voiture crée avec les paramêtres.
     */
    public Car add_car (ArrayList<Integer> xPos, ArrayList<Integer> yPos) {
        Car newCar = new Car(xPos, yPos);
        for ( int[] pos : newCar ) {
            if (this.parkingMatrix[pos[0]][pos[1]] > 0) {
                
                // TODO error checking throw "Position déjà occupée par une autre voiture.";
            } else {
                this.parkingMatrix[pos[0]][pos[1]] = newCar.get_num(); 
            }
        }

        this._carList.add( newCar );

        return newCar;
    }
    private String make_line(String c,int fin){
        String chaine="";
        for (int i=0;i<fin;++i){
            chaine+=c;
        }
        return chaine;
    }

    /* @desc Permet la représentation du parking sous la forme d'un dessin.
     *      Ex pour la taille 4x4:
     *      +---+---+---+---+
     *      |   |   |   | c2|
     *      +   +   +   +   +
     *      |   |  G|  G| c2
     *      +   +   +   +   +
     *      |   |   | c1| c1|
     *      +   +   +   +   +
     *      |   |   |   |   |
     *      +---+---+---+---+
     *
     * @return {String} Renvoie la représentation du parking.
     */
    @Override
    public String toString() {
        // Création des limites de la grille: "+---+---+---+---+\n"
        String delimiter = "";
        //for (int i = 0; i < this._xSize; ++i) {
          //  delimiter += "---+";
        ///}
        //delimiter += "\n";
        //String res="";
        delimiter+=this.make_line("+---",this._xSize);
        delimiter+="+\n";
        String result="";
        int taille=delimiter.length();
        int j=1;
        for (int i=1;i<=this._ySize*2-1;++i){
            if(i%2==1){
                result+=this.is_exit(i-j,0);
                result+=this.is_car(i-j,taille-3);
                result+=this.is_exit(i-j,1);
                result+="\n"; 
            }
            else if(i%2==0){
                ++j;
                result+="+";
                result+=this.make_line("   +", this._xSize);
                result+="\n";
            }   
        }
        result=delimiter+result+delimiter;
        return result;
    }
    private String is_car(int ligne,int fin){
        String res="";
        for (int j = 0; j < this._xSize; ++j) {
            int carNum = this.parkingMatrix[j][ligne];
            if ( carNum > 0 ) {
                if (carNum==1){
                  res+=" G ";
                  //res+= String.format("%3s", "G");
                }
                else if(carNum>1){
                  res +=" V"+carNum;
              }
            }
            else{ 
                res+="   ";
            }
            if (j==this._xSize-1){
            res+="";
            }
            else{
              res+=" ";
            }

          }
          res+=make_line(" ",fin-res.length());
        return res;
  }
    private String is_exit(int ligne,int mode){
        String res="";
        if (mode==0){
            if (this._exit[0]==0 && ligne==this._exit[1]){
                res+=" ";
            }
            else{
                res+="|";   
            }
        }
        else if(mode==1){
            if (this._exit[0]==this._xSize-1 && this._exit[1]==ligne){
                res+=" ";
            }
            else{
                res+="|";   
                
            }
        }
        return res;
    }

    /* @desc Constructeur du parking avec les informations de base.
     *
     * @param {_xSize} : Taille horizontale du parking.
     * @param {_ySize} : Taille verticale du parking.
     * @param {exit} : Coordonnée [x, y] de la sortie du parking.
     */
    Parking (int _xSize, int _ySize, int[] exit) {
        this._xSize = _xSize;
        this._ySize = _ySize;
        this._exit=exit.clone();

        this.parkingMatrix = new int[_xSize][_ySize];
    }

    /* @desc Constructeur du parking, où l'on passe directement toutes les voitures
     *      et le constructeur s'occupe de directement créer sa matrice.
     *
     * @param {_xSize} : Taille horizontale du parking.
     * @param {_ySize} : Taille verticale du parking.
     * @param {exit} : Coordonnée [x, y] de la sortie du parking.
     * @param {_carList} : Liste des voitures présentes dans le parking,
     *      à utilisé pour calculer la matrice directement.
     */
    Parking (int _xSize, int _ySize, int[] exit, ArrayList<Car> _carList) {
        this._xSize = _xSize;
        this._ySize = _ySize;
        this._carList = _carList;
        this._exit = exit.clone();

        // On place les voitures dans la matrice.
        this.parkingMatrix = new int[_xSize][_ySize];
        for ( Car car : _carList ) {
            for ( int[] pos : car ) {
                if (this.parkingMatrix[pos[0]][pos[1]] > 0) {
                    // throw "Position déjà occupée par une autre voiture.";
                } else {
                    this.parkingMatrix[pos[0]][pos[1]] = car.get_num();
                }
            }
        }

        // Ajout de la voiture "goal" 
        if (_carList.size() > 0) {
            // La voiture "goal" qui est toujours dans la liste à l'index "0".
            this._goal_car = _carList.get(0);
            this._isWin = true;

            // Vérification que le parking est une configuration gagnant ou non.
            if (this._exit[0] == 0) {
                // Si la sortie à été définie à gauche. 
                int[] currentPos = this._get_head_b(this._goal_car);
                for (int i = currentPos[0]; i >= 0; --i) {
                    if ( this.parkingMatrix[i][currentPos[1]] > 0 ) {
                        // Si il y a un obstacle sur le chemin on ne sait pas gagner
                        this._isWin = false;
                        break;
                    }
                }
            } else {
                // Si la sortie à été définie à droite.
                int[] currentPos = this._get_head_f(this._goal_car);
                for (int i = currentPos[0]; i < this._xSize; ++i) {
                    if ( this.parkingMatrix[i][currentPos[1]] > 0 ) {
                        // Si il y a un obstacle sur le chemin on ne sait pas gagner
                        this._isWin = false;
                        break;
                    }
                }
            }
        } else {
            this._isWin = false;
        }
    }
}
