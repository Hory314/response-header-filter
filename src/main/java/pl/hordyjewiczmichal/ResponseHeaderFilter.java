package pl.hordyjewiczmichal;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("WeakerAccess")
public class ResponseHeaderFilter implements Filter
{
    private Map<String, List<String>> headers;


    @Override
    public void init(FilterConfig config)
    {
        this.headers = new HashMap<>();

        Enumeration<String> paramNames = config.getInitParameterNames();

        if (paramNames == null) return;

        while (paramNames.hasMoreElements())
        {
            String headerName = paramNames.nextElement();
            String headerValues = config.getInitParameter(headerName); // can contain multi-values (separated by new line)

            List<String> values = new ArrayList<>();

            try
            {
                if ("".equals(headerValues)) throw new IllegalArgumentException();

                StringTokenizer st = new StringTokenizer(headerValues, "\n");
                while (st.hasMoreTokens())
                {
                    values.add(st.nextToken().trim());
                }
            }
            catch (NullPointerException | IllegalArgumentException e)
            {
                continue; // skip if no value specified
            }

            this.headers.put(headerName, values);
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException
    {
        HttpServletResponse response = (HttpServletResponse) resp;
        Set<String> headerNames = this.getHeaders().keySet();

        headerNames.forEach(headerName ->
                this.getHeaders().get(headerName).forEach(val ->
                        response.addHeader(headerName, val)));

        chain.doFilter(req, resp);
    }

    @Override
    public void destroy()
    {
        // nothing to clean up.
    }

    public Map<String, List<String>> getHeaders()
    {
        return headers;
    }
}
