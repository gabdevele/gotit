package schiraldi.gabriele.socket;


public abstract class SocketDefaults{
    protected final String SERVER_IP= Utils.getServerConfig().get("SERVER_IP");
    protected final int SERVER_PORT= Integer.parseInt(Utils.getServerConfig().get("SERVER_PORT"));

    public abstract void start();
}