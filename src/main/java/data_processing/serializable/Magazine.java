package data_processing.serializable;

public class Magazine {

    private int id;
    private int magazineSize;
    //private int[] slotSize;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMagazineSize() {
        return magazineSize;
    }

    public void setMagazineSize(int magazineSize) {
        this.magazineSize = magazineSize;
    }

    @Override
    public String toString() {
        return "Magazine{" +
                "id=" + id +
                ", magazineSize=" + magazineSize +
                '}';
    }
}
