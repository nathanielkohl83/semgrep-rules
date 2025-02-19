/**
* OWASP Benchmark Project v1.2
*
* This file is part of the Open Web Application Security Project (OWASP)
* Benchmark Project. For details, please see
* <a href="https://www.owasp.org/index.php/Benchmark">https://www.owasp.org/index.php/Benchmark</a>.
*
* The OWASP Benchmark is free software: you can redistribute it and/or modify it under the terms
* of the GNU General Public License as published by the Free Software Foundation, version 2.
*
* The OWASP Benchmark is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* @author Nick Sanidas <a href="https://www.aspectsecurity.com">Aspect Security</a>
* @created 2015
*/

/** From: Iago
   Java/tainting:
   This is a test that comes from the OWASP Benchmark v1.2.
   Here DeepSemgrep doesn't report lines 56, 128, and 195.
   But these are actually false positives! This benchmark tries
   to confuse analyzers into reporting these false positives.
   It does this in two ways, 1) by using a third-function
   `doSomething` that receives tainted data, even though it
   returns safe data; and 2) by putting both safe and unsafe
   data into a `HashMap`, but ultimately only returning the
   safe data. FOSS Semgrep falls into the first trap.
   DeepSemgrep does inter-procedural analysis so it is only
   affected by the second trap, but it seems to not fall
   into it because we are lacking a `pattern-propagators` spec
   for `HashMap`s. If we told DeepSemgrep that `HashMap`s
   store/propagate taint, then it should report the same
   false positives.
*/

package org.owasp.benchmark.testcode;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value="/xss-04/BenchmarkTest02229")
public class BenchmarkTest02229 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        java.util.Map<String,String[]> map = request.getParameterMap();
        String param = "";
        if (!map.isEmpty()) {
            String[] values = map.get("BenchmarkTest02229");
            if (values != null) param = values[0];
        }


        String bar = doSomething(request, param);

response.setHeader("X-XSS-Protection", "0");
        Object[] obj = { "a", bar};
        // NOTE: see comment at start of file
        // ruleid: deepok: no-direct-response-writer
        response.getWriter().printf(java.util.Locale.US,"Formatted like: %1$s and %2$s.",obj);
    }  // end doPost


    private static String doSomething(HttpServletRequest request, String param) throws ServletException, IOException {

        String bar = "safe!";
        java.util.HashMap<String,Object> map26903 = new java.util.HashMap<String,Object>();
        map26903.put("keyA-26903", "a_Value"); // put some stuff in the collection
        map26903.put("keyB-26903", param); // put it in a collection
        map26903.put("keyC", "another_Value"); // put some stuff in the collection
        bar = (String)map26903.get("keyB-26903"); // get it back out
        bar = (String)map26903.get("keyA-26903"); // get safe value back out

        return bar;
    }
}

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value="/hash-02/BenchmarkTest02388")
public class BenchmarkTest02388 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        org.owasp.benchmark.helpers.SeparateClassRequest scr = new org.owasp.benchmark.helpers.SeparateClassRequest( request );
        String param = scr.getTheParameter("BenchmarkTest02388");
        if (param == null) param = "";

        String bar = doSomething(request, param);

        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] input = { (byte)'?' };
            Object inputParam = bar;
            if (inputParam instanceof String) input = ((String) inputParam).getBytes();
            if (inputParam instanceof java.io.InputStream) {
                byte[] strInput = new byte[1000];
                int i = ((java.io.InputStream) inputParam).read(strInput);
                if (i == -1) {
                    // ok: no-direct-response-writer
                    response.getWriter().println(
"This input source requires a POST, not a GET. Incompatible UI for the InputStream source."
);
                    return;
                }
                input = java.util.Arrays.copyOf(strInput, i);
            }
            md.update(input);

            byte[] result = md.digest();
            java.io.File fileTarget = new java.io.File(
                    new java.io.File(org.owasp.benchmark.helpers.Utils.testfileDir),"passwordFile.txt");
            java.io.FileWriter fw = new java.io.FileWriter(fileTarget,true); //the true will append the new data
                fw.write("hash_value=" + org.owasp.esapi.ESAPI.encoder().encodeForBase64(result, true) + "\n");
            fw.close();
            response.getWriter().println(
            // ok: no-direct-response-writer
            "Sensitive value '" + org.owasp.esapi.ESAPI.encoder().encodeForHTML(new String(input)) + "' hashed and stored<br/>"
);

        } catch (java.security.NoSuchAlgorithmException e) {
            System.out.println("Problem executing hash - TestCase");
            throw new ServletException(e);
        }

        // OK because constant string
        // ok: no-direct-response-writer
        response.getWriter().println(
"Hash Test java.security.MessageDigest.getInstance(java.lang.String) executed"
);
    }  // end doPost


    private static String doSomething(HttpServletRequest request, String param) throws ServletException, IOException {

        String bar = "safe!";
        java.util.HashMap<String,Object> map94322 = new java.util.HashMap<String,Object>();
        map94322.put("keyA-94322", "a_Value"); // put some stuff in the collection
        map94322.put("keyB-94322", param); // put it in a collection
        map94322.put("keyC", "another_Value"); // put some stuff in the collection
        bar = (String)map94322.get("keyB-94322"); // get it back out
        bar = (String)map94322.get("keyA-94322"); // get safe value back out

        return bar;
    }
}


