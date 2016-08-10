import javax.swing.JFrame;
public class ServerTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Server admin = new Server();
		admin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		admin.startRunning();
	}

}
