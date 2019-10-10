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

import java.util.*;

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
            //settings.put(Environment.URL, "jdbc:mysql://localhost:3306/exams");
            settings.put(Environment.URL, "jdbc:mysql://192.168.0.110:3306/exams");
            settings.put(Environment.USER, "root");
            settings.put(Environment.PASS, "");
            //settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect");
            settings.put(Environment.DIALECT, "org.hibernate.dialect.MariaDB53Dialect");
            //DELETE BELOW:
            settings.put(Environment.USE_NEW_ID_GENERATOR_MAPPINGS, "false");

            registryBuilder.applySettings(settings);
            this.registry = registryBuilder.build();

            MetadataSources sources = new MetadataSources(this.registry);

            sources.addAnnotatedClass(Credentials.class);
            sources.addAnnotatedClass(Student.class);
            sources.addAnnotatedClass(Group.class);
            sources.addAnnotatedClass(Exam.class);
            sources.addAnnotatedClass(Question.class);
            sources.addAnnotatedClass(Answer.class);
            sources.addAnnotatedClass(Result.class);

            Metadata metadata = sources.getMetadataBuilder().build();
            this.factory = metadata.getSessionFactoryBuilder().build();
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    public Student getStudentByID(int id) {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            List<Student> studentList = session.createQuery("FROM Student WHERE id="+id).list();

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

    public List<Exam> getExamsByStudent(Student student, boolean includeInactive, boolean includeDone) {
        Set<Exam> examSet = new HashSet<Exam>();
        Date date = new Date();

        for (Group group : student.getGroupSet()) {
            for (Exam exam : new HashSet<Exam>(group.getExamSet())) {
                if (includeInactive || (exam.getStart().getTime() <= date.getTime() && exam.getEnd().getTime() >= date.getTime())) {
                    if (includeDone) {
                        examSet.add(exam);
                    } else {
                        boolean done = false;
                        for (Result result : student.getResultSet()) {
                            if (exam.equals(result.getExam())) {
                                done = true;
                                break;
                            }
                        }
                        if (!done) examSet.add(exam);
                    }
                }
            }
        }

        return new ArrayList<Exam>(examSet);
    }

    public Exam getExamByID(int id) {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            List<Exam> examList = session.createQuery("FROM Exam WHERE id="+id).list();

            if (examList.size() >= 1) return examList.get(0);

            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    public boolean saveResult(Result result) {
        Session session = factory.openSession();
        Transaction tx = null;

        boolean saved = false;
        try {
            tx = session.beginTransaction();
            session.save(result);
            tx.commit();
            saved = true;
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return saved;
    }
}