import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value = "/xss-04/BenchmarkTest02229")
public class BenchmarkTest02229 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        java.util.Map<String, String[]> map = request.getParameterMap();
        String param = "";
        if (!map.isEmpty()) {
            String[] values = map.get("BenchmarkTest02229");
            if (values != null) param = values[0];
        }

        String bar = doSomething(request, param);

        response.setHeader("X-XSS-Protection", "0");
        Object[] obj = {"a", bar};
        // NOTE: see comment at start of file
        // ruleid: deepok: no-direct-response-writer
        response.getWriter().printf(java.util.Locale.US, "Formatted like: %1$s and %2$s.", obj);
    } // end doPost

    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String bar = "safe!";
        java.util.HashMap<String, Object> map26903 = new java.util.HashMap<String, Object>();
        map26903.put("keyA-26903", "a_Value"); // put some stuff in the collection
        map26903.put("keyB-26903", param); // put it in a collection
        map26903.put("keyC", "another_Value"); // put some stuff in the collection
        bar = (String) map26903.get("keyB-26903"); // get it back out
        bar = (String) map26903.get("keyA-26903"); // get safe value back out

        return bar;
    }
}

@WebServlet(value = "/xss-00/BenchmarkTest00013")
public class BenchmarkTest00013 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // some code
        response.setContentType("text/html;charset=UTF-8");

        String param = "";
        java.util.Enumeration<String> headers = request.getHeaders("Referer");

        if (headers != null && headers.hasMoreElements()) {
            param = headers.nextElement(); // just grab first element
        }

        // URL Decode the header value since req.getHeaders() doesn't. Unlike req.getParameters().
        param = java.net.URLDecoder.decode(param, "UTF-8");

        response.setHeader("X-XSS-Protection", "0");
        Object[] obj = {"a", "b"};
        // ruleid: no-direct-response-writer
        response.getWriter().format(java.util.Locale.US, param, obj);
    }
}

/**
 * OWASP Benchmark Project v1.2
 *
 * <p>This file is part of the Open Web Application Security Project (OWASP) Benchmark Project. For
 * details, please see <a
 * href="https://owasp.org/www-project-benchmark/">https://owasp.org/www-project-benchmark/</a>.
 *
 * <p>The OWASP Benchmark is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, version 2.
 *
 * <p>The OWASP Benchmark is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details.
 *
 * @author Nick Sanidas
 * @created 2015
 */
package org.owasp.benchmark.testcode;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value = "/xss-04/BenchmarkTest02221")
public class BenchmarkTest02221 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        java.util.Map<String, String[]> map = request.getParameterMap();
        String param = "";
        if (!map.isEmpty()) {
            String[] values = map.get("BenchmarkTest02221");
            if (values != null) param = values[0];
        }

        String bar = doSomething(request, param);

        response.setHeader("X-XSS-Protection", "0");
        Object[] obj = {"a", bar};
        java.io.PrintWriter out = response.getWriter();
        out.write("<!DOCTYPE html>\n<html>\n<body>\n<p>");
        // ruleid: no-direct-response-writer
        out.format(java.util.Locale.US, "Formatted like: %1$s and %2$s.", obj);
        out.write("\n</p>\n</body>\n</html>");
    } // end doPost

    private static String doSomething(HttpServletRequest request, String param)
            throws ServletException, IOException {

        String bar = param;
        if (param != null && param.length() > 1) {
            StringBuilder sbxyz71523 = new StringBuilder(param);
            bar = sbxyz71523.replace(param.length() - "Z".length(), param.length(), "Z").toString();
        }

        return bar;
    }
}

