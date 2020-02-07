package general.database;

import general.Config;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
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
            settings.put(Environment.URL, (String)Config.getInstance().get("databaseURL"));
            settings.put(Environment.USER, (String)Config.getInstance().get("databaseUser"));
            settings.put(Environment.PASS, (String)Config.getInstance().get("databasePass"));
            settings.put(Environment.DIALECT, (String)Config.getInstance().get("databaseDialect"));

            settings.putAll(Config.getInstance().getHibernateConfig());

            registryBuilder.applySettings(settings);
            this.registry = registryBuilder.build();

            MetadataSources sources = new MetadataSources(this.registry);

            sources.addAnnotatedClass(Credentials.class);
            sources.addAnnotatedClass(User.class);
            sources.addAnnotatedClass(Student.class);
            sources.addAnnotatedClass(Teacher.class);
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

    //TODO: Delete
    public void deleteResults() {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            session.createQuery("DELETE FROM Result").executeUpdate();
            session.createQuery("DELETE FROM Result").executeUpdate();
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void createUser(User user) {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            session.save(user);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public Student getStudent(int id) {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Student> query = cb.createQuery(Student.class);
            Root<Student> root = query.from(Student.class);

            Predicate predicate = cb.equal(root.get("id"),id);
            query.select(root).where(predicate);

            List<Student> studentList = session.createQuery(query).list();
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

    public List<Student> getStudents() {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Student> query = cb.createQuery(Student.class);
            Root<Student> root = query.from(Student.class);
            CriteriaQuery<Student> all = query.select(root);

            TypedQuery<Student> typedQuery = session.createQuery(all);
            tx.commit();

            return typedQuery.getResultList();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return null;
    }

    public void createGroup(Group group) {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            session.save(group);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public List<Group> getGroups() {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Group> query = cb.createQuery(Group.class);
            Root<Group> root = query.from(Group.class);
            CriteriaQuery<Group> all = query.select(root);

            TypedQuery<Group> typedQuery = session.createQuery(all);
            tx.commit();

            return typedQuery.getResultList();
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

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Credentials> query = cb.createQuery(Credentials.class);
            Root<Credentials> root = query.from(Credentials.class);

            Predicate predicate = cb.equal(root.get("login"),username);
            query.select(root).where(predicate);

            List<Credentials> credentialsList = session.createQuery(query).list();
            if (credentialsList.size() >= 1) return credentialsList.get(0);

            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    public void changeCredentials(Credentials credentials) {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            session.update(credentials);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void createExam(Exam exam) {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            session.save(exam);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public List<Exam> getExams(Student student, boolean includeInactive, boolean includeDone) {
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

    public List<Exam> getExams() {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Exam> query = cb.createQuery(Exam.class);
            Root<Exam> root = query.from(Exam.class);
            CriteriaQuery<Exam> all = query.select(root);

            TypedQuery<Exam> typedQuery = session.createQuery(all);
            tx.commit();

            return typedQuery.getResultList();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return null;
    }

    public Exam getExam(int id) {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Exam> query = cb.createQuery(Exam.class);
            Root<Exam> root = query.from(Exam.class);

            Predicate predicate = cb.equal(root.get("id"),id);
            query.select(root).where(predicate);

            List<Exam> examList = session.createQuery(query).list();
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

    public List<Result> getResults() {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Result> query = cb.createQuery(Result.class);
            Root<Result> root = query.from(Result.class);
            CriteriaQuery<Result> all = query.select(root);

            TypedQuery<Result> typedQuery = session.createQuery(all);
            tx.commit();

            return typedQuery.getResultList();
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
