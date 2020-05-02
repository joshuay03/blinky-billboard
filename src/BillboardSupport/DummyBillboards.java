package BillboardSupport;

import com.sun.source.tree.NewArrayTree;

/**
 * A class designed to provide billboards which reproduce different combinations of data
 */
public class DummyBillboards {

    public Billboard informationOnlyBillboard() {
        Billboard b = new Billboard();

        b.setMessage("Message Only!");

        return b;
    }

}
