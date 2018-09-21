package br.com.caelum.pm73.dao;

import br.com.caelum.pm73.dominio.Usuario;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;

public class UsuarioDaoTest {

    public static final String NOME_JOÃO_DA_SILVA = "João da Silva";
    public static final String EMAIL_JOAO = "a@b.c";

    @Test
    public void buscarPorNomeEEmail(){

        Session session = new CriadorDeSessao().getSession();
        UsuarioDao usuarioDao = new UsuarioDao(session);

        Usuario usuario = usuarioDao.porNomeEEmail(NOME_JOÃO_DA_SILVA, EMAIL_JOAO);

        Assert.assertEquals(usuario.getNome(), "");
        Assert.assertEquals(usuario.getEmail(), "");



    }

}
