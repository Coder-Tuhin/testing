package interfaces;

/**
 * Created by XTREMSOFT on 10/20/2016.
 */
public interface ItemTouchHelperAdapter {
    void onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
