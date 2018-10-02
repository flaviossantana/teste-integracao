package br.com.caelum.pm73.dao;

import br.com.caelum.pm73.dominio.Leilao;
import br.com.caelum.pm73.dominio.Usuario;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.List;

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

    @Test
    public void contarLeiloesEncerrados(){

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

    @Test
    public void deveRetornarLeiloesDeProdutosNovos(){

        Usuario usuario = new Usuario("Flavio Santana", "meu@email.com");

        Leilao geladeira = new Leilao("Geladeira", 1500.00, usuario, true);
        Leilao xbox = new Leilao("Xbox", 800.00, usuario, false);
        xbox.encerra();

        usuarioDao.salvar(usuario);
        leilaoDao.salvar(geladeira);
        leilaoDao.salvar(xbox);

        List<Leilao> novos = leilaoDao.novos();

        Assert.assertEquals(novos.size(), 1);

    }

    @Test
    public void leiloesAntigos(){

        Usuario usuario = new Usuario("Flavio Santana", "meu@email.com");

        Leilao geladeira = new Leilao("Geladeira", 1500.00, usuario, true);
        Calendar dataAntiga = Calendar.getInstance();
        dataAntiga.add(Calendar.DAY_OF_MONTH, -10);
        geladeira.setDataAbertura(dataAntiga);

        Leilao xbox = new Leilao("Xbox", 800.00, usuario, false);
        Calendar dataAtual = Calendar.getInstance();
        xbox.setDataAbertura(dataAtual);

        usuarioDao.salvar(usuario);
        leilaoDao.salvar(geladeira);
        leilaoDao.salvar(xbox);

        List<Leilao> antigos = leilaoDao.antigos();

        Assert.assertEquals(antigos.size(), 1);

    }

    @Test
    public void leiloesAntigoNoLimite(){

        Usuario usuario = new Usuario("Flavio Santana", "meu@email.com");

        Leilao geladeira = new Leilao("Geladeira", 1500.00, usuario, true);
        Calendar dataAntiga = Calendar.getInstance();
        dataAntiga.add(Calendar.DAY_OF_MONTH, -7);
        geladeira.setDataAbertura(dataAntiga);

        Leilao xbox = new Leilao("Xbox", 800.00, usuario, false);
        Calendar dataAtual = Calendar.getInstance();
        xbox.setDataAbertura(dataAtual);

        usuarioDao.salvar(usuario);
        leilaoDao.salvar(geladeira);
        leilaoDao.salvar(xbox);

        List<Leilao> antigos = leilaoDao.antigos();

        Assert.assertEquals(antigos.size(), 1);

    }

    @Test
    public void buscarLeiloesNaoEncerradosNoPeriodo(){

        Calendar inicio = Calendar.getInstance();
        inicio.add(Calendar.DAY_OF_MONTH, -10);

        Calendar fim = Calendar.getInstance();

        Usuario flavioSantana = new Usuario("Flavio Santana", "meu@email.com");

        Leilao leilaoXbox = new Leilao("XBOX", 1.700, flavioSantana, false);
        Calendar dataAnertura = Calendar.getInstance();
        dataAnertura.add(Calendar.DAY_OF_MONTH, -2);
        leilaoXbox.setDataAbertura(dataAnertura);

        Leilao leilaoPs4 = new Leilao("PS4", 1.700, flavioSantana, false);
        Calendar dataAbertura = Calendar.getInstance();
        dataAnertura.add(Calendar.DAY_OF_MONTH, -20);
        leilaoPs4.setDataAbertura(dataAbertura);

        usuarioDao.salvar(flavioSantana);
        leilaoDao.salvar(leilaoPs4);
        leilaoDao.salvar(leilaoXbox);

        List<Leilao> leilaos = leilaoDao.porPeriodo(inicio, fim);

        Assert.assertEquals(1, leilaos.size());
        Assert.assertEquals("PS4", leilaos.get(0).getNome());


    }

    @Test
    public void naoDeveTrazerLeilaoEncerradoNoperiodo(){

        Calendar inicio = Calendar.getInstance();
        inicio.add(Calendar.DAY_OF_MONTH, -10);

        Calendar fim = Calendar.getInstance();

        Usuario flavioSantana = new Usuario("Flavio Santana", "meu@email.com");

        Leilao leilaoXbox = new Leilao("XBOX", 1.700, flavioSantana, false);
        Calendar dataAnertura = Calendar.getInstance();
        dataAnertura.add(Calendar.DAY_OF_MONTH, -2);
        leilaoXbox.setDataAbertura(dataAnertura);

        usuarioDao.salvar(flavioSantana);
        leilaoDao.salvar(leilaoXbox);

        List<Leilao> leilaos = leilaoDao.porPeriodo(inicio, fim);

        Assert.assertEquals(1, leilaos.size());

    }

}
