@WebServlet("/games")
public class GameServlet extends HttpServlet {

    private JuegoDAO dao = new JuegoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        List<Juego> juegos = dao.listar();

        PrintWriter out = resp.getWriter();

        out.print("[");
        for (int i = 0; i < juegos.size(); i++) {
            Juego j = juegos.get(i);

            out.print("{");
            out.print("\"id\":\"" + j.getID() + "\",");
            out.print("\"titulo\":\"" + j.getTitulo() + "\",");
            out.print("\"consola\":\"" + j.getConsola() + "\",");
            out.print("\"precio\":" + j.getPrecio() + ",");
            out.print("\"stock\":" + j.getStock());
            out.print("}");

            if (i < juegos.size() - 1) out.print(",");
        }
        out.print("]");
    }
}