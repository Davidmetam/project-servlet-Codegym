package com.tictactoe;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession currentSession = req.getSession();

        Field field = extractField(currentSession);

        int index = Integer.parseInt(req.getParameter("click"));

        if(field.getField().get(index) != Sign.EMPTY){
            resp.sendRedirect("/index.jsp");
            return;
        }

        field.getField().put(index, Sign.CROSS);
        if (checkWin(resp, currentSession, field)) {
            return;
        }

        int NoughtIndex = field.getEmptyFieldIndex();

        if (NoughtIndex >= 0) {
            field.getField().put(NoughtIndex, Sign.NOUGHT);
            if (checkWin(resp, currentSession, field)) {
                return;
            }
        }else {
            currentSession.setAttribute("draw", true);
            List<Sign> data = field.getFieldData();
            currentSession.setAttribute("data", data);
            resp.sendRedirect("/index.jsp");
            return;
        }


        List<Sign> data = field.getFieldData();

        currentSession.setAttribute("data", data);
        currentSession.setAttribute("field", field);

        resp.sendRedirect("/index.jsp");
    }


    private Field extractField(HttpSession currentSession) {
        Object fieldAttribute = currentSession.getAttribute("field");
        if (Field.class != fieldAttribute.getClass()) {
            currentSession.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return (Field) fieldAttribute;
    }

    private int getNoughtIndex(HttpServletRequest req){
        HttpSession currentSession = req.getSession();

        Field field = extractField(currentSession);
        int NoughtIndex = field.getEmptyFieldIndex();
        if(NoughtIndex < 0){
            getNoughtIndex(req);
        }
        return NoughtIndex;
    }

    private boolean checkWin(HttpServletResponse response, HttpSession currentSession, Field field)throws IOException{
        Sign winner = field.checkWin();
        if (Sign.CROSS == winner || Sign.NOUGHT == winner){
            currentSession.setAttribute("winner", winner);

            List<Sign> data = field.getFieldData();

            currentSession.setAttribute("data", data);

            response.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }


}
