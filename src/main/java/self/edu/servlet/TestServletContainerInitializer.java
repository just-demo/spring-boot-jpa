package self.edu.servlet;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

/**
 * @author Anatolii
 */
public class TestServletContainerInitializer implements ServletContainerInitializer {
//    @PersistenceUnit(unitName = "self-edu-persistence-unit")
//    private EntityManagerFactory entityManagerFactory;

    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        System.out.println("=====================");
        System.out.println(set);
    }
}
