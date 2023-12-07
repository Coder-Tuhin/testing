package connection;

/**
 * Created by Admin on 03/02/2016.
 */
public interface ConnectionProcess {
    void connected();
    void serverNotAvailable();
    void sensexNiftyCame();

}
