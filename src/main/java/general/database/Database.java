package general.database;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {

    private static Database instance = null;

    public static Database getInstance() {
        if (Database.instance == null) Database.instance = new Database();
        return Database.instance;
    }

    private StandardServiceRegistry registry;
    private SessionFactory factory;

    private Database() {
        try {
            StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();

            Map<String, String> settings = new HashMap<String, String>();
            settings.put(Environment.DRIVER, "com.mysql.jdbc.Driver");
            settings.put(Environment.URL, "jdbc:mysql://localhost:3306/exams");
            settings.put(Environment.USER, "root");
            settings.put(Environment.PASS, "");
            settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect");

            registryBuilder.applySettings(settings);
            this.registry = registryBuilder.build();

            MetadataSources sources = new MetadataSources(this.registry);

            sources.addAnnotatedClass(Credentials.class);
            sources.addAnnotatedClass(Student.class);

            Metadata metadata = sources.getMetadataBuilder().build();
            this.factory = metadata.getSessionFactoryBuilder().build();
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    public Student getStudentByUsername(String username) {
        Credentials credentials = this.getCredentials(username);

        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            List<Student> studentList = session.createQuery("FROM Student WHERE id="+credentials.getIdstudent()).list();

            if (studentList.size() >= 1) return studentList.get(0);

            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    public Credentials getCredentials(String username) {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            List<Credentials> credentialsList = session.createQuery("FROM Credentials").list();
            for (Credentials credentials : credentialsList) {
                if (credentials.getLogin().equals(username)) return credentials;
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

}
