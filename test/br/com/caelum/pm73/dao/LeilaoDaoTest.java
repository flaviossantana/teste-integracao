package br.com.caelum.pm73.dao;

import br.com.caelum.pm73.dominio.Leilao;
import br.com.caelum.pm73.dominio.Usuario;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LeilaoDaoTest {

    private Session session;
    private LeilaoDao leilaoDao;
    private UsuarioDao usuarioDao;

    @Before
    public void antes(){
        session = new CriadorDeSessao().getSession();
        leilaoDao = new LeilaoDao(session);
        usuarioDao = new UsuarioDao(session);
        session.beginTransaction();
    }

    @After
    public void depois(){
        session.getTransaction().rollback();
        session.close();
    }

    @Test
    public void contarLeiloesNaoEncerrados(){

        Usuario usuario = new Usuario("Flavio Santana", "meu@email.com");

        Leilao geladeira = new Leilao("Geladeira", 1500.00, usuario, true);
        Leilao xbox = new Leilao("Xbox", 800.00, usuario, false);
        xbox.encerra();

        usuarioDao.salvar(usuario);
        leilaoDao.salvar(geladeira);
        leilaoDao.salvar(xbox);

        long total = leilaoDao.total();

        Assert.assertEquals(total, 1L);

    }

}
