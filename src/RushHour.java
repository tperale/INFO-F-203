import java.util.ArrayList;
import java.util.LinkedList;

public class RushHour {
    // private Parking baseParking;
    private ArrayList<ArrayList<Integer>> parkingGraph = new ArrayList<>();
    // Chaques configuration de parking vont être stockée ici.
    private ArrayList<Parking> parkingList = new ArrayList<Parking>();

    // TODO Mettre les opération sur les matrices dans une classe ou quelque
    // chose comme ça pour une lecture plus facile du code.
    /* @desc Ajoute une entrée (pour le sommet) dans le graph.
     */
    private void _add_entry_to_graph () {
        // Ajout à chaque colonne d'une nouvelle entrée.
        for ( int i = 0; i < parkingGraph.size(); ++i ) {
            this.parkingGraph.get(i).add(0);
        }

        // Création de la nouvelle colonne
        int newSize = (parkingGraph.size() + 1);
        ArrayList<Integer> newColumn = new ArrayList<>();
        for ( int i = 0; i < (this.parkingGraph.size() + 1); ++i ) {
            newColumn.add(0);
        }
        this.parkingGraph.add(newColumn);
    }

    /* @desc Place dans le graph que l'on vient de générer.
     *
     * @param {newParking} : Nouveau parking à placer dans le graphe.
     *
     * @return {index} du nouvel élément si on a dû l'ajouter, sinon -1.
     */
    private int _put_in_graph (Parking newParking) {
        int i = 0;
        // Recherche pour savoir si il y a déjà un {newParking} dans la liste.
        while ( (i < this.parkingList.size()) && !parkingList.get(i).equals(newParking) ) {
            ++i;
        }
        if (i == this.parkingList.size()) {
            // Si il n'est pas dans la liste.
            parkingList.add(newParking);
            this._add_entry_to_graph(); // Rajoute de la place pour {i}
        }

        return i;
    }

    /* @desc Génère récursivement toutes les configurations 
     *      possible de parking.
     *
     * @param {parking_conf} : Un objet parking qui contient 
     *      la configuration d'un parking (position voiture
     *      + taille du parking).
     *
     *  @return {Index du noeud} : Retourne l'index du noeud qui a été traité.
     *      
     */
    public int generate_parkings(Parking parkingConf) {
        int i = this._put_in_graph (parkingConf);
        if ( i < (parkingList.size() - 1) ) {
            // Si i est plus petit que la taille de "parkingList - 1"
            // ça veut dire que l'on a déjà traité ce sommet, ou qu'il est
            // occupé d'être traité.
            return i;
        }
        if ( parkingConf.is_won() ) {
            // Inutile de faire plus de génération si la partie est gagnée
            // Les graphes généré seront redondant car ils sont des sous-cas
            // d'un cas gagnant.
            return i;
        }

        int j; // Sert à savoir quel noeud à été ajouté
        for ( Car specific_car : parkingConf ) {
            // On avance la voiture tant que c'est possible.
            Parking tmp_parking_conf = parkingConf.move_forward( specific_car );
            while ( tmp_parking_conf != null ) {
                j = this.generate_parkings( tmp_parking_conf );
                // Création d'une arrête entre les deux en indiquant quel
                // voiture à été bougée. 
                this.parkingGraph.get(i).set( j, specific_car.get_num() );

                tmp_parking_conf = tmp_parking_conf.move_forward( specific_car );
            }

            // On recule la voiture tant que c'est possible.
            tmp_parking_conf = parkingConf.move_backward( specific_car );
            while ( tmp_parking_conf != null ) {
                j = this.generate_parkings( tmp_parking_conf );
                // Création d'une arrête entre les deux en indiquant quel
                // voiture à été bougée. 
                this.parkingGraph.get(i).set( j, specific_car.get_num() );

                tmp_parking_conf = tmp_parking_conf.move_backward( specific_car );
            }
        }
        return i;
    }

    /* @desc Parcour en breadth first du graph pour trouver le chemin le plus
     *      court pour accêder à la sortie.
     *
     * @param {baseParking} : Configuration du parking au départ.
     *
     * @return {Parking[]} La trajet à travers les différentes configurations
     *      de parking pour arriver à la sortie.
     */
    public Parking[] find_shortest_path (int baseParking) {
        // Permet de marquer si un noeud a dejà été traité ou non.
        boolean[] nodeMark = new boolean[this.parkingList.size()];

        // Va stocker l'antécédent à chaques noeuds traité.
        // À chaque "case" de ce tableau il va être stocké
        // l'index de son antécédent.
        int[] nodePath = new int[this.parkingList.size()];
        nodePath[0] = 0;

        // Va stocker la distance par apport à la base.
        int[] countNode = new int[this.parkingList.size()];
        countNode[0] = 0;

        // Stock les noeuds à traiter.
        LinkedList<Integer> queue = new LinkedList<>();
        int currentNode = baseParking;

        while ( !(this.parkingList.get(currentNode).is_won()) ) {
            for (int i = 0; i < this.parkingList.size(); ++i) {
                if ( this.parkingGraph.get(currentNode).get(i) > 0 && !(nodeMark[i]) ) {
                    // Si le noeud est accessible et n'a pas encore été traité.
                    // System.out.println("POUR " + currentNode + " : " + i);
                    nodeMark[i] = true;
                    nodePath[i] = currentNode; 
                    countNode[i] = (countNode[currentNode] + 1);
                    queue.add(i);
                }
            }
            
            if ( queue.size() > 0 ) {
                currentNode = queue.poll();
            } else {
                // Si on est arrivé au dernier noeud et qu'on a toujours 
                // pas trouvé de chemin gagnant.
                return null;
            }
        }
        // Si l'on sort normalement de la boucle, on a trouvé un chemin.
        // Il faut désormait créer une liste avec tout les chemins emprunté
        int resultSize = countNode[currentNode];
        Parking[] result = new Parking[resultSize + 1];
        // Ajouter 1 car le count commence à 0 pour des raisons de simplicité.

        // Création de la liste résultat, contenant le trajet dans l'ordre.
        for (int i = resultSize; i >= 0; --i) {
            result[i] = this.parkingList.get(currentNode);
            currentNode = nodePath[currentNode];
        }
        return result;
    }

    RushHour() {
    }

    public static void main (String[] args) {
        if ( args.length > 0) {
            RushHour main = new RushHour();

            ParkingIN parsedParking = new ParkingIN(args[0]);
            Parking baseParking = parsedParking.parse_input_file();
            // System.out.println(baseParking);
            main.generate_parkings(baseParking);
            Parking[] result = main.find_shortest_path(0);

            System.out.println("--------------------------------\n\n");
            // Pour le test.
            for (int i = 0; i < result.length; ++i) {
                System.out.println(result[i]);
            }
            // ------------

        } else {
            System.out.println("Veuillez passer le fichier en argument.");
        }
    }
}
