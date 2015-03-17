package test;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class HangingServlet
 */
public class HangingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public HangingServlet() {
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    // don't set a content length so we use chunked encoding
	    if (request.getParameter("delay") == null) {
	        response.setContentType("text/plain");
	        PrintWriter writer = response.getWriter();
	        writer.println("This servlet takes the following parameters (GET)");
	        writer.println("delay the time in ms to delay between loops");
	        writer.println("loops the number of times to looop");
	        writer.println("crash (optional) set if we should throw an exception after we have finished looping");
            writer.println("close (optional) set if we should close the response after we have finished looping");
            writer.println();
            writer.println("e.g. get  hang?delay=20000&loops=10&close");
            writer.println("\r\n\r\n");
            writer.println("The purpose of this servlet is to test the error handling scenarious of various containers.");
            writer.println("Tthe responses should be sent with Transfer-Enconding: chunked");
            writer.println("If crash is set then it is important that the client is notified that something failed.");
            writer.println("The spec is not clear on this however sending a terminating chunk (a chunk of size zero) is certainly not telling the client this");
            writer.println("The expecteation would be something like not sending the terminating chunk and killing the connection.");
            return;
	    }
	    int sleep = Integer.parseInt(request.getParameter("delay"));
	    int crash = Integer.parseInt(request.getParameter("loops"));
	    
	    response.setContentType("text/plain");
	    for (int i=0; i<crash; i++) {
	        System.out.println("sending some content... ("+i+"<"+crash+")");
	        response.getWriter().print("James Was here!");
	        response.getWriter().flush();
	        try {
	            Thread.sleep(sleep);
	        }
	        catch (InterruptedException ex) {
	            throw new IOException(ex);
	        }
	    }
	    if (request.getParameter("crash") != null) {
	        System.out.println("throwing exception");
	        throw new IllegalArgumentException("crash");
	    }
	    if (request.getParameter("close") != null) {
	        System.out.println("closing connection");
	        response.getOutputStream().close();
	        System.out.println("closed connection");
	        return;
	    }
	    System.out.println("method completing");
	}

}
