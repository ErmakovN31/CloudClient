package ru.xaero.javacore;

import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.junit.Test;

import javax.jcr.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Для проведения теста необходим запущенный сервер jackrabbit 2.14.6 по адресу http://localhost:8080
 */
public class IntegrationTest {

    @Test
    public void test() throws MalformedURLException, RepositoryException {
        final Repository repository = new URLRemoteRepository("http://localhost:8080/rmi");
        final Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        final Node root = session.getRootNode();
        root.addNode("test2", "nt:folder");
        root.addNode("test1", "nt:folder");
        session.move("/test1", "/test2/test1");
        session.save();
        session.logout();
    }
}
