// Paquete al que pertenece el servlet
package controllers;

// Importación de clases necesarias para el servlet
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.LoginService;
import service.LoginServiceImplement;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Optional;

// Anotación que indica las URLs que este servlet va a manejar
@WebServlet({"/login", "/login.html"})
public class LoginServlet extends HttpServlet {

    // Constantes para el usuario y contraseña válidos
    final static String USERNAME = "admin";
    final static String PASSWORD = "12345";

    // Método que maneja las solicitudes GET (por ejemplo, cuando se accede a /login directamente)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /* Obtiene todas las cookies enviadas por el cliente, si no hay, crea un arreglo vacío
        Cookie[] cookies = req.getCookies() != null ? req.getCookies() : new Cookie[0];

        // Busca si existe una cookie con el nombre "username"
        Optional<String> cookieOptional = Arrays.stream(cookies)
                .filter(c -> "username".equals(c.getName())) // Filtra por nombre "username"
                .map(Cookie::getValue)                        // Obtiene el valor de la cookie
                .findAny(); */                                  // Toma cualquier coincidencia (si existe)

        // Si la cookie existe, el usuario ya inició sesión anteriormente

        LoginService auth= new LoginServiceImplement();
        Optional<String> cookieOptional = auth.getUsername(req);
        if (cookieOptional.isPresent()) {
            // Establece el tipo de contenido de la respuesta como HTML con codificación UTF-8
            resp.setContentType("text/html;charset=UTF-8");

            // Se obtiene el escritor para escribir la respuesta
            try (PrintWriter out = resp.getWriter()) {
                // Genera la página HTML de bienvenida
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<meta charset=\"utf-8\">");
                out.println("<title>Bienvenido</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Hola " + cookieOptional.get() + " ya iniciaste sesión anteriormente!</h1>");
                out.println("<p><a href='" + req.getContextPath() + "/index.html'>Volver al inicio</a></p>");
                out.println("</body>");
                out.println("</html>");
            }
        } else {
            // Si no hay cookie, redirige al formulario login.jsp
            getServletContext().getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    // Método que maneja las solicitudes POST (cuando se envía el formulario de login)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Obtiene los valores del formulario enviados por el usuario
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // Verifica si las credenciales coinciden con las credenciales válidas
        if (username.equals(USERNAME) && password.equals(PASSWORD)) {
            // Crea una cookie con el nombre "username" y el valor del usuario autenticado
            Cookie usernameCookie = new Cookie("username", username);

            // Añade la cookie a la respuesta
            resp.addCookie(usernameCookie);

            // Establece el tipo de contenido de la respuesta como HTML con codificación UTF-8
            resp.setContentType("text/html;charset=UTF-8");

            // Se obtiene el escritor para escribir la respuesta
            try (PrintWriter out = resp.getWriter()) {
                // Muestra mensaje de bienvenida (opcional, ya que hay un redirect después)
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<meta charset=\"utf-8\">");
                out.println("<title>Bienvenido a la app</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Bienvenido a mi APP</h1>");
                out.println("</body>");
                out.println("</html>");
            }

            // Redirecciona al usuario nuevamente a /login.html (se ejecutará el método doGet)
            resp.sendRedirect(req.getContextPath() + "/login.html");

        } else {
            // Si las credenciales no son válidas, devuelve un error 401 (No autorizado)
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Lo sentimos no tiene acceso");
        }
    }
}
