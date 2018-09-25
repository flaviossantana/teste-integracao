package br.com.caelum.pm73.dao;

import br.com.caelum.pm73.dominio.Usuario;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UsuarioDaoTest {

    public static final String NOME_JOÃO_DA_SILVA = "João da Silva";
    public static final String NOME_FLAVIO_SANTANA = "Flavio Santana";
    public static final String EMAIL_JOAO = "a@b.c";
    public static final String EMAIL_FLAVIO_SANTANA = "f@s.s";

    private Session session;
    private UsuarioDao usuarioDao;


    @Before
    public void antes(){
        session = new CriadorDeSessao().getSession();
        usuarioDao = new UsuarioDao(session);
        session.beginTransaction();
    }

    @After
    public void depois(){
        session.getTransaction().rollback();
        session.close();
    }

    @Test
    public void buscarPorNomeEEmail(){

        Usuario novoUsuario = new Usuario(NOME_JOÃO_DA_SILVA, EMAIL_JOAO);
        usuarioDao.salvar(novoUsuario);

        Usuario usuario = usuarioDao.porNomeEEmail(NOME_JOÃO_DA_SILVA, EMAIL_JOAO);

        Assert.assertEquals(usuario.getNome(), novoUsuario.getNome());
        Assert.assertEquals(usuario.getEmail(), novoUsuario.getEmail());

    }

    @Test
    public void usuarioNaoEncontrado(){
        Usuario usuario = usuarioDao.porNomeEEmail(NOME_FLAVIO_SANTANA, EMAIL_FLAVIO_SANTANA);
        Assert.assertNull(usuario);
    }


}
