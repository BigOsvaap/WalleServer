package WalleServer.persistence;

import java.util.ArrayList;

public class JSONArrayList<T> extends ArrayList<T> {

    @Override
    public String toString() {
        var strBuilder = new StringBuilder();
        strBuilder.append("[");
        for (int i = 0; i < this.size(); i++) {
            strBuilder.append(this.get(i).toString());
            if (i != this.size() - 1){
                strBuilder.append(",");
            }
        }
        strBuilder.append("]");
        return strBuilder.toString();
    }

}
