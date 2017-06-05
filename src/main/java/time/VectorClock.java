package time;

import peersim.core.Network;

import java.util.HashMap;

public class VectorClock implements Cloneable, Comparable<VectorClock> {

    private long id_node;

    private HashMap<Long, Integer> vectors;

    /**
     * Allocates a vector of Network.size() items all of them set to 0
     * For example if Network.size() is equal to 4 then the result is [0,0,0,0]
     * @param id_node the id of the node in which we need a new VectorClock for a particular event
     */
    public VectorClock(long id_node) {
        this.id_node = id_node;
        vectors = new HashMap<Long, Integer>(Network.size());
    }

    public VectorClock(long id_node, HashMap<Long, Integer> vectors) {
        this.id_node = id_node;
        this.vectors = vectors;
    }

    public void increment() {
        vectors.put(id_node - 1, vectors.get(id_node - 1) + 1);
    }

//    private void update(VectorClock last) {
//        for (int i = 0; i < Network.size(); i++) {
//
//            vectors.put(i, )
//
//            vectors[i] = last.getElement(i);
//        }
//        vectors[(int)last.getId_node()] += 1;
//    }

    public int getElement(int index){
        return vectors.get(index);
    }

    public long getId_node() {
        return id_node;
    }

    public void setId_node(long id_node) {
        this.id_node = id_node;
    }

    public boolean isConcurrent(VectorClock vc) {
        long vc_id_node = vc.getId_node();
        return this.getElement((int)id_node - 1) > vc.getElement((int)id_node - 1) && vc.getElement((int)vc_id_node - 1) > this.getElement((int)vc_id_node - 1);
    }

    @Override
    public String toString() {

        StringBuilder ris = new StringBuilder("[");
        for (int i = 0; i < (vectors.size() - 1) ; i++) {
            ris.append(vectors.get(i) + ",");
        }
        ris.append("]");
        return ris.toString();
    }

    @Override
    public VectorClock clone() throws CloneNotSupportedException {
        super.clone();
        return new VectorClock(id_node, (HashMap<Long, Integer>) vectors.clone());
    }

    public int compareTo(VectorClock vc) {

        if (id_node == vc.id_node) {
            int el1 = vectors.get(id_node);
            int el2 = vc.vectors.get(vc.id_node);
            return (el1 < el2) ? -1 : ((el1 == el2) ? 0 : 1);
        }
        return 0;
    }

}