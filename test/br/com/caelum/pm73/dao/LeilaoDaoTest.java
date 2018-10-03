package br.com.caelum.pm73.dao;

import br.com.caelum.pm73.builder.LeilaoBuilder;
import br.com.caelum.pm73.dominio.Lance;
import br.com.caelum.pm73.dominio.Leilao;
import br.com.caelum.pm73.dominio.Usuario;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LeilaoDaoTest {

    private Session session;
    private LeilaoDao leilaoDao;
    private UsuarioDao usuarioDao;
    private LeilaoBuilder builder;

    @Before
    public void antes() {
        session = new CriadorDeSessao().getSession();
        leilaoDao = new LeilaoDao(session);
        usuarioDao = new UsuarioDao(session);
        session.beginTransaction();
        builder = new LeilaoBuilder();
    }

    @After
    public void depois() {
        session.getTransaction().rollback();
        session.close();
    }

    @Test
    public void contarLeiloesNaoEncerrados() {

        Usuario usuario = new Usuario("Flavio Santana", "meu@email.com");

        Leilao geladeira = builder.usado().comValor(1.500).comNome("Geladeira").comDono(usuario).constroi();
        Leilao xbox = builder.comValor(800.00).comNome("Xbox").comDono(usuario).encerrado().constroi();

        usuarioDao.salvar(usuario);
        leilaoDao.salvar(geladeira);
        leilaoDao.salvar(xbox);

        long total = leilaoDao.total();

        assertEquals(total, 1L);

    }

    @Test
    public void contarLeiloesEncerrados() {

        Usuario usuario = new Usuario("Flavio Santana", "meu@email.com");

        Leilao geladeira = builder.comNome("Geladeira").comValor(1.500).comDono(usuario).usado().constroi();
        Leilao xbox = builder.comNome("Xbox").comValor(800.00).comDono(usuario).encerrado().constroi();

        usuarioDao.salvar(usuario);
        leilaoDao.salvar(geladeira);
        leilaoDao.salvar(xbox);

        long total = leilaoDao.total();

        assertEquals(total, 1L);

    }

    @Test
    public void deveContarLeiloesNaoEncerrados() {
        // criamos um usuario
        Usuario mauricio = new Usuario("Mauricio Aniche",
                "mauricio@aniche.com.br");

        // criamos os dois leiloes

        Leilao ativo = new LeilaoBuilder()
                .comDono(mauricio)
                .constroi();

        Leilao encerrado = new LeilaoBuilder()
                .comDono(mauricio)
                .encerrado()
                .constroi();

        // persistimos todos no banco
        usuarioDao.salvar(mauricio);
        leilaoDao.salvar(ativo);
        leilaoDao.salvar(encerrado);

        // pedimos o total para o DAO
        long total = leilaoDao.total();

        assertEquals(1L, total);
    }

    @Test
    public void deveRetornarZeroSeNaoHaLeiloesNovos() {
        Usuario mauricio = new Usuario("Mauricio Aniche",
                "mauricio@aniche.com.br");

        Leilao encerrado = new LeilaoBuilder()
                .comDono(mauricio)
                .encerrado()
                .constroi();

        Leilao tambemEncerrado = new LeilaoBuilder()
                .comDono(mauricio)
                .encerrado().constroi();

        usuarioDao.salvar(mauricio);
        leilaoDao.salvar(encerrado);
        leilaoDao.salvar(tambemEncerrado);

        long total = leilaoDao.total();

        assertEquals(0L, total);
    }

    @Test
    public void deveRetornarLeiloesDeProdutosNovos() {

        Usuario mauricio = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

        Leilao produtoNovo = builder.comDono(mauricio).comNome("XBox").constroi();
        Leilao produtoUsado = builder.comNome("XBox").comDono(mauricio).usado().constroi();

        usuarioDao.salvar(mauricio);
        leilaoDao.salvar(produtoNovo);
        leilaoDao.salvar(produtoUsado);

        List<Leilao> novos = leilaoDao.novos();

        assertEquals(1, novos.size());
        assertEquals("XBox", novos.get(0).getNome());
    }

    @Test
    public void deveTrazerSomenteLeiloesAntigos() {

        Usuario mauricio = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

        Leilao recente = builder.comNome("XBox").comDono(mauricio).constroi();
        Leilao antigo = builder.comDono(mauricio).comNome("Geladeira").diasAtras(10).constroi();

        usuarioDao.salvar(mauricio);
        leilaoDao.salvar(recente);
        leilaoDao.salvar(antigo);

        List<Leilao> antigos = leilaoDao.antigos();

        assertEquals(1, antigos.size());
        assertEquals("Geladeira", antigos.get(0).getNome());
    }

    @Test
    public void deveTrazerSomenteLeiloesAntigosHaMaisDe7Dias() {

        Usuario mauricio = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

        Leilao noLimite = builder.diasAtras(7).comDono(mauricio).constroi();

        Calendar dataAntiga = Calendar.getInstance();
        dataAntiga.add(Calendar.DAY_OF_MONTH, -7);

        noLimite.setDataAbertura(dataAntiga);

        usuarioDao.salvar(mauricio);
        leilaoDao.salvar(noLimite);

        List<Leilao> antigos = leilaoDao.antigos();

        assertEquals(1, antigos.size());
    }

    @Test
    public void deveTrazerLeiloesNaoEncerradosNoPeriodo() {

        // criando as datas
        Calendar comecoDoIntervalo = Calendar.getInstance();
        comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
        Calendar fimDoIntervalo = Calendar.getInstance();

        Usuario mauricio = new Usuario("Mauricio Aniche",
                "mauricio@aniche.com.br");

        // criando os leiloes, cada um com uma data
        Leilao leilao1 = new LeilaoBuilder()
                .diasAtras(2)
                .comDono(mauricio)
                .comNome("XBox")
                .constroi();

        Leilao leilao2 = new LeilaoBuilder()
                .diasAtras(20)
                .comDono(mauricio)
                .comNome("XBox")
                .constroi();

        // persistindo os objetos no banco
        usuarioDao.salvar(mauricio);
        leilaoDao.salvar(leilao1);
        leilaoDao.salvar(leilao2);

        // invocando o metodo para testar
        List<Leilao> leiloes =
                leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);

        // garantindo que a query funcionou
        assertEquals(1, leiloes.size());
        assertEquals("XBox", leiloes.get(0).getNome());
    }

    @Test
    public void naoDeveTrazerLeilaoEncerradoNoperiodo() {

        // criando as datas
        Calendar comecoDoIntervalo = Calendar.getInstance();
        comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
        Calendar fimDoIntervalo = Calendar.getInstance();
        Calendar dataDoLeilao1 = Calendar.getInstance();
        dataDoLeilao1.add(Calendar.DAY_OF_MONTH, -2);

        Usuario mauricio = new Usuario("Mauricio Aniche",
                "mauricio@aniche.com.br");

        // criando os leiloes, cada um com uma data
        Leilao leilao1 = new LeilaoBuilder()
                .comDono(mauricio)
                .diasAtras(2)
                .comNome("XBox")
                .encerrado()
                .constroi();

        // persistindo os objetos no banco
        usuarioDao.salvar(mauricio);
        leilaoDao.salvar(leilao1);

        // invocando o metodo para testar
        List<Leilao> leiloes =
                leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);

        // garantindo que a query funcionou
        assertEquals(0, leiloes.size());

    }

    @Test
    public void disputadosEntre(){

        Usuario mauricio = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");
        Usuario interessadoUm = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

        Leilao leilao = builder.comNome("XBOX").comValor(1000).comDono(mauricio)
                .comLance(Calendar.getInstance(), interessadoUm, 750.00)
                .comLance(Calendar.getInstance(), interessadoUm, 950.00)
                .comLance(Calendar.getInstance(), interessadoUm, 999.00)
                .comLance(Calendar.getInstance(), interessadoUm, 1001.00)
                .constroi();

        usuarioDao.salvar(mauricio);
        usuarioDao.salvar(interessadoUm);
        leilaoDao.salvar(leilao);

        List<Leilao> leiloes = leilaoDao.disputadosEntre(800.00, 1001.00);

        assertEquals(1, leiloes.size());
        assertEquals(1000.0, leiloes.get(0).getValorInicial(), 0.00001);

    }

    @Test
    public void listaLeiloesDoUsuario(){

        Usuario mauricio = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");
        Usuario interessadoUm = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

        Leilao leilao = builder.comNome("XBOX").comValor(1000).comDono(mauricio)
                .comLance(Calendar.getInstance(), interessadoUm, 750.00)
                .comLance(Calendar.getInstance(), interessadoUm, 950.00)
                .comLance(Calendar.getInstance(), interessadoUm, 999.00)
                .comLance(Calendar.getInstance(), interessadoUm, 1001.00)
                .constroi();

        usuarioDao.salvar(mauricio);
        usuarioDao.salvar(interessadoUm);
        leilaoDao.salvar(leilao);

        List<Leilao> leiloes = leilaoDao.listaLeiloesDoUsuario(interessadoUm);

        assertEquals(1, leiloes.size());

    }

    @Test
    public void getValorInicialMedioDoUsuario(){

        Usuario mauricio = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");
        Usuario interessadoUm = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

        Leilao leilaoXbox = new LeilaoBuilder().comNome("XBOX").comValor(1000.00).comDono(mauricio)
                .comLance(Calendar.getInstance(), interessadoUm, 750.00)
                .comLance(Calendar.getInstance(), interessadoUm, 950.00)
                .comLance(Calendar.getInstance(), interessadoUm, 999.00)
                .comLance(Calendar.getInstance(), interessadoUm, 1001.00)
                .constroi();

        Leilao leilaoPs4 = new LeilaoBuilder().comNome("PS4").comValor(100.00).comDono(mauricio)
                .comLance(Calendar.getInstance(), interessadoUm, 750.00)
                .comLance(Calendar.getInstance(), interessadoUm, 950.00)
                .comLance(Calendar.getInstance(), interessadoUm, 999.00)
                .comLance(Calendar.getInstance(), interessadoUm, 1001.00)
                .constroi();

        usuarioDao.salvar(mauricio);
        usuarioDao.salvar(interessadoUm);
        leilaoDao.salvar(leilaoXbox);
        leilaoDao.salvar(leilaoPs4);

        Double valorInicialMedioDoUsuario = leilaoDao.getValorInicialMedioDoUsuario(interessadoUm);

        assertEquals(550.00, valorInicialMedioDoUsuario,0.00001);

    }


    @Test
    public void deveDeletarUmUsuario() {
        Usuario usuario =
                new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

        usuarioDao.salvar(usuario);
        usuarioDao.deletar(usuario);

        session.flush();
        session.clear();

        Usuario usuarioNoBanco =
                usuarioDao.porNomeEEmail("Mauricio Aniche", "mauricio@aniche.com.br");

        assertNull(usuarioNoBanco);

    }

    @Test
    public void porId() {
        Usuario usuario =
                new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

        usuarioDao.salvar(usuario);
        usuarioDao.deletar(usuario);

        session.flush();
        session.clear();

        assertNull(usuarioDao.porId(usuario.getId()));

    }

    @Test
    public void deveDeletarUmLeilao() {
        Usuario mauricio = new Usuario("Mauricio", "m@a.com");
        Leilao leilao = new LeilaoBuilder()
                .comDono(mauricio)
                .comLance(Calendar.getInstance(), mauricio, 10000.0)
                .constroi();

        usuarioDao.salvar(mauricio);
        leilaoDao.salvar(leilao);

        session.flush();

        leilaoDao.deleta(leilao);

        assertNull(leilaoDao.porId(leilao.getId()));

    }

}
