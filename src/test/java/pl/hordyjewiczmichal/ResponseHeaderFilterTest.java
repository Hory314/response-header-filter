package pl.hordyjewiczmichal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class ResponseHeaderFilterTest
{
    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    private FilterChain chain;

    @Mock
    private FilterConfig config;

    private ResponseHeaderFilter filter;


    @Before
    public void setUp()
    {
        filter = new ResponseHeaderFilter();
    }

    @Test
    public void initTest()
    {
        // given
        Map<String, List<String>> expected = new HashMap<>();
        List<String> values = new ArrayList<>();
        values.add("value1");
        values.add("value2");
        expected.put("x-header-1", values);
        values = new ArrayList<>();
        values.add("value3");
        expected.put("x-header-2", values);

        // when
        Mockito.when(config.getInitParameter("x-header-1")).thenReturn("value1\n                value2");
        Mockito.when(config.getInitParameter("x-header-2")).thenReturn("value3");
        Mockito.when(config.getInitParameter("x-header-3")).thenReturn("");

        Vector<String> v = new Vector<>();
        v.add("x-header-1");
        v.add("x-header-2");
        v.add("x-header-3");
        Mockito.when(config.getInitParameterNames()).thenReturn(v.elements());

        filter.init(config);

        // then
        assertEquals(expected, filter.getHeaders());
    }

    @Test
    public void doFilterTest() throws ServletException, IOException
    {
        // given
        initTest();

        // when
        filter.doFilter(req, resp, chain);

        // then
        Mockito.verify(resp, times(3)).addHeader(Mockito.any(), Mockito.any());
        Mockito.verify(resp, times(1)).addHeader("x-header-1", "value1");
        Mockito.verify(resp, times(1)).addHeader("x-header-1", "value2");
        Mockito.verify(resp, times(1)).addHeader("x-header-2", "value3");
    }

    @Test
    public void destroyTest()
    {
        filter.destroy();
        assertTrue(true);
    }
}
