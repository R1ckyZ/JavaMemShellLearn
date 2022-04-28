import org.apache.tomcat.util.codec.binary.Base64;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class IndexServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String message = "tomcat index servlet test";
		String id = req.getParameter("id");

		StringBuilder sb = new StringBuilder();
		sb.append(message);
		if (id != null && !id.isEmpty()) {
			sb.append("\nid: ").append(id);
		}

		resp.getWriter().println(sb);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		byte[] poc = Base64.decodeBase64(req.getParameter("poc"));
		try {
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(poc));
			ois.readObject();
			ois.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
