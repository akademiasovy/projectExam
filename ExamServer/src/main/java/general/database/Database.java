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
            settings.put(Environment.URL, "jdbc:mysql://localhost:3306/exams");
            settings.put(Environment.USER, "root");
            settings.put(Environment.PASS, "");
            settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect");

            registryBuilder.applySettings(settings);
            this.registry = registryBuilder.build();

            MetadataSources sources = new MetadataSources(this.registry);

            sources.addAnnotatedClass(Credentials.class);
            sources.addAnnotatedClass(Student.class);
            sources.addAnnotatedClass(StudentToGroup.class);
            sources.addAnnotatedClass(Groups.class);
            sources.addAnnotatedClass(GroupToExam.class);
            sources.addAnnotatedClass(Exam.class);

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

    public List<Groups> getGroupsByStudent(Student student) {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            List<Integer> groupIDs = new ArrayList<Integer>();

            tx = session.beginTransaction();
            List<StudentToGroup> stgList = session.createQuery("FROM StudentToGroup").list();
            for (StudentToGroup stg : stgList) {
                if (stg.getIdstudent() == student.getId()) groupIDs.add(stg.getIdgroup());
            }
            tx.commit();

            List<Groups> studentGroups = new ArrayList<Groups>();
            if (groupIDs.size() == 0) return studentGroups;

            tx = session.beginTransaction();
            List<Groups> groupList = session.createQuery("FROM Groups").list();
            for (Groups group : groupList) {
                if (groupIDs.contains(group.getId())) studentGroups.add(group);
            }
            tx.commit();

            return studentGroups;
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    public List<Exam> getExamsByGroups(List<Groups> groups, boolean activeOnly) {
        if (groups.size() == 0) return new ArrayList<Exam>();

        Session session = factory.openSession();
        Transaction tx = null;

        try {
            StringBuilder queryBuilder = new StringBuilder("FROM GroupToExam WHERE idgroup="+groups.get(0).getId());
            for (int i = 1; i < groups.size(); i++) {
                queryBuilder.append(" or id="+groups.get(i).getId());
            }

            tx = session.beginTransaction();
            List<GroupToExam> gteList = session.createQuery(queryBuilder.toString()).list();
            tx.commit();

            if (gteList.size() == 0) return new ArrayList<Exam>();

            queryBuilder = new StringBuilder("FROM Exam WHERE id="+gteList.get(0).getIdexam());
            for (int i = 1; i < gteList.size(); i++) {
                queryBuilder.append(" or id="+gteList.get(i).getIdexam());
            }

            tx = session.beginTransaction();
            List<Exam> examList = session.createQuery(queryBuilder.toString()).list();
            tx.commit();

            if (!activeOnly) return examList;

            List<Exam> filteredExamList = new ArrayList<Exam>();
            Date date = new Date();
            for (Exam exam : examList) {
                if (exam.getStart().getTime() <= date.getTime() && exam.getEnd().getTime() >= date.getTime()) filteredExamList.add(exam);
            }

            return filteredExamList;
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }
}
