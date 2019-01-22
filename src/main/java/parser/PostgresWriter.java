package parser;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class PostgresWriter {
    private SessionFactory sessionFactory;
    private AtomicInteger counter = new AtomicInteger();

    private void openSessionFactory() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    private void closeSessionFactory() {
        sessionFactory.close();
    }

    public void writeAccidents(String path, Function<String, List<Accident>> parser) {
        try {
            openSessionFactory();
            System.out.println("Parsing data from: " + path);
            List<Accident> accidents = parser.apply(path);
            System.out.println("Writing data to database");
            Session session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            accidents.stream()
                    .forEach(accident -> {
                        session.save(accident);
                        if(counter.addAndGet(1) % 1000 == 0) {
                            session.flush();
                            session.clear();
                        }
                    });
            session.getTransaction().commit();
        } finally {
            closeSessionFactory();
        }
    }
}
